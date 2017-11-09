package com.jgz.rxnet2.converter;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/8
 * 描述:
 * 修订历史：
 * ================================================
 */
public class RxNetGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    RxNetGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(@NonNull ResponseBody responseBody) throws IOException {
        String response = responseBody.string(); //把responsebody转为string
        // 这里只是为了检测code是否==1,所以只解析HttpStatus中的字段,因为只要code和message就可以了
        HttpStatus httpStatus = gson.fromJson(response, HttpStatus.class);
        if (httpStatus.isUnSucceed()) {
            responseBody.close();
            //抛出一个RuntimeException, 这里抛出的异常会到Subscriber的onError()方法中统一处理
            throw new ApiException(httpStatus.getCode(), httpStatus.getMsg());
        }

        MediaType contentType = responseBody.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);
        T read;
        try {
            read = adapter.read(jsonReader);
        } finally {
            responseBody.close();
        }
        return read;
    }
}
