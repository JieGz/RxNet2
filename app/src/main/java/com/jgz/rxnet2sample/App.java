package com.jgz.rxnet2sample;

import android.app.Application;

import com.jgz.rxnet2.RxNet;
import com.jgz.rxnet2.interceptor.baseurl.RxNetOKHttpClient;
import com.jgz.rxnet2sample.net.utils.RxNetOkHttpClientFactory;

/**
 * Created by gdjie on 2017/8/9.
 */

public class App extends Application {
    // public final String BASE_URL = "http://dev.wangou.ns002.com/";
    public final String BASE_URL = "https://api.ygo118.com/";

    static {
        RxNetOKHttpClient.getInstance().putBaseUrl("test", "http://test.ns002.com/");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        RxNet.getDefault().init(this, BASE_URL).setOkHttpClient(RxNetOkHttpClientFactory.create());
    }

}
