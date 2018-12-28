package com.szkct.weloopbtsmartdevice.data.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table HEART.
*/
public class EcgDao extends AbstractDao<Ecg, Long> {

    public static final String TABLENAME = "Ecg_test";    // 睡眠表
    /**
     * Properties of entity HearData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Mac = new Property(1, String.class, "mac", false, "Mac");
        public final static Property Date = new Property(2, String.class, "date", false, "Date");
        public final static Property BinTime = new Property(3, String.class, "binTime", false, "BinTime");
        public final static Property Hearts = new Property(4, String.class, "hearts", false, "Hearts");
        public final static Property Ecgs = new Property(5, String.class, "ecgs", false, "Ecgs");

    };


    public EcgDao(DaoConfig config) {
        super(config);
    }

    public EcgDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'Ecg_test' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'Mac' TEXT," + // 1: HeightBlood
                "'Date' TEXT," + // 2: MinBlood
                "'BinTime' TEXT," + // 3: Hour
                "'Hearts' TEXT," + // 3: Hour
                "'Ecgs' TEXT );" // 5: Conunt
                ); // 12

    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'Ecg_test'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Ecg entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        if(TextUtils.isEmpty(entity.getMac()))
        {
            entity.setMac("");
        }
        if(TextUtils.isEmpty(entity.getBinTime()))
        {
            entity.setBinTime("");
        }
        if(TextUtils.isEmpty(entity.getDate()))
        {
            entity.setDate("");
        }
        stmt.bindString(2, entity.getMac());
        stmt.bindString(3, entity.getDate());
        stmt.bindString(4, entity.getBinTime());
        stmt.bindString(5, entity.getHearts());
        stmt.bindString(6, entity.getEcgs());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Ecg readEntity(Cursor cursor, int offset) {
        Ecg entity = new Ecg(
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // HeightBlood
            cursor.getString(offset + 2), // MinBlood
            cursor.getString(offset + 3), // Hour
            cursor.getString(offset + 4), // Hour
            cursor.getString(offset + 5) // Hour
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Ecg entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMac(cursor.getString(offset + 1));
        entity.setDate(cursor.getString(offset + 2));
        entity.setBinTime(cursor.getString(offset + 3));
        entity.setHearts(cursor.getString(offset + 4));
        entity.setEcgs(cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Ecg entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Ecg entity) {
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
