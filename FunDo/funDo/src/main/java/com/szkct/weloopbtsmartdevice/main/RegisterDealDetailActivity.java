package com.szkct.weloopbtsmartdevice.main;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.Utils;

/**
 * 
 * @author chendalin
 * 说明：ActionBarActivit对于最新的sdk 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 */
public class RegisterDealDetailActivity extends AppCompatActivity {

	private LinearLayout mRelativeDeal;
	private Toolbar toolbar;
	private WebView mWebView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); */
		setContentView(R.layout.registerdealdetail);
		mRelativeDeal = (LinearLayout) findViewById(R.id.re_dealdetail);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		mWebView = (WebView)findViewById(R.id.webwiew_useragreement);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.action_back_normal);
		toolbar.setTitleTextColor(getResources()
				.getColor(android.R.color.white));
		// 状态栏与标题栏一体
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,mRelativeDeal);
		}
		mWebView.getSettings().setJavaScriptEnabled(true);
		if (Utils.getLanguage().equals("zh")&& Utils.getCountry().equals("CN")) {
			mWebView.loadUrl("file:///android_asset/user_agreement_ZH.htm");
		}else if(Utils.getLanguage().equals("zh")&& Utils.getCountry().equals("TW")||Utils.getCountry().equals("HK")){
			mWebView.loadUrl("file:///android_asset/user_agreement_ZH-rTW.htm");
		}else {
			mWebView.loadUrl("file:///android_asset/user_agreement_US.htm");
		}
	}
	
	private float XPosition = 0;
	private float YPosition = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			XPosition = event.getX();
			YPosition = event.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			if(event.getY() - YPosition > 50||YPosition - event.getY() > 50){
				break;
			}
			if(event.getX() - XPosition > 80){
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
