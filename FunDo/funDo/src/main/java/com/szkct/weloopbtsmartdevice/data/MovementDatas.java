package com.szkct.weloopbtsmartdevice.data;


import android.os.Parcel;
import android.os.Parcelable;

public class MovementDatas implements Parcelable {
	/**
	 * 
	 */
	
	public static final String TABLE_NAME= "MovementDatas";
	public static final String TYPE_RUN= "0";
	public static final String TYPE_SLEEP= "1";
	public static final String TYPE_SEAT= "2";
	public static final String TYPE_HEARTBEAT= "3";
	
	public static final String DATAID = "dataId";
	public static final String MID = "mid";
	public static final String UPLOAD = "upload";
	public static final String TYPE = "type";
	public static final String TIMES = "times";
	public static final String BINTIME = "binTime";
	public static final String DATE = "date";
	public static final String CALORIE = "calorie";
	public static final String DISTANCE = "distance";
	
	private int dataId;
	private String mid;
	private String upload;	//上传状态
	private String type;	//数据类型
	private String times;	//运动的时间，秒
	private String binTime;	//开始时间
	private String date;	//运动的数据
	private String calorie;	//卡路里
	private String distance;	//距离
	
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getCalorie() {
		return calorie;
	}
	public void setCalorie(String calorie) {
		this.calorie = calorie;
	}
	public String getUpload() {
		return upload;
	}
	public void setUpload(String upload) {
		this.upload = upload;
	}
	
	public int getDataId() {
		return dataId;
	}
	public void setDataId(int dataId) {
		this.dataId = dataId;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
	public String getBinTime() {
		return binTime;
	}
	public void setBinTime(String binTime) {
		this.binTime = binTime;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mid);
		dest.writeString(upload);
		dest.writeString(type);
		dest.writeString(times);
		dest.writeString(binTime);
		dest.writeString(date);
		dest.writeString(calorie);
		dest.writeString(distance);
	}
	public static final Parcelable.Creator<MovementDatas> CREATOR = new Creator<MovementDatas>() {
		@Override
		public MovementDatas[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MovementDatas[size];
		}
		
		@Override
		public MovementDatas createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new MovementDatas(source);
		}
	};
	 public MovementDatas(Parcel in) {
		 	mid = in.readString();
		 	upload = in.readString();
		 	type = in.readString();
		 	times = in.readString();
		 	binTime = in.readString();
		 	date = in.readString();
		 	calorie = in.readString();
		 	distance = in.readString();
	 }
	 public MovementDatas() {}
	
}
