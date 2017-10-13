package com.jgz.rxnet2sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jgz.rxnet2.HttpResult;
import com.jgz.rxnet2.NetObserver;
import com.jgz.rxnet2.RxNet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

        Observable<String> observable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String ipAddress = GetInetAddress("api.ns002.com");
            emitter.onNext(ipAddress);
        }).subscribeOn(Schedulers.io());
        Consumer<String> onNext = ipAddress -> Log.d("<Je>", "onNext: " + ipAddress);
        observable.subscribe(onNext);
    }


    public String GetInetAddress(String host) {
        String IPAddress = "";
        InetAddress ReturnStr1;
        try {
            ReturnStr1 = InetAddress.getByName(host);
            IPAddress = ReturnStr1.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return IPAddress;
        }
        return IPAddress;
    }

    private Disposable disposable;

    private void loginApp() {

        RxNet.beginRequest(Api.Wrapper.loginApp(ServerUtil.sign(this, null, null)), new NetObserver<HttpResult<loginData>>() {

            @Override
            public void onSuccess(HttpResult<loginData> result) {
                mTextTv.setText(result.toString());
                mResultTv.setText("onSuccess");
            }

            @Override
            public void onErrored(Throwable e) {
                mTextTv.setText(e.getMessage());
                mResultTv.setText("onErrored");
            }

            @Override
            public void cancel(Disposable disposable) {
                Toast.makeText(MainActivity.this, "cancel()调用了", Toast.LENGTH_SHORT).show();

                if (MainActivity.this.disposable != null) {
                    MainActivity.this.disposable.dispose();
                    Toast.makeText(MainActivity.this, "取消了", Toast.LENGTH_SHORT).show();
                }
                MainActivity.this.disposable = disposable;
            }
        });
    }

    private void loginApp2() {

        if (disposable != null) {
            disposable.dispose();
        }

        Consumer<HttpResult<loginData>> onNext = new Consumer<HttpResult<loginData>>() {
            @Override
            public void accept(HttpResult<loginData> result) throws Exception {
                mTextTv.setText(result.toString());
                mResultTv.setText("onSuccess");
            }
        };

        Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                mTextTv.setText(e.getMessage());
                mResultTv.setText("onErrored");
            }
        };

        disposable = RxNet.beginRequest(Api.Wrapper.loginApp(ServerUtil.sign(this, null, null)), onNext, onError);
    }


}
