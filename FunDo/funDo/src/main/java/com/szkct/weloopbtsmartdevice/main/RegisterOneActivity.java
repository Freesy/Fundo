package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
import com.szkct.weloopbtsmartdevice.util.PublicTools;
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
public class RegisterOneActivity extends Activity implements View.OnClickListener{

    private TextView tv_alert_content;
    private TextView btn_login;
    private ImageView back;
    private Button btn_next;
    private EditText et_phonenumber;

    private String mRegisterEmail;
    private MyLoadingDialog myLoadingDialog;
    private  String locale;

    private String code;
    private String codeCountTime;
    private int codeCountTimeI;
    private int transCountTimeI = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }

        setContentView(R.layout.activity_registerone);

        locale = Locale.getDefault().getLanguage();  //检测当前系统的语言
        Log.e("MD555555", "--当前系统的语言为---：" + locale);    // zh :

        initView();
        BTNotificationApplication.setActivity(this);
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

        et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);  //应该为输入手机号码或邮箱
      /*  et_phonenumber.addTextChangedListener(new TextWatcher() {
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
                if (StringUtils.isEmpty(et_yhm.getText().toString())) {
//                    usernameCleanButton.setVisibility(View.INVISIBLE);
                } else {
//                    usernameCleanButton.setVisibility(View.VISIBLE);
                }
//                refreshConfirm();
            }
        });*/

        et_phonenumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String username = et_phonenumber.getText().toString();
                    if (!StringUtils.isEmpty(username)) {
                        if (PublicTools.IsValidMobileNo(username) || StringUtils.isEmail(username)) {
                            et_phonenumber.setText(username);
                        } else {
                            Toast.makeText(RegisterOneActivity.this, "用户名输入内容格式不正确", Toast.LENGTH_SHORT).show();
//                        et_phonenumber.setText("");
                        }
                    } else {
                        Toast.makeText(RegisterOneActivity.this, "输入内容不能为空或空格字符", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
    }

    private void showDialog(final String content) {
            final AlertDialog myDialog;
            myDialog = new AlertDialog.Builder(RegisterOneActivity.this).create();
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

    private void getEmailCodeToEmailRegister(String email,String language,RequestCallBackEx<String> respon){ // ,String repwd
//        String data = password;
//        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密

        // System.out.println("MD5加密前：" + data);
        // System.out.println("MD5加密后：" + encryptionData);
        // System.out.println("MD5解密后" + MD5Utils.DEcrypt(MD5Utils.MD5_KEY,
        // encryptionData));

// 封装参数
        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
//        reParams.addBodyParameter("type", "m");
        reParams.addBodyParameter("param", email);
        reParams.addBodyParameter("reg", "false");
        reParams.addBodyParameter("l", language);

        String url = ServerConfig.EMAIL_REGISTER_GETCODE;
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

    /**
     * 解析登录
     *
     * @param jsonStr
     * @param retStr
     *            出参
     * @return UserModel
     */
    public  void parseEmailRegisterGetCode(String content) { // , String[] errStr
// {"flag":"Success","msg":0,"data":{"codes":"123678"}}
//        UserModel user = null;

        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");

            if(nRetCode == 0){
                JSONObject obj = jsonObj.optJSONObject("data");
//                int mEmailVerifiCode = obj.optInt("codes");
                String mEmailVerifiCode = obj.optString("codes");
//                Toast.makeText(RegisterOneActivity.this, "注册成功了", Toast.LENGTH_SHORT).show();

//                开始倒计时时间
                Calendar calendar = Calendar.getInstance();   //  long endTime;
                long currentTime = calendar.getTimeInMillis() / 1000 ;
                Log.e("MD555555", "--获取到验证码时间---：" + currentTime);

                dismissLoadingDialog();

                Intent intent = new Intent(RegisterOneActivity.this, EmailVerifiCodeActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                intent.putExtra("EmailVerifiCode",mEmailVerifiCode);
                intent.putExtra("RegisterEmail", mRegisterEmail);
                intent.putExtra("CountdownStartTime",currentTime);//验证码倒计时开始时间
                startActivity(intent);
                finish();
            }else if(nRetCode == 1){
                dismissLoadingDialog();
                Toast.makeText(RegisterOneActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 2){
//                Toast.makeText(RegisterOneActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 3){
                dismissLoadingDialog();
                Toast.makeText(RegisterOneActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 4){
                dismissLoadingDialog();
                Toast.makeText(RegisterOneActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
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
            case R.id.btn_next:
                // TODO 点击下一步时，需要判断手机号或邮箱是否正确
                if(NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())){
                    String username = et_phonenumber.getText().toString();
                    //                            还有一个国外只支持邮箱注册、登录，国内支持邮箱、手机号登录，注册只支持手机号。
                    if(locale.equals("zh")){  // 为国内
                        if (!StringUtils.isEmpty(username)) {
                            if (PublicTools.IsValidMobileNo(username)) {  //手机号注册
                                //TODO ----- 接口未ok
                                et_phonenumber.setText(username);
                                Intent intent = new Intent(this, RegisterTwoActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                                intent.putExtra("number",et_phonenumber.getText().toString());

                                if(transCountTimeI > 0){
                                    intent.putExtra("backCountTime",transCountTimeI);
                                }

                                startActivity(intent);
                            } else if(StringUtils.isEmail(username)){    //邮箱注册
                                //TODO 添加提示框---获取验证码中
                                showLoadingDialogNew("获取验证码中");
                                et_phonenumber.setText(username);
                                if(!StringUtils.isEmpty(et_phonenumber.getText().toString())){
                                    //TODO 注册成功---进入AddPersonaldataActivity页面
                                    String language = "zh";
                                    getEmailCodeToEmailRegister(et_phonenumber.getText().toString(), language, new RequestCallBackEx<String>() {
                                        @Override
                                        public void onSuccessEx(ResponseInfo<String> responseInfo) {
                                            mRegisterEmail = et_phonenumber.getText().toString();
                                            String result = responseInfo.result;
                                            parseEmailRegisterGetCode(responseInfo.result);
                                        }

                                        @Override
                                        public void onFailure(HttpException error, String msg) {
                                            dismissLoadingDialog();
                                            Toast.makeText(RegisterOneActivity.this, "请求失败了，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(RegisterOneActivity.this, "输入内容格式不正确", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterOneActivity.this, "输入内容不能为空或空格字符", Toast.LENGTH_SHORT).show();
                        }
                    }else {  // 国外 -----只支持邮箱注册登录
                        if (!StringUtils.isEmpty(username)) {
                             if(StringUtils.isEmail(username)){    //邮箱注册
                                //TODO 添加提示框---获取验证码中
                                showLoadingDialogNew("获取验证码中");
                                et_phonenumber.setText(username);
                                if(!StringUtils.isEmpty(et_phonenumber.getText().toString())){
                                    //TODO 注册成功---进入AddPersonaldataActivity页面
                                    String language = "zh";
                                    getEmailCodeToEmailRegister(et_phonenumber.getText().toString(), language, new RequestCallBackEx<String>() {
                                        @Override
                                        public void onSuccessEx(ResponseInfo<String> responseInfo) {
                                            mRegisterEmail = et_phonenumber.getText().toString();
                                            String result = responseInfo.result;
                                            parseEmailRegisterGetCode(responseInfo.result);
                                        }

                                        @Override
                                        public void onFailure(HttpException error, String msg) {
                                            dismissLoadingDialog();
                                            Toast.makeText(RegisterOneActivity.this, "请求失败了，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(RegisterOneActivity.this, "输入内容格式不正确", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterOneActivity.this, "输入内容不能为空或空格字符", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(RegisterOneActivity.this, getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
//                    ShowMessage(getString(R.string.net_error_tip));
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void showLoadingDialogNew(final String content) {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(RegisterOneActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        code = SharedPreUtil.readPre(RegisterOneActivity.this, SharedPreUtil.USER, SharedPreUtil.PHONEVERIFICODE);
        codeCountTime = SharedPreUtil.readPre(RegisterOneActivity.this, SharedPreUtil.USER, SharedPreUtil.PHONEVERIFICODECOUNTTIME);
        if(!StringUtils.isEmpty(codeCountTime)){
            codeCountTimeI = Integer.valueOf(codeCountTime);
            if(!StringUtils.isEmpty(code) && !StringUtils.isEmpty(codeCountTime)){
                new Thread()
                {
                    public void run()
                    {
                        while (codeCountTimeI != -1)
                        {
                            codeCountTimeI--;
                            handler.sendEmptyMessage(3);
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
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 3:
                    if (codeCountTimeI == 0)
                    {
                        transCountTimeI = -1;
//                        text.startAnimation(set);
//                        Toast.makeText(RegisterTwoActivity.this, "验证码已失效，请重新申请验证码", Toast.LENGTH_SHORT).show();
//                        et_yzm.setText("");
//                        mPhotoVerifiCode = "";
//                        btn_getverificationcode.setText("获取");
//                        btn_getverificationcode.setBackgroundResource(R.drawable.login_btn_2_n);
                    } else if (codeCountTimeI > 0)
                    {
                        transCountTimeI = codeCountTimeI;
//                        text.startAnimation(set);
//                        btn_getverificationcode.setText(codeCountTimeI + "s");
//                        btn_getverificationcode.setBackgroundResource(R.drawable.login_btn_2_p);
                    }
                    break;

                default:
                    break;
            }
        };
    };
}
