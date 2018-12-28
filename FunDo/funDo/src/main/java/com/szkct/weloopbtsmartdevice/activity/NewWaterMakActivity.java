package com.szkct.weloopbtsmartdevice.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.adapter.ViewPagerAdapter;
import com.szkct.takephoto.uitl.TUriParse;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.ScreenshotsShare;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.CustomerViewPager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

//import com.szkct.weloopbtsmartdevice.dialog.Gdata;
//import com.szkct.weloopbtsmartdevice.util.NewUploadDataUtil;
//import com.szkct.weloopbtsmartdevice.view.MyTrajectoryFitView;
//import cn.sharesdk.sina.weibo.SinaWeibo;

//import cn.sharesdk.demo.wxapi.onekeyshare.OnekeyShare;

/**
 * @author zhaixiang$.
 * @explain
 * @time 2017/5/24$ 11:57$.
 */
public class NewWaterMakActivity extends Activity implements PlatformActionListener {
    private final static String TAG = NewWaterMakActivity.class.getName();

    private int[] mImgIds;
    private int[] mImgIdsForEn;
    private LinearLayout mGallery;
    private LayoutInflater mInflater;
    private Bitmap bitmap;
    private ImageView imageView;
    private FileInputStream fileInputStream = null;
    private Bitmap weaterMakBitMap;
    private String mFilePath;
    private MyThread myThread;
    private String sportDistance = "";

    private  float mDist;
    String sportTime;
    String maxspace;
    String calorie;
    String avgspace;
    String ave_step_width;
    String ave_rate;
    String arrcadence;
    String[] gdPoints;

    private CustomerViewPager vp;
    private View homeView, analysis;
    private ImageView moreshow,morenotshow;//更多点击按钮和 灰色更多点击按钮
    private ImageView wenxinshare,wenxinpyquanshare,qqshare,instegramshare,messageshare,lingyingshare,facebookshare;//微信，微信朋友圈，qq,instegram,message,领英，facebook
    private ImageView xinlangshare,qqzoneshare,twittershare,downshare,shareto_whatsapp;//微博，QQ空间，twitter，下载
    private List<View> views;
    private ViewPagerAdapter vpAdapter;
    private RelativeLayout shareall_tone;//分享控件

    View rl_newphoto;//新增加的水印  10>
    TextView tv_key1,tv_key2,tv_key3,tv_key4,tv_key5,tv_value1,tv_value2,tv_value3,tv_value4,tv_value5;
    TextView share_name,share_data;
    ImageView share_my_headphoto;
//    MyTrajectoryFitView myTrajectoryFitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }

        getIntentData();

        sportDistance = getIntent().getStringExtra("distance") + "km";   //  getString(R.string.kilometre)

        if(mFilePath==null) {
            // 获取SD卡路径
            mFilePath = Environment.getExternalStorageDirectory().getPath();
            // 文件名
            mFilePath = mFilePath + "/" + "photo.png";
        }
        try {
            fileInputStream = new FileInputStream(mFilePath);
            // 把流解析成bitmap
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            /** 某些手机用decodeStream解析出的图片会自动旋转 如:LG G3 所以在这旋转一下 */
            int degree = getBitmapDegree(mFilePath);
            if(degree != 0){
                bitmap = rotateBitmapByDegree(bitmap,getBitmapDegree(mFilePath));
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        setContentView(R.layout.activity_new_water_mak);
        imageView = (ImageView) findViewById(R.id.im_photo);
        findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (weaterMakBitMap != null){
                myThread = new MyThread();
                myThread.start();
//                Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.save_pic_success) +appDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplication(), "图片保存成功", Toast.LENGTH_SHORT).show();
//                }else{
//
//                }
            }
        });

        findViewById(R.id.bt_share).setOnClickListener(new View.OnClickListener() {  // 分享
            @Override
            public void onClick(View v) {
//                showShare(weaterMakBitMap);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showShare(weaterMakBitMap);
                    }
                }).start();
            }
        });

        imageView.setImageBitmap(bitmap);
        mInflater = LayoutInflater.from(this);
        initData();
        initView();
        setheadpandname();
    }

    /**
     * 读取图片的旋转的角度
     */
    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            /** 获取图片的旋转信息 */
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     */
    public Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    private void getIntentData() {
        mDist = Float.valueOf(getIntent().getStringExtra("distance"));
        sportTime=getIntent().getStringExtra("sportTime");
        maxspace=getIntent().getStringExtra("maxspace");
        calorie=getIntent().getStringExtra("calorie");
        avgspace=getIntent().getStringExtra("avgspace");
        ave_step_width=getIntent().getStringExtra("ave_step_width");
        ave_rate=getIntent().getStringExtra("ave_rate");
        arrcadence=getIntent().getStringExtra("arrcadence");
        gdPoints=getIntent().getStringArrayExtra("gdPoints");
        mFilePath=getIntent().getStringExtra("imgpath");
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Log.d("ShareSDK", "onComplete ---->  分享成功");
        platform.isClientValid();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.d("ShareSDK", "onError ---->  分享失败" + throwable.getStackTrace().toString());
        Log.d("ShareSDK", "onError ---->  分享失败" + throwable.getMessage());
        throwable.getMessage();
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Log.d("ShareSDK", "onCancel ---->  分享取消");
    }


    class MyThread extends Thread {

        @Override
        public void run() {
//            Bitmap bm =;
            saveImageToGallery(getApplicationContext(),((BitmapDrawable) (imageView).getDrawable()).getBitmap());
        }
    }

    private void initData() {
        mImgIds = new int[]{R.drawable.watermark_s_1, R.drawable.watermark_s_2, R.drawable.watermark_s_3,     //todo --- 黑色背景图片
                R.drawable.watermark_s_4, R.drawable.watermark_s_5, R.drawable.watermark_s_6, R.drawable.watermark_s_7,
                R.drawable.watermark_s_8, R.drawable.watermark_s_9, R.drawable.watermark_s_10,
                //新加水印
//                R.drawable.watermark_preview_15, R.drawable.watermark_preview_11, R.drawable.watermark_preview_12, R.drawable.watermark_preview_13, R.drawable.watermark_preview_14, R.drawable.watermark_preview_15\
        };

        mImgIdsForEn = new int[]{R.drawable.watermark_s_11, R.drawable.watermark_s_12, R.drawable.watermark_s_13,     //todo --- 黑色背景图片
                R.drawable.watermark_s_14, R.drawable.watermark_s_15, R.drawable.watermark_s_16
        };
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mGallery = (LinearLayout) findViewById(R.id.id_gallery);

        String languageLx = Utils.getLanguage();
        if (languageLx.equals("zh")) {  // 中文
            for (int i = 0; i < mImgIds.length; i++) {

                View view = mInflater.inflate(R.layout.activity_index_gallery_item, mGallery, false);
                ImageView img = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);
                img.setImageResource(mImgIds[i]);
                img.setId(i);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap waterMak = null;

                        Bitmap waterLogo = null;
//                        if(v.getId()>=10) {   // todo --- I am on my way 之后的图片
//                            rl_newphoto.setVisibility(View.VISIBLE);
//                            newTvInvisibleAll();//影长全部
//                            weaterMakBitMap = createBitmap(bitmap, null, null);
//                        }else {    // todo --- I am on my way 之前的图片
//                            rl_newphoto.setVisibility(View.INVISIBLE);
//                        }

                        waterLogo = BitmapFactory.decodeResource(getResources(), R.drawable.share_logo); // logo

                        switch (v.getId()) {
                            case 0:
                                weaterMakBitMap = createBitmap(bitmap, null, null);
                                break;
                            case 1:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_2);  // 无论
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance); //todo --- 添加 运动距离
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 2:
                                if (mDist >= 42.19) {
                                    waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_3);  //全马  TODO ---- 需换图
//                                weaterMakBitMap = createBitmap(bitmap, waterMak, null);
                                    weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, null);
                                } else {
                                    weaterMakBitMap = createBitmap(bitmap, null, null);
                                    showDialog(getString(R.string.watermark_tip_quanma));
                                }
                                break;
                            case 3:
                                if (mDist >= 21.09) {
                                    waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_4);  // 半马
//                                weaterMakBitMap = createBitmap(bitmap, waterMak, null);
                                    weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, null);
                                } else {
                                    weaterMakBitMap = createBitmap(bitmap, null, null);
                                    showDialog(getString(R.string.watermark_tip_banma));
                                }

                                break;
                            case 4:
                                // 直接设置距离   21.09km
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_5);  //todo --- 需要一张背景全黑的图片
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);  //  weaterMakBitMap = createBitmap(bitmap, null, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 5:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_6);  // 汗水
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 6:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_7);  // just do it    watermark_7   watermark_s_7
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 7:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_8);  // 坚持
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 8:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_9);  // 谁与争锋
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 9:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_10); // I am on
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);

                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 10:
                                setTvKeyValue(3,"最远距离",""+mDist);
                                setTvKeyValue(4,"最长时间",sportTime);
                                setTvKeyValue(5,"最快配速",maxspace);
                                break;
                            case 11:
                                setTvKeyValue(1,"总公里",""+mDist);
                                setTvKeyValue(3,getString(R.string.detailed_average_pace),avgspace);
                                setTvKeyValue(4,"时长小时/分/秒",sportTime);
                                setTvKeyValue(5,"消耗/千卡",calorie);
                                break;
                            case 12:
//                            setTvKeyValue(1,"总公里",""+mDist);
//                            myTrajectoryFitView.setVisibility(View.VISIBLE);
//                            RelativeLayout.LayoutParams rl= (RelativeLayout.LayoutParams) myTrajectoryFitView.getLayoutParams();
//                            rl.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                            rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                            rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                            myTrajectoryFitView.setLayoutParams(rl);
                                break;
                            case 13:
//                            setTvKeyValue(1,"总公里",""+mDist);
//                            setTvKeyValue(2,"消耗/千卡",calorie);
//                            setTvKeyValue(3,"时长小时/分/秒",sportTime);
//                            myTrajectoryFitView.setVisibility(View.VISIBLE);
//                            rl= (RelativeLayout.LayoutParams) myTrajectoryFitView.getLayoutParams();
//                            rl.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                            rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                            myTrajectoryFitView.setLayoutParams(rl);
                                break;
                            case 14:
                                setTvKeyValue(1,getString(R.string.detailed_average_stride_frequency),arrcadence);
                                setTvKeyValue(3,getString(R.string.detailed_average_pace),avgspace);
                                setTvKeyValue(4,getString(R.string.mean_heart),ave_rate);
                                setTvKeyValue(5,getString(R.string.detailed_average_stride),ave_step_width);
                                break;
                            case 15:
                                setTvKeyValue(3,"最远距离",""+mDist);
                                setTvKeyValue(4,"最长时间",sportTime);
                                setTvKeyValue(5,"最快配速",maxspace);
                                break;
                        }

//                    if (waterMak != null) {   // bitmap --- 手机拍照照片
//                        weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
//                    } else {
//                        weaterMakBitMap = createBitmap(bitmap, null, null);
//                    }

                        imageView.setImageBitmap(weaterMakBitMap);
                    }
                });
                mGallery.addView(view);
            }//////////////////
        }else { // todo ---- 非中文
            for (int i = 0; i < mImgIdsForEn.length; i++) {

                View view = mInflater.inflate(R.layout.activity_index_gallery_item, mGallery, false);
                ImageView img = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);
                img.setImageResource(mImgIdsForEn[i]);
                img.setId(i);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap waterMak = null;

                        Bitmap waterLogo = null;
//                        if(v.getId()>=10) {   // todo --- I am on my way 之后的图片
//                            rl_newphoto.setVisibility(View.VISIBLE);
//                            newTvInvisibleAll();//影长全部
//                            weaterMakBitMap = createBitmap(bitmap, null, null);
//                        }else {    // todo --- I am on my way 之前的图片
//                            rl_newphoto.setVisibility(View.INVISIBLE);
//                        }

                        waterLogo = BitmapFactory.decodeResource(getResources(), R.drawable.share_logo); // logo

                        switch (v.getId()) {
//                            case 0:
//                                weaterMakBitMap = createBitmap(bitmap, null, null);
//                                break;
                            case 0:
                                if (mDist >= 42.19) {
                                    waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_11);  //全马  TODO ---- 需换图
//                                weaterMakBitMap = createBitmap(bitmap, waterMak, null);
                                    weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, null);
                                } else {
								     weaterMakBitMap = createBitmap(bitmap, null, null);
                                    showDialog(getString(R.string.watermark_tip_quanma));
                                }


                                break;
                            case 1:
                                if (mDist >= 21.09) {
                                    waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_12);  // 半马
//                                weaterMakBitMap = createBitmap(bitmap, waterMak, null);
                                    weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, null);
                                } else {
								     weaterMakBitMap = createBitmap(bitmap, null, null);
                                    showDialog(getString(R.string.watermark_tip_banma));
                                }

                                break;
                            case 2:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_13);  // 无论
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance); //todo --- 添加 运动距离
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);

                                break;
                            case 3:
                                // 直接设置距离   21.09km
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_14);  //todo --- 需要一张背景全黑的图片
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);  //  weaterMakBitMap = createBitmap(bitmap, null, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 4:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_15);  // 汗水
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 5:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_16);  // just do it    watermark_7   watermark_s_7
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                          /*  case 7:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_8);  // 坚持
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 8:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_9);  // 谁与争锋
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 9:
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_10); // I am on
//                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);

                                weaterMakBitMap = createBitmap(bitmap,waterLogo, waterMak, sportDistance);
                                break;
                            case 10:
                                setTvKeyValue(3,"最远距离",""+mDist);
                                setTvKeyValue(4,"最长时间",sportTime);
                                setTvKeyValue(5,"最快配速",maxspace);
                                break;*/

                        }

                        imageView.setImageBitmap(weaterMakBitMap);
                    }
                });
                mGallery.addView(view);
            }//////////////////
        }



        shareall_tone= (RelativeLayout) findViewById(R.id.shareall_tone);
        /***************************************************************************************************************************/
        analysis = LayoutInflater.from(this).inflate(R.layout.activity_sharetoall_one, null);  //分享图表 前面
        moreshow = (ImageView)analysis.findViewById(R.id.shaall_more);
        wenxinshare= (ImageView)analysis.findViewById(R.id.shareto_wenxin);
        wenxinpyquanshare= (ImageView)analysis.findViewById(R.id.shareto_wenxinpyquan);
        qqshare= (ImageView)analysis.findViewById(R.id.shareto_qq);
//        instegramshare= (ImageView)analysis.findViewById(R.id.shareto_instgream);
//        messageshare= (ImageView)analysis.findViewById(R.id.shareto_msage);
        lingyingshare= (ImageView)analysis.findViewById(R.id.shareto_lingying);
        facebookshare= (ImageView)analysis.findViewById(R.id.shareto_facebook);
        moreshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(1);
            }
        });
        /***************************************************************************************************************************/
        homeView = LayoutInflater.from(this).inflate(R.layout.activity_sharetoall_two, null);  // 分享图表后半部
        morenotshow = (ImageView)homeView.findViewById(R.id.shaall_more_notshow);
        morenotshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(0);
            }
        });
//        xinlangshare= (ImageView)homeView.findViewById(R.id.shareto_weibo);
        qqzoneshare= (ImageView)homeView.findViewById(R.id.shareto_qqzone);
        twittershare= (ImageView)homeView.findViewById(R.id.shareto_twitter);
        shareto_whatsapp= (ImageView)homeView.findViewById(R.id.shareto_whatsapp);
        downshare= (ImageView)homeView.findViewById(R.id.shareto_down);
        /***************************************************************************************************************************/
        vp= (CustomerViewPager)findViewById(R.id.share_test_vpager);
        views = new ArrayList<View>();
        views.add(analysis);
        views.add(homeView);
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);   // 将运动和睡眠的页面装载进来
        /***************************************************************************************************************************/
        wenxinshare.setOnClickListener(new ShareClick());
        wenxinpyquanshare.setOnClickListener(new ShareClick());
        qqshare.setOnClickListener(new ShareClick());
//        instegramshare.setOnClickListener(new ShareClick());
//        messageshare.setOnClickListener(new ShareClick());
        lingyingshare.setOnClickListener(new ShareClick());
        facebookshare.setOnClickListener(new ShareClick());
//        xinlangshare.setOnClickListener(new ShareClick());
        qqzoneshare.setOnClickListener(new ShareClick());
        twittershare.setOnClickListener(new ShareClick());
        shareto_whatsapp.setOnClickListener(new ShareClick());
        downshare.setOnClickListener(new ShareClick());

        rl_newphoto=findViewById(R.id.rl_newphoto);
        rl_newphoto.setVisibility(View.INVISIBLE);
        tv_key1= (TextView) findViewById(R.id.tv_key1);
        tv_key2= (TextView) findViewById(R.id.tv_key2);
        tv_key3= (TextView) findViewById(R.id.tv_key3);
        tv_key4= (TextView) findViewById(R.id.tv_key4);
        tv_key5= (TextView) findViewById(R.id.tv_key5);
        tv_value1= (TextView) findViewById(R.id.tv_value1);
        tv_value2= (TextView) findViewById(R.id.tv_value2);
        tv_value3= (TextView) findViewById(R.id.tv_value3);
        tv_value4= (TextView) findViewById(R.id.tv_value4);
        tv_value5= (TextView) findViewById(R.id.tv_value5);
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/AGENCYR.TTF");
//        tv_value1.setTypeface(typeface);
//        tv_value2.setTypeface(typeface);
//        tv_value3.setTypeface(typeface);
//        tv_value4.setTypeface(typeface);
//        tv_value5.setTypeface(typeface);
//        share_name= (TextView) findViewById(R.id.share_name);
//        share_data= (TextView) findViewById(R.id.share_data);
//        share_my_headphoto= (ImageView) findViewById(R.id.share_my_headphoto);
//        myTrajectoryFitView= (MyTrajectoryFitView) findViewById(R.id.myTrajectoryFitView);
        if(gdPoints!=null) {
            ArrayList<float[]> list = new ArrayList<>();
            float jjjjx=0,jjjjy=0;//*xss进行偏差处理
            final float xss=1000000;
            for (int i = 0; i < gdPoints.length; i++) {
                String[] ss = gdPoints[i].split(":");
                if(i==0){
                    list.add(new float[]{0,0});
                    jjjjx= Float.parseFloat(ss[0]) * xss;
                    jjjjy= Float.parseFloat(ss[1]) * xss;
                }else{
                    list.add(new float[]{Float.parseFloat(ss[0]) * xss-jjjjx, Float.parseFloat(ss[1]) * xss-jjjjy});//经纬度放大  求差
                }
                //Log.e("hrj", "test: "+list.get(i)[0]+" "+list.get(i)[1] );
            }
//            if (gdPoints.length > 0)
//                myTrajectoryFitView.setData(list);
        }
    }

    private void newTvInvisibleAll(){
        tv_key1.setVisibility(View.INVISIBLE);
        tv_key2.setVisibility(View.INVISIBLE);
        tv_key3.setVisibility(View.INVISIBLE);
        tv_key4.setVisibility(View.INVISIBLE);
        tv_key5.setVisibility(View.INVISIBLE);
        tv_value1.setVisibility(View.INVISIBLE);
        tv_value2.setVisibility(View.INVISIBLE);
        tv_value3.setVisibility(View.INVISIBLE);
        tv_value4.setVisibility(View.INVISIBLE);
        tv_value5.setVisibility(View.INVISIBLE);
//        myTrajectoryFitView.setVisibility(View.INVISIBLE);
    }

    private void setTvKeyValue(int i,String key,String value){
        switch (i){
            case 1:
                tv_key1.setVisibility(View.VISIBLE);
                tv_value1.setVisibility(View.VISIBLE);
                tv_key1.setText(key);
                tv_value1.setText(value);
                break;
            case 2:
                tv_key2.setVisibility(View.VISIBLE);
                tv_value2.setVisibility(View.VISIBLE);
                tv_key2.setText(key);
                tv_value2.setText(value);
                break;
            case 3:
                tv_key3.setVisibility(View.VISIBLE);
                tv_value3.setVisibility(View.VISIBLE);
                tv_key3.setText(key);
                tv_value3.setText(value);
                break;
            case 4:
                tv_key4.setVisibility(View.VISIBLE);
                tv_value4.setVisibility(View.VISIBLE);
                tv_key4.setText(key);
                tv_value4.setText(value);
                break;
            case 5:
                tv_key5.setVisibility(View.VISIBLE);
                tv_value5.setVisibility(View.VISIBLE);
                tv_key5.setText(key);
                tv_value5.setText(value);
                break;
        }
    }

    private class ShareClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shareto_wenxin://微信
                    if( Utils.isFastClick()){showShare(0);}     
                    break;
                case R.id.shareto_wenxinpyquan://微信朋友圈
                    if( Utils.isFastClick()){  showShare(1);}
                    break;
                case R.id.shareto_qq://QQ
                    if( Utils.isFastClick()){ showShare(2);}
                    break;
//                case R.id.shareto_instgream://instgream
//                    if( Utils.isFastClick()){  showShare(3);}
//                    break;
//                case R.id.shareto_msage://msage
//                    if( Utils.isFastClick()){  showShare(4);}
//                    break;
                case R.id.shareto_lingying://领英
                    if( Utils.isFastClick()){  showShare(5);}
                    break;
                case R.id.shareto_facebook://facebook
                    if( Utils.isFastClick()){showShare(6);}
                    break;
//                case R.id.shareto_weibo://微博
//                    if( Utils.isFastClick()){showShare(7);}//这里要注意下
//                    break;
                case R.id.shareto_qqzone://QQ空间
                    if( Utils.isFastClick()){ showShare(8);}
                    break;
                case R.id.shareto_twitter://twitter
                    if( Utils.isFastClick()){ showShare(9);}
                    break;

                case R.id.shareto_whatsapp://whatsapp
                    if( Utils.isFastClick()){ showShare(10);}
                    break;
                case R.id.shareto_down://下载
                    ScreenshotsShare.savePicture(ScreenshotsShare.getShareViewBitmap(shareall_tone, shareall_tone.getWidth(), shareall_tone.getHeight()), filePath, fileName);
                    Toast.makeText(getApplicationContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public static Bitmap createBitmap(Bitmap src, Bitmap waterMak, String title) {
        Bitmap bitmap = null;
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        Log.e(TAG, "createBitmap: " + w + " " + h);
        if (waterMak != null) {
            bitmap = scaleWithWH(waterMak, w, h);
        } else {
            return src;
        }
        int ww = waterMak.getWidth();
        int wh = waterMak.getHeight();

        Log.i("jiangqq", "w = " + w + ",h = " + h + ",ww = " + ww + ",wh = " + wh);
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        // 在src的右下角添加水印
        Paint paint = new Paint();
        //paint.setAlpha(100);
        mCanvas.drawBitmap(bitmap, 0, 0, paint);
        // 开始加入文字
        if (null != title) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(w/8);
            String familyName = "宋体";
            Typeface typeface = Typeface.create(familyName, Typeface.BOLD_ITALIC);
            textPaint.setTypeface(typeface);
            textPaint.setTextAlign(Paint.Align.CENTER);
//            mCanvas.drawText(title, w / 2, 25, textPaint);
            mCanvas.drawText(title, w / 2, h-w/40, textPaint);
        }
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        return newBitmap;
    }

    public static Bitmap createBitmap(Bitmap src, Bitmap waterLogo, Bitmap waterMak, String title) {  //todo  1:拍照的图片 2：Logo 3： 水印
        Bitmap bitmap = null;
        Bitmap bitmapLogo = null;
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();

        if (waterLogo != null) {
            bitmapLogo = scaleWithWHForLogo(waterLogo, w, h);  // todo --- 得到logo的图片
        }

        if (waterMak != null) {
            bitmap = scaleWithWH(waterMak, w, h);  // todo --- 得到水印的图片
        } else {
            return src;
        }


        int ww = waterMak.getWidth();
        int wh = waterMak.getHeight();

        Log.i("jiangqq", "w = " + w + ",h = " + h + ",ww = " + ww + ",wh = " + wh);
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);      // ARGB_8888
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);

//        mCanvas.drawBitmap(waterLogo, 10, 10, null);
        mCanvas.drawBitmap(bitmapLogo, 150, 150, null);
        // 在src的右下角添加水印
        Paint paint = new Paint();
        //paint.setAlpha(100);
        mCanvas.drawBitmap(bitmap, 0, 0, paint);
        // 开始加入文字
        if (null != title) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(250);
            String familyName = "宋体";
            Typeface typeface = Typeface.create(familyName, Typeface.BOLD_ITALIC);
            textPaint.setTypeface(typeface);
            textPaint.setTextAlign(Paint.Align.CENTER);
//            mCanvas.drawText(title, w / 2, 25, textPaint);
            mCanvas.drawText(title, w / 2, h-85, textPaint);
        }
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        return newBitmap;
    }

    /**
     * 缩放图片
     *
     * @param src
     * @param w
     * @param h
     * @return
     */
    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    public static Bitmap scaleWithWHForLogo(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
//            float scaleWidth = (float) (w / width);
//            float scaleHeight = (float) (h / height);
            float scaleWidth = (float)4;
            float scaleHeight = (float)6;
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    public  void saveImageToGallery(Context context, Bitmap bmp) {     // static
        // 首先保存图片
        final File appDir = new File(Environment.getExternalStorageDirectory(), "Fundo");  // Boohee
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.save_pic_success) + appDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });

        //发广播告诉相册有图片需要更新，这样可以在图册下看到保存的图片了
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(file);  // file  -- appDir
//        intent.setData(uri);
//        context.sendBroadcast(intent);

        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        Log.e("PICCOMPRESS", "广播通知相册成功！");
    }

    public final static void saveImage(Bitmap bmp) {   // TODO --- 将图片保存
        File appDir = new File(Environment.getExternalStorageDirectory(), "Fundo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myThread !=null){
//            myThread.destroy();
            myThread = null;
        }
    }

    private String filePath = Environment.getExternalStorageDirectory()
            + "/appmanager/funfit/";
    private String fileName = "screenshot_analysis.png";  // screenshot_analysis.png
    private String detailPath = filePath + File.separator + fileName;

    private void showShare(Bitmap bm) {
        ScreenshotsShare.savePicture(bm, filePath, fileName);  //之前注释了
        //  ShareSDK.initSDK(this);
        mapPackageName = setImage(this);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://fundoshouhu.szkct.cn/funfit.html");  // todo --- ??????
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
        if (Build.VERSION.SDK_INT < 21) {  // android.os.Build.VERSION.SDK_INT < 21
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
            Utils.onClickShareToQQ(getApplicationContext(), detailPath);
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
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);  // Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565   ---- Bitmap.Config.ARGB_4444 : Bitmap.Config.RGB_565
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    //弹出警报框
    private void showDialog(final String content) {  //todo ---- 还需要考虑 重新设置 目标后，应该将  isShowAlertDialog 置为 false
        final android.app.AlertDialog myDialog;
        myDialog = new android.app.AlertDialog.Builder(this).create();
        myDialog.show();

        myDialog.getWindow().setContentView(R.layout.alert_fence_dialog);
        TextView tv_alert_content = (TextView) myDialog.getWindow().findViewById(R.id.tv_alert_content);
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

    private void showShare(int type) {  // 分享
        ScreenshotsShare.savePicture(ScreenshotsShare.getShareViewBitmap(shareall_tone, shareall_tone.getWidth(), shareall_tone.getHeight()), filePath, fileName);
        Platform.ShareParams oks = new Platform.ShareParams();
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
        oks.setShareType(Platform.SHARE_IMAGE);
        if(0==type){shareToALL(oks, Wechat.NAME);}//微信     99999
        if(1==type){shareToALL(oks, WechatMoments.NAME);}//微信朋友圈
        if(2==type){ Utils.onClickShareToQQ(this, detailPath);}//QQ
        if(3==type){ actionShare_sms_email_facebook("com.instagram.android", this, "");}//instgream
//        if(4==type){shareToALL(oks, FacebookMessenger.NAME);}  //todo  ---- 启动不了 facebook  messenger
        if(5==type){actionShare_sms_email_facebook("com.linkedin.android", this, "");}//领英
        if(6==type){ actionShare_sms_email_facebook("com.facebook.katana", this, "");}//facebook
//        if(7==type){shareToALL(oks, SinaWeibo.NAME);}//微博  不成功换掉jar 换成 TencentWeibo这个包（SinaWeibo.NAME）修改成TencentWeibo.NAME
        if(8==type){shareToALL(oks, QZone.NAME);}//QQ空间
        if(9==type){actionShare_sms_email_facebook("com.twitter.android", this, "");}//twitter
        if(10==type){actionShare_sms_email_facebook("com.whatsapp", this, "");}//WhatsApp   todo -- add 20180205

    }
    /**
     * 分享至》》》》   facebook  messenger
     */
    public void shareToALL(Platform.ShareParams sp ,String name) {
        Platform weibo = ShareSDK.getPlatform(name);
        weibo.setPlatformActionListener(this); // 设置分享事件回调
        weibo.share(sp);
    }

    /**
     * 设置头像
     */
    private void setheadpandname() {
//        share_name.setText(Gdata.getPersonData().getName());
//
//        NewUploadDataUtil.showUserImg(this,share_my_headphoto,Gdata.getPersonData().getIconPath());    //头像
//        share_data.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
    
}

