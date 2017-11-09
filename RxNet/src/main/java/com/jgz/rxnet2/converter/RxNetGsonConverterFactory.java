package com.jgz.rxnet2.converter;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/8
 * 描述: RxNetGsonConverterFactory
 * 修订历史：
 * ================================================
 */
public class RxNetGsonConverterFactory extends Converter.Factory {

    private final Gson gson;

    public static RxNetGsonConverterFactory create() {
        return create(new Gson());
    }

    public static RxNetGsonConverterFactory create(Gson gson) {
        if(gson == null) {
            throw new NullPointerException("gson == null");
        } else {
            return new RxNetGsonConverterFactory(gson);
        }
    }

    private RxNetGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter adapter = this.gson.getAdapter(TypeToken.get(type));
        return new RxNetGsonResponseBodyConverter(this.gson, adapter);
    }

    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter adapter = this.gson.getAdapter(TypeToken.get(type));
        return new RxNetGsonRequestBodyConverter(this.gson, adapter);
    }
}
