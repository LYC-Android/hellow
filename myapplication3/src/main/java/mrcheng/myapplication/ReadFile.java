package mrcheng.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mr.cheng on 2016/11/22.
 */
public class ReadFile {
    private final int N0;
    private final float N1;
    private final MyThread myThread;
    private BufferedInputStream bis;
    private ArrayList<Double> Result = new ArrayList<>();
    private Context mContext;
    private static final String TAG = "ReadFile";
    private int[] xinlvdatas;

    public ReadFile(Context context) {
        this.mContext = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int mScreenWidth = wm.getDefaultDisplay().getWidth();
        int mScreenHeight = wm.getDefaultDisplay().getHeight();
        float xdpi = context.getResources().getDisplayMetrics().xdpi;
        Caculate caculate = new Caculate(xdpi, mScreenWidth, mScreenHeight);
        N0 = caculate.getN0();
        N1 = caculate.getN1();
        myThread = new MyThread(mContext);
//        this.read(path, context);
        openFile();
    }

    static {
        System.loadLibrary("myNativeLib");
    }


    public short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    public ArrayList<Double> getResult() {
        return Result;
    }

    private void openFile() {
        try {
            bis = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.fmsignal));
            int length;
            byte[] buf = new byte[64000 * 2];
            double[] doubles = new double[65536];//Modified
            while ((length = bis.read(buf, 0, buf.length)) != -1) {
                short[] shorts = byteArray2ShortArray(buf, buf.length / 2);
                myThread.getStringFromNative(shorts, doubles);
                //这里是写文件的方法
                //                        if (!WriteFlag) {
                //                            MyWriteFileMethod(doubles);
                //                        }
                //心率部分方法
//                ArrayList<Float> XH = new ArrayList<>();
//                for (int q = 0; q < 4000; q++) {
//                    XH.add((float) doubles[q]);
//                }
//                xinlvdatas = CaculateXinLv(XH);
                //**********************************************************************//

                //结束

//                List<Double> mDraws = new ArrayList<>();
                List<Double> TempDoubles = new ArrayList<>();
                List<Double> mDoubls = new ArrayList<>();
                int mycount = 4000;//这个数字是要65536个doubles里面有用的数据长度
                for (int k = 0; k < mycount; k++) {
                    TempDoubles.add(doubles[k]);
                    if (TempDoubles.size() >= mycount / 8) {//Modified 每8秒的数据量为500*8=4000，要画出150*8=1200点
                        for (int h = 0; h < TempDoubles.size(); h++) {
                            mDoubls.add(TempDoubles.get(h));
                            if (h > 0 && h % 5 == 0) {//Modified 每5点插值一次使得8秒数据量达到4800点
                                mDoubls.add((TempDoubles.get(h - 1) + TempDoubles.get(h + 1)) / 2);
                            }
                        }
                        mDoubls.add(TempDoubles.get((mycount / 8) - 1));
                        for (int l = 0; l < mDoubls.size(); l++) {
                            if (l % 4 == 0) {//Modified 然后4倍抽取使得8秒画图点数为1200点
                                Result.add((double) (N0 - (float) (mDoubls.get(l) * N1)));
                            }
                        }

                        mDoubls.clear();
                        TempDoubles.clear();
                    }
                }
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    //计算心率的方法，返回一个长度为8的数组
    private int[] CaculateXinLv(ArrayList<Float> datas) {
        float[] mFloats = new float[datas.size()];
        float[] mDaoshu = new float[datas.size()];
        for (int i = 0; i < datas.size(); i++) {
            mFloats[i] = datas.get(i);
        }

        mDaoshu[0] = mDaoshu[1] = mDaoshu[datas.size() - 1] = mDaoshu[datas.size() - 2] = 0;
        for (int i = 2; i < mFloats.length - 2; i++) {
            mDaoshu[i] = mFloats[i + 1] - mFloats[i - 1] + 2 * (mFloats[i + 2] - mFloats[i - 2]);
        }
        //最大导数
        float max_daoshu = 0;
        for (int i = 0; i < mDaoshu.length; i++) {
            if (max_daoshu < mDaoshu[i]) {
                max_daoshu = mDaoshu[i];
            }
        }
        //存储点数的R波数组
        ArrayList<Integer> dianshu = new ArrayList<>();
        float threshold = (float) (max_daoshu * 0.375);
        for (int i = 0; i < mFloats.length - 1; i++) {
            if (mFloats[i] > threshold && (mDaoshu[i] * mDaoshu[i + 1]) < 0) {
                dianshu.add(i);
            }
        }

        int[] result1 = new int[dianshu.size() - 1];
        for (int i = 0; i < dianshu.size() - 1; i++) {
            result1[i] = dianshu.get(i + 1) - dianshu.get(i);
        }

        int fs = 500;
        int[] result2 = new int[result1.length];
        for (int i = 0; i < result1.length; i++) {
            result2[i] = (60 * fs) / result1[i];
        }

        //输出结果result2【】
        return result2;
    }

    public int[] getXinlvdatas() {
        return xinlvdatas;
    }
}
