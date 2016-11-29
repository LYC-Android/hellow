package bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by mr.cheng on 2016/8/25.
 */
public class MyUser extends BmobUser {
    private Boolean isDoctors;
    private String realName;
    private Boolean isBoys;
    private String age;
    private String medicalNumber;
    private String hospital;
    private String zhicheng;
    private String avatar;
    private String cardNumber;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getZhicheng() {
        return zhicheng;
    }

    public void setZhicheng(String zhicheng) {
        this.zhicheng = zhicheng;
    }

    public Boolean getIsDoctors() {
        return isDoctors;
    }

    public void setIsDoctors(Boolean isDoctors) {
        this.isDoctors = isDoctors;
    }

    public Boolean getIsBoys() {
        return isBoys;
    }

    public void setIsBoys(Boolean isBoys) {
        this.isBoys = isBoys;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMedicalNumber() {
        return medicalNumber;
    }

    public void setMedicalNumber(String medicalNumber) {
        this.medicalNumber = medicalNumber;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
