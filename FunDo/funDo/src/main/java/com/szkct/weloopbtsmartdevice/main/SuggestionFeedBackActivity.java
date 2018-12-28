package com.szkct.weloopbtsmartdevice.main;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.SpotsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 *********** 作者：laiqinglin 时间：2015/3/24 功能： 意见反馈 说明：ActionBarActivit对于最新的sdk
 * 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 */
public class SuggestionFeedBackActivity extends AppCompatActivity {

	private RelativeLayout mRelativeSuggestion;
	private EditText feedbackeditText;
	private EditText contacttext;
	private Button submit_feedback_btn;
	private Toolbar toolbar;
	private String[] emailkindstr = { "qq.com", "163.com", "126.com",
			"188.com", "gmail.com", "yahoo.com", "yahoo.com.cn", "sina.com",
			"sohu.com", "tom.com", "163.net", "hotmail.com" };
	private String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	private Pattern p = Pattern.compile(strPattern);
	private String TAG = "SuggestionFeedBackActivity";
	private final int UPLOADSUGGESTION = 1;
	private HTTPController hc = null;
	private SpotsDialog landingLoadDialog = null;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); */
		setContentView(R.layout.suggestion_feedback);
		feedbackeditText = (EditText) findViewById(R.id.feedback_editText);
		contacttext = (EditText) findViewById(R.id.feedback_touchText);
		submit_feedback_btn = (Button) findViewById(R.id.submit_feedback_btn);
		mRelativeSuggestion = (RelativeLayout) findViewById(R.id.re_suggestion);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.action_back_normal);
		toolbar.setTitleTextColor(getResources()
				.getColor(android.R.color.white));
		// 状态栏与标题栏一体
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,
					mRelativeSuggestion);
		}
		submit_feedback_btn.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String inputbodystr = feedbackeditText.getText().toString();
				inputbodystr = "Android-"+inputbodystr;
				String inputcontactstr = contacttext.getText().toString();
				Matcher m = p.matcher(inputcontactstr);
				if ((!"".equals(inputbodystr))) {
					if (!inputcontactstr.equals("")) {
						if (inputcontactstr.matches("[0-9]+")) {			//输入的联系方式为纯数字(QQ号)？
							if ((6 <= inputcontactstr.length())&&(inputcontactstr.length() <= 11)) {
								
								SendToServer(inputbodystr,inputcontactstr);
								
								
							}else {
								Toast.makeText(SuggestionFeedBackActivity.this, R.string.invalid_QQ,Toast.LENGTH_SHORT).show();
							}
							
						}else if (m.matches()) {                            //输入的是邮箱？
							String[] strs = inputcontactstr.split("[@]");// 截取邮箱@后面的内容
							if (isEmailTrue(emailkindstr, strs[1])) { // 邮箱是否合法？
								
								SendToServer(inputbodystr,inputcontactstr);
								
							}else {
								Toast.makeText(SuggestionFeedBackActivity.this,
										R.string.email_format_error, Toast.LENGTH_SHORT).show();
							}
							
						}else {

							Toast.makeText(SuggestionFeedBackActivity.this,
									R.string.email_format_error, Toast.LENGTH_SHORT).show();
						}
						
						
					} else {
						Toast.makeText(SuggestionFeedBackActivity.this,
								getString(R.string.input_contact),
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(SuggestionFeedBackActivity.this,
							getString(R.string.nothing_submit),
							Toast.LENGTH_SHORT).show();
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
	
	private void SendToServer(final String inputbodystr, final String inputcontactstr) {
		// TODO Auto-generated method stub
		//http://www.fundo.cc/export/sys_suggest.php? opened=_&mid=_& rid =_&context=_& contact=_
		
		if (NetWorkUtils.isConnect(SuggestionFeedBackActivity.this)) {
			if (hc == null) {
				hc = HTTPController.getInstance();
				hc.open(getApplicationContext());
			}
			String mid = SharedPreUtil.readPre(
					SuggestionFeedBackActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);
			if(landingLoadDialog == null){
				landingLoadDialog = new SpotsDialog(SuggestionFeedBackActivity.this);
			}
			if(!landingLoadDialog.isShowing()){
				landingLoadDialog.show();
			}
			StringRequest myReq = new StringRequest(Method.POST,Constants.SUGGESTIONFEEDBACK + "mid=" + mid, new Listener<String>()
					{
						@Override
						public void onResponse(String response)
						{
							Log.e(TAG,"结果：" + response);
							try
							{
								JSONObject jsonObject = new JSONObject(response);
								if ("0".equals(jsonObject.getString("result")))
								{
									Toast.makeText(SuggestionFeedBackActivity.this,
											getString(R.string.submit_ok),
											Toast.LENGTH_SHORT).show();
									landingLoadDialog.dismiss();
									finish();
								}else {
									Toast.makeText(SuggestionFeedBackActivity.this,
											getString(R.string.shebei_is_timeout),
											Toast.LENGTH_SHORT).show();
									landingLoadDialog.dismiss();
								}
							} catch (JSONException e)
							{
								e.printStackTrace();
							}
						}
						
					}, new ErrorListener()
					{
						@Override
						public void onErrorResponse(VolleyError error)
						{
							
						}
					})
					{
						@Override
						protected Map<String, String> getParams() throws AuthFailureError
						{
							Map<String, String> params = new HashMap<String,String>();
							params.put("context", inputbodystr);
							params.put("contact", inputcontactstr);
							return params;
						}
					};
					BTNotificationApplication.requestQueue.add(myReq);
			
		} else {
			Toast.makeText(SuggestionFeedBackActivity.this,
					getString(R.string.check_network_message),
					Toast.LENGTH_SHORT).show();
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