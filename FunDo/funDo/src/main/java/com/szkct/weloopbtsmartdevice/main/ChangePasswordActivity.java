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
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
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
public class ChangePasswordActivity extends Activity implements View.OnClickListener{

    private TextView tv_alert_content;
    private TextView btn_login,tv_forgetpassword;
    private EditText et_password,et_password_confirm;
    private Button btn_next,btn_login_password_show_hide,rebtn_login_password_show_hide;
    private boolean isShowPassword = false;
    private boolean isShowRePassword = false;
    private ImageView back;

    private MyLoadingDialog myLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }

        setContentView(R.layout.activity_changepassword);

        initView();

    }

    public void initView() { //Bundle savedInstanceState
//        btn_login = (TextView) findViewById(R.id.btn_login);
//        btn_login.setOnClickListener(new View.OnClickListener() {  // 登录
//            @Override
//            public void onClick(View v) {
//                showDialog("用户名或密码错误");
//            }
//        });

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        tv_forgetpassword = (TextView) findViewById(R.id.tv_forgetpassword); // 忘记密码
        tv_forgetpassword.setOnClickListener(this);

        btn_login_password_show_hide = (Button) findViewById(R.id.btn_login_password_show_hide);//显示密码
        btn_login_password_show_hide.setOnClickListener(this);
        rebtn_login_password_show_hide = (Button) findViewById(R.id.rebtn_login_password_show_hide);//显示密码
        rebtn_login_password_show_hide.setOnClickListener(this);



        et_password = (EditText) findViewById(R.id.et_password);
        et_password_confirm = (EditText) findViewById(R.id.et_password_confirm);

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
                    if(et_password.getText().toString().length() < 6 || et_password.getText().toString().length() > 20){
                        Toast.makeText(ChangePasswordActivity.this, "密码长度不对", Toast.LENGTH_SHORT).show();
                    }
                    et_password.setBackgroundResource(R.drawable.login_input_n);
                    btn_login_password_show_hide.setVisibility(View.INVISIBLE);
                }
            }
        });

        et_password_confirm.addTextChangedListener(new TextWatcher() {

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
                if (StringUtils.isEmpty(et_password_confirm.getText().toString())) {
                    rebtn_login_password_show_hide.setVisibility(View.INVISIBLE);
                } else {
                    rebtn_login_password_show_hide.setVisibility(View.VISIBLE);
                }
//                refreshConfirm();
            }
        });

        et_password_confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_password_confirm.setBackgroundResource(R.drawable.login_input_s);
                    if (StringUtils.isEmpty(et_password_confirm.getText()
                            .toString())) {
                        rebtn_login_password_show_hide.setVisibility(View.INVISIBLE);
                    } else {
                        rebtn_login_password_show_hide.setVisibility(View.VISIBLE);
                    }
                } else {
                    if(et_password_confirm.getText().toString().length() < 6 || et_password_confirm.getText().toString().length() > 20){
                        Toast.makeText(ChangePasswordActivity.this, "密码长度不对", Toast.LENGTH_SHORT).show();
                    }
                    et_password_confirm.setBackgroundResource(R.drawable.login_input_n);
                    rebtn_login_password_show_hide.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void showDialog(final String content) {
            final AlertDialog myDialog;
            myDialog = new AlertDialog.Builder(ChangePasswordActivity.this).create();
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

    private void toChangePassword(String password,String repassword,String userid,RequestCallBackEx<String> respon){ // ,String repwd
//        String data = password;
//        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密

        // System.out.println("MD5加密前：" + data);
        // System.out.println("MD5加密后：" + encryptionData);
        // System.out.println("MD5解密后" + MD5Utils.DEcrypt(MD5Utils.MD5_KEY,encryptionData));

        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
        reParams.addBodyParameter("mid", userid);

        String isEmailLogin = SharedPreUtil.readPre(ChangePasswordActivity.this, SharedPreUtil.USER, SharedPreUtil.ISEMAILLOGIN);  // 本地的邮箱登录标志位
        String emailLoginPassword = SharedPreUtil.readPre(ChangePasswordActivity.this, SharedPreUtil.USER, SharedPreUtil.CUREMAILPASSWORD);  // 登录的邮箱密码

        if(!StringUtils.isEmpty(isEmailLogin) && !StringUtils.isEmpty(emailLoginPassword)){
            if(isEmailLogin.equals("true")){
                reParams.addBodyParameter("oldpwd", emailLoginPassword);   //添加邮箱登录时的旧密码
            }
        }else {  // TODO ---- 添加手机号登录的时的旧密码

        }

//        reParams.addBodyParameter("oldpwd", "123456"); // 用户旧密码怎么获取
        reParams.addBodyParameter("pwd", password);
        reParams.addBodyParameter("repwd", repassword);

        String url = ServerConfig.CHANGE_PASSWORD;
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

    public  void parseChangePasswordData(String content) { // , String[] errStr
// {"flag":"Success","msg":0,"data":{"codes":"123678"}}
//        UserModel user = null;
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");
            if(nRetCode == 0){
                JSONObject obj = jsonObj.optJSONObject("data");
                String newUserid = obj.optString("mid");
                SharedPreUtil.savePre(ChangePasswordActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, newUserid);  // 保存新的用户id
//                Log.e("MD555555", "--获取到验证码时间---：" + currentTime);

                dismissLoadingDialog();
                Toast.makeText(ChangePasswordActivity.this, "密码修改成功了", Toast.LENGTH_SHORT).show();
                finish();
            }else if(nRetCode == 1){
                dismissLoadingDialog();
                Toast.makeText(ChangePasswordActivity.this, "传入参数不完整", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 2){
                Toast.makeText(ChangePasswordActivity.this, "新密码两次不匹配", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 3){
                dismissLoadingDialog();
                Toast.makeText(ChangePasswordActivity.this, "旧密码匹配错误", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 4){
                dismissLoadingDialog();
                Toast.makeText(ChangePasswordActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
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
            case R.id.btn_next:   // 点击完成
                String pwOne = et_password.getText().toString();
                String pwTwo = et_password_confirm.getText().toString();

                if(StringUtils.isEmpty(pwOne)){
                    Toast.makeText(ChangePasswordActivity.this, "初始密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(StringUtils.isEmpty(pwTwo)){
                    Toast.makeText(ChangePasswordActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(pwOne.length() < 6 || pwOne.length() > 20){
                    Toast.makeText(ChangePasswordActivity.this, "初始密码长度不对", Toast.LENGTH_SHORT).show();
                }else if(pwTwo.length() < 6 || pwTwo.length() > 20){
                    Toast.makeText(ChangePasswordActivity.this, "确认密码长度不对", Toast.LENGTH_SHORT).show();
                }else if(!StringUtils.isEmpty(pwOne) && !StringUtils.isEmpty(pwTwo) && !pwOne.equals(pwTwo)){
                    Toast.makeText(ChangePasswordActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                }else if(!StringUtils.isEmpty(pwOne) && !StringUtils.isEmpty(pwTwo) && pwOne.equals(pwTwo)){

                    showLoadingDialogNew("修改密码中");
//                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id
                    String userId =  SharedPreUtil.readPre(ChangePasswordActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);  // 1063024
//                    int i = 6;
//                    Toast.makeText(ChangePasswordActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();

                    toChangePassword(pwOne,pwTwo,userId, new RequestCallBackEx<String>() {
                        @Override
                        public void onSuccessEx(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            parseChangePasswordData(responseInfo.result);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            dismissLoadingDialog();
                            Toast.makeText(ChangePasswordActivity.this, "请求失败了，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

//                Intent intent = new Intent(this, PersonaldataActivity.class);
//                startActivity(intent);
                finish();
                break;

            case R.id.back:
                finish();
                break;

            case R.id.tv_forgetpassword:  //忘记密码
                Intent intent = new Intent(this, FindPasswordOneActivity.class);
                startActivity(intent);
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

            case R.id.rebtn_login_password_show_hide:
                isShowRePassword = !isShowRePassword;
                et_password_confirm
                        .setInputType(isShowRePassword ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                : InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et_password_confirm.setSelection(et_password_confirm.getText().toString()
                        .length());
                rebtn_login_password_show_hide
                        .setBackgroundResource(isShowRePassword ? R.drawable.display
                                : R.drawable.hide);
                break;
        }
    }

    private void showLoadingDialogNew(final String content) {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(ChangePasswordActivity.this);
//            mSpotsDialog.setWaitingTitle(getString(R.string.dialog_waitting_text));   setWaitingTitle
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
