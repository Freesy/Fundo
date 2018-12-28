package com.szkct.weloopbtsmartdevice.util;

/**
 * Created by thinkpad on 2017/3/13.
 */

public class MessageEvent {

    private String message;
    private Object object;

    public MessageEvent(String message, Object object) {
        this.message = message;
        this.object = object;
    }

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
