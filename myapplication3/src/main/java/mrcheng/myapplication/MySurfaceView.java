package mrcheng.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by mr.cheng on 2016/8/7.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;
    private Paint mPaint;
    private Context mContext;
    private int mScreenWidth,mScreenHeight;
    private FileInputStream fis = null;
    private InputStreamReader isr=null;
    private BufferedReader br=null;
    private int start;
    private int oldX;
    private float oldY;
    int MyDrawFlag = 0;
    private ArrayList<Double> myDatas;
    private ArrayList<Double> mybuf;
    private static final String TAG = "MySurfaceView";
//    private int Vertical_line=213;
//    private int Horization_line=125;
    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    public MySurfaceView(Context context) {
        super(context);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void initView(Context context){
    mHolder=getHolder();
    mHolder.addCallback(this);
    this.mContext=context;
        myDatas=new ArrayList<>();
        mybuf=new ArrayList<>();
    mPaint = new Paint();
    mPaint.setColor(Color.GREEN);// 画笔为绿色
    mPaint.setStrokeWidth(1);// 设置画笔粗细
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setAntiAlias(true);//设置上抗锯齿，自己加的不知道有没有必要咯
    WindowManager wm = (WindowManager) getContext()
            .getSystemService(Context.WINDOW_SERVICE);
    mScreenWidth=wm.getDefaultDisplay().getWidth();//获得屏幕的宽
    mScreenHeight=wm.getDefaultDisplay().getHeight();//获得屏幕的高
}


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        MyDrawBcaground();
//        mPaint.setColor(Color.GREEN);
        mIsDrawing=true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void run() {

while (mIsDrawing){

    try {
        //读TXT文件可以看这里
//        InputStream inputStream=mContext.getResources().openRawResource(R.raw.f);
//        InputStreamReader isr=new InputStreamReader(inputStream,"UTF-8");
//        BufferedReader bf=new BufferedReader(isr);
//        String length;
//        int[] buf=new int[mScreenWidth/4];
//        int i=0;
//        while ((length=bf.readLine())!=null){
//            try {
//            buf[i]=Byte.valueOf(length);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//            i++;
//            if (i>=mScreenWidth/4){
//                i=0;
//            MyDraw(buf);
//            start=buf.length+start;
//            if (start>=mScreenWidth){start=0;}
//            break;
// }
//        }
        String pathName="/storage/sdcard0/NewECG65K.dat";//写下txt文档的路劲
        fis = new FileInputStream(pathName);//
//        bis = new BufferedInputStream(fis);
        isr=new InputStreamReader(fis);
        br=new BufferedReader(isr);//用bufferreader读(字符流)
        String length;
        while ((length=br.readLine())!=null){//每读一行就把它加到list，知道读完
           myDatas.add(new Double(length));//由于数据不确定有多长所以先把它放在一个list里面
        }
//        Log.v("mysize",myDatas.size()+"");
//        float temp[]=new float[(mScreenWidth)];
//            for (int i=0;i<myDatas.size();i++){
//                temp[i]=myDatas.get(i);
//                MyDrawFlag++;
//                if (MyDrawFlag>=mScreenWidth-1){
//                    MyDraw(temp);
//                            start=temp.length+start;
//        if (start>=mScreenWidth){start=0;}
//                    MyDrawFlag=0;
//                    return;
//                }
//            }
//        if (MyDrawFlag!=0){
//            MyDraw(temp);
//            start=temp.length+start;
//            if (start>=mScreenWidth){start=0;}
//            MyDrawFlag=0;
//        }
        br.close();//关闭相应的流
        isr.close();//关闭相应的流
        fis.close();//关闭相应的流
        double temp[]=new double[mScreenWidth/8];//定义一个长度为屏幕长度为1/8的double数组
        while (true) {//死循环，一直画图
            for (int i = 0; i < myDatas.size(); i++) {
                if (i%54==0) {
                    mybuf.add(myDatas.get(i));//考虑到可能数据不够1/8屏幕宽会导致赋值数组会出问题
                    // 所以再创一个list进行赋值
                    if (mybuf.size() >= mScreenWidth / 8) {//开始
                        for (int j = 0; j < mybuf.size(); j++) {
                            temp[j] = mybuf.get(j);
                        }//将buflist的值赋给double数组
                        MyDraw(temp);//传到画图的函数
                        start = temp.length + start;//start是X坐标，用于保存上次画图，X坐标画到哪里
                        if (start >= mScreenWidth) {
                            start = 0;
                        }//如果画完一组数据之后，大于屏幕宽则返回0点
                        mybuf.clear();//清除buflist的数据再继续循环
                    }//结束，再重来
                }
            }
        }




//        start=buf.length+start;
//        if (start>=mScreenWidth){start=0;}
//
//        mIsDrawing=false;

    } catch (Exception e) {
        e.printStackTrace();
    }
    finally {

    }
}

    }
    private void MyDraw(double[] buf){
            if (start==0)oldX=0;//如果X返回了0点，那么旧的X自然也要是0点了
            mCanvas=mHolder.lockCanvas(new Rect(start,0,start+buf.length,mScreenHeight));
        //获得canvas对象，并且规定只能在一个矩形(上面参数分别对应上下左右的坐标)上画图
            mCanvas.drawColor(Color.BLACK);//设置如果上面已经有图了，那么就用黑色覆盖它再画图
            for (int i=0;i<buf.length;i++){
               int x=i+start;//X坐标，
//             float y= mScreenHeight-(float) (buf[i]*k+mScreenHeight/2);//Y坐标
                float y=420-(float)(buf[i]*420.0);
             mCanvas.drawLine(oldX,oldY,x,y,mPaint);
            oldX=x;
             oldY=y;
           }//都画完了
            try {
                Thread.sleep(1000);//线程睡眠1S，
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mCanvas!=null)
            mHolder.unlockCanvasAndPost(mCanvas);//一次性提交刚刚画好的东西，刷新页面
//        Log.d(TAG, "1111111");

    }
}
