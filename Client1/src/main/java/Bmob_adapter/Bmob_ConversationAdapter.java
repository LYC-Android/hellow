package Bmob_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import Bmob_util.ImageLoaderFactory;
import Bmob_util.TimeUtil;
import bean.Conversation;
import bean.PrivateConversation;
import butterknife.ButterKnife;
import butterknife.InjectView;
import mrcheng.myapplication.R;

/**
 * Created by mr.cheng on 2016/9/22.
 */
public class Bmob_ConversationAdapter extends RecyclerView.Adapter<Bmob_ConversationAdapter.ViewHolder> {

    private Context mContext;
    private List<Conversation> mDatas;

    public Bmob_ConversationAdapter(Context context, List<Conversation> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mDatas.get(position)instanceof PrivateConversation) {
            holder.tv_recent_name.setText(((PrivateConversation) mDatas.get(position)).getRealName());
            holder.tv_recent_time.setText(TimeUtil.getChatTime(false, mDatas.get(position).getLastMessageTime()));
            Object obj = mDatas.get(position).getAvatar();
            if (obj instanceof String) {
                String avatar = (String) obj;
                ImageLoaderFactory.getLoader().loadAvator(holder.iv_recent_avatar, avatar, R.mipmap.head);
            }
            holder.tv_recent_msg.setText(mDatas.get(position).getLastMessageContent());
            long unread = mDatas.get(position).getUnReadCount();
            if (unread > 0) {
                holder.tv_recent_unread.setVisibility(View.VISIBLE);
                holder.tv_recent_unread.setText(String.valueOf(unread));
            } else {
                holder.tv_recent_unread.setVisibility(View.GONE);
            }
        }else {
            holder.tv_recent_name.setText(mDatas.get(position).getcName());
            Object obj = mDatas.get(position).getAvatar();
            if (obj instanceof String) {
                String avatar = (String) obj;
                ImageLoaderFactory.getLoader().loadAvator(holder.iv_recent_avatar, avatar, R.mipmap.head);
            }
        }

        /**
         * 模版里面的onclickListener
         */
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.iv_recent_avatar)
        ImageView iv_recent_avatar;
        @InjectView(R.id.tv_recent_name)
        TextView tv_recent_name;
        @InjectView(R.id.tv_recent_msg)
        TextView tv_recent_msg;
        @InjectView(R.id.tv_recent_time)
        TextView tv_recent_time;
        @InjectView(R.id.tv_recent_unread)
        TextView tv_recent_unread;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

}
