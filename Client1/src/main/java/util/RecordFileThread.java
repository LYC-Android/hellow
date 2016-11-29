package util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by mr.cheng on 2016/10/11.
 */
public class RecordFileThread extends Thread {
    private AudioRecord recorder;
    private Handler mHandler;

    static {
        System.loadLibrary("myNativeLib");
    }

    public native void getStringFromNative(short[] shorts, double[] doubles);

    public RecordFileThread(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {
        super.run();
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
        getStringFromNative(myRecoed, doubles);
        MyWriteFileMethod(doubles);
    }

    private void MyWriteFileMethod(final double[] doubles) {
        BufferedWriter writer = null;
        try {
            String path = "/storage/sdcard0/LYC.txt";
            File file = new File(path);
            if (file.exists()) file.delete();
            writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < 8192; i++) {
                writer.write(doubles[i] + "\r\n");
                writer.flush();
            }
            Message message = Message.obtain();
            message.arg1 = 3;
            mHandler.sendMessage(message);
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