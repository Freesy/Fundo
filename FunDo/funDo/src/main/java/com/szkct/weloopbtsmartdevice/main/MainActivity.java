package com.szkct.weloopbtsmartdevice.main;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.mtk.app.applist.FileUtils;
import com.szkct.bluetoothgyl.Bluttoothbroadcast;
import com.szkct.map.service.SportService;
import com.szkct.receiver.DataAndTimeReceiver;
import com.szkct.takephoto.uitl.IntentUtils;
import com.szkct.weloopbtsmartdevice.data.BaseEntity;
import com.szkct.weloopbtsmartdevice.data.ImageViewListItem;
import com.szkct.weloopbtsmartdevice.data.User;
import com.szkct.weloopbtsmartdevice.data.WatchInfoData;
import com.szkct.weloopbtsmartdevice.login.Logg;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.HttpToService;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.net.RetrofitFactory;
import com.szkct.weloopbtsmartdevice.util.AppActivitysLifecycleCallback;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.DailogUtils;
import com.szkct.weloopbtsmartdevice.util.DeviceUtils;
import com.szkct.weloopbtsmartdevice.util.DownloadService;
import com.szkct.weloopbtsmartdevice.util.JobSchedulerManager;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.OSUtils;
import com.szkct.weloopbtsmartdevice.util.ScreenManager;
import com.szkct.weloopbtsmartdevice.util.ScreenReceiverUtil;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.AnalysisFragment;
import com.szkct.weloopbtsmartdevice.view.HealthFragment;
import com.szkct.weloopbtsmartdevice.view.HelperFragment;
import com.szkct.weloopbtsmartdevice.view.HomeFragment;
import com.szkct.weloopbtsmartdevice.view.PushdialFragment;
import com.szkct.weloopbtsmartdevice.view.SettingFragment;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.model.UpdateBean;

import static com.szkct.weloopbtsmartdevice.main.MainService.AUTO_CONNECT;
import static com.szkct.weloopbtsmartdevice.util.SystemUtils.getVersionName;

/**
 * @author chendalin 说明：ActionBarActivit对于最新的sdk
 *         20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 */
public class MainActivity extends FragmentActivity implements OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LOG_TAG = "MainActivity";
    private static final String PUSHIN_STRING = "puin";
    private static final String PUSHOUT_STRING = "pout";
    private static final int CHECK_UPDATE = 8;
    //    public static boolean isUpDateFlag = true;
    public static boolean isUpDateFlagForMain = false;
    public static MainActivity mMainActivity = null;

    //private Toolbar toolbar;
//	private NavigationView mNavigationView;
    private RelativeLayout mRelativeLayout;
    private DrawerLayout mDrawerLayout;
    private TextView deviceName;
    //	private ImageView headimg;
//	private TextView tvUserName,tvUserEmail;
    private View content;
    // 点击两次退出应用程序的第一个时间
    private long firstTime = 0;
    private SetthemeBroadcast stb;
    private Bluttoothbroadcast blutbroadcast;
    private Musicbroadcast musicbroadcast;

    public User user = null;
    private final int ImgWhat = 0;
    private final int UPDATE_APP = 1;
    private final int trajectiryshow = 2;
    private Boolean isdiscover = true;
    private Boolean ishelper = true;
    boolean isFirstReadHelp = false;
    private String mLogPath;
    private String logFileName = "FunFit_Log.txt";

    private List<ImageViewListItem> mData = new ArrayList<ImageViewListItem>();
    /**
     * 将action bar和drawerlayout绑定的组件
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**** mtk add *****/
    private static final String FOTA_TAG = "[FOTA_UPDATE][MainActivity]";

    public static final Intent ACCESSIBILITY_INTENT = new Intent(
            "android.settings.ACCESSIBILITY_SETTINGS");

    public static final Intent NOTIFICATION_LISTENER_INTENT = new Intent(
            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
    public Context mContext;
    private FragmentManager fragmentManager;
    private AnalysisFragment analysisFragment;       // 大数据					--- 分析
    private HomeFragment homeFragment;    // 					主Fragment ---- 运动，睡眠
    //private DiscoverFragment discoverFragment;
    private HelperFragment helperFragment;     // 手表助手  // 手表助手(更多)   ---- 更多
    private SettingFragment settingFragment;   // 我的   // 我的Fragment			--- 我的
    private HealthFragment healthfragment;     // 健康							--- 报告
    private PushdialFragment pushdialFragment;   // 表盘推送
    boolean inpush = false;
    private RadioButton homeBtn, helpBtn, bigdataBtn, bleServiceBtn, healthBtn;
    public static int analysisfragmentchange = 0;

    private PendingIntent pendingIntent;

    private static final String SHAREDPREFERENCES_NAME = "first_pref";

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;

    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;

    public static boolean isScreenOn = true;
    private HTTPController hc;

    private UpdateBean updateBean;
    public static final String UPDATE_BEAN = "update_bean";

    private DataAndTimeReceiver dataAndTimeReceiver;  // todo --- 动态注册广播

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(null!=intent&&null!=intent. getStringExtra("Notification")){

            /** 点击通知,如果当前的Activity是OutdoorRunActitivy,则跳到它那里 */
            if(AppActivitysLifecycleCallback.getLastActivity() != null){
                if(AppActivitysLifecycleCallback.getLastActivity().getLocalClassName().equals(OutdoorRunActitivy.class.getName())){
                    Intent intentTemp =new Intent(this, OutdoorRunActitivy.class);
                    intentTemp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentTemp);
                    return;
                }
            }

            /*if (ishelper) {
                if (!helperFragment.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.container, helperFragment, "helperFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions();
                }
                fragmentManager.beginTransaction().show(helperFragment).hide(analysisFragment).hide(homeFragment).hide(healthfragment).hide(pushdialFragment)
                        .hide(settingFragment).commitAllowingStateLoss();
            } else {
                if (!pushdialFragment.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.container, pushdialFragment, "pushdialFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions();
                }
                fragmentManager.beginTransaction().show(pushdialFragment).hide(analysisFragment).hide(homeFragment).hide(healthfragment).hide(helperFragment)
                        .hide(settingFragment).commitAllowingStateLoss();
                inpush = true;
                if (MainService.getInstance().getState() == 3) {
                    MainService.getInstance().sendMessage(PUSHIN_STRING);
                }
            }
            homeBtn.setChecked(false);
            helpBtn.setChecked(false);
            bigdataBtn.setChecked(false);
            bleServiceBtn.setChecked(true);
            healthBtn.setChecked(false);*/
        }
    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ImgWhat:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    FileUtils.saveBitmap(bitmap, FileUtils.SDPATH + SharedPreUtil.readPre(MainActivity.this, SharedPreUtil.USER, SharedPreUtil.FACE));
                    //headimg.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));
                    break;
                case UPDATE_APP:
                    if(Utils.isActivityRunning(MainActivity.this,MainActivity.class.getName())){
                        DailogUtils.doNewVersionUpdate(MainActivity.this);
                    }
                    break;
                case 4:      // TODO --- 通过发消息显示表盘推送页面
                    ishelper = false;
                    if (!pushdialFragment.isAdded()) {
                        fragmentManager.beginTransaction().add(R.id.container, pushdialFragment, "pushdialFragment").commitAllowingStateLoss();
                    }
                    fragmentManager.beginTransaction().show(pushdialFragment).hide(homeFragment)
                            .hide(settingFragment).hide(analysisFragment).hide(helperFragment).hide(healthfragment).commitAllowingStateLoss();
                    inpush = true;
                    if (MainService.getInstance().getState() == 3) {
                        MainService.getInstance().sendMessage(PUSHIN_STRING);  // TODO  -------   ????

//						String keyValue = position + "";
//						byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.DIAL_REQUEST,keyValue.getBytes());  //   04 4E -- 表盘推送，发送对应的表盘序号
//						MainService.getInstance().writeToDevice(l2, true);
                    }
                    break;
                case 5:
                    ishelper = true;
                    if (!helperFragment.isAdded()) {
                        fragmentManager.beginTransaction().add(R.id.container, helperFragment, "helperFragment").commitAllowingStateLoss();
                    }
                    inpush = false;
                    if (MainService.getInstance().getState() == 3) {
                        MainService.getInstance().sendMessage(PUSHOUT_STRING);
                    }
                    fragmentManager.beginTransaction().show(helperFragment).hide(homeFragment).hide(healthfragment)
                            .hide(settingFragment).hide(analysisFragment).hide(pushdialFragment).commitAllowingStateLoss();
                    break;

                case 6:
                    fragmentManager.beginTransaction().remove(healthfragment).commitAllowingStateLoss();
                    healthfragment = new HealthFragment();
                    fragmentManager.beginTransaction().add(R.id.container, healthfragment, "healthfragment").commitAllowingStateLoss();
                    break;
                case 7:
                    if (!analysisFragment.isAdded()) {
                        fragmentManager.beginTransaction().add(R.id.container, analysisFragment, "analysisFragment").commitAllowingStateLoss();
                    }

                    fragmentManager.beginTransaction().show(analysisFragment).hide(homeFragment)
                            .hide(settingFragment).hide(pushdialFragment).hide(helperFragment).hide(healthfragment).commitAllowingStateLoss();
                    homeBtn.setChecked(false);
                    helpBtn.setChecked(false);
                    bigdataBtn.setChecked(true);
                    bleServiceBtn.setChecked(false);
                    healthBtn.setChecked(false);
                    analysisfragmentchange = Utils.toint(msg.obj.toString());
                    if (analysisFragment != null) {
                        analysisFragment.showdata(analysisfragmentchange);
                    }

                    break;
                case CHECK_UPDATE://todo 检查强制更新的接口响应
                    updateRespose(msg.obj.toString());
                Log.e(LOG_TAG, msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    //接收Service发过来的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
    }

    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();

            isScreenOn = true;
        }

        @Override
        public void onSreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            mScreenManager.startActivity();

            isScreenOn = false;
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        registerBroad();
        //logFileSize();
        EventBus.getDefault().register(this);

        hc = HTTPController.getInstance();
        hc.open(this);
        checkUpdate();

        if(Utils.isServiceRunning(this,"com.szkct.map.service.SportService")){  //判断运动模式是否在进行，是的话跳转到运动模式界面
            startActivity(new Intent(this,OutdoorRunActitivy.class));
        }

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 18) {
            if (!MainService.isNotificationReceiverActived()) {
                showAccessibilityPrompt();
            }
        } else {
            if (!Utils.isEnabled(MainActivity.this)) {
                showNotifiListnerPrompt();
            }
        }

        /////////////////////////////////////////////////////////////////////   todo --- add 20171130
        //  注册锁屏广播监听器
        mScreenListener = new ScreenReceiverUtil(this);
        mScreenManager = ScreenManager.getScreenManagerInstance(this);
        mScreenListener.setScreenReceiverListener(mScreenListenerer);

        if(currentapiVersion >= 21){ // 21
            mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
            mJobManager.startJobScheduler();     // TODO --- 此方法不支持 5.0以下系统
        }

        /////////////////////////////////////////////////////////////////////////////

        //判断是否安卓谷歌音乐播放器。添加定时跳转。手表断传来指令跳转
        String packageName = "com.google.android.music";
        // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
        String className = "com.android.music.activitymanagement.TopLevelActivity";
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        Log.e("startActivity", "className" + className + "packageName" + packageName);
        // 设置ComponentName参数1:packagename参数2:MainActivity路径
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BTNotificationApplication.setActivity(this);
        //允许设置弹出框
//        if(Build.MANUFACTURER.contains("mi")){
//            if(Build.VERSION.SDK_INT >= 23){
//                if (!Settings.canDrawOverlays(MainActivity.this)) {
//                    Intent intentpackage = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                            Uri.parse("package:" + getPackageName()));
//                    startActivityForResult(intentpackage,0);
//                }
//            }
//        }

        isUpDateFlagForMain = false;

//        请求适配型号接口
//        调用机制：
//        ①app上线前，开发过程拉取服务器所有的适配信息，保存到代码文件中。（此过程一定要在开发阶段执行，否则用户端会一次拉取大量数据）
//        ②app上线后，每次打开app，调用此接口。（仅在app打开时调用。）
//        接口地址：fundo/adaptiveFromAPP/request.do?
//        appName	是	Int	0:分动；5：分动圈
//        systemType	是	int	安卓：1；ios：2
//        appVersion	是	String	App版本号
//        uuid	是	String	手机唯一标识
//        updateTimes	是	String	适配信息最新的更新时间
        String uid = DeviceUtils.getUniqueId(this);
        HashMap<String,Object> map = new HashMap<>();
        map.put("appName",0);
        map.put("systemType",1);
        map.put("appVersion",getVersionName());
        map.put("uuid",uid);
        map.put("updateTimes","2015-07-17 09:35:00");
        RetrofitFactory.getInstance().getModelAdaptions(map)  // Base64Utils.getBase64(json.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseEntity<List<WatchInfoData>>>() {
                    @Override
                    public void accept(BaseEntity<List<WatchInfoData>> watchInfoDataBaseEntity) throws Exception {
                        Log.d("[MainActivity]",watchInfoDataBaseEntity.toString());
                        if(watchInfoDataBaseEntity.isSuccess()){
                            DBHelper db = DBHelper.getInstance(mContext);
                            db.saveWatchInfoDataList(watchInfoDataBaseEntity.getData());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if(throwable != null && throwable.getMessage() != null) {
                            Log.e("[MainActivity]", throwable.getMessage());
                        }
                    }
                });

		dataAndTimeReceiver = new DataAndTimeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		registerReceiver(dataAndTimeReceiver, filter);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(this)) {
//                                    Toast.makeText(context, R.string.xuanfukuang, Toast.LENGTH_SHORT).show();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(R.string.xuanfukuang)
                        .setNegativeButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                        startActivity(intent);
                                    }
                                });

                builder.show();
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    private boolean isPermission() {
        Logg.e(TAG, "isPermission: ");
        for (int i = 0; i < All_PERMISION.length; i++) {

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            int writeSdCardPermission;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(All_PERMISION[i]) != PackageManager.PERMISSION_GRANTED) {
                    Logg.e(TAG, "isPermission: " + All_PERMISION[i]);
                    return false;
                }
//                writeSdCardPermission =  mContext.checkSelfPermission(Manifest.permission.WRITE_CALL_LOG); // == PackageManager.PERMISSION_GRANTED;
            }else{
//                if (PermissionChecker.checkSelfPermission(All_PERMISION[i]) != PackageManager.PERMISSION_GRANTED) {
//                    Logg.e(TAG, "isPermission: " + All_PERMISION[i]);
//                    return false;
//                }
                return true;
//                writeSdCardPermission = PermissionChecker.checkSelfPermission(mContext, Manifest.permission.WRITE_CALL_LOG); //  == PermissionChecker.PERMISSION_GRANTED;
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//            if (checkSelfPermission(All_PERMISION[i]) != PackageManager.PERMISSION_GRANTED) {
//                Logg.e(TAG, "isPermission: " + All_PERMISION[i]);
//                return false;
//            }
        }

        return true;
    }

    private static String[] All_PERMISION = {
//            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
//            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.WRITE_SETTINGS
    };

    /** 同时请求多个权限（合并结果）的情况 */
    private void MultPermission() {


        RxPermissions rxPermission = new RxPermissions(MainActivity.this);

        rxPermission.request(All_PERMISION).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean granted) throws Exception {
                if (granted) {
                    // 用户已经同意该权限
                    Logg.e(TAG, "accept:同意的权限 " + granted);
                    whenFirstPerOK();
                    //	MainService.getInstance().startCallService();
                    //    sendBroadcast(new Intent(SportService.PERMISION_GRANTED_GPS));
                    //    isFirstGetPermision = false;
                } else {
                    // 用户拒绝了该权限，并且选中『不再询问』
                    Logg.e(TAG, "accept:用户拒绝了该权限 " + granted);
                    showPermissionPrompt();
                }
            }
        });

    }

    private void whenFirstPerOK() {
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int writeSdCardPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            writeSdCardPermission =  mContext.checkSelfPermission(Manifest.permission.WRITE_CALL_LOG); // == PackageManager.PERMISSION_GRANTED;
        }else{
            writeSdCardPermission = PermissionChecker.checkSelfPermission(mContext, Manifest.permission.WRITE_CALL_LOG); //  == PermissionChecker.PERMISSION_GRANTED;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || writeSdCardPermission == PackageManager.PERMISSION_GRANTED) {
            if(null != MainService.getInstance()){
                MainService.getInstance().startCallService();
            }
        }

//        MainService.getInstance().startCallService();


        sendBroadcast(new Intent(SportService.PERMISION_GRANTED_GPS));
        homeFragment.weatherInit();
        isFirstGetPermision = false;
    }


    /**
     * 检查更新
     */
    private void checkUpdate() {
        String uid = DeviceUtils.getUniqueId(this);//设备id
        Log.e(LOG_TAG, "-----------uuid="+uid);
        String url = Constants.FUNDO_UNIFIED_DOMAIN_test + String.format(Locale.ENGLISH,Constants.APP_CHECK_UPDATE, 0, uid);//todo 0 指 appName 0:分动；1：分动手环，2：分动穿戴,3:funfit,4:funrun
        hc.getNetworkData(url, myHandler, CHECK_UPDATE);
    }

    /**
     * 强制更新的响应
     * @param s
     */
    private void updateRespose(String s){
        try{
            JSONObject jo = new JSONObject(s);
            JSONObject joData = jo.getJSONObject("data");
            updateBean = new Gson().fromJson(joData.toString(), UpdateBean.class);
            //比较服务端版本和本地版本
            String appVersion = updateBean.getAppVersion();
            String versionName = getVersionName();
            if(versionName.startsWith("V") || versionName.startsWith("v")){
                versionName = versionName.substring(1);
            }

            String oldVersion = versionName.replace(".","");
            String newVersion = appVersion.replace(".","");
            int ioldVersion = Integer.valueOf(oldVersion);
            int inewVersion = Integer.valueOf(newVersion);


//          appVersion 新版本号 1.3.2       versionName 旧的版本号  = "1.0.0";
            if(inewVersion > ioldVersion){      // appVersion.compareTo(versionName) > 0
                //有更新
                //判断是不是强制升级 0:升级开关关闭，1：普通升级，2：强制升级
                int status = updateBean.getStatus();
                if(status != 0){//当升级开关开启时
                    if(status == 2) {//1.普通升级，需要判断 isUpgrade ； 2.强制升级       (status == 1 && updateBean.isIsUpgrade()) ||

                        isUpDateFlagForMain = true;  //TODO  ---  有强制升级，不需要普通升级

//                        UpdateActivity.startSelf(updateBean);
                        //////////todo --- 强制升级 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        showDialog();
                        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    }
                }else{
                    //升级开关关闭
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showDialog() {  //todo ---- 还需要考虑 重新设置 目标后，应该将  isShowAlertDialog 置为 false
        final android.app.AlertDialog myDialog;
//        if(!isShowAlertDialog) {//是否显示过
        myDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
        myDialog.show();
//            isShowAlertDialog = true;
        myDialog.getWindow().setContentView(R.layout.update);    // user_comment_dialog     999999999999999999999
        myDialog.setCancelable(false);

//        String languageLx = Utils.getLanguage();
//        if (languageLx.equals("de")) {  // 德语改小字体
//            TextView comment1 = (TextView) myDialog.getWindow().findViewById(R.id.tv_comment1);
//            comment1.setTextSize(12);
//            TextView comment2 = (TextView) myDialog.getWindow().findViewById(R.id.tv_comment2);
//            comment2.setTextSize(12);
//            TextView comment3 = (TextView) myDialog.getWindow().findViewById(R.id.tv_comment3);
//            comment3.setTextSize(12);
//        }

        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        myDialog.getWindow().setBackgroundDrawable(dw);
        myDialog.getWindow()
                .findViewById(R.id.tv_update)  //           makesure_btn    cancel_btn
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //////////////////////////////////////////////////////////////////////////////////////////////////
//                        if (Utils.getLanguage().equals("zh")) { //中文环境跳转到应用宝
                            if (NetWorkUtils.isConnect(MainActivity.this)) {    //todo 立即更新
                                try {
//                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT, "0");//     0：未评论 1：已评论
//                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.PAGE_WELCOME_STARTNUM, "0");// 将启动次数置为0
                                    BTNotificationApplication context = BTNotificationApplication.getInstance();
                                    Intent intent = new Intent(context, DownloadService.class);   // UpdateActivity
                                    intent.putExtra(UPDATE_BEAN, updateBean);
                                    startService(intent);
//                        startService(new Intent(getApplicationContext(), DownloadService.class));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(MainActivity.this, R.string.net_error_tip, Toast.LENGTH_SHORT).show();
                            }
//                        } else { // 非中文显示 Google Play  引导到  Google Play 下载
//                            toUpdate();
//                        }

                        myDialog.dismiss();
                    }
                });
//        myDialog.getWindow()
//                .findViewById(R.id.cancel_btn)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        myDialog.dismiss();
//                    }
//                });
    }

    private void toUpdate() {  // 国外引导到google
        String url = null;
        if(updateBean != null){
            url = updateBean.getAppMarketForeignUrl(); //Google apk下载链接
        }
        //todo 立即更新
        if (NetWorkUtils.isConnect(this)) {
            try {
//                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT, "0");//     0：未评论 1：已评论
//                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.PAGE_WELCOME_STARTNUM, "0");// 将启动次数置为0
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                this.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, R.string.net_error_tip, Toast.LENGTH_SHORT).show();
        }
    }


    private void initView() {
        //判断主题。
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_main);
        mContext = this;

        initRadioButton();

        mMainActivity = this;

        fragmentManager = getSupportFragmentManager();
        //	if (savedInstanceState == null) {
        homeFragment = HomeFragment.newInstance("");
        analysisFragment = AnalysisFragment.newInstance("");
        //discoverFragment=DiscoverFragment.newInstance("");
        helperFragment = HelperFragment.newInstance("");
        pushdialFragment = PushdialFragment.newInstance("");
        settingFragment = SettingFragment.newInstance("");
        healthfragment = HealthFragment.newInstance("");
        fragmentManager.beginTransaction().add(R.id.container, homeFragment, "homeFragment").commitAllowingStateLoss();
    }


    boolean isFirstGetPermision = true;
    @Override
    public void onResume() {
        //	setHeadPhoto();
        super.onResume();
        MobclickAgent.onResume(this);
        EventBus.getDefault().post(new MessageEvent(AUTO_CONNECT));

        if (!isPermission()) {
            if (perAlert != null && perAlert.isShowing()) {
                Logg.e(TAG, "onResume: perAlert.isShowing(");
            } else {
                MultPermission();
                Logg.e(TAG, "onResume: MultPermission()");
            }
        } else {
            if(isFirstGetPermision){
                whenFirstPerOK();
			//	 MainService.getInstance().startCallService();
            //    sendBroadcast( new Intent(SportService.PERMISION_GRANTED_GPS));
            //    isFirstGetPermision = false;
            }

			/* if(perAlert != null && perAlert.isShowing()){
                perAlert.dismiss();
                MainService.getInstance().startCallService();
            }*/

        }
    }


    private void initRadioButton() {
        homeBtn = (RadioButton) findViewById(R.id.iv_personal_homepage);     // 主Fragment
        helpBtn = (RadioButton) findViewById(R.id.iv_personal_settinghelp);   // 我的Fragment
        bigdataBtn = (RadioButton) findViewById(R.id.iv_personal_bigdata);     // 大数据
        bleServiceBtn = (RadioButton) findViewById(R.id.iv_personal_bleservice);  // 手表助手
        healthBtn = (RadioButton) findViewById(R.id.iv_personal_health);       // 健康
        homeBtn.setOnClickListener(this);
        helpBtn.setOnClickListener(this);
        bigdataBtn.setOnClickListener(this);
        bleServiceBtn.setOnClickListener(this);
        healthBtn.setOnClickListener(this);
    }

    public static MainActivity getInstance() {
        return mMainActivity;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);

        if (!isUpDateFlagForMain && NetWorkUtils.isConnect(this)) {
            // 请求网络是否更新apk
            new HttpToService(this).start();
        }

    }

    /**
     * TODO --- 返回键按下两次退出应用程序
     *
     * onKeyDown  /onKeyUp 区别
     *
     * onkeydown 事件最先执行，其次是onkeypress,最后是onkeyup
     * onKeyUp 的话会与其他 onkeydown 冲突的哦
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                // 如果两次按键时间间隔大于2秒，则不退出
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(this, R.string.quit_app, Toast.LENGTH_SHORT).show();
                    // 更新firstTime
                    firstTime = secondTime;
                    return true;
                    // 两次按键小于2秒时，退出应用
                } else {
                    finish();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        SharedPreferences sharedPreferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstIn", false);  //  todo --- 退出app时，将isFirstIn 置为false
        editor.commit();


        Log.e(LOG_TAG, "onDestroy");
        if (blutbroadcast != null) {
            unregisterReceiver(blutbroadcast);
        }
        if (musicbroadcast != null) {
            unregisterReceiver(musicbroadcast);
        }
        if (stb != null) {
            unregisterReceiver(stb);
            stb = null;
        }

        if(null != mScreenListener){
            mScreenListener.stopScreenReceiverListener();
        }

        if(dataAndTimeReceiver != null){
            unregisterReceiver(dataAndTimeReceiver);
        }

        super.onDestroy();
    }

//    @Override
//    public void finish() {
////        super.finish();
//        moveTaskToBack(true);
//    }

    // 注册广播的方法
    private void registerBroad() {
        stb = new SetthemeBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainService.ACTION_THEME_CHANGE);
        registerReceiver(stb, filter);
        blutbroadcast = new Bluttoothbroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(blutbroadcast, intentFilter);

        musicbroadcast = new Musicbroadcast();
        filter = new IntentFilter();
        filter.addAction("musicstart");
        registerReceiver(musicbroadcast, filter);
    }

    class SetthemeBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MainService.ACTION_THEME_CHANGE == action) {
                initView();
            }
        }
    }

    AlertDialog perAlert;
    /****** 以下 mtk add ****/
    private void showPermissionPrompt() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.permision_alert_title));
        builder.setMessage(getString(R.string.permision_alert_content));

        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gotoPermissionSetting(MainActivity.this);
                    }
                });

        perAlert = builder.create();
        perAlert.setCancelable(false);
        perAlert.show();
    }

    /**
     * 跳转: 「权限设置」界面
     * <p>
     * 根据各大厂商的不同定制而跳转至其权限设置
     * 目前已测试成功机型: 小米V7V8V9, 华为, 三星, 锤子, 魅族; 测试失败: OPPO
     *
     * @return 成功跳转权限设置, 返回 true; 没有适配该厂商或不能跳转, 则自动默认跳转设置界面, 并返回 false
     */
    public boolean gotoPermissionSetting(Context context) {
        boolean success = true;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String packageName = context.getPackageName();

        OSUtils.ROM romType = OSUtils.getRomType();
        switch (romType) {
            case EMUI: // 华为
                intent.putExtra("packageName", packageName);
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
                break;
            case Flyme: // 魅族
                intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra("packageName", packageName);
                break;
            case MIUI: // 小米
                String rom = getMiuiVersion();
                if ("V6".equals(rom) || "V7".equals(rom)) {
                    intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    intent.putExtra("extra_pkgname", packageName);
                } else if ("V8".equals(rom) || "V9".equals(rom)) {
                    intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                    intent.putExtra("extra_pkgname", packageName);
                } else {
                    intent = getAppDetailsSettingsIntent(packageName);
                }
                break;
            case Sony: // 索尼
                intent.putExtra("packageName", packageName);
                intent.setComponent(new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity"));
                break;
            case ColorOS: // OPPO
                intent.putExtra("packageName", packageName);
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.PermissionManagerActivity"));
                break;
            case EUI: // 乐视
                intent.putExtra("packageName", packageName);
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps"));
                break;
            case LG: // LG
                intent.setAction("android.intent.action.MAIN");
                intent.putExtra("packageName", packageName);
                ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
                intent.setComponent(comp);
                break;
            case SamSung: // 三星
            case SmartisanOS: // 锤子
                gotoAppDetailSetting(packageName);
                break;
            default:
                intent.setAction(Settings.ACTION_SETTINGS);
                Log.i(IntentUtils.class.getSimpleName(), "没有适配该机型, 跳转普通设置界面");
                success = false;
                break;
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // 跳转失败, 前往普通设置界面
           startActivity(getSettingIntent());
            success = false;
            Log.i(IntentUtils.class.getSimpleName(), "无法跳转权限界面, 开始跳转普通设置界面");
        }
        return success;
    }

    public static Intent getSettingIntent() {
        return new Intent(Settings.ACTION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 获取 MIUI 版本号
     */
    private static String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i(IntentUtils.class.getSimpleName(), "MiuiVersion = " + line);
        return line;
    }

    /**
     * 获取跳转「应用详情」的意图
     *
     * @param packageName 应用包名
     * @return 意图
     */
    public Intent getAppDetailsSettingsIntent(String packageName) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void gotoAppDetailSetting(String packageName) {
        startActivity(getAppDetailsSettingsIntent(packageName));
    }

    /****** 以下 mtk add ****/
    private void showAccessibilityPrompt() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.accessibility_prompt_title);
        builder.setMessage(R.string.accessibility_prompt_content);

        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // Go to accessibility settings
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(ACCESSIBILITY_INTENT);
                    }
                });
        builder.create().show();
    }

    private void showNotifiListnerPrompt() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.notificationlistener_prompt_title);
        builder.setMessage(R.string.notificationlistener_prompt_content);
       /* builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        // Go to notification listener settings
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(NOTIFICATION_LISTENER_INTENT);
                    }
                });
        builder.setCancelable(false);
        builder.create().show();
    }



    private void switchContent(int id) {
        switch (id) {
            case R.id.iv_personal_homepage:    // 主Fragment
                if (!homeFragment.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.container, homeFragment, "homeFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions();
                }
                fragmentManager.beginTransaction().show(homeFragment).hide(analysisFragment)
                        .hide(settingFragment).hide(helperFragment).hide(pushdialFragment).hide(healthfragment).commitAllowingStateLoss();
                homeBtn.setChecked(true);
                helpBtn.setChecked(false);
                bigdataBtn.setChecked(false);
                bleServiceBtn.setChecked(false);
                healthBtn.setChecked(false);
                if (inpush) {
                    inpush = false;
                    if (MainService.getInstance().getState() == 3) {
                        MainService.getInstance().sendMessage(PUSHOUT_STRING);
                    }
                }
                break;

            case R.id.iv_personal_bleservice:
                if (ishelper) {
                    if (!helperFragment.isAdded()) {
                        fragmentManager.beginTransaction().add(R.id.container, helperFragment, "helperFragment").commitAllowingStateLoss();
                        fragmentManager.executePendingTransactions();
                    }
                    fragmentManager.beginTransaction().show(helperFragment).hide(analysisFragment).hide(homeFragment).hide(healthfragment).hide(pushdialFragment)
                            .hide(settingFragment).commitAllowingStateLoss();
                } else {
                    if (!pushdialFragment.isAdded()) {
                        fragmentManager.beginTransaction().add(R.id.container, pushdialFragment, "pushdialFragment").commitAllowingStateLoss();
                        fragmentManager.executePendingTransactions();
                    }
                    fragmentManager.beginTransaction().show(pushdialFragment).hide(analysisFragment).hide(homeFragment).hide(healthfragment).hide(helperFragment)
                            .hide(settingFragment).commitAllowingStateLoss();
                    inpush = true;
                    if (MainService.getInstance().getState() == 3) {
                        MainService.getInstance().sendMessage(PUSHIN_STRING);
                    }
                }
                homeBtn.setChecked(false);
                helpBtn.setChecked(false);
                bigdataBtn.setChecked(false);
                bleServiceBtn.setChecked(true);
                healthBtn.setChecked(false);

                break;

            case R.id.iv_personal_settinghelp:
                if (!settingFragment.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.container, settingFragment, "settingFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions();
                }
                fragmentManager.beginTransaction().show(settingFragment).hide(analysisFragment).hide(helperFragment).hide(pushdialFragment).hide(healthfragment)
                        .hide(homeFragment).commitAllowingStateLoss();
                homeBtn.setChecked(false);
                helpBtn.setChecked(true);
                bigdataBtn.setChecked(false);
                bleServiceBtn.setChecked(false);
                healthBtn.setChecked(false);
                if (inpush) {
                    inpush = false;
                    if (MainService.getInstance().getState() == 3) {
                        MainService.getInstance().sendMessage(PUSHOUT_STRING);
                    }
                }
                break;

            case R.id.iv_personal_bigdata:
                if (!analysisFragment.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.container, analysisFragment, "analysisFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions();
                }
                fragmentManager.beginTransaction().show(analysisFragment).hide(homeFragment).hide(helperFragment).hide(pushdialFragment)
                        .hide(healthfragment).hide(settingFragment).commitAllowingStateLoss();

                homeBtn.setChecked(false);
                helpBtn.setChecked(false);
                bigdataBtn.setChecked(true);
                bleServiceBtn.setChecked(false);
                healthBtn.setChecked(false);
                if (inpush) {
                    inpush = false;
                    if (MainService.getInstance().getState() == 3) {
                        MainService.getInstance().sendMessage(PUSHOUT_STRING);
                    }
                }
                break;

            case R.id.iv_personal_health:
                if (!healthfragment.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.container, healthfragment, "healthfragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions();
                }
                fragmentManager.beginTransaction().show(healthfragment).hide(homeFragment)//.hide(discoverFragment)
                        .hide(settingFragment).hide(analysisFragment).hide(helperFragment).hide(pushdialFragment).commitAllowingStateLoss();
                //	}

                homeBtn.setChecked(false);
                helpBtn.setChecked(false);
                bigdataBtn.setChecked(false);
                bleServiceBtn.setChecked(false);
                healthBtn.setChecked(true);
                if (inpush) {
                    inpush = false;
                    if (MainService.getInstance().getState() == 3) {
//					MainService.getInstance().sendMessage(PUSHOUT_STRING);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switchContent(v.getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Log.v("LH", "onSaveInstanceState"+outState);
        //super.onSaveInstanceState(outState);   //将这一行注释掉，阻止activity保存fragment的状态
    }

    public class Musicbroadcast extends BroadcastReceiver {
		/*
		 * State wifiState = null; State mobileState = null;
		 * "com.android.music.metachanged"//播放下一首音乐的时候会发送广播;
		 * "com.android.music.queuechanged" 
		 * "com.android.music.playbackcomplete"
		 * "com.android.music.playstatechanged"
		 */

        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.doStartApplicationWithPackageName(context, "com.google.android.music", pendingIntent);
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

}