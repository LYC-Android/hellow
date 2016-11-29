package bean;

import java.io.Serializable;

/**
 *用来在线传输
 */

public class SocketTrasmit implements Serializable {
    private float[] datas;
    private boolean isCancle;
    private boolean batteryInfo;

    public SocketTrasmit(boolean batteryInfo, float[] datas, boolean isCancle) {
        this.batteryInfo = batteryInfo;
        this.datas = datas;
        this.isCancle = isCancle;
    }

    public boolean isBatteryInfo() {
        return batteryInfo;
    }

    public void setBatteryInfo(boolean batteryInfo) {
        this.batteryInfo = batteryInfo;
    }

    public float[] getDatas() {
        return datas;
    }

    public void setDatas(float[] datas) {
        this.datas = datas;
    }

    public boolean isCancle() {
        return isCancle;
    }

    public void setCancle(boolean cancle) {
        isCancle = cancle;
    }
}
