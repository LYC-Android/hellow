package mrcheng.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import bean.MyUser;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by mr.cheng on 2016/9/27.
 */
public class RegisterActivity extends AppCompatActivity {
    @InjectView(R.id.username)
    EditText mUsername;
    @InjectView(R.id.age)
    EditText mAge;
    @InjectView(R.id.medical_number)
    EditText mMedicalNumber;
    @InjectView(R.id.phoneNumber)
    EditText mPhoneNumber;
    @InjectView(R.id.boy)
    CheckBox mBoy;
    @InjectView(R.id.girl)
    CheckBox mGirl;
    @InjectView(R.id.register)
    Button mRegister;
    @InjectView(R.id.progress)
    AVLoadingIndicatorView mProgress;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.password1)
    EditText mPassword1;
    @InjectView(R.id.realName)
    EditText mRealName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
       setTitle("注册");
    }

    @OnClick(R.id.register)
    public void onClick() {
        String username = mUsername.getText().toString();
        String age = mAge.getText().toString();
        String Number = mMedicalNumber.getText().toString();
        String PhoneNumber = mPhoneNumber.getText().toString();
        String realNmae = mRealName.getText().toString();
        String medicalNumebr=mMedicalNumber.getText().toString();
        String password = mPassword.getText().toString();
        String password1 = mPassword1.getText().toString();
        if (username.equals("")) {
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age.equals("")) {
            Toast.makeText(RegisterActivity.this, "年龄不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Number.equals("")) {
            Toast.makeText(RegisterActivity.this, "病历号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (PhoneNumber.equals("")) {
            Toast.makeText(RegisterActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (realNmae.equals("")) {
            Toast.makeText(RegisterActivity.this, "真实姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password1.equals("")) {
            Toast.makeText(RegisterActivity.this, "确定密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(password1)) {
            Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        MyUser myUser=new MyUser();
        myUser.setUsername(username);
        myUser.setIsDoctors(false);
        myUser.setPassword(password);
        myUser.setMobilePhoneNumber(PhoneNumber);
        myUser.setMedicalNumber(medicalNumebr);
        myUser.setAge(age);
        if (mGirl.isChecked()){
            myUser.setIsBoys(false);
        }else {
            myUser.setIsBoys(true);
        }
        myUser.setRealName(realNmae);
        mRegister.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        myUser.signUp(RegisterActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, ChatActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                mProgress.setVisibility(View.GONE);
                mRegister.setVisibility(View.VISIBLE);
            }
        });

    }

    @OnClick({R.id.boy, R.id.girl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.boy:
                if (mGirl.isChecked()) {
                    mGirl.setChecked(false);
                }
                if (!mBoy.isChecked()) {
                    mGirl.setChecked(true);
                }
                break;
            case R.id.girl:
                if (mBoy.isChecked()) {
                    mBoy.setChecked(false);
                }
                if (!mGirl.isChecked()) {
                    mBoy.setChecked(true);
                }
                break;
        }
    }
}
