package com.jgz.rxnet2.converter;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/8
 * 描述:
 * 修订历史：
 * ================================================
 */
public class ApiException extends RuntimeException {

    private int mErrorCode;

    public int getErrorCode() {
        return mErrorCode;
    }

    public ApiException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.mErrorCode = errorCode;
    }
}
