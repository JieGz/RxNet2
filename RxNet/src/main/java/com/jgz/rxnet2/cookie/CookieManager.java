package com.jgz.rxnet2.cookie;

import com.jgz.rxnet2.cookie.store.ICookieStore;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/7
 * 描述:
 * 修订历史：
 * ================================================
 */
public class CookieManager implements CookieJar {

    private ICookieStore iCookieStore;

    public CookieManager(ICookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null!");
        }
        this.iCookieStore = cookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        iCookieStore.saveCookie(url, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return iCookieStore.loadCookie(url);
    }

    public ICookieStore getiCookieStore() {
        return iCookieStore;
    }
}
