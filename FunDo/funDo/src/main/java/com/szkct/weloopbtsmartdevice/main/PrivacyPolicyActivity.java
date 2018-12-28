package com.szkct.weloopbtsmartdevice.main;

import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

/**
 * 
 * @author chendalin
 * 说明：ActionBarActivit对于最新的sdk 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 */
public class PrivacyPolicyActivity extends AppCompatActivity {
	private final static String TAG = "PrivacyPolicyActivity";
	
	private WebView mWebView;

	private LinearLayout mLinearLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			setTheme(R.style.KCTStyleWhite);
		}else{
			setTheme(R.style.KCTStyleBlack);
		}
		setContentView(R.layout.about_privacy_policy);
		initControls ();
		//状态栏与标题栏一体
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,mLinearLayout,R.color.trajectory_bg);
		}
	}
	
	private void initControls() {
		// TODO Auto-generated method stub
		mLinearLayout = (LinearLayout)findViewById(R.id.layout_privacy);
		mWebView = (WebView)findViewById(R.id.webwiew);
		if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			 mWebView.setBackgroundColor(Color.parseColor("#f5f5f5")); // 设置背景色
		}else{
			 mWebView.setBackgroundColor(Color.parseColor("#292C30")); // 设置背景色
		}
	
	//	  mWebView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				super.onReceivedSslError(view, handler, error);
			}
		});
		
		if (Utils.getLanguage().equals("zh")&& Utils.getCountry().equals("CN")) {
			
			if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
				mWebView.loadUrl("file:///android_asset/about_ZH_white.htm");
			}else{
				mWebView.loadUrl("file:///android_asset/about_ZH.htm");
			}
			
		}else {
			if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
				mWebView.loadUrl("file:///android_asset/about_US_white.htm");
			}else{
				mWebView.loadUrl("file:///android_asset/about_US.htm");
			}
			
		}
		
		/*mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				super.onReceivedSslError(view, handler, error);
			}
		});
		
		if (Utils.getLanguage().equals("zh")&& Utils.getCountry().equals("CN")) {
			
			
				mWebView.loadUrl("file:///android_asset/about_ZH.htm");
			 
			
		}else {
			
				mWebView.loadUrl("file:///android_asset/about_US.htm");
			
			
		}*/
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	

	
}
