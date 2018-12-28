package com.szkct.weloopbtsmartdevice.main;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.ScreenshotsShare;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import cn.sharesdk.demo.wxapi.onekeyshare.OnekeyShare;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import com.szkct.takephoto.uitl.TUriParse;

/**
 * @author zhaixiang$.
 * @explain
 * @time 2017/5/24$ 11:57$.
 */
public class WaterMakActivity extends Activity {
    private final static String TAG = WaterMakActivity.class.getName();

    private int[] mImgIds;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDist = Float.valueOf(getIntent().getStringExtra("distance"));
        sportDistance = getIntent().getStringExtra("distance") + "km";   //  getString(R.string.kilometre)

        // 获取SD卡路径
        mFilePath = Environment.getExternalStorageDirectory().getPath();
        // 文件名
        mFilePath = mFilePath + "/" + "photo.png";
        try {
            fileInputStream = new FileInputStream(mFilePath);
            // 把流解析成bitmap
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_watermak);
        imageView = (ImageView) findViewById(R.id.im_photo);
        findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaterMakActivity.this.finish();
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
    }

    class MyThread extends Thread {

        @Override
        public void run() {
//            Bitmap bm =;
            saveImageToGallery(WaterMakActivity.this,((BitmapDrawable) (imageView).getDrawable()).getBitmap());
        }
    }

    private void initData() {
        mImgIds = new int[]{R.drawable.watermark_s_1, R.drawable.watermark_s_2, R.drawable.watermark_s_3,
                R.drawable.watermark_s_4, R.drawable.watermark_s_5, R.drawable.watermark_s_6, R.drawable.watermark_s_7,
                R.drawable.watermark_s_8, R.drawable.watermark_s_9, R.drawable.watermark_s_10};
    }

    private void initView() {
        mGallery = (LinearLayout) findViewById(R.id.id_gallery);

        for (int i = 0; i < mImgIds.length; i++) {

            View view = mInflater.inflate(R.layout.activity_index_gallery_item, mGallery, false);
            ImageView img = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);
            img.setImageResource(mImgIds[i]);
            img.setId(i);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap waterMak = null;
                    switch (v.getId()) {
                        case 0:
                            weaterMakBitMap = createBitmap(bitmap, null, null);
                            break;
                        case 1:
                            waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_2);  // 无论
                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance); //todo --- 添加 运动距离
                            break;
                        case 2:
                            weaterMakBitMap = createBitmap(bitmap, null, null);
                            if (mDist >= 42.19) {
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_3);  //全马  TODO ---- 需换图
                                weaterMakBitMap = createBitmap(bitmap, waterMak, null);
                            } else {
                                showDialog(getString(R.string.watermark_tip_quanma));
                            }
                            break;
                        case 3:
                            weaterMakBitMap = createBitmap(bitmap, null, null);
                            if (mDist >= 21.09) {
                                waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_4);  // 半马
                                weaterMakBitMap = createBitmap(bitmap, waterMak, null);
                            } else {
                                showDialog(getString(R.string.watermark_tip_banma));
                            }

                            break;
                        case 4:
                              // 直接设置距离   21.09km
                            waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_5);  //todo --- 需要一张背景全黑的图片
                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);  //  weaterMakBitMap = createBitmap(bitmap, null, sportDistance);
                            break;
                        case 5:
                            waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_6);  // 汗水
                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                            break;
                        case 6:
                            waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_7);  // just do it    watermark_7   watermark_s_7
                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                            break;
                        case 7:
                            waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_8);  // 坚持
                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                            break;
                        case 8:
                            waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_9);  // 谁与争锋
                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
                            break;
                        case 9:
                            waterMak = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_10); // I am on
                            weaterMakBitMap = createBitmap(bitmap, waterMak, sportDistance);
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
        }
    }

    public static Bitmap createBitmap(Bitmap src, Bitmap waterMak, String title) {
        Bitmap bitmap = null;
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
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
//        oks.setTitleUrl("http://fundoshouhu.szkct.cn/funfit.html");  // todo
        oks.setTitleUrl("http://www.fundo.cc");  //
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
            Utils.onClickShareToQQ(WaterMakActivity.this, detailPath);
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
            myDialog = new android.app.AlertDialog.Builder(WaterMakActivity.this).create();
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

}
