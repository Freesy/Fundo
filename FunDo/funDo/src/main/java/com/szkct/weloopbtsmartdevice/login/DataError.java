package com.szkct.weloopbtsmartdevice.login;

import java.io.Serializable;

/**
 * 服务器通用返回数据格式,所有服务器返回失败的都用这个类封装
 */

public class DataError<E> implements Serializable {

    private int code;
    private String message;
    private E data;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public E getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(E data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "msgCode=" + code +
                ", flag='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
