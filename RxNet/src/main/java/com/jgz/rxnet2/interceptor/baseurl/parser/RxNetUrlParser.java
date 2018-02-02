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
public class RxNetUrlParser implements IUrlParser {
    /**
     * 只支持http或https
     * 如果(HttpUrl#889号)HttpUrl.parser()解析为null,说明格式不正确,(正确的格式:"https://github.com:443)"
     * http 默认端口 80,https 默认端口 443 ,如果端口号是默认端口号就可以将 ":443" 去掉
     */
    @Override
    public HttpUrl parseUrl(HttpUrl headConfigUrl, HttpUrl url) {

        if (null == headConfigUrl) return url;
        return url.newBuilder()
                .scheme(headConfigUrl.scheme())     //协议
                .host(headConfigUrl.host())         //地址
                .port(headConfigUrl.port())         //端口
                .build();
    }
}
