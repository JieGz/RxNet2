package com.jgz.rxnet2.stomp;

import io.reactivex.Flowable;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2018/3/4 0004
 * 描述:
 * 修订历史：
 * ================================================
 */
public interface ConnectionProvider {

    /**
     * Subscribe this for receive stomp messages
     */
    Flowable<String> messages();

    /**
     * Sending stomp messages via you ConnectionProvider.
     * onError if not connected or error detected will be called, or onCompleted id sending started
     * TODO: send messages with ACK
     */
    Flowable<Void> send(String stompMessage);

    /**
     * Subscribe this for receive #LifecycleEvent events
     */
    Flowable<LifecycleEvent> getLifecycleReceiver();
}
