package Bmob_adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import Bmob_util.ImageLoaderFactory;
import butterknife.InjectView;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import mrcheng.myapplication.R;

/**
 * Created by mr.cheng on 2016/9/28.
 */
public class ReceiveLocationHolderBmob extends Bmob_BaseViewHolder {
    @InjectView(R.id.tv_time)
    TextView tv_time;
    @InjectView(R.id.iv_avatar)
    ImageView iv_avatar;
    @InjectView(R.id.tv_message)
    TextView tv_message;
    public ReceiveLocationHolderBmob(Context context, ViewGroup root, Bmob_ChatAdapter.OnRecyclerViewListener listener) {
        super(context, root, R.layout.item_chat_received_message,listener);
    }

    @Override
    public void setMesaage(BmobIMMessage message) {
        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.head);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format( message.getCreateTime());
        tv_time.setText(time);
        tv_message.setText("心店传输");
    }
    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
