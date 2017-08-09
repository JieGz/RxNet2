package com.jgz.rxnet2;


import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * 观察者抽象类
 * Created by Je on 2017/06/14.
 */
public abstract class NetObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        cancel(d);
    }

    @Override
    public void onNext(@NonNull T t) {
        if (t == null) {
            onErrored(new Error("onNext receive a NullPointerException"));
        } else {
            onSuccess(t);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onErrored(e);
    }

    @Override
    public void onComplete() {

    }


    public abstract void onSuccess(T t);

    public abstract void onErrored(Throwable e);

    public abstract void cancel(Disposable disposable);
}
