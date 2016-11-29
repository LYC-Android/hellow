package mrcheng.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mr.cheng on 2016/8/5.
 */
public class MyThread extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    //start
    private String filename = null;
    private TextView one, two, three, four, five, six, seven, eight, xinlv;
    private int len = 64000;
    private String chunkdescriptor = null;
    private int lenchunkdescriptor = 4;
    private long chunksize = 0;
    private int lenchunksize = 4;
    private String waveflag = null;
    private int lenwaveflag = 4;
    private String fmtubchunk = null;
    private int lenfmtubchunk = 4;
    private long subchunk1size = 0;
    private int lensubchunk1size = 4;
    private int audioformat = 0;
    private int lenaudioformat = 2;
    private static final String TAG = "MyThread";
    private int numchannels = 0;
    private int lennumchannels = 2;
    private long samplerate = 0;
    private int lensamplerate = 2;
    private long byterate = 0;
    private int blockalign = 0;
    private int bitspersample = 0;
    private String datasubchunk = null;
    private int lendatasubchunk = 4;
    private long subchunk2size = 0;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;
    private boolean issuccess = false;
    private boolean WriteFlag;
    //end
    private SimpleDateFormat format;
    private Date date;
    private int[] xinlvdatas = new int[8];
    private int counter;
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;
    private Paint mPaint;
    private Context mContext;
    private int mScreenWidth, mScreenHeight;
    private int start;
    private int oldX;
    private float oldY;

    static {
        System.loadLibrary("myNativeLib");
    }

    private float xdpi;
    private Caculate caculate;
    private int N0;
    private float N1;
    private Handler mHandler;

    public native void getStringFromNative(short[] shorts, double[] doubles);

    public MyThread(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyThread(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public MyThread(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mHolder = getHolder();
        this.mContext = context;
        mHolder.addCallback(this);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);// 画笔为绿色
        mPaint.setStrokeWidth(3);// 设置画笔粗细
        mPaint.setAntiAlias(true);//设置上抗锯齿，自己加的不知道有没有必要咯
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        xdpi = getResources().getDisplayMetrics().xdpi;
        caculate = new Caculate(xdpi, mScreenWidth, mScreenHeight);
        N0 = caculate.getN0();
        N1 = caculate.getN1();
    }

    public short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    public void GoToDraw(Handler handler, TextView textView, TextView one, TextView two, TextView three, TextView four, TextView five, TextView six, TextView seven, TextView eight, TextView xinlv) {
        this.mHandler = handler;
        this.xinlv = textView;
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
        this.six = six;
        this.seven = seven;
        this.eight = eight;
        mIsDrawing = true;

    }

//    private AudioRecord recorder;
//
//    @Override
////    这是用来录音然后显示数据的run方法
//    public void run() {
//        while (true) {
//            short[] myRecoed = new short[64000];
//            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                    8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT,
//                    128000);
//            recorder.startRecording();//开始录音
//            recorder.read(myRecoed, 0, myRecoed.length);
//            recorder.stop();
//            recorder.release();
//            double[] doubles = new double[65536];
//            getStringFromNative(myRecoed, doubles);
//            MyWriteFileMethod(doubles);
//            List<Double> mDraws = new ArrayList<>();
//            List<Double> TempDoubles = new ArrayList<>();
//            List<Double> draws = new ArrayList<>();
//            for (int k = 0; k < doubles.length / 8; k++) {
//                mDraws.add(doubles[k]);
//                if (mDraws.size() >= 1024) {
//                    for (int h = 0; h < mDraws.size(); h++) {
//                        TempDoubles.add(mDraws.get(h));
//                        if (h > 0 && h % 38 == 0) {
//                            TempDoubles.add((mDraws.get(h - 1) + mDraws.get(h + 1)) / 2);
//                        }
//                    }
//                    for (int l = 0; l < TempDoubles.size(); l++) {
//                        if (l % 7 == 0) {
//                            draws.add(TempDoubles.get(l));
//                        }
//                    }
//
//                    double[] tt = new double[30];
//                    for (int c = 0; c < 5; c++) {
//                        for (int d = 0; d < 30; d++) {
//                            tt[d] = draws.get(d + 30 * c);
//                        }
//                        MyDraw(tt);
//                        start = tt.length + start;
//                        if (start >= mScreenWidth) {
//                            start = 0;
//                        }
//                    }
//                    draws.clear();
//                    TempDoubles.clear();
//                    mDraws.clear();
//                }
//
//            }
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(mContext, "已经画完一次", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
////
////
////
//////        String temp=null;
//////        short[] myRecoed=new short[64000];
//////        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
//////                8000,AudioFormat.CHANNEL_CONFIGURATION_MONO,
//////                AudioFormat.ENCODING_PCM_16BIT,
//////                128000);
//////        recorder.startRecording();//开始录音
//////         recorder.read(myRecoed, 0, myRecoed.length);
//////        recorder.stop();
//////        recorder.release();
//////
//////        while (true){
//////            List<Short> mDraws=new ArrayList<>();
//////            short[] draws=new short[mScreenWidth/8];
//////            for (int k = 0; k <myRecoed.length; k++) {
//////                if (k%50==0){
//////                    mDraws.add(myRecoed[k]);
//////                    if (mDraws.size()>=mScreenWidth/8){
//////                        for (int j=0;j<mDraws.size();j++){
//////                            draws[j]=mDraws.get(j);
//////                        }
//////                        short[] tt=new short[30];
//////                        for (int c = 0; c < 5; c++) {
//////                            for (int d = 0; d < 30; d++) {
//////                                tt[d]=draws[d+30*c];
//////                            }
//////                            MyDraw(tt);
//////                            start=tt.length+start;
//////                            if (start>=mScreenWidth){start=0;}
//////                        }
//////
//////
//////                        mDraws.clear();
//////                    }
//////                }
//////            }
//////        }
////
////
////
//////while (mIsDrawing){
//////    String pathName="/storage/sdcard0/FMSignal.wav";
//////    try {
//////        fis = new FileInputStream(pathName);
//////        bis = new BufferedInputStream(fis);
//////        int length;
//////        byte[] buf=new byte[mScreenWidth/2];
//////
//////        while ((length=bis.read(buf,0,buf.length))!=-1){
//////            short[] shorts=byteArray2ShortArray(buf,buf.length/2);
//////            MyDraw(buf);
//////            start=buf.length+start;
//////            if (start>=mScreenWidth){start=0;}
//////        }
//////        mIsDrawing=false;
//////    }catch (Exception e){
//////
//////    }
////
//////   initReader(pathName);
//////}
//    }

    //    /**
//     * 测试audioTrack,录音
//     */
//    private AudioRecord recorder;
//    private void myTestAudioTrack() {
//        int bufferSize = AudioRecord.getMinBufferSize(8000,
//                AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                AudioFormat.ENCODING_PCM_16BIT);
//        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                8000,AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                AudioFormat.ENCODING_PCM_16BIT,
//                bufferSize*10);
//
//        recorder.startRecording();//开始录音
//        byte[] readBuffer = new byte[640];//录音缓冲区
////        double[] doubles=new double[320];
//        int length;
//
//        while(true){
//            length = recorder.read(readBuffer,0,640);//从mic读取音频数据
//            length=recorder.read(new short[200],0,200);
//            short[] shorts=byteArray2ShortArray(readBuffer,readBuffer.length/2);
////            getStringFromNative(shorts, doubles);
//            MyDraw(shorts);
//            start=shorts.length+start;
//            if (start>=mScreenWidth){start=0;}
//
//        }
//    }
//
    private void MyDraw(double[] buf) {
        counter++;
        if (start == 0) oldX = 0;
        mCanvas = mHolder.lockCanvas(new Rect(start, 0, start + buf.length, mScreenHeight));
        mCanvas.drawColor(Color.WHITE);
        for (int i = 0; i < buf.length; i++) {
            int x = i + start;
            float y = (N0 - (float) (buf[i] * N1));
            mCanvas.drawLine(oldX, oldY, x, y, mPaint);
            oldX = x;
            oldY = y;
        }

        //利用Canvas画东西
        try {
            Thread.sleep(200);//线程睡眠1S，
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mCanvas != null)
            mHolder.unlockCanvasAndPost(mCanvas);
        if (counter > 0 && counter % 5 == 0) {
            updateUI(counter / 5);
            if (counter == 40) counter = 0;
        }

    }

    private void openFile() {
        try {
            bis = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.fmsignal));
            int length;
            byte[] buf = new byte[len * 2];
            double[] doubles = new double[65536];//Modified
            while ((length = bis.read(buf, 0, buf.length)) != -1) {
                short[] shorts = byteArray2ShortArray(buf, buf.length / 2);
                getStringFromNative(shorts, doubles);
                //这里是写文件的方法
                //                        if (!WriteFlag) {
                //                            MyWriteFileMethod(doubles);
                //                        }
                ArrayList<Float> XH = new ArrayList<>();
                for (int q = 0; q < 4000; q++) {
                    XH.add((float) doubles[q]);
                }
                xinlvdatas = CaculateXinLv(XH);
                //**********************************************************************//

                //结束

                List<Double> mDraws = new ArrayList<>();
                double[] draws = new double[(mScreenWidth / 8)];
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
                                mDraws.add(mDoubls.get(l));
                            }
                        }

                        double[] tt = new double[30];
                        for (int c = 0; c < 5; c++) {
                            for (int d = 0; d < 30; d++) {
                                tt[d] = mDraws.get(d + 30 * c);
                            }
                            MyDraw(tt);
                            start = tt.length + start;
                            if (start >= mScreenWidth) {
                                start = 0;
                            }
                        }
                        mDraws.clear();
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

//    private void initReader(String filename) {
//
////FileInputStream fileInputStream=new FileInputStream()
//        this.filename = filename;  //获得用户名
//        try {
//            fis = new FileInputStream(this.filename);
//            bis = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.fmsignal));
//
//            this.chunkdescriptor = readString(lenchunkdescriptor);
//            if (!chunkdescriptor.endsWith("RIFF"))  //0~3检查前四个字节是否为RIFF
//                throw new IllegalArgumentException("RIFF miss, " + filename + " is not a wave file.");
//
//            this.chunksize = readLong();  //4~7代表大小
//            this.waveflag = readString(lenwaveflag);
//            if (!waveflag.endsWith("WAVE"))  //8~11检查是不是WAVE这四个字节
//                throw new IllegalArgumentException("WAVE miss, " + filename + " is not a wave file.");
//
//            this.fmtubchunk = readString(lenfmtubchunk);
//            if (!fmtubchunk.endsWith("fmt "))  //12~15 fmt"" 检查是不是这个
//                throw new IllegalArgumentException("fmt miss, " + filename + " is not a wave file.");
//
//            this.subchunk1size = readLong();  //16~19代表PCM数据
//            this.audioformat = readInt();  //20~21fmt的头
//            this.numchannels = readInt();  //22~23单声道还是双声道
//            this.samplerate = readLong();  //24~27采样率
//            this.byterate = readLong();  //28~31每秒播放字节数
//            this.blockalign = readInt();  //32~33采样一次占字节数
//            this.bitspersample = readInt();  //34~35量化数 8或者16
//
//            this.datasubchunk = readString(lendatasubchunk);
//            if (!datasubchunk.endsWith("data"))  //36~39肯定是data
//                throw new IllegalArgumentException("data miss, " + filename + " is not a wave file.");
//            this.subchunk2size = readLong();  //40~43采样数据字节数
//
//            this.len = (int) (this.subchunk2size / (this.bitspersample / 8) / this.numchannels);
//
//            int length;
//            byte[] buf = new byte[len * 2];
//            double[] doubles = new double[65536];//Modified
//            for (int i = 0; i < this.len; ++i) {
//                for (int n = 0; n < this.numchannels; ++n) {
//                    while ((length = bis.read(buf, 0, buf.length)) != -1) {
//                        short[] shorts = byteArray2ShortArray(buf, buf.length / 2);
//                        getStringFromNative(shorts, doubles);
//                        //这里是写文件的方法
////                        if (!WriteFlag) {
////                            MyWriteFileMethod(doubles);
////                        }
//                        ArrayList<Float> XH = new ArrayList<>();
//                        for (int q = 0; q < 4000; q++) {
//                            XH.add((float) doubles[q]);
//                        }
//                        xinlvdatas = CaculateXinLv(XH);
//                        //**********************************************************************//
//
//                        //结束
//
//                        List<Double> mDraws = new ArrayList<>();
//                        double[] draws = new double[(mScreenWidth / 8)];
//                        List<Double> TempDoubles = new ArrayList<>();
//                        List<Double> mDoubls = new ArrayList<>();
//                        int mycount = 4000;//这个数字是要65536个doubles里面有用的数据长度
//                        for (int k = 0; k < mycount; k++) {
//                            TempDoubles.add(doubles[k]);
//                            if (TempDoubles.size() >= mycount / 8) {//Modified 每8秒的数据量为500*8=4000，要画出150*8=1200点
//                                for (int h = 0; h < TempDoubles.size(); h++) {
//                                    mDoubls.add(TempDoubles.get(h));
//                                    if (h > 0 && h % 5 == 0) {//Modified 每5点插值一次使得8秒数据量达到4800点
//                                        mDoubls.add((TempDoubles.get(h - 1) + TempDoubles.get(h + 1)) / 2);
//                                    }
//                                }
//                                mDoubls.add(TempDoubles.get((mycount / 8) - 1));
//                                for (int l = 0; l < mDoubls.size(); l++) {
//                                    if (l % 4 == 0) {//Modified 然后4倍抽取使得8秒画图点数为1200点
//                                        mDraws.add(mDoubls.get(l));
//                                    }
//                                }
//
//                                double[] tt = new double[30];
//                                for (int c = 0; c < 5; c++) {
//                                    for (int d = 0; d < 30; d++) {
//                                        tt[d] = mDraws.get(d + 30 * c);
//                                    }
//                                    MyDraw(tt);
//                                    start = tt.length + start;
//                                    if (start >= mScreenWidth) {
//                                        start = 0;
//                                    }
//                                }
//                                mDraws.clear();
//                                mDoubls.clear();
//                                TempDoubles.clear();
//                            }
//                        }
//
//                    }
//
//
//                }
//
//            }
//
//
//            mIsDrawing = false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (bis != null)
//                    bis.close();
//                if (fis != null)
//                    fis.close();
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
//    }

    private String readString(int len) {
        byte[] buf = new byte[len];
        try {
            if (bis.read(buf) != len)
                throw new IOException("no more data!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buf);
    }

    private int readInt() {
        byte[] buf = new byte[2];
        int res = 0;
        try {
            if (bis.read(buf) != 2)
                throw new IOException("no more data!!!");
            res = (buf[0] & 0x000000FF) | (((int) buf[1]) << 8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private long readLong() {
        long res = 0;
        try {
            long[] l = new long[4];
            for (int i = 0; i < 4; ++i) {
                l[i] = bis.read();
                if (l[i] == -1) {
                    throw new IOException("no more data!!!");
                }
            }
            res = l[0] | (l[1] << 8) | (l[2] << 16) | (l[3] << 24);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void run() {
//        String pathName = "/storage/sdcard0/FMECG8s.wav";//FMSignal.wav";//Modified
//        initReader(pathName);
        openFile();
    }

    private void MyWriteFileMethod(final double[] doubles) {
        if (!WriteFlag) {
            new Thread(new Runnable() {
                BufferedWriter writer;

                @Override
                public void run() {
                    try {
                        String path = "/storage/sdcard0/LYC.txt";
                        File file = new File(path);
                        if (file.exists()) file.delete();
                        writer = new BufferedWriter(new FileWriter(file));
                        for (int i = 0; i < 8192; i++) {//Modified
                            writer.write(doubles[i] + "\r\n");
                            writer.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    WriteFlag = true;
                }
            }).start();
        }
    }

    private void updateUI(final int i) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (i) {
                    case 1:
                        one.setText(getCurrentTime());
                        break;
                    case 2:
                        two.setText(getCurrentTime());
                        break;
                    case 3:
                        three.setText(getCurrentTime());
                        break;
                    case 4:
                        four.setText(getCurrentTime());
                        break;
                    case 5:
                        five.setText(getCurrentTime());
                        break;
                    case 6:
                        six.setText(getCurrentTime());
                        break;
                    case 7:
                        seven.setText(getCurrentTime());
                        break;
                    case 8:
                        eight.setText(getCurrentTime());
                        break;
                }

                xinlv.setText(xinlvdatas[i - 1] + "/min");
            }
        });
    }

    private String getCurrentTime() {
        format = new SimpleDateFormat("HH:mm:ss");
        date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(this).start();
        Toast.makeText(mContext, "start", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Toast.makeText(mContext, "destory", Toast.LENGTH_SHORT).show();
    }
}
