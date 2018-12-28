package com.szkct.weloopbtsmartdevice.data;

import com.google.gson.annotations.SerializedName;

/**
 * 服务器通用返回数据格式
 */

public class BaseEntity<E> {

    @SerializedName("msgCode")
    private int msgCode;
    @SerializedName("flag")
    private String flag;
    @SerializedName("data")
    private E data;

    public boolean isSuccess() {
        return msgCode == 0;
    }

    public int getCode() {
        return msgCode;
    }

    public String getFlag() {
        return flag;
    }

    public E getData() {
        return data;
    }

    public void setMsgCode(int msgCode) {
        this.msgCode = msgCode;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setData(E data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "msgCode=" + msgCode +
                ", flag='" + flag + '\'' +
                ", data=" + data +
                '}';
    }
}
