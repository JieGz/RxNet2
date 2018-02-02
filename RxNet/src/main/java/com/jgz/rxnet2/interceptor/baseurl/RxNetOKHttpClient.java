package com.jgz.rxnet2.interceptor.baseurl;

import android.text.TextUtils;
import android.util.Log;

import com.jgz.rxnet2.interceptor.baseurl.parser.IUrlParser;
import com.jgz.rxnet2.interceptor.baseurl.parser.RxNetUrlParser;
import com.jgz.rxnet2.utils.RxNetUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 作者:Je(揭光智)     联系方式:QQ:364049613
 * 版本:
 * 创建日期:2018/2/2
 * 描述:
 * 修订历史：
 * ================================================
 */
public class RxNetOKHttpClient {
    private static final String TAG = "RxNetOKHttpClient";
    private static final boolean DEPENDENCY_OKHTTP;
    private static final String MUL_BASE_URL_CONFIG_NAME = "BASE_URL_CONFIG";
    private static final String GLOBAL_BASE_URL = "https://github.com/";
    public static final String MUL_BASE_URL_NAME_HEADER = MUL_BASE_URL_CONFIG_NAME + ": ";

    private boolean isRun = true; //默认开始运行,可以随时停止运行,比如你在 App 启动后已经不需要在动态切换 baseurl 了
    private final Map<String, HttpUrl> mBaseUrlMap = new HashMap<>();
    private final Interceptor mInterceptor;
    private final List<onUrlChangeListener> mListeners = new ArrayList<>();
    private IUrlParser mUrlParser;

    static {
        boolean hasDependency;
        try {
            Class.forName("okhttp3.OkHttpClient");
            hasDependency = true;
        } catch (ClassNotFoundException e) {
            hasDependency = false;
        }
        DEPENDENCY_OKHTTP = hasDependency;
    }


    /**
     * 管理器是否在运行
     *
     * @return
     */
    public boolean isRun() {
        return this.isRun;
    }

    /**
     * 控制管理器是否运行,在每个域名地址都已经确定,不需要再动态更改时可设置为 false
     *
     * @param run
     */
    public void setRun(boolean run) {
        this.isRun = run;
    }

    private RxNetOKHttpClient() {
        if (!DEPENDENCY_OKHTTP) { //使用本管理器必须依赖 Okhttp
            throw new IllegalStateException("Must be dependency Okhttp");
        }
        setUrlParser(new RxNetUrlParser());
        this.mInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (!isRun()) // 可以在 App 运行时,随时通过 setRun(false) 来结束本管理器的运行,
                    return chain.proceed(chain.request());
                return chain.proceed(processRequest(chain.request()));
            }
        };
    }


    /**
     * 可自行实现 {@link IUrlParser} 动态切换 Url 解析策略
     *
     * @param parser
     */
    public void setUrlParser(IUrlParser parser) {
        this.mUrlParser = parser;
    }

    private static class RetrofitUrlManagerHolder {
        private static final RxNetOKHttpClient INSTANCE = new RxNetOKHttpClient();
    }

    public static final RxNetOKHttpClient getInstance() {
        return RetrofitUrlManagerHolder.INSTANCE;
    }

    /**
     * 将 {@link okhttp3.OkHttpClient.Builder} 传入,配置一些本管理器需要的参数
     *
     * @param builder
     * @return
     */
    public OkHttpClient.Builder with(OkHttpClient.Builder builder) {
        return builder.addInterceptor(mInterceptor);
    }


    /**
     * 对 {@link Request} 进行一些必要的加工
     *
     * @param request
     * @return
     */
    public Request processRequest(Request request) {

        Request.Builder newBuilder = request.newBuilder();

        String baseUrlName = getBaseUrlFromHeaders(request);

        HttpUrl httpUrl;

        Object[] listeners = listenersToArray();

        // 如果有 header,获取 header 中 baseUrlName 所映射的 url,若没有,则检查全局的 BaseUrl,未找到则为null
        if (!TextUtils.isEmpty(baseUrlName)) {
            notifyListener(request, baseUrlName, listeners);
            httpUrl = getBaseUrlByName(baseUrlName);
            newBuilder.removeHeader(MUL_BASE_URL_CONFIG_NAME);
        } else {
            notifyListener(request, GLOBAL_BASE_URL, listeners);
            httpUrl = getBaseUrlByName(GLOBAL_BASE_URL);
        }

        if (null != httpUrl) {
            HttpUrl newUrl = mUrlParser.parseUrl(httpUrl, request.url());
            Log.d(RxNetOKHttpClient.TAG, "New Url is { " + newUrl.toString() + " } , Old Url is { " + request.url().toString() + " }");

            if (listeners != null) {
                for (int i = 0; i < listeners.length; i++) {
                    ((onUrlChangeListener) listeners[i]).onUrlChanged(newUrl, request.url()); // 通知监听器此 Url 的 BaseUrl 已被改变
                }
            }

            return newBuilder
                    .url(newUrl)
                    .build();
        }

        return newBuilder.build();

    }

    private void notifyListener(Request request, String baseUrlName, Object[] listeners) {
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ((onUrlChangeListener) listeners[i]).onUrlChangeBefore(request.url(), baseUrlName);
            }
        }
    }


    /**
     * 全局动态替换 BaseUrl，优先级： Header中配置的url > 全局配置的url
     * 除了作为备用的 BaseUrl ,当你项目中只有一个 BaseUrl ,但需要动态改变
     * 这种方式不用在每个接口方法上加 Header,也是个很好的选择
     *
     * @param url
     */
    public void setGlobalBaseUrl(String url) {
        synchronized (mBaseUrlMap) {
            mBaseUrlMap.put(GLOBAL_BASE_URL, RxNetUtil.checkUrl(url));
        }
    }

    /**
     * 获取全局 BaseUrl
     */
    public HttpUrl getGlobalBaseUrl() {
        return mBaseUrlMap.get(GLOBAL_BASE_URL);
    }

    /**
     * 移除全局 BaseUrl
     */
    public void removeGlobalBaseUrl() {
        synchronized (mBaseUrlMap) {
            mBaseUrlMap.remove(GLOBAL_BASE_URL);
        }
    }

    /**
     * 存放 BaseUrl 的映射关系
     *
     * @param baseUrlName
     * @param baseUrl
     */
    public void putBaseUrl(String baseUrlName, String baseUrl) {
        synchronized (mBaseUrlMap) {
            mBaseUrlMap.put(baseUrlName, RxNetUtil.checkUrl(baseUrl));
        }
    }

    /**
     * 取出对应 BaseUrlName  的 Url
     *
     * @param baseUrlName
     * @return
     */
    public HttpUrl getBaseUrlByName(String baseUrlName) {
        return mBaseUrlMap.get(baseUrlName);
    }

    public void removeBaseUrl(String baseUrlName) {
        synchronized (mBaseUrlMap) {
            mBaseUrlMap.remove(baseUrlName);
        }
    }

    public void clearAllBaseUrl() {
        mBaseUrlMap.clear();
    }

    public boolean haveBaseUrl(String baseUrlName) {
        return mBaseUrlMap.containsKey(baseUrlName);
    }

    public int baseUrlMapSize() {
        return mBaseUrlMap.size();
    }


    /**
     * 注册当 Url 的 BaseUrl 被改变时会被回调的监听器
     *
     * @param listener
     */
    public void registerUrlChangeListener(onUrlChangeListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    /**
     * 注销当 Url 的 BaseUrl 被改变时会被回调的监听器
     *
     * @param listener
     */
    public void unregisterUrlChangeListener(onUrlChangeListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private Object[] listenersToArray() {
        Object[] listeners = null;
        synchronized (mListeners) {
            if (mListeners.size() > 0) {
                listeners = mListeners.toArray();
            }
        }
        return listeners;
    }


    /**
     * 从 {@link Request#header(String)} 中取出 DomainName
     *
     * @param request
     * @return
     */
    private String getBaseUrlFromHeaders(Request request) {
        List<String> headers = request.headers(MUL_BASE_URL_CONFIG_NAME);
        if (headers == null || headers.size() == 0)
            return null;
        if (headers.size() > 1)
            throw new IllegalArgumentException("Only one BASE_URL_NAME in the headers");
        return request.header(MUL_BASE_URL_CONFIG_NAME);
    }
}
