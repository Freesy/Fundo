package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;

//import com.kct.fundobeta.btnotification.R;

/**
 * 对话框万能的自定义  只需替换导入代码   自动换成最新的风格 需要时再添加新逻辑
 * 多选单选  自定义view的对话框就不要调用这个了
 * setPositiveButton 确定必须用这个
 * Created by HRJ on 2018/1/16.
 */

public class AlertDialog extends android.support.v7.app.AlertDialog{
    protected AlertDialog(Context context) {
        super(context);
    }

    protected AlertDialog(Context context, int theme) {
        super(context, theme);
    }

    protected AlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder extends android.support.v7.app.AlertDialog.Builder{

        public Builder(Context context) {
            super(context);
            init();
        }

        public Builder(Context context, int theme) {
            super(context, theme);
            init();
        }

        TextView txt_title,txt_msg;
        Button btn_neg,btn_pos;
        android.support.v7.app.AlertDialog mAlertDialog;

        private void init() {
            View rootview= LayoutInflater.from(getContext()).inflate(R.layout.view_alertdialog,null);
            setView(rootview);
            txt_title= (TextView) rootview.findViewById(R.id.txt_title);
            txt_msg= (TextView) rootview.findViewById(R.id.txt_msg);
            btn_neg= (Button) rootview.findViewById(R.id.btn_neg);
            btn_pos= (Button) rootview.findViewById(R.id.btn_pos);
            btn_neg.setVisibility(View.GONE);
            btn_pos.setVisibility(View.GONE);
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setTitle(int titleId) {
            return setTitle(getContext().getText(titleId));
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setTitle(CharSequence title) {
            txt_title.setText(title);
            return this;
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setMessage(int messageId) {
            return setMessage(getContext().getText(messageId));
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setMessage(CharSequence message) {
            txt_msg.setText(message);
            return this;
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setPositiveButton(int textId, OnClickListener listener) {
            return setPositiveButton(getContext().getText(textId), listener);
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            btn_pos.setText(text);
            btn_pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                        listener.onClick(mAlertDialog,0);
                    mAlertDialog.cancel();
                }
            });
            btn_pos.setVisibility(View.VISIBLE);
            return this;
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setNegativeButton(int textId, OnClickListener listener) {
            return setNeutralButton(textId, listener);
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setNeutralButton(int textId, OnClickListener listener) {
            return setNegativeButton(getContext().getText(textId), listener);
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            return setNeutralButton(text, listener);
        }

        @Override
        public android.support.v7.app.AlertDialog.Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            btn_neg.setText(text);
            btn_neg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                        listener.onClick(mAlertDialog,0);
                    mAlertDialog.cancel();
                }
            });
            btn_neg.setVisibility(View.VISIBLE);
            return this;
        }

        @Override
        public android.support.v7.app.AlertDialog create() {
            mAlertDialog=super.create();
            mAlertDialog.getWindow().getDecorView().setBackgroundResource(0);//背景为空
            return mAlertDialog;
        }

    }

    @Override
    public void create() {
        super.create();
        getWindow().getDecorView().setBackgroundResource(0);//背景为空
    }

}
