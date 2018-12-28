package com.szkct.weloopbtsmartdevice.util;

import android.view.animation.Interpolator;

/**
 *This is a class that is related to the load dialog.
 *
 *@author zhangxiong
 */
class HesitateInterpolator implements Interpolator {

    private static double POW = 1.0/2.0;

    @Override
    public float getInterpolation(float input) {
        return input < 0.5
                ? (float) Math.pow(input * 2, POW) * 0.5f
                : (float) Math.pow((1 - input) * 2, POW) * -0.5f + 1;
    }
}