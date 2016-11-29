package mrcheng.myapplication;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity {
    @InjectView(R.id.xinlv)
    TextView mXinlv;
    @InjectView(R.id.one)
    TextView mOne;
    @InjectView(R.id.two)
    TextView mTwo;
    @InjectView(R.id.three)
    TextView mThree;
    @InjectView(R.id.four)
    TextView mFour;
    @InjectView(R.id.five)
    TextView mFive;
    @InjectView(R.id.six)
    TextView mSix;
    @InjectView(R.id.seven)
    TextView mSeven;
    @InjectView(R.id.eight)
    TextView mEight;
    private MyThread myThread;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myThread = (MyThread) findViewById(R.id.surfaceView);

        myThread.GoToDraw(mHandler,mXinlv,mOne,mTwo,mThree,mFour,mFive,mSix,mSeven,mEight,mXinlv );

//        new MyThread(MainActivity.this).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    InputStream inputStream=getResources().openRawResource(R.raw.my_wave);
//                    int length;
//                    byte[] buf=new byte[1024];
//                    while ((length=inputStream.read(buf,0,buf.length))!=-1){
//                        for (int i=0;i<length;i++){
//                            Log.v(i+" ",buf[i]+"");
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        File myFile=new File("/storage/sdcard0/SquareWave.txt");
//        Log.v("SB",myFile.exists()+"");
//        File file=new File(Environment.getExternalStorageDirectory().getPath());
//        if (file.canRead()){
//            File[] files=file.listFiles();
//            for (int i=0;i<files.length;i++){
//                Log.v(i+"",i+"??"+files[i].getPath());
//            }
//        }else {
//            Toast.makeText(MainActivity.this, "SB", Toast.LENGTH_SHORT).show();
//        }
    }
}
