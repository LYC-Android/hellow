package bean;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by mr.cheng on 2016/9/28.
 */
public class Trasmit implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<Float> datas;
    private Boolean isOnline;
    private String batteryInfo;

    public String getBatteryInfo() {
        return batteryInfo;
    }

    public void setBatteryInfo(String batteryInfo) {
        this.batteryInfo = batteryInfo;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public List<Float> getDatas() {
        return datas;
    }

    public void setDatas(List<Float> datas) {
        this.datas = datas;
    }
}
