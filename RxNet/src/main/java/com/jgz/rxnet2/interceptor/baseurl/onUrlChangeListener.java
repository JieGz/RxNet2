package com.jgz.rxnet2.interceptor.baseurl;

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
public interface onUrlChangeListener {
    /**
     * 此方法在框架使用 {@code domainName} 作为 key,从 {@link RxNetOKHttpClient#mDomainNameHub}
     * 中取出对应的 BaseUrl 构建新的 Url 之前会被调用
     * <p>
     * 可以使用此回调确保 {@link RxNetOKHttpClient#mDomainNameHub} 中是否已经存在自己期望的 BaseUrl
     * 如果不存在可以在此方法中进行 {@code put}
     *
     * @param oldUrl
     * @param domainName
     */
    void onUrlChangeBefore(HttpUrl oldUrl, String domainName);

    /**
     * 当 Url 的 BaseUrl 被改变时回调
     * 调用时间是在接口请求服务器之前
     *
     * @param newUrl
     * @param oldUrl
     */
    void onUrlChanged(HttpUrl newUrl, HttpUrl oldUrl);
}
