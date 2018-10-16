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
 * 描述: 使用RxJava2+Retrofit+OKHttp3封装的网络请求框架
 * 修订历史：
 * 1: 提供了标准返回格式的方法,和自定义返回格式的方法 2018/10/16
 * 2: 可取消的网络请求  2018/10/16
 * 3: 能自定义超时时间,失败重连和拦截器 2018/10/16
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

    /**
     * 初始化网络框架
     *
     * @param context 上下文
     * @param baseUrl baseUrl
     * @return RxNet 实例
     */
    public RxNet init(Context context, String baseUrl) {
        mContext = context;
        this.mBaseUrl = baseUrl;
        return this;
    }

    /**
     * 用户自定义OkHttpClien,主要是自定义请求超时间,是否重连,拦截器等操作
     */
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


    /**********************************************************************************************
     * *******************************固定格式的网络请求结果,Demo如下*********************************
     * ********************************************************************************************
     *                    {
     *                       "code": 200,
     *                       "data": {
     *                           "deprecated": false,
     *                           "ipIsCn": false,
     *                           "publicIp": "113.65.32.138",
     *                           "safeguard": false
     *                       },
     *                       "desc": "ok",
     *                       "msg": "请求成功",
     *                       "path": "/api/v1/app/init"
     *                    }
     **********************************************************************************************
     **********************************************************************************************
     **********************************************************************************************/

    /**
     * 不可取消的网络请求,拿到的是data里面的值,网络产生的各种情况由利用者自行处理
     *
     * @param observable 某个网络请求(观察者)
     * @param observer   观察者
     * @param <T>        实体类型
     */
    public static <T> void beginRequest(Observable<HttpResult<T>> observable, Observer<? super T> observer) {
        doRequest(observable, observer);
    }

    /**
     * 不可取消的网络请求,拿到的是整个result,网络产生的各种情况由利用者自行处理
     *
     * @param observable 某个网络请求(观察者)
     * @param observer   观察者
     * @param <T>        实体类型
     */
    public static <T> void beginRequestGetResult(Observable<HttpResult<T>> observable, Observer<? super HttpResult<T>> observer) {
        doRequestGetResult(observable, observer);
    }

    /**
     * 可取消的网络请求,拿到的是data里面的值,请求异常情况交给网络框架处理
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param <T>        实体类型
     * @return 用于取消某次请求，或者判断某次请求是否已经被取消了
     */
    public static <T> Disposable beginCancellableRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext) {
        return doRequest(observable, onNext);
    }


    /**
     * 可取消的网络请求,拿到的是整个result,请求异常情况交给网络框架处理
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param <T>        实体类型
     * @return 用于取消某次请求，或者判断某次请求是否已经被取消了
     */
    public static <T> Disposable beginCancellableRequestGetResult(Observable<HttpResult<T>> observable, Consumer<? super HttpResult<T>> onNext) {
        return doRequestGetResult(observable, onNext);
    }


    /**
     * 不可取消的网络请求,拿到的是data里面的值,请求异常情况交给网络框架处理
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param <T>        实体类型
     */
    public static <T> void beginRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext) {
        doRequest(observable, onNext);
    }

    /**
     * 不可取消的网络请求,拿到的是整个result,请求异常情况交给网络框架处理
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param <T>        实体类型
     */
    public static <T> void beginRequestGetResult(Observable<HttpResult<T>> observable, Consumer<? super HttpResult<T>> onNext) {
        doRequestGetResult(observable, onNext);
    }

    /**
     * 可取消的网络请求,拿到的是data里面的值,自己处理请求异常情况
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param onError    自己处理异常情况
     * @param <T>        实体类型
     * @return 用于取消某次请求，或者判断某次请求是否已经被取消了
     */
    public static <T> Disposable beginCancellableRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return doRequest(observable, onNext, onError);
    }

    /**
     * 可取消的网络请求,拿到的是整个result,自己处理请求异常情况
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param onError    自己处理异常情况
     * @param <T>        实体类型
     * @return 用于取消某次请求，或者判断某次请求是否已经被取消了
     */
    public static <T> Disposable beginCancellableRequestGetResult(Observable<HttpResult<T>> observable, Consumer<? super HttpResult<T>> onNext, Consumer<? super Throwable> onError) {
        return doRequestGetResult(observable, onNext, onError);
    }

    /**
     * 可取消的网络请求,拿到的是data里面的值,自己处理请求异常情况
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param onError    自己处理异常情况
     * @param <T>        实体类型
     * @return 用于取消某次请求, 或者判断某次请求是否已经被取消了
     */
    public static <T> void beginRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        doRequest(observable, onNext, onError);
    }

    /**
     * 可取消的网络请求,拿到的是整个result,自己处理请求异常情况
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param onError    自己处理异常情况
     * @param <T>        实体类型
     * @return 用于取消某次请求, 或者判断某次请求是否已经被取消了
     */
    public static <T> void beginRequestGetResult(Observable<HttpResult<T>> observable, Consumer<? super HttpResult<T>> onNext, Consumer<? super Throwable> onError) {
        doRequestGetResult(observable, onNext, onError);
    }

    /**
     * 通过RxJava,让请求在IO线程中执行,并把结果返回到UI线程中,结果是data里面的值,请求异常情况交给网络框架处理
     * </p>
     */
    private static <T> void doRequest(Observable<HttpResult<T>> observable, Observer<? super T> observer) {
        observable.map(HttpResult::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 通过RxJava,让请求在IO线程中执行,并把结果返回到UI线程中,结果是data里面的值,请求异常情况交给网络框架处理
     * </p>
     */
    private static <T> Disposable doRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext) {
        return observable.map(HttpResult::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, new RxNetErrorConsumer(getContext()) {
                });
    }

    /**
     * 通过RxJava,让请求在IO线程中执行,并把结果返回到UI线程中,结果是data里面的值,自己处理请求异常情况
     * </p>
     */
    private static <T> Disposable doRequest(Observable<HttpResult<T>> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.map(HttpResult::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    /**
     * 通过RxJava,让请求在IO线程中执行,并把结果返回到UI线程中,结果整个result,请求异常情况交给网络框架处理
     * </p>
     */
    private static <T> void doRequestGetResult(Observable<HttpResult<T>> observable, Observer<? super HttpResult<T>> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * 通过RxJava,让请求在IO线程中执行,并把结果返回到UI线程中,结果整个result,请求异常情况交给网络框架处理
     * </p>
     */
    private static <T> Disposable doRequestGetResult(Observable<HttpResult<T>> observable, Consumer<? super HttpResult<T>> onNext) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, new RxNetErrorConsumer(getContext()) {
                });
    }

    /**
     * 通过RxJava,让请求在IO线程中执行,并把结果返回到UI线程中,结果整个result,自己处理请求异常情况
     * </p>
     */
    private static <T> Disposable doRequestGetResult(Observable<HttpResult<T>> observable, Consumer<? super HttpResult<T>> onNext, Consumer<? super Throwable> onError) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }


    /**********************************************************************************************
     *  ***************************完全自定义的网络返回数据类型的*************************************
     **********************************************************************************************/


    /**
     * 不可取消的网络请求,拿到的是整个result,网络产生的各种情况由利用者自行处理
     *
     * @param observable 某个网络请求(观察者)
     * @param observer   观察者
     * @param <T>        实体类型
     */
    public static <T> void beginFreeRequest(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 可取消的网络请求,拿到的是整个result,请求异常情况交给网络框架处理
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param <T>        实体类型
     * @return 用于取消某次请求, 或者判断某次请求是否已经被取消了
     */
    public static <T> Disposable beginCancellableFreeRequest(Observable<T> observable, Consumer<? super T> onNext) {
        return doFreeRequest(observable, onNext);
    }

    /**
     * 不可取消的网络请求,拿到的是整个result,请求异常情况交给网络框架处理
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param <T>        实体类型
     */
    public static <T> void beginFreeRequest(Observable<T> observable, Consumer<? super T> onNext) {
        doFreeRequest(observable, onNext);
    }

    /**
     * 可取消的网络请求,拿到的是整个result,自己处理请求异常情况
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param onError    自己处理异常情况
     * @param <T>        实体类型
     * @return 用于取消某次请求, 或者判断某次请求是否已经被取消了
     */
    public static <T> Disposable beginCancellableFreeRequest(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return doFreeRequest(observable, onNext, onError);
    }

    /**
     * 不可取消的网络请求,拿到的是整个result,自己处理请求异常情况
     *
     * @param observable 某个网络请求(观察者)
     * @param onNext     观察者成功的回调
     * @param onError    自己处理异常情况
     * @param <T>        实体类型
     */
    public static <T> void beginFreeRequest(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        doFreeRequest(observable, onNext, onError);
    }

    /**
     * 通过RxJava,让请求在IO线程中执行,并把结果返回到UI线程中,结果整个result,请求异常情况交给网络框架处理
     * </p>
     */
    private static <T> Disposable doFreeRequest(Observable<T> observable, Consumer<? super T> onNext) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, new RxNetErrorConsumer(getContext()) {
                });
    }

    /**
     * 通过RxJava,让请求在IO线程中执行,并把结果返回到UI线程中,结果整个result,自己处理请求异常情况
     * </p>
     */
    private static <T> Disposable doFreeRequest(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }
}
