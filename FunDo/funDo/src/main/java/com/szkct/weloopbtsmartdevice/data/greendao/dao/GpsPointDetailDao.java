package com.szkct.weloopbtsmartdevice.data.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table GpsPoint.
 */
public class GpsPointDetailDao extends AbstractDao<GpsPointDetailData, Long> {   // GPS详细数据

    public static final String TABLENAME = "GpsPointDetail";

    /**
     * Properties of entity HearData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Mac = new Property(1, String.class, "mac", false, "MAC");
        public final static Property Mid = new Property(2, String.class, "mid", false, "MID");
        public final static Property Mile = new Property(3, Double.class, "mile", false, "MILE");
        public final static Property Ele = new Property(4, String.class, "ele", false, "ELE");
        public final static Property Date = new Property(5, String.class, "date", false, "DATE");
        public final static Property Speed = new Property(6, String.class, "speed", false, "SPEED");
        public final static Property SportTime = new Property(7, String.class, "sportTime", false, "SPORT_TIME");
        public final static Property Calorie = new Property(8, String.class, "calorie", false, "CALORIE");
        public final static Property Stime = new Property(9, String.class, "sTime", false, "S_TIME");
        public final static Property ArrLat = new Property(10, String.class, "arrLat", false, "ARRLAT");
        public final static Property ArrLng = new Property(11, String.class, "arrLng", false, "ARRLNG");
        public final static Property MCurrentSpeed = new Property(12, String.class, "mCurrentSpeed", false, "MCURRENTSPEED");
        public final static Property ArrTotalSpeed = new Property(13, String.class, "arrTotalSpeed", false, "ARRTOTALSPEED");
        public final static Property TimeMillis = new Property(14, String.class, "timeMillis", false, "TIMEMILLIS");
        public final static Property SportType = new Property(15, String.class, "sportType", false, "SPORTTYPE");
        public final static Property HeartRate = new Property(16, String.class, "heartRate", false, "HEARTRATE");
        public final static Property DeviceType = new Property(17, String.class, "deviceType", false, "DEVICETYPE");
        public final static Property PauseNumber = new Property(18, String.class, "pauseNumber", false, "PAUSENUMBER");
        public final static Property PauseTime = new Property(19, String.class, "pauseTime", false, "PAUSETIME");
        public final static Property arrspeed = new Property(20, String.class, "arrspeed", false, "ARRSPEED");
        public final static Property arrcadence = new Property(21, String.class, "arrcadence", false, "ARRCADENCE");
        public final static Property arraltitude = new Property(22, String.class, "arraltitude", false, "ARRALTITUDE");
        public final static Property arrheartRate = new Property(23, String.class, "arrheartRate", false, "ARRHEARTRATE");
        public final static Property min_step_width = new Property(24, String.class, "min_step_width", false, "MIN_STEP_WIDTH");
        public final static Property max_step_width = new Property(25, String.class, "max_step_width", false, "MAX_STEP_WIDTH");
        public final static Property ave_step_width = new Property(26, String.class, "ave_step_width", false, "AVE_STEP_WIDTH");
        public final static Property step = new Property(27, String.class, "step", false, "STEP");
    }

    ;


    public GpsPointDetailDao(DaoConfig config) {
        super(config);
    }

    public GpsPointDetailDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'GpsPointDetail' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'MAC' TEXT NOT NULL ," + // 1: mac
                "'MID' TEXT NOT NULL ," + // 2: mid
                "'MILE' DOUBLE ," + // 5: MILE
                "'ELE' TEXT NOT NULL ," + // 6: ELE
                "'DATE' TEXT NOT NULL ," + // 7: DATE
                "'SPEED' TEXT NOT NULL ," + // 8: SPEED
                "'SPORT_TIME' TEXT NOT NULL ," +// 9: SPORT_TIME
                "'CALORIE' TEXT NOT NULL ," +// 10: CALORIE
                "'S_TIME' TEXT NOT NULL ," + // 11: S_TIME
                "'ARRLAT' TEXT NOT NULL ," + // 13: ARRLAT
                "'ARRLNG' TEXT NOT NULL ," + // 14: ARRLNG
                "'MCURRENTSPEED' TEXT NOT NULL ," + // 15: MCURRENTSPEED
                "'ARRTOTALSPEED' TEXT NOT NULL ," + // 16: ARRTOTALSPEED
                "'TIMEMILLIS' TEXT NOT NULL ," + // 17: TIMEMILLIS
                "'SPORTTYPE' TEXT NOT NULL ," + // 18: SPORTTYPE
                "'DEVICETYPE' TEXT NOT NULL ," +  // 19: DEVICETYPE
                "'HEARTRATE' TEXT NOT NULL ," +  // 20: HEARTRATE
                "'PAUSENUMBER' TEXT NOT NULL ,"+ // 21: PAUSENUMBER
                "'PAUSETIME' TEXT NOT NULL ,"+ // 22: PAUSETIME
                "'ARRSPEED' TEXT NOT NULL ,"+ // 23: ARRSPEED
                "'ARRCADENCE' TEXT NOT NULL ,"+ // 24: ARRCADENCE
                "'ARRALTITUDE' TEXT NOT NULL ,"+ // 25: ARRALTITUDE
                "'ARRHEARTRATE' TEXT NOT NULL ,"+ // 26: ARRHEARTRATE
                "'MIN_STEP_WIDTH' TEXT NOT NULL ,"+ // 27: MIN_STEP_WIDTH
                "'MAX_STEP_WIDTH' TEXT NOT NULL ,"+ // 28: max_step_width
                "'AVE_STEP_WIDTH' TEXT NOT NULL ,"+ // 29: ARRALTITUDE
                "'STEP');"); // 30: STEP
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'GpsPointDetail'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, GpsPointDetailData entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getMac());
        stmt.bindString(3, entity.getMid());
        stmt.bindDouble(4, entity.getMile());
        stmt.bindString(5, entity.getAltitude());
        stmt.bindString(6, entity.getDate());
        stmt.bindString(7, entity.getSpeed());
        stmt.bindString(8, entity.getSportTime());
        stmt.bindString(9, entity.getCalorie());
        stmt.bindString(10, entity.getsTime());
        stmt.bindString(11, entity.getArrLat());
        stmt.bindString(12, entity.getArrLng());
        stmt.bindString(13, entity.getmCurrentSpeed());
        stmt.bindString(14, entity.getArrTotalSpeed());
        stmt.bindString(15, entity.getTimeMillis());
        stmt.bindString(16, entity.getSportType());
        stmt.bindString(17, entity.getHeartRate());
        stmt.bindString(18, entity.getDeviceType());
        stmt.bindString(19, entity.getPauseNumber());
        stmt.bindString(20, entity.getPauseTime());
        stmt.bindString(21, entity.getArrspeed());
        stmt.bindString(22, entity.getArrcadence());
        stmt.bindString(23, entity.getArraltitude());
        stmt.bindString(24, entity.getArrheartRate());
        stmt.bindString(25, entity.getMin_step_width());
        stmt.bindString(26, entity.getMax_step_width());
        stmt.bindString(27, entity.getAve_step_width());
        stmt.bindString(28, entity.getStep());
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public GpsPointDetailData readEntity(Cursor cursor, int offset) {
        GpsPointDetailData entity = new GpsPointDetailData(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.getString(offset + 1), // mac
                cursor.getString(offset + 2), // mid
                cursor.getDouble(offset + 3), // upload
                cursor.getString(offset + 4), // times
                cursor.getString(offset + 5), // binTime
                cursor.getString(offset + 6), // date
                cursor.getString(offset + 7), // hour
                cursor.getString(offset + 8), // heartbeat
                cursor.getString(offset + 9), // heartbeat
                cursor.getString(offset + 10), // heartbeat
                cursor.getString(offset + 11), // data
                cursor.getString(offset + 12), // distance
                cursor.getString(offset + 13), // distance
                cursor.getString(offset + 14), // distance
                cursor.getString(offset + 15), // distance
                cursor.getString(offset + 16), // distance
                cursor.getString(offset + 17), // distance
                cursor.getString(offset + 18), // distance
                cursor.getString(offset + 19), // distance
                cursor.getString(offset + 20), // distance
                cursor.getString(offset + 21), // distance
                cursor.getString(offset + 22), // distance
                cursor.getString(offset + 23), // distance
                cursor.getString(offset + 24), // distance
                cursor.getString(offset + 25), // distance
                cursor.getString(offset + 26), // distance
                cursor.getString(offset + 27)
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, GpsPointDetailData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMac(cursor.getString(offset + 1));
        entity.setMid(cursor.getString(offset + 2));
        entity.setMile(cursor.getDouble(offset + 3));
        entity.setAltitude(cursor.getString(offset + 4));
        entity.setDate(cursor.getString(offset + 5));
        entity.setSpeed(cursor.getString(offset + 6));
        entity.setSportTime(cursor.getString(offset + 7));
        entity.setCalorie(cursor.getString(offset + 8));
        entity.setsTime(cursor.getString(offset + 9));
        entity.setArrLat(cursor.getString(offset + 10));
        entity.setArrLng(cursor.getString(offset + 11));
        entity.setmCurrentSpeed(cursor.getString(offset + 12));
        entity.setArrTotalSpeed(cursor.getString(offset + 13));
        entity.setTimeMillis(cursor.getString(offset + 14));
        entity.setSportType(cursor.getString(offset + 15));
        entity.setHeartRate(cursor.getString(offset + 16));
        entity.setDeviceType(cursor.getString(offset + 17));
        entity.setPauseNumber(cursor.getString(offset + 18));
        entity.setPauseTime(cursor.getString(offset + 19));
        entity.setArrspeed(cursor.getString(offset + 20));
        entity.setArrcadence(cursor.getString(offset + 21));
        entity.setArraltitude(cursor.getString(offset + 22));
        entity.setArrheartRate(cursor.getString(offset + 23));
        entity.setMin_step_width(cursor.getString(offset + 24));
        entity.setMax_step_width(cursor.getString(offset + 25));
        entity.setAve_step_width(cursor.getString(offset + 26));
        entity.setStep(cursor.getString(offset + 27));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(GpsPointDetailData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(GpsPointDetailData entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
