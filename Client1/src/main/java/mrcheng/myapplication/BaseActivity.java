package mrcheng.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.List;

import bean.DatabaseInfo;
import bean.DialogWrapper;
import cn.bmob.newim.notification.BmobNotificationManager;
import util.CardService;
import util.MToast;
import util.ReadFileThread;

/**
 * Created by mr.cheng on 2016/9/14.
 */
public class BaseActivity extends AppCompatActivity {
    private Context mContext;
    private ReadFileThread TransmitThread;
    private AlertDialog TrasmitDialog;
    private AlertDialog YesOrNodialog;
    private ReadFileThread readFileThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = BaseActivity.this;
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String Msg) {
        MToast.showToast(BaseActivity.this, Msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final DialogWrapper event) {
        processCustomMessage(event.getIp(), event.getCardService(), event.getObjectId());

    }

    private void processCustomMessage(String ip, final CardService cardService, String objectId) {
        List<DatabaseInfo> mList = DataSupport.where("objectId = ?", objectId).find(DatabaseInfo.class);
        String realName = "";
        if (mList.size() > 0) {
            realName = mList.get(0).getRealName();
        }
        YesOrNodialog = new AlertDialog.Builder(mContext).create();
        readFileThread = new ReadFileThread(mContext, ip);
        readFileThread.start();
        YesOrNodialog.setTitle("医生:" + realName + "请求与您你连接");
        YesOrNodialog.setCancelable(false);
        final String finalRealName = realName;
        YesOrNodialog.setButton(DialogInterface.BUTTON_POSITIVE, "接受", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cardService.findSicker();
                    }
                }).start();


                ShowTransmitDialog(finalRealName);
            }
        });
        YesOrNodialog.setButton(DialogInterface.BUTTON_NEGATIVE, "拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cardService.refuse();
                    }
                }).start();

            }
        });
        YesOrNodialog.show();
    }


    /**
     * 弹出正在与对方进行心电传输的对话框
     */
    private void ShowTransmitDialog(String realName) {
        BmobIMApplication.setCONNECTING(true);
        TransmitThread = new ReadFileThread(mContext, "");
        TransmitThread.start();
        TrasmitDialog = new AlertDialog.Builder(mContext).create();
        TrasmitDialog.setTitle("正在" + realName + "进行心电传输");
        TrasmitDialog.setCancelable(false);
        TrasmitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readFileThread.SendDisimiss();
                    }
                }).start();

                BmobIMApplication.setCONNECTING(false);
            }
        });
        TrasmitDialog.show();
    }
}
