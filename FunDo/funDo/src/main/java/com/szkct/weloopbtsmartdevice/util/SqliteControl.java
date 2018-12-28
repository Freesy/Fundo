package com.szkct.weloopbtsmartdevice.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.szkct.weloopbtsmartdevice.data.MovementDatas;

public class SqliteControl extends SQLiteOpenHelper {
	private SQLiteDatabase db;
	public static final String dbName = "MovementDatas.db";
	public static final int version = 3;
	public static SqliteControl sc = null;
	ArrayList<MovementDatas> arrRunData = null;
	ArrayList<MovementDatas> arrRunDataDay = null;
	private SimpleDateFormat mDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd"); 
	private static String id;

	private SqliteControl(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public static SqliteControl getInstence(Context context){
		if(sc == null){
			sc = new SqliteControl(context, dbName, null, version);
			id = SharedPreUtil.readPre(context,
					SharedPreUtil.USER, SharedPreUtil.MID);
		}
		return sc;
	}
	
	public void openDB(){
		db = this.getWritableDatabase();

	}
	
	public void closeDB(){
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE IF NOT EXISTS "+MovementDatas.TABLE_NAME+"("
				+MovementDatas.DATAID+" integer primary key autoincrement,"
				+MovementDatas.MID+" text, "
				+MovementDatas.UPLOAD+" text, "
				+MovementDatas.TYPE+" text, "
				+MovementDatas.TIMES+" text, "
				+MovementDatas.BINTIME+" text, "
				+MovementDatas.CALORIE+" text, "
				+MovementDatas.DISTANCE+" text, "
				+MovementDatas.DATE+" text);";
		db.execSQL(sql);
		//运动轨迹显示；
		db.execSQL(Constants.DATABASE_TABLE_USER_CREATE);
		db.execSQL(Constants.DATABASE_TABLE_GPS_POINTS_CREATE);
		db.execSQL(Constants.DATABASE_TABLE_GPS_RUN_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//运动轨迹显示；
		db.execSQL("DROP TABLE IF EXISTS "
				+ Constants.DATABASE_TABLE_SPORT);
		db.execSQL("DROP TABLE IF EXISTS "
				+ Constants.DATABASE_TABLE_GPS_POINTS);
		db.execSQL("DROP TABLE IF EXISTS "
				+ Constants.DATABASE_TABLE_GPS_RUN);
		onCreate(db);
	}
	
	//插入数据
		public long insertMessage(MovementDatas md){
			ContentValues values = new ContentValues();
			values.put(MovementDatas.MID, md.getMid());
			values.put(MovementDatas.UPLOAD, md.getUpload());
			values.put(MovementDatas.TYPE, md.getType());
			values.put(MovementDatas.TIMES, md.getTimes());
			values.put(MovementDatas.BINTIME,md.getBinTime());
			values.put(MovementDatas.CALORIE,md.getCalorie());
			values.put(MovementDatas.DISTANCE,md.getDistance());
			values.put(MovementDatas.DATE,md.getDate());
			return db.insert(MovementDatas.TABLE_NAME, null, values);
		}
		
		//删除数据
		public int delMessage(String where){
			return db.delete(MovementDatas.TABLE_NAME, where, null);
		}
		
		//修改数据  根据条件修改
		public int updateMessage(String where,String str){
			ContentValues values = new ContentValues();
//			values.put(MovementDatas.MID, md.getMid());
			values.put(MovementDatas.UPLOAD, str);
//			values.put(MovementDatas.TYPE, md.getType());
//			values.put(MovementDatas.TIMES, md.getTimes());
//			values.put(MovementDatas.BINTIME,md.getBinTime());
//			values.put(MovementDatas.CALORIE,md.getCalorie());
//			values.put(MovementDatas.DISTANCE,md.getDistance());
//			values.put(MovementDatas.DATE,md.getDate());
			 return db.update(MovementDatas.TABLE_NAME, values, where, null);
		}
		
		//查询数据 
		public ArrayList<MovementDatas> selectMessage(String where,String orderby){
			ArrayList<MovementDatas> mds = new ArrayList<MovementDatas>();
			Cursor cursor = db.query(MovementDatas.TABLE_NAME, null, where+" and mid='"+id+"'", null, null, null, orderby);
			while(cursor.moveToNext()){
				int dataId = cursor.getInt(0);
				String mid = cursor.getString(1);
				String upload = cursor.getString(2);
				String type = cursor.getString(3);
				String times = cursor.getString(4);
				String binTime = cursor.getString(5);
				String calorie = cursor.getString(6);
				String distance = cursor.getString(7);
				String date = cursor.getString(8);
				MovementDatas md = new MovementDatas();
				md.setDataId(dataId);
				md.setMid(mid);
				md.setUpload(upload);
				md.setType(type);
				md.setTimes(times);
				md.setBinTime(binTime);
				md.setCalorie(calorie);
				md.setDistance(distance);
				md.setDate(date);
				
				mds.add(md);
			}
			cursor.close();
			return mds;
		}
		
		/**
		 * 从数据库读数据的方法
		 * @param String date 格式“ 2015-04-07 ”
		 * @param int type 0运动，1睡眠，2久坐，3心率
		 * @return 
		 */
		public ArrayList<MovementDatas> getData(String date,int type){
			ArrayList<MovementDatas> arrRunData =null;
			ArrayList<MovementDatas> arrRunDataDay =null;
			switch (type) {
			case 0:
				arrRunData = sc.selectMessage("type='0'", null);
				break;

			case 1:
				arrRunData = sc.selectMessage("type='1'", null);
				break;
				
			case 2:
				arrRunData = sc.selectMessage("type='2'", null);
				break;
				
			case 3:
				arrRunData = sc.selectMessage("type='3'", null);
				break;

			default:
				break;
			}
			arrRunDataDay = new ArrayList<MovementDatas>();
			Log.e("EverydayDataActivity", "arrRunData.size()......."+arrRunData.size());
			if(arrRunData!=null){
				for(int i = 0; i<arrRunData.size();i++){
					MovementDatas mb = arrRunData.get(i);
					String[] dateData = mb.getBinTime().split(" ");
					String[] strData = dateData[0].split("-");
					String month = "";
					String day = "";
					if(strData[1].length()==1){
						month = "-0"+strData[1];
					}else{
						month = "-"+strData[1];
					}
					if(strData[2].length()==1){
						day = "-0"+strData[2];
					}else{
						day = "-"+strData[2];
					}
					String str = strData[0]+month+day;
					Log.e("EverydayDataActivity", "时间 = "+str);
					String strDateData = str+" "+dateData[1];
					Log.e("SqliteControl-----", " strDateData="+strDateData);
					if(date.equals("")){
						if(getTime().equals(str)){
							mb.setBinTime(strDateData);
							arrRunDataDay.add(mb);
						}
					}else{
						if(date.equals(str)){
							mb.setBinTime(strDateData);
							arrRunDataDay.add(mb);
						}
					}
				}
				Log.e("EverydayDataActivity", "当前时间 = "+getTime());
				
			}
			return arrRunDataDay;
		}
		
		public String getTime(){
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
			return mDateFormat.format(curDate); 
		}
	

}
