package mrcheng.myapplication;

import org.litepal.LitePalApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import Bmob_util.DemoMessageHandler;
import Bmob_util.UniversalImageLoader;
import cn.bmob.newim.BmobIM;

/**
 * Created by mr.cheng on 2016/9/19.
 */
public class BmobIMApplication extends LitePalApplication {
    private static boolean CONNECTING =false;
    private  static boolean WAITTING=false;
    public static final String YES="yse";
    public static final String NO="no";
    public static final String DISMISS="dimiss";

    public static boolean isCONNECTING() {
        return CONNECTING;
    }

    public static void setCONNECTING(boolean CONNECTING) {
        BmobIMApplication.CONNECTING = CONNECTING;
    }

    public static boolean isWAITTING() {
        return WAITTING;
    }

    public static void setWAITTING(boolean WAITTING) {
        BmobIMApplication.WAITTING = WAITTING;
    }
    private static BmobIMApplication INSTANCE;
    public static BmobIMApplication INSTANCE(){
        return INSTANCE;
    }
    private void setInstance(BmobIMApplication app) {
        setBmobIMApplication(app);
    }
    private static void setBmobIMApplication(BmobIMApplication a) {
        BmobIMApplication.INSTANCE = a;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }
        //uil初始化
        UniversalImageLoader.initImageLoader(this);
    }

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}