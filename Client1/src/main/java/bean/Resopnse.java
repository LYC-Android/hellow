package bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;

/**
 * Created by mr.cheng on 2016/10/8.
 */

public class Resopnse extends BmobIMExtraMessage {
    private Boolean receive;
    private Boolean isBusy;
    private String realName;
    private String objectId;
    private Boolean cancle;
     public  static Resopnse convert(BmobIMMessage msg){
         Resopnse resopnse=new Resopnse();
         try {
             String extra = msg.getExtra();
             if(!TextUtils.isEmpty(extra)){
                 JSONObject json =new JSONObject(extra);
                 try {
                     String time = json.getString("realName");
                     resopnse.setRealName(time);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
                 try {
                     String uid =json.getString("objectId");
                     resopnse.setObjectId(uid);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
                 try {
                     boolean m =json.getBoolean("isBusy");
                     resopnse.setBusy(m);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
                 try {
                     boolean n=json.getBoolean("receive");
                     resopnse.setReceive(n);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
                 try {
                     boolean x=json.getBoolean("cancle");
                     resopnse.setCancle(x);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         return resopnse;
     }
    public Boolean getBusy() {
        return isBusy;
    }

    public Boolean getCancle() {
        return cancle;
    }

    public void setCancle(Boolean cancle) {
        this.cancle = cancle;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
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

    public Boolean getReceive() {
        return receive;
    }

    public void setReceive(Boolean receive) {
        this.receive = receive;
    }

    @Override
    public String getMsgType() {
        return "response";
    }

    @Override
    public boolean isTransient() {
        return true;
    }
}
