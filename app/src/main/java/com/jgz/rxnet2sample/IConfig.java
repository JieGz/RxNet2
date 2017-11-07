package com.jgz.rxnet2sample;

import com.jgz.rxnet2.HttpResult;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by gdjie on 2017/8/9.
 */

public interface IConfig {
    /**
     * 验证码登录
     */
    @POST("/app/user/v2/login")
    Observable<HttpResult<loginData>> loginApp(@Body Object json);
}
