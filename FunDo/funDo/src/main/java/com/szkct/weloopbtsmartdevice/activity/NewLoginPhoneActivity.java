package com.szkct.weloopbtsmartdevice.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.lidroid.xutils.http.client.HttpRequest;
import com.mtk.app.applist.FileUtils;
import com.szkct.weloopbtsmartdevice.data.CityInfoModel;
import com.szkct.weloopbtsmartdevice.data.advertisingBean;
import com.szkct.weloopbtsmartdevice.login.DataError;
import com.szkct.weloopbtsmartdevice.login.Gdata;
import com.szkct.weloopbtsmartdevice.login.Logg;
import com.szkct.weloopbtsmartdevice.login.MyCallback;
import com.szkct.weloopbtsmartdevice.login.NewNetUtils;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainActivity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.MD5Utils;
import com.szkct.weloopbtsmartdevice.util.RequestCallBackEx;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.util.XHttpUtils;
import com.thefinestartist.finestwebview.FinestWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NewLoginPhoneActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = NewLoginPhoneActivity.class.getSimpleName();
    private SharedPreferences preferences;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    boolean isFirstLogin ;

    TextView tv_country;
    Integer countrynumber;//国际区域代码
    EditText et_phonenumber, et_passwd;
    LoadingDialog loadingDialog;
    String username, password;
    public final int CODE = 10086, CHECK = 10;
    public String murl = "https://mp.weixin.qq.com/s/KgcZYWvDIYa6N0rA-NsJQQ";
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
                        hc.open(NewLoginPhoneActivity.this);
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
                                new FinestWebView.Builder(NewLoginPhoneActivity.this).showIconMenu(false).show(urls);
                            } else {
                                new FinestWebView.Builder(NewLoginPhoneActivity.this).showIconMenu(false).show(Constants.CHECK_moren);
                            }
                            finish();
                        } else {
                            new FinestWebView.Builder(NewLoginPhoneActivity.this).showIconMenu(false).show(Constants.CHECK_moren);
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
        setContentView(R.layout.activity_new_login_phone);
        initview();
    }

    private void initview() {
        findViewById(R.id.bt_register_phone).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.bt_next).setOnClickListener(this);
        findViewById(R.id.tv_toforget).setOnClickListener(this);
        tv_country = (TextView) findViewById(R.id.tv_country);
        et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        initcity(UTIL.readPre(getApplicationContext(), CityInfoModel.class + "", "country"));
        findViewById(R.id.v_quhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), NewSelectCityListActivity.class), 0);
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.bt_register_phone:
                startActivity(new Intent(getApplicationContext(), NewRegisterPhoneActivity.class));
                break;
            case R.id.tv_toforget:
                startActivity(new Intent(getApplicationContext(), NewForgetPhoneActivity.class));
//                startActivity(new Intent(getApplicationContext(),NewForgetActivity.class));
//                finish();
                break;
            case R.id.bt_next:
                username = et_phonenumber.getText().toString();
                password = et_passwd.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.please_fill_complete, Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoadingDialogNew(getString(R.string.progress_dialog_title));

                HashMap map = new HashMap();
                map.put("phone", getStringPhone(countrynumber, username));
                map.put("password", MD5Utils.getMD5(password));
                String json = NewNetUtils.getPostJson(map);

                NewNetUtils.getInstance().sendPost(ServerConfig.TO_LOGIN, json, new MyCallback() {
                    @Override
                    public void mySuccess(Object responseInfo) {
                        Logg.e(TAG, "mySuccess: responseInfo=" + responseInfo);
                        String result = responseInfo.toString();
                        Gdata.setPersonData(result);
                        dismissLoadingDialog();
                        downloadImg(Gdata.getPersonData().getFace());

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
                      /*  if(Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")) {
                            startActivity(new Intent(NewLoginPhoneActivity.this, WxGuildeActivity.class));
                        }
                        finish();*/
                    }

                    @Override
                    public void myFailure(DataError error) {
                        Logg.e(TAG, "mySuccess: responseInfo=" + error.getCode()+"  message="+error.getMessage());
                        switch (error.getCode()){
                            case 2:
                            case 3:
                                Toast.makeText(getApplicationContext(), getString(R.string.email_login_fail), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), getString(R.string.request_fail), Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dismissLoadingDialog();
                    }
                });
                break;
        }
    }


    public void showLoadingDialogNew(String content) {
        loadingDialog = new LoadingDialog(this,R.style.Custom_Progress, content);
        loadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (loadingDialog == null)
            return;
        loadingDialog.cancel();
        loadingDialog = null;
    }

    /**
     * 手机号登录
     */
    private void toLogin(String username, String password, RequestCallBackEx<String> respon) {
//        String data = password;
////        final String encryptionData = MD5Utils.Encrypt(MD5Utils.MD5_KEY, data);  // MD5加密
//        final String encryptionData = MD5Utils.getMD5(data);
//        com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
//        reParams.addQueryStringParameter("mobile", username);
//        reParams.addQueryStringParameter("pwd", encryptionData);
        HashMap map = new HashMap();
        map.put("phone", username);
        map.put("password", MD5Utils.getMD5(password));
        String url = XHttpUtils.newGet(ServerConfig.TO_LOGIN, map);
        Log.e("hrj", "toLogin: " + url);
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
        XHttpUtils.getInstance().send(HttpRequest.HttpMethod.GET, url, respon);
    }

    public void parsePhoneLoginData(String content) {   // 解析手机号登录的数据
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msgCode");
            if (nRetCode == 0) {
                Gdata.setPersonData(content);
                JSONObject obj = jsonObj.optJSONObject("data");
                String userId = obj.optString("mid");  // 当前登录用户的用户id
                String userOldId = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID);// 之前登录过的保存在本地的用户id
                if (!StringUtils.isEmpty(userOldId) && userOldId.equals(userId)) { // || StringUtils.isEmpty(userOldId) 当用户id不一样时，说明是新的账号登录了
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.ISSAMEUSER, "true");  // 是同一个用户
                } else {
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.ISSAMEUSER, "false");  // 不是同一个用户  ---- 不替换当前保存在本地的用户id

                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id  ---- 替换当前保存在本地的用户id

                    Intent intent = new Intent();
                    intent.setAction(MainService.CHANGE_USER);
                    sendBroadcast(intent);

                    String nickName = obj.optString("name");  // 当前登录用户的用户昵称
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.NAME, nickName);  // 保存用户的昵称

                    String sex = obj.optString("sex");
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.SEX, sex);
                    String height = obj.optString("height").replace("cm", "");
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.HEIGHT, height);
                    String weight = obj.optString("weight").replace("kg", "");
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.WEIGHT, weight);
                    String birth = obj.optString("birth");
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.BIRTH, birth);

                    String faceName = obj.optString("face");  // 当前登录用户的用户图片名
                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE, faceName);  // 保存用户的图片名
                }
              /*  SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID, userId);  // 保存用户id  ---- 替换当前保存在本地的用户id

                String nickName = obj.optString("name");  // 当前登录用户的用户昵称
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.NAME,nickName);  // 保存用户的昵称

                String faceName = obj.optString("face");  // 当前登录用户的用户图片名
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE,faceName);  // 保存用户的图片名*/

                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.ISEMAILLOGIN, "false");  // 将本地的邮箱登录标志位置为false
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.ISPHONELOGIN, "true");  // TODO --- 当前为手机号登录----将本地的手机登录标志位置为true
                // TODO ---- 若当前登录的为手机号登录时，应将 ISEMAILLOGIN 邮箱登录的标志位 置为 false   登录成功后，将用户名和密码保存到本地
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CURPHONENUM, username);  // 保存当前登录的手机号
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CURPHONEPASSWORD, password);  // 保存登录的手机密码

                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.USERNAME, getStringPhone(countrynumber, username));  // 保存当前登录的手机号 ---- 作为当前的保存的账号
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.PASSWORD, password);  // 保存登录的手机密码   ---- 作为当前的保存的账号对应的密码

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                dismissLoadingDialog();
//                intent.putExtra("EmailVerifiCode",mEmailVerifiCode);
//                intent.putExtra("RegisterEmail",mRegisterEmail);
                startActivity(intent);
                finish();
            } else if (nRetCode == 1) {
                Toast.makeText(getApplicationContext(), getString(R.string.username_or_password_null), Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            } else if (nRetCode == 2) {
                Toast.makeText(getApplicationContext(), getString(R.string.user_isnot_register), Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            } else if (nRetCode == 3) {
                Toast.makeText(getApplicationContext(), "密码不正确", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    private void sendBroadToUpdata(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainService.ACTION_MYINFO_CHANGE);
        sendBroadcast(broadcastIntent);
    }

    public void downloadImg(String url) {
        if(TextUtils.isEmpty(url)) {
            sendBroadToUpdata();
            return;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //实例ImageRequest，并设置参数，分别为地址，响应成功监听，最大宽、高，图片质量，网络异常监听
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        String picName = "userPicture";
                        FileUtils.saveBitmap(bitmap, picName);
                        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE, picName + ".JPEG");
                        sendBroadToUpdata();
                    }
                }, 480, 480, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        sendBroadToUpdata();
                    }
                });
        //添加请求
        requestQueue.add(imageRequest);
    }


}
