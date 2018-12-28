package com.szkct.weloopbtsmartdevice.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;


/**
*This is a class that is related to the load dialog.
*
*@author zhangxiong
*/
public class MyLoadingDialog extends AlertDialog {

private static final int DELAY = 150;
private static final int DURATION = 1500;

private int size;
private AnimatedView[] spots;
private AnimatorPlayer animator;

private String mTitle = null;
private TextView tv_alert_content;

public MyLoadingDialog(Context context) {
    super(context);
//this(context, R.style.SpotsDialogDefault);
}

//    public MyLoadingDialog(Context context, String title) {
//        super(context, title);
//    }

public MyLoadingDialog(Context context, int theme) {
super(context, theme);
}

public MyLoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
super(context, cancelable, cancelListener);
}

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);

setContentView(R.layout.loading_dialog_new);  // landing_load_dialog
setCanceledOnTouchOutside(false);

    tv_alert_content = (TextView)findViewById(R.id.tv_alert_content);
    tv_alert_content.setText(mTitle);

//        myDialog.getWindow().setContentView(R.layout.loading_dialog_new);
//        tv_alert_content = (TextView) myDialog.getWindow().findViewById(R.id.tv_alert_content);
//        tv_alert_content.setText(content);
//        myDialog.setView(tv_alert_content);
//        myDialog.setCancelable(true);   // 应该让该对话框自动销毁
//        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
//        myDialog.getWindow().setBackgroundDrawable(dw);
//initProgress();
}

@Override
protected void onStart() {
super.onStart();

//animator = new AnimatorPlayer(createAnimations());
//animator.play();
}

@Override
protected void onStop() {
super.onStop();

//animator.stop();
}

//

private void initProgress() {
TextView titleTv = (TextView) findViewById(R.id.title);
if(mTitle != null && !mTitle.equals("")){
titleTv.setText(mTitle);
}

ProgressLayout progress = (ProgressLayout) findViewById(R.id.progress);
//        ImageView progress = (ImageView) findViewById(R.id.progress);

size = progress.getSpotsCount();

//spots = new AnimatedView[size];
//int size = getContext().getResources().getDimensionPixelSize(R.dimen.spot_size);
//int progressWidth = getContext().getResources().getDimensionPixelSize(R.dimen.progress_width);
//for (int i = 0; i < spots.length; i++) {
//AnimatedView v = new AnimatedView(getContext());
//v.setBackgroundResource(R.drawable.spot);
//v.setTarget(progressWidth);
//v.setXFactor(-1f);
//progress.addView(v, size, size);
//spots[i] = v;
//}
}

    @SuppressLint("NewApi")
    private Animator[] createAnimations() {
        Animator[] animators = new Animator[size];
        for (int i = 0; i < spots.length; i++) {
            Animator move = ObjectAnimator.ofFloat(spots[i], "xFactor", 0, 1);
            move.setDuration(DURATION);
            move.setInterpolator(new HesitateInterpolator());
            move.setStartDelay(DELAY * i);
            animators[i] = move;
        }
        return animators;
    }

public void setWaitingTitle(String title){
mTitle = title;
//    tv_alert_content.setText(mTitle);
}
}
