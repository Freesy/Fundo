package com.szkct.weloopbtsmartdevice.data.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.szkct.weloopbtsmartdevice.data.greendao.RunData;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table RUN.
*/
public class RunDataDao extends AbstractDao<RunData, Long> {

    public static final String TABLENAME = "RUN";   // 跑步表
    /**
     * Properties of entity RunData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Mac = new Property(1, String.class, "mac", false, "MAC");  // 蓝牙mac地址
        public final static Property Mid = new Property(2, String.class, "mid", false, "MID");
        public final static Property Upload = new Property(3, String.class, "upload", false, "UPLOAD");
        public final static Property Times = new Property(4, String.class, "times", false, "TIMES");
        public final static Property BinTime = new Property(5, String.class, "binTime", false, "BIN_TIME");
        public final static Property Date = new Property(6, String.class, "date", false, "DATE");
        public final static Property Hour = new Property(7, String.class, "hour", false, "HOUR");
        public final static Property Step = new Property(8, String.class, "step", false, "STEP");
        public final static Property DayStep = new Property(9, String.class, "dayStep", false, "DAY_STEP");
        public final static Property Calorie = new Property(10, String.class, "calorie", false, "CALORIE");
        public final static Property Distance = new Property(11, String.class, "distance", false, "DISTANCE");
        public final static Property DayCalorie = new Property(12, String.class, "dayCalorie", false, "DAY_CALORIE");
        public final static Property DayDistance = new Property(13, String.class, "dayDistance", false, "DAY_DISTANCE");
        public final static Property Data = new Property(14, String.class, "data", false, "DATA");
    };


    public RunDataDao(DaoConfig config) {
        super(config);
    }
    
    public RunDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'RUN' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'MAC' TEXT NOT NULL ," + // 1: mac
                "'MID' TEXT NOT NULL ," + // 2: mid
                "'UPLOAD' TEXT NOT NULL ," + // 3: upload
                "'TIMES' TEXT NOT NULL ," + // 4: times
                "'BIN_TIME' TEXT NOT NULL ," + // 5: binTime
                "'DATE' TEXT NOT NULL ," + // 6: date
                "'HOUR' TEXT NOT NULL ," + // 7: hour
                "'STEP' TEXT NOT NULL ," + // 8: step
                "'DAY_STEP' TEXT NOT NULL ," + // 9: dayStep
                "'CALORIE' TEXT NOT NULL ," + // 10: calorie
                "'DISTANCE' TEXT NOT NULL ," + // 11: distance
                "'DAY_CALORIE' TEXT NOT NULL ," + // 12: dayCalorie
                "'DAY_DISTANCE' TEXT NOT NULL ," + // 13: dayDistance
                "'DATA' TEXT NOT NULL );"); // 14: data
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'RUN'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, RunData entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getMac());
        stmt.bindString(3, entity.getMid());
        stmt.bindString(4, entity.getUpload());
        stmt.bindString(5, entity.getTimes());
        stmt.bindString(6, entity.getBinTime());
        stmt.bindString(7, entity.getDate());
        stmt.bindString(8, entity.getHour());
        stmt.bindString(9, entity.getStep());
        stmt.bindString(10, entity.getDayStep());
        stmt.bindString(11, entity.getCalorie());
        stmt.bindString(12, entity.getDistance());
        stmt.bindString(13, entity.getDayCalorie());
        stmt.bindString(14, entity.getDayDistance());
        stmt.bindString(15, entity.getData());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public RunData readEntity(Cursor cursor, int offset) {
        RunData entity = new RunData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // mac
            cursor.getString(offset + 2), // mid
            cursor.getString(offset + 3), // upload
            cursor.getString(offset + 4), // times
            cursor.getString(offset + 5), // binTime
            cursor.getString(offset + 6), // date
            cursor.getString(offset + 7), // hour
            cursor.getString(offset + 8), // step
            cursor.getString(offset + 9), // dayStep
            cursor.getString(offset + 10), // calorie
            cursor.getString(offset + 11), // distance
            cursor.getString(offset + 12), // dayCalorie
            cursor.getString(offset + 13), // dayDistance
            cursor.getString(offset + 14) // data
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, RunData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMac(cursor.getString(offset + 1));
        entity.setMid(cursor.getString(offset + 2));
        entity.setUpload(cursor.getString(offset + 3));
        entity.setTimes(cursor.getString(offset + 4));
        entity.setBinTime(cursor.getString(offset + 5));
        entity.setDate(cursor.getString(offset + 6));
        entity.setHour(cursor.getString(offset + 7));
        entity.setStep(cursor.getString(offset + 8));
        entity.setDayStep(cursor.getString(offset + 9));
        entity.setCalorie(cursor.getString(offset + 10));
        entity.setDistance(cursor.getString(offset + 11));
        entity.setDayCalorie(cursor.getString(offset + 12));
        entity.setDayDistance(cursor.getString(offset + 13));
        entity.setData(cursor.getString(offset + 14));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(RunData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(RunData entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
