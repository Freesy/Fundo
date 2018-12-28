package com.szkct.weloopbtsmartdevice.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.SpotsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindBackPassWordActivity extends AppCompatActivity {

	private LinearLayout mfindpslayout;
	private EditText findbackeditText;

	private TextView findbacktv;
	private Toolbar toolbar;
	private String[] emailkindstr = { "qq.com", "163.com", "126.com",
			"188.com", "gmail.com", "yahoo.com", "yahoo.com.cn", "sina.com",
			"sohu.com", "tom.com", "163.net", "hotmail.com" };
	private String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	private Pattern p = Pattern.compile(strPattern);
	private String TAG = "FindBackPassWordActivity";
	private final int SUBMITFINDEMAIL = 1;
	private HTTPController hc = null;
	private SpotsDialog landingLoadDialog = null;
	private String emailstr;
	private String[] strs = {};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUBMITFINDEMAIL:
				String mString = (String) msg.obj;
				Log.e(TAG, " return =" + mString);
				landingLoadDialog.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(mString);
					String returns = jsonObject.getString("result");
					if (returns.equals("0")) {
						Toast.makeText(FindBackPassWordActivity.this,
								getString(R.string.reset_email_password),
								Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(FindBackPassWordActivity.this,
								getString(R.string.unvaild_find_email),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findback_password);
		findbackeditText = (EditText) findViewById(R.id.findps_email_id);
		findbacktv = (TextView) findViewById(R.id.findback_tv);

		mfindpslayout = (LinearLayout) findViewById(R.id.find_back_ps_layout);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.action_back_normal);
		toolbar.setTitleTextColor(getResources()
				.getColor(android.R.color.white));
		// 状态栏与标题栏一体
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,
					mfindpslayout);
		}
		Intent mIntent = getIntent();
		String emalis =  mIntent.getStringExtra("emali");
		findbackeditText.setText(emalis);
		findbacktv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				emailstr = findbackeditText.getText().toString();
				String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
				Pattern p = Pattern.compile(strPattern);
				Matcher m = p.matcher(emailstr);
				if (m.matches()) { // 邮箱是否合法？
					strs = emailstr.split("[@]");// 截取邮箱@后面的内容
					//if (isEmailTrue(emailkindstr, strs[1])) { // 邮箱是否合法？

						if (NetWorkUtils
								.isConnect(FindBackPassWordActivity.this)) { // 网络是否连接？
							if (hc == null) {
								hc = HTTPController.getInstance();
							}
							// http://www.fundo.cc/export/rpwd_email.php? email=
							String url = Constants.FINDBACKPASSWORD + "email="
									+ emailstr;
							Log.e(TAG, "  find back passoword url =" + url);
							if (landingLoadDialog == null)
								landingLoadDialog = new SpotsDialog(
										FindBackPassWordActivity.this);
							if(!landingLoadDialog.isShowing()){
								landingLoadDialog.show();
							}
							hc.getNetworkData(url, handler, SUBMITFINDEMAIL);

						} else {
							Toast.makeText(FindBackPassWordActivity.this,
									R.string.my_network_disconnected,
									Toast.LENGTH_SHORT).show();
						}

//					} else {
//						Toast.makeText(FindBackPassWordActivity.this,
//								R.string.email_format_error, Toast.LENGTH_SHORT)
//								.show();
//					}

				} else {

					Toast.makeText(FindBackPassWordActivity.this,
							R.string.email_format_error, Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
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

	private boolean isEmailTrue(String[] s1, String s2) {

		System.out.println("s1=" + s1);
		System.out.println("s2=" + s2);
		// TODO Auto-generated method stub
		for (int i = 0; i < s1.length; i++) {
			if (s1[i].indexOf(s2) != -1) {// 循环查找字符串数组中的每个字符串中是否包含所有查找的内容
				return true; // 查找到了就返回真，不在继续查询
			}
		}
		return false;
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
