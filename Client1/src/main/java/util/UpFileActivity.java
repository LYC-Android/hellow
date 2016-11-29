package util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bean.MyUser;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import mrcheng.myapplication.BaseActivity;
import mrcheng.myapplication.R;

/**
 *
 */

public class UpFileActivity extends BaseActivity implements Runnable {

    @InjectView(R.id.status)
    TextView mStatus;
    private AudioRecord recorder;
    private String path;
    private Handler mHandler=new Handler(Looper.getMainLooper());

    static {
        System.loadLibrary("myNativeLib");
    }

//    public native void getStringFromNative(short[] shorts, double[] doubles);

    @InjectView(R.id.record)
    Button mRecord;
    @InjectView(R.id.up)
    Button mUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upfile_layout);
        ButterKnife.inject(this);
        setTitle("离线上传");
        mUp.setEnabled(false);
    }

    @OnClick({R.id.record, R.id.up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record:
                new Thread(this).start();
                break;
            case R.id.up:
                mStatus.setText("正在上传");
                new UpFileMethod(path, UpFileActivity.this,mHandler,mStatus).done();
                break;
        }
    }

    @Override
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpFileActivity.this, "正在录制，请耐心等待8秒", Toast.LENGTH_SHORT).show();
                mStatus.setText("当前状态:录制中.....");
            }
        });
        short[] myRecoed = new short[64000];
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                128000);
        recorder.startRecording();//开始录音
        recorder.read(myRecoed, 0, myRecoed.length);
        recorder.stop();
        recorder.release();
        double[] doubles = new double[65536];
        new ReadFileThread(UpFileActivity.this, "").getStringFromNative(myRecoed, doubles);
        MyWriteFileMethod(doubles);
    }

    private void MyWriteFileMethod(final double[] doubles) {
        BufferedWriter writer = null;
        try {
            MyUser myUser = BmobUser.getCurrentUser(UpFileActivity.this, MyUser.class);
            StringBuilder builder = new StringBuilder();
            builder.append("/storage/sdcard0/");
            builder.append(myUser.getUsername());
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());
            String name = format.format(date);
            builder.append(name);
            builder.append(".txt");
            path = builder.toString();
            File file = new File(path);
            if (file.exists()) file.delete();
            writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < 8192; i++) {
                writer.write(doubles[i] + "\r\n");
                writer.flush();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UpFileActivity.this, "录制成功", Toast.LENGTH_SHORT).show();
                    mUp.setEnabled(true);
                    mStatus.setText("当前状态:录制成功，可以等待上传");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
