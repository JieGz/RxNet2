package com.jgz.rxnet2;

import android.support.annotation.NonNull;
import android.util.Log;

import com.jgz.rxnet2.interceptor.HttpLogginInterceptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Je on 2017/8/8.
 */

public class RxNet {
    private static String mBaseUrl;
    private static volatile RxNet mRxNet;

    /**
     * 这个是日志拦截器
     */
    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(@NonNull String message) {
            Log.i("RxNet", message);
        }
    });


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

    public void init(String baseUrl, boolean isLog) {
        mBaseUrl = baseUrl;

        if (isLog) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
    }


    /**
     * 这是一个OkHttpClient
     */
    private static OkHttpClient client = new OkHttpClient.Builder()
            //添加interceptor,日志拦截器
            .addInterceptor(new HttpLogginInterceptor("光智").setHttpLevel(HttpLogginInterceptor.Level.BODY))
            //.addInterceptor(new CookiesInterceptor())
            .addInterceptor(new ReceivedCookiesInterceptor())
            .addInterceptor(new AddCookiesInterceptor())
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


    private static HashSet<String> mCookies = new HashSet<>();

    /**
     * 将服务器Cookies保存起来
     */
    private static class ReceivedCookiesInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {

            Response originalResponse = chain.proceed(chain.request());

            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                HashSet<String> cookies = new HashSet<>();

                for (String header : originalResponse.headers("Set-Cookie")) {
                    cookies.add(header);
                }

                mCookies = cookies;
            }

            return originalResponse;
        }
    }

    /**
     * 将Cookies传递给服务器
     */
    private static class AddCookiesInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();

            HashSet<String> preferences = mCookies;

            for (String cookie : preferences) {
                builder.addHeader("Cookie", cookie);
                Log.v("OkHttp", "Adding Header: " + cookie);
            }

            return chain.proceed(builder.build());
        }
    }


}
