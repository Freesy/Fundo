package com.szkct.weloopbtsmartdevice.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.qq.e.comm.util.Md5Util;
import com.szkct.map.bean.GuangGaoBean;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.DeviceUtils;
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
import com.szkct.weloopbtsmartdevice.util.PublicTools;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.yd.base.interfaces.AdViewSpreadListener;
import com.yd.config.exception.YdError;
import com.yd.ydsdk.YdSpread;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.functions.Consumer;

/**
 * @author chendalin
 */
public class WelcomeActivity extends Activity implements SplashADListener{
    // 延迟2秒
    private static final long SPLASH_DELAY_MILLIS = 1 * 2000;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    boolean isFirstIn = false;
    private static final int GO_HOME = 1000;
    int s = 6;

    private ImageView mIm4;
    private ImageView mIm3;
    private ImageView mIm2;
    private ImageView mIm1;
    private TextView mWelcome_four_tv;
    private Button mWelcome_btn_in;
    private ImageView mWelcome_background;
    private TextView mTv_tiaoguo;
    private RelativeLayout mFl_container;
//    private FrameLayout mFl_container;
    private ImageView mIv_bg;
    private TextView mTv_time;
    private FrameLayout flRoot;

//    private Button btn_in;
    private Typeface lantinghei;
//    private ImageView im1, im2, im3,imgBackGround;
//    private TextView welcome_four_tv,tv_tiaoguo;
    int runtime = 1500;

    private MyLoadingDialog myLoadingDialog;
    private Toast toast = null;
    private String mSaveUserName;
    private String mSavePassword;

    private  SharedPreferences preferences;

    public static final String APPID = "1106588549";//todo 广告必须

    public static final String SplashPosID = "3050927903527550";//todo 广告必须
    private HTTPController hc;

    public static final int LUANCH_PAGE = 1003;
    public static final int TJ = 1004;
    public static final int DOWN_IMG = 1005;
    public static final String LUANCH_IMG_NAME = "luanch_img_name";
    private int pageId;
    private String skipPageUrl = "";
    private Handler mLuachHandler = new Handler();
//    private TextView tvTime;
    private static final String VERSION_KEY = "kct_versioncode";
    private static final String SKIP_TEXT = "点击跳过 %d";
    public boolean canJump = false;
//    private ImageView ivBg;
//    private TextView tvTime1;
//    private ViewGroup container;
    private SplashAD splashAD;
    private int duration;//广告时长
    private boolean isToMain;

    private long firstTime = 0;

//    private Timer timer2 = null;//运动暂停计时
//    private MyTask2 mTimerTask2;//运动暂停计时
//    private long count2 = 0;// 时间变量   ----- 暂停的时间累积

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	/*	if ((getIntent().getFlags()&Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)!=0){
			finish();
			return;
		}*/
        setContentView(R.layout.welcome_splash);
//        GDTAction.init(this, "yourUserActionSetID", "yourAppSecretKey"); // 第一个参数是Context上下文，第二个参数是您在DMP上获得的行为数据源ID，第三个参数是您在DMP上获得AppSecretKey
//        GDTAction.logAction(ActionType.START_APP); // 每次初始化时要上报启动行为，SDK内部会自动识别这是否为用户首次启动App并上报激活行为和启动行为
        if(!this.isTaskRoot()) {
            Intent mainIntent=getIntent();
            String action=mainIntent.getAction();
            if(mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

//        GDTAction.logAction(ActionType.START_APP); // 每次初始化时要上报启动行为，SDK内部会自动识别这是否为用户首次启动App并上报激活行为和启动行为

        initView();
        //加入权限请求 6.0及以上不会自动弹出权限框
        if (Build.VERSION.SDK_INT >= 23 && !(Build.MANUFACTURER.contains("mi"))) {
            MultPermission();
        }
        formatCountDown = getResources().getString(R.string.click_to_skip) + " %d";
        preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        isFirstIn = preferences.getBoolean("isFirstIn", false);

//        goHome(); //todo --- 不是第一次使用，直接进入 主页

//        if(isFirstIn) {    // todo --- 注释 20180205
//           return;
//        }


//        init();
//        if(!isFirstIn){
//            SharedPreferences sharedPreferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
//            Editor editor = sharedPreferences.edit();
//            editor.putBoolean("isFirstIn", true);  //  editor.putBoolean("isFirstIn", false);
//            editor.commit();
//        }

       if(!Utils.getLanguage().equals("zh")){//todo 非中文直接进入主页----------------------------
//            mIm4.setImageResource(R.drawable.welcome_background_ch);
           loadSplashAd();
        }else {
            loadLuanchPage();
        }

        /*if(!Utils.getLanguage().equals("zh")){//todo 非中文直接进入主页----------------------------
//            mIm4.setImageResource(R.drawable.welcome_background_ch);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {*/
//            loadLuanchPage();
        //}

    }

    private void initView() {

        mIm4 = (ImageView) findViewById(R.id.im4);
        mIm3 = (ImageView) findViewById(R.id.im3);
        mIm2 = (ImageView) findViewById(R.id.im2);
        mIm1 = (ImageView) findViewById(R.id.im1);
        mWelcome_four_tv = (TextView) findViewById(R.id.welcome_four_tv);
        mWelcome_btn_in = (Button) findViewById(R.id.welcome_btn_in);
        mWelcome_background = (ImageView) findViewById(R.id.welcome_background);
        mTv_tiaoguo = (TextView) findViewById(R.id.tv_tiaoguo);
        mFl_container = (RelativeLayout) findViewById(R.id.fl_container);
        mIv_bg = (ImageView) findViewById(R.id.iv_bg);
        mTv_time = (TextView) findViewById(R.id.tv_time);
//        ivBg = (ImageView) findViewById(R.id.iv_bg);
//        tvTime1 = (TextView) findViewById(R.id.tv_time);
//        container  = (ViewGroup) findViewById(R.id.fl_container);
        lantinghei = BTNotificationApplication.getInstance().lanTingThinBlackTypeface;
//        imgBackGround = (ImageView) findViewById(R.id.welcome_background);
//        btn_in = (Button) findViewById(R.id.welcome_btn_in);
//        welcome_four_tv = (TextView) findViewById(R.id.welcome_four_tv);
        mWelcome_btn_in.getBackground().setAlpha(225);
        mWelcome_btn_in.setTypeface(lantinghei);
        mWelcome_four_tv.setTypeface(lantinghei);
//        tv_tiaoguo = (TextView) findViewById(R.id.tv_tiaoguo);
        flRoot = (FrameLayout)findViewById(R.id.fl_root);



    }

    private void loadLuanchPage() {
        hc = HTTPController.getInstance();
        hc.open(this);
        if (NetWorkUtils.isConnect(this) && Utils.getLanguage().equals("zh")) {
             String uid = DeviceUtils.getUniqueId(this);//设备id
            String url = Constants.FUNDO_UNIFIED_DOMAIN_test + Constants.LAUCH_PAGE + "appName=0&uuid="+uid;//0:分动；1：分动手环，2：分动穿戴,3:funfit,4:funrun
            Log.e("a", "----------广告请求---" + url + "-----------");
            hc.getNetworkStringData(url, mHandler, LUANCH_PAGE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toMain();
                }
            }, 25000);   // 3600
//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    toMain();
//                }
//            }, 25000);    /** 延时finish,不然会点到主界面的开始 */
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    toMain();
//                }
//            }, 25000);   // 3600

//            if (null == timer2) {
//                timer2 = new Timer();
//            } else {
//                timer2.cancel();
//                timer2 = new Timer();
//            }
//            if (null == mTimerTask2) {
//                mTimerTask2 = new MyTask2();
//            } else {
//                mTimerTask2.cancel();
//                mTimerTask2 = new MyTask2();
//            }
//            timer2.schedule(mTimerTask2, 1000, 30000);
        }else{
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toMain();
                }
            }, 1600);   // 3600
        }


    }


    /**同时请求多个权限（合并结果）的情况*/
    private void MultPermission(){
        RxPermissions rxPermission = new RxPermissions(WelcomeActivity.this);
        rxPermission.requestEach(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Log.d("aaa", permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d("aaa", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d("aaa", permission.name + " is denied.");
                        }
                    }
                });


//        if (Build.VERSION.SDK_INT >= 23) {
            if(Build.MANUFACTURER.contains("mi")){
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
                int check = ContextCompat.checkSelfPermission(WelcomeActivity.this,permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                Utils. openGPS(WelcomeActivity.this);//强制开启好了
                if (check != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
                }
            }
//        }
    }

    private void init() {
        Log.e("isFirstIn  ------ ", isFirstIn + "");

        if (!isFirstIn) {
            try {
                BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
                m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                String m_szBTMAC = m_BluetoothAdapter.getAddress();   // 64:A6:51:73:D9:8C
                Log.e("m_szBTMAC", m_szBTMAC + "-");
                SharedPreUtil.savePre(WelcomeActivity.this, SharedPreUtil.USER, SharedPreUtil.MYMAC, m_szBTMAC);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        ivBg = (ImageView) findViewById(R.id.iv_bg);
//        tvTime1 = (TextView) findViewById(R.id.tv_time);
//        container  = (ViewGroup) findViewById(R.id.fl_container);

//        lantinghei = BTNotificationApplication.getInstance().lanTingThinBlackTypeface;
//        btn_in = (Button) findViewById(R.id.welcome_btn_in);
//        welcome_four_tv = (TextView) findViewById(R.id.welcome_four_tv);
//        btn_in.getBackground().setAlpha(225);
//        btn_in.setTypeface(lantinghei);
//        welcome_four_tv.setTypeface(lantinghei);

//        tv_tiaoguo = (TextView) findViewById(R.id.tv_tiaoguo);
        mTv_tiaoguo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PublicTools.isForeground(BTNotificationApplication.getInstance(), WelcomeActivity.class.getSimpleName())){
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);  // 点击开始使用按钮  ---- 去掉登录注册
                    startActivity(intent);
                    mHandler.removeCallbacksAndMessages(null);
                    finish();
                }
            }
        });

        String appStartNumStr = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.PAGE_WELCOME_STARTNUM);  //  记录APP启动次数
        if(StringUtils.isEmpty(appStartNumStr)){
            SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.PAGE_WELCOME_STARTNUM, "1");//
        }else {
            int appStartNum = Integer.valueOf(appStartNumStr);
            appStartNum++;
            SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.PAGE_WELCOME_STARTNUM, appStartNum + "");//
        }

//        startTimes++;
//        Log.e("WelcomeTimes", "Times:" + startTimes);

        //TODO --- 获取当前保存在本地的手机号，手机号对应的密码 和 邮箱号，邮箱号对应的密码   -0----- 自动登录时，使用本地保存的账号，密码等
        mSaveUserName = SharedPreUtil.readPre(WelcomeActivity.this, SharedPreUtil.USER, SharedPreUtil.USERNAME);
        mSavePassword = SharedPreUtil.readPre(WelcomeActivity.this, SharedPreUtil.USER, SharedPreUtil.PASSWORD);


//        if(Utils.getLanguage().equals("zh")){//todo 非中文直接进入主页----------------------------
////            imgBackGround.setImageResource(R.drawable.welcome_background_ch);
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }


        mWelcome_btn_in.setOnClickListener(new OnClickListener() {  //todo  ---  点击开始使用按钮
            @Override
            public void onClick(View v) {
                if(!isFirstIn){
                    SharedPreferences sharedPreferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
                    Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirstIn", true);  //  editor.putBoolean("isFirstIn", false);
                    editor.commit();
                }

                if (!StringUtils.isEmpty(mSaveUserName) && !StringUtils.isEmpty(mSavePassword)) {  // 登录过（有账号登录过，且本地保存有账号信息）
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                    startActivity(intent);
                    finish();
                } else {
                    //TODO   ------- 没有账号登录过，直接进入登录页面
//                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);  // 点击开始使用按钮
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);  // 点击开始使用按钮  ---- 去掉登录注册
                    startActivity(intent);
                    finish();
                }
            }
        });

        welanimator(mIm1, runtime, alistener);
        mHandler.sendEmptyMessageDelayed(1002, 3000);  // 发送动画延时的消息      mHandler.sendEmptyMessageDelayed(1002, 1970)

        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
//        if (!isFirstIn) {
//            // 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
//            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//        } else {
//            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
//        }
    }

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case GO_HOME:     // TODO --- 无效代码（没有用到）
//                    goHome();
//                    break;

                case 1002:   // 动画延时的消息

                    //todo 加载启动图
                    loadLuanchPage();

//                    if(!PublicTools.isForeground(BTNotificationApplication.getInstance(),WelcomeActivity.class.getSimpleName())){
//                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
//                        startActivity(intent);
//                        finish();
//                    }
                    break;
//todo---------------------------------广告部分----------------------------
                case LUANCH_PAGE:
					boolean isToHome = true;
                    try {
                    String result = msg.obj.toString();
                        Log.e("a", "----------" + result + "-----------");
                        BTNotificationApplication.getInstance().setGuangGaoBean(result);
                        //这里就不做判空了，有异常直接捕获
                        JSONObject jo = new JSONObject(result);
                        int code = jo.getInt("code");
                        if(code == 0){
                            JSONArray ja = jo.getJSONArray("data");
                                if(ja.length() <= 0) {//当数据为空时直接进入主页
                                    toMain();
                                    return;
                                }
                            for (int i =0; i < ja.length(); i++){
                                JSONObject jsonObject = ja.getJSONObject(i);
                                if(jsonObject.has("location0")){
                                    JSONObject location0 = jsonObject.getJSONObject("location0");
                                    GuangGaoBean locationBean = new Gson().fromJson(location0.toString(), GuangGaoBean.class);
                                    int operationPosition = locationBean.getOperationPosition();
                                    if (operationPosition == 0) {
                                        //todo 说明开屏页有广告,然后取得广告类型==》dataType 数据类型：0：sdk； 1：自定义
                                        int dataType = locationBean.getDataType();
                                        String status1 = locationBean.getStatus();
                                        if (dataType == 0 && status1.equals("1")) {
                                            //todo 第三方广告
                                            parseGuideJson(locationBean);
                                            isToHome = false;   // todo  --- add 20180630
                                        } else if (dataType == 1) {
                                            //todo 自己服务器广告
                                            String status = locationBean.getStatus();
                                            if (status.equals("1")) {//todo 0：关闭，1：开启
                                                //todo mobileSystem投放手机系统 0:所有，1：安卓， 2：ios
                                                int mobileSystem = locationBean.getMobileSystem();
                                                //todo country投放国家：0：所有，1：国内，2：国外
                                                int country = locationBean.getCountry();
                                                if ((mobileSystem == 0 || mobileSystem == 1) && (country == 0 || country == 1)) {
                                                    parseLuachJson(locationBean);
													isToHome = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e("a", "----------" + e.toString() + "-----------");
                 
                    } finally {
						if(isToHome){
						 mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toMain();
                            }
                        }, 1000);
						}
                    }

                    break;
                case TJ://todo 统计
                    String s = msg.obj.toString();
                    Log.e("a", "----------统计" + s + "-----------");
                    break;
                case DOWN_IMG://todo 下载启动图
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        mIv_bg.setImageBitmap(bitmap);
                        mIv_bg.setVisibility(View.VISIBLE);
                        //开始倒计时
                        mLuachHandler.post(new CountThread(duration));
                    }else{
                        startHomeActivity();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void parseLuachJson(GuangGaoBean bean) {
        String pageUrl = bean.getPageEntry();
        skipPageUrl = bean.getDetailPageUrl();
        pageId = bean.getId();
        duration = bean.getDuration();
        hc.downloadImage(pageUrl, Md5Util.encode(pageUrl), mHandler, DOWN_IMG);
    }

    private void parseGuideJson(GuangGaoBean locationBean) {
        duration = locationBean.getDuration();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
// 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAndRequestPermission();
                } else {
                    // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
                    fetchSplashAD(WelcomeActivity.this, mFl_container, mTv_time, APPID, SplashPosID, WelcomeActivity.this, duration);
                }
            }
        }, 1000);

    }

    /**
     * ----------非常重要----------
     * <p>
     * Android6.0以上的权限适配简单示例：
     * <p>
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广点通SDK，否则广点通SDK不会工作。
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int writeSdCardPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            writeSdCardPermission =  checkSelfPermission(Manifest.permission.WRITE_CALL_LOG); // == PackageManager.PERMISSION_GRANTED;

            if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
            }

            if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }else{
//            writeSdCardPermission = PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG); //  == PermissionChecker.PERMISSION_GRANTED;

            if (!(PermissionChecker.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
            }

            if (!(PermissionChecker.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!(PermissionChecker.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      /*  if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }*/

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            fetchSplashAD(this, mFl_container, mTv_time, APPID, SplashPosID, this, duration);
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
     * @param appId         应用ID
     * @param posId         广告位ID
     * @param adListener    广告状态监听器
     * @param fetchDelay    拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }

    private void goHome() {
//    	if (isFirstIn){      // todo --- 注释 20180205
            if(!PublicTools.isForeground(BTNotificationApplication.getInstance(),WelcomeActivity.class.getSimpleName())){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
//    	}
    }

    public void welanimator(View view, long duration,
                            Animator.AnimatorListener listener) {
        PropertyValuesHolder enlarge = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.05f);
        PropertyValuesHolder enlarge2 = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.05f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, enlarge, enlarge2).setDuration(duration);
        animator.addListener(listener);
        animator.start();
    }

    public void welanimator2(View view, long duration, Animator.AnimatorListener listener) {
        PropertyValuesHolder enlarge = PropertyValuesHolder.ofFloat("scaleX", 1.05f, 1.1f);
        PropertyValuesHolder enlarge2 = PropertyValuesHolder.ofFloat("scaleY", 1.05f, 1.1f);
        PropertyValuesHolder transparent = PropertyValuesHolder.ofFloat("alpha", 1f, 0);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, enlarge, enlarge2, transparent).setDuration(duration);
        animator.addListener(listener);
        animator.start();
    }

    public void welanimator3(View view, long duration, Animator.AnimatorListener listener) {
        PropertyValuesHolder enlarge = PropertyValuesHolder.ofFloat("scaleX", 1.1f, 1f);
        PropertyValuesHolder enlarge2 = PropertyValuesHolder.ofFloat("scaleY", 1.1f, 1f);
        PropertyValuesHolder transparent = PropertyValuesHolder.ofFloat("alpha", 0, 1f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, enlarge, enlarge2, transparent).setDuration(duration);
        animator.start();
    }

    AnimatorListener alistener = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (s % 6) {
                case 0:
                    welanimator2(mIm1, runtime, alistener);
                    welanimator3(mIm2, 1, alistener);
                    welanimator3(mIm3, 1, alistener);
                    break;
                case 1:
                    welanimator(mIm2, runtime, alistener);
                    break;
                case 2:
                    welanimator2(mIm2, runtime, alistener);
                    break;
                case 3:
                    welanimator(mIm3, runtime, alistener);
                    break;
                case 4:
                    welanimator2(mIm3, runtime, alistener);
                    break;
                case 5:
                    welanimator3(mIm1, 1, alistener);
                    welanimator(mIm1, runtime, alistener);
                    break;
                default:
                    break;
            }
            s++;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }
    };

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("WelcomeActivity");
        if(isToMain){
            toMain();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("WelcomeActivity");
    }

    //todo -------------------------第三方广告----------------------
    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
        mTv_time.setVisibility(View.VISIBLE);
        flRoot.setVisibility(View.INVISIBLE);
        mIv_bg.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
    }

    @Override
    public void onADClicked() {
        Log.i("AD_DEMO", "SplashADClicked");
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
        Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
        mTv_time.setText(String.format(Locale.ENGLISH,SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
//        next();
        toMain();
    }

    @Override
    public void onNoAD(AdError error) {
        Log.i(
                "AD_DEMO",
                String.format(Locale.ENGLISH,"LoadSplashADFail, eCode=%d, errorMsg=%s", error.getErrorCode(),
                        error.getErrorMsg()));
        /** 如果加载广告失败，则直接跳转 */
        startHomeActivity();
    }

    public void toMain(){
        mLuachHandler.removeCallbacksAndMessages(null);
        startHomeActivity();
    }

    public void startHomeActivity() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        this.finish();

       /* int currentVersion = info.versionCode;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = prefs.getInt(VERSION_KEY, 0);
        if (currentVersion > lastVersion) {
            //如果当前版本大于上次版本，该版本属于第一次启动
//            startActivity(new Intent(getApplicationContext(), GuideActivity.class));
            //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
            prefs.edit().putInt(VERSION_KEY, currentVersion).commit();
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        this.finish();*/
    }

    private void next() {
        if (canJump) {
            startHomeActivity();
        } else {
            canJump = true;
        }
    }

    //倒计时
    class CountThread implements Runnable {
        int duration = 3;

        public CountThread(int duration){
            if(duration == 0)return;
            this.duration = duration;
        }
        public CountThread(){
        }
        @Override
        public void run() {
            mTv_time.setText("点击跳过"+duration+"s");
            mTv_time.setVisibility(View.VISIBLE);
            duration--;
            if(duration < 0){
                toMain();
                return;
            }

            mLuachHandler.postDelayed(this, 1000);
        }
    }


    public void onToMain(View view){
        toMain();
    }

    public void onLuanchClick(View view) {
        //点击启动图移除消息
        mLuachHandler.removeCallbacksAndMessages(null);
        isToMain = true;
        //点击启动图，进行统计
        //页面统计
        if(pageId == -1 || TextUtils.isEmpty(skipPageUrl))return;
        String uid = DeviceUtils.getUniqueId(this);//设备id
        String tjUrl = Constants.FUNDO_UNIFIED_DOMAIN_test + Constants.TJ + "pageId=" + pageId + "&uuid=" + uid + "&mobileSystem=1";
        hc.getNetworkStringData(tjUrl, mHandler, TJ);
        Uri uri = Uri.parse(skipPageUrl);
        Intent in = new Intent(Intent.ACTION_VIEW, uri);
        this.startActivity(in);
    }

    /*private void stopTimer2() {
//        isStop = false;
        if (null != timer2) {
            timer2.cancel();
            timer2 = null;
        }

        if (null != mTimerTask2) {
            mTimerTask2.cancel();
            mTimerTask2 = null;
        }
    }*/

  /*  class MyTask2 extends TimerTask {    // todo --
        @Override
        public void run() {
            count2++;
            int totalSec2 = 0;
            int yunshu2 = 0;
            totalSec2 = (int) (count2 / 60);
            yunshu2 = (int) (count2 % 60);
            int mai2 = totalSec2 / 60;
            int sec2 = totalSec2 % 60;
            try {
//                shijian2 = String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", mai2, sec2, yunshu2);   // 暂停时的时间长度
//                Log.e(TAG, "暂停时长 = " + shijian2);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            handler.sendEmptyMessage(2);
        }
    }*/

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (isUploading) {
//                Toast.makeText(context, R.string.dfu_status_uploading, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//            finish();

            return true;
        }
        return true;
    }*/

    /**
     * TODO --- 返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            long secondTime = System.currentTimeMillis();
            // 如果两次按键时间间隔大于2秒，则不退出
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, R.string.quit_app, Toast.LENGTH_SHORT).show();
                // 更新firstTime
                firstTime = secondTime;
                return true;
                // 两次按键小于2秒时，退出应用
            } else {
//                finish();
                SharedPreferences sharedPreferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstIn", false);  //  todo --- 退出app时，将isFirstIn 置为false
                editor.commit();
                System.exit(0);
            }
        }
//        return super.onKeyUp(keyCode, event);
        return true;
    }


    private YdSpread ydSpread;
    private View.OnClickListener listener;
    CountDownTimer countDownTimer;
    private boolean isCancel; //是否取消后续动作
    private boolean isAdReturn; //广告是否返回
    private boolean isCountDownStart; //计时器是否开始
    private String formatCountDown;

    private void loadSplashAd() {
        //设置底部logo图片
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdReturn) { //3秒后广告回来了，启动倒计时
                    countDown();
                } else { //广告没回来，直接跳到主页
                    jumpToMain();
                }
            }
        }, 5000);

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doJump();
            }
        };
        ydSpread = new YdSpread.Builder(this)
                .setKey("fundo_android_splash")
//                .setSkipView(tvTime)
                .setSkipOnClickListener(listener)
                .setContainer(mFl_container)
                .setSpreadListener(new AdViewSpreadListener() {    // AdViewSpreadListener
                    @Override
                    public void onAdDisplay() {
                        //国外广告-展示
                        MobclickAgent.onEvent(WelcomeActivity.this, Constants.TJ_AD_YQ_PAGE_SHOW);
                        //todo 广告成功返回并展示在当前页， 如果使用自定义跳过按钮，在这里设置visiable
                        Log.e("aaa", "----------国外广告---onAdDisplay-----------");
                        isAdReturn = true;
//                        splashHolder.setVisibility(View.GONE);
//                        tvTime.setVisibility(View.VISIBLE);
                        countDown();
                    }

                    @Override
                    public void onAdClose() {
                        Log.e("aaa", "----------国外广告---onAdFailed-----------");
                        jumpToMain();
                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        //云晴--加载失败
                        MobclickAgent.onEvent(WelcomeActivity.this, Constants.TJ_AD_YQ_LOAD_FALURE);
                        //todo 广告异常，失败，中断会调用
                        Log.e("aaa", "----------国外广告---onAdFailed-----------");
                        jumpToMain();
                    }

                    @Override
                    public void onAdClick() {
                        Log.e("aaa", "----------国外广告---onAdClick-----------");
                        //todo 广告被点击的监听
                        MobclickAgent.onEvent(WelcomeActivity.this, Constants.TJ_AD_YQ_CLICK);
                        isCancel = true;
                    }

                })
                .build();
        ydSpread.requestSpread();
    }

    private void doJump() {
        isCancel = true;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void jumpToMain() {
        if (!isCancel) {
            doJump();
        }
    }

    private void countDown() {
        if (!isCountDownStart) {
//            tvTime.setVisibility(View.VISIBLE);
            countDownTimer = new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
//                    tvTime.setText(String.format(formatCountDown,(millisUntilFinished + 1000) / 1000));
                }

                @Override
                public void onFinish() {
                    jumpToMain();
                }
            };
            countDownTimer.start();
            isCountDownStart = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mLuachHandler.removeCallbacksAndMessages(null);
        if (ydSpread != null) {
            ydSpread.destroy();
        }
    }
}
