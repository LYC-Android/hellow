package Bmob_adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;

import Bmob_util.ImageLoaderFactory;
import butterknife.InjectView;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import mrcheng.myapplication.R;

/**
 * Created by mr.cheng on 2016/9/22.
 */
public class ReceiveImageHolderBmob extends Bmob_BaseViewHolder {

    BmobIMConversation c;
    @InjectView(R.id.tv_time)
    TextView tv_time;
    @InjectView(R.id.iv_avatar)
    ImageView iv_avatar;
    @InjectView(R.id.iv_picture)
    ImageView iv_picture;
    @InjectView(R.id.progress_load)
    ProgressBar progress_load;

    public ReceiveImageHolderBmob(Context context, ViewGroup root, BmobIMConversation c, Bmob_ChatAdapter.OnRecyclerViewListener listener) {
        super(context, root, R.layout.item_chat_received_image,listener);
    }

    @Override
    public void setMesaage(BmobIMMessage msg) {
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.head);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        //可使用buildFromDB方法转化为指定类型的消息
        final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(false, msg);
        //显示图片
        ImageLoaderFactory.getLoader().load(iv_picture,message.getRemoteUrl(),  R.mipmap.ic_launcher,new ImageLoadingListener(){;

            @Override
            public void onLoadingStarted(String s, View view) {
                progress_load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progress_load.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                progress_load.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                progress_load.setVisibility(View.INVISIBLE);
            }
        });


        iv_picture.setOnLongClickListener(new View.OnLongClickListener() {
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
