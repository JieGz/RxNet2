package com.jgz.rxnet2sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jgz.rxnet2.HttpResult;
import com.jgz.rxnet2.RxNet;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private Button mTextBt;
    private Button mResultBt;
    private TextView mTextTv;
    private TextView mResultTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                //disposable.dispose();
            }
        });


    }


    private Disposable disposable;

    private void loginApp() {


        Consumer<HttpResult<loginData>> onNext = result -> {
            mTextTv.setText(result.toString());
            mResultTv.setText("onSuccess");
        };

        Consumer<Throwable> onError = e -> {
            mTextTv.setText(e.getMessage());
            mResultTv.setText("onErrored");
        };

        String[] keys = new String[]{"phone", "password"};
        Object[] vs = new Object[]{"13570578417", "123456"};
        RxNet.beginRequest(Api.loginApp(keys, vs)).subscribe(onNext, onError);
    }


}
