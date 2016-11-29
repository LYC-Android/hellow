package bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;

/**
 * Created by mr.cheng on 2016/10/8.
 */

public class Request extends BmobIMExtraMessage {
    private String objectId;
    private String realName;
    public static Request convert(BmobIMMessage msg){
        Request request=new Request();
        try {
            String extra = msg.getExtra();
            if(!TextUtils.isEmpty(extra)){
                JSONObject json =new JSONObject(extra);
                try {
                    String time = json.getString("realName");
                    request.setRealName(time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String uid =json.getString("objectId");
                    request.setObjectId(uid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String getMsgType() {
        return "request";
    }

    @Override
    public boolean isTransient() {
        return true;
    }
}
