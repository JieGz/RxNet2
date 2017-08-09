package com.jgz.rxnet2;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Je on 2017/8/9.
 */

public class CookiesInterceptor implements Interceptor {
    private HashSet<String> mCookies = new HashSet<>();

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

        Request.Builder builder = chain.request().newBuilder();

        for (String cookie : mCookies) {
            builder.addHeader("Cookie", cookie);
        }

        return chain.proceed(builder.build());
    }
}
