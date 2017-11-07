package com.jgz.rxnet2.interceptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * ================================================
 * 作者:Je(揭光智)
 * 版本:2.0.5
 * 创建日期:2017/09/22
 * 描述:OkHttp日志拦截器,主要用于打印日志的
 * 写作目的:对Http协议的认识做进一步的加深理解
 * 修订历史：
 * ================================================
 */

public class HttpLogginInterceptor implements Interceptor {

    //编码类型
    private static final Charset UTF8 = Charset.forName("UTF-8");
    //日志的级别(默认设置成不打印日志)
    private volatile HttpLogginInterceptor.Level httpLevel = Level.NONE;
    //日志工具(使用的是java.util.logging.Logger,目的是不仅仅在Android中使用)
    private Logger logger;
    //
    private java.util.logging.Level logLevel = java.util.logging.Level.INFO;


    //使用默认的日志tag
    public HttpLogginInterceptor() {
        logger = Logger.getLogger("RxNet");
    }

    //自定义日志tag
    public HttpLogginInterceptor(String tag) {
        logger = Logger.getLogger(tag);
    }

    //设置网络请求日志的级别
    public HttpLogginInterceptor setHttpLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        httpLevel = level;
        return this;
    }

    //设置打印日志的级别 (默认是INFO级别)
    public void setLogLevel(java.util.logging.Level level) {
        logLevel = level;
    }

    //输出日志
    private void log(String message) {
        logger.log(logLevel, message);
    }


    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //不需要输出日志
        if (httpLevel == Level.NONE) {
            return chain.proceed(request);
        }

        //拦截请求日志
        inteceptRequestLog(request, chain.connection());

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        return logForResponse(response, tookMs);
    }

    private Response logForResponse(Response response, long tookMs) {

        Response.Builder builder = response.newBuilder();
        Response cloneResponse = builder.build();
        ResponseBody responseBody = cloneResponse.body();
        boolean isLogBody = httpLevel == Level.BODY;
        boolean isLogHeaders = isLogBody || httpLevel == Level.HEADERS;

        String responseLineMessage = "<-- " + cloneResponse.code() + " " + cloneResponse.message() + " " + cloneResponse.request().url() + " (" + tookMs + "ms)";
        log(responseLineMessage);//1:输出响应的状态行

        try {
            if (isLogHeaders) {
                Headers headers = cloneResponse.headers();
                int count = headers.size();
                for (int i = 0; i < count; ++i) {
                    log("\t" + headers.name(i) + ": " + headers.value(i)); //2:打印消息报头
                }
                //3:打印空行(因为这是一个标准Http协议,空行不能少)
                log(" ");

                if (isLogBody && HttpHeaders.hasBody(cloneResponse)) {//如果日志方式需要打印响应正文,并且http响应中有响应正文的话,打印响应正文
                    if (isPlaintext(responseBody.contentType())) {

                        BufferedSource bufferedSource = responseBody.source();
                        bufferedSource.request(9223372036854775807L);//用于服务器异常的情况!
                        Buffer buffer = bufferedSource.buffer();
                        log("\tRequestBody:" + buffer.clone().readString(getCharset(responseBody.contentType())));//clone方法不能少!!!

                        log("<-- END HTTP (" + buffer.size() + "-byte body )");
                        return response;
                    } else {
                        log("\tRequestBody: maybe [binary body], omitted!");//4:打印响应正文,为二进制情况
                        log("<-- END HTTP");
                    }
                } else {
                    log("<-- END HTTP");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    //拦截请求日志
    private void inteceptRequestLog(Request request, Connection connection) throws IOException {
        //1:先打印请求行
        //获取请求的协议
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestLineMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        log(requestLineMessage);//输出请求行

        //2:打印请求头部
        //判断是否需要打印头部信息(加深对枚举的理解,枚举本身并没有任何含义,他的含义是在逻辑处理的时候赋予的)
        boolean isLogBody = httpLevel == Level.BODY;
        boolean isLogHeaders = isLogBody || httpLevel == Level.HEADERS;
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        if (isLogHeaders) {
            //如果有需要输出请求数据的时候,需要额外打印一下Content-Type 和 Content-Length
            if (hasRequestBody) {
                if (requestBody.contentType() != null) {
                    log("\tContent-Type: " + requestBody.contentType());
                }

                if (requestBody.contentLength() != -1L) {
                    log("\tContent-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    log("\t" + name + ": " + headers.value(i));
                }
            }
        }

        //3:打印空行(因为这是一个标准Http协议,空行不能少)
        log(" ");

        //4:打印请求体
        if (hasRequestBody) {
            if (isPlaintext(requestBody.contentType())) {
                bodyToString(request);
            } else {
                log("\tRequestBody: maybe [binary body], omitted!" + " (binary " + requestBody.contentLength() + "-byte body omitted)");
            }
        }

        //5:结束标识符
        log("--> END " + request.method());
    }

    /**
     * 如果是可读的文本,则返回true,否则返回flase
     * 注:比如请求体中包含的是一张图片的信息,那么就是不可能的状态
     */
    private boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private void bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            log("\tRequestBody:" + buffer.readString(charset) + " (" + body.contentLength() + "-byte body)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    //打印日志的级别
    public enum Level {
        NONE,       //不打印log
        BASIC,      //只打印 请求首行 和 响应首行
        HEADERS,    //打印请求和响应的所有 Header
        BODY;        //所有数据全部打印

        Level() {
        }
    }
}
