package Bmob_adapter;

import android.content.Context;
import android.view.ViewGroup;

import cn.bmob.newim.bean.BmobIMMessage;
import mrcheng.myapplication.R;

/**
 * Created by mr.cheng on 2016/10/8.
 */

public class Bmob_null extends Bmob_BaseViewHolder {
    public Bmob_null(Context context, ViewGroup root, Bmob_ChatAdapter.OnRecyclerViewListener listener) {
        super(context, root, R.layout.item_chat_null,listener);
    }

    @Override
    public void setMesaage(BmobIMMessage msg) {

    }
}
