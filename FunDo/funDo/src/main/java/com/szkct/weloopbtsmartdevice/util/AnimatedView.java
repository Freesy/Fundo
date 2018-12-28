package com.szkct.weloopbtsmartdevice.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

/**
 *This is a class that is related to the load dialog.
 *
 *@author zhangxiong
 */
@SuppressLint("NewApi") class AnimatedView extends View {

    private int target;

    public AnimatedView(Context context) {
        super(context);
    }

    public float getXFactor() {
        return getX() / target;
    }

    public void setXFactor(float xFactor) {
        setX(target * xFactor);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }
}
