package com.jgz.rxnet2sample;

import com.jgz.rxnet2.HttpResult;
import com.jgz.rxnet2.RxNet;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by gdjie on 2017/8/9.
 */

public interface Api {
    /**
     * 验证码登录
     */
    @POST("/app/timeout")
    Observable<HttpResult<loginData>> loginApp(@Body Object json);


    class Wrapper {
        public static Observable<HttpResult<loginData>> loginApp(@Body Object json) {
            return RxNet.getService(Api.class).loginApp(json);
        }
    }
}
