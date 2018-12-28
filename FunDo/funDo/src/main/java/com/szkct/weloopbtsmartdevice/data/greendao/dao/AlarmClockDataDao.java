package com.szkct.weloopbtsmartdevice.data.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.szkct.weloopbtsmartdevice.data.greendao.AlarmClockData;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/6/28
 * 描述: ${VERSION}
 * 修订历史：
 */

public class AlarmClockDataDao extends AbstractDao<AlarmClockData, Long> {

    public static final String TABLENAME = "ALARMCLOCK";    // 闹钟表

    /**
     * Properties of entity SleepData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Mac = new Property(1, String.class, "mac", false, "MAC");
        public final static Property Mid = new Property(2, String.class, "mid", false, "MID");
        public final static Property Upload = new Property(3, String.class, "upload", false, "UPLOAD");
        public final static Property Time = new Property(4, String.class, "time", false, "TIME");
        public final static Property Type = new Property(5, String.class, "type", false, "TYPE");
        public final static Property cycle = new Property(6, String.class, "cycle", false, "CYCLE");
        public final static Property alarm_time = new Property(7, String.class, "alarm_time", false, "ALARM_TIME");
        public final static Property remind = new Property(8, String.class, "remind", false, "REMIND");

    };



    public AlarmClockDataDao(DaoConfig config) {
        super(config);
    }

    public AlarmClockDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ALARMCLOCK' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'MAC' TEXT NOT NULL ," + // 1: mac
                "'MID' TEXT NOT NULL ," + // 2: mid
                "'UPLOAD' TEXT NOT NULL ," + // 3: upload
                "'TIME' TEXT NOT NULL ," + // 4: time
                "'TYPE' TEXT NOT NULL ," + // 5: type
                "'CYCLE' TEXT NOT NULL ," + // 6: cycle
                "'ALARM_TIME' TEXT NOT NULL ," + // 7: alarm_time
                "'REMIND' TEXT NOT NULL );"); // 8: remind
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ALARMCLOCK'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AlarmClockData entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getMac());
        stmt.bindString(3, entity.getMid());
        stmt.bindString(4, entity.getUpload());
        stmt.bindString(5, entity.getTime());
        stmt.bindString(6, entity.getType());
        stmt.bindString(7, entity.getCycle());
        stmt.bindString(8, entity.getAlarm_time());
        stmt.bindString(9, entity.getRemind());

    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public AlarmClockData readEntity(Cursor cursor, int offset) {
        AlarmClockData entity = new AlarmClockData( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // mac
                cursor.getString(offset + 2), // mid
                cursor.getString(offset + 3), // upload
                cursor.getString(offset + 4), // time
                cursor.getString(offset + 5), // type
                cursor.getString(offset + 6), // cycle
                cursor.getString(offset + 7), // alarm_time
                cursor.getString(offset + 8)  // remind
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AlarmClockData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMac(cursor.getString(offset + 1));
        entity.setMid(cursor.getString(offset + 2));
        entity.setUpload(cursor.getString(offset + 3));
        entity.setTime(cursor.getString(offset + 4));
        entity.setType(cursor.getString(offset + 5));
        entity.setCycle(cursor.getString(offset + 6));
        entity.setAlarm_time(cursor.getString(offset + 7));
        entity.setRemind(cursor.getString(offset + 8));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(AlarmClockData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(AlarmClockData entity) {
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
