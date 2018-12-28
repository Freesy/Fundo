package com.szkct.weloopbtsmartdevice.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;

/**
 * 创建自定义的dialog，主要学习其实现原理
 * Created by chengguo on 2016/3/22.
 */
public class helpHintDialog extends Dialog {

    private Button btn_close;//确定按钮
    private TextView txt_msg;//消息提示文本
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    //确定文本和取消文本的显示内容
    private String yesStr;

    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器



    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {
        this.yesOnclickListener = onYesOnclickListener;
    }


    public helpHintDialog(Context context) {
        super(context,R.style.AppTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_helphintdialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {

        if (messageStr != null) {
            txt_msg.setText(messageStr);
        }
        //如果设置按钮的文字
        if (yesStr != null) {
            btn_close.setText(yesStr);
        }

    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        btn_close = (Button) findViewById(R.id.btn_close);
        txt_msg = (TextView) findViewById(R.id.txt_msg);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    /**
     * 设置确定按钮被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

}