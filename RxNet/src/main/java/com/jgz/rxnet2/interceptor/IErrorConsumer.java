package com.jgz.rxnet2.interceptor;

import io.reactivex.functions.Consumer;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/8
 * 描述:
 * 修订历史：
 * ================================================
 */
public interface IErrorConsumer<T> extends Consumer<Throwable> {

    void get(T t) throws Exception;
}
