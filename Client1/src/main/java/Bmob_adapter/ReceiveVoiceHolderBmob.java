package Bmob_adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import Bmob_util.ImageLoaderFactory;
import butterknife.InjectView;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobDownloadManager;
import cn.bmob.newim.listener.FileDownloadListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import mrcheng.myapplication.R;


/**
 * Created by mr.cheng on 2016/9/22.
 */
public class ReceiveVoiceHolderBmob extends Bmob_BaseViewHolder {
    @InjectView(R.id.tv_time)
    TextView tv_time;
    @InjectView(R.id.iv_avatar)
    ImageView iv_avatar;
    @InjectView(R.id.iv_voice)
    ImageView iv_voice;
    @InjectView(R.id.layout_voice)
    LinearLayout layout_voice;
    @InjectView(R.id.tv_voice_length)
    TextView tv_voice_length;
    @InjectView(R.id.progress_load)
    ProgressBar progress_load;
    private String currentUid = "";

    public ReceiveVoiceHolderBmob(Context context, ViewGroup root, Bmob_ChatAdapter.OnRecyclerViewListener listener) {
        super(context, root, R.layout.item_chat_received_voice, listener);
        try {
            currentUid = BmobUser.getCurrentUser(context).getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMesaage(BmobIMMessage msg) {
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.head);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        //显示特有属性
        final BmobIMAudioMessage message = BmobIMAudioMessage.buildFromDB(false, msg);
        boolean isExists = BmobDownloadManager.isAudioExist(currentUid, message);
        if(!isExists){//若指定格式的录音文件不存在，则需要下载，因为其文件比较小，故放在此下载
            BmobDownloadManager downloadTask = new BmobDownloadManager(itemView.getContext(),msg,new FileDownloadListener() {

                @Override
                public void onStart() {
                    progress_load.setVisibility(View.VISIBLE);
                    tv_voice_length.setVisibility(View.GONE);
                    iv_voice.setVisibility(View.INVISIBLE);//只有下载完成才显示播放的按钮
                }

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        progress_load.setVisibility(View.GONE);
                        tv_voice_length.setVisibility(View.VISIBLE);
                        tv_voice_length.setText(message.getDuration()+"\''");
                        iv_voice.setVisibility(View.VISIBLE);
                    }else{
                        progress_load.setVisibility(View.GONE);
                        tv_voice_length.setVisibility(View.GONE);
                        iv_voice.setVisibility(View.INVISIBLE);
                    }
                }
            });
            downloadTask.execute(message.getContent());
        }else{
            tv_voice_length.setVisibility(View.VISIBLE);
            tv_voice_length.setText(message.getDuration() + "\''");
        }
        iv_voice.setOnClickListener(new Bmob_NewRecordPlayClickListener(itemView.getContext(), message, iv_voice));

        iv_voice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });

    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

}
