package com.szkct.weloopbtsmartdevice.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.szkct.weloopbtsmartdevice.data.MovementDatas;
import com.szkct.weloopbtsmartdevice.data.User;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;


public class Resolve {
	
	public static User resolveMyInfo(Context context,String str){
		User user =null;
		try {
			JSONObject jsonObj = new JSONObject(str);
			if(jsonObj.getString("result").equals("0")){
				String mid = jsonObj.getString("mid");
				String run_day = jsonObj.getString("run_day");
				String run_week = jsonObj.getString("run_week");
				String run_month = jsonObj.getString("run_month");
				String feet_run = jsonObj.getString("feet_run");
				String feet_walk = jsonObj.getString("feet_walk");
				String email = jsonObj.getString("email");
				String weight = jsonObj.getString("weight");
				String height = jsonObj.getString("height");
				String birth = jsonObj.getString("birth");
				String name = jsonObj.getString("name");
				String face = jsonObj.getString("face");
				String sex = jsonObj.getString("sex");
				String score = jsonObj.getString("score");
				String type = jsonObj.getString("type");
				String dyn = jsonObj.getString("dyn");
				String msg = jsonObj.getString("msg");
				String fans = jsonObj.getString("fans");
				String follow = jsonObj.getString("follow");
				String sit = jsonObj.getString("sit");
				String version_code = jsonObj.getString("version_code");
				String friend_request = jsonObj.getString("friend_request");
				String activity = jsonObj.getString("activity");
				String message = jsonObj.getString("message");
				String pos_time = jsonObj.getString("pos_time");
				user = User.getInstance();
				user.setMid(mid);
				user.setRun_day(run_day);
				user.setRun_week(run_week);
				user.setRun_month(run_month);
				user.setFeet_run(feet_run);
				user.setFeet_walk(feet_walk);
				user.setEmail(email);
				user.setName(name);
				user.setFace(face);
				user.setSex(sex);
				user.setScore(score);
				user.setMsg(msg);
				user.setWeight(weight);
				user.setHeight(height);
				user.setBirth(birth);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.MID, mid);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.RUN_DAY, run_day);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.RUN_WEEK, run_week);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.RUN_MONTH, run_month);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.FEET_RUN, feet_run);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.FEET_WALK, feet_walk);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.EMAIL, email);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.NAME, name);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.FACE, face);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.BIRTH, birth);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.FACEURL, "");
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.SEX, sex);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.SCORE, score);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.MSG, msg);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.WEIGHT, weight);
				SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.HEIGHT, height);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return user;
		
	}
	
	public static ArrayList<RunData> resolveRunData(String str){
		ArrayList<RunData> ar = new ArrayList<RunData>();
		try {
			JSONObject jsonObj = new JSONObject(str);
			String result = jsonObj.getString("result");
			if("0".equals(result)){
				JSONArray jsonArr = jsonObj.getJSONArray("datas");
				for(int i = 0; i<jsonArr.length();i++){
					RunData md = new RunData();
					JSONObject json = (JSONObject) jsonArr.get(i);
					String step  = json.getString("step");
					String mile  = json.getString("mile");
					String calorie  = json.getString("calo");
					String datetime  = json.getString("datetime");
					md.setStep(step);
					md.setDistance(mile);
					md.setCalorie(calorie);
					md.setBinTime(datetime);
					ar.add(md);
				}
				return ar;
			}else{
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public static ArrayList<SleepData> resolveSleepData(String str){
		/*ArrayList<SleepData> ar = new ArrayList<SleepData>();
		try {
			JSONObject jsonObj = new JSONObject(str);
			JSONArray jsonArr = jsonObj.getJSONArray("datas");
			for(int i = 0; i<jsonArr.length();i++){
				SleepData md = new SleepData();
				JSONObject json = (JSONObject) jsonArr.get(i);
				String shock  = json.getString("shock");
				String times  = json.getString("times");
				String datetime  = json.getString("datetime");
				md.setShock(shock);
				md.setTimes(times);
				md.setBinTime(datetime);
				ar.add(md);
			}
			return ar;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
*/
		return null;
	}
	
	public static User getUserInfo(Context context){
		try {
			User use = User.getInstance();
			use.setMid(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MID));
			use.setRun_day(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.RUN_DAY));
			use.setRun_week(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.RUN_WEEK)) ;
			use.setRun_month(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.RUN_MONTH)) ;
			use.setFeet_run(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.FEET_RUN)) ;
			use.setFeet_walk(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.FEET_WALK)) ;
			use.setEmail(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.EMAIL)) ;
			use.setName(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.NAME)) ;
			use.setFace(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.FACE)) ;
			use.setFaceUrl(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.FACEURL)) ;
			use.setSex(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SEX)) ;
			use.setScore(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SCORE)) ;
			use.setMsg(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MSG)) ;
			use.setMsg(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.BIRTH)) ;
			use.setWeight(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.WEIGHT)) ;
			use.setHeight(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.HEIGHT)) ;
//			Log.e("Resolve", "getUserInfo00000000000000000"+use.getMid());
//			Log.e("Resolve", "getUserInfo00000000000000000"+SharedPreUtil.readPre(context, SharedPreUtil.USER, "mid"));
			if((use.getMid()).equals("")){
				return null;
			}
			return use;
		} catch (Exception e) {
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.MID, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.RUN_DAY, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.RUN_WEEK, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.RUN_MONTH, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.FEET_RUN, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.FEET_WALK, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.EMAIL, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.NAME, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.FACE, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER,SharedPreUtil.SEX, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.SCORE, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.BIRTH, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.MSG, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.WEIGHT, "");
			SharedPreUtil.savePre(context, SharedPreUtil.USER, SharedPreUtil.HEIGHT, "");
//			Log.e("Resolve", "异常000000000000000000000000000000000000000");
			return null;
		}
	}
}
