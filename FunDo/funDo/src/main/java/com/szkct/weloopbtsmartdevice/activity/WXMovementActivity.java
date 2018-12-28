package com.szkct.weloopbtsmartdevice.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;



/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/8/17
 * 描述: ${VERSION}
 * 修订历史：
 */

public class WXMovementActivity extends AppCompatActivity implements View.OnClickListener {   //微信运动
    private static final String TAG = WXMovementActivity.class.getSimpleName();
    private ImageView iv_wx_authorization;
    private int[] iv_authorizations = {R.drawable.wx_movement_1, R.drawable.wx_movement_2,R.drawable.wx_movement_3,R.drawable.wx_movement_4};  //轮播图片
    private MyHandler handler;
    private MyRunnable myRunnable;
    private int index = 0;
    private static Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_wxmovement);

        context = this;
        iv_wx_authorization = (ImageView) findViewById(R.id.iv_wx_authorization);
        findViewById(R.id.iv_back).setOnClickListener(this);

        handler = new MyHandler(this);
        myRunnable = new MyRunnable();
        handler.postDelayed(myRunnable,2000);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }


    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            index++;
            index = index % 4;
            iv_wx_authorization.setImageResource(iv_authorizations[index]);
            handler.postDelayed(myRunnable, 2000);
        }

    }

    static class MyHandler extends Handler {
        WeakReference<Activity> mActivityReference;
        MyHandler(Activity activity) {
            mActivityReference= new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(myRunnable);
    }
}
