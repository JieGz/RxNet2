package com.jgz.rxnet2.interceptor.baseurl.parser;

import okhttp3.HttpUrl;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2018/2/2
 * 描述:
 * 修订历史：
 * ================================================
 */
public interface IUrlParser {

    /**
     * 将Retrofit中通过@Headers()中映射的url解析成完整的的{Okhttp3#HttpUrl}
     */
    HttpUrl parseUrl(HttpUrl headConfigUrl, HttpUrl url);
}
