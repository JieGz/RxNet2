package com.jgz.rxnet2.http;

/**
 * Created by Je on 2017/8/8.
 */

public class HttpResult<T> {
    private int code;
    private String msg;
    private String desc;
    private String path;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", desc='" + desc + '\'' +
                ", path='" + path + '\'' +
                ", data=" + data +
                '}';
    }
}
