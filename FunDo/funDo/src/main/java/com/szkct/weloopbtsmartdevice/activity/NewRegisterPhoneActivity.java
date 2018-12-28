package com.szkct.weloopbtsmartdevice.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.CityInfoModel;
import com.szkct.weloopbtsmartdevice.data.advertisingBean;
import com.szkct.weloopbtsmartdevice.login.DataError;
import com.szkct.weloopbtsmartdevice.login.Gdata;
import com.szkct.weloopbtsmartdevice.login.Logg;
import com.szkct.weloopbtsmartdevice.login.MyCallback;
import com.szkct.weloopbtsmartdevice.login.NewNetUtils;
import com.szkct.weloopbtsmartdevice.login.WxGuildeActivity;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MyDataActivity;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.MD5Utils;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.thefinestartist.finestwebview.FinestWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class NewRegisterPhoneActivity extends Activity implements View.OnClickListener {
    private static final String TAG = NewRegisterPhoneActivity.class.getSimpleName();
    private SharedPreferences preferences;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    boolean isFirstLogin ;

    private final int COUNT_TIME = 60;

    TextView tv_title;
    boolean bindTo;//是不是需要绑定

    TextView tv_country;
    ImageView imageView4;
    Integer countrynumber;//国际区域代码
    EditText et_phonenumber;
    LoadingDialog loadingDialog;
    EditText et_yzm, et_password;
    Button btn_getverificationcode, btn_ok;
    public final int CODE = 10086, CHECK = 10;
    private HTTPController hc;
    private Handler mmHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case CHECK:  // 登陆后请求接口得到跳转H5连接

                    if (Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")) {
                        //    startActivity(new Intent(NewLoginPhoneActivity.this, WxGuildeActivity.class));
                        String url = Constants.CHECK_Login_next;
                        hc = HTTPController.getInstance();
                        hc.open(NewRegisterPhoneActivity.this);
                        hc.getNetworkStringData(url, mmHandler, CODE);

                    } else {
                        finish();
                    }

                    break;

                case CODE:
                    if (!StringUtils.isEmpty(msg.obj.toString())) {
                        Log.e("msg:", "" + msg.obj.toString());
                        if (!("-1").equals(msg.obj.toString())) {

                            advertisingBean advertisingBean = new Gson().fromJson(msg.obj.toString(), advertisingBean.class);
                            Log.e("advertisingBean:", "" + advertisingBean.getData().getGuideUrl());

                            String urls = advertisingBean.getData().getGuideUrl();
                            if (!StringUtils.isEmpty(urls)) {
                                new FinestWebView.Builder(NewRegisterPhoneActivity.this).showIconMenu(false).show(urls);
                            } else {
                                new FinestWebView.Builder(NewRegisterPhoneActivity.this).showIconMenu(false).show(Constants.CHECK_moren);
                            }
                            finish();
                        } else {
                            new FinestWebView.Builder(NewRegisterPhoneActivity.this).showIconMenu(false).show(Constants.CHECK_moren);
                            finish();
                        }

                    } else {
                        finish();
                    }

                    break;

            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断主题。
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_new_register_phone);
        initview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 123) {
            initcity(data.getStringExtra("key"));
        }
    }

    private void initcity(String country) {
        //国家/地区 中国(＋86)
        countrynumber = BTNotificationApplication.getInstance().getGlobalRoaming(country);
        if(countrynumber == null){
            countrynumber = 86;
//            country = "中国";

            if(Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")) {
                country = "中国";
            }else{
                country = "China";
            }

        }
        tv_country.setText(Html.fromHtml("<font color='#ffffff'>"+getString(R.string.country_area)+"</font>"+"<font color='#37eeea'>"
                +country+"(＋"+countrynumber+")</font>"));
    }

    Timer mTimer = new Timer();
    int time = COUNT_TIME;//倒计时

    private void initview() {
        btn_getverificationcode = (Button) findViewById(R.id.btn_getverificationcode);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        if(Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")) {
            imageView4.setVisibility(View.VISIBLE);
        }else{
            imageView4.setVisibility(View.GONE);
        }
        btn_getverificationcode.setOnClickListener(this);
        et_yzm = (EditText) findViewById(R.id.et_yzm);
        et_password = findViewById(R.id.et_password);
        findViewById(R.id.tv_tologin).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.bt_next).setOnClickListener(this);
        findViewById(R.id.tv_privacypolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), WebActivity.class);
                String url;
                if (Utils.getLanguage().equals("zh")&& Utils.getCountry().equals("CN")) {
                    url = "file:///android_asset/about_ZH.htm";
                }else if(Utils.getCountry().equals("TW")||Utils.getCountry().equals("HK")){
                    url = "file:///android_asset/about_TW.htm";
                }else {
                    url = "file:///android_asset/about_US.htm";
                }
                it.putExtra("url",url);
                startActivity(it);
            }
        });
        et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);
        tv_country = (TextView) findViewById(R.id.tv_country);
        initcity(UTIL.readPre(getApplicationContext(), CityInfoModel.class + "", "country"));

        bindTo = getIntent().getBooleanExtra("bindTo", false);
        tv_title = (TextView) findViewById(R.id.tv_title);
        if (bindTo) {
            tv_title.setText(R.string.registered_button_text);
            findViewById(R.id.tv_tologin).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.v_quhao).setOnClickListener(this);

        // 注册短信监听器
        SMSSDK.unregisterAllEventHandler();
        SMSSDK.registerEventHandler(eventHandler);
    }

    private void count() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                btn_getverificationcode.post(new Runnable() {
                    @Override
                    public void run() {
                        if (time != 0) {
                            time--;
                            btn_getverificationcode.setText(time + "");
                            return;
                        }
                        btn_getverificationcode.setEnabled(true);
                        btn_getverificationcode.setText(R.string.obtain);
                        time = COUNT_TIME;
                        mTimer.cancel();
                        mTimer.purge();
                        mTimer = null;
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getverificationcode://获取验证码
            {
                final String phone = et_phonenumber.getText().toString();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(getApplicationContext(), R.string.phone_is_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(time != COUNT_TIME) return;
                verifyPhone(phone);
                break;
            }
            case R.id.tv_tologin:
//                startActivity(new Intent(getApplicationContext(),NewLoginActivity.class));
                finish();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.v_quhao://选择区号
                startActivityForResult(new Intent(getApplicationContext(), NewSelectCityListActivity.class), 0);
                break;
            case R.id.bt_next://注册   验证验证码
            {
                if (TextUtils.isEmpty(et_yzm.getText().toString()) || TextUtils.isEmpty(et_password.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.please_fill_complete, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_password.length() < 6) { //密码长度6-20
                    Toast.makeText(getApplicationContext(), R.string.pw_length_limit, Toast.LENGTH_SHORT).show();
                    return;
                }
//                showLoadingDialogNew(null);
//                registerPhone();

                showLoadingDialogNew(getString(R.string.progress_dialog_title));
                final String phone = et_phonenumber.getText().toString();
                SMSSDK.submitVerificationCode(countrynumber + "", phone, et_yzm.getText().toString());
                break;
            }
        }
    }

    private void verifyPhone(final String phone){
        HashMap map = new HashMap();
        map.put("phone", getStringPhone(countrynumber, phone));
        String json = NewNetUtils.getPostJson(map);
        NewNetUtils.getInstance().sendPost(ServerConfig.VERIFY_GETCODE_FORGET, json, new MyCallback() {
            @Override
            public void mySuccess(Object responseInfo) {
          //      btn_getverificationcode.setText(time + "");
          //      if(mTimer == null){
          //          mTimer = new Timer();
          //      }
          //      count();
                //请求短信的验证码
                SMSSDK.getVerificationCode(countrynumber + "", phone);
            }

            @Override
            public void myFailure(DataError error) {
                Logg.e(TAG, "myFailure: error="+error.getMessage());
                switch (error.getCode()){
                    case 2:
                        Toast.makeText(getApplicationContext(), getString(R.string.user_already_register), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), getString(R.string.request_fail), Toast.LENGTH_SHORT).show();
                        break;
                }
                dismissLoadingDialog();
            }
        });
    }

    // 创建EventHandler对象
    EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, final Object data) {
            Logg.e(TAG, "afterEvent: result="+result);
//            dismissLoadingDialog();
            if (result == SMSSDK.RESULT_COMPLETE) {
                      if(mTimer == null){
                          mTimer = new Timer();
                      }
                      count();

                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    registerPhone();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Log.e("hrj", "afterEvent: 获取验证码成功");
                    dismissLoadingDialog();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    Log.e("hrj", "afterEvent: 返回支持发送验证码的国家列表");
                }
            } else {
                ((Throwable) data).printStackTrace();
                tv_country.post(new Runnable() {
                    @Override
                    public void run() {
                        if (((Throwable) data).getMessage().contains("468")) {
                            //验证码错误
                            Toast.makeText(getApplicationContext(), getString(R.string.ssdk_sms_dialog_error_code), Toast.LENGTH_SHORT).show();
                        } else if(((Throwable) data).getMessage().contains("603")|| ((Throwable) data).getMessage().contains("457")){
                            Toast.makeText(getApplicationContext(), R.string.phone_is_error, Toast.LENGTH_SHORT).show();
                        }else{
                            //短信验证失败
                            Toast.makeText(getApplicationContext(), getString(R.string.ssdk_sms_dialog_error_desc_106), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dismissLoadingDialog();
            }
        }
    };

    private void registerPhone() {
        HashMap map = new HashMap();
        String phone = et_phonenumber.getText().toString();
        map.put("phone", getStringPhone(countrynumber, phone));
        map.put("password", MD5Utils.getMD5(et_password.getText().toString()));
        String json = NewNetUtils.getPostJson(map);

        Logg.e(TAG, "registerPhone: json="+json);

        NewNetUtils.getInstance().sendPost(ServerConfig.REGISTER_GETCODE, json, new MyCallback() {
            @Override
            public void mySuccess(Object responseInfo) {
                Logg.e(TAG, "mySuccess: responseInfo=" + responseInfo);
//                String result = responseInfo.toString();
//                Gdata.setPersonData(result);
             /*   if(Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")) {
                    startActivity(new Intent(NewRegisterPhoneActivity.this, WxGuildeActivity.class));
                }
                dismissLoadingDialog();
                finish();*/
                preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
                isFirstLogin = preferences.getBoolean("isFirstLogin", true);
                if (isFirstLogin) {
                    mmHandler.sendEmptyMessage(CHECK);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isFirstLogin", false); //保存是否第一次连接设备
                    editor.commit();

                }else{
                    finish();
                }
            }

            @Override
            public void myFailure(DataError error) {
                Logg.e(TAG, "myFailure: error="+error.getMessage());
                switch (error.getCode()){
                    case 2:
                        Toast.makeText(getApplicationContext(), getString(R.string.user_already_register), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), getString(R.string.request_fail), Toast.LENGTH_SHORT).show();
                        break;
                }
                dismissLoadingDialog();
            }
        });
        Logg.e(TAG, "registerPhone: ");
    }

    public void parsePhotoRegisterData(String content) { // , String[] errStr
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msgCode");
            if (nRetCode == 0) {

                JSONObject obj = jsonObj.optJSONObject("data");
//                int mEmailVerifiCode = obj.optInt("codes");   RegisterTwoActivity
                String userId = obj.optString("mid");
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id
                Toast.makeText(getApplicationContext(), getString(R.string.registered_button_text) + getString(R.string.install_code_success), Toast.LENGTH_SHORT).show();

//                if(handler != null){
//                    handler.removeCallbacksAndMessages(null); // TODO -- 注册成功后，将handler消息置空，否则，都注册成功了还会提示，验证码过期的消息
//                }
                dismissLoadingDialog();
                String phone = et_phonenumber.getText().toString();
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.USERNAME, phone);  // 保存当前登录的邮箱 ---- 作为当前的保存的账号
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.PASSWORD, et_password.getText().toString());  // 保存登录的邮箱密码   ---- 作为当前的保存的账号对应的密码
                Intent intent = new Intent(getApplicationContext(), MyDataActivity.class);
////                intent.putExtra("Height",mEmailVerifiCode);
////                intent.putExtra("Weight",mRegisterEmail);
                startActivity(intent);
                finish();
            } else if (nRetCode == 1) {
                dismissLoadingDialog();
                Toast.makeText(getApplicationContext(), "参数为空", Toast.LENGTH_SHORT).show();

                //TODO ----- 测试用
//                Intent intent = new Intent(getApplicationContext(), AddPersonaldataActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
//                startActivity(intent);
            } else if (nRetCode == 2) {
                dismissLoadingDialog();
                Toast.makeText(getApplicationContext(), "用户已存在", Toast.LENGTH_SHORT).show();
            } else if (nRetCode == 3) {
                dismissLoadingDialog();
                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
            }
//            else if(nRetCode == 4){
//                dismissLoadingDialog();
//                Toast.makeText(getApplicationContext(), "验证码输入错误", Toast.LENGTH_SHORT).show();
//            }else if(nRetCode == 5){
//                dismissLoadingDialog();
//                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        return user;
    }

    public void showLoadingDialogNew(String content) {
        if (loadingDialog != null)
            return;
        loadingDialog = new LoadingDialog(this,R.style.Custom_Progress, content);
        loadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (loadingDialog == null)
            return;
        loadingDialog.cancel();
        loadingDialog = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /**
     * +86123456789
     *
     * @param countrynumber
     * @param phone
     * @return
     */
    public String getStringPhone(Integer countrynumber, String phone) {
        return "+" + countrynumber + phone;
    }

}
