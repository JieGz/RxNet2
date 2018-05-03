package com.jgz.rxnet2;

import android.content.Context;

import com.jgz.rxnet2.consumer.RxNetErrorConsumer;
import com.jgz.rxnet2.converter.RxNetGsonConverterFactory;
import com.jgz.rxnet2.cookie.CookieManager;
import com.jgz.rxnet2.cookie.store.MemoryCookieStore;
import com.jgz.rxnet2.http.HttpResult;
import com.jgz.rxnet2.interceptor.baseurl.RxNetOKHttpClient;
import com.jgz.rxnet2.interceptor.log.HttpLogginInterceptor;
import com.jgz.rxnet2.utils.RxNetUtil;

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

/**
 * ================================================
 * 作者:T(揭光智)     联系方式:wechat/phone:13570578417
 * 版本:
 * 创建日期:2018/5/3
 * 描述:
 * 修订历史：
 * ================================================
 */

public class RxNet {
    private String mBaseUrl;
    private static Context mContext;
    private OkHttpClient okHttpClient;
    private static volatile RxNet defaultInstance;

    private RxNet() {
        okHttpClient = RxNetOKHttpClient.getInstance().with(new OkHttpClient.Builder())
                //cookie管理策略
                .cookieJar(new CookieManager(new MemoryCookieStore()))
                //设置连接超时的时间
                .connectTimeout(10L, TimeUnit.SECONDS)
                //设置读取超时的时间
                .readTimeout(10L, TimeUnit.SECONDS)
                //设置失败重连
                .retryOnConnectionFailure(true)
                //添加interceptor,日志拦截器
                .addInterceptor(new HttpLogginInterceptor().setHttpLevel(HttpLogginInterceptor.Level.BODY))
                .build();

    }

    public static RxNet getDefault() {
        RxNet instance = defaultInstance;
        if (instance == null) {
            synchronized (RxNet.class) {
                instance = defaultInstance;
                if (instance == null) {
                    instance = defaultInstance = new RxNet();
                }
            }
        }
        return instance;
    }

    public RxNet init(Context context, String baseUrl) {
        mContext = context;
        this.mBaseUrl = baseUrl;
        return this;
    }

    /**用户自定义OkHttpClien,主要是自定义请求超时间,是否重连,拦截器等操作*/
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        RxNetUtil.checkNotNull(okHttpClient, "okHttpClient == null");
        this.okHttpClient = okHttpClient;
    }

    private static Context getContext() {
        return mContext;
    }


    public void dismiss() {
        defaultInstance = null;
        mContext = null;
    }

    /**
     * 获取一个网络请求公用的接口
     *
     * @param clazz 网络路径/头部参数配置接口所在的Class对象
     * @param <T>   网络路径/头部参数配置接口的类型
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

    public static  <T> void beginRequest(Observable<HttpResult<T>> observable, Observer<T> observer) {
        observable.map(HttpResult::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static <T> void beginRequest2(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static <T> Disposable beginRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.map(HttpResult::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }


    public static <T> Disposable beginRequest2(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public static <T> Disposable beginRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext) {
        return observable.map(HttpResult::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, new RxNetErrorConsumer(getContext()) {
                });
    }

    public static <T> Disposable beginRequest2(Observable<T> observable, Consumer<? super T> onNext) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, new RxNetErrorConsumer(getContext()) {
                });
    }
}
