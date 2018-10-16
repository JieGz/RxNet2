package com.jgz.rxnet2sample.net.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jgz.rxnet2.cookie.CookieManager;
import com.jgz.rxnet2.cookie.store.MemoryCookieStore;
import com.jgz.rxnet2.interceptor.baseurl.RxNetOKHttpClient;
import com.jgz.rxnet2.interceptor.log.HttpLogginInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 作者:T(揭光智)     联系方式:wechat/phone:13570578417
 * 版本:
 * 创建日期:2018/5/3
 * 描述:
 * 修订历史：
 * ================================================
 */
public class RxNetOkHttpClientFactory {

    public static OkHttpClient create() {
        return RxNetOKHttpClient.getInstance().with(new OkHttpClient.Builder())
                //cookie管理策略
                .cookieJar(new CookieManager(new MemoryCookieStore()))
                //设置连接超时的时间
                .connectTimeout(10L, TimeUnit.SECONDS)
                //设置读取超时的时间
                .readTimeout(10L, TimeUnit.SECONDS)
                //设置失败重连
                .retryOnConnectionFailure(true)
                //增加两个请求头
                .addInterceptor(new AddTokenInterceptor())
                //添加interceptor,日志拦截器
                .addInterceptor(new HttpLogginInterceptor().setHttpLevel(HttpLogginInterceptor.Level.BODY))
                .build();
    }


    /**
     * 将Cookies传递给服务器
     */
    static class AddTokenInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("Accept", "text/plain");

            if (!TextUtils.isEmpty(getToken())) builder.addHeader("Authorization", getToken());

            Map<String, Object> map = new HashMap<>();
            map.put("os", "android");
            map.put("sysVersion", Build.VERSION.RELEASE);//系统版本
            map.put("phoneBrand", Build.BRAND);//手机品牌
            map.put("phoneModel", Build.MODEL);//手机型号
            builder.addHeader("request-flag", new Gson().toJson(map));

            return chain.proceed(builder.build());
        }

        /**
         * 在这里判断好Token的方式,然后设置请求里面
         */
        private String getToken() {
            String token;
            token = "Bearer " + "token";
            if (!TextUtils.isEmpty(token)) {
                return token;
            }
            return null;
        }
    }


}
