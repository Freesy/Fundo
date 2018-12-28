/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.szkct.weloopbtsmartdevice.main;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.weloopbtsmartdevice.data.greendao.UserInfo;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.DateUtil;
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
import com.szkct.weloopbtsmartdevice.util.PublicTools;
import com.szkct.weloopbtsmartdevice.util.RequestCallBackEx;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.XHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 * 说明：ActionBarActivit对于最新的sdk 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity mtk
 * source | chendalin modify
 */
public class SoscallActivity extends AppCompatActivity implements  // 紧急拨号页面
		OnClickListener {
	LinearLayout soslayout;
	private   String SOSNUMBER1="SOSNUMBER1";
	private   String SOSNUMBER2="SOSNUMBER2";
	private   String SOSNUMBER3="SOSNUMBER3";
	private   String SOSNAME1="SOSNAME1";
	private   String SOSNAME2="SOSNAME2";
	private   String SOSNAME3="SOSNAME3";
	EditText sos_input_name1,sos_input_name2,sos_input_name3;
	EditText sos_input_number1,sos_input_number2,sos_input_number3;

	private MyLoadingDialog myLoadingDialog;
	private Toast toast = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			setTheme(R.style.KCTStyleWhite);
		}else{
			setTheme(R.style.KCTStyleBlack);
		}
		setContentView(R.layout.activity_soscall);
		// chendalin add
		initContorl();
		initedittext();
		initSosData();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this, soslayout,R.color.trajectory_bg);
		}
	}

	private void toGetSosData(String userId,RequestCallBackEx<String> respon){ // 根据用户id获取用户信息
		try {
//            reParams.addQueryStringParameter("mid", 1063024 + "");
//            reParams.addBodyParameter("Images",file);
			com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
			reParams.addBodyParameter("user_id", userId);
			String url = ServerConfig.SOS_DATA;
			if (url.contains(" ")){
				if(url.substring(url.length()-1)==" "){
					url= url.substring(0,url.length()-1);
				}else{
					url= url.replace(" ","%20");
				}
			}
			if (url.contains("\"")){
				url= url.replace("\"","%22");
			}
			if (url.contains("{")){
				url= url.replace("{","%7B");
			}
			if (url.contains("}")){
				url= url.replace("{","%7D");
			}
			XHttpUtils.getInstance().send(HttpRequest.HttpMethod.POST, url, reParams, respon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void AddSosNumber(String userId,RequestCallBackEx<String> respon){ // 根据用户id获取用户信息
		try {
//            reParams.addQueryStringParameter("mid", 1063024 + "");
//            reParams.addBodyParameter("Images",file);
//			获取紧急联系人信息
			String contacts1name = sos_input_name1.getText().toString();
			String contacts2name = sos_input_name2.getText().toString();
			String contacts3name = sos_input_name3.getText().toString();

			String contacts1number = sos_input_number1.getText().toString();
			String contacts2number = sos_input_number2.getText().toString();
			String contacts3number = sos_input_number3.getText().toString();

			StringBuilder mBuilderContactsName = new StringBuilder();
			StringBuilder mBuilderContactsNumber = new StringBuilder();
			if(!StringUtils.isEmpty(contacts1name) && !StringUtils.isEmpty(contacts1number) && PublicTools.IsValidMobileNo(contacts1number)){
				mBuilderContactsName.append(contacts1name);
				mBuilderContactsNumber.append(contacts1number);
			}
			if(!StringUtils.isEmpty(contacts2name) && !StringUtils.isEmpty(contacts2number) && PublicTools.IsValidMobileNo(contacts2number)){
				mBuilderContactsName.append("|").append(contacts2name);
				mBuilderContactsNumber.append("|").append(contacts2number);
			}
			if(!StringUtils.isEmpty(contacts3name) && !StringUtils.isEmpty(contacts3number) && PublicTools.IsValidMobileNo(contacts3number)){
				mBuilderContactsName.append("|").append(contacts3name);
				mBuilderContactsNumber.append("|").append(contacts3number);
			}
			String mContactsName = mBuilderContactsName.toString();
			String mContactsNumber = mBuilderContactsNumber.toString();

			com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
			reParams.addBodyParameter("user_id", userId);
			reParams.addBodyParameter("contact_name", mContactsName);    // one|two|three  上传多个格式 one 上传单条数据
			reParams.addBodyParameter("contact_phone", mContactsNumber);   //15049388018|15047745099|15048845099上传多个格式 15049388018上传单条数据

			String url = ServerConfig.ADD_SOS_DATA;
			if (url.contains(" ")){
				if(url.substring(url.length()-1)==" "){
					url= url.substring(0,url.length()-1);
				}else{
					url= url.replace(" ","%20");
				}
			}
			if (url.contains("\"")){
				url= url.replace("\"","%22");
			}
			if (url.contains("{")){
				url= url.replace("{","%7B");
			}
			if (url.contains("}")){
				url= url.replace("{","%7D");
			}
			XHttpUtils.getInstance().send(HttpRequest.HttpMethod.POST, url, reParams, respon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  void parseSosData(String content) {
		try {
			String result = content;
			JSONObject jsonObj = new JSONObject(content);
			int nRetCode = jsonObj.optInt("msg");
			if(nRetCode == 0){
				////////////////////////////////////////////////////////////////////////////////////
				UserInfo userInfo = new UserInfo();   // {"flag":"Success","msg":0,"data":"dhhhh#15889895656"}
				JSONObject obj = jsonObj.optJSONObject("data");    // {"flag":"Success","msg":0,"data":[["dhhhh","15889895656"],["dhhhh","15889895656"],["dhhhh","15889895656"]]}
				String ss = jsonObj.optString("data");  // [["dhhhh","15889895656"]]
//				String sss = jsonObj.getString("data"); // [["dhhhh","15889895656"]]

				if(!StringUtils.isEmpty(ss)){
					if(ss.contains("|")){
//						ss.replace("|","%");
						String[] mContent = ss.split("\\|");
						for(int i=0 ; i<mContent.length ; i++){
							String[] contentI = mContent[i].split("#");
							if(i == 0){
								sos_input_name1.setText(contentI[0]);
								sos_input_number1.setText(contentI[1]);
							}
							if(i == 1){
								sos_input_name2.setText(contentI[0]);
								sos_input_number2.setText(contentI[1]);
							}
							if(i == 2){
								sos_input_name3.setText(contentI[0]);
								sos_input_number3.setText(contentI[1]);
							}
						}
					}else{
						String[] mContent = ss.split("#");
						sos_input_name1.setText(mContent[0]);
						sos_input_number1.setText(mContent[1]);
					}
				}

				/*if(!StringUtils.isEmpty(sss) && sss.contains("],")){
					sss.replace("],","|");
					String[] mStr = sss.split("|");
				}else {
					String a = sss.replace("]]","").replace("[[", "");
//					String b= sss.replace("[[","");
					String[] str = a.replace("'","").split(",");
					String name1 = str[0].replace("'","");
					String phone1 = str[1].replace("'","");
					sos_input_name1.setText(name1);
					sos_input_number1.setText(phone1);
				}*/

				// TODO --- 数据解析成功，保存数据到本地  ---- 当本地没有数据时，从网络获取sos,否则直接从本地获取（本地有数据时，不发送获取sos的请求）


				/*SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME1, sos_input_name1.getText().toString());
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME2, sos_input_name2.getText().toString());
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME3, sos_input_name3.getText().toString());
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER1, sos_input_number1.getText().toString());
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER2, sos_input_number2.getText().toString());
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER3, sos_input_number3.getText().toString());*/

//				String newUserid = obj.optString("mid");
//				userInfo.setMid(newUserid);
//				SharedPreUtil.savePre(SoscallActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, newUserid);  // 保存新的用户id
//                Log.e("MD555555", "--获取到验证码时间---：" + currentTime);

				dismissLoadingDialog();
//                Toast.makeText(AddPersonaldataActivity.this, "上传头像成功了", Toast.LENGTH_SHORT).show();
			}else if(nRetCode == 1){
//				dismissLoadingDialog();
				Toast.makeText(SoscallActivity.this, "参数填写不完整", Toast.LENGTH_SHORT).show();
			}else if(nRetCode == 2){
				Toast.makeText(SoscallActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
//        return user;
	}

	public  void parseAddSosData(String content) {
		try {
			String result = content;
			JSONObject jsonObj = new JSONObject(content);
			int nRetCode = jsonObj.optInt("msg");
			if(nRetCode == 0){
				////////////////////////////////////////////////////////////////////////////////////
				UserInfo userInfo = new UserInfo();
				JSONObject obj = jsonObj.optJSONObject("data");
				String sosId = obj.optString("id");
//				userInfo.setMid(newUserid);
//				SharedPreUtil.savePre(SoscallActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, newUserid);  // 保存新的用户id
//                Log.e("MD555555", "--获取到验证码时间---：" + currentTime);

				dismissLoadingDialog();
//                Toast.makeText(AddPersonaldataActivity.this, "上传头像成功了", Toast.LENGTH_SHORT).show();
			}else if(nRetCode == 1){
				dismissLoadingDialog();
				Toast.makeText(SoscallActivity.this, "参数填写不完整", Toast.LENGTH_SHORT).show();
			}else if(nRetCode == 2){
				Toast.makeText(SoscallActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
				dismissLoadingDialog();
			}else if(nRetCode == 3){
				Toast.makeText(SoscallActivity.this, "上传的联系人个数与联系方式个数不一致", Toast.LENGTH_SHORT).show();
				dismissLoadingDialog();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
//        return user;
	}

	private void initSosData(){
		String userId =  SharedPreUtil.readPre(SoscallActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);  // 1063024
//			getUserInfo(userId);
		/*toGetSosData(userId, new RequestCallBackEx<String>() {  // getBytes(file).toString(),
			@Override
			public void onSuccessEx(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				parseSosData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(SoscallActivity.this, "获取SOS失败了", Toast.LENGTH_SHORT).show();
//				dismissLoadingDialog();
			}
		});*/
	}

	private void initContorl() {
		// TODO Auto-generated method stub
		findViewById(R.id.back).setOnClickListener(this);
		 findViewById(R.id.sos_editok).setOnClickListener(this);  // 点击完成按钮
		soslayout = (LinearLayout) findViewById(R.id.soscall_layout);
		 sos_input_name1=(EditText)findViewById(R.id.sos_input_name1);  // 联系人1姓名
		 sos_input_name2=(EditText)findViewById(R.id.sos_input_name2);  // 联系人2姓名
		 sos_input_name3=(EditText)findViewById(R.id.sos_input_name3);  // 联系人3姓名
		 
		 sos_input_number1=(EditText)findViewById(R.id.sos_input_number1); // 联系人1号码
		 sos_input_number2=(EditText)findViewById(R.id.sos_input_number2); // 联系人2号码
		 sos_input_number3=(EditText)findViewById(R.id.sos_input_number3); // 联系人3号码

		 sos_input_number1.setInputType(sos_input_number1.getInputType());
		 
	}

private void initedittext() {
	
	if(!SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME1).equals("")){
		sos_input_name1.setCompoundDrawables(null, null, null, null);
	}
	if(!SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME2).equals("")){
		sos_input_name2.setCompoundDrawables(null, null, null, null);
	}
	if(!SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME3).equals("")){
		sos_input_name3.setCompoundDrawables(null, null, null, null);
	}
	if(!SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER1).equals("")){
		sos_input_number1.setCompoundDrawables(null, null, null, null);
	}
	if(!SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER2).equals("")){
		sos_input_number2.setCompoundDrawables(null, null, null, null);
	}
	if(!SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER3).equals("")){
		sos_input_number3.setCompoundDrawables(null, null, null, null);
	}
	sos_input_name1.setText(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME1));
	sos_input_name2.setText(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME2));
	sos_input_name3.setText(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME3));
	sos_input_number1.setText(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER1));
	sos_input_number2.setText(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER2));
	sos_input_number3.setText(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER3));
	sos_input_name1.setSelection(sos_input_name1.length());
	 sos_input_name2.setSelection(sos_input_name2.length());
	 sos_input_name3.setSelection(sos_input_name3.length());
	 sos_input_number1.setSelection(sos_input_number1.length());
	 sos_input_number2.setSelection(sos_input_number2.length());
	 sos_input_number3.setSelection(sos_input_number3.length());
}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.sos_editok:   // 点击完成按钮
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			/*if(NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())){
				showLoadingDialogNew("上传SOS号码中");
				String userId =  SharedPreUtil.readPre(SoscallActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);
				AddSosNumber(userId, new RequestCallBackEx<String>() {  // getBytes(file).toString(),
					@Override
					public void onSuccessEx(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						parseAddSosData(responseInfo.result);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Toast.makeText(SoscallActivity.this, "上传SOS号码失败了", Toast.LENGTH_SHORT).show();  // TODO ---- 上传失败的处理
						dismissLoadingDialog();
					}
				});
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}else{
				ShowMessage(getString(R.string.net_error_tip));
			}*/

			if(sos_input_name1.getText().toString().getBytes().length > 30 || sos_input_name2.getText().toString().getBytes().length > 30
					|| sos_input_name3.getText().toString().getBytes().length > 30){
				Toast.makeText(SoscallActivity.this,getString(R.string.data_too_long),Toast.LENGTH_SHORT).show();
				return;
			}
			if(sos_input_number1.getText().toString().length() > 20 || sos_input_number2.getText().toString().length() > 20
					|| sos_input_number3.getText().toString().length() > 20){
				Toast.makeText(SoscallActivity.this,getString(R.string.data_too_long),Toast.LENGTH_SHORT).show();
				return;
			}

			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME1, sos_input_name1.getText().toString());
			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME2, sos_input_name2.getText().toString());
			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNAME, SOSNAME3, sos_input_name3.getText().toString());
			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER1, sos_input_number1.getText().toString());
			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER2, sos_input_number2.getText().toString());
			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SOSNUMBER, SOSNUMBER3, sos_input_number3.getText().toString());

			if(MainService.getInstance().getState() == 3){
				String s1="";
				String s2="";
				String s3="";



				StringBuffer sosContacts = new StringBuffer();
				String protocolCode =  SharedPreUtil.readPre(SoscallActivity.this, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.PROTOCOLCODE);
				if(DateUtil.versionCompare("V1.1.37",protocolCode)) {         //V1.1.38之后用A|10086#B|10086#C|10086 ; V1.1.38之前的版本10086#10010#10000
					if (sos_input_name1.getText().toString().length() > 0) {
						sosContacts.append(sos_input_name1.getText().toString() + "|");
					}else{
						sosContacts.append("A|");
					}
					if (sos_input_number1.getText().toString().length() > 0) {
						sosContacts.append(sos_input_number1.getText().toString() + "#");
					} else {
						sosContacts.append("#");
					}
					if (sos_input_name2.getText().toString().length() > 0) {
						sosContacts.append(sos_input_name2.getText().toString() + "|");
					}else{
						sosContacts.append("B|");
					}
					if (sos_input_number2.getText().toString().length() > 0) {
						sosContacts.append(sos_input_number2.getText().toString() + "#");
					} else {
						sosContacts.append("#");
					}

					if (sos_input_name3.getText().toString().length() > 0) {
						sosContacts.append(sos_input_name3.getText().toString() + "|");
					}else{
						sosContacts.append("C|");
					}
					if (sos_input_number3.getText().toString().length() > 0) {
						sosContacts.append(sos_input_number3.getText().toString() + "#");
					} else {
						sosContacts.append("#");
					}
				}else {
					if (sos_input_number1.getText().toString().length() > 0) {
						sosContacts.append(sos_input_number1.getText().toString());
					} else {
						sosContacts.append("#");
					}
					if (sos_input_number2.getText().toString().length() > 0) {
						sosContacts.append("#" + sos_input_number2.getText().toString());
					} else {
						sosContacts.append("#");
					}
					if (sos_input_number3.getText().toString().length() > 0) {
						sosContacts.append("#" + sos_input_number3.getText().toString());
					} else {
						sosContacts.append("#");
					}
				}
				//TODO --- 发送紧急拨号联系人的 命令
//				MainService.getInstance().sendMessage("sosc"+s1+s2+s3);
				/*MainService.getInstance().sendMessage("sosc"+sos_input_name1.getText().toString()+"w50253d"+sos_input_name2.getText().toString()
						+"w50253d"+sos_input_name3.getText().toString()+"w50253d"+ sos_input_number1.getText().toString()
						+"w50253d"+sos_input_number2.getText().toString()+"w50253d"+sos_input_number3.getText().toString());*/
				//String sosContacts = s1+s2+s3;
				Log.e("[SOSCallActivity]",sosContacts.toString());
				byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.SOS,sosContacts.toString().getBytes());  // 发送紧急拨号 紧急联系人 到 手表
				MainService.getInstance().writeToDevice(l2, true);

			}else{
				Toast.makeText(getApplicationContext(), getString(R.string.contacts_synchronization_failed), Toast.LENGTH_SHORT).show();;
			}
			onBackPressed();
			break;
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	public void ShowMessage(String text) {
		if (null == toast) {
			toast = Toast.makeText(BTNotificationApplication.getInstance(), text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void showLoadingDialogNew(final String content) {
		if (myLoadingDialog == null) {
			myLoadingDialog = new MyLoadingDialog(SoscallActivity.this);
			myLoadingDialog.setWaitingTitle(content);
		}
		myLoadingDialog.show();
	}

	private void dismissLoadingDialog() {
		if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
			myLoadingDialog.dismiss();
		}
		myLoadingDialog = null;
	}

	
}
