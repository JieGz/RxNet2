package com.jgz.rxnet2.utils;

import com.jgz.rxnet2.interceptor.baseurl.InvalidUrlException;

import okhttp3.HttpUrl;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/9
 * 描述: RxNet提功的工具类
 * 修订历史：
 * ================================================
 */
public class RxNetUtil {
    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }


    public static HttpUrl checkUrl(String url) {
        HttpUrl parseUrl = HttpUrl.parse(url);
        if (null == parseUrl) {
            throw new InvalidUrlException(url);
        } else {
            return parseUrl;
        }
    }
}
