package com.jgz.rxnet2sample.net.api;

import com.jgz.rxnet2.RxNet;
import com.jgz.rxnet2.http.HttpResult;
import com.jgz.rxnet2sample.net.bean.InitData;
import com.jgz.rxnet2sample.net.bean.loginData;
import com.jgz.rxnet2sample.net.utils.ServerUtil;

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


    public static Observable<HttpResult> timeout() {
        return iConfig.timeout(ServerUtil.sign());
    }

    public static Observable<HttpResult<InitData>> getInitData() {
        return iConfig.getInitData();
    }
}
