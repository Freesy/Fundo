package com.szkct.map.utils;

import android.view.animation.TranslateAnimation;

/**
 * 作者：xiaodai.
 * 2016/12/2.
 * 版本：v1.0
 */

public class AnimationUtil {
    private static final String TAG = AnimationUtil.class.getSimpleName();


    /**
     * 从控件所在位置移除窗口向左
     *
     * @return
     */
    public static TranslateAnimation moveToViewRight() {
//        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        TranslateAnimation mHiddenAction = new TranslateAnimation( 0, 200, 0, 0 );
        mHiddenAction.setDuration( 2000 );
        return mHiddenAction;
    }
    /**
     * 从控件所在位置移除窗口向左
     *
     * @return
     */
    public static TranslateAnimation moveToViewLeft() {
//        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        TranslateAnimation mHiddenAction = new TranslateAnimation( 200, 0, 0, 0 );
        mHiddenAction.setDuration( 2000 );
        return mHiddenAction;
    }

    /**
     * 从控件所在位置移除窗口向右
     *
     * @return
     */
    public static TranslateAnimation moveToRight() {
//        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        TranslateAnimation mHiddenAction = new TranslateAnimation(-200,0, 0, 0);
        mHiddenAction.setDuration( 2000 );
        return mHiddenAction;
    }
    /**
     * 从控件所在位置移除窗口向左
     *
     * @return
     */
    public static TranslateAnimation moveToLeft() {
//        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        TranslateAnimation mHiddenAction = new TranslateAnimation( 0, -200, 0, 0 );
        mHiddenAction.setDuration( 2000 );
        return mHiddenAction;
    }

    /**
     * 从控件所在位置移除窗口
     *
     * @return
     */
    public static TranslateAnimation moveToViewBottom() {
//        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        TranslateAnimation mHiddenAction = new TranslateAnimation( 0, 0, 0, -500 );
        mHiddenAction.setDuration( 2000 );
        return mHiddenAction;
    }

    /**
     * 从窗口外将空间移入到控件位置
     *
     * @return
     */
    public static TranslateAnimation moveToViewLocation() {
//        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                0.0f, Animation.RELATIVE_TO_SELF, 2.0f);
        TranslateAnimation mHiddenAction = new TranslateAnimation( 0, 0, -500, 0 );
        mHiddenAction.setDuration( 2000 );
        return mHiddenAction;
    }
}
