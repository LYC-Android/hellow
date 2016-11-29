package util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

import bean.DialogWrapper;
import bean.MyUser;
import cn.bmob.v3.BmobUser;
import mrcheng.myapplication.BaseActivity;

/**
 * Created by mr.cheng on 2016/10/27.
 */
public class CardService extends Service {
    private MulticastSocket multicastSocket;
    private static final int BUF_SIZE = 30;
    private byte[] regBuffer = new byte[BUF_SIZE];

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                myUdpMethod();
            }
        }).start();
    }

    private void myUdpMethod() {
        try {
            getIp();
            multicastSocket = new MulticastSocket(Constant.UDP_PORT);
            multicastSocket.joinGroup(InetAddress.getByName(Constant.MULTICAST_IP));
            while (true) {
                byte[] recvBuffer = new byte[BUF_SIZE];
                for (int i = 0; i < BUF_SIZE; i++) {
                    recvBuffer[i] = 0;
                }
                DatagramPacket rdp = new DatagramPacket(recvBuffer, recvBuffer.length);
                multicastSocket.receive(rdp);
                parsePackage(recvBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parsePackage(byte[] recvBuffer) throws IOException {
        MyUser myUser = BmobUser.getCurrentUser(this, MyUser.class);
        byte[] bytes = new byte[8];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = recvBuffer[i];
        }
        String result = new String(bytes);
        if (result.equals(myUser.getCardNumber())) {
            getIp();
            byte[] ipBytes = new byte[4];// 获得请求方的ip地址
            System.arraycopy(recvBuffer, 20, ipBytes, 0, 4);
            byte[] bytes1 = new byte[10];
            System.arraycopy(bytes1, 0, recvBuffer, 9, 10);
            String objectId = new String(bytes1);
            InetAddress targetIp = InetAddress.getByAddress(ipBytes);
            String IP = targetIp.getHostAddress().toString();
            EventBus.getDefault().post(new DialogWrapper(this, IP, objectId));
        }

    }

    public void findSicker() {
        byte[] bytes = Constant.isMe.getBytes();
        System.arraycopy(bytes, 0, regBuffer, 0, 4);
        try {
            if (null != multicastSocket && !multicastSocket.isClosed()) {
                DatagramPacket dp = new DatagramPacket(regBuffer, BUF_SIZE, InetAddress.getByName(Constant.MULTICAST_IP), Constant.UDP_PORT);
                multicastSocket.send(dp);
                EventBus.getDefault().post("已发送请求");
            }
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post("发送请求失败");
        }
    }

    public void refuse() {
        byte[] bytes = Constant.Refuse.getBytes();
        System.arraycopy(bytes, 0, regBuffer, 0, 4);
        try {
            if (null != multicastSocket && !multicastSocket.isClosed()) {
                DatagramPacket dp = new DatagramPacket(regBuffer, BUF_SIZE, InetAddress.getByName(Constant.MULTICAST_IP), Constant.UDP_PORT);
                multicastSocket.send(dp);
                EventBus.getDefault().post("已发送请求");
            }
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post("发送请求失败");
        }
    }


    private void getIp() throws IOException {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress()) {
                    if (inetAddress.isReachable(1000)) {
                        byte[] localIpBytes = inetAddress.getAddress();
                        System.arraycopy(localIpBytes, 0, regBuffer, 20, 4);

                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (multicastSocket != null && !multicastSocket.isClosed()) {
            multicastSocket.close();
        }
    }


}
