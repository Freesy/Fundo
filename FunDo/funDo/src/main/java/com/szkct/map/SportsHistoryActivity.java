package com.szkct.map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mtk.app.thirdparty.EXCDController;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.map.adapter.HistoryAdapter;
import com.szkct.map.dialog.AlertDialog;
import com.szkct.weloopbtsmartdevice.activity.SportHistoryActivityForMtkYouyong;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.GpsPointDetailDao;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.CalendarAcitity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.main.SportHistoryActivity;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.Log;
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.CustomProgress;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.Query;

/**
 * 作者：xiaodai.
 * 2016/12/15.
 * 版本：v1.0
 */

public class SportsHistoryActivity extends AppCompatActivity implements View.OnClickListener {  // 运动历史数据条目页面
 private final static String TAG = SportsHistoryActivity.class.getName();
    private ImageView back, history_rili, history_sync;
    HistoryAdapter adapter;
    private ListView listview;
    private SportsHistoryActivity mContext;
    private List<GpsPointDetailData> gpsList = new ArrayList<GpsPointDetailData>();
    private List<GpsPointDetailData> allList = new ArrayList<GpsPointDetailData>();
    private  List<GpsPointDetailData> allListOK = new ArrayList<GpsPointDetailData>();
    private DBHelper db;
    private Double totalMile = 0.0;
    private long totalTime = 0;
    private Double mileMax = 0.0;
    private Double mileMin = 0.0;

    private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM");
    private SimpleDateFormat getDateFormatB = Utils.setSimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat getMMFormat = Utils.setSimpleDateFormat("MM");
    private SharedPreferences datePreferences;
    
    private TextView sport_hint;
    private ProgressDialog dialog;
	private MyLoadingDialog myLoadingDialog;
    private sycnBroadcast stb;
    private BluetoothAdapter mBluetoothAdapter = null;

   // private LoadingDialog loadingDialog;
    public Dialog dialogwyl;

    private  ArrayList<GpsPointDetailData> allListOkLast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }

        setContentView(R.layout.activity_sporthistory);


        dialogwyl= CustomProgress.show(SportsHistoryActivity.this  , getString(R.string.sync_datas), null);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mContext = this;
        if (db == null) {
            db = DBHelper.getInstance(mContext);
        }
        initView();
//        initdata();
        registerBroad();
    }
	
	 private void showWaitDialog(){
        if(dialog==null){
            dialog = ProgressDialog.show(this, null, "正在同步，请稍候...", true, true);
        }else{
            dialog.show();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                hideWaitDialog();  // 10秒后不自动关闭     同步完成的监听没起作用
            }
        }, 50000);//50s自动关闭
    }

    private void hideWaitDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    void initdata() {     // MAC -- 手表端mac  MYMAC --- 手机端mac
        if (db == null) {
            db = DBHelper.getInstance(mContext);
        }
        Query query = null;
        query = db.getGpsPointDetailDao().queryBuilder().whereOr(GpsPointDetailDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)),GpsPointDetailDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MYMAC))).orderDesc(GpsPointDetailDao.Properties.TimeMillis).build();
        gpsList = (List<GpsPointDetailData>) query.list();   // 取得所有的6条数据

        SimpleDateFormat dff = new SimpleDateFormat("yyyy", Locale.ENGLISH);//设置日期格式
        String mdatef = dff.format(new Date());   // 2017
        int mdateYearIf = Integer.valueOf(mdatef);
        List<GpsPointDetailData> allgpsListOk = new ArrayList<GpsPointDetailData>();
        if(gpsList.size() > 0){
            for(GpsPointDetailData mdata:gpsList){
                if( mdata.getDate().length() > 4){
                    String dd = mdata.getDate().substring(0,4);  // 2017
                    int yearI = Integer.valueOf(mdata.getDate().substring(0,4));
                    int ccc = mdateYearIf - yearI;
                    if((mdateYearIf - yearI) < 2){
                        allgpsListOk.add(mdata);
                    }
                }else {
                    allgpsListOk.add(mdata);
                }
            }
        }
        
        String maxtime = "";
        String mintime = "";

        List<GpsPointDetailData> gpsListPx = new ArrayList<GpsPointDetailData>();
        if(allgpsListOk != null && allgpsListOk.size() > 0) { // gpsList  ---- gpsListOK
            long maxTimes = 0;
            for(GpsPointDetailData data:allgpsListOk){
                if(Long.valueOf(data.getTimeMillis()) > maxTimes){
                    maxTimes = Long.valueOf(data.getTimeMillis());
                }
            }

            long minTimes = 0;
            for(GpsPointDetailData data:allgpsListOk){
                if(minTimes == 0 || Long.valueOf(data.getTimeMillis()) < minTimes){
                    minTimes = Long.valueOf(data.getTimeMillis());
                }
            }

            mintime = minTimes*1000 + ""  ;
            maxtime = maxTimes*1000 + "";
//            mintime = Long.parseLong(gpsList.get(0).getTimeMillis())*1000 + ""  ;   // 之前保存时 ，保存的10位  --- 现转为 13位     1490425500000
//            maxtime =  Long.parseLong(gpsList.get(gpsList.size() - 1).getTimeMillis())*1000 + "";   //    之前保存时 ，保存的10位  --- 现转为 13位    1491076380000
        } else {
            sport_hint.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
            return;
        }

        String maxmoth = StringUtils.timestamp2Datemonth(maxtime);   // 2017-04
        String minmoth = StringUtils.timestamp2Datemonth(mintime);   //2017-03

        Log.e("maxmoth", maxmoth + "==" + minmoth);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(getDateFormat.parse(maxmoth).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        allList = new ArrayList<GpsPointDetailData>();  //
        Long bigmonth = calendar.getTimeInMillis();  // 最大的月份    2017/3/1 0:0:0   1488297600000     1490976000000    -----  2017/4/1 0:0:0    1490976000 ---   1490976000000     ----- add 0512 1493568000000
        Long nmonth;
        List<GpsPointDetailData> slist = new ArrayList<GpsPointDetailData>();
        query = db.getGpsPointDetailDao().queryBuilder(). whereOr(GpsPointDetailDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)), GpsPointDetailDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MYMAC))).where(GpsPointDetailDao.Properties.TimeMillis.ge(bigmonth)).orderDesc(GpsPointDetailDao.Properties.TimeMillis).build();
        slist = (List<GpsPointDetailData>) query.list();   // 取两次本地数据    ----- 取大月的本地数据   取到3月份的两条
        ////////////////////////////////////////////////////////////////////////////////////
        List<GpsPointDetailData> gpsListOKBigMonth = new ArrayList<GpsPointDetailData>();
        for(int i=0 ;i<slist.size();i++){   // MAC -- 手表端mac  MYMAC --- 手机端mac
            if(slist.get(i).getMac().equals(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)) || slist.get(i).getMac().equals(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MYMAC))){
                if(maxmoth.equals(StringUtils.timestamp2Datemonth(Long.valueOf(slist.get(i).getTimeMillis()) * 1000 + ""))){
                    gpsListOKBigMonth.add(slist.get(i));
                }
            }
        }
        /////////////////////////////////////////////////////////////////////////////////

        if (gpsListOKBigMonth != null && gpsListOKBigMonth.size() > 0) {    // 4月的数据   slist
            Float lenth = 0f;
            Float time = 0f;
            GpsPointDetailData sss;
            for (int s = 0; s < gpsListOKBigMonth.size(); s++) {
                lenth += Utils.tofloat(((GpsPointDetailData) gpsListOKBigMonth.get(s)).getMile() + "");//单位米   20.7   61.07    ----- 两次的总运动距离    // 402.96002    所有运动条目的总距离
                time += Utils.tofloat(((GpsPointDetailData) gpsListOKBigMonth.get(s)).getsTime().toString());//单位秒    38.0   69.0   ---- 两次的总运动时间  // 344.0      所有运动条目的总时间
            }
            GpsPointDetailData alldata = new GpsPointDetailData();
            alldata.setMile(Double.valueOf(lenth));    // 设置总运动距离
            alldata.setsTime(time + "");                // 设置总运动时间

            String ssshh= getMMFormat.format(calendar.getTimeInMillis());  //MM 取月份  01    04
            alldata.setDate(getMMFormat.format(calendar.getTimeInMillis()));     // 设置数据的日期
            allList.add(alldata);    // 添加4月对应的总数据
            allList.addAll(gpsListOKBigMonth);  // 添加4月对应的各条目数据
        }

        calendar.add(Calendar.MONTH, -1);   // 在当前最大月的基础上 减 1
        nmonth = calendar.getTimeInMillis();  // 2017/2/1 0:0:0   --- 2月   1485878400000   1488297600000   ---- 2017/3/1 0:0:0   1488297600000  ---- 必须要13位

        while (bigmonth > Utils.tolong(mintime)) {   // 大月的日期比最小的时间大时
            List<GpsPointDetailData> dlist = new ArrayList<GpsPointDetailData>();
            query = db.getGpsPointDetailDao().queryBuilder(). whereOr(GpsPointDetailDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)),GpsPointDetailDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MYMAC))).where(GpsPointDetailDao.Properties.TimeMillis.between(nmonth, bigmonth)).orderDesc(GpsPointDetailDao.Properties.TimeMillis).build();
            dlist = (List<GpsPointDetailData>) query.list();  // 从本地获取保存的运动数据    ----- 取到3月的4条记录数据
            /////////////////////////////////////////////////////////////////////////////////
            List<GpsPointDetailData> gpsListOKSmallMonth = new ArrayList<GpsPointDetailData>();
            for(int i=0 ;i<dlist.size();i++){   // MAC -- 手表端mac  MYMAC --- 手机端mac
                if(dlist.get(i).getMac().equals(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)) || dlist.get(i).getMac().equals(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MYMAC))){
                    gpsListOKSmallMonth.add(dlist.get(i));
                }
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////
            if (gpsListOKSmallMonth != null && gpsListOKSmallMonth.size() > 0) {  //  dlist
                Float lenth = 0f;
                Float time = 0f;
                GpsPointDetailData sss;
                for (int s = 0; s < gpsListOKSmallMonth.size(); s++) {
                    lenth += Utils.tofloat(((GpsPointDetailData) gpsListOKSmallMonth.get(s)).getMile() + "");//单位米    --- 2月4条数据的总距离 142.15
                    time += Utils.tofloat(((GpsPointDetailData) gpsListOKSmallMonth.get(s)).getsTime().toString());//单位秒   --- 2月4条数据的总时间 269.0
                }
                GpsPointDetailData alldata = new GpsPointDetailData();
                alldata.setMile(Double.valueOf(lenth));
                alldata.setsTime(time + "");

                String sddds = getMMFormat.format(calendar.getTimeInMillis());   // MM 03
                alldata.setDate(getMMFormat.format(calendar.getTimeInMillis()));
                allList.add(alldata);  // 添加3月对应的总数据
                allList.addAll(gpsListOKSmallMonth); // 添加3月对应的各条目数据
            }
            bigmonth = nmonth;   //  将当前的2月的时间，替换为最大的时间   ----- 依次轮询，到每个月的数据
            calendar.add(Calendar.MONTH, -1);
            nmonth = calendar.getTimeInMillis();  // 1485878400000    ---- 2017/2/1 0:0:0
        }

        if (allList != null && allList.size() > 0) {   // 37 条    ------    2000-00-00 00:00
            SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);//设置日期格式
            String mdate = df.format(new Date());   // 2017
            int mdateYearI = Integer.valueOf(mdate);
            List<GpsPointDetailData> allListOk = new ArrayList<GpsPointDetailData>();
            for(GpsPointDetailData mdata:allList){
                if( mdata.getDate().length() > 4){
                    String dd = mdata.getDate().substring(0,4);  // 2017
                    int yearI = Integer.valueOf(mdata.getDate().substring(0,4));
                    int ccc = mdateYearI - yearI;
                    if((mdateYearI - yearI) < 2){
                        allListOk.add(mdata);
                    }
                }else {
                    allListOk.add(mdata);
                }
            }
            //todo ---- 添加运动模式数据重复的判断  0921
            String mSportDate = "";
//            ArrayList<GpsPointDetailData> allListOkLast = new ArrayList<>();
            allListOkLast = new ArrayList<>();
            for (int j = 0; j < allListOk.size(); j++) {
//                String dd = allListOk.get(j).getsTime();
//                double ddd = allListOk.get(j).getMile();
                if(("0".equals(allListOk.get(j).getsTime()) && allListOk.get(j).getMile() == 0.0) || ("0".equals(allListOk.get(j).getsTime()) && allListOk.get(j).getMile() == 0)){  // todo  --- 运动时间和运动距离为0，不显示
                    continue;
                }

                if(!allListOk.get(j).getDate().equals(mSportDate)){   // 运动的具体时间：2017-09-21 10:30
                    mSportDate = allListOk.get(j).getDate();
                    if(allListOkLast.size() > 0){
                        boolean isExist = false;
                        for(GpsPointDetailData data:allListOkLast){
                            if(data.getDate().equals(allListOk.get(j).getDate()) && data.getCalorie().equals(allListOk.get(j).getCalorie()) && data.getsTime().equals(allListOk.get(j).getsTime())){
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist){
                            if(allListOk.get(j).getId() != null) {
                                if(Integer.valueOf(allListOk.get(j).getSportType()) != 8 && Integer.valueOf(allList.get(j).getSportType()) != 9
                                        && Integer.valueOf(allListOk.get(j).getSportType()) != 10 && Integer.valueOf(allListOk.get(j).getSportType()) != 12
                                        && Integer.valueOf(allListOk.get(j).getSportType()) != 13 && Integer.valueOf(allListOk.get(j).getSportType()) != 14
                                        && Integer.valueOf(allListOk.get(j).getSportType()) != 15 && Integer.valueOf(allListOk.get(j).getSportType()) != 16
                                        && Integer.valueOf(allListOk.get(j).getSportType()) != 17 && Integer.valueOf(allListOk.get(j).getSportType()) != 18
                                        && Integer.valueOf(allListOk.get(j).getSportType()) != 19) {
                                    if (allListOk.get(j).getMile() > mileMax) {
                                        mileMax = allListOk.get(j).getMile();
                                    }else{
                                        mileMin = allListOk.get(j).getMile();
                                    }
                                    if (allListOk.get(j).getMile() < mileMin) {
                                        mileMin = allListOk.get(j).getMile();
                                    }
                                }
                            }
                            allListOkLast.add(allListOk.get(j));
                        }
                    }else {
                        if(allListOk.get(j).getId() != null) {
                            if(Integer.valueOf(allListOk.get(j).getSportType()) != 8 && Integer.valueOf(allListOk.get(j).getSportType()) != 9
                                    && Integer.valueOf(allListOk.get(j).getSportType()) != 10 && Integer.valueOf(allListOk.get(j).getSportType()) != 12
                                    && Integer.valueOf(allListOk.get(j).getSportType()) != 13 && Integer.valueOf(allListOk.get(j).getSportType()) != 14
                                    && Integer.valueOf(allListOk.get(j).getSportType()) != 15 && Integer.valueOf(allListOk.get(j).getSportType()) != 16
                                    && Integer.valueOf(allListOk.get(j).getSportType()) != 17 && Integer.valueOf(allListOk.get(j).getSportType()) != 18
                                    && Integer.valueOf(allListOk.get(j).getSportType()) != 19) {
                                mileMax = allListOk.get(j).getMile();
                                mileMin = allListOk.get(j).getMile();
                            }
                        }
                        allListOkLast.add(allListOk.get(j));
                    }
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            listview.setVisibility(View.VISIBLE);       // listView的上拉刷新，下拉加载
            sport_hint.setVisibility(View.GONE);
            listview.setOnItemClickListener(new MyItemOnclick());
//            adapter = new HistoryAdapter(mContext, allList);   // 设置历史数据的适配器
            adapter = new HistoryAdapter(mContext, allListOkLast,mileMax,mileMin);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        } else {
            listview.setVisibility(View.GONE);
            sport_hint.setVisibility(View.VISIBLE);
            // TODO ---- 当没有本地数据时，从后台获取 运动历史数据  （不会出现这种情况，运动数据上传后台，其他的地方可以用到后台的数据）
        }
    }
	
	class MyLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            if(null != allListOkLast && allListOkLast.size()>0) {  // allList  allListOkLast
                if (allListOkLast.get(position).getId() == null) {
                    return true;
                }
            }

            Log.e(TAG, "item长按效果");
            new AlertDialog(mContext).builder().setTitle(getString(R.string.prompt))
                    .setMsg(getString(R.string.delete_history_data))
                    .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            long sid = allList.get(position).getId();
                            long sid = allListOkLast.get(position).getId();
                            Query<GpsPointDetailData> build = db.getGpsPointDetailDao().queryBuilder().where(GpsPointDetailDao.Properties.Id.eq(sid)).build();
                            db.deleteGpsPointDetailData(sid);
                            adapter.removeItem(position);
                        }
                    }).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
            return true;
        }
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        history_rili = (ImageView) findViewById(R.id.history_rili);
        history_sync = (ImageView) findViewById(R.id.history_sync);
        listview = (ListView) findViewById(R.id.listview);
        sport_hint = (TextView) findViewById(R.id.sport_hint);
        listview.setOnItemLongClickListener(new MyLongClickListener());  // 设置listView的 长按 事件
        back.setOnClickListener(this);
        history_rili.setOnClickListener(this);
        history_sync.setOnClickListener(this);

        syncDatas();
    }

    //    短时间重复点击
    private long mLastClickTime = 0L;
    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = 0;
        slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 1000) {
            return true;
        }
        return false;
    }

    private void syncDatas(){
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            if (MainService.getInstance().getState() == 3) {
                if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {  //mtk

                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));
                    if(("K3").equals(device.getName()) || ("PH-F3").equals(device.getName()) || ("PH-F3_LE").equals(device.getName()) || ("PH-W5A").equals(device.getName())
                            || ("THCHWATCH M3").equals(device.getName()) || ("K3_LE").equals(device.getName()) || ("MTS036").equals(device.getName())) {  // todo --- K3设备没有运动模式
                        Toast.makeText(this,getString(R.string.device_not_support),Toast.LENGTH_SHORT).show();
                        initdata();
                        return;
                    }


                    //todo --- 快速点击的处理
                    if(EXCDController.isReceiveSyncData){
                        BluetoothMtkChat.getInstance().syncSportIndex();  // mtk同步运动模式数据
                        EXCDController.isReceiveSyncData = false;
                        showLoadingDialogNew();  //同步 运动模式数据
                    }
                }else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){ //72
                    if(MainService.SPORT) {
                        String modeTimeData = SharedPreUtil.readPre(this, SharedPreUtil.SPORT_BT, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC));

                        String lastSportTime = "";
                        byte[] sleepBTBytes = new byte[6];
                        if (!StringUtils.isEmpty(modeTimeData)) {
                            String lastGetSportData = StringUtils.timestamp2Date(modeTimeData);   //由时间戳格式转为日期格式  2017-03-16 19:00:00     2017-04-12 19:00:00

                            int sportYear = Integer.valueOf(lastGetSportData.substring(2, 4));  // 11
                            int sportMonth = Integer.valueOf(lastGetSportData.substring(5, 7));  // 3
                            int sportRi = Integer.valueOf(lastGetSportData.substring(8, 10));  // 10
                            int sportShi = Integer.valueOf(lastGetSportData.substring(11, 13));
                            int sportFen = Integer.valueOf(lastGetSportData.substring(14, 16));
                            int sportSecond = Integer.valueOf(lastGetSportData.substring(17, 19));

                            sleepBTBytes[0] = (byte) sportYear;
                            sleepBTBytes[1] = (byte) sportMonth;
                            sleepBTBytes[2] = (byte) sportRi;
                            sleepBTBytes[3] = (byte) sportShi;
                            sleepBTBytes[4] = (byte) sportFen;
                            sleepBTBytes[5] = (byte) sportSecond;
                        }

                        if (StringUtils.isEmpty(modeTimeData)) {
                            //TODO --   点击刷新按钮时，也发送 运动请求 命令
                            byte[] l2 = new L2Bean().L2Pack(BleContants.RUN_MODE_COMMADN, BleContants.RUN_REQUEST, sleepBTBytes);  // 运动模式 命令 --- 请求计步  07 70
                            MainService.getInstance().writeToDevice(l2, true);
                        } else {
                            byte[] l2 = new L2Bean().L2Pack(BleContants.RUN_MODE_COMMADN, BleContants.RUN_REQUEST, sleepBTBytes);  // 运动模式 命令 --- 请求计步  07 70
                            MainService.getInstance().writeToDevice(l2, true);
                        }
                        showLoadingDialogNew();  //同步 运动模式数据
                    }else{
                        initdata();
                        dismissLoadingDialog();
                        Toast.makeText(getApplicationContext(), getString(R.string.data_refresh_success), Toast.LENGTH_LONG).show();  // 数据刷新成功
                    }

                }else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")){   //ble
                    //Toast.makeText(getApplicationContext(),getString(R.string.watch_not_support),Toast.LENGTH_SHORT).show();

//                        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST,new byte[]{4});  //TODO 运动模式 命令 --- 0x0A 0xA0
//                        MainService.getInstance().writeToDevice(l2, true);

                    String modeTimeData = SharedPreUtil.readPre(this, SharedPreUtil.SPORT_BT, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC));
                    String lastSportTime = "";
                    byte[] sleepBTBytes = new byte[7];

                    Date date = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);//设置日期格式
                    String format = df.format(date);
                    int sportYear = Integer.valueOf(format.substring(2, 4));  // 11
                    int sportMonth = Integer.valueOf(format.substring(5, 7));  // 3
                    int sportRi = Integer.valueOf(format.substring(8, 10));  // 10
                    int sportShi = Integer.valueOf(format.substring(11, 13));
                    int sportFen = Integer.valueOf(format.substring(14, 16));
                    int sportSecond = Integer.valueOf(format.substring(17, 19));
                    sleepBTBytes[0] = (byte) 4;
                    sleepBTBytes[1] = (byte) sportYear;
                    sleepBTBytes[2] = (byte) sportMonth;
                    sleepBTBytes[3] = (byte) sportRi;
                    sleepBTBytes[4] = (byte) sportShi;
                    sleepBTBytes[5] = (byte) sportFen;
                    sleepBTBytes[6] = (byte) sportSecond;

                    String uuid = (String) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.UUID, "");
                    if(uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())){
                        byte[] l2 = new L2Bean().L2Pack(BleContants.RUN_MODE_COMMADN, BleContants.RUN_REQUEST, sleepBTBytes);
                        MainService.getInstance().writeToDevice(l2, false);
                    }else {
                        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, sleepBTBytes);
                        MainService.getInstance().writeToDevice(l2, true);
                    }
                    showLoadingDialogNew();  //同步 运动模式数据    sync_datas   同步数据中
                }

                String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);
                SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.TEMP_WATCH,tempWatchType);   //TODO --- 连接时保存一份临时TEMP_WATCH
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ble_not_connected), Toast.LENGTH_LONG).show();

                initdata();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.history_rili: {
                Intent mIntent = new Intent(mContext, CalendarAcitity.class);
                startActivity(mIntent);
            }
            break;

            case R.id.history_sync: {
                if (isFastDoubleClick()) {  //todo ----   不能短时间重复点击
                    return;
                }
                syncDatas();
            }
            break;

            default:
                break;
        }

    }
    android.os.Handler handler=new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                   SportsHistoryActivity.this.onResume();
                    break;
                default:
                    break;
            }
        }
    };
    public class MyItemOnclick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if(isFastDoubleClick()){
                return;
            }

            if(null != allListOkLast && allListOkLast.size()>0) {
                if (allListOkLast.get(i).getId() == null) {
                    return;
                }
            }

            View viewTemp = view.findViewById(R.id.iv_rightarrow);
            if(viewTemp.getVisibility() != View.VISIBLE){
                return;
            }

//            if(null != allListOkLast && allListOkLast.size()>0){
//                if (allListOkLast.get(i).getId() == null) {
//                    return;
//                }
//
//                try{
//                    if (MainService.getInstance().getState() == 3) { // TODO -- 设备连接时
//                        if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {  //ble  todo --- X2
//                            if(!allListOkLast.get(i).getDeviceType().equals("1")){     //todo ---  // 设备类型    2：手表   1： 手机
//                                return;    // TODO -- X2设备的运动模式，不需要点击进去 看详情
//                            }
//                        } else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")){   //mtk
//                            String deviceType = allListOkLast.get(i).getDeviceType();    //设备类型  1：手机  2：手表
//                            String sportType = allListOkLast.get(i).getSportType();     //运动模式类型  1:健走  2：跑步   11：骑行    4:登山     setmCurrentSpeed  setSpeed
//                            String speed = allListOkLast.get(i).getSpeed();
//                            String curspeed = allListOkLast.get(i).getmCurrentSpeed();
//
//                            String arrLat = allListOkLast.get(i).getArrLat();  //纬度数组
//                            String arrLng = allListOkLast.get(i).getArrLng();  //经度数组
//
//                            if(deviceType.equals("2") && sportType.equals("4")){ // 登山
//                                if(!StringUtils.isEmpty(speed) && speed.equals("0.000") && !StringUtils.isEmpty(curspeed) && curspeed.equals("0")){
//                                    return;
//                                }else if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NJY-L3_LE")
//                                        || SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NB1_LE")){
//                                    return;      //MTK 2502的运动模式不让点击进去
//                                }
//                            }else if(deviceType.equals("2") && !sportType.equals("1") && !sportType.equals("2") && !sportType.equals("11")) {   // 13
//                                return;      //MTK 除了健走，跑步，骑行都不需要点击进去
//                            }else if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NJY-L3_LE")
//                                    || SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NB1_LE")){
//                                return;      //MTK 2502的运动模式不让点击进去
//                            }
//                        }
//                    }else {    // TODO -- 设备未连接时
//                        String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH);
//                        if(!StringUtils.isEmpty(tempWatchType)){
//                            if(tempWatchType.equals("2")) {  // X2手环端运动模式数据
//                                if(!allListOkLast.get(i).getDeviceType().equals("1")){     //todo ---  // 设备类型    2：手表   1： 手机
//                                    return;    // TODO -- X2设备的运动模式，不需要点击进去 看详情
//                                }
//                            }else if(tempWatchType.equals("3")){
//                                String deviceType = allListOkLast.get(i).getDeviceType();    //设备类型  1：手机  2：手表
//                                String sportType = allListOkLast.get(i).getSportType();     //运动模式类型  1:健走  2：跑步   11：骑行
//                                String speed = allListOkLast.get(i).getSpeed();  // 0.000
//                                String curspeed = allListOkLast.get(i).getmCurrentSpeed(); // 0
//
//                                String arrLat = allListOkLast.get(i).getArrLat();  //纬度数组
//                                String arrLng = allListOkLast.get(i).getArrLng();  //经度数组
//
//                                if(deviceType.equals("2") && sportType.equals("4")){
//                                    if(!StringUtils.isEmpty(speed) && speed.equals("0.000") && !StringUtils.isEmpty(curspeed) && curspeed.equals("0")){
//                                        return;
//                                    }else if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NJY-L3_LE")
//                                            || SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NB1_LE")){
//                                        return;      //MTK 2502的运动模式不让点击进去
//                                    }
//                                }else if(deviceType.equals("2") && !sportType.equals("1") && !sportType.equals("2") && !sportType.equals("11")){   // 13
//                                    return;      //MTK 除了健走，跑步，骑行都不需要点击进去
//                                }else if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NJY-L3_LE")
//                                        || SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NB1_LE")){
//                                    return;      //MTK 2502的运动模式不让点击进去
//                                }
//                            }
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

//            if((SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") && gpsData.getDeviceType().equals("2")) ||
//                    (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3") && gpsData.getDeviceType().equals("2")) ) { //mtk
//                if (gpsData.getDeviceType().equals("2")) {   // 设备类型    2：手表   1： 手机   TODO---- 手表端

            if(allListOkLast.get(i).getSportType().equals("18") && SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") &&
                    SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){   //TODO ---  MTK游泳模式
                Intent intent = new Intent();
                intent.putExtra("Vo", allListOkLast.get(i));   // 传递 运动数据（GpsPointDetailData） 给 后面打开的页面
                intent.setClass(mContext, SportHistoryActivityForMtkYouyong.class);   //TODO  MTK 游泳模式 数据
                startActivity(intent);
            }else{
                Intent intent = new Intent();
                intent.putExtra("Vo", allListOkLast.get(i));   // 传递 运动数据（GpsPointDetailData） 给 后面打开的页面
                intent.setClass(mContext, SportHistoryActivity.class);   //TODO 点击运动历史条目数据时，进入每一条目的 运动数据详情页面
                startActivity(intent);
            }

//                Intent intent = new Intent();
//                intent.putExtra("Vo", allListOkLast.get(i));   // 传递 运动数据（GpsPointDetailData） 给 后面打开的页面
//                intent.setClass(mContext, SportHistoryActivity.class);   //TODO 点击运动历史条目数据时，进入每一条目的 运动数据详情页面
//                startActivity(intent);
//            }
        }
    }

    public void onResume() {
        super.onResume();
        // 接受日期选着Activity 穿过来的数据
        dealDateShow();
        // progressBarShowDate(); // 下拉刷新时同步显示运动跟睡眠数据到界面
    }

    private void dealDateShow() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        datePreferences = getSharedPreferences("datepreferences", Context.MODE_PRIVATE);  // todo --- ???  MODE_PRIVATE
        final int select_day = datePreferences.getInt("select_day", 0);
        final int select_month = datePreferences.getInt("select_month", 0);
        final int select_year = datePreferences.getInt("select_year", 0);

        if ((select_day != 0) && (select_month != 0) && (select_year != 0)) {
            Calendar calendar = Calendar.getInstance();
            try {
                if (select_month < 10) {
                    calendar.setTimeInMillis(getDateFormat.parse(select_year + "-0" + select_month).getTime());
                } else {
                    calendar.setTimeInMillis(getDateFormat.parse(select_year + "-" + select_month).getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long minmonth = calendar.getTimeInMillis();
            calendar.add(Calendar.MONTH, +1);
            Long bigmonth = calendar.getTimeInMillis();

            SharedPreferences.Editor editor = datePreferences.edit();
            editor.remove("select_day");
            editor.remove("select_month");
            editor.remove("select_year");
            editor.commit();

            if (db == null) {
                db = DBHelper.getInstance(mContext);
            }
            Query query = null;
            allList = new ArrayList<GpsPointDetailData>();
            List<GpsPointDetailData> slist = new ArrayList<GpsPointDetailData>();
            query = db.getGpsPointDetailDao().queryBuilder()
                    .where(GpsPointDetailDao.Properties.TimeMillis.between(minmonth, bigmonth)).orderDesc(GpsPointDetailDao.Properties.TimeMillis)
                    .build();
            slist = (List<GpsPointDetailData>) query.list();
            /////////////////////////////////////////////////////////////////////////////////
            List<GpsPointDetailData> gpsListOKData = new ArrayList<GpsPointDetailData>();
            for(int i=0 ;i<slist.size();i++){   // MAC -- 手表端mac  MYMAC --- 手机端mac
                if(slist.get(i).getMac().equals(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)) || slist.get(i).getMac().equals(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MYMAC))){
                    gpsListOKData.add(slist.get(i));
                }
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////
            if (gpsListOKData != null && gpsListOKData.size() > 0) {  // slist
                listview.setVisibility(View.VISIBLE);
                sport_hint.setVisibility(View.GONE);
                Float lenth = 0f;
                Float time = 0f;
                GpsPointDetailData sss;
                for (int s = 0; s < gpsListOKData.size(); s++) {
                    lenth += Utils.tofloat(((GpsPointDetailData) gpsListOKData.get(s)).getMile() + "");//单位米
                    time += Utils.tofloat(((GpsPointDetailData) gpsListOKData.get(s)).getsTime().toString());//单位秒
                }
                GpsPointDetailData alldata = new GpsPointDetailData();
                alldata.setMile(Double.valueOf(lenth));
                alldata.setsTime(time + "");
                calendar.add(Calendar.MONTH, -1);
                alldata.setDate(getMMFormat.format(calendar.getTimeInMillis()));
                allList.add(alldata);
                allList.addAll(gpsListOKData);
            } else {
                listview.setVisibility(View.GONE);
                sport_hint.setVisibility(View.VISIBLE);
            }

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String mSportDate = "";
            allListOkLast = new ArrayList<>();
            for (int j = 0; j < allList.size(); j++) {
                if(!allList.get(j).getDate().equals(mSportDate)){   // 运动的具体时间：2017-09-21 10:30
                    mSportDate = allList.get(j).getDate();
                    if(allListOkLast.size() > 0){
                        boolean isExist = false;
                        for(GpsPointDetailData data:allListOkLast){
                            if(data.getDate().equals(allList.get(j).getDate()) && data.getCalorie().equals(allList.get(j).getCalorie()) && data.getsTime().equals(allList.get(j).getsTime())){
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist){
                            if(allList.get(j).getId() != null) {
                                if(Integer.valueOf(allList.get(j).getSportType()) != 8 && Integer.valueOf(allList.get(j).getSportType()) != 9
                                        && Integer.valueOf(allList.get(j).getSportType()) != 10 && Integer.valueOf(allList.get(j).getSportType()) != 12
                                        && Integer.valueOf(allList.get(j).getSportType()) != 13 && Integer.valueOf(allList.get(j).getSportType()) != 14
                                        && Integer.valueOf(allList.get(j).getSportType()) != 15 && Integer.valueOf(allList.get(j).getSportType()) != 16
                                        && Integer.valueOf(allList.get(j).getSportType()) != 17 && Integer.valueOf(allList.get(j).getSportType()) != 18
                                        && Integer.valueOf(allList.get(j).getSportType()) != 19){
                                    if (allList.get(j).getMile() > mileMax) {
                                        mileMax = allList.get(j).getMile();
                                    }else{
                                        mileMin = allList.get(j).getMile();
                                    }
                                    if (allList.get(j).getMile() < mileMin) {
                                        mileMin = allList.get(j).getMile();
                                    }
                                }

                            }
                            allListOkLast.add(allList.get(j));
                        }
                    }else {
                        if(allList.get(j).getId() != null) {
                            if(allList.get(j).getId() != null) {
                                if(Integer.valueOf(allList.get(j).getSportType()) != 8 && Integer.valueOf(allList.get(j).getSportType()) != 9
                                        && Integer.valueOf(allList.get(j).getSportType()) != 10 && Integer.valueOf(allList.get(j).getSportType()) != 12
                                        && Integer.valueOf(allList.get(j).getSportType()) != 13 && Integer.valueOf(allList.get(j).getSportType()) != 14
                                        && Integer.valueOf(allList.get(j).getSportType()) != 15 && Integer.valueOf(allList.get(j).getSportType()) != 16
                                        && Integer.valueOf(allList.get(j).getSportType()) != 17 && Integer.valueOf(allList.get(j).getSportType()) != 18
                                        && Integer.valueOf(allList.get(j).getSportType()) != 19) {
                                    mileMax = allList.get(j).getMile();
                                    mileMin = allList.get(j).getMile();
                                }
                            }
                        }
                        allListOkLast.add(allList.get(j));
                    }
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            listview.setOnItemClickListener(new MyItemOnclick());
//            adapter = new HistoryAdapter(mContext, allList);
            adapter = new HistoryAdapter(mContext, allListOkLast,mileMax,mileMin);
//            allListOkLast = (ArrayList<GpsPointDetailData>) allList;
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void registerBroad() {
        stb = new sycnBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainService.ACTION_SYNFINSH_SPORTS);
        registerReceiver(stb, filter);
    }

    @Override
    protected void onDestroy() {
        Log.e("", "onDestroy");
        if (stb != null) {
            unregisterReceiver(stb);
        }
        super.onDestroy();
    }

    class sycnBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (MainService.ACTION_SYNFINSH_SPORTS.equals(action)) {  // 同步 运动模式数据
                initdata();
                dismissLoadingDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.data_refresh_success), Toast.LENGTH_LONG).show();  // 数据刷新成功
            }
        }
    }

    private void showLoadingDialogNew() {
        if(null != dialogwyl&&dialogwyl.isShowing()){
            dialogwyl.dismiss();
        }else{
            dialogwyl.show();
        }
    }

    private void dismissLoadingDialog() {
        if (null != dialogwyl) {
            dialogwyl.dismiss();
        }
    }
}
