package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.MD5Utils;
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
import com.szkct.weloopbtsmartdevice.util.RequestCallBackEx;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.XHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kct on 2017/2/5.
 */
public class RegisterTwoActivity extends Activity implements View.OnClickListener{

    private TextView tv_alert_content,tv_serviceagreement,tv_privacypolicy;
    private TextView btn_login;
    private ImageView back;
    private Button btn_getverificationcode,btn_ok,btn_login_password_show_hide;
    private EditText et_yzm,et_password;
    private boolean isShowPassword = false;

    private String mUsername;
    private int backCountTime;
    private String  mRegisterEmail;// 注册的邮箱
    private int seconds = 0;  // 到注册页面的倒计时开始时间
    private long countdownStartTime;

    private int photoseconds = -1;

    private MyLoadingDialog myLoadingDialog;
    private  String locale;
    private String mPhotoVerifiCode;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 1:   // 邮箱的验证码
                    if (seconds == 0)
                    {
//                        text.startAnimation(set);
                        Toast.makeText(RegisterTwoActivity.this, "验证码已失效，请重新申请验证码", Toast.LENGTH_SHORT).show();
                        btn_getverificationcode.setText("获取");
                        btn_getverificationcode.setBackgroundResource(R.drawable.login_btn_2_n);
                    } else if (seconds > 0)
                    {
//                        text.startAnimation(set);
                        btn_getverificationcode.setText(seconds + "s");
                        btn_getverificationcode.setBackgroundResource(R.drawable.login_btn_2_p);
                    } else
                    {
//                        setResult(55);
//                        finish();
                    }
                    break;

                case 2:  // 手机号的验证码
                    if (photoseconds == 0)
                    {
//                        text.startAnimation(set);
                        Toast.makeText(RegisterTwoActivity.this, "验证码已失效，请重新申请验证码", Toast.LENGTH_SHORT).show();
                        et_yzm.setText("");
                        mPhotoVerifiCode = "";
                        btn_getverificationcode.setText("获取");
                        btn_getverificationcode.setBackgroundResource(R.drawable.login_btn_2_n);

                         SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.PHONEVERIFICODE,"");  // 短信验证码
                         SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.PHONEVERIFICODECOUNTTIME, "");
                    } else if (photoseconds > 0)
                    {
//                        text.startAnimation(set);
                        btn_getverificationcode.setText(photoseconds + "s");
                        btn_getverificationcode.setBackgroundResource(R.drawable.login_btn_2_p);
                    }
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }

        setContentView(R.layout.activity_registertwo);

        locale = Locale.getDefault().getLanguage();  //检测当前系统的语言

        initView();

    }

    public void initView() { //Bundle savedInstanceState
        Calendar calendar = Calendar.getInstance();   //  long endTime;
        long currentTime = calendar.getTimeInMillis() / 1000 ;
        Log.e("MD555555", "--进入注册页面验证码时间---：" + currentTime);
        countdownStartTime = getIntent().getLongExtra("CountdownStartTime", 0);
        seconds = 120 - (int)(currentTime - countdownStartTime);  // 到注册页面的倒计时开始时间

        mRegisterEmail = getIntent().getStringExtra("RegisterEmail");  //上个页面传递过来的邮箱

        mUsername = getIntent().getStringExtra("number");               //上个页面传递过来的手机号

        backCountTime = getIntent().getIntExtra("backCountTime",0);               //上个页面传递过来的倒计时时间

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        tv_serviceagreement = (TextView) findViewById(R.id.tv_serviceagreement);  // 服务协议
        tv_privacypolicy = (TextView) findViewById(R.id.tv_privacypolicy);  // 隐私政策
        tv_serviceagreement.setOnClickListener(this);
        tv_privacypolicy.setOnClickListener(this);


        et_yzm = (EditText) findViewById(R.id.et_yzm);  // 验证码
        et_password = (EditText) findViewById(R.id.et_password); // 密码

        btn_getverificationcode = (Button) findViewById(R.id.btn_getverificationcode);  // 获取验证码
        btn_getverificationcode.setOnClickListener(this);
        if(!StringUtils.isEmpty(mRegisterEmail)) { // 为邮箱注册
            new Thread()
            {
                public void run()
                {
                    while (seconds != -1)
                    {
                        seconds--;
                        handler.sendEmptyMessage(1);
                        try
                        {
                            sleep(1000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
            }.start();
        }

        btn_login_password_show_hide = (Button) findViewById(R.id.btn_login_password_show_hide);//显示密码
        btn_login_password_show_hide.setOnClickListener(this);

        et_yzm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_yzm.setBackgroundResource(R.drawable.login_input_2_s);
                    if (StringUtils.isEmpty(et_password.getText()
                            .toString())) {
                        btn_login_password_show_hide.setVisibility(View.INVISIBLE);
                    } else {
                        btn_login_password_show_hide.setVisibility(View.VISIBLE);
                    }
                } else {
                    et_yzm.setBackgroundResource(R.drawable.login_input_2_n);
                    btn_login_password_show_hide.setVisibility(View.INVISIBLE);
                }
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isEmpty(et_password.getText().toString())) {
                    btn_login_password_show_hide.setVisibility(View.INVISIBLE);
                } else {
                    btn_login_password_show_hide.setVisibility(View.VISIBLE);
                }
//                refreshConfirm();
            }
        });

        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_password.setBackgroundResource(R.drawable.login_input_s);
                    if (StringUtils.isEmpty(et_password.getText()
                            .toString())) {
                        btn_login_password_show_hide.setVisibility(View.INVISIBLE);
                    } else {
                        btn_login_password_show_hide.setVisibility(View.VISIBLE);
                    }
                } else {
                    et_password.setBackgroundResource(R.drawable.login_input_n);
                    btn_login_password_show_hide.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        if(backCountTime > 0){
            String mCode = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.PHONEVERIFICODE);  // 短信验证码
//            SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.PHONEVERIFICODECOUNTTIME, photoseconds + "");
            if(!StringUtils.isEmpty(mCode)){
                et_yzm.setText(mCode);
            }

            photoseconds = backCountTime;
            btn_getverificationcode.setText(photoseconds + "s");
            btn_getverificationcode.setBackgroundResource(R.drawable.login_btn_2_p);
            new Thread()
            {
                public void run()
                {
                    while (photoseconds != -1)
                    {
                        photoseconds--;
                        handler.sendEmptyMessage(2);
                        try
                        {
                            sleep(1000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
            }.start();
        }else {
            photoseconds = -1;
        }

//        tv_bottom_health = (TextView) findViewById(R.id.tv_bottom_health);
//        tv_bottom_reward = (TextView) findViewById(R.id.tv_bottom_reward);
//        tv_bottom_set = (TextView) findViewById(R.id.tv_bottom_set);
//        iv_bottom_talk = (ImageView) findViewById(R.id.iv_bottom_talk);
//        iv_bottom_talkButton = (AudioRecordButton)findViewById(R.id.iv_bottom_talkButton);
//        tv_bottom_guard.setOnClickListener(onClickListener);
//        tv_bottom_health.setOnClickListener(onClickListener);
//        tv_bottom_reward.setOnClickListener(onClickListener);
//        tv_bottom_set.setOnClickListener(onClickListener);
//        iv_bottom_talk.setOnClickListener(onClickListener);
//        initFragment(); // savedInstanceState
//        setIconBottomStyle(0);//默认选中守护
//        SpeakingFragment.newInstance("").setAudioRecordButton(iv_bottom_talkButton);
    }

    private void showDialog(final String content) {
            final AlertDialog myDialog;
            myDialog = new AlertDialog.Builder(RegisterTwoActivity.this).create();
            myDialog.show();
            myDialog.getWindow().setContentView(R.layout.alert_fence_dialog);
            tv_alert_content = (TextView) myDialog.getWindow().findViewById(R.id.tv_alert_content);
            tv_alert_content.setText(content);
            myDialog.setView(tv_alert_content);
            myDialog.setCancelable(false);
            ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
            myDialog.getWindow().setBackgroundDrawable(dw);
            myDialog.getWindow()
                    .findViewById(R.id.btn_fence_pop_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });

    }

    private void toRegister(String username,String password,RequestCallBackEx<String> respon){ // ,String repwd
        String data = password;
//        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密
        final String encryptionData = MD5Utils.getMD5(data);

        // System.out.println("MD5加密前：" + data);
        // System.out.println("MD5加密后：" + encryptionData);
        // System.out.println("MD5解密后" + MD5Utils.DEcrypt(MD5Utils.MD5_KEY,encryptionData));

        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
        reParams.addBodyParameter("mobile", username);
        reParams.addBodyParameter("codes", mPhotoVerifiCode);
        reParams.addBodyParameter("pwd", encryptionData);

       String url = ServerConfig.USER_LOGIN;
        if (url.contains(" ")){
            if(url.substring(url.length()-1)==" "){
                url= url.substring(0,url.length()-1);
            }else{
                url= url.replace(" ","%20");
            }
        }
        if (url.contains("\"")){
            url= url.replace("\"","%22");
        }
        if (url.contains("{")){
            url= url.replace("{","%7B");
        }
        if (url.contains("}")){
            url= url.replace("{","%7D");
        }

//        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.GET, ServerConfig.mProtocol + SharedUtils.readServerIp() + ":" + ServerConfig.mTomcatPort + "/webSer/app/user/Login", reParams, respon);
//        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.GET, ServerConfig.USER_LOGIN , reParams, respon);
//        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.GET, url , reParams, respon);
        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.POST, url, reParams, respon);
    }

    private void toEmailRegister(String verificationCode,String password,RequestCallBackEx<String> respon){ // ,String repwd
        String data = password;
//        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密
        final String encryptionData = MD5Utils.getMD5(data);   // 用这种加密方法
        Log.e("MD555555", "-注册---MD5加密前：" + data);
        Log.e("MD555555", "-注册---MD5加密后：" + encryptionData);
        // System.out.println("MD5加密前：" + data);
        // System.out.println("MD5加密后：" + encryptionData);
        // System.out.println("MD5解密后" + MD5Utils.DEcrypt(MD5Utils.MD5_KEY,
        // encryptionData));

        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
//        reParams.addBodyParameter("type", "m");
        reParams.addBodyParameter("email", mRegisterEmail);
        reParams.addBodyParameter("codes", verificationCode);
        reParams.addBodyParameter("pwd", encryptionData);  // encryptionData    password

        String url = ServerConfig.EMAIL_REGISTER;
        if (url.contains(" ")){
            if(url.substring(url.length()-1)==" "){
                url= url.substring(0,url.length()-1);
            }else{
                url= url.replace(" ","%20");
            }
        }
        if (url.contains("\"")){
            url= url.replace("\"","%22");
        }
        if (url.contains("{")){
            url= url.replace("{","%7B");
        }
        if (url.contains("}")){
            url= url.replace("{","%7D");
        }

        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.POST, url, reParams, respon);
    }

    public  void parseEmailRegisterData(String content) { // , String[] errStr
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");
            if(nRetCode == 0){
                JSONObject obj = jsonObj.optJSONObject("data");
//                int mEmailVerifiCode = obj.optInt("codes");
                String userId = obj.optString("mid");
                SharedPreUtil.savePre(RegisterTwoActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id
                Toast.makeText(RegisterTwoActivity.this, "注册成功了", Toast.LENGTH_SHORT).show();

                if(handler != null){
                    handler.removeCallbacksAndMessages(null); // TODO -- 注册成功后，将handler消息置空，否则，都注册成功了还会提示，验证码过期的消息
                }

                dismissLoadingDialog();
                // TODO ---- 注册成功后，后台会返回，一些用户的默认数据（身高，体重等）---- 应该传给AddPersonaldataActivity页面 --- 再由AddPersonaldataActivity 传给 个人资料页面
                Intent intent = new Intent(RegisterTwoActivity.this, AddPersonaldataActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
//                intent.putExtra("Height",mEmailVerifiCode);
//                intent.putExtra("Weight",mRegisterEmail);
                startActivity(intent);
                finish();
            }else if(nRetCode == 1){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "验证码为空", Toast.LENGTH_SHORT).show();
                //TODO ----- 测试用
//                Intent intent = new Intent(RegisterTwoActivity.this, AddPersonaldataActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
//                startActivity(intent);
            }else if (nRetCode == 2){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "请重新申请验证码", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 3){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "该邮箱已经注册", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 4){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "验证码输入错误", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        return user;
    }

    public  void parsePhotoRegisterData(String content) { // , String[] errStr
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");
            if(nRetCode == 0){
                JSONObject obj = jsonObj.optJSONObject("data");
//                int mEmailVerifiCode = obj.optInt("codes");   RegisterTwoActivity
                String userId = obj.optString("mid");
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id
                Toast.makeText(RegisterTwoActivity.this, "注册成功了", Toast.LENGTH_SHORT).show();

                if(handler != null){
                    handler.removeCallbacksAndMessages(null); // TODO -- 注册成功后，将handler消息置空，否则，都注册成功了还会提示，验证码过期的消息
                }
                dismissLoadingDialog();
                // TODO ---- 注册成功后，后台会返回，一些用户的默认数据（身高，体重等）---- 应该传给AddPersonaldataActivity页面 --- 再由AddPersonaldataActivity 传给 个人资料页面
                Intent intent = new Intent(RegisterTwoActivity.this, AddPersonaldataActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
//                intent.putExtra("Height",mEmailVerifiCode);
//                intent.putExtra("Weight",mRegisterEmail);
                startActivity(intent);
                finish();
            }else if(nRetCode == 1){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "验证码为空", Toast.LENGTH_SHORT).show();

                //TODO ----- 测试用
//                Intent intent = new Intent(RegisterTwoActivity.this, AddPersonaldataActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
//                startActivity(intent);
            }else if(nRetCode == 2){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "请重新申请验证码(验证码从发送邮件后60秒后失效)", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 3){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "该手机号已经注册", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 4){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "验证码输入错误", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 5){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        return user;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:   // 点击注册
                if(NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())){
                    // 点击完成时--- 需要两个参数1:验证码，2：密码   ---- 需要判空处理
                    String verificationCode = et_yzm.getText().toString();
                    String password = et_password.getText().toString(); // ---- 且需要做密码长度的判断（6-20）
                    if(locale.equals("zh")) {  // 为国内
                        if(!StringUtils.isEmpty(mRegisterEmail)){ // 为邮箱注册
                            if(!StringUtils.isEmpty(verificationCode) && !StringUtils.isEmpty(password)){
                                showLoadingDialogNew("注册中");
                                toEmailRegister(verificationCode, password, new RequestCallBackEx<String>() {
                                    @Override
                                    public void onSuccessEx(ResponseInfo<String> responseInfo) {
                                        String result = responseInfo.result;
                                        parseEmailRegisterData(responseInfo.result);
                                    }

                                    @Override
                                    public void onFailure(HttpException error, String msg) {
                                        Toast.makeText(RegisterTwoActivity.this, "请求失败了", Toast.LENGTH_SHORT).show();
                                        dismissLoadingDialog();
                                    }
                                });
                            }else if(StringUtils.isEmpty(verificationCode)){
                                Toast.makeText(RegisterTwoActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                            }else if(StringUtils.isEmpty(password)){
                                Toast.makeText(RegisterTwoActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                            }
                        }else { // 手机号注册
                            if(!StringUtils.isEmpty(mUsername) && !StringUtils.isEmpty(et_yzm.getText().toString()) && !StringUtils.isEmpty(et_password.getText().toString())){
                                showLoadingDialogNew("注册中");
                                //TODO 注册成功---进入AddPersonaldataActivity页面
                                toRegister(mUsername,et_password.getText().toString(),new RequestCallBackEx<String>(){
                                    @Override
                                    public void onSuccessEx(ResponseInfo<String> responseInfo) {
                                        String  result = responseInfo.result;
//                                        Toast.makeText(RegisterTwoActivity.this, "注册成功了", Toast.LENGTH_SHORT).show();
                                        parsePhotoRegisterData(responseInfo.result);
                                    }

                                    @Override
                                    public void onFailure(HttpException error, String msg) {
                                        Toast.makeText(RegisterTwoActivity.this, "请求失败了,请稍后重试", Toast.LENGTH_SHORT).show();
                                        dismissLoadingDialog();
                                    }
                                });
                            }
                        }
                        //TODO 点击同意注册按钮时，需判断验证码和密码（1：格式是否正确，2：内容是否正确）   MyDataActivity    MyRegisterDataActivity
                    }else { // 国外   ----- 只支持邮箱注册
                        if(!StringUtils.isEmpty(mRegisterEmail)){ // 为邮箱注册
                            if(!StringUtils.isEmpty(verificationCode) && !StringUtils.isEmpty(password)){
                                showLoadingDialogNew("注册中");
                                toEmailRegister(verificationCode, password, new RequestCallBackEx<String>() {
                                    @Override
                                    public void onSuccessEx(ResponseInfo<String> responseInfo) {
                                        String result = responseInfo.result;
                                        parseEmailRegisterData(responseInfo.result);
                                    }

                                    @Override
                                    public void onFailure(HttpException error, String msg) {
                                        Toast.makeText(RegisterTwoActivity.this, "请求失败了", Toast.LENGTH_SHORT).show();
                                        dismissLoadingDialog();
                                    }
                                });
                            }else if(StringUtils.isEmpty(verificationCode)){
                                Toast.makeText(RegisterTwoActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                            }else if(StringUtils.isEmpty(password)){
                                Toast.makeText(RegisterTwoActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(RegisterTwoActivity.this, "只支持邮箱注册", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(RegisterTwoActivity.this, getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
//                    ShowMessage(getString(R.string.net_error_tip));
                }
                break;

            case R.id.back:
                // TODO 返回销毁页面时，将没有失效的验证码和倒计时时间保存
                if(!StringUtils.isEmpty(mPhotoVerifiCode) && photoseconds > 0){  // 验证码不为空时，验证码有效且倒计时时间未到
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.PHONEVERIFICODE,mPhotoVerifiCode);  // 短信验证码
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.PHONEVERIFICODECOUNTTIME, photoseconds + "");
                }
                finish();
                break;

            case R.id.btn_login_password_show_hide:
                isShowPassword = !isShowPassword;
                et_password
                        .setInputType(isShowPassword ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                : InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et_password.setSelection(et_password.getText().toString()
                        .length());
                btn_login_password_show_hide
                        .setBackgroundResource(isShowPassword ? R.drawable.display
                                : R.drawable.hide);
                break;

            case R.id.tv_serviceagreement:
                    //TODO 服务协议
                break;

            case R.id.tv_privacypolicy:
                //TODO 隐私政策
                break;

            case R.id.btn_getverificationcode:  // 获取验证码
                if(NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())){
                    if(!StringUtils.isEmpty(mRegisterEmail)) { // 为邮箱注册，点击获取邮箱验证码
                        showLoadingDialogNew("获取验证码中");
//                        String language = "zh";
                        getEmailCodeToEmailRegister(mRegisterEmail, locale, new RequestCallBackEx<String>() {
                            @Override
                            public void onSuccessEx(ResponseInfo<String> responseInfo) {
                                String result = responseInfo.result;
                                parseEmailRegisterGetCode(responseInfo.result);
                            }

                            @Override
                            public void onFailure(HttpException error, String msg) {
                                Toast.makeText(RegisterTwoActivity.this, "获取失败了,请稍后重试", Toast.LENGTH_SHORT).show();
                                dismissLoadingDialog();
                            }
                        });
                    } else {   // 为手机号注册，点击获取手机号验证码
                        showLoadingDialogNew("获取验证码中");
                        toGetPhotoVerifiCode( new RequestCallBackEx<String>() {
                            @Override
                            public void onSuccessEx(ResponseInfo<String> responseInfo) {
                                String result = responseInfo.result;
                                parsePhotoRegisterGetCode(responseInfo.result);
                            }

                            @Override
                            public void onFailure(HttpException error, String msg) {
                                Toast.makeText(RegisterTwoActivity.this, "获取失败了,请稍后重试", Toast.LENGTH_SHORT).show();
                                dismissLoadingDialog();
                            }
                        });
                    }
                }else{
                    Toast.makeText(RegisterTwoActivity.this, getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
//                    ShowMessage(getString(R.string.net_error_tip));
                }
                break;
        }
    }

    private void toGetPhotoVerifiCode(RequestCallBackEx<String> respon){ // ,String repwd
//        String data = password;
//        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密

        // System.out.println("MD5加密前：" + data);
        // System.out.println("MD5加密后：" + encryptionData);
        // System.out.println("MD5解密后" + MD5Utils.DEcrypt(MD5Utils.MD5_KEY,
        // encryptionData));

        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
        reParams.addBodyParameter("type", "m");  // 手机传m,默认为邮箱
        reParams.addBodyParameter("param", mUsername);   // 用户手机号
        reParams.addBodyParameter("reg", "");   // 默认是注册，忘记密码传true
        reParams.addBodyParameter("l", locale);  // locale为检测到的系统语音

        String url = ServerConfig.REGISTER_GETCODE;
        if (url.contains(" ")){
            if(url.substring(url.length()-1)==" "){
                url= url.substring(0,url.length()-1);
            }else{
                url= url.replace(" ","%20");
            }
        }
        if (url.contains("\"")){
            url= url.replace("\"","%22");
        }
        if (url.contains("{")){
            url= url.replace("{","%7B");
        }
        if (url.contains("}")){
            url= url.replace("{","%7D");
        }

        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.POST, url, reParams, respon);
    }

    private void getEmailCodeToEmailRegister(String email,String language,RequestCallBackEx<String> respon){ // ,String repwd
//        String data = password;
//        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密

        // System.out.println("MD5加密前：" + data);
        // System.out.println("MD5加密后：" + encryptionData);
        // System.out.println("MD5解密后" + MD5Utils.DEcrypt(MD5Utils.MD5_KEY,
        // encryptionData));

        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
//        reParams.addBodyParameter("type", "m");
        reParams.addBodyParameter("param", email);
        reParams.addBodyParameter("reg", "false");  //   reParams.addBodyParameter("reg", "false");
        reParams.addBodyParameter("l", language);

        String url = ServerConfig.REGISTER_GETCODE;
        if (url.contains(" ")){
            if(url.substring(url.length()-1)==" "){
                url= url.substring(0,url.length()-1);
            }else{
                url= url.replace(" ","%20");
            }
        }
        if (url.contains("\"")){
            url= url.replace("\"","%22");
        }
        if (url.contains("{")){
            url= url.replace("{","%7B");
        }
        if (url.contains("}")){
            url= url.replace("{","%7D");
        }

        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.POST, url, reParams, respon);
    }

    public  void parseEmailRegisterGetCode(String content) { // , String[] errStr
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");

            if(nRetCode == 0){
                JSONObject obj = jsonObj.optJSONObject("data");
//                int mEmailVerifiCode = obj.optInt("codes");
                String mEmailVerifiCode = obj.optString("codes");

                dismissLoadingDialog();

                // 点击注册第2个页面的获取按钮，获取验证码成功了，
                Calendar calendar = Calendar.getInstance();   //  long endTime;
                long currentTime = calendar.getTimeInMillis() / 1000 ;
                Log.e("MD555555", "注册第2个页面--获取到验证码时间---：" + currentTime);
//                Toast.makeText(RegisterTwoActivity.this, "注册成功了", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterTwoActivity.this, EmailVerifiCodeActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                intent.putExtra("EmailVerifiCode",mEmailVerifiCode);
                intent.putExtra("RegisterEmail",mRegisterEmail);
                intent.putExtra("CountdownStartTime",currentTime);//验证码倒计时开始时间
                startActivity(intent);
                finish();
            }else if(nRetCode == 1){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 2){
//                Toast.makeText(RegisterOneActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 3){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 4){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "获取失败了，请重试", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        return user;
    }

    public  void parsePhotoRegisterGetCode(String content) { // , String[] errStr
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");

            if(nRetCode == 0){
                JSONObject obj = jsonObj.optJSONObject("data");
//                int mEmailVerifiCode = obj.optInt("codes");
                mPhotoVerifiCode = obj.optString("codes");   // 获取到的短信验证码
                et_yzm.setText(mPhotoVerifiCode);
                photoseconds = 60;   // 获取成功了将 倒计时时间重置为 60

                btn_getverificationcode.setText(photoseconds + "s");
                btn_getverificationcode.setBackgroundResource(R.drawable.login_btn_2_p);
                new Thread()
                {
                    public void run()
                    {
                        while (photoseconds != -1)
                        {
                            photoseconds--;
                            handler.sendEmptyMessage(2);
                            try
                            {
                                sleep(1000);
                            } catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    };
                }.start();

                dismissLoadingDialog();

                // 点击注册第2个页面的获取按钮，获取验证码成功了，
               /* Calendar calendar = Calendar.getInstance();   //  long endTime;
                long currentTime = calendar.getTimeInMillis() / 1000 ;
                Log.e("MD555555", "注册第2个页面--获取到验证码时间---：" + currentTime);
//                Toast.makeText(RegisterTwoActivity.this, "注册成功了", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterTwoActivity.this, EmailVerifiCodeActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                intent.putExtra("EmailVerifiCode",mEmailVerifiCode);
                intent.putExtra("RegisterEmail",mRegisterEmail);
                intent.putExtra("CountdownStartTime",currentTime);//验证码倒计时开始时间
                startActivity(intent);
                finish();*/
            }else if(nRetCode == 1){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "传参未完整", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 2){
                Toast.makeText(RegisterTwoActivity.this, "手机号未注册", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 3){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 4){
                dismissLoadingDialog();
                Toast.makeText(RegisterTwoActivity.this, "获取失败了，请重试", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        return user;
    }

    private void showLoadingDialogNew(final String content) {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(RegisterTwoActivity.this);
//            mSpotsDialog.setWaitingTitle(getString(R.string.dialog_waitting_text));
            myLoadingDialog.setWaitingTitle(content);
        }
        myLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
            myLoadingDialog.dismiss();
        }
        myLoadingDialog = null;
    }
}
