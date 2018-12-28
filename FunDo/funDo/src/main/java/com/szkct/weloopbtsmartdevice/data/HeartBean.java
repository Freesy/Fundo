package com.szkct.weloopbtsmartdevice.data;

import java.io.Serializable;

public class HeartBean implements Serializable {

    public HeartBean(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int x;

    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
