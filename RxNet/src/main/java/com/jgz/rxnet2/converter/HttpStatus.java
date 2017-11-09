package com.jgz.rxnet2.converter;

import com.google.gson.annotations.SerializedName;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/8
 * 描述:
 * 修订历史：
 * ================================================
 */
public class HttpStatus {

    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isUnSucceed() {
        return code != 200;
    }

    @Override
    public String toString() {
        return "HttpStatus{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
