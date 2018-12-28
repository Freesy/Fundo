package com.szkct.weloopbtsmartdevice.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mtk.app.applist.FileUtils;
import com.szkct.takephoto.uitl.TUriParse;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.login.Gdata;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.ImageCacheUtil;
import com.szkct.weloopbtsmartdevice.util.ScreenshotsShare;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class SportHistoryActivityForMtkYouyong extends AppCompatActivity implements View.OnClickListener {

    WebView webview;

    private View mView;
    private GpsPointDetailData gpsPoint;   // 关键数据
    private String choicetime;
    private DBHelper db = null;
    public static ScrollView detailfragment_sc;   //

    private TextView tv_zts,tv_zhss,tv_yyhscs,tv_wyhscs,tv_dyhscs,tv_zzyhscs,tv_qtyzhscs;

    private TextView mdetail_date;
    private TextView mtv_showdic, mdetail_zdbf, mdetail_zxbf, mdetail_pjbf;
    private TextView mtv_showtime, mdetail_czsd, mdetail_zdxl, mdetail_zxxl, mdetail_pjxl;
    private TextView mdetail_sudu, mdetail_pjps, mdetail_ljps, mdetail_ljxj;
    private TextView mdetail_peisu, mdetail_xlqd, mdetail_zgps, mdetail_zdps;
    private TextView mdetail_xiaohao, mdetail_bushu, mdetail_zdsyl;
    private TextView mtv_showdic_up,mdetail_sudu_up,mdetail_peisu_up,mdetail_xiaohao_up,mdetail_zgps_up,mdetail_zdps_up
            ,mdetail_pjps_up,mdetail_ljps_up,mdetail_ljxj_up,mdetail_czsd_up,mdetail_zdbf_up, mdetail_zxbf_up, mdetail_pjbf_up;
    private TextView total_length_id,realTime_hms_id,detailed_mileage_id;
    private boolean isMetric;
//    private FragmentActivity mContext;
    private SharedPreferences preferences;
    private List<Double> psList = new ArrayList<Double>();//配速集合
    private List<Integer> xlList = new ArrayList<Integer>();//心率集合
    private List<Integer> watchPsList = new ArrayList<Integer>();//手表配速集合
    private List<Integer> buPinglist = new ArrayList<Integer>();//步频集合
    private TextView mdetail_zdbp, mdetail_zxbp, mdetail_pjbp, mdetail_sjsc, mdetail_ztsc, mdetail_ztcs;
    private ImageView detail_icon;
    private TextView detail_name,sportdataerror_tv;
    private String sex;
    private ImageView sportmode_twopage_logo_iv;

    // 下面3个字段都为 手机端数据
    private int maxBp = 0; //最大步频
    private  int minBp; // 最小步频
    private  String pjPs; // 平均配速

    private ImageButton ib_sporthistory_share;
    private ImageView back;

    TextView tv_sporthistory_title;

    private String mFilePath;
    private boolean isRunning = false;

    private String filePath = Environment.getExternalStorageDirectory()
            + "/appmanager/fundoShare/";
    private String fileName = "screenshot_analysis.png";  // screenshot_analysis.png
    private String detailPath = filePath + File.separator + fileName;

    public static final String COM_FACEBOOK = "com.facebook";
    public static final String COM_FACEBOOK_KATANA = "com.facebook.katana";

    public static final String COM_INSTAGRAM = "com.instagram";
    public static final String COM_INSTAGRAM_ANDROID = "com.instagram.android";

    public static final String COM_TWITTER = "com.twitter";
    public static final String COM_TWITTER_ANDROID = "com.twitter.android";

    public static final String COM_WHATSAPP = "com.whatsapp";

    public static final String COM_LINKEDIN = "com.linkedin";
    public static final String COM_LINKEDIN_ANDROID = "com.linkedin.android";

    public static final String COM_STRAVA = "com.strava";

    public static final String COM_TENCENT_MOBILEQQ = "com.tencent.mobileqq";
    private Map<String, String> mapPackageName;

    private static final int[] SECTION_STRINGS = {R.string.sportshistory_jianzou,
            R.string.sportshistory_huwaipao,
            R.string.sportshistory_shineipao,
            R.string.sportshistory_dengshan,
            R.string.sportshistory_yueyepao,
            R.string.sportshistory_banma,
            R.string.sportshistory_quanma,  // 之前7
            R.string.sportshistory_tiaosheng,
            R.string.sportshistory_yumaoqiu,
            R.string.sportshistory_lanqiu,
            R.string.sportshistory_qixing,
            R.string.sportshistory_huabing,
            R.string.sportshistory_jianshen,
            R.string.sportshistory_yujia,
            R.string.sportshistory_wangqiu,
            R.string.sportshistory_pingpang,
            R.string.sportshistory_zuqiu,
            R.string.sportshistory_youyong,
            R.string.sportshistory_xingai,
            R.string.sportshistory_fanhang,
    };

    public Map<String, String> setImage(Activity activity) {
        Map<String, String> mapPackageName = new LinkedHashMap<String, String>();

        // PackageManager pManager = activity.getPackageManager();
        ArrayList<ResolveInfo> resolveInfos = getShareApp(activity);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            // String packageName = resolveInfo.activityInfo.name;
            if (packageName.startsWith(COM_FACEBOOK_KATANA)) {

                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_FACEBOOK_KATANA, packageName);

            }
            if (packageName.startsWith(COM_INSTAGRAM_ANDROID)) {

                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_INSTAGRAM_ANDROID, packageName);

            }
            if (packageName.startsWith(COM_TWITTER_ANDROID)) {

                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_TWITTER_ANDROID, packageName);

            }
            if (packageName.startsWith(COM_WHATSAPP)) {

                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_WHATSAPP, packageName);

            }
            if (packageName.startsWith(COM_LINKEDIN_ANDROID)) {

                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_LINKEDIN_ANDROID, packageName);

            }
            if (packageName.startsWith(COM_TENCENT_MOBILEQQ)) {

                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_TENCENT_MOBILEQQ, packageName);

            }
            if (packageName.startsWith(COM_STRAVA)){
                mapPackageName.put(COM_STRAVA, packageName);
            }
        }

        return mapPackageName;
    }

    public ArrayList<ResolveInfo> getShareApp(Context context) {
        ArrayList<ResolveInfo> WECHAT_FACEBOOK = new ArrayList<ResolveInfo>();

        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("image/png");
        PackageManager pManager = context.getPackageManager();
        List<ResolveInfo> mApps = pManager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

        for (ResolveInfo resolveInfo : mApps) {
            String packageName = resolveInfo.activityInfo.packageName;

            if (packageName.startsWith(COM_FACEBOOK)) {
                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_INSTAGRAM)) {

                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_TWITTER)) {

                WECHAT_FACEBOOK.add(resolveInfo);
            }

            if (packageName.startsWith(COM_WHATSAPP)) {

                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_LINKEDIN)) {

                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_TENCENT_MOBILEQQ)) {

                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_STRAVA)){
                WECHAT_FACEBOOK.add(resolveInfo);
            }
        }

        return WECHAT_FACEBOOK;
    }

    View.OnClickListener facebookclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToFacebook();
        }
    };
    View.OnClickListener Instagramclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToInstagram();
        }
    };
    View.OnClickListener twitterclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareTotwitter();
        }
    };
    View.OnClickListener whatsappclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareTowhatsapp();
        }
    };
    View.OnClickListener Linkedinclick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            shareToLinkedin();
        }
    };
    View.OnClickListener mobileqqclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            shareTomobileqq();
            Utils.onClickShareToQQ(SportHistoryActivityForMtkYouyong.this, detailPath);
        }
    };
    View.OnClickListener stravaclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToStrava();
        }
    };

    /**
     * 分享至Facebook
     */
    public void shareToFacebook() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_FACEBOOK_KATANA);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_facebook_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_facebook_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 分享至Instagram
     */
    public void shareToInstagram() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_INSTAGRAM_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_instagram_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_instagram_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至Twitter
     */
    public void shareTotwitter() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_TWITTER_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_twitter_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_twitter_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至whatsapp
     */
    public void shareTowhatsapp() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_WHATSAPP);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至Linkedin
     */
    public void shareToLinkedin() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_LINKEDIN_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_linkedin_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_linkedin_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至strava
     */
    public void shareToStrava() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_STRAVA);
            if (packageName != null) {
                PackageManager pm = this.getPackageManager();
                boolean isAdd = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE",packageName));
                if(!isAdd){
                    Toast.makeText(this, getString(R.string.strava_need_open_permission), Toast.LENGTH_SHORT).show();
                    return;
                }
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_strava_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_strava_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至mobileqq
     */
    public void shareTomobileqq() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_TENCENT_MOBILEQQ);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    public void actionShare_sms_email_facebook(String packageName, Activity activity, String shareText) {

//		String fileName = "rideSummary_" + datetime_start + ".png";
//		File file = new File("/storage/emulated/0/DCIM/Camera/1411099620786.jpg");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png"); //   intent.setType("image/jpeg");

        intent.putExtra(Intent.EXTRA_SUBJECT, "SchwinnCycleNav Ride Share");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);

        File file = new File(detailPath);

//        Uri ddd =  Uri.fromFile(file);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.putExtra(Intent.EXTRA_STREAM, TUriParse.getUriForFile(activity, file));
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//		intent.putExtra(Intent.EXTRA_STREAM, DatabaseProvider.queryScreenshot(activity, datetime.getTime()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(packageName);
        activity.startActivity(Intent.createChooser(intent, getString(R.string.app_name)));

        System.out.println("****3");
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.fragment_detailed_data_foryouyong);

//        mView = inflater.inflate(R.layout.fragment_detailed_data, null);

     /*   initView();
        initWebview();
        webview.loadUrl(getIntent().getStringExtra("url"));*/

        //////////////////////////////////////////////////////////////
        gpsPoint = (GpsPointDetailData)getIntent().getSerializableExtra("Vo");
        choicetime = gpsPoint.getTimeMillis();//时间毫秒数
        if (db == null) {
            db = DBHelper.getInstance(BTNotificationApplication.getInstance());
        }
        preferences = BTNotificationApplication.getInstance().getSharedPreferences("userinfo", MODE_MULTI_PROCESS);
        initview();
//        setheadp();
        setUserName();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      mFilePath = Environment.getExternalStorageDirectory().getPath();
        // 文件名
        mFilePath = mFilePath + "/" + "photo.png";
        /*  gpsPoint = (GpsPointDetailData) getIntent().getSerializableExtra("Vo");
        isMetricInActivity = SharedPreUtil.YES.equals(SharedPreUtil.getParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));

        String totalPs = gpsPoint.getArrspeed().replace("Infinity","");         // TODO ---- 速度值 GpsPointDetailData
        Log.e("rq", "totalPs==" + totalPs);
//        if(!StringUtils.isEmpty(totalPs) && totalPs.contains("&")){
        String[] arrPs = totalPs.split("&");
        if(arrPs != null && arrPs.length !=0){
            int psSize = arrPs.length;
            for (int i = 0; i < psSize; i++) {
                Float psValue = Utils.tofloat(arrPs[i]);
                if(!isMetricInActivity){
                    psValue = (float)Utils.getUnit_mile(psValue);
                }
                speedListInAc.add(psValue);
            }
        }
        //###########################
        String totalal= gpsPoint.getArraltitude();  // 海拔值   地图得到的海拔值 260.15902099774047&256.7814860623313&258.2029664825295&263.0911206371336&237.06402266995303&
        Log.e("rq","totalal=="+totalal);
        String[] arral = totalal.split("&");
        if(arral!=null&&arral.length!=0){
            int caSize = arral.length;
            for (int i = 0; i < caSize; i++) {
                Float psValue = Utils.tofloat(arral[i]);
                if(!isMetricInActivity){
                    psValue = (float)Utils.getUnit_mile(psValue);
                }
                altitudeListInAc.add(psValue);     // TODO --- 海拔数组
            }
        }
        //###########################
        String totalht= gpsPoint.getArrheartRate();  //todo  ---  心率值
        Log.e("rq","totalht=="+totalht);
        String[] arrht = totalht.split("&");
        Log.e("rq",arrht.toString()+"=="+arrht.length);
        if(arrht!=null && arrht.length!=0){
            int caSize = arrht.length;
            for (int i = 0; i < caSize; i++) {
                Float psValue = Utils.tofloat(arrht[i]);
                heartlistInAc.add(psValue);
            }
        }
        //###########################
        String totalca= gpsPoint.getArrcadence();   // TODO --- 步频值
        Log.e("rq","totalca=="+totalca);
        String[] arrca = totalca.split("&");
        if(arrca!=null&&arrca.length!=0){
            int caSize = arrca.length;
            for (int i = 0; i < caSize; i++) {
                Float psValue = Utils.tofloat(arrca[i]);
                cadencelistInAc.add(psValue);
            }
        }
        initView();
        initRadioButton();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        OnekeyShare.isShowShare = true;   // todo --- 页面初始化时*/
    }

    private void initview() {

        int type = Utils.toint(gpsPoint.getSportType()) - 1;   // TODO ----  运动类型
        if(type < 0){
            type = 0;
        }

        tv_sporthistory_title = (TextView) findViewById(R.id.tv_sporthistory_title);

        String languageLx  = Utils.getLanguage();
        if (!languageLx.equals("en") || !languageLx.equals("zh")) {
            tv_sporthistory_title.setTextSize(12);
        }

        tv_sporthistory_title.setText(SECTION_STRINGS[type]);  // TODO  ---- 给标题设置运动类型


        back= (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        findViewById(R.id.ib_sporthistory_photo).setOnClickListener(this);  //TODO  拍照
        ib_sporthistory_share= (ImageButton) findViewById(R.id.ib_sporthistory_share);
        ib_sporthistory_share.setOnClickListener(this);

        detailfragment_sc = (ScrollView)findViewById(R.id.detailfragment_sc);

        mdetail_date = (TextView)findViewById(R.id.detail_date);    // 运动结束的最后日期 ： 2017.02.28 11.08
        mtv_showdic = (TextView)findViewById(R.id.tv_showdic);     // 运动的距离   ---- 公里数
        mtv_showtime = (TextView)findViewById(R.id.tv_showtime);    // 运动结束时的时长
        tv_zts = (TextView)findViewById(R.id.tv_zts);  // 速度    ----     tv_zts  总趟数
        tv_zhss = (TextView)findViewById(R.id.tv_zhss);  // 配速   ----   tv_zhss 总划水数
        tv_yyhscs = (TextView)findViewById(R.id.tv_yyhscs); // 消耗卡路里   --- tv_yyhscs 仰泳划水次数
        tv_wyhscs  = (TextView)findViewById(R.id.tv_wyhscs);  // 步数        --------  tv_wyhscs 蛙泳划水次数
        tv_dyhscs = (TextView)findViewById(R.id.tv_dyhscs);        // 最大摄氧量    ----   tv_dyhscs 蝶泳划水次数
        tv_zzyhscs  = (TextView)findViewById(R.id.tv_zzyhscs);     // 训练强度      ----- tv_zzyhscs 自由泳划水次数
        tv_qtyzhscs = (TextView)findViewById(R.id.tv_qtyzhscs);     //  最高配速    ---  tv_qtyzhscs 其他泳姿划水次数


//        mdetail_zdps = (TextView)findViewById(R.id.detail_zdps);     //  最低配速
//        mdetail_pjps = (TextView)findViewById(R.id.detail_pjps);     //  平均配速
//        mdetail_ljps = (TextView)findViewById(R.id.detail_ljps);     //   累计攀爬
//        mdetail_ljxj = (TextView)findViewById(R.id.detail_ljxj);     //   累计下降
//        mdetail_czsd = (TextView)findViewById(R.id.detail_czsd);     //   垂直速度
//        mdetail_zdxl = (TextView)findViewById(R.id.detail_zdxl);     //   最大心率
//        mdetail_zxxl = (TextView)findViewById(R.id.detail_zxxl);     //   最小心率
//        mdetail_pjxl = (TextView)findViewById(R.id.detail_pjxl);     //   平均心率
//        mdetail_zdbf = (TextView)findViewById(R.id.detail_zdbf);     //   最大步副
//        mdetail_zxbf = (TextView)findViewById(R.id.detail_zxbf);       //   最小步副
//        mdetail_pjbf = (TextView)findViewById(R.id.detail_pjbf);       //   平均步副
//        mdetail_zdbp = (TextView)findViewById(R.id.detail_zdbp);       //   最大频率
//        mdetail_zxbp = (TextView)findViewById(R.id.detail_zxbp);      //   最小频率
//        mdetail_pjbp = (TextView)findViewById(R.id.detail_pjbp);      //   平均频率
//        mdetail_sjsc = (TextView)findViewById(R.id.detail_sjsc);      //   实际时长
//        mdetail_ztsc = (TextView)findViewById(R.id.detail_ztsc);      //   暂停时长
//        mdetail_ztcs = (TextView)findViewById(R.id.detail_ztcs);      //    暂停次数

        detail_icon = (ImageView)findViewById(R.id.detail_icon);      // 用户头像
        detail_name = (TextView)findViewById(R.id.detail_name);       // 用户名字

        sportdataerror_tv = (TextView)findViewById(R.id.sportdataerror_tv);

        sportmode_twopage_logo_iv = (ImageView)findViewById(R.id.sportmode_twopage_logo_iv);
        if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){  // 白色背景
            sportmode_twopage_logo_iv.setImageResource(R.drawable.sportmode_logo_w);
        }else{
            sportmode_twopage_logo_iv.setImageResource(R.drawable.sportmode_logo_b);
        }


        mtv_showdic_up = (TextView)findViewById(R.id.tv_showdic_up);     //距离单位
        mdetail_sudu_up = (TextView)findViewById(R.id.detail_sudu_up);  // 速度单位
        mdetail_peisu_up = (TextView)findViewById(R.id.detail_peisu_up);  // 配速单位
        mdetail_xiaohao_up = (TextView)findViewById(R.id.detail_xiaohao_up); // 消耗卡路里单位
        mdetail_zgps_up = (TextView)findViewById(R.id.detail_zgps_up);     //  最高配速单位
        mdetail_zdps_up = (TextView)findViewById(R.id.detail_zdps_up);     //  最低配速单位
        mdetail_pjps_up = (TextView)findViewById(R.id.detail_pjps_up);     //  平均配速单位
        mdetail_ljps_up = (TextView)findViewById(R.id.detail_ljps_up);     //   累计攀爬单位
        mdetail_ljxj_up = (TextView)findViewById(R.id.detail_ljxj_up);     //   累计下降单位
        mdetail_czsd_up = (TextView)findViewById(R.id.detail_czsd_up);     //   垂直速度单位
        mdetail_zdbf_up = (TextView)findViewById(R.id.detail_zdbf_up);     //   最大步副单位
        mdetail_zxbf_up = (TextView)findViewById(R.id.detail_zxbf_up);       //   最小步副单位
        mdetail_pjbf_up = (TextView)findViewById(R.id.detail_pjbf_up);       //   平均步副单位

        total_length_id = (TextView)findViewById(R.id.total_length_id);
        realTime_hms_id = (TextView)findViewById(R.id.realTime_hms_id);
        detailed_mileage_id = (TextView)findViewById(R.id.detailed_mileage_id);

        if(Utils.getLanguage().contains("ru") || Utils.getLanguage().contains("it") || Utils.getLanguage().contains("pt") || Utils.getLanguage().contains("de")  || Utils.getLanguage().contains("es")){
            total_length_id.setTextSize(15);
            detailed_mileage_id.setTextSize(15);
            realTime_hms_id.setTextSize(10);
            mtv_showdic_up.setTextSize(10);
        }

        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));

        if(!isMetric) {
            mtv_showdic_up.setText(getString(R.string.unit_mi));
            mdetail_sudu_up.setText(getString(R.string.unit_mi_hour));
            mdetail_peisu_up.setText(getString(R.string.unit_min_mi));
            mdetail_xiaohao_up.setText(getString(R.string.unit_kj));
            mdetail_zgps_up.setText(getString(R.string.unit_min_mi));
            mdetail_zdps_up.setText(getString(R.string.unit_min_mi));
            mdetail_pjps_up.setText(getString(R.string.unit_min_mi));
            mdetail_ljps_up.setText(getString(R.string.unit_ft));
            mdetail_ljxj_up.setText(getString(R.string.unit_ft));
            mdetail_czsd_up.setText(getString(R.string.unit_ft));
            mdetail_zdbf_up.setText(getString(R.string.unit_in));
            mdetail_zxbf_up.setText(getString(R.string.unit_in));
            mdetail_pjbf_up.setText(getString(R.string.unit_in));
        }

//        initnamehead();//添加姓名

        /**得到步数**/
        int height2 = Integer.valueOf(Utils.gethight(BTNotificationApplication.getInstance()));//获取用户身高
        int feetweek2 = (int)(height2*0.45);// 走路步长   170*0.45  = 76.5
        double mile22 = (int) gpsPoint.getMile();
        int gps_bushu = (int)(mile22 * 100 / feetweek2);//得到步数 （路程/步长）

        /**计算配速**/
        String arrPs[] = Utils.getPace(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile()));//得到配速 数组
        String m = arrPs[0];//分
        String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
        double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
        /**计算最大摄氧量**/
        //推测公式为：Vo2max=6.70-2.28 x 性别+0.056 x 时间（s）(健康成人，其中性别：男=1，女=2)
        sex = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER,
                SharedPreUtil.SEX);
        int sexInt = 1;
        if ("0".equals(sex) || "".equals(sex)) {
            sexInt = 1;
        } else if ("1".equals(sex)) {
            sexInt = 2;
        }
        Double maxSyl = Utils.decimalTo2(6.70 - 2.28 * sexInt + 0.056 * Double.valueOf(gpsPoint.getsTime()), 2);

        /**根据海拔计算累计上升高度和累计下降**/
        List<String> altitude = new ArrayList<String>();//海拔集合
        /**得到所有的海拔*/
        String mLtitude = gpsPoint.getArraltitude().trim();
        if (!mLtitude.equals("")) {
            String[] arrLat = mLtitude.split("&");
            int latSize = arrLat.length;
            for (int i = 0; i < latSize; i++) {
                altitude.add(arrLat[i]);
            }
        }

        int up = 0;
        int down = 0;
        for (int i = 1; i < altitude.size(); i++) {
            double c = Double.valueOf(altitude.get(i)) - Double.valueOf(altitude.get(i - 1));
            if (c > 0) {//计算海拔累计上升
                up += c;
            } else if (c < 0) {//计算海拔累计下降
                down += c;
            }
        }
        down=Math.abs(down);
        /**计算垂直速度  min/米**/
        int total=up+down;
//        int countTime = Integer.valueOf(gpsPoint.getsTime())/60;    double sTime = Double.parseDouble(gpsPoint.getsTime());
        int countTime = (int)Double.parseDouble(gpsPoint.getsTime())/60;
        if (countTime<1){
            countTime =1;
        }
        int vSpeed=total/countTime;


        Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
        Double time = Double.valueOf(gpsPoint.getsTime()) / 3600;
        Double sudu = 0.00;
        if(isMetric) {
            sudu = Utils.decimalTo2(mile / 1000 / time, 2); //小时每公里   速度值   TODO  ---- 一个值，总距离/总时间
        }else{
            sudu = Utils.decimalTo2(Utils.getUnit_km(mile / 1000)/ time, 2); //小时每公里   速度值   TODO  ---- 一个值，总距离/总时间
        }

        ////////////////////// todo --- 游泳次数赋值 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        tv_zts.setText(gpsPoint.getCalorie());              // todo ----     tv_zts  总趟数
        tv_zhss.setText(gpsPoint.getmCurrentSpeed());       // todo ----     tv_zhss 总划水数
        tv_yyhscs.setText(gpsPoint.getHeartRate());          // todo ----     tv_yyhscs 仰泳划水次数
        tv_wyhscs.setText(gpsPoint.getAve_step_width());     // todo ----     tv_wyhscs 蛙泳划水次数
        tv_dyhscs.setText(gpsPoint.getArraltitude());       // todo ----     tv_dyhscs 蝶泳划水次数
        tv_zzyhscs.setText(gpsPoint.getArrcadence());       // todo ----     tv_zzyhscs 自由泳划水次数
        tv_qtyzhscs.setText(gpsPoint.getPauseTime());        // todo ----     tv_qtyzhscs 其他泳姿划水次数


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //得到总时间 总距离配速
      /*  String totalPs = gpsPoint.getArrTotalSpeed();
        if (!totalPs.equals("") && !totalPs.equals("0")) {//TODO 手机配速
            Double psSum = 0.00;
            if(totalPs.indexOf("&") !=-1) {
                String[] arrPs2 = totalPs.split("&");
                int psSize = arrPs2.length;
                for (int i = 0; i < psSize; i++) {
                    if(arrPs2[i].contains("'")){
                        String[] ss = arrPs2[i].split("'");
                        String psok = ss[0] + "." + ss[1];
                        Double psValue = Double.valueOf(psok);  //  Double psValue = Double.valueOf(arrPs[i]);
                        if (psValue > 0) {
                            psList.add(psValue);
                            psSum += psValue;
                        }
                    }else {
                        Double psValue = Double.valueOf(arrPs2[i]);
                        if (psValue > 0) {
                            psList.add(psValue);
                            psSum += psValue;
                        }
                    }
                }

                double max = Collections.min(psList);//得到集合中最小值
                double min = Collections.max(psList);//得到集合中最大值

                String maxPs = getPeisu(String.valueOf(max));
                String minPs = getPeisu(String.valueOf(min));
                pjPs = getPeisu(String.valueOf(psSum/psList.size()));  // 应该过滤掉 配速为 0 的
                //平均配速
                mdetail_pjps.setText(pjPs);
                mdetail_zgps.setText(maxPs + "");  // minPs
                mdetail_zdps.setText(minPs + "");
            }
        }else{//TODO  --- 手表配速
            //得到手表配速 手表只有每公里的配速
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")
                    || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")){ //mtk
                String avgSpeed =  gpsPoint.getmCurrentSpeed();//获取平均配速    setArrTotalSpeed
                String arrSpeed =  gpsPoint.getArrTotalSpeed(); // 获取配速数组  ---- 平均配速是否根据配速数组来计算的  ？？？？？？？？
                if(!StringUtils.isEmpty(arrSpeed) || arrSpeed.equals("0")){
                    mdetail_zgps.setText("--");
                    mdetail_zdps.setText("--");
                    mdetail_pjps.setText("--");
                }else {
                    if(arrSpeed.contains("&")){
                        String[] arrWatchPs = arrSpeed.split("&");
                        watchPsList.clear();
                        for (int i = 0; i < arrWatchPs.length; i++) {
                            if (!arrWatchPs[i].equals("")) {
                                watchPsList.add((int)Math.round(Double.parseDouble(arrWatchPs[i])));
                            }
                        }
                        int max = Collections.min(watchPsList);//得到集合中最小值
                        int min = Collections.max(watchPsList);//得到集合中最大值
                        mdetail_zgps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", max / 60, max % 60) + "");  // min，miao
                        mdetail_zdps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", min / 60, min % 60) + "");
                        mdetail_pjps.setText(avgSpeed);
                    }
                }
            }else { // H872等
                String watchPs = gpsPoint.getSpeed();
                if (!watchPs.equals("")) {
                    String[] arrWatchPs = watchPs.split("&");
                    for (int i = 0; i < arrWatchPs.length; i++) {
                        if (!arrWatchPs[i].equals("")) {
                            watchPsList.add((int)Math.round(Double.parseDouble(arrWatchPs[i])));
                        }
                    }
                    int max = Collections.min(watchPsList);//得到集合中最小值
                    int min = Collections.max(watchPsList);//得到集合中最大值

                    int totalWatchPs = 0;
                    for (int j = 0; j < watchPsList.size(); j++) {
                        totalWatchPs += watchPsList.get(j);
                    }
                    int pjPs = totalWatchPs / watchPsList.size();//平均配速
                    *平均配速*
                    mdetail_pjps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''",pjPs/60,pjPs%60) + "");
                    mdetail_zgps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", max / 60, max % 60) + "");  // min，miao
                    mdetail_zdps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", min / 60, min % 60) + "");
                }
            }
        }

        try {
            if (Integer.parseInt(mdetail_zdps.getText().toString().split("'")[0]) > 1000) {
                mdetail_zdps.setTextSize(24);
            }

            if (Integer.parseInt(mdetail_zgps.getText().toString().split("'")[0]) > 1000) {
                mdetail_zgps.setTextSize(24);
            }

            if (Integer.parseInt(mdetail_pjps.getText().toString().split("'")[0]) > 1000) {
                mdetail_pjps.setTextSize(24);
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/


        mdetail_date.setText(gpsPoint.getDate() + "");    // 运动结束的时间
        if(isMetric){
            mtv_showdic.setText(Utils.decimalTo2(mile / 1000, 2) + "");
//            mdetail_xiaohao.setText(gpsPoint.getCalorie() + "");//千卡
        }else{
            mtv_showdic.setText(Utils.decimalTo2(Utils.getUnit_km(mile / 1000), 2) + "");
//            mdetail_xiaohao.setText(Utils.decimalTo2(Utils.getUnit_kal(Double.parseDouble(gpsPoint.getCalorie())),1)+ "");//千焦
        }
        //mtv_showdic.setText(Utils.decimalTo2(mile / 1000, 2) + "");
        mtv_showtime.setText(gpsPoint.getSportTime() + "");   // 运动结束是的运动时长
        //mdetail_xiaohao.setText(gpsPoint.getCalorie() + "");//千卡

      /*  if(gpsPoint.getDeviceType().equals("1")){  //   2：手表   1： 手机
            mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int) sec) + "");// 配速
        }else { // 手表
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
                String avgSpeed =  gpsPoint.getmCurrentSpeed();//获取平均配速    setArrTotalSpeed
                if(!StringUtils.isEmpty(avgSpeed)){
                    int avgPeisu = (int)Math.round(Double.parseDouble(avgSpeed));
                    int fen = avgPeisu/60;
                    int miao  = avgPeisu%60 ;
                    mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen , miao) + "");
                }else {
                    mdetail_peisu.setText("--");
                }
            }else {
                mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int) sec) + "");// 配速
            }
        }
        //速度
        mdetail_sudu.setText(sudu + "");  //TODO ----  速度值
        if(gpsPoint.getDeviceType().equals("1")) {   //   2：手表   1： 手机
            mdetail_bushu.setText(gps_bushu + "");
        }else{
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")
                    || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")){
                mdetail_bushu.setText(gpsPoint.getStep().equals("0") ? gps_bushu + "" : gpsPoint.getStep());
            }else{
                if((SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) &&  gpsPoint.getSportType().equals("11")){
                    mdetail_bushu.setText("--");  // MTK设备骑行模式不需要步数
                }else {
                    String step = gpsPoint.getStep();
                    if(step.equals("0")) {
                        mdetail_bushu.setText(gps_bushu + "");
                    }else{
                        mdetail_bushu.setText(step + "");
                    }
                }

            }
        }

        mdetail_zdsyl.setText(maxSyl + "");//最大摄氧量
        mdetail_xlqd.setText("--" + "");//最大摄氧量
        if (gpsPoint.getSportType().trim().equals("4")) {//登山跑 TODO--- 之前为3 ，现在为 5
            mdetail_ljps.setText(up + "");
            mdetail_ljxj.setText(down + "");
            mdetail_czsd.setText(vSpeed + "");
        } else {
            mdetail_ljps.setText("--" + "");
            mdetail_ljxj.setText("--" + "");
            mdetail_czsd.setText("--" + "");
        }
        String arrHeartRate = gpsPoint.getArrheartRate();

        if (gpsPoint.getDeviceType().equals("1")) {//手机心率
            mdetail_zdxl.setText("--" + "");
            mdetail_zxxl.setText("--" + "");
            mdetail_pjxl.setText("--" + "");
        } else {//手表心率值
            List<Integer> xlList = getHeartRate(arrHeartRate);
            if (xlList.size() > 0) {
                int max = Collections.max(xlList);//得到集合中最大值
                int min = Collections.min(xlList);//得到集合中最小值
                int rate = 0;
                for (int i = 0; i < xlList.size(); i++) {
                    rate += xlList.get(i);
                }
                int pjRate = rate / xlList.size();//得到集合中平均值
                mdetail_zdxl.setText(max + "");
                mdetail_zxxl.setText(min + "");
                mdetail_pjxl.setText(pjRate + "");
                String strength ;
                if (pjRate<=120){
                    strength = "C";
                }else if(120<=pjRate&&pjRate<150){
                    strength = "B";
                }else {
                    strength = "A";
                }
                mdetail_xlqd.setText(strength+ "");   // 训练强度赋值
            } else {
                mdetail_zdxl.setText("--" + "");
                mdetail_zxxl.setText("--" + "");
                mdetail_pjxl.setText("--" + "");
                mdetail_xlqd.setText("--" + "");
            }
        }*/

      /*  if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //mtk
            if(gpsPoint.getDeviceType().equals("2")){   // 设备类型    2：手表   1： 手机   TODO---- 手表端步幅
                mdetail_zdbf.setText("--" + "");   //手表端步幅需要*100
                mdetail_zxbf.setText("--" + "");
                if(isMetric) {
                    mdetail_pjbf.setText(Float.valueOf(gpsPoint.getAve_step_width()) + "");
                }else{
                    mdetail_pjbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getAve_step_width()))) + "");
                }
            }else {   // TODO---- 手机端步幅
                if(Integer.valueOf(gpsPoint.getMin_step_width()) < 30 ){
                    gpsPoint.setMin_step_width("30");
                }
                if(Integer.valueOf(gpsPoint.getMax_step_width()) > 160 ){
                    gpsPoint.setMax_step_width("160");
                }

                if(!isMetric){
                    gpsPoint.setMin_step_width((int)(Utils.getUnit_cm(Double.parseDouble(gpsPoint.getMin_step_width()))) + "");
                    gpsPoint.setMax_step_width((int)(Utils.getUnit_cm(Double.parseDouble(gpsPoint.getMax_step_width()))) + "");
                    gpsPoint.setAve_step_width((int)(Utils.getUnit_cm(Double.parseDouble(gpsPoint.getAve_step_width()))) + "");
                }
                mdetail_zdbf.setText(gpsPoint.getMax_step_width() + "");
                mdetail_zxbf.setText(gpsPoint.getMin_step_width() + "");
                mdetail_pjbf.setText(gpsPoint.getAve_step_width() + "");
            }
        }else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")
                || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")){
            mdetail_zdbf.setText(gpsPoint.getMax_step_width().equals("0") ? "30" : gpsPoint.getMax_step_width() + "");
            mdetail_zxbf.setText(gpsPoint.getMin_step_width().equals("0") ? "30" : gpsPoint.getMin_step_width() + "");
            mdetail_pjbf.setText(gpsPoint.getAve_step_width().equals("0") ? "30" : gpsPoint.getAve_step_width() + "");
        }else {
            if(gpsPoint.getDeviceType().equals("2")){   // 设备类型    2：手表   1： 手机   TODO---- 手表端步幅

                if(Float.valueOf(gpsPoint.getMin_step_width()) < 0.30 ){
                    gpsPoint.setMin_step_width("0.30");
                }
                if(Integer.valueOf(gpsPoint.getMax_step_width()) > 1.50 ){
                    gpsPoint.setMax_step_width("1." + "50");
                }
                if(isMetric) {
                    mdetail_zdbf.setText(Float.valueOf(gpsPoint.getMax_step_width()) * 100 + "");   //手表端步幅需要*100
                    mdetail_zxbf.setText(Float.valueOf(gpsPoint.getMin_step_width()) * 100 + "");
                    mdetail_pjbf.setText(Float.valueOf(gpsPoint.getAve_step_width()) * 100 + "");
                }else{
                    mdetail_zdbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getMax_step_width()) * 100)) + "");   //手表端步幅需要*100
                    mdetail_zxbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getMin_step_width()) * 100)) + "");
                    mdetail_pjbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getAve_step_width()) * 100)) + "");
                }
            }else {   // TODO---- 手机端步幅

                if(Integer.valueOf(gpsPoint.getMin_step_width()) < 30 ){
                    gpsPoint.setMin_step_width("30");
                }
                if(Integer.valueOf(gpsPoint.getMax_step_width()) > 160 ){
                    gpsPoint.setMax_step_width("160");
                }
                if(isMetric) {
                    mdetail_zdbf.setText(gpsPoint.getMax_step_width() + "");
                    mdetail_zxbf.setText(gpsPoint.getMin_step_width() + "");
                    mdetail_pjbf.setText(gpsPoint.getAve_step_width() + "");
                }else{
                    mdetail_zdbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getMax_step_width()))) + "");
                    mdetail_zxbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getMin_step_width()))) + "");
                    mdetail_pjbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getAve_step_width())))+ "");
                }
            }
        }

        if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //mtk

            if(gpsPoint.getDeviceType().equals("2")) {   //todo --- 手表端数据 设备类型    2：手表   1： 手机
                String mcadence = gpsPoint.getArrcadence();  // G703S手表端无 步频数组
                mdetail_zdbp.setText("--" + "");
                mdetail_zxbp.setText("--" + "");
                mdetail_pjbp.setText(mcadence);
            }else { // 手机端步频
                String totalca = gpsPoint.getArrcadence();   //TODO 手机端步频值很大 ，待跟进，可以是调试运动轨迹引起
                buPinglist = getBuPing(totalca);
                if (buPinglist.size() > 0) {
                    maxBp = Collections.max(buPinglist);//得到集合中最大值
                    minBp = Collections.min(buPinglist);//得到集合中最小值
                    int rate = 0;
                    for (int i = 0; i < buPinglist.size(); i++) {
                        rate += buPinglist.get(i);
                    }
                    int pjBuping = rate / buPinglist.size();//得到集合中平均值
                    mdetail_zdbp.setText(maxBp + "");
                    mdetail_zxbp.setText(minBp + "");
                    mdetail_pjbp.setText(pjBuping + "");
                } else {
                    mdetail_zdbp.setText("--" + "");
                    mdetail_zxbp.setText("--" + "");
                    mdetail_pjbp.setText("--" + "");
                }
            }
        }else { // H872
            // todo 得到步频
            String totalca = gpsPoint.getArrcadence();
            buPinglist = getBuPing(totalca);
            if (buPinglist.size() > 0) {
                maxBp = Collections.max(buPinglist);//得到集合中最大值
                minBp = Collections.min(buPinglist);//得到集合中最小值
                int rate = 0;
                for (int i = 0; i < buPinglist.size(); i++) {
                    rate += buPinglist.get(i);
                }
                int pjBuping = rate / buPinglist.size();//得到集合中平均值
                mdetail_zdbp.setText(maxBp + "");
                mdetail_zxbp.setText(minBp + "");
                mdetail_pjbp.setText(pjBuping + "");
            } else {
                mdetail_zdbp.setText("--" + "");
                mdetail_zxbp.setText("--" + "");
                mdetail_pjbp.setText("--" + "");
            }
        }
        mdetail_sjsc.setText(gpsPoint.getSportTime() + "");
        mdetail_ztsc.setText(gpsPoint.getPauseTime() + "");
        int pauseNumber = 0;
        try {
            pauseNumber = Integer.parseInt(gpsPoint.getPauseNumber()) < 0
                    ? 0 : Integer.parseInt(gpsPoint.getPauseNumber());
        }catch (Exception e){
            e.printStackTrace();
        }
        mdetail_ztcs.setText(pauseNumber + "");

        int psInt = 0;
        if(!StringUtils.isEmpty(pjPs) && pjPs.contains("'")){  // %1$02d'%2$02d''
            String[] ps = pjPs.split("'");
            psInt = Integer.valueOf(ps[0]);
        }
        if(maxBp > 200 || psInt <= 3 && psInt> 0){ //最大步频大于200 或 平均配速小于3分钟
            sportdataerror_tv.setVisibility(View.VISIBLE);
//            maxBp = 0;
//            psInt = 0;
        }else {
            sportdataerror_tv.setVisibility(View.GONE);
        }*/
    }

    private void setUserName() {
        if (Gdata.getMid() != Gdata.NOT_LOGIN) {
            String photoName = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.FACE);
            String path = FileUtils.SDPATH + photoName;
            Log.e("MyDataActivity ", " 显示的图片路径：" + path);
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if(bitmap != null)
                    detail_icon.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));
                Log.e("MyDataActivity ", " 显示本地图片");
            }
            detail_name.setText(Gdata.getPersonData().getUsername());
        }else{
            detail_icon.setImageResource(R.drawable.head_men);
            detail_name.setText(getString(R.string.not_set_info));
        }
    }

    private String getPeisu(String ps) {
        String arrPs[] = ps.split("\\.");
        String m = arrPs[0];//分
        String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
        double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
        if(!isMetric){
            int second = Utils.getUnit_pace(Integer.parseInt(m) * 60 + (int)sec);
            m = second / 60 + "";
            sec = second % 60;
        }
        String peisu = String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int) sec);
        return peisu;
    }


    private List<Integer> getBuPing(String cadencelist) {
        if (!cadencelist.equals("")) {//得到步频
            String[] arrRate = cadencelist.split("&");
            int psSize = arrRate.length;
            for (int i = 0; i < psSize; i++) {
                double psValue = (Double.valueOf(arrRate[i]));
                int value = (int)psValue;
                if (value > 0) {
                    buPinglist.add(value);
                }
            }
        }
        return buPinglist;
    }

    private List<Integer> getHeartRate(String arrHeartRate) {
        if (!arrHeartRate.equals("")) {//得到心率
            String[] arrRate = arrHeartRate.split("&");
            int psSize = arrRate.length;
            for (int i = 0; i < psSize; i++) {
                int psValue = (int)Math.round(Double.parseDouble(arrRate[i]));
                if (psValue > 0) {
                    xlList.add(psValue);
                }
            }
        }
        return xlList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode ==-1){
            Intent intent = new Intent();
            intent.setClass(SportHistoryActivityForMtkYouyong.this, NewWaterMakActivity.class);   // WaterMakActivity

            Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
            String distance = Utils.decimalTo2(mile / 1000, 2) + "";// 里程/千米

            intent.putExtra("distance",distance);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_sporthistory_photo:  // 拍照
//                Toast.makeText(getApplicationContext(),R.string.developed,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Uri uri = Uri.fromFile(new File(mFilePath));

                Uri uri;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                    uri = Uri.fromFile(new File(mFilePath));
                }else{
                    uri = TUriParse.getUriForFile(SportHistoryActivityForMtkYouyong.this,new File(mFilePath));
                }

                // 指定存储路径，这样就可以保存原图了
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1);
                break;

            case R.id.back:
                finish();
                break;
            case R.id.ib_sporthistory_share:     // 点击分享
                if (isRunning) {
                    return;
                }
                isRunning = true;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
//                        showShare();
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //详情数据页面分享
                            if (!NetWorkUtils.isConnect(SportHistoryActivityForMtkYouyong.this)) {
                                Toast.makeText(SportHistoryActivityForMtkYouyong.this, getString(R.string.my_network_disconnected), Toast.LENGTH_SHORT).show();
                            } else {
//                                if( Utils.isFastClick()) {
                                if(OnekeyShare.isShowShare){ // todo ---- 弹出分享框了
                                    OnekeyShare.isShowShare = false;
                                    showShareNotmap(MainService.PAGE_INDEX_SPORT_DETAILED_DATA);// todo  --- 运动模式详细数据页面
                                }
                            }
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        isRunning = false;
                    }
                }, 1400);
                break;
        }
    }

    private void showShareNotmap(int pageIndex) {
//        if(pageIndex == MainService.PAGE_INDEX_SPORT_DETAILED_DATA){   // 运动模式详细数据页面
//            ScreenshotsShare.savePicture(ScreenshotsShare.getViewBitmap(this, DetailedFragment.detailfragment_sc), filePath, fileName);   // 滚动分享OK
//        }else if(pageIndex == MainService.PAGE_INDEX_SPORT_SPEED_DETAILS){   // 运动模式配速详情页面
//            ScreenshotsShare.savePicture(ScreenshotsShare.getViewBitmap(this, SpeedFragment.speedfragment_sc), filePath, fileName);   // 滚动分享OK
//        }else if(pageIndex == MainService.PAGE_INDEX_SPORT_MOTION_CHART){   // 运动模式运动图表页面
//            ScreenshotsShare.savePicture(ScreenshotsShare.getViewBitmap(this, MotionChartFragment.motionchart_sc), filePath, fileName);   // 滚动分享OK
//        }

        ScreenshotsShare.savePicture(ScreenshotsShare.getViewBitmap(this, detailfragment_sc), filePath, fileName);   // 滚动分享OK

        //ShareSDK.initSDK(this);
        mapPackageName = setImage(this);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://fundoshouhu.szkct.cn/funfit.html");
        oks.setTitleUrl("http://www.fundo.cc");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(getString(R.string.welcome_funrun));
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(detailPath);// 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        // oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(getString(R.string.welcome_funrun));
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("");
        if (android.os.Build.VERSION.SDK_INT < 21) {
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        } else {
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        }
        // 启动分享GUI
        oks.show(this);
    }



//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
//            webview.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
