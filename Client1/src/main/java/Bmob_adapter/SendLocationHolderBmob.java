package Bmob_adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import Bmob_util.ImageLoaderFactory;
import butterknife.InjectView;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;
import mrcheng.myapplication.R;

/**
 * Created by mr.cheng on 2016/9/28.
 */
public class SendLocationHolderBmob extends Bmob_BaseViewHolder {

    @InjectView(R.id.tv_time)
    TextView tv_time;
    @InjectView(R.id.iv_avatar)
    ImageView iv_avatar;
    @InjectView(R.id.tv_message)
    TextView tv_message;
    @InjectView(R.id.iv_fail_resend)
    ImageView iv_fail_resend;
    @InjectView(R.id.tv_send_status)
    TextView tv_send_status;
    @InjectView(R.id.progress_load)
    ProgressBar progress_load;
    BmobIMConversation c;

    public SendLocationHolderBmob(Context context, ViewGroup root, BmobIMConversation c, Bmob_ChatAdapter.OnRecyclerViewListener listener) {
        super(context, root, R.layout.item_chat_sent_message,listener);
        this.c = c;
    }

    @Override
    public void setMesaage( final BmobIMMessage msg) {
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        ImageLoaderFactory.getLoader().loadAvator(iv_avatar, info != null ? info.getAvatar() : null, R.mipmap.head);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        tv_message.setText("心电传输");
        int status = msg.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus()) {
            iv_fail_resend.setVisibility(View.VISIBLE);
            progress_load.setVisibility(View.GONE);
        } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.VISIBLE);
        } else {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.GONE);
        }

        //重发
        iv_fail_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.resendMessage(msg, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        progress_load.setVisibility(View.VISIBLE);
                        iv_fail_resend.setVisibility(View.GONE);
                        tv_send_status.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {
                            tv_send_status.setVisibility(View.VISIBLE);
                            tv_send_status.setText("已发送");
                            iv_fail_resend.setVisibility(View.GONE);
                            progress_load.setVisibility(View.GONE);
                        } else {
                            iv_fail_resend.setVisibility(View.VISIBLE);
                            progress_load.setVisibility(View.GONE);
                            tv_send_status.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
