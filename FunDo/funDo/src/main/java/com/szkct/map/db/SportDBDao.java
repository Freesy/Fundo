package com.szkct.map.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.szkct.map.bean.GpsPoint;

import java.util.ArrayList;
import java.util.List;


/**
 * 2016/12/1.
 * 版本：v1.0
 */

public class SportDBDao {

    private static final String DB_NAME = "gpsPoint.db";//数据库名称
    private static final String TABLE_NAME = "gps_list";//数据表名称
    private static final int DB_VERSION = 2;//数据库版本


    //表的字段名
    private static String KEY_ID = "id";
    private static String KEY_SPORTTIME = "sportTime";//运动时间
    private static String KEY_LAT = "lat";//经度
    private static String KEY_LON= "lon";//纬度
    private static String KEY_MILE= "mile";//距离/米
    private static String KEY_ELE= "ele";//海拔
    private static String KEY_DATE= "date";//系统时间
    private static String KEY_SPEED= "speed";//速度
    private static String KEY_CALORIE= "calorie";//消耗/千卡
    private static String KEY_STIME= "sTime";//秒数 时间
    private static String KEY_TOTALPS="totalps"; //总时间 总距离 配速

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private SportDBOpenHelper mDbOpenHelper;//数据库打开帮助类

    /**
     * Database creation sql statement
     */
    private static final String CREATE_CARLIST_TABLE = "create table "
            + TABLE_NAME + " (" + KEY_ID
            + " integer primary key autoincrement, " + KEY_SPORTTIME
            + " text not null, " + KEY_LAT + " text not null,"
            + KEY_LON + " text not null, " + KEY_MILE + " text not null,"
            + KEY_ELE + " text not null, " + KEY_DATE
            + " text not null," + KEY_SPEED + " text not null,"+KEY_CALORIE+" text not null,"+KEY_STIME+" text not null,"+KEY_TOTALPS+" text not null)";


    public SportDBDao(Context context) {
        mContext = context;
    }

    //打开数据库
    public void openDataBase() {
        mDbOpenHelper = new SportDBOpenHelper(mContext, DB_NAME, null, DB_VERSION);
        try {
            mDatabase = mDbOpenHelper.getWritableDatabase();//获取可写数据库
        } catch (SQLException e) {
            mDatabase = mDbOpenHelper.getReadableDatabase();//获取只读数据库
        }
    }

    //关闭数据库
    public void closeDataBase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    //插入一条数据
    public long insertData(GpsPoint gpsPoint) {
        ContentValues values = new ContentValues();
        values.put(KEY_SPORTTIME, gpsPoint.getSportTime());
        values.put(KEY_LAT, gpsPoint.getLat());
        values.put(KEY_LON, gpsPoint.getLon());
        values.put(KEY_MILE, gpsPoint.getMile());
        values.put(KEY_ELE, gpsPoint.getEle());
        values.put(KEY_DATE, gpsPoint.getDate());
        values.put(KEY_SPEED, gpsPoint.getSpeed());
        values.put(KEY_CALORIE, gpsPoint.getCalorie());
        values.put(KEY_STIME, gpsPoint.getsTime());
        values.put(KEY_TOTALPS, gpsPoint.getTotalPs());
        return mDatabase.insert(TABLE_NAME, null, values);
    }

    //删除一条数据
    public long deleteData(long id) {
        return mDatabase.delete(TABLE_NAME, KEY_ID + "=" + id, null);
    }

    //删除所有数据
    public long deleteAllData() {
        return mDatabase.delete(TABLE_NAME, null, null);
    }

    //更新一条数据
    public long updateData(long id, GpsPoint gpsPoint) {
        ContentValues values = new ContentValues();
        values.put(KEY_SPORTTIME, gpsPoint.getSportTime());
        values.put(KEY_LAT, gpsPoint.getLat());
        values.put(KEY_LON, gpsPoint.getLon());
        values.put(KEY_MILE, gpsPoint.getMile());
        values.put(KEY_ELE, gpsPoint.getEle());
        values.put(KEY_DATE, gpsPoint.getDate());
        values.put(KEY_SPEED, gpsPoint.getSpeed());
        values.put(KEY_CALORIE, gpsPoint.getCalorie());
        values.put(KEY_STIME, gpsPoint.getsTime());
        values.put(KEY_TOTALPS, gpsPoint.getTotalPs());
        return mDatabase.update(TABLE_NAME, values, KEY_ID + "=" + id, null);
    }

    //查询一条数据
    public List<GpsPoint> queryData(long id) {
        Cursor results = mDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_SPORTTIME, KEY_LAT, KEY_LON,KEY_MILE,KEY_ELE,KEY_DATE,KEY_SPEED,KEY_CALORIE,KEY_STIME,KEY_TOTALPS},
                KEY_ID + "=" + id, null, null, null, null);
        return convertToGpsPoint(results);
    }

    /**
     * 查询获取所有数据
     * @return
     */
    public List<GpsPoint> getAllData() {
        openDataBase();//打开数据库
        Cursor results = mDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_SPORTTIME, KEY_LAT, KEY_LON,KEY_MILE,KEY_ELE,KEY_DATE,KEY_SPEED,KEY_CALORIE,KEY_STIME,KEY_TOTALPS},
                null, null, null, null, null);
        return convertToGpsPoint(results);

    }

    private List<GpsPoint> convertToGpsPoint(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<GpsPoint> mGpsPointList = new ArrayList<>();
        for (int i = 0; i < resultCounts; i++) {
            GpsPoint gpsPoint = new GpsPoint();
//            gpsPoint.setId(cursor.getInt(0));
            gpsPoint.setSportTime(cursor.getString(cursor.getColumnIndex(KEY_SPORTTIME)));
            gpsPoint.setLat(cursor.getString(cursor.getColumnIndex(KEY_LAT)));
            gpsPoint.setLon(cursor.getString(cursor.getColumnIndex(KEY_LON)));
            gpsPoint.setMile(cursor.getString(cursor.getColumnIndex(KEY_MILE)));
            gpsPoint.setEle(cursor.getString(cursor.getColumnIndex(KEY_ELE)));
            gpsPoint.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            gpsPoint.setSpeed(cursor.getString(cursor.getColumnIndex(KEY_SPEED)));
            gpsPoint.setCalorie(cursor.getString(cursor.getColumnIndex(KEY_CALORIE)));
            gpsPoint.setsTime(cursor.getString(cursor.getColumnIndex(KEY_STIME)));
            gpsPoint.setTotalPs(cursor.getString(cursor.getColumnIndex(KEY_TOTALPS)));
            mGpsPointList.add(gpsPoint);
            cursor.moveToNext();
        }
        return mGpsPointList;
    }


    /**
     * 数据表打开帮助类
     */
    private static class SportDBOpenHelper extends SQLiteOpenHelper {

        public SportDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            final String sqlStr = "create table if not exists " + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_NAME + " text not null, " + KEY_AGE + " integer," + KEY_PRICE + " float);";
            db.execSQL(CREATE_CARLIST_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            final String sqlStr = "DROP TABLE IF EXISTS " + TABLE_NAME;
            db.execSQL(sqlStr);
            onCreate(db);
        }
    }
    
}
