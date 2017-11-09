package com.jgz.rxnet2sample;

import com.jgz.rxnet2.RxNet;
import com.jgz.rxnet2.http.HttpResult;

import io.reactivex.Observable;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/7
 * 描述:
 * 修订历史：
 * ================================================
 */
public class Api {

    private static IConfig iConfig = RxNet.getDefault().getService(IConfig.class);

    public static Observable<HttpResult<loginData>> loginApp(String[] keys, Object[] vs) {
        return iConfig.loginApp(ServerUtil.sign(keys, vs));
    }

    public static Observable<HttpResult> timeout() {
        return iConfig.timeout(ServerUtil.sign());
    }
}
