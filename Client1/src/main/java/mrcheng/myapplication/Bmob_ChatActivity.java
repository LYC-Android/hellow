package mrcheng.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Bmob_adapter.Bmob_ChatAdapter;
import bean.DatabaseInfo;
import bean.MyUser;
import bean.Request;
import bean.Resopnse;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.listener.OnRecordChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import util.ReadFileThread;

/**
 * Created by mr.cheng on 2016/9/22.
 */
public class Bmob_ChatActivity extends Activity implements ObseverListener, MessageListHandler {
    @InjectView(R.id.rc_view)
    RecyclerView rc_view;
    @InjectView(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;
    @InjectView(R.id.iv_record)
    ImageView iv_record;
    @InjectView(R.id.tv_voice_tips)
    TextView tv_voice_tips;
    @InjectView(R.id.layout_record)
    RelativeLayout layout_record;
    @InjectView(R.id.btn_chat_add)
    Button btn_chat_add;
    @InjectView(R.id.btn_chat_emo)
    Button btn_chat_emo;
    @InjectView(R.id.edit_msg)
    EditText edit_msg;
    @InjectView(R.id.btn_speak)
    Button btn_speak;
    @InjectView(R.id.btn_chat_voice)
    Button btn_chat_voice;
    @InjectView(R.id.btn_chat_keyboard)
    Button btn_chat_keyboard;
    @InjectView(R.id.btn_chat_send)
    Button btn_chat_send;
    @InjectView(R.id.pager_emo)
    ViewPager pager_emo;
    @InjectView(R.id.layout_emo)
    LinearLayout layout_emo;
    @InjectView(R.id.tv_picture)
    TextView tv_picture;
    @InjectView(R.id.tv_camera)
    TextView tv_camera;
    @InjectView(R.id.tv_location)
    TextView tv_location;
    @InjectView(R.id.layout_more)
    LinearLayout layout_more;
    @InjectView(R.id.ll_chat)
    LinearLayout ll_chat;
    @InjectView(R.id.layout_add)
    LinearLayout layout_add;
    @InjectView(R.id.title)
    TextView mTitle;
    private BmobIMConversation c;
    private Bmob_ChatAdapter adapter;
    private Drawable[] drawable_Anims;// 话筒动画
    protected LinearLayoutManager layoutManager;
    BmobRecordManager recordManager;
    private static final int CEMRA_CODE = 100;
    private static final int Image_CODE = 101;
    String mCurrentPhotoPath;
    private AlertDialog LocationDialog;
    private AlertDialog YesOrNodialog;
    private AlertDialog TrasmitDialog;
    private ReadFileThread TransmitThread;
    private BatteryReceiver batteryReceiver;
    private String mObjecid;
    /**
     * 电量百分比
     */
    private int battery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmob_activity_chat);
        ButterKnife.inject(this);
        c = BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) getIntent().getSerializableExtra("target"));
        mObjecid = c.getConversationId();
        List<DatabaseInfo> mList = DataSupport.select("realName").where("objectId = ?", mObjecid).find(DatabaseInfo.class);
        if (mList.size() > 0) {
            mTitle.setText(mList.get(0).getRealName());
        } else {
            mTitle.setText("未填写");
        }

        initSwipeLayout();
        initVoiceView();
        initBottomView();
        //注册广播接受者java代码
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //创建广播接受者对象
        batteryReceiver = new BatteryReceiver();

        //注册receiver
        registerReceiver(batteryReceiver, intentFilter);
    }

    @OnClick(R.id.edit_msg)
    public void onEditClick(View view) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
            layout_more.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_chat_emo)
    public void onEmoClick(View view) {
        if (layout_more.getVisibility() == View.GONE) {
            showEditState(true);
        } else {
            if (layout_add.getVisibility() == View.VISIBLE) {
                layout_add.setVisibility(View.GONE);
                layout_emo.setVisibility(View.VISIBLE);
            } else {
                layout_more.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_chat_add)
    public void onAddClick(View view) {
        if (layout_more.getVisibility() == View.GONE) {
            layout_more.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            if (layout_emo.getVisibility() == View.VISIBLE) {
                layout_emo.setVisibility(View.GONE);
                layout_add.setVisibility(View.VISIBLE);
            } else {
                layout_more.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_chat_voice)
    public void onVoiceClick(View view) {
        edit_msg.setVisibility(View.GONE);
        layout_more.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.GONE);
        btn_chat_keyboard.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.VISIBLE);
        hideSoftInputView();
    }

    @OnClick(R.id.btn_chat_keyboard)
    public void onKeyClick(View view) {
        showEditState(false);
    }

    @OnClick(R.id.btn_chat_send)
    public void onSendClick(View view) {
        sendMessage();
    }

    @OnClick(R.id.tv_picture)
    public void onPictureClick(View view) {
        sendRemoteImageMessage();

    }

    @OnClick(R.id.tv_camera)
    public void onCameraClick(View view) {
        sendVideoMessage();

    }

    @OnClick(R.id.tv_location)
    public void onLocationClick(View view) {
        sendLocationMessage();
    }

    /**
     * 发送请求
     */
    public void sendLocationMessage() {
        MyUser myUser = BmobUser.getCurrentUser(Bmob_ChatActivity.this, MyUser.class);
        Request msg = new Request();
        msg.setContent("1");
        Map<String, Object> map = new HashMap<>();
        map.put("objectId", myUser.getObjectId());
        map.put("realName", myUser.getRealName());
        msg.setExtraMap(map);
        c.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (e == null) {
                    BmobIMApplication.setWAITTING(true);
                    LocationDialog = new AlertDialog.Builder(Bmob_ChatActivity.this).create();
                    LocationDialog.setTitle("等待对方确认");
                    LocationDialog.setCancelable(false);
                    LocationDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Resopnse msg = new Resopnse();
                            msg.setContent("1");
                            Map<String, Object> map = new HashMap<>();
                            map.put("cancle", true);
                            msg.setExtraMap(map);
                            c.sendMessage(msg, new MessageSendListener() {
                                @Override
                                public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                                    BmobIMApplication.setWAITTING(false);
                                }
                            });
                        }
                    });
                    LocationDialog.show();
                } else {
                    Toast.makeText(Bmob_ChatActivity.this, "发送请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 直接发送远程图片地址
     */
    public void sendRemoteImageMessage() {
        Intent intent = new Intent();
                       /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                        /* 取得相片后返回本画面 */
        startActivityForResult(intent, Image_CODE);

    }

    /**
     * 发送文本消息
     */
    private void sendMessage() {
        String text = edit_msg.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            Toast.makeText(Bmob_ChatActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(text);
        //可设置额外信息
        Map<String, Object> map = new HashMap<>();
        map.put("level", "1");//随意增加信息
        msg.setExtraMap(map);
        c.sendMessage(msg, listener);
    }

    /**
     * 拍照
     */
    private void sendVideoMessage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//判断是否有相机应用
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();//创建临时图片文件
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CEMRA_CODE);
            }
        }
    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @param isEmo 用于区分文字和表情
     * @return void
     */
    private void showEditState(boolean isEmo) {
        edit_msg.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_msg.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_msg, 0);
        }
    }

    private void initBottomView() {
        edit_msg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    scrollToBottom();
                }
                return false;
            }
        });
        edit_msg.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollToBottom();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initSwipeLayout() {
        sw_refresh.setEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        adapter = new Bmob_ChatAdapter(this, c);
        rc_view.setAdapter(adapter);
        ll_chat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_chat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                //自动刷新
                queryMessages(null);
            }
        });
        //下拉加载
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
            }
        });
        //设置RecyclerView的点击事件
        adapter.setOnRecyclerViewListener(new Bmob_ChatAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public boolean onItemLongClick(int position) {
                //这里省了个懒，直接长按就删除了该消息
                c.deleteMessage(adapter.getItem(position));
                adapter.remove(position);
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        //清理资源
        if (recordManager != null) {
            recordManager.clear();
        }
        //更新此会话的所有消息为已读状态
        if (c != null) {
            c.updateLocalCache();
        }
        hideSoftInputView();
        unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
    }

    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage() {
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if (cache.size() > 0) {
            int size = cache.size();
            for (int i = 0; i < size; i++) {
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
        scrollToBottom();
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        for (int i = 0; i < list.size(); i++) {
            addMessage2Chat(list.get(i));
        }
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {


        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addMessage(msg);
            edit_msg.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            adapter.notifyDataSetChanged();
            edit_msg.setText("");
            scrollToBottom();
        }
    };

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    /**
     * 添加消息到聊天界面中
     *
     * @param event
     */
    private void addMessage2Chat(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (msg.getMsgType().equals("response") || msg.getMsgType().equals("request")) {
            if (c != null && event != null) {
                voice(msg);
                return;
            }
        }

        if (c != null && event != null && c.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()) {
            if (adapter.findPosition(msg) < 0) {//如果未添加到界面中
                adapter.addMessage(msg);

                //更新该会话下面的已读状态
                c.updateReceiveStatus(msg);
            }
            scrollToBottom();

        }
    }

    /**
     * 处理语音消息
     *
     * @param msg
     */
    private void voice(BmobIMMessage msg) {
        if (BmobIMApplication.isWAITTING()) {
            Resopnse resopnse = Resopnse.convert(msg);
            if (resopnse.getReceive()) {
                ShowTransmitDialog(resopnse.getRealName());
            } else {
                Toast.makeText(Bmob_ChatActivity.this, "对方拒绝了", Toast.LENGTH_SHORT).show();
            }
            if (LocationDialog != null && LocationDialog.isShowing()) {
                LocationDialog.dismiss();
            }
            BmobIMApplication.setWAITTING(false);
        } else {
            Resopnse resopnse = Resopnse.convert(msg);
            if (resopnse.getCancle() != null && resopnse.getCancle()) {
                if (BmobIMApplication.isCONNECTING()) {
                    if (TrasmitDialog != null && TrasmitDialog.isShowing())
                        TrasmitDialog.dismiss();
                    TransmitThread.setFlag(false);
                    BmobIMApplication.setCONNECTING(false);
                    Toast.makeText(Bmob_ChatActivity.this, "对方中断了连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (YesOrNodialog != null && YesOrNodialog.isShowing()) {
                    YesOrNodialog.dismiss();
                    Toast.makeText(Bmob_ChatActivity.this, "对方取消了请求", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Request request=Request.convert(msg);
            final String realName=request.getRealName();
            YesOrNodialog = new AlertDialog.Builder(Bmob_ChatActivity.this).create();
            YesOrNodialog.setTitle(realName+"请求与您你连接");
            YesOrNodialog.setCancelable(false);
            YesOrNodialog.setButton(DialogInterface.BUTTON_POSITIVE, "接受", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Resopnse resopnse = new Resopnse();
                    resopnse.setContent("1");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("receive", true);
                    resopnse.setExtraMap(map);
                    c.sendMessage(resopnse, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                            if (e == null) {
                                ShowTransmitDialog(realName);
                                Toast.makeText(Bmob_ChatActivity.this, "正在与"+realName+"连接", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Bmob_ChatActivity.this, "发送请求失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            YesOrNodialog.setButton(DialogInterface.BUTTON_NEGATIVE, "拒绝", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Resopnse resopnse = new Resopnse();
                    resopnse.setContent("1");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("receive", false);
                    resopnse.setExtraMap(map);
                    c.sendMessage(resopnse, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                            if (e == null) {
                                Toast.makeText(Bmob_ChatActivity.this, "拒绝了连接", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Bmob_ChatActivity.this, "发送请求失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            YesOrNodialog.show();
        }
    }

    /**
     * 弹出正在与对方进行心电传输的对话框
     */
    private void ShowTransmitDialog(String realName) {
        if (LocationDialog != null && LocationDialog.isShowing()) {
            LocationDialog.dismiss();
        }
        BmobIMApplication.setCONNECTING(true);
        TransmitThread = new ReadFileThread(Bmob_ChatActivity.this,"");
        TransmitThread.start();
        TrasmitDialog = new AlertDialog.Builder(Bmob_ChatActivity.this).create();
        TrasmitDialog.setTitle("正在"+realName+"进行心电传输");
        TrasmitDialog.setCancelable(false);
        TrasmitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TransmitThread.setFlag(false);
                TransmitThread.SendDisimiss();
                BmobIMApplication.setCONNECTING(false);
            }
        });
        TrasmitDialog.show();
    }

    /**
     * 首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     *
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg) {
        c.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                sw_refresh.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                }
            }
        });
    }

    /**
     * 初始化语音布局
     *
     * @param
     * @return void
     */
    private void initVoiceView() {
        btn_speak.setOnTouchListener(new VoiceTouchListener());
        initVoiceAnimRes();
        initRecordManager();

    }

    /**
     * 初始化语音动画资源
     *
     * @param
     * @return void
     * @Title: initVoiceAnimRes
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[]{
                getResources().getDrawable(R.mipmap.chat_icon_voice2),
                getResources().getDrawable(R.mipmap.chat_icon_voice3),
                getResources().getDrawable(R.mipmap.chat_icon_voice4),
                getResources().getDrawable(R.mipmap.chat_icon_voice5),
                getResources().getDrawable(R.mipmap.chat_icon_voice6)};
    }

    private void initRecordManager() {
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                }
            }
        });
    }

    /**
     * 发送语音消息
     *
     * @param local
     * @param length
     * @return void
     * @Title: sendVoiceMessage
     */
    private void sendVoiceMessage(String local, int length) {
        BmobIMAudioMessage audio = new BmobIMAudioMessage(local);
        c.sendMessage(audio, listener);
    }

    /**
     * 长按说话
     *
     * @author smile
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!Bmob_Util.checkSdCard()) {
                        Toast.makeText(Bmob_ChatActivity.this, "发送语音需要sdcard支持！", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(c.getConversationId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                sendVoiceMessage(recordManager.getRecordFilePath(c.getConversationId()), recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                default:
                    return false;
            }
        }

    }

    Toast toast;

    /**
     * 显示录音时间过短的Toast
     *
     * @return void
     * @Title: showShortToast
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CEMRA_CODE:
                if (resultCode != RESULT_OK) return;
                BmobIMImageMessage imImageMessage = new BmobIMImageMessage(mCurrentPhotoPath);
                c.sendMessage(imImageMessage, listener);

                break;
            case Image_CODE:
                if (resultCode == RESULT_OK) {
                    String path = getRealFilePath(Bmob_ChatActivity.this, data.getData());
                    BmobIMImageMessage image = new BmobIMImageMessage(path);
                    c.sendMessage(image, listener);
                }
                break;

        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //.getExternalFilesDir()方法可以获取到 SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //创建临时文件,文件前缀不能少于三个字符,后缀如果为空默认未".tmp"
        File image = File.createTempFile(
                imageFileName,  /* 前缀 */
                ".jpg",         /* 后缀 */
                storageDir      /* 文件夹 */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public int getBattery() {
        return battery;
    }

    private String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 广播接受者
     */
    class BatteryReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //判断它是否是为电量变化的Broadcast Action
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                //获取当前电量
                int level = intent.getIntExtra("level", 0);
                //电量的总刻度
                int scale = intent.getIntExtra("scale", 100);
                battery = ((level * 100) / scale);
            }
        }

    }
}

class Bmob_Util {
    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

}

