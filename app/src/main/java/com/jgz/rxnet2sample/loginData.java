package com.jgz.rxnet2sample;

/**
 * 登录App的信息
 * Created by Je on 2017/7/24.
 */

public class loginData {
    private String token;           //App的token需要保存到SharePreference里面
    private String phone;           //登录用户的手机号码需要保存到SharePreference里面
    private String rongToken;       //融云的token用于直接聊天室里面，最好也保存到SharePreference里面
    private String secretaryId;     //私人小秘的Id,最好也保存到SharePreference里面
    private int integral;        //当前积分，保存到SharePreference里面
    private String grade;           //用户的等级
    private String id;              //id
    private String top_integral;    //历史积分
    private String name;            //真实名字
    private String nickname;        //昵称
    private String signature;       //个性签名
    private String sex;             //
    private String label;           //标签
    private String is_sign;         //是否已经签到

    public String getToken() {
        return token;
    }

    public String getPhone() {
        return phone;
    }

    public String getRongToken() {
        return rongToken;
    }

    public String getSecretaryId() {
        return secretaryId;
    }

    public int getIntegral() {
        return integral;
    }

    public String getGrade() {
        return grade;
    }

    public String getId() {
        return id;
    }

    public String getTop_integral() {
        return top_integral;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSignature() {
        return signature;
    }

    public String getSex() {
        return sex;
    }

    public String getLabel() {
        return label;
    }

    public String getIs_sign() {
        return is_sign;
    }

    @Override
    public String toString() {
        return "loginData{" +
                "token='" + token + '\'' +
                ", phone='" + phone + '\'' +
                ", rongToken='" + rongToken + '\'' +
                ", secretaryId='" + secretaryId + '\'' +
                ", integral=" + integral +
                ", grade='" + grade + '\'' +
                ", id='" + id + '\'' +
                ", top_integral='" + top_integral + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", signature='" + signature + '\'' +
                ", sex='" + sex + '\'' +
                ", label='" + label + '\'' +
                ", is_sign='" + is_sign + '\'' +
                '}';
    }
}
