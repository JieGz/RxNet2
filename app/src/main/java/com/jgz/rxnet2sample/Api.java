package com.jgz.rxnet2sample;

import com.jgz.rxnet2.HttpResult;
import com.jgz.rxnet2.RxNet;

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
    public static Observable<HttpResult<loginData>> loginApp(String[] keys, Object[] vs) {
        return RxNet.getService(IConfig.class).loginApp(ServerUtil.sign(keys, vs));
    }
}
