package com.szkct.weloopbtsmartdevice.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;

/**
 * 
 * @author chendalin 说明：ActionBarActivit对于最新的sdk
 *         20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 */
public class RegisteredActivity extends AppCompatActivity {
	private LinearLayout mLinearRegis;
	private Toolbar toolbar;
	private EditText emailText, password, againpassword;
	private String emailstr, passwordstr, againpasswordstr;
	private TextView registerbtn;
	private CheckBox agreedealBox;
	private TextView dealtext;
	private final int REGISTEROK = 2;
	private String code, rightcode;
	private String[] emailkindstr = { "qq.com", "163.com", "126.com",
			"188.com", "gmail.com", "yahoo.com", "yahoo.com.cn", "sina.com",
			"sohu.com", "tom.com", "163.net","hotmail.com" };
	private String[] strs = {};
	private String ifexeststr ="";
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case REGISTEROK:
				// 已注册的邮箱保存本地下次注册显示邮箱已注册；
				SharedPreferences sPreferences = RegisteredActivity.this
						.getSharedPreferences(
								"regemailfiles",
								Context.MODE_PRIVATE);
				Editor editor = sPreferences
						.edit();
				editor.putString(
						"haveemail",
						emailstr);
				editor.commit();				
				
				String getrightcode = (String) msg.obj;
				System.out
						.println("RegisterDealDetailActivity-----thread()--rightcode="
								+ getrightcode);
				Log.e("RegisterActivity()","注册返回码="+getrightcode);
				if ("0".equals(getrightcode)) {
					Toast.makeText(RegisteredActivity.this,
							getString(R.string.email_register_ok), Toast.LENGTH_SHORT)
							.show();
					finish();
				} else if("2".equals(getrightcode)){
					Toast.makeText(RegisteredActivity.this,
							getString(R.string.email_already_registered),
							Toast.LENGTH_SHORT).show();

				}else {
					Toast.makeText(RegisteredActivity.this,getString(R.string.email_ok_no_set),Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}

		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registered_main);
		init();
		registerUser();

	}

	private void init() {
		// TODO Auto-generated method stub
		mLinearRegis = (LinearLayout) findViewById(R.id.li_registered);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.action_back_normal);
		toolbar.setTitleTextColor(getResources()
				.getColor(android.R.color.white));
		// 状态栏与标题栏一体
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,
					mLinearRegis);
		}
		emailText = (EditText) findViewById(R.id.registered_name_id);
		password = (EditText) findViewById(R.id.registered_password_id);
		againpassword = (EditText) findViewById(R.id.registered_againpassword_id);
		registerbtn = (TextView) findViewById(R.id.registered_button);
		agreedealBox = (CheckBox) findViewById(R.id.unselete_ra);
		dealtext = (TextView) findViewById(R.id.dealtv_show);
		// 默认不同意条款
		agreedealBox.setChecked(false);

		// 查看协议
		showdealdetail();

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

	private void showdealdetail() {
		// TODO Auto-generated method stub
		dealtext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent nIntent = new Intent(RegisteredActivity.this,
						RegisterDealDetailActivity.class);
				startActivity(nIntent);
			}
		});

	}

	private void registerUser() {
		// TODO Auto-generated method stub
		registerbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getUserInput();

			}
		});

	}

	private void getUserInput() {
		// TODO Auto-generated method stub
		emailstr = emailText.getText().toString();
		passwordstr = password.getText().toString();
		againpasswordstr = againpassword.getText().toString();
		SharedPreferences sPreferences = RegisteredActivity.this
				.getSharedPreferences("regemailfiles",
						Context.MODE_PRIVATE);
		ifexeststr = sPreferences.getString("haveemail","");

		String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(emailstr);
		Log.e("registerActivity", "ifexeststr ="+ifexeststr);
		if(!emailstr.equals("")){
		if (m.matches()) { // 邮箱是否合法？
			strs = emailstr.split("[@]");// 截取邮箱@后面的内容
				if (!emailstr.equals(ifexeststr)) {    //避免重复发送注册请求
					
					if (!passwordstr.equals("")) {
						if ((passwordstr.length() >= 6)
								&& (passwordstr.length() <= 16)) { // 密码长度？

							if (passwordstr.equals(againpasswordstr)) { // 密码是否相同？
								if (agreedealBox.isChecked()) { // 同意条款？

									if (NetWorkUtils.isConnect(this)) { // 网络是否连接？

										new Thread(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												try {
													
													// MD5加密后的邮箱密码。
													String md5pwd = MD5PWD(passwordstr);
													String userinfourl = "http://www.fundo.cc/export/reg_email.php?"
															+ "email="
															+ emailstr
															+ "&pwd=" + md5pwd;
													System.out
															.println("RegisterDealDetailActivity-----thread()--userinfourl="
																	+ userinfourl);
													URL Url = new URL(
															userinfourl);
													HttpURLConnection HttpConn = (HttpURLConnection) Url
															.openConnection();
													HttpConn.setRequestMethod("GET");
													HttpConn.setReadTimeout(5000);
													HttpConn.connect();
													if (HttpConn
															.getResponseCode() == 200) {
														InputStream is = HttpConn
																.getInputStream();
														BufferedReader bf = new BufferedReader(
																new InputStreamReader(
																		is));
														code = bf.readLine();
														rightcode = code
																.substring(11,
																		12); // {"return":"0"}
														System.out
																.println("RegisterDealDetailActivity-----thread()--code="
																		+ code);
														Log.e("RegisteredActivity()", "提交注册时服务器返回的code ="+code);
														Message Message = new Message();
														Message.what = REGISTEROK;
														Message.obj = rightcode;
														handler.sendMessage(Message);

													}

												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}

											}

											// MD5加密邮箱密码
											private String MD5PWD(String string) {
												// TODO Auto-generated method
												// stub
												byte[] hash;
												try {
													hash = MessageDigest
															.getInstance("MD5")
															.digest(string
																	.getBytes("UTF-8"));
												} catch (NoSuchAlgorithmException e) {
													throw new RuntimeException(
															"Huh, MD5 should be supported?",
															e);
												} catch (UnsupportedEncodingException e) {
													throw new RuntimeException(
															"Huh, UTF-8 should be supported?",
															e);
												}

												StringBuilder hex = new StringBuilder(
														hash.length * 2);
												for (byte b : hash) {
													if ((b & 0xFF) < 0x10)
														hex.append("0");
													hex.append(Integer
															.toHexString(b & 0xFF));
												}
												return hex.toString();
											}
										}).start();

									} else {
										Toast.makeText(
												RegisteredActivity.this,
												R.string.my_network_disconnected,
												Toast.LENGTH_SHORT).show();
									}

								} else {
									Toast.makeText(RegisteredActivity.this,
											R.string.agree_deal_yet,
											Toast.LENGTH_SHORT).show();

								}

							} else {
								Toast.makeText(RegisteredActivity.this,
										R.string.different_password,
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(RegisteredActivity.this,
									R.string.err_format_password,
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(RegisteredActivity.this,
								R.string.empty_password, Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(RegisteredActivity.this,
							R.string.email_already_registered,
							Toast.LENGTH_SHORT).show();
				}
//			} else {
//				Toast.makeText(RegisteredActivity.this,
//						R.string.email_format_error, Toast.LENGTH_SHORT).show();
//			}

		} else {

			Toast.makeText(RegisteredActivity.this,
					R.string.email_format_error, Toast.LENGTH_SHORT).show();
		}
		}else {
			Toast.makeText(RegisteredActivity.this,
					getString(R.string.mailbox_cant_empty), Toast.LENGTH_SHORT).show();
		}

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		return super.onCreateOptionsMenu(menu);
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
