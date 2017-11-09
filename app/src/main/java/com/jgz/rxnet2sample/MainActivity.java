package com.jgz.rxnet2sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jgz.rxnet2.RxNet;

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
        mTextBt = (Button) findViewById(R.id.bt_text);
        mResultBt = (Button) findViewById(R.id.bt_result);
        mTextTv = (TextView) findViewById(R.id.tv_text);
        mResultTv = (TextView) findViewById(R.id.tv_result);


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
        Consumer<loginData> onNext = data -> {
            mTextTv.setText(data.getPhone());
            mResultTv.setText(data.getNickname());
        };

        String[] keys = new String[]{"phone", "password"};
        Object[] vs = new Object[]{"13570578417", "1234567"};
        RxNet.beginRequest(Api.loginApp(keys, vs), onNext);


    }

    private void testTimeOut() {
        Consumer onNext = result -> {
            mTextTv.setText(result.toString());
            mResultTv.setText("onSuccess");
        };
        RxNet.beginRequest2(Api.timeout(), onNext);
    }


}
