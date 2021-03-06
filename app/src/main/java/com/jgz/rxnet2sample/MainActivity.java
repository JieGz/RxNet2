package com.jgz.rxnet2sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jgz.rxnet2.RxNet;
import com.jgz.rxnet2.http.HttpResult;
import com.jgz.rxnet2sample.net.api.Api;
import com.jgz.rxnet2sample.net.bean.InitData;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private Button mTextBt;
    private Button mResultBt;
    private TextView mTextTv;
    private TextView mResultTv;
    private Disposable disposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mTextBt = findViewById(R.id.bt_text);
        mResultBt = findViewById(R.id.bt_result);
        mTextTv = findViewById(R.id.tv_text);
        mResultTv = findViewById(R.id.tv_result);


        mTextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginApp();
            }
        });

        mResultBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testTimeOut();
            }
        });


    }


    private void loginApp() {

        Consumer<InitData> onNext = data -> {
            Log.d("<Je>",data.getNewVersionUrl());
        };

        RxNet.beginRequest(Api.getInitData(), onNext);
    }


    private void testTimeOut() {
        Consumer onNext = result -> {
            mTextTv.setText(result.toString());
            mResultTv.setText("onSuccess");
        };
        RxNet.beginFreeRequest(Api.timeout(), onNext);
    }


}
