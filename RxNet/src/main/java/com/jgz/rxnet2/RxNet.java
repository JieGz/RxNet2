package com.jgz.rxnet2;

import com.jgz.rxnet2.cookie.CookieManager;
import com.jgz.rxnet2.cookie.store.MemoryCookieStore;
import com.jgz.rxnet2.interceptor.HttpLogginInterceptor;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Je on 2017/8/8.
 */

public class RxNet {
    private static String mBaseUrl;
    private static volatile RxNet mRxNet;

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

    public void init(String baseUrl) {
        mBaseUrl = baseUrl;
    }


    /**
     * 这是一个OkHttpClient
     */
    private static OkHttpClient client = new OkHttpClient.Builder()
            //添加interceptor,日志拦截器
            .addInterceptor(new HttpLogginInterceptor("光智").setHttpLevel(HttpLogginInterceptor.Level.BODY))
            //cookie管理策略
            .cookieJar(new CookieManager(new MemoryCookieStore()))
            //设置连接超时的时间
            .connectTimeout(10L, TimeUnit.SECONDS)
            //设置读取超时的时间
            .readTimeout(10L, TimeUnit.SECONDS)
            //设置失败重连
            .retryOnConnectionFailure(true)
            .build();

    /**
     * 获取一个网络请求公用的接口
     *
     * @param clazz
     * @param <T>
     * @return 公用接口对应的class
     */
    public static <T> T getService(Class<T> clazz) {
        return createService(clazz);
    }


    private static <T> T createService(Class<T> clazz) {
        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(clazz);
    }


    public static <T> void beginRequest(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    public static <T> Disposable beginRequest(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public static <T> Observable<T> beginRequest(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
