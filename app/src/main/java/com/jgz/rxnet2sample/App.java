package com.jgz.rxnet2sample;

import android.app.Application;

import com.jgz.rxnet2.RxNet;

/**
 * Created by gdjie on 2017/8/9.
 */

public class App extends Application {
    // public final String BASE_URL = "http://dev.wangou.ns002.com/";
    public final String BASE_URL = "http://api.ns002.com/";

    @Override
    public void onCreate() {
        super.onCreate();
        RxNet.getDefault().init(BASE_URL);
    }
}
