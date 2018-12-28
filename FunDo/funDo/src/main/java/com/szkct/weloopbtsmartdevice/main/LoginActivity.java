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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.szkct.weloopbtsmartdevice.util.PublicTools;
import com.szkct.weloopbtsmartdevice.util.RequestCallBackEx;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.SpotsDialog;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.XHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kct on 2017/2/5.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private TextView tv_alert_content, tv_tourist_login;
    private TextView btn_login, tv_register, tv_forgetpassword;
    private EditText et_yhm, et_password;
    private Button btn_login_password_show_hide;
    private boolean isShowPassword = false;

    private SpotsDialog mSpotsDialog;
    private MyLoadingDialog myLoadingDialog;
    private Toast toast = null;
    private String username;  // 登录用户名
    private String password; // 登录密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        BTNotificationApplication.setActivity(this);
    }

    public void initView() {
        tv_tourist_login = (TextView) findViewById(R.id.tv_tourist_login);
        tv_tourist_login.setOnClickListener(this);

        et_yhm = (EditText) findViewById(R.id.et_yhm);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login_password_show_hide = (Button) findViewById(R.id.btn_login_password_show_hide);
        et_yhm.setOnClickListener(this);
        et_password.setOnClickListener(this);
        btn_login_password_show_hide.setOnClickListener(this);

        et_yhm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {  // 账号输入框没有焦点时
                    et_yhm.setBackgroundResource(R.drawable.login_input_n);
                    String username = et_yhm.getText().toString();
                    if (!StringUtils.isEmpty(username)) {
                        if (PublicTools.IsValidMobileNo(username) || StringUtils.isEmail(username)) {
                            et_yhm.setText(username);
                        } else {  //
                            Toast.makeText(LoginActivity.this, getString(R.string.username_format_error), Toast.LENGTH_SHORT).show();
                            //    et_yhm.setText("");
                        }
                    } else {
                        Toast.makeText(LoginActivity.this,getString(R.string.username_is_null) , Toast.LENGTH_SHORT).show();
                    }
                } else {  // 账号输入框有焦点时
                    et_yhm.setBackgroundResource(R.drawable.login_input_s);
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
            public void afterTextChanged(Editable s) {  // 切换是否显示密码的按钮
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
                    if (StringUtils.isEmpty(et_password.getText().toString())) {
                        btn_login_password_show_hide.setVisibility(View.INVISIBLE);
                    } else {
                        btn_login_password_show_hide.setVisibility(View.VISIBLE);
                    }
                } else {
                    et_password.setBackgroundResource(R.drawable.login_input_n);
                    btn_login_password_show_hide.setVisibility(View.INVISIBLE);
                    if (StringUtils.isEmpty(et_password.getText().toString())) {
                        Toast.makeText(LoginActivity.this, getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
                    } else if (et_password.getText().toString().length() < 6 && et_password.getText().toString().length() > 20) {
                        Toast.makeText(LoginActivity.this, getString(R.string.pw_length_limit), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_login = (TextView) findViewById(R.id.btn_login);  // 登录
        btn_login.setOnClickListener(this);
        tv_register = (TextView) findViewById(R.id.tv_register); // 注册
        tv_register.setOnClickListener(this);
        tv_forgetpassword = (TextView) findViewById(R.id.tv_forgetpassword); // 忘记密码
        tv_forgetpassword.setOnClickListener(this);
    }


   /* private void showLoadingDialog(final String content) {
        final AlertDialog myDialog;
        myDialog = new AlertDialog.Builder(LoginActivity.this).create();
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.loading_dialog);
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
    }*/

    private void showDialog(final String content) {
        final AlertDialog myDialog;
        myDialog = new AlertDialog.Builder(LoginActivity.this).create();
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.alert_fence_dialog);
        tv_alert_content = (TextView) myDialog.getWindow().findViewById(R.id.tv_alert_content);
        tv_alert_content.setText(content);
        myDialog.setView(tv_alert_content);
        myDialog.setCancelable(false);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        myDialog.getWindow().setBackgroundDrawable(dw);
        myDialog.getWindow().findViewById(R.id.btn_fence_pop_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
    }


    private void touristLogin(String touristId, String type, RequestCallBackEx<String> respon) {   // 游客登录不用，请求服务器了
        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
        reParams.addQueryStringParameter("touristId", touristId);
        reParams.addQueryStringParameter("type", type);
        String url = ServerConfig.TO_LOGIN;
        if (url.contains(" ")) {
            if (url.substring(url.length() - 1) == " ") {
                url = url.substring(0, url.length() - 1);
            } else {
                url = url.replace(" ", "%20");
            }
        }
        if (url.contains("\"")) {
            url = url.replace("\"", "%22");
        }
        if (url.contains("{")) {
            url = url.replace("{", "%7B");
        }
        if (url.contains("}")) {
            url = url.replace("{", "%7D");
        }
        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.GET, url, reParams, respon);
    }

    /**
     * 手机号登录
     */
    private void toLogin(String username, String password, RequestCallBackEx<String> respon) {
        String data = password;
//        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密
        final String encryptionData = MD5Utils.getMD5(data);
        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
        reParams.addQueryStringParameter("mobile", username);
        reParams.addQueryStringParameter("pwd", encryptionData);
        String url = ServerConfig.TO_LOGIN;
        if (url.contains(" ")) {
            if (url.substring(url.length() - 1) == " ") {
                url = url.substring(0, url.length() - 1);
            } else {
                url = url.replace(" ", "%20");
            }
        }
        if (url.contains("\"")) {
            url = url.replace("\"", "%22");
        }
        if (url.contains("{")) {
            url = url.replace("{", "%7B");
        }
        if (url.contains("}")) {
            url = url.replace("{", "%7D");
        }
        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.GET, url, reParams, respon);
    }

    private void toEmailLogin(String username, String password, RequestCallBackEx<String> respon) { // ,String repwd
        String data = password;
        final String encryptionData = MD5Utils.getMD5(data);

//        Log.e("MD555555", "--登录--MD5加密前：" + data);
//        Log.e("MD555555", "--登录--MD5加密后：" + encryptionData);
//        Log.e("MD555555", "----MD5解密后:" + MD5Utils.DEcrypt(MD5Utils.MD5_KEY,encryptionData));
//         System.out.println("MD5加密前：" + data);
//         System.out.println("MD5加密后：" + encryptionData);
//         System.out.println("MD5解密后" + MD5Utils.DEcrypt(MD5Utils.MD5_KEY,encryptionData));

        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
        reParams.addQueryStringParameter("email", username);
        reParams.addQueryStringParameter("pwd", encryptionData);

        SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.USERNAME, username);  // 保存用户账号
        SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.PASSWORD, password);  // 保存用户的登录原始密码

        String url = ServerConfig.TO_LOGIN;
        if (url.contains(" ")) {
            if (url.substring(url.length() - 1) == " ") {
                url = url.substring(0, url.length() - 1);
            } else {
                url = url.replace(" ", "%20");
            }
        }
        if (url.contains("\"")) {
            url = url.replace("\"", "%22");
        }
        if (url.contains("{")) {
            url = url.replace("{", "%7B");
        }
        if (url.contains("}")) {
            url = url.replace("{", "%7D");
        }
        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.GET, url, reParams, respon);
    }

    public void parsePhoneLoginData(String content) {   // 解析手机号登录的数据
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");
            if (nRetCode == 0) {
                JSONObject obj = jsonObj.optJSONObject("data");
                String userId = obj.optString("mid");  // 当前登录用户的用户id
                String userOldId = SharedPreUtil.readPre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);// 之前登录过的保存在本地的用户id
                if (!StringUtils.isEmpty(userOldId) && userOldId.equals(userId)) { // || StringUtils.isEmpty(userOldId) 当用户id不一样时，说明是新的账号登录了
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.ISSAMEUSER, "true");  // 是同一个用户
                } else {
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.ISSAMEUSER, "false");  // 不是同一个用户  ---- 不替换当前保存在本地的用户id

                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id  ---- 替换当前保存在本地的用户id

                    String nickName = obj.optString("name");  // 当前登录用户的用户昵称
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.NAME,nickName);  // 保存用户的昵称

                    String sex = obj.optString("sex");
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.SEX,sex);
                    String height = obj.optString("height").replace("cm","");
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT,height);
                    String weight = obj.optString("weight").replace("kg","");
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.WEIGHT,weight);
                    String birth = obj.optString("birth");
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.BIRTH,birth);

                    String faceName = obj.optString("face");  // 当前登录用户的用户图片名
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.FACE,faceName);  // 保存用户的图片名
                }
              /*  SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id  ---- 替换当前保存在本地的用户id

                String nickName = obj.optString("name");  // 当前登录用户的用户昵称
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.NAME,nickName);  // 保存用户的昵称

                String faceName = obj.optString("face");  // 当前登录用户的用户图片名
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.FACE,faceName);  // 保存用户的图片名*/

                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.ISEMAILLOGIN, "false");  // 将本地的邮箱登录标志位置为false
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.ISPHONELOGIN, "true");  // TODO --- 当前为手机号登录----将本地的手机登录标志位置为true
                // TODO ---- 若当前登录的为手机号登录时，应将 ISEMAILLOGIN 邮箱登录的标志位 置为 false   登录成功后，将用户名和密码保存到本地
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.CURPHONENUM, username);  // 保存当前登录的手机号
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.CURPHONEPASSWORD, password);  // 保存登录的手机密码

                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.USERNAME, username);  // 保存当前登录的手机号 ---- 作为当前的保存的账号
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.PASSWORD, password);  // 保存登录的手机密码   ---- 作为当前的保存的账号对应的密码

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                dismissLoadingDialog();
//                intent.putExtra("EmailVerifiCode",mEmailVerifiCode);
//                intent.putExtra("RegisterEmail",mRegisterEmail);
                startActivity(intent);
                finish();
            } else if (nRetCode == 1) {
                Toast.makeText(LoginActivity.this, getString(R.string.username_or_password_null), Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            } else if (nRetCode == 2) {
                Toast.makeText(LoginActivity.this, getString(R.string.user_isnot_register), Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            } else if (nRetCode == 3) {
                Toast.makeText(LoginActivity.this, getString(R.string.request_fail), Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseEmailLoginData(String content) {  // 解析邮箱登录的数据
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");
            if (nRetCode == 0) {
                JSONObject obj = jsonObj.optJSONObject("data");
                String userId = obj.optString("mid");  // 用户id
                String userOldId = SharedPreUtil.readPre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);
                if (!StringUtils.isEmpty(userOldId) && userOldId.equals(userId)) { // 当用户id不一样时，说明时新的账号登录了
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.ISSAMEUSER, "true");  // 是同一个用户

                } else {
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.ISSAMEUSER, "false");  // 不是同一个用户
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id

                    String nickName = obj.optString("name");  // 当前登录用户的用户昵称
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.NAME,nickName);  // 保存用户的昵称

                    String sex = obj.optString("sex");
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.SEX,sex);
                    String height = obj.optString("height");
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT,height);
                    String weight = obj.optString("weight");
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.WEIGHT,weight);
                    String birth = obj.optString("birth");
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.BIRTH,birth);

                    String faceName = obj.optString("face");  // 当前登录用户的用户图片名
                    SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.FACE,faceName);  // 保存用户的图片名
                }
               /* SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id

                String nickName = obj.optString("name");  // 当前登录用户的用户昵称
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.NAME,nickName);  // 保存用户的昵称

                String faceName = obj.optString("face");  // 当前登录用户的用户图片名
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.FACE,faceName);  // 保存用户的图片名*/

                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.ISPHONELOGIN, "false");
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.ISEMAILLOGIN, "true");  // 将本地的邮箱登录标志位置为true
                // TODO ---- 若当前登录的为手机号登录时，应将 ISEMAILLOGIN 邮箱登录的标志位 置为 false      登录成功后，将用户名和密码保存到本地
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.CUREMAILNUM, username);  // 保存当前登录的邮箱号
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.CUREMAILPASSWORD, password);  // 保存登录的邮箱密码

                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.USERNAME, username);  // 保存当前登录的邮箱 ---- 作为当前的保存的账号
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.PASSWORD, password);  // 保存登录的邮箱密码   ---- 作为当前的保存的账号对应的密码

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
//                intent.putExtra("EmailVerifiCode",mEmailVerifiCode);
//                intent.putExtra("RegisterEmail",mRegisterEmail);
                startActivity(intent);
                dismissLoadingDialog();
                finish();
            } else if (nRetCode == 1) {
                Toast.makeText(LoginActivity.this, getString(R.string.username_or_password_null), Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            } else if (nRetCode == 2) {
                Toast.makeText(LoginActivity.this, getString(R.string.user_isnot_register), Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            } else if (nRetCode == 3) {
                // 请求失败时，将本地保存的用户名和密码清除
//                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.USERNAME, "");  // 保存用户账号
//                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.PASSWORD, "");  // 保存用户的登录原始密码
                Toast.makeText(LoginActivity.this, getString(R.string.request_fail), Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseTouristLoginData(String content) {
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");
            if (nRetCode == 0) {
                dismissLoadingDialog();
                Intent intent3 = new Intent(this, MainActivity.class);
                startActivity(intent3);
                finish();
            } else if (nRetCode == 1) {
                Toast.makeText(LoginActivity.this, "touristId为空", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            } else if (nRetCode == 2) {
//                Toast.makeText(RegisterOneActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
            } else if (nRetCode == 3) {
                Toast.makeText(LoginActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            } else if (nRetCode == 504) {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:  // 点击登录按钮
                if (NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())) {
                    username = et_yhm.getText().toString();
                    password = et_password.getText().toString();
                    // 国外只支持邮箱注册、登录，国内支持邮箱、手机号登录，注册只支持手机号。
                    if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                        if (PublicTools.IsValidMobileNo(username)) {  //手机号登录
                            showLoadingDialogNew(getString(R.string.dialog_waitting_text));
                            toLogin(username, password, new RequestCallBackEx<String>() {
                                @Override
                                public void onSuccessEx(ResponseInfo<String> responseInfo) {
//                                    String result = responseInfo.result;
                                    parsePhoneLoginData(responseInfo.result);  // 解析手机号登录的数据
                                }

                                @Override
                                public void onFailure(HttpException error, String msg) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.request_fail), Toast.LENGTH_SHORT).show();
                                    dismissLoadingDialog();
                                }
                            });
                        } else if (StringUtils.isEmail(username)) {    //邮箱登录
                            showLoadingDialogNew(getString(R.string.dialog_waitting_text));
                            toEmailLogin(username, password, new RequestCallBackEx<String>() {
                                @Override
                                public void onSuccessEx(ResponseInfo<String> responseInfo) {
                                    parseEmailLoginData(responseInfo.result);   // 解析邮箱登录的数据
                                }

                                @Override
                                public void onFailure(HttpException error, String msg) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.request_fail), Toast.LENGTH_SHORT).show();
                                    dismissLoadingDialog();
                                }
                            });
                        }
                    } else {
                        showDialog(getString(R.string.username_or_password_null));
                    }
                } else {
                    ShowMessage(getString(R.string.net_error_tip));
                }
                break;

            case R.id.tv_register:
                Intent intent = new Intent(this, RegisterOneActivity.class);
                startActivity(intent);
//                finish();
                break;

            case R.id.tv_forgetpassword:
                Intent intent2 = new Intent(this, FindPasswordOneActivity.class);
                startActivity(intent2);
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

            case R.id.tv_tourist_login:  // 游客登录
                // TODO  --- 游客登录时，先将本地的mid,用户名，密码清除
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, "");
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.USERNAME, "");  // 保存当前登录的邮箱 ---- 作为当前的保存的账号
                SharedPreUtil.savePre(LoginActivity.this, SharedPreUtil.USER, SharedPreUtil.PASSWORD, "");
                Intent intent3 = new Intent(this, MainActivity.class);
                startActivity(intent3);
                finish();

                /*if (NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())) {
                    showLoadingDialogNew("加载中");
                   *//* TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String touristId = TelephonyMgr.getDeviceId();
                    String type = 3 + "";
                    //TODO  游客登录不用登录到服务器了，直接进入主页。相当于原来的单机版
                    touristLogin(touristId, type, new RequestCallBackEx<String>() {
                        @Override
                        public void onSuccessEx(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            parseTouristLoginData(responseInfo.result);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Toast.makeText(LoginActivity.this, "请求失败了", Toast.LENGTH_SHORT).show();
                            dismissLoadingDialog();
                        }
                    });*//*
                    Intent intent3 = new Intent(this, MainActivity.class);
                    startActivity(intent3);
                    finish();
                } else {
                    ShowMessage(getString(R.string.net_error_tip));
                }*/
                break;
        }
    }

    private void showSpotsDialog() {
        if (mSpotsDialog == null) {
            mSpotsDialog = new SpotsDialog(LoginActivity.this);
//            mSpotsDialog.setWaitingTitle(getString(R.string.dialog_waitting_text));
        }
        mSpotsDialog.show();
    }

    private void showLoadingDialogNew(final String content) {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(LoginActivity.this);
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

    private void dismissDialog() {
        if (mSpotsDialog != null && mSpotsDialog.isShowing()) {
            mSpotsDialog.dismiss();
        }
        mSpotsDialog = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    public void ShowMessage(String text) {
        if (null == toast) {
            toast = Toast.makeText(BTNotificationApplication.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
