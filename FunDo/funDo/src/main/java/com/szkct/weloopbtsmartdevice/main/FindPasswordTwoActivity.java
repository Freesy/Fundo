package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.szkct.weloopbtsmartdevice.util.MD5Utils;
import com.szkct.weloopbtsmartdevice.util.RequestCallBackEx;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.XHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kct on 2017/2/5.
 */
public class FindPasswordTwoActivity extends Activity implements View.OnClickListener{

    private TextView tv_alert_content;
    private TextView btn_login;
    private String phoneNumber;
    private Button btn_getverificationcode,btn_ok,btn_login_password_show_hide;
    private EditText et_yzm,et_password;
    private boolean isShowPassword = false;
    private ImageView back;

    private String  mRegisterEmail;// 注册的邮箱
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }

        setContentView(R.layout.activity_findpasswordtwo);

        initView();

    }

    public void initView() { //Bundle savedInstanceState
//        btn_login = (TextView) findViewById(R.id.btn_login);

        phoneNumber = getIntent().getStringExtra("number");
        mRegisterEmail = getIntent().getStringExtra("RegisterEmail");

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        et_yzm = (EditText) findViewById(R.id.et_yzm);  // 验证码
        et_password = (EditText) findViewById(R.id.et_password); // 密码

        btn_getverificationcode = (Button) findViewById(R.id.btn_getverificationcode);//获取验证码
        btn_getverificationcode.setOnClickListener(this);
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

        btn_ok = (Button) findViewById(R.id.btn_ok);  //完成按钮
        btn_ok.setOnClickListener(this);

    }

    private void showDialog(final String content) {
            final AlertDialog myDialog;
            myDialog = new AlertDialog.Builder(FindPasswordTwoActivity.this).create();
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

    private void toEmailRegister(String verificationCode,String password,RequestCallBackEx<String> respon){ // ,String repwd
        String data = password;
        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密
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

    public  void parseEmailRegisterGetCode(String content) { // , String[] errStr
// {"flag":"Success","msg":0,"data":{"codes":"123678"}}
//        UserModel user = null;

//        {
//            "flag": "Success",
//                "msg": 0,
//                "data": {
//            "mid": 302962
//        }
//        }
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");

            if(nRetCode == 0){
                JSONObject obj = jsonObj.optJSONObject("data");
//                int mEmailVerifiCode = obj.optInt("codes");
                String mEmailVerifiCode = obj.optString("codes");
                Toast.makeText(FindPasswordTwoActivity.this, "注册成功了", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FindPasswordTwoActivity.this, EmailVerifiCodeActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                intent.putExtra("EmailVerifiCode",mEmailVerifiCode);
                intent.putExtra("RegisterEmail",mRegisterEmail);
                startActivity(intent);
            }else if(nRetCode == 1){
                Toast.makeText(FindPasswordTwoActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 2){
//                Toast.makeText(RegisterOneActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 3){
                Toast.makeText(FindPasswordTwoActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 4){
                Toast.makeText(FindPasswordTwoActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }

//            JSONObject obj = jsonObj.optJSONObject("data");
//            int mEmailVerifiCode = obj.optInt("codes");

           /* errStr[0] = jsonObj.optString("ErrorMsg");

            if (0 == nRetCode) {
                user = new UserModel();
                JSONObject jsonResult = jsonObj.getJSONObject("Result");
                user.sessionKey = jsonResult.optString("sessionKey");
                user.userType = jsonResult.optInt("userType");
                user.ddPushIP = jsonResult.optString("DDPushIP");
                user.ddPushPort = jsonResult.optInt("DDPushPort");
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        return user;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                // 点击完成时--- 需要两个参数1:验证码，2：密码   ---- 需要判空处理
                String verificationCode = et_yzm.getText().toString();
                String password = et_password.getText().toString(); // ---- 且需要做密码长度的判断（6-20）

                if(!StringUtils.isEmpty(mRegisterEmail)){ // 为邮箱注册
                    if(!StringUtils.isEmpty(verificationCode) && !StringUtils.isEmpty(password)){

                        toEmailRegister(verificationCode, password, new RequestCallBackEx<String>() {

                            @Override
                            public void onSuccessEx(ResponseInfo<String> responseInfo) {
//                                mRegisterEmail = et_phonenumber.getText().toString();
                                String result = responseInfo.result;

                                parseEmailRegisterGetCode(responseInfo.result);

//                            Intent intent = new Intent(RegisterTwoActivity.this, AddPersonaldataActivity.class);  //  ----注册页面的填写个人资料页面  MyRegisterDataActivity---直接用报错
//                            startActivity(intent);
//                            finish();
                            }

                            @Override
                            public void onFailure(HttpException error, String msg) {
                                Toast.makeText(FindPasswordTwoActivity.this, "请求失败了", Toast.LENGTH_SHORT).show();
                            }
                        });//只传了用户名和密码（重复密码和密码一样）


                        Intent intent = new Intent(this, AddPersonaldataActivity.class);//   进入填写个人资料的页面
                        startActivity(intent);
                    }else if(StringUtils.isEmpty(verificationCode)){
                        Toast.makeText(FindPasswordTwoActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    }else if(StringUtils.isEmpty(password)){
                        Toast.makeText(FindPasswordTwoActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                }else { // 手机号注册
//                    if(!StringUtils.isEmpty(verificationCode) && !StringUtils.isEmpty(password)){
//                        Intent intent = new Intent(this, AddPersonaldataActivity.class);//   进入填写个人资料的页面
//                        startActivity(intent);
//                    }
                }

//                Intent intent = new Intent(this, AddPersonaldataActivity.class);// LoginActivity  进入已发送验证码的页面  EmailVerifiCodeActivity
//                startActivity(intent);
                break;

            case R.id.btn_getverificationcode:  // 获取短信验证码

                break;

            case R.id.btn_login_password_show_hide:  //显示密码
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

            case R.id.back:
                finish();
                break;
        }
    }
}
