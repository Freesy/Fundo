package com.szkct.weloopbtsmartdevice.util;

import java.io.Serializable;

public class NearByFriendInfo implements Serializable {

	private static final long serialVersionUID = -1238060267830184874L;
	// id
	private String id;
	// 定义头像
	private String headIcon;
	// 定义姓名
	private String name;
	// 定义等级
	private String level;
	// 添加好友识别id
	private String rid;

	// 附近的人距离
	private String range;
	// 排行
	private String rank;
	// 申请好友附加信息
	private String note;
	// flag判断好友的标示
	private String flag;
	// 是否公开位置
	private String frp_flag;
	// 经度
	private String longitudestr;
	// 纬度
	private String latitudestr;
	// 性别
	private String sex;

	private String birthday;

	public NearByFriendInfo() {

	}

	// 不带fid参数的构造函数
	public NearByFriendInfo(String id, String headIcon, String name,
			String level) {
		this.headIcon = headIcon;
		this.id = id;
		this.name = name;
		this.level = level;
	}
	
	// 带rank参数的构造函数
		public NearByFriendInfo(String id, String headIcon, String name,
				String level,String rank,String sex,String flag) {
			this.headIcon = headIcon;
			this.rank = rank;
			this.name = name;
			this.level = level;
			this.id = id;
			this.sex = sex;
			this.flag = flag;
		}

	// 带rid参数的构造函数
	public NearByFriendInfo(String id, String headIcon, String name,
			String level, String rid) {
		this.headIcon = headIcon;
		this.id = id;
		this.name = name;
		this.level = level;
		this.rid = rid;
	}

	// 带range参数
	public NearByFriendInfo(String id, String headIcon, String name,
			String level, String rid, String sex) {
		this.headIcon = headIcon;
		this.id = id;
		this.name = name;
		this.level = level;
		this.rid = rid;
		this.sex = sex;
	}

	// 带经纬度的参数
	public NearByFriendInfo(String id, String headIcon, String name,
			String level, String rid, String range, String longitudestr,
			String latitudestr, String sex, String birthday) {
		this.headIcon = headIcon;
		this.id = id;
		this.name = name;
		this.level = level;
		this.rid = rid;
		this.range = range;
		this.longitudestr = longitudestr;
		this.latitudestr = latitudestr;
		this.sex = sex;
		this.birthday = birthday;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLongitudestr() {
		return longitudestr;

	}

	public void setLongitudestr(String longitudestr) {
		this.longitudestr = longitudestr;
	}

	public String getLatitudestr() {
		return latitudestr;

	}

	public void setlatitudestr(String latitudestr) {
		this.latitudestr = latitudestr;
	}

	public String getFrp_flag() {
		return frp_flag;
	}

	public void setFrp_flag(String frp_flag) {
		this.frp_flag = frp_flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

}
