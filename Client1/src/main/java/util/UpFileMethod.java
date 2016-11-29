package util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bean.DowmloadTable;
import bean.MyUser;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by mr.cheng on 2016/9/10.
 */
public class UpFileMethod {
    private String FileName;
    private static final String TAG = "UpFileMethod";
    private Context mContext;
    private Handler mHandler;
    private TextView mTextView;

    public UpFileMethod(String fileName, Context context, Handler handler, TextView status) {
        FileName = fileName;
        mContext = context;
        this.mHandler = handler;
        this.mTextView=status;
    }

    public void done() {

        final BmobFile bmobFile = new BmobFile(new File(FileName));
        bmobFile.upload(mContext, new UploadFileListener() {
            @Override
            public void onSuccess() {
                final MyUser myUser = BmobUser.getCurrentUser(mContext, MyUser.class);
                BmobQuery<DowmloadTable> query = new BmobQuery<>();
                query.addWhereEqualTo("author", myUser);
                query.findObjects(mContext, new FindListener<DowmloadTable>() {
                    @Override
                    public void onSuccess(final List<DowmloadTable> list) {
                        DowmloadTable dowmloadTable = new DowmloadTable();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                        Date date = new Date(System.currentTimeMillis());
                        dowmloadTable.add("urls", bmobFile.getFileUrl(mContext));
                        dowmloadTable.add("times", format.format(date));
                        if (list.size() == 0) {
                            dowmloadTable.setAuthor(myUser);
                            dowmloadTable.save(mContext, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    updateUi("上传成功");

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    updateUi("上传失败");

                                }
                            });
                        } else {
                            dowmloadTable.update(mContext, list.get(0).getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    updateUi("上传成功");

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    updateUi("上传失败");

                                }
                            });
                        }


                    }

                    @Override
                    public void onError(int i, String s) {
                        updateUi("上传失败");

                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                updateUi("上传失败");
            }
        });
    }

    private void updateUi(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (msg) {
                    case "上传成功":
                        mTextView.setText("上传成功");
                    break;
                    case "上传失败": mTextView.setText("上传失败");
                        break;
                }
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
