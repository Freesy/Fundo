package com.szkct.weloopbtsmartdevice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.CityInfoModel;
import com.szkct.weloopbtsmartdevice.login.DataError;
import com.szkct.weloopbtsmartdevice.login.Logg;
import com.szkct.weloopbtsmartdevice.login.MyCallback;
import com.szkct.weloopbtsmartdevice.login.NewNetUtils;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.MD5Utils;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class NewForgetPhoneActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = NewForgetPhoneActivity.class.getSimpleName();

    private final int COUNT_TIME = 60;

    TextView tv_country;
    Integer countrynumber;//国际区域代码
    EditText et_phonenumber;
    LoadingDialog loadingDialog;
    Button btn_getverificationcode,btn_ok;
    EditText et_yzm,et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断主题。
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_new_forget_phone);
        initview();
    }

    private void initview() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.bt_next).setOnClickListener(this);
        et_phonenumber= (EditText) findViewById(R.id.et_phonenumber);
        tv_country= (TextView) findViewById(R.id.tv_country);
        btn_getverificationcode= (Button) findViewById(R.id.btn_getverificationcode);
        btn_getverificationcode.setOnClickListener(this);
        et_yzm= (EditText) findViewById(R.id.et_yzm);
        et_password= (EditText) findViewById(R.id.et_password);
        btn_ok= (Button) findViewById(R.id.bt_next);
        btn_ok.setOnClickListener(this);

        initcity(UTIL.readPre(getApplicationContext(),CityInfoModel.class+"","country"));

        // 注册短信监听器
        SMSSDK.unregisterAllEventHandler();
        SMSSDK.registerEventHandler(eventHandler);
        findViewById(R.id.v_quhao).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode==123){
            initcity(data.getStringExtra("key"));
        }
    }

    private void initcity(String country) {
        //国家/地区 中国(＋86)
        countrynumber= BTNotificationApplication.getInstance().getGlobalRoaming(country);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                onBackPressed();
                break;
            case R.id.bt_next://下一步 发送验证码
            {
                final String phone = et_phonenumber.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), R.string.please_fill_complete, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(et_yzm.getText().toString())||TextUtils.isEmpty(et_password.getText().toString())){
                    Toast.makeText(getApplicationContext(),R.string.please_fill_complete,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_password.length()<6){ //密码长度6-20
                    Toast.makeText(getApplicationContext(),R.string.pw_length_limit,Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoadingDialogNew(getString(R.string.progress_dialog_title));
                SMSSDK.submitVerificationCode(countrynumber + "", phone, et_yzm.getText().toString());
                break;
            }
            case R.id.v_quhao:
                startActivityForResult(new Intent(getApplicationContext(),NewSelectCityListActivity.class),0);
                break;
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


        }
    }

    private void verifyPhone(final String phone){
        HashMap map = new HashMap();
        map.put("phone", getStringPhone(countrynumber, phone));
        String json = NewNetUtils.getPostJson(map);
        NewNetUtils.getInstance().sendPost(ServerConfig.VERIFY_GETCODE_FORGET, json, new MyCallback() {
            @Override
            public void mySuccess(Object responseInfo) {
                Toast.makeText(getApplicationContext(), getString(R.string.user_isnot_register), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myFailure(DataError error) {
                Logg.e(TAG, "myFailure: error="+error.getMessage());
                switch (error.getCode()){
                    case 2:
                        btn_getverificationcode.setText(time + "");
                        if(mTimer == null){
                            mTimer = new Timer();
                        }
                        count();
                        //请求短信的验证码
                        SMSSDK.getVerificationCode(countrynumber + "", phone);
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

    // 创建EventHandler对象
    EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, final Object data) {
//            dismissLoadingDialog();
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功   验证成功
                    Log.e("hrj", "afterEvent: 提交验证码成功");
                    final String phone=et_phonenumber.getText().toString();

                    HashMap map = new HashMap();
                    map.put("phone", getStringPhone(countrynumber, phone));
                    map.put("password", MD5Utils.getMD5(et_password.getText().toString()));
                    String json = NewNetUtils.getPostJson(map);

                    NewNetUtils.getInstance().sendPost(ServerConfig.REGISTER_GETCODE_FORGET, json, new MyCallback() {
                        @Override
                        public void mySuccess(Object responseInfo) {
                            Logg.e(TAG, "mySuccess: responseInfo=" + responseInfo);
                            String result = responseInfo.toString();
//                            Gdata.setPersonData(result);
                            Toast.makeText(getApplicationContext(), R.string.install_code_success, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewForgetPhoneActivity.this, NewLoginPhoneActivity.class); //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                            startActivity(intent);
                            dismissLoadingDialog();
                            finish();
                        }

                        @Override
                        public void myFailure(DataError error) {
                            Logg.e(TAG, "myFailure: "+error.toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.request_fail), Toast.LENGTH_SHORT).show();
                            dismissLoadingDialog();
                        }
                    });

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Log.e("hrj", "afterEvent: 获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    Log.e("hrj", "afterEvent: 返回支持发送验证码的国家列表");
                }
            } else {
                ((Throwable) data).printStackTrace();
                tv_country.post(new Runnable() {
                    @Override
                    public void run() {
                        if(((Throwable) data).getMessage().contains("468")){
                            //验证码错误
                            Toast.makeText(getApplicationContext(), getString(R.string.ssdk_sms_dialog_error_code), Toast.LENGTH_SHORT).show();
                        }else {
                            //短信验证失败
                            Toast.makeText(getApplicationContext(), getString(R.string.ssdk_sms_dialog_error_desc_106), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dismissLoadingDialog();
            }
        }
    };

    Timer mTimer = new Timer();
    int time =COUNT_TIME;//倒计时
    private void count()
    {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                btn_getverificationcode.post(new Runnable() {
                    @Override
                    public void run() {
                        if(time !=0) {
                            time--;
                            btn_getverificationcode.setText(time +"");
                            return;
                        }
                        btn_getverificationcode.setEnabled(true);
                        btn_getverificationcode.setText(R.string.obtain);
                        time =COUNT_TIME;
                        mTimer.cancel();
                        mTimer.purge();
                        mTimer=null;
                    }
                });
            }
        },0,1000);
    }
    public void showLoadingDialogNew(String content) {
        loadingDialog = new LoadingDialog(this,R.style.Custom_Progress, content);
        loadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if(loadingDialog==null)
            return;
        loadingDialog.cancel();
        loadingDialog=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /**
     * +86123456789
     * @param countrynumber
     * @param phone
     * @return
     */
    public String getStringPhone(Integer countrynumber, String phone){
        return "+"+countrynumber+phone;
    }

}
