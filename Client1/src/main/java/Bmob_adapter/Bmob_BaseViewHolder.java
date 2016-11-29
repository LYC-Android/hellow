package Bmob_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.bmob.newim.bean.BmobIMMessage;

/**
 * Created by mr.cheng on 2016/9/22.
 */
public abstract class Bmob_BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
    public Bmob_ChatAdapter.OnRecyclerViewListener onRecyclerViewListener;
    public Bmob_BaseViewHolder(Context context, ViewGroup root, int layoutRes, Bmob_ChatAdapter.OnRecyclerViewListener listener) {
        super(LayoutInflater.from(context).inflate(layoutRes, root, false));
        this.onRecyclerViewListener =listener;
        ButterKnife.inject(this, itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }



    public abstract void setMesaage(BmobIMMessage msg);
    @Override
    public void onClick(View v) {
        if(onRecyclerViewListener!=null){
            onRecyclerViewListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(onRecyclerViewListener!=null){
            onRecyclerViewListener.onItemLongClick(getAdapterPosition());
        }
        return true;
    }
}
