package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;

import com.szkct.weloopbtsmartdevice.data.WatchInfoData;
import com.szkct.weloopbtsmartdevice.data.greendao.AlarmClockData;
import com.szkct.weloopbtsmartdevice.data.greendao.Bloodpressure;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointData;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.Oxygen;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.Temperature;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.AlarmClockDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.BloodpressureDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.CustomerDao.Properties;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.DaoSession;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.EcgDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.GpsPointDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.GpsPointDetailDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.HearDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.OxyDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.RunDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.SleepDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.TemperatureDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.WatchInfoDataDao;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class DBHelper {
	private static final String TAG = DBHelper.class.getSimpleName(); 
    private static DBHelper instance; 
    private static BTNotificationApplication appContext;
    private static DaoSession mDaoSession;
    private static RunDataDao runDao;	//�˶����ݿ������
    private static SleepDataDao sleepDao; 	//˯�����ݿ������
    private static HearDataDao hearDao;	//�������ݿ������
    private static GpsPointDao mGpsPointDao;
    private static GpsPointDetailDao mGpsPointDetailDao;
    private static AlarmClockDataDao alarmClockDataDao;
    private static BloodpressureDao bloodpressureDao;
    private static OxyDao oxygenDao;
    private static EcgDao ecgDao;
    private static TemperatureDao temperatureDao;
    private static WatchInfoDataDao watchInfoDataDao;



    private DBHelper() {
    }

    //����ģʽ��DBHelperֻ��ʼ��һ��
    public static  DBHelper getInstance(Context context) { 
        if (instance == null) {
                   instance = new DBHelper(); 
                   if (appContext == null){ 
//                       appContext = (ControlApp)context.getApplicationContext(); 
                	   appContext = BTNotificationApplication.getInstance();
                   } 
                   instance.mDaoSession = BTNotificationApplication.getDaoSession(context);
                   instance.runDao = instance.mDaoSession.getRunDataDao();	//�˶����ݿ����������ȡ
                   instance.sleepDao = instance.mDaoSession.getSleepDataDao();	//˯�����ݿ����������ȡ
                   instance.hearDao = instance.mDaoSession.getHearDataDao();	//�������ݿ����������ȡ
                   instance.mGpsPointDao = instance.mDaoSession.getGpsPointDao();
                   instance.mGpsPointDetailDao = instance.mDaoSession.getGpsPointDetailDao();
                   instance.alarmClockDataDao = instance.mDaoSession.getAlarmClockDataDao();
                   instance.bloodpressureDao = instance.mDaoSession.getBloodpressureDaos();
                   instance.oxygenDao = instance.mDaoSession.getOxygenDao();
                   instance.ecgDao = instance.mDaoSession.getEcgDao();
                   instance.temperatureDao = instance.mDaoSession.getTemperatureDao();
                   instance.watchInfoDataDao = instance.mDaoSession.getWatchInfoDataDao();

               }
        return instance; 
    }

   public RunDataDao getRunDao(){
	   return runDao;
   }
   public SleepDataDao getSleepDao(){
	   return sleepDao;
   }
   public HearDataDao getHearDao(){
	   return hearDao;
   }
    public GpsPointDao getGpsPointDao(){
        return mGpsPointDao;
    }
    public GpsPointDetailDao getGpsPointDetailDao(){
        return mGpsPointDetailDao;
    }
    public AlarmClockDataDao getAlarmClockDataDao(){
        return alarmClockDataDao;
    }

    public BloodpressureDao getBloodpressureDao() {return bloodpressureDao;}
    public OxyDao getOxygenDao() {return oxygenDao;}

    public EcgDao getEcgDao() {return ecgDao;}

    public static TemperatureDao getTemperatureDao() {
        return temperatureDao;
    }

    public WatchInfoDataDao getWatchInfoDataDao() {return watchInfoDataDao;}


    //ɾ��Session��
    public  void dropRunTable()
    {
        RunDataDao.dropTable(mDaoSession.getDatabase(), true);
    }
    //ɾ��MqttChatEntity��
    public void dropSleepTable()
    {
        SleepDataDao.dropTable(mDaoSession.getDatabase(), true);
    }
  //ɾ��MqttChatEntity��
    public void dropHearTable()
    {
        HearDataDao.dropTable(mDaoSession.getDatabase(), true);
    }
    //ɾ�����б�
    public void dropGpsPointTable()
    {
        GpsPointDao.dropTable(mDaoSession.getDatabase(), true);
    }
    public void dropmGpsPointDetailTable()
    {
        GpsPointDetailDao.dropTable(mDaoSession.getDatabase(), true);
    }
    public void dropAlarmClockTable()
    {
        AlarmClockDataDao.dropTable(mDaoSession.getDatabase(), true);
    }
    public void dropBloodpressureTable()
    {
        BloodpressureDao.dropTable(mDaoSession.getDatabase(), true);
    }

    public void dropAllTable()
    {
    	RunDataDao.dropTable(mDaoSession.getDatabase(), true);
        SleepDataDao.dropTable(mDaoSession.getDatabase(), true);
        HearDataDao.dropTable(mDaoSession.getDatabase(), true);
        GpsPointDao.dropTable(mDaoSession.getDatabase(), true);
        GpsPointDetailDao.dropTable(mDaoSession.getDatabase(), true);
        AlarmClockDataDao.dropTable(mDaoSession.getDatabase(), true);
        BloodpressureDao.dropTable(mDaoSession.getDatabase(), true);
        OxyDao.dropTable(mDaoSession.getDatabase(), true);
        EcgDao.dropTable(mDaoSession.getDatabase(), true);
        TemperatureDao.dropTable(mDaoSession.getDatabase(), true);
    }
    //�������б�
    public static void createAllTable()
    {
    	RunDataDao.createTable(mDaoSession.getDatabase(), true);
        SleepDataDao.createTable(mDaoSession.getDatabase(), true);
        HearDataDao.createTable(mDaoSession.getDatabase(), true);
        GpsPointDao.createTable(mDaoSession.getDatabase(), true);
        GpsPointDetailDao.createTable(mDaoSession.getDatabase(), true);
        AlarmClockDataDao.createTable(mDaoSession.getDatabase(), true);
        BloodpressureDao.createTable(mDaoSession.getDatabase(), true);
        OxyDao.createTable(mDaoSession.getDatabase(), true);
        EcgDao.createTable(mDaoSession.getDatabase(), true);
        TemperatureDao.createTable(mDaoSession.getDatabase(), true);
    }
    /**
     * insert or update note
     */
    //����RunData��
    public long saveRunData(RunData runData){ 
        return runDao.insert(runData);
    }
    public long saveBloodpressure(Bloodpressure bloodpressure){return bloodpressureDao.insert(bloodpressure);}
    public long saveOxygen(Oxygen oxygen){return oxygenDao.insert(oxygen);}
    public long saveTemperature(Temperature temperature){return temperatureDao.insert(temperature);}
    public long saveEcg(Ecg ecg){return ecgDao.insert(ecg);}
    public long saveWatchInfoData(WatchInfoData watchInfoData){return watchInfoDataDao.insert(watchInfoData);}
    public void saveWatchInfoDataList(List<WatchInfoData> watchInfoData){watchInfoDataDao.insertOrReplaceInTx(watchInfoData);}

  //����SleepData��
    public long saveSleepData(SleepData sleepData){ 
        return sleepDao.insert(sleepData); 
    } 
    
  //����HearData��
    public long saveHearData(HearData hearData){
        return hearDao.insert(hearData); 
    }

    public long saveGpsPointData(GpsPointData mGpsPointdata){
        return mGpsPointDao.insert(mGpsPointdata);
    }
    public long saveGpsPointDeatilData(GpsPointDetailData mGpsPointDeatilData){
        return mGpsPointDetailDao.insert(mGpsPointDeatilData);
    }

    public long saveAlarmClockData(AlarmClockData alarmClockData){
        return alarmClockDataDao.insert(alarmClockData);
    }

    //����RunData��
    public void updataRunData(RunData runData){
    	runDao.update(runData);
    }
    public void updataBloodpressureData(Bloodpressure bloodpressure){bloodpressureDao.update(bloodpressure);}

    public void updataEcgData(Ecg ecg){ecgDao.update(ecg);}
    public void updataTemperatureData(Temperature temperature){temperatureDao.update(temperature);}

  //����SleepData��
    public void updataSleepData(SleepData sleepData){
    	sleepDao.update(sleepData);
    }
    
  //����HearData��
    public void updataHearData(HearData HearData){
    	hearDao.update(HearData);
    }

    public void updataGpsPointData(GpsPointData mGpsPointdata){
        mGpsPointDao.update(mGpsPointdata);
    }
    public void updataGpsPointDetailData(GpsPointDetailData mGpsPointDetailData){
        mGpsPointDetailDao.update(mGpsPointDetailData);
    }
    public void updataAlarmClockData(AlarmClockData alarmClockData){
        alarmClockDataDao.update(alarmClockData);
    }
    public void updataWatchInfoData(WatchInfoData watchInfoData){
        watchInfoDataDao.update(watchInfoData);
    }

    //������е�RunData�����Ŵ浽List�б�����
    public List<RunData> loadAllSession() {
        List<RunData> sessions = new ArrayList<RunData>();
        List<RunData> tmpSessions = runDao.loadAll();
        int len = tmpSessions.size();
        for (int i = len-1; i >=0; i--) {
                sessions.add(tmpSessions.get(i));
        }
        return sessions;
    } 
 
    public void DeleteHearData(HearData entity) {
        hearDao.delete(entity);
    } 
    public void DeleteSleepData(SleepData entity) {
        sleepDao.delete(entity);
    } 
 
    public void DeleteRunData(RunData entity) {
        runDao.delete(entity);
    }

    public void DeleteGpsPointData(GpsPointData entity) {
        mGpsPointDao.delete(entity);
    }

    public void DeleteGpsPointData(GpsPointDetailData entity) {
        mGpsPointDetailDao.delete(entity);

    }
    public void DeleteAlarmClockData(AlarmClockData entity) {
        alarmClockDataDao.delete(entity);

    }
    public void DeleteWatchInfoData(WatchInfoData entity) {
        watchInfoDataDao.delete(entity);

    }

    //ɾ��ĳһ��Session
    public void DeleteNoteBySession(RunData entity) {
        QueryBuilder<RunData> mqBuilder = runDao.queryBuilder();
        mqBuilder.where(Properties.Id.eq(entity.getId()));
        List<RunData> chatEntityList = mqBuilder.build().list();
        runDao.deleteInTx(chatEntityList);
    } 
 
 
    //����id�ҵ�ĳһ��
    public RunData loadRunData(long id) {
        return runDao.load(id); 
    } 
  //����id�ҵ�ĳһ��
    public SleepData loadSleepData(long id) {
        return sleepDao.load(id); 
    } 
  //����id�ҵ�ĳһ��
    public HearData loadHearData(long id) {
        return hearDao.load(id); 
    }
    public Temperature loadTemperatureDaoData(long id) {
        return temperatureDao.load(id);
    }
    public Ecg loadEcgDaoData(long id) {
        return ecgDao.load(id);
    }
    public GpsPointData loadGpsPointData(long id) {
        return mGpsPointDao.load(id);
    }
    public GpsPointDetailData loadGpsPointDetailData(long id) {
        return mGpsPointDetailDao.load(id);
    }



//    //������е�RunData�б�
//    public List<RunData> loadAllNote(){ 
//        return runDao.loadAll(); 
//    } 
 
 
    /**
     * query list with where clause
     * ex: begin_date_time >= ? AND end_date_time <= ?
     * @param where where clause, include 'where' word
     * @param params query parameters
     * @return
     */ 
  //原始查询
    public List<RunData> queryNote(String where, String... params){
        ArrayList<RunData> ad = new ArrayList<RunData>();
        return runDao.queryRaw(where, params);
    }
 
 
    public List<RunData> loadLastMsgBySessionid(String sessionid){
        QueryBuilder<RunData> mqBuilder = runDao.queryBuilder();
        mqBuilder.where(Properties.Id.eq(sessionid))
        .orderDesc(Properties.Id)
        .limit(1);
        return mqBuilder.list();
    }
 
 
    public List<RunData> loadMoreMsgById(String sessionid, Long id){
        QueryBuilder<RunData> mqBuilder = runDao.queryBuilder();
        mqBuilder.where(RunDataDao.Properties.Id.lt(id))
        .where(RunDataDao.Properties.Id.eq(sessionid))
        .orderDesc(Properties.Id)
        .build();
//        .limit(20);
        return mqBuilder.list();
    }
 
 
    /**
     * delete all note
     */ 
    public void deleteAllNote(){ 
        runDao.deleteAll(); 
    } 
 
 
    /**
     * delete note by id
     * @param id
     */ 
    public void deleteRunData(long id){ 
        runDao.deleteByKey(id); 
    } 
    
    /**
     * delete note by id
     * @param id
     */ 
    public void deleteHearData(long id){ 
        hearDao.deleteByKey(id); 
    } 
    /**
     * delete note by id
     * @param id
     */ 
    public void deleteSleepData(long id){ 
        sleepDao.deleteByKey(id); 
    }
    /**
     * delete note by id
     * @param id
     */
    public void deleteGpsPointData(long id){
        mGpsPointDao.deleteByKey(id);
    }
    /**
     * delete note by id
     * @param id
     */
    public void deleteGpsPointDetailData(long id){
        mGpsPointDetailDao.deleteByKey(id);
    }

    public void deleteTemperatureDaoData(long id){
        temperatureDao.deleteByKey(id);
    }
    public void deleteEcgDaoData(long id){
        ecgDao.deleteByKey(id);
    }

    public void deleteAlarmClockData(long id){
        alarmClockDataDao.deleteByKey(id);
    }

    public void deleteWatchInfoData(long id){
        watchInfoDataDao.deleteByKey(id);
    }
}
