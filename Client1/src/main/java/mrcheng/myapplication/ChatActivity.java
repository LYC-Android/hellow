package mrcheng.myapplication;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import Bmob_adapter.Bmob_ConversationAdapter;
import bean.Conversation;
import bean.DatabaseInfo;
import bean.MyUser;
import bean.PrivateConversation;
import bean.emptyConversation;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import util.CardService;
import util.NetworkConnectChangedReceiver;
import util.UpFileActivity;

public class ChatActivity extends BaseActivity {
    @InjectView(R.id.download)
    FloatingActionButton mDownload;
    @InjectView(R.id.setting)
    FloatingActionButton mSetting;
    @InjectView(R.id.out)
    FloatingActionButton mOut;
    @InjectView(R.id.online_user_list)
    RecyclerView mRecycle;
    @InjectView(R.id.progress)
    AVLoadingIndicatorView mProgress;
    @InjectView(R.id.no_user)
    LinearLayout mNoUser;
    @InjectView(R.id.bt_reconnect)
    Button mBtReconnect;
    private ArrayList<Conversation> mDatas;
    private Bmob_ConversationAdapter mAdapter;
    private NetworkConnectChangedReceiver mReceiver=new NetworkConnectChangedReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        setTitle("医生列表");
        final MyUser myUser = BmobUser.getCurrentUser(ChatActivity.this, MyUser.class);
        if (myUser == null) {
            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
            finish();
            return;
        }
        initView();
        connect(myUser);
                IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver,filter);
    }


    private void initView() {
        mDatas = new ArrayList<>();
        mAdapter = new Bmob_ConversationAdapter(ChatActivity.this, mDatas);
        mRecycle.setLayoutManager(new LinearLayoutManager(this));
        mRecycle.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new Bmob_ConversationAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

                if (mDatas.get(position) instanceof PrivateConversation) {
                    Intent intent = new Intent(ChatActivity.this, Bmob_ChatActivity.class);
                    intent.putExtra("target", ((PrivateConversation) mDatas.get(position)).getConversation());
                    startActivity(intent);
                } else {
                    BmobIMUserInfo info = ((emptyConversation) (mDatas.get(position))).getInfo();
                    BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                    Intent intent = new Intent(ChatActivity.this, Bmob_ChatActivity.class);
                    intent.putExtra("target", c);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mProgress.setIndicatorColor(Color.RED);
    }

    private void connect(final MyUser myUser) {
        mProgress.setVisibility(View.VISIBLE);
        BmobIM.connect(myUser.getObjectId(), new ConnectListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    queryAllUser(myUser);
                } else {
                    mNoUser.setVisibility(View.VISIBLE);
                    mProgress.hide();
                }
            }
        });
    }


    private void queryAllUser(BmobUser user) {
        BmobQuery<MyUser> query1=new BmobQuery<>();
        query1.addWhereNotEqualTo("username", user.getUsername());
        BmobQuery<MyUser> query2=new BmobQuery<>();
        query2.addWhereNotEqualTo("isDoctors",false);
        List<BmobQuery<MyUser>> andQuerys = new ArrayList<>();
        andQuerys.add(query1);
        andQuerys.add(query2);
        final BmobQuery<MyUser> query = new BmobQuery<>();
        query.and(andQuerys);
        query.findObjects(ChatActivity.this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list.size() > 0) {
                    DataSupport.deleteAll(DatabaseInfo.class);
                    for (int i = 0; i < list.size(); i++) {
                        mDatas.add(new emptyConversation(list.get(i)));
                        SvaeInDataBase(list, i);
                    }
                    mNoUser.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ChatActivity.this, "没有用户", Toast.LENGTH_SHORT).show();
                }
                FirstQuery();
                mProgress.hide();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ChatActivity.this, s, Toast.LENGTH_SHORT).show();
                mNoUser.setVisibility(View.VISIBLE);
                mProgress.hide();
            }
        });
    }

    private void SvaeInDataBase(List<MyUser> list, int i) {
        DatabaseInfo info = new DatabaseInfo();
        if (list.get(i).getAge() != null) {
            info.setAge(list.get(i).getAge());
        } else {
            info.setAge("未填写");
        }

        info.setObjectId(list.get(i).getObjectId());

        if (list.get(i).getRealName() != null) {
            info.setRealName(list.get(i).getRealName());
        } else {
            info.setRealName("未填写");
        }

        if (list.get(i).getIsBoys()!=null) {
            if (list.get(i).getIsBoys()) {
                info.setSex("男");
            } else {
                info.setSex("女");
            }
        } else {
            info.setSex("未填写");
        }

        if (list.get(i).getMobilePhoneNumber() != null) {
            info.setPhoneNumber(list.get(i).getMobilePhoneNumber());
        } else {
            info.setPhoneNumber("未填写");
        }

        if (list.get(i).getMedicalNumber() != null) {
            info.setMedicalNumber(list.get(i).getMedicalNumber());
        } else {
            info.setMedicalNumber("未填写");
        }

        info.save();
    }
    @OnClick({R.id.download, R.id.setting, R.id.out, R.id.bt_reconnect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download:
//                Intent intent1 = new Intent(ChatActivity.this, DownloadActivity.class);
//                startActivity(intent1, ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this, mDownload, "fab").toBundle());
                Intent intent2=new Intent(ChatActivity.this, UpFileActivity.class);
                startActivity(intent2);
                break;
            case R.id.setting:
                Intent intent = new Intent(ChatActivity.this, InformationActivity.class);
                startActivity(intent);
                break;
            case R.id.out:
                out_dialog(ChatActivity.this).show();
                break;
            case R.id.bt_reconnect:
                MyUser myUser = BmobUser.getCurrentUser(ChatActivity.this, MyUser.class);
                mNoUser.setVisibility(View.GONE);
                connect(myUser);
                break;
        }
    }

    /**
     * 退出登陆
     */
    public void Out() {
        BmobUser.logOut(ChatActivity.this);
        startActivity(new Intent(ChatActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        BmobIM.getInstance().clear();

        stopService(new Intent(ChatActivity.this, CardService.class));
        super.onDestroy();

    }



    @Override
    protected void onRestart() {
        if (mAdapter != null) {
            FirstQuery();
        } else {
            connect(BmobUser.getCurrentUser(ChatActivity.this, MyUser.class));
        }

        super.onRestart();
    }

    /**
     * 捕捉back
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            ExitDialog(ChatActivity.this).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 提示退出系统
     *
     * @param context
     * @return
     */
    private Dialog ExitDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("系统信息");
        builder.setMessage("确定要退出程序吗?");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ActivityCollector.finishAll();
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        return builder.create();
    }

    private Dialog out_dialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("系统信息");
        builder.setMessage("要退出当前用户吗");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Out();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        //重新获取本地消息并刷新列表
        FirstQuery();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        //重新刷新列表
        FirstQuery();
    }

    /**
     * 获取会话列表的数据：增加新朋友会话
     *
     * @return
     */
    private List<PrivateConversation> getConversations() {
        //添加会话
        List<PrivateConversation> conversationList = new ArrayList<>();
        conversationList.clear();
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if (list != null && list.size() > 0) {
            for (BmobIMConversation item : list) {
                switch (item.getConversationType()) {
                    case 1://私聊
                        conversationList.add(new PrivateConversation(item));
                        break;
                    default:
                        break;
                }
            }
        }
        //添加新朋友会话-获取好友请求表中最新一条记录

        return conversationList;
    }


    private void FirstQuery() {
        List<PrivateConversation> mlist = getConversations();
        if (mlist.size() > 0) {
            for (int i = 0; i < mlist.size(); i++) {
                for (int j = 0; j < mDatas.size(); j++) {
                    if (mDatas.get(j).getcId().equals(mlist.get(i).getcId())){
                        mDatas.remove(j);
                        mDatas.add(0,mlist.get(i));
                        break;
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

//    private void query() {
//        List<PrivateConversation> mlist = getConversations();
//        if (mlist.size() > 0) {
//            for (int i = 0; i < mlist.size(); i++) {
//                for (int j = 0; j < templist.size(); j++) {
//                    if (templist.get(j).equals(mlist.get(i).getcId())) {
//                        mDatas.set(j, mlist.get(i));
//                        break;
//                    }
//                }
//                mAdapter.notifyDataSetChanged();
//            }
//        }
//
//    }


}
