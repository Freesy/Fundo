package com.szkct.weloopbtsmartdevice.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;


/**
 * 加载中Dialog
 *
 * @author HerotCulb
 * 
 * @E-mail herotculb@live.com
 * 
 * @Createtime 2014-5-10 上午9:14:34
 *
 */
public class LoadingDialog extends AlertDialog {

    private TextView tips_loading_msg;

    private String message = null;

    public LoadingDialog(Context context) {
        super(context);
        message = getContext().getResources().getString(R.string.pull_to_refresh_footer_refreshing_label);
    }

    public LoadingDialog(Context context, String message) {
        super(context);
        this.message = message;
        this.setCancelable(false);
    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
        this.message = message;
        this.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        this.setContentView(R.layout.view_tips_loading);
//        tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);

        this.setContentView(R.layout.progress_custom);   // progress_custom    ---- view_tips_loading
        tips_loading_msg = (TextView) findViewById(R.id.message);   // message   --- tips_loading_msg
        tips_loading_msg.setText(this.message);
    }

    public void setText(String message) {
        this.message = message;
        tips_loading_msg.setText(this.message);
    }

    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }

    /**
     * 当窗口焦点改变时调用
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        // 获取ImageView上的动画背景
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        // 开始动画
        spinner.start();
    }

}
