package mrcheng.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedInputStream;
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
    private int M;
    private int addNum;
    private ArrayList<Double> Result = new ArrayList<>();
    private Context mContext;
    private static final String TAG = "ReadFile";
    private int[] xinlvdatas;
    private ArrayList<Integer> resultX=new ArrayList<>();

    public ReadFile(Context context) {
        this.mContext = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int mScreenWidth = wm.getDefaultDisplay().getWidth();
        int mScreenHeight = wm.getDefaultDisplay().getHeight();
        float xdpi = context.getResources().getDisplayMetrics().xdpi;
        Calculate calculate = new Calculate(xdpi, mScreenWidth, mScreenHeight);
        N0 = calculate.getN0();
        N1 = calculate.getN1();
        M = calculate.getM();
        addNum=calculate.getAddNum();
        myThread = new MyThread(mContext);
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
            bis = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.fmecg8s));
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
                ArrayList<Double> XH = new ArrayList<>();
//                for (int q = 0; q < 4000; q++) {
//                    XH.add((float) doubles[q]);
//                }
//                xinlvdatas = CaculateXinLv(XH);
                //**********************************************************************//

                //结束

//                List<Double> mDraws = new ArrayList<>();
                int mycount = 4000;
                for (int q=0; q<mycount; q++)
                {
                    doubles[q]=doubles[q]*1000;//将doubles数组数值单位化为毫伏mV
//                            if(q%100==0)
//                                doubles[q]=1.0;
//                            if(doubles[q]<0)
//                                doubles[q]=0;
                }
                List<Double> TempDoubles = new ArrayList<>();
                List<Double> mDoubls = new ArrayList<>();
//                int mycount = 4000;//这个数字是要65536个doubles里面有用的数据长度
                for (int k = 0; k < mycount; k++) {
                    TempDoubles.add(doubles[k]);
                    if (TempDoubles.size() >= mycount / 8) {//Modified 每8秒的数据量为500*8=4000，要画出150*8=1200点
                        for (int h = 0; h < TempDoubles.size(); h++) {
                            mDoubls.add(TempDoubles.get(h));
                            if (h > 0 && h % addNum == 0) {//Modified 每5点插值一次使得8秒数据量达到4800点
                                mDoubls.add((TempDoubles.get(h - 1) + TempDoubles.get(h + 1)) / 2);
                            }
                        }
                        mDoubls.add(TempDoubles.get((mycount / 8) - 1));
                        for (int l = 0; l < mDoubls.size(); l++) {
                            if (l % M == 0) {//Modified 然后4倍抽取使得8秒画图点数为1200点
                                XH.add(mDoubls.get(l));
                                Result.add((double) (N0 - (float) (mDoubls.get(l) * N1)));
                            }
                        }

                        mDoubls.clear();
                        TempDoubles.clear();
                    }
                }
                xinlvdatas = CaculateXinLv(XH);
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

    /**
     * 计算心率的方法，返回一个长度为8的数组
     *
     * @param drawList
     */
    private int[] CaculateXinLv(ArrayList<Double> drawList) {
        int size = 1200;
        double[] mFloats = new double[size];
        double[] mDaoshu = new double[size];
        for (int i = 0; i < size; i++) {
            mFloats[i] = drawList.get(i);
        }

        mDaoshu[0] = mDaoshu[1] = mDaoshu[size - 1] = mDaoshu[size - 2] = 0;
        for (int i = 2; i < mFloats.length - 2; i++) {
            mDaoshu[i] = mFloats[i + 1] - mFloats[i - 1] + 2 * (mFloats[i + 2] - mFloats[i - 2]);
        }
        //最大导数
        double max_daoshu = 0;
        for (int i = 0; i < mDaoshu.length; i++) {
            if (max_daoshu < mDaoshu[i]) {
                max_daoshu = mDaoshu[i];
            }
        }
        //存储点数的R波数组
        ArrayList<Integer> dianshuX = new ArrayList<>();
        double threshold = (max_daoshu * 0.10);
        for (int i = 0; i < mFloats.length - 1; i++) {
            if (mFloats[i] > threshold && (mDaoshu[i] * mDaoshu[i + 1]) < 0) {
                dianshuX.add(i);
            }
        }

        //计算斜线部分，局部变换法
        double[] dianshuY = new double[dianshuX.size()];
        for (int i = 0; i < dianshuY.length; i++) {
            dianshuY[i] = drawList.get(dianshuX.get(i));
        }

        CaculateQRSWave(drawList, dianshuX, dianshuY);


        int[] result1 = new int[dianshuX.size() - 1];
        for (int i = 0; i < dianshuX.size() - 1; i++) {
            result1[i] = dianshuX.get(i + 1) - dianshuX.get(i);
        }

        int fs = 150;
        int[] result2 = new int[result1.length];
        for (int i = 0; i < result1.length; i++) {
            result2[i] = (60 * fs) / result1[i];
        }
        //输出结果result2【】
        return result2;
    }

    /**
     * 计算QRS波的方法
     * @param drawList
     * @param dianshuX
     * @param dianshuY
     */
    private void CaculateQRSWave(ArrayList<Double> drawList, ArrayList<Integer> dianshuX, double[] dianshuY) {
        if (dianshuX.get(0) < 13) {
            //第一个点小于40的时候，忽略第一个点
            //****************************************
            double[] foward13Y = new double[dianshuX.size() - 1];
            for (int i = 1; i < dianshuX.size(); i++) {
                foward13Y[i - 1] = drawList.get(dianshuX.get(i) - 12);
            }
            for (int i = 0; i < foward13Y.length; i++) {
                double tan = (dianshuY[i] - foward13Y[i]) / 12;
                double[] tempResult = new double[11];
                for (int j = 0; j < 11; j++) {
                    tempResult[j] = Math.abs((tan * (j + 1)) + foward13Y[i] - drawList.get(dianshuX.get(i) - 11 + j));
                }

                int MaxIndex = 0;
                double max = tempResult[0];
                for (int h = 0; h < tempResult.length - 1; h++) {
                    if (max < tempResult[h + 1]) {
                        max = tempResult[h + 1];
                        MaxIndex = h + 1;
                    }
                }

                int tempXresult=dianshuX.get(i) - 11 + MaxIndex;

                int secondIndex = 0;
                double secondMin = Math.abs(drawList.get(tempXresult-1) - drawList.get(tempXresult - 2));
                for (int j = 0; j < MaxIndex-1; j++) {
                    if (secondMin > (Math.abs(drawList.get(tempXresult - j - 2) - drawList.get(tempXresult - j - 3)))) {
                        secondIndex = j + 1;
                        secondMin = Math.abs(drawList.get(tempXresult - j - 2) - drawList.get(tempXresult - j - 3));
                    }
                }
                resultX.add(tempXresult - secondIndex-1);
            }
        } else {
            //***********************************************
            double[] foward13Y = new double[dianshuX.size()];
            for (int i = 0; i < dianshuX.size(); i++) {
                foward13Y[i] = drawList.get(dianshuX.get(i) - 12);
            }
            for (int i = 0; i < foward13Y.length; i++) {
                double tan = (dianshuY[i] - foward13Y[i]) / 12;
                double[] tempResult = new double[11];
                for (int j = 0; j < 11; j++) {
                    tempResult[j] = Math.abs((tan * (j + 1)) + foward13Y[i] - drawList.get(dianshuX.get(i) - 11 + j));
                }

                int MaxIndex = 0;
                double max = tempResult[0];
                for (int h = 0; h < tempResult.length - 1; h++) {
                    if (max < tempResult[h + 1]) {
                        max = tempResult[h + 1];
                        MaxIndex = h + 1;
                    }
                }
                int tempXresult=dianshuX.get(i) - 11 + MaxIndex;

                int secondIndex = 0;
                double secondMin = Math.abs(drawList.get(tempXresult-1) - drawList.get(tempXresult - 2));
                for (int j = 0; j < MaxIndex-1; j++) {
                    if (secondMin > (Math.abs(drawList.get(tempXresult - j - 2) - drawList.get(tempXresult - j - 3)))) {
                        secondIndex = j + 1;
                        secondMin = Math.abs(drawList.get(tempXresult - j - 2) - drawList.get(tempXresult - j - 3));
                    }
                }
                resultX.add(tempXresult - secondIndex-1);

                //算完第一个点之后应该算的是后面的15个点。
                if (dianshuX.get(i)  <= 1185) {
                    double back15Y = drawList.get(dianshuX.get(i) + 14);
                    tan = (dianshuY[i] - back15Y) / 15;
                    double[] backTempDouble = new double[13];
                    for (int j = 0; j < 13; j++) {
                        backTempDouble[j] = Math.abs((tan*(13-j)) +back15Y-drawList.get(dianshuX.get(i)+j+1));
                    }

                    MaxIndex = 0;
                    max = backTempDouble[0];
                    for (int h = 0; h < backTempDouble.length - 1; h++) {
                        if (max < backTempDouble[h + 1]) {
                            max = backTempDouble[h + 1];
                            MaxIndex = h + 1;
                        }
                    }
                    int backTempXresult=dianshuX.get(i)+ MaxIndex;

                    secondIndex = 0;
                    secondMin = Math.abs(drawList.get(backTempXresult+1) - drawList.get(backTempXresult + 2));
                    for (int j = 0; j < 13-MaxIndex; j++) {
                        if (secondMin > (Math.abs(drawList.get(backTempXresult + j+ 2) - drawList.get(backTempXresult + j + 3)))) {
                            secondIndex = j + 1;
                            secondMin = Math.abs(drawList.get(backTempXresult + j + 2) - drawList.get(backTempXresult + j + 3));
                        }
                    }
                    resultX.add(backTempXresult + secondIndex+1);
                }
            }
        }
    }

    public int[] getXinlvdatas() {
        return xinlvdatas;
    }

    public ArrayList<Integer> getResultX() {
        return resultX;
    }
}
