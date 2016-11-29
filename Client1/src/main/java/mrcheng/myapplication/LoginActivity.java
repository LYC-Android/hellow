package mrcheng.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import bean.MyUser;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by mr.cheng on 2016/9/27.
 */
public class LoginActivity extends AppCompatActivity {
    @InjectView(R.id.username)
    EditText mEtUsername;
    @InjectView(R.id.password)
    EditText  mEtPassword;
    @InjectView(R.id.login)
    Button mLogin;
    @InjectView(R.id.register)
    Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("登陆");
        ButterKnife.inject(this);

    }

    @OnClick({R.id.login, R.id.register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                if (mEtUsername.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEtPassword.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mName = mEtUsername.getText().toString();
                String mPassWord = mEtPassword.getText().toString();
                BmobUser.loginByAccount(LoginActivity.this, mName, mPassWord, new LogInListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        if (myUser != null) {
                            if (!myUser.getIsDoctors()) {
                                Toast.makeText(LoginActivity.this, "用户登录成功", Toast.LENGTH_SHORT).show();
                                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
                                Intent i2 = new Intent(LoginActivity.this, ChatActivity.class);
                                startActivity(i2, oc2.toBundle());
                            } else {
                                Toast.makeText(LoginActivity.this, "请使用病人版的账号", Toast.LENGTH_SHORT).show();
                                myUser.logOut(LoginActivity.this);
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
    /**
     * 捕捉back
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            ExitDialog(LoginActivity.this).show();
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
}
