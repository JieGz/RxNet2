package com.jgz.rxnet2sample.net.api;

import com.jgz.rxnet2.http.HttpResult;
import com.jgz.rxnet2sample.net.bean.loginData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.jgz.rxnet2.interceptor.baseurl.RxNetOKHttpClient.MUL_BASE_URL_NAME_HEADER;

/**
 * Created by gdjie on 2017/8/9.
 */

public interface IConfig {
    /**
     * 验证码登录
     */
    @POST("/app/user/v2/login")
    Observable<HttpResult<loginData>> loginApp(@Body Object json);

    @Headers({MUL_BASE_URL_NAME_HEADER + "test"})
    @POST("/app/timeout")
    Observable<HttpResult> timeout(@Body Object json);
}
