package Bmob_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.v3.BmobUser;

/**
 * Created by mr.cheng on 2016/9/22.
 */
public class Bmob_ChatAdapter extends RecyclerView.Adapter<Bmob_BaseViewHolder> {
    private List<BmobIMMessage> msgs = new ArrayList<>();
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;
    private String currentUid = "";
    BmobIMConversation c;
    /**
     * 显示时间间隔:10分钟
     */
    private final long TIME_INTERVAL = 10 * 60 * 1000;

    public Bmob_ChatAdapter(Context context, BmobIMConversation c) {
        try {
            currentUid = BmobUser.getCurrentUser(context).getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.c = c;
    }

    @Override
    public Bmob_BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SEND_TXT) {
            return new SendTextHolderBmob(parent.getContext(), parent, c, onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_TXT) {
            return new ReceiveTextHolderBmob(parent.getContext(), parent, onRecyclerViewListener);
        } else if (viewType == TYPE_SEND_VOICE) {
            return new SendVoiceHolderBmob(parent.getContext(), parent, c, onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_VOICE) {
            return new ReceiveVoiceHolderBmob(parent.getContext(), parent, onRecyclerViewListener);
        } else if (viewType == TYPE_SEND_IMAGE) {
            return new SendImageHolderBmob(parent.getContext(), parent, c, onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_IMAGE) {
            return new ReceiveImageHolderBmob(parent.getContext(), parent, c, onRecyclerViewListener);
        } else if (viewType == TYPE_SEND_LOCATION) {
            return new SendLocationHolderBmob(parent.getContext(), parent, c, onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_LOCATION) {
            return new ReceiveLocationHolderBmob(parent.getContext(), parent, onRecyclerViewListener);
        } else {
            return new Bmob_null(parent.getContext(),parent,onRecyclerViewListener);
        }

    }

    @Override
    public void onBindViewHolder(Bmob_BaseViewHolder holder, int position) {
        holder.setMesaage(msgs.get(position));
        if (holder instanceof ReceiveTextHolderBmob) {
            ((ReceiveTextHolderBmob) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendTextHolderBmob) {
            ((SendTextHolderBmob) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof ReceiveVoiceHolderBmob) {
            ((ReceiveVoiceHolderBmob) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendVoiceHolderBmob) {
            ((SendVoiceHolderBmob) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendImageHolderBmob) {
            ((SendImageHolderBmob) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof ReceiveImageHolderBmob) {
            ((ReceiveImageHolderBmob) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof ReceiveLocationHolderBmob) {
            ((ReceiveLocationHolderBmob) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendLocationHolderBmob) {
            ((SendLocationHolderBmob) holder).showTime(shouldShowTime(position));
        }
    }


    @Override
    public int getItemCount() {
        return msgs.size();
    }


    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = msgs.get(position);
        if (message.getMsgType().equals(BmobIMMessageType.TEXT.getType())) {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        } else if (message.getMsgType().equals(BmobIMMessageType.IMAGE.getType())) {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
        } else if (message.getMsgType().equals(BmobIMMessageType.VOICE.getType())) {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
        } else if (message.getMsgType().equals(BmobIMMessageType.LOCATION.getType())) {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_LOCATION : TYPE_RECEIVER_LOCATION;
        } else {
            return -1;
        }
    }

    public void addMessage(BmobIMMessage message) {
        msgs.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }

    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = msgs.get(position - 1).getCreateTime();
        long curTime = msgs.get(position).getCreateTime();
        return curTime - lastTime > TIME_INTERVAL;
    }

    public BmobIMMessage getFirstMessage() {
        if (null != msgs && msgs.size() > 0) {
            return msgs.get(0);
        } else {
            return null;
        }
    }

    /**
     * 移除消息
     *
     * @param position
     */
    public void remove(int position) {
        msgs.remove(position);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.msgs == null ? 0 : this.msgs.size();
    }

    public int findPosition(BmobIMMessage message) {
        int index = this.getCount();
        int position = -1;
        while (index-- > 0) {
            if (message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }
        return position;
    }

    /**
     * 获取消息
     *
     * @param position
     * @return
     */
    public BmobIMMessage getItem(int position) {
        return this.msgs == null ? null : (position >= this.msgs.size() ? null : this.msgs.get(position));
    }

    public void addMessages(List<BmobIMMessage> messages) {
        msgs.addAll(0, messages);
        notifyDataSetChanged();
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public interface OnRecyclerViewListener {
        void onItemClick(int position);

        boolean onItemLongClick(int position);
    }

}
