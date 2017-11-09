package com.jgz.rxnet2.consumer;

import android.content.Context;
import android.widget.Toast;

import com.jgz.rxnet2.converter.ApiException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2017/11/8
 * 描述:  统一处理请求服务器后处理的类
 * 修订历史：
 * ================================================
 */
public class RxNetErrorConsumer implements Consumer<Throwable> {
    private Context mContext;

    protected RxNetErrorConsumer(Context context) {
        this.mContext = context;
    }

    @Override
    public void accept(Throwable e) throws Exception {
        if (e instanceof ApiException) {
            error((ApiException) e);
        } else if (e instanceof UnknownHostException) {//没有网络
            Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT).show();
        } else if (e instanceof HttpException) {//404,地址不存在
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (e instanceof SocketTimeoutException) {//请求超时
            Toast.makeText(mContext, "请求超时", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 如果创建这个类的对象的时候想重写error方法,但是不想手动再一次使用Toast提示的吗,可以通过
     * super.error(e)的方法调用这个方法从而达到自动调出Toast
     *
     * @param e 自定义的解决服务器的类型,包含一个code和一个message
     */
    protected void error(ApiException e) {
        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
