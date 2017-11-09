package com.jgz.rxnet2.consumer;


import android.content.Context;
import android.widget.Toast;

import com.jgz.rxnet2.converter.ApiException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * 观察者抽象类
 * Created by Je on 2017/06/14.
 */
public abstract class RxNetObserver<T> implements Observer<T> {

    private Context mContext;

    public RxNetObserver(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        cancel(d);
    }

    @Override
    public void onNext(@NonNull T t) {
        onSuccess(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (e instanceof ApiException) {
            onErrored((ApiException) e);
        } else if (e instanceof UnknownHostException) {//没有网络
            Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT).show();
        } else if (e instanceof HttpException) {//404,地址不存在
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (e instanceof SocketTimeoutException) {//请求超时
            Toast.makeText(mContext, "请求超时", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onComplete() {

    }


    public abstract void onSuccess(T t);

    /**
     * 如果创建这个类的对象的时候想重写error方法,但是不想手动再一次使用Toast提示的吗,可以通过
     * super.error(e)的方法调用这个方法从而达到自动调出Toast
     *
     * @param e 自定义的解决服务器的类型,包含一个code和一个message
     */
    public void onErrored(ApiException e) {
        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void cancel(Disposable disposable) {

    }
}
