package com.szkct.weloopbtsmartdevice.data;

import java.io.Serializable;

public class User implements Serializable{
	
	private User(){};
	private static User user = null;
	public static User getInstance(){
		if(user==null){
			user = new User();
		}
		return user;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private String mid = "";             		//用户id
	private String run_day = "";				//天目标
	private String run_week = "";				//周目标
	private String run_month = "";				//月目标
	private String feet_run = "";				//跑步步长
	private String feet_walk = "";				//走路步长
	private String email = "";					//如果是邮箱登录就返回email，否则为空
	private String birth = "";					//生日
	private String name = "";					//昵称
	private String face = "";					//头像
	private String faceUrl = "";             	//头像Url
	private String sex = "";					//性别，0为女，1为男
	private String score = "";					//积分
	private String msg = "";					//消息条数
	private String friend_request = "";			//好友
	private String weight = "";					//体重
	private String height = "";					//身高
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getRun_day() {
		return run_day;
	}
	public void setRun_day(String run_day) {
		this.run_day = run_day;
	}
	public String getRun_week() {
		return run_week;
	}
	public void setRun_week(String run_week) {
		this.run_week = run_week;
	}
	public String getRun_month() {
		return run_month;
	}
	public void setRun_month(String run_month) {
		this.run_month = run_month;
	}
	public String getFeet_run() {
		return feet_run;
	}
	public void setFeet_run(String feet_run) {
		this.feet_run = feet_run;
	}
	public String getFeet_walk() {
		return feet_walk;
	}
	public void setFeet_walk(String feet_walk) {
		this.feet_walk = feet_walk;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getFaceUrl() {
		return faceUrl;
	}
	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getFriend_request() {
		return friend_request;
	}
	public void setFriend_request(String friend_request) {
		this.friend_request = friend_request;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
	
	
}
