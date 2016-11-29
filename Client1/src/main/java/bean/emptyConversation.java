package bean;

import android.text.TextUtils;

import cn.bmob.newim.bean.BmobIMConversationType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import mrcheng.myapplication.R;

/**
 * Created by mr.cheng on 2016/9/24.
 */
public class emptyConversation extends Conversation {
private BmobIMUserInfo info;
    private MyUser mUser;

    public emptyConversation(MyUser user) {
        mUser = user;
    }

    @Override
    public Object getAvatar() {
        if (cType == BmobIMConversationType.PRIVATE){
            String avatar =  "";
            if (TextUtils.isEmpty(avatar)){//头像为空，使用默认头像
                return R.mipmap.head;
            }else{
                return avatar;
            }
        }else{
            return R.mipmap.head;
        }
    }

    @Override
    public long getLastMessageTime() {
        return 0;
    }

    @Override
    public String getLastMessageContent() {
        return null;
    }

    @Override
    public int getUnReadCount() {
        return 0;
    }

    @Override
    public void readAllMessages() {

    }

    public BmobIMUserInfo getInfo() {
        info=new BmobIMUserInfo(mUser.getObjectId(),mUser.getUsername(),mUser.getAvatar());
        return info;
    }

    @Override
    public String getcName() {
        return mUser.getRealName();
    }

    @Override
    public String getcId() {
        return mUser.getObjectId();
    }
}
