package Bmob_util;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import bean.DialogWrapper;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;

/**
 * Created by mr.cheng on 2016/9/19.
 */
public class DemoMessageHandler extends BmobIMMessageHandler {
    private Context context;

    public DemoMessageHandler(Context context) {
        this.context = context;
    }
    //当接收到服务器发来的消息时，此方法被调用
    @Override
    public void onMessageReceive(final MessageEvent event) {
        excuteMessage(event);
    }
    private void excuteMessage(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {
            EventBus.getDefault().post(new DialogWrapper(event.getFromUserInfo(),msg));
        } else {//直接发送消息事件
            EventBus.getDefault().post(event);
        }
    }

    //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        Map<String, List<MessageEvent>> map = event.getEventMap();
        //挨个检测下离线消息所属的用户的信息是否需要更新
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (BmobIMMessageType.getMessageTypeValue(list.get(i).getMessage().getMsgType()) == 0)
                    continue;
                excuteMessage(list.get(i));
            }
        }
    }
}