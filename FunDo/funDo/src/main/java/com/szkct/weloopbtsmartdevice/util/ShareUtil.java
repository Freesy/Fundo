package com.szkct.weloopbtsmartdevice.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.map.utils.ScreenShotHelper;
import com.szkct.takephoto.uitl.TUriParse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;

import static cn.sharesdk.onekeyshare.OnekeyShareTheme.CLASSIC;

public class ShareUtil {
    Context mCtx;
    private String filePath = Environment.getExternalStorageDirectory()
            + "/appmanager/fundoShare/";
    private String fileName = "screenshot_analysis.png";
    private String detailPath = filePath + File.separator + fileName;
    public  ShareUtil(Context ctx)
    {
        mCtx = ctx;
    }

    public void showShare(Bitmap bm,Activity activity,boolean isSavePicture) {   // todo --- 分享图片用的方法   ScreenshotsShare.savePicture(ScreenshotsShare.getViewBitmap(this, DetailedFragment.detailfragment_sc), filePath, fileName);   // 滚动分享OK
//        ScreenshotsShare.savePicture(ScreenshotsShare.takeScreenShot(activity, 1), filePath, fileName);   // todo --- 地图截屏页面不需要
        mapPackageName = setImage(activity);     //  999999999999999999999999
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(com.kct.fundoHealth.btnotification.common.Constants.SHARE_APP_URL);  // todo --- ??????
        oks.setTitleUrl("");

        // text是分享文本，所有平台都需要这个字段
        oks.setText("");

// url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(com.kct.fundoHealth.btnotification.common.Constants.SHARE_APP_URL);

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        String path = Environment.getExternalStorageDirectory()+ File.separator+shareFileDirectory + File.separator + fileName;
        oks.setImagePath(path);// 确保SDcard下面存在此张图片
//        oks.setImagePath(detailPath);// 确保SDcard下面存在此张图片

        // url仅在微信（包括好友和朋友圈）中使用
        // oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment(activity.getString(R.string.share_app_content));
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(activity.getString(R.string.app_name));
        oks.setSite("");

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(com.kct.fundoHealth.btnotification.common.Constants.SHARE_APP_URL);
        oks.setSiteUrl("");
        oks.setTheme(CLASSIC);
//        if (android.os.Build.VERSION.SDK_INT < 21) {
////            oks.setCustomerLogo(drawableToBitmap(activity.getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
////        } else {
////            oks.setCustomerLogo(drawableToBitmap(activity.getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
//////            oks.setCustomerLogo(drawableToBitmap(activity.getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
////        }

        if (android.os.Build.VERSION.SDK_INT < 21) {
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), mCtx.getResources().getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        } else {oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_linkedin)), mCtx.getResources().getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        }

        // 启动分享GUI
//        oks.setCallback(new ShareSDKModel());
        oks.show(activity);
    }


    public void showShare(int pageIndex) {  //分享
        ScreenshotsShare.savePicture(ScreenshotsShare.takeScreenShot((Activity) mCtx,pageIndex), filePath, fileName);
        //ShareSDK.initSDK(getActivity());
        mapPackageName=	setImage((Activity) mCtx);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(mCtx.getResources().getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		oks.setTitleUrl("http://fundoshouhu.szkct.cn/funfit.html");
        oks.setTitleUrl("http://www.fundo.cc");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mCtx.getResources().getString(R.string.welcome_funrun));
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(detailPath);// 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        // oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mCtx.getResources().getString(R.string.welcome_funrun));
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("");
        if (android.os.Build.VERSION.SDK_INT < 21) {
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), mCtx.getResources().getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        } else {oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_linkedin)), mCtx.getResources().getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        }

        // 启动分享GUI
        oks.show(mCtx);
    }

    public Bitmap getNewShareViewBitmap(View comBitmap, int width, int height) {
        Bitmap bitmap = null;
        if (comBitmap != null) {
            comBitmap.clearFocus();
            comBitmap.setPressed(false);

            boolean willNotCache = comBitmap.willNotCacheDrawing();
            comBitmap.setWillNotCacheDrawing(false);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = comBitmap.getDrawingCacheBackgroundColor();
            comBitmap.setDrawingCacheBackgroundColor(0);
            float alpha = comBitmap.getAlpha();
            comBitmap.setAlpha(1.0f);

            if (color != 0) {
                comBitmap.destroyDrawingCache();
            }

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            comBitmap.measure(widthSpec, heightSpec);
            //comBitmap.layout(0, 0, width, height);

            comBitmap.buildDrawingCache();
            Bitmap cacheBitmap = comBitmap.getDrawingCache();
            if (cacheBitmap == null) {
                android.util.Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + comBitmap + ")",
                        new RuntimeException());
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            comBitmap.setAlpha(alpha);
            comBitmap.destroyDrawingCache();
            comBitmap.setWillNotCacheDrawing(willNotCache);
            comBitmap.setDrawingCacheBackgroundColor(color);
        }
        return bitmap;
    }

    private static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }


    public void showShare(ScrollView view) {  //分享
//        convertViewToBitmap(view);
//        getNewShareViewBitmap(view,view.getWidth(),view.getHeight());
        ScreenshotsShare.savePicture(getScrollviewBitmap(view), filePath, fileName);
//        ScreenshotsShare.savePicture(ScreenshotsShare.takeScreenShot(view), filePath, fileName);
        //ShareSDK.initSDK(getActivity());
        mapPackageName=	setImage((Activity) mCtx);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(mCtx.getResources().getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		oks.setTitleUrl("http://fundoshouhu.szkct.cn/funfit.html");
        oks.setTitleUrl("http://www.fundo.cc");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mCtx.getResources().getString(R.string.welcome_funrun));
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(detailPath);// 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        // oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mCtx.getResources().getString(R.string.welcome_funrun));
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("");
        if (android.os.Build.VERSION.SDK_INT < 21) {
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), mCtx.getResources().getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        } else {oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_linkedin)), mCtx.getResources().getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        }

        // 启动分享GUI
        oks.show(mCtx);
    }


//    public void showShare(View view) {  //分享
////        convertViewToBitmap(view);
////        getNewShareViewBitmap(view,view.getWidth(),view.getHeight());
//        ScreenshotsShare.savePicture(ScreenshotsShare.takeScreenShot(view), filePath, fileName);
//        //ShareSDK.initSDK(getActivity());
//        mapPackageName=	setImage((Activity) mCtx);
//        OnekeyShare oks = new OnekeyShare();
//        // 关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(mCtx.getResources().getString(R.string.app_name));
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
////		oks.setTitleUrl("http://fundoshouhu.szkct.cn/funfit.html");
//        oks.setTitleUrl("http://www.fundo.cc");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(mCtx.getResources().getString(R.string.welcome_funrun));
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath(detailPath);// 确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        // oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment(mCtx.getResources().getString(R.string.welcome_funrun));
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("");
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("");
//        if (android.os.Build.VERSION.SDK_INT < 21) {
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), mCtx.getResources().getString(R.string.linkedin), Linkedinclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
//        } else {oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_linkedin)), mCtx.getResources().getString(R.string.linkedin), Linkedinclick);
//            oks.setCustomerLogo(drawableToBitmap(mCtx.getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
//        }
//
//        // 启动分享GUI
//        oks.show(mCtx);
//    }
    View.OnClickListener facebookclick=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            shareToFacebook();
        }
    };
    View.OnClickListener Instagramclick=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            shareToInstagram();
        }
    };
    View.OnClickListener twitterclick=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            shareTotwitter();
        }
    };
    View.OnClickListener whatsappclick=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            shareTowhatsapp();
        }
    };
    View.OnClickListener Linkedinclick=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            shareToLinkedin();
        }
    };
    View.OnClickListener mobileqqclick=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//			shareTomobileqq();
            Utils.onClickShareToQQ(mCtx,detailPath);
        }
    };
    View.OnClickListener stravaclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToStrava();
        }
    };

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    /**
     * 分享至Facebook
     */
    public void shareToFacebook() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_FACEBOOK_KATANA);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, (Activity) mCtx,  "");
            } else {
                Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_facebook_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_facebook_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * 分享至Instagram
     */
    public void shareToInstagram() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_INSTAGRAM_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, (Activity) mCtx,  "");
            } else {
                Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_instagram_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_instagram_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至Twitter
     */
    public void shareTotwitter() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_TWITTER_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, (Activity) mCtx,  "");
            } else {
                Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_twitter_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_twitter_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 分享至whatsapp
     */
    public void shareTowhatsapp() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_WHATSAPP);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, (Activity) mCtx,  "");
            } else {
                Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 分享至Linkedin
     */
    public void shareToLinkedin() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_LINKEDIN_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, (Activity) mCtx,  "");
            } else {
                Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_linkedin_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_linkedin_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 分享至mobileqq
     */
    public void shareTomobileqq() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_TENCENT_MOBILEQQ);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, (Activity) mCtx,  "");
            } else {
                Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至strava
     */
    public void shareToStrava() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_STRAVA);
            if (packageName != null) {
                PackageManager pm = mCtx.getPackageManager();
                boolean isAdd = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE",packageName));
                if(!isAdd){
                    Toast.makeText(mCtx, mCtx.getResources().getString(R.string.strava_need_open_permission), Toast.LENGTH_SHORT).show();
                    return;
                }
                actionShare_sms_email_facebook(packageName, (Activity) mCtx, "");
            } else {
                Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_strava_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, mCtx.getResources().getString(R.string.no_strava_in_your_phone), Toast.LENGTH_SHORT).show();
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
    public  void actionShare_sms_email_facebook(String packageName, Activity activity, String shareText ){
//		String fileName = "rideSummary_" + datetime_start + ".png";
//		File file = new File("/storage/emulated/0/DCIM/Camera/1411099620786.jpg");
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_SUBJECT, "SchwinnCycleNav Ride Share");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);

        File file = new File(detailPath);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.putExtra(Intent.EXTRA_STREAM, TUriParse.getUriForFile(activity, file));
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

//		intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
//		intent.putExtra(Intent.EXTRA_STREAM, DatabaseProvider.queryScreenshot(activity, datetime.getTime()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(packageName);
        activity.startActivity(Intent.createChooser(intent, mCtx.getResources().getString(R.string.app_name)));
        System.out.println("****3");
    }


    private Bitmap getScrollviewBitmap(ScrollView views)
    {
        int h = 0;
        Bitmap screenShotBitmap = null;
        // 获取listView实际高度
        for (int i = 0;views.getChildCount() > i; i++) {
            h += views.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        screenShotBitmap = Bitmap.createBitmap(views.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(screenShotBitmap);
        views.draw(canvas);
        return screenShotBitmap;
    }
    public  void drawBitmapAndView(ScrollView views) {

        int h = 0;
        Bitmap screenShotBitmap = null;
        // 获取listView实际高度
        for (int i = 0;views.getChildCount() > i; i++) {
            h += views.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        screenShotBitmap = Bitmap.createBitmap(views.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(screenShotBitmap);
        views.draw(canvas);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File appDir = new File(Environment.getExternalStorageDirectory(), shareFileDirectory);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File f = new File(appDir, fileName);
//            File f = new File(detailPath);
            try {
//                f = new File(filePath);
//                if (!f.exists()) {
//                    f.createNewFile();
//                }
                FileOutputStream fos;
                String path = Environment.getExternalStorageDirectory() + File.separator + shareFileDirectory + File.separator + fileName;
                fos = new FileOutputStream(path);
                if (null != fos) {
                    screenShotBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    public  void drawBitmapAndView(View... views) {
//        int width = views[0].getWidth();
//        int height = views[views.length - 1].getBottom();     //底部留多30个像素应该会好看些
//
//        Bitmap fullBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(fullBitmap);
//        /**设置背景色*/
////        canvas.drawColor(Color.parseColor("#ffffff"));
//        canvas.drawColor(Color.parseColor("#00292C30"));
//
//        for (View view : views) {
//            view.setDrawingCacheEnabled(true);
//            canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);
//        }
//
//        Bitmap screenShotBitmap = ScreenShotHelper.compressScale(fullBitmap);  // mCompressPic
//        for (View view : views) {
//            view.setDrawingCacheEnabled(false);
//        }
//
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            File appDir = new File(Environment.getExternalStorageDirectory(), shareFileDirectory);
//            if (!appDir.exists()) {
//                appDir.mkdir();
//            }
//            File f = new File(appDir, fileName);
////            File f = new File(detailPath);
//            try {
////                f = new File(filePath);
////                if (!f.exists()) {
////                    f.createNewFile();
////                }
//                FileOutputStream fos;
//                String path = Environment.getExternalStorageDirectory() + File.separator + shareFileDirectory + File.separator + fileName;
//                fos = new FileOutputStream(path);
//                if (null != fos) {
//                    screenShotBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                    fos.flush();
//                    fos.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 截取scrollview的屏幕
     * **/
    public Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // 测试输出
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        FileOutputStream out = null;
        try {
//            out = new FileOutputStream("/sdcard/screen_test.png");
            out = new FileOutputStream(filePath + File.separator + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public static File systemDirectory = Environment.getExternalStorageDirectory();
    public static String shareFileDirectory = "app_share";
    public static String sharefileName = "share_icon.png";
    public static String shareDetailPath = systemDirectory + "/" + shareFileDirectory + "/" + sharefileName;

}
