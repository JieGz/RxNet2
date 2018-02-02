package com.jgz.rxnet2.interceptor.baseurl;

import android.text.TextUtils;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2018/2/2
 * 描述:
 * 修订历史：
 * ================================================
 */
public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String url) {
        super("You've configured an invalid url : " + (TextUtils.isEmpty(url) ? "EMPTY_OR_NULL_URL" : url));
    }
}
