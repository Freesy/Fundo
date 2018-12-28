package com.szkct.weloopbtsmartdevice.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;

/**
 *This is a class that is related to the load dialog.
 *
 *@author zhangxiong
 */
@SuppressLint("NewApi") class AnimatorPlayer extends AnimatorListenerAdapter {

    private boolean interrupted = false;
    private Animator[] animators;

    public AnimatorPlayer(Animator[] animators) {
        this.animators = animators;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!interrupted) animate();
    }

    public void play() {
        animate();
    }

    public void stop() {
        interrupted = true;
    }

    private void animate() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.addListener(this);
        set.start();
    }
}
