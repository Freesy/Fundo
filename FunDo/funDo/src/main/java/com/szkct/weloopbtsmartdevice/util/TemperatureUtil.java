package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;
import android.os.Message;

import com.szkct.weloopbtsmartdevice.data.greendao.Oxygen;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.OxyDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.TemperatureDao;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;

public class TemperatureUtil {



    public static List judgmentTemperatureDB(Context context) {
        DBHelper db = DBHelper.getInstance(context);
        Query query = null;
        if (SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
            query = db.getTemperatureDao().queryBuilder().orderAsc(TemperatureDao.Properties.BinTime).build();
        } else {  //  不需要展示的设备的数据的mac地址
            query = db.getTemperatureDao().queryBuilder().orderAsc(TemperatureDao.Properties.BinTime).build();  // 根据日期
        }
        List list = query.list();
        return list;
    }
//    public List judgmentTemperatureDB(Context context,String choiceDate) {
//        DBHelper db = DBHelper.getInstance(context);
//        Query query = null;
//        if (SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
//            query = db.getTemperatureDao().queryBuilder().where(OxyDao.Properties.Mac.eq(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MAC))).where(TemperatureDao.Properties.Date.eq(choiceDate)).orderAsc(TemperatureDao.Properties.BinTime).build();
//        } else {  //  不需要展示的设备的数据的mac地址
//            query = db.getTemperatureDao().queryBuilder().where(OxyDao.Properties.Mac.eq(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).where(TemperatureDao.Properties.Date.eq(choiceDate)).orderAsc(TemperatureDao.Properties.BinTime).build();  // 根据日期
//        }
//        List list = query.list();
//        return list;
//    }










}
