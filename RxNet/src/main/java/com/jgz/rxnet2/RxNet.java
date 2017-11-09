package com.jgz.rxnet2;

import android.app.Application;
import android.content.Context;

import com.jgz.rxnet2.consumer.RxNetErrorConsumer;
import com.jgz.rxnet2.converter.RxNetGsonConverterFactory;
import com.jgz.rxnet2.cookie.CookieManager;
import com.jgz.rxnet2.cookie.store.MemoryCookieStore;
import com.jgz.rxnet2.http.HttpResult;
import com.jgz.rxnet2.interceptor.HttpLogginInterceptor;
import com.jgz.rxnet2.utils.RxNetUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Je on 2017/8/8.
 */

public class RxNet {
    private String mBaseUrl;
    private static Application mContext;
    private OkHttpClient okHttpClient;
    private static volatile RxNet mRxNet;

    private RxNet() {
        okHttpClient = new OkHttpClient.Builder()
                //添加interceptor,日志拦截器
                .addInterceptor(new HttpLogginInterceptor().setHttpLevel(HttpLogginInterceptor.Level.BODY))
                //cookie管理策略
                .cookieJar(new CookieManager(new MemoryCookieStore()))
                //设置连接超时的时间
                .connectTimeout(10L, TimeUnit.SECONDS)
                //设置读取超时的时间
                .readTimeout(10L, TimeUnit.SECONDS)
                //设置失败重连
                .retryOnConnectionFailure(true)
                .build();
    }

    public static RxNet getDefault() {
        if (mRxNet == null) {
            synchronized (RxNet.class) {
                if (mRxNet == null) {
                    mRxNet = new RxNet();
                }
            }
        }
        return mRxNet;
    }

    public RxNet init(Application context, String baseUrl) {
        this.mContext = context;
        mBaseUrl = baseUrl;
        return this;
    }

    /**
     * 必须设置
     */
    public RxNet setOkHttpClient(OkHttpClient okHttpClient) {
        RxNetUtil.checkNotNull(okHttpClient, "okHttpClient == null");
        this.okHttpClient = okHttpClient;
        return this;
    }

    public static Context getContext() {
        return mContext;
    }


    /**
     * 获取一个网络请求公用的接口
     *
     * @param clazz
     * @param <T>
     * @return 公用接口对应的class
     */

    public <T> T getService(Class<T> clazz) {
        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(RxNetGsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(clazz);
    }


    public static <T> void beginRequest2(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static <T> void beginRequest(Observable<HttpResult<T>> observable, Observer<T> observer) {
        observable.map(new Function<HttpResult<T>, T>() {
            @Override
            public T apply(@NonNull HttpResult<T> result) throws Exception {
                return result.getData();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }


    public static <T> Disposable beginRequest2(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public static <T> Disposable beginRequest2(Observable<T> observable, Consumer<? super T> onNext) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, new RxNetErrorConsumer(getContext()) {
                });
    }

    public static <T> Disposable beginRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.map(new Function<HttpResult<T>, T>() {
            @Override
            public T apply(@NonNull HttpResult<T> result) throws Exception {
                return result.getData();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError);
    }

    public static <T> Disposable beginRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext) {
        return observable.map(new Function<HttpResult<T>, T>() {
            @Override
            public T apply(@NonNull HttpResult<T> result) throws Exception {
                return result.getData();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, new RxNetErrorConsumer(getContext()) {
        });
    }
}
