package mrcheng.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import bean.MyUser;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by mr.cheng on 2016/9/4.
 */
public class InformationActivity extends BaseActivity {
    @InjectView(R.id.realName)
    EditText mRealName;
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
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.inject(this);
        setTitle("个人信息");
        progressDialog = new ProgressDialog(InformationActivity.this);
        progressDialog.setTitle("正在获取信息");
        progressDialog.setMessage("请稍候...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mRealName.getText().toString() != null || mAge.getText().toString() != null || mPhoneNumber.getText().toString() != null) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        return;
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        getinformation();
    }

    private void getinformation() {
        MyUser myUser = BmobUser.getCurrentUser(InformationActivity.this, MyUser.class);
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", myUser.getUsername());
        query.addQueryKeys("mobilePhoneNumber,realName,isBoys,medicalNumber,age");
        query.findObjects(InformationActivity.this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    ActivityCollector.LingYiFlag = true;
                }
                if (list.get(0).getRealName() != null) {
                    mRealName.setText(list.get(0).getRealName());
                }
                if (list.get(0).getIsBoys() != null) {
                    if (list.get(0).getIsBoys()) {
                        mBoy.setChecked(true);
                    } else {
                        mGirl.setChecked(true);
                        mBoy.setChecked(false);
                    }
                }
                if (list.get(0).getMobilePhoneNumber() != null) {
                    mPhoneNumber.setText(list.get(0).getMobilePhoneNumber());
                }
                if (list.get(0).getMedicalNumber() != null) {
                    mMedicalNumber.setText(list.get(0).getMedicalNumber());
                }
                if (list.get(0).getAge() != null) {
                    mAge.setText(list.get(0).getAge());
                }
            }

            @Override
            public void onError(int i, String s) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    ActivityCollector.LingYiFlag = true;
                }
                Toast.makeText(InformationActivity.this, "查询失败" + s, Toast.LENGTH_SHORT).show();

            }
        });

    }


    @OnClick({R.id.boy, R.id.girl, R.id.register})
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
            case R.id.register:
                String realName = mRealName.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();
                String medicalNumber = mMedicalNumber.getText().toString();
                String age = mAge.getText().toString();
                if (realName.equals("")) {
                    Toast.makeText(InformationActivity.this, "真实姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phoneNumber.equals("")) {
                    Toast.makeText(InformationActivity.this, "电话不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (medicalNumber.equals("")) {
                    Toast.makeText(InformationActivity.this, "病历号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (age.equals("")) {
                    Toast.makeText(InformationActivity.this, "年龄不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                MyUser myUser = BmobUser.getCurrentUser(InformationActivity.this, MyUser.class);
                if (mBoy.isChecked()) {
                    myUser.setIsBoys(true);
                } else {
                    myUser.setIsBoys(false);
                }
                myUser.setRealName(realName);
                myUser.setMobilePhoneNumber(phoneNumber);
                myUser.setMedicalNumber(medicalNumber);
                myUser.setAge(age);
                mRegister.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                MyUser myUser1 = BmobUser.getCurrentUser(InformationActivity.this, MyUser.class);
                myUser.update(InformationActivity.this, myUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(InformationActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(InformationActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                        mRegister.setVisibility(View.VISIBLE);
                        mProgress.setVisibility(View.GONE);
                    }
                });

                break;
        }
    }
}