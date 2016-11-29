package mrcheng.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by mr.cheng on 2016/8/9.
 */
public class MyBackGround extends View {
    private Paint mPaint;
    private int Vertical_line;
    private int Horization_line;
    private int mScreenWidth,mScreenHeigth;
    private float xdpi;
    private static final String TAG = "MyBackGround";
    private int NumperMM;

    public MyBackGround(Context context) {
        super(context);
        initView();
    }


    public MyBackGround(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyBackGround(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        xdpi = getResources().getDisplayMetrics().xdpi;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenHeigth=h;
        mScreenWidth=w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        setBackgroundColor();
        Caculate caculate=new Caculate(xdpi,mScreenWidth,mScreenHeigth);
        NumperMM=caculate.getNumPerMM();
        Vertical_line=caculate.getVertical_line();
        Horization_line=caculate.getHorization_line();
        mPaint = new Paint();
        mPaint.setColor(0x7FFF5151);
        // 画笔为绿色
        mPaint.setStrokeWidth(1);// 设置画笔粗细
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        for (int i=0;i<Horization_line+1;i++){
            canvas.drawLine(1,1+i*NumperMM,mScreenWidth-1,1+i*NumperMM,mPaint);
            //               x   y     x              y
        }
        for (int j=0;j<Vertical_line+1;j++){
            canvas.drawLine(1+j*NumperMM,1,1+j*NumperMM,mScreenHeigth-1,mPaint);
            //                  x   y    x      y
        }

        mPaint.setStrokeWidth((float) 1.50);// 设置画笔粗细
        for (int i=0;i<(Horization_line/5)+1;i++){
            canvas.drawLine(1,1+i*NumperMM*5,mScreenWidth-1,1+i*NumperMM*5,mPaint);
            //               x   y     x              y
        }
        for (int j=0;j<(Vertical_line/5)+1;j++){
            canvas.drawLine(1+j*NumperMM*5,1,1+j*NumperMM*5,mScreenHeigth-1,mPaint);
            //                  x   y    x      y
        }
//        mPaint.setStrokeWidth(3);// 设置画笔粗细
        mPaint.setColor(0xAFFF5151);
        for (int j=1;j<(mScreenWidth/25)+1;j++){
            canvas.drawLine(j*NumperMM*5*5,1,j*NumperMM*5*5,mScreenHeigth-1,mPaint);
            //                  x   y    x      y
        }
    }
}
