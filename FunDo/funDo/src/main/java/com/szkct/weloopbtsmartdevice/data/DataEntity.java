package com.szkct.weloopbtsmartdevice.data;

import java.io.Serializable;

/**
 * 服务器通用返回数据格式,原来的没有实现Serializable,所以拷贝一个
 */

public class DataEntity<E> implements Serializable {

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
                ", data=" + data+
                '}';
    }
}
