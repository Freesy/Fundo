package com.szkct.weloopbtsmartdevice.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.kct.fundo.btnotification.R;
import com.szkct.map.utils.Util;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.Log;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kct on 2016/12/12.
 */
public class MotionChartFragment extends Fragment implements View.OnClickListener {  // // 运动图表页面


    private static final int TYPE_SPEED = 0;
    private static final int TYPE_ALTITUDE = 1;
    private static final int TYPE_HEART_RATE = 2;
    private static final int TYPE_CADENCE = 3;

    private View mmotionchartfragment;
    private MotionChartView speedchartview, altitudechartview, heart_ratechartview, cadencechartview;  // 速度曲线，海拔曲线，心率曲线， 步频曲线  --- 根据 GpsPointDetailData  填充 各对应图表

    private float speedmins=0;
    private float speedmaxs=0;
    private float altitudemaxs=0;
    private float altitudemins=0;
    private float cadencemaxs=0;
    private float cadencemins=0;
    private float heartmaxs=0;
    private float heartmins=0;

    private GpsPointDetailData gpsPoint;  // 关键数据

    private List<Float> speedList = new ArrayList<Float>();//
    private List<Float> altitudeList = new ArrayList<Float>();//
    private List<Float> cadencelist = new ArrayList<Float>();//

    private List<Float> heartlist = new ArrayList<Float>();//
    int minute = 1;
    private DBHelper db = null;
    String choicetime ;
    private boolean isMetric;

    public static ScrollView motionchart_sc;

    @Override
    public void onClick(View view) {

    }

    public static MotionChartFragment newInstance() {
        MotionChartFragment fragment = new MotionChartFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mmotionchartfragment = inflater.inflate(R.layout.fragment_motionchart, null);
        gpsPoint = (GpsPointDetailData) getActivity().getIntent().getSerializableExtra("Vo");
       /* if (db == null) {
            db = DBHelper.getInstance(getActivity());
        }*/
        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
        initSpeed();        // 速度曲线数据
        initaltitude();     // 海拔曲线数据
        initheart();        // 心率曲线数据
        initcadence();      // 步频曲线数据
        //getgpspointdata();

        motionchart_sc = (ScrollView) mmotionchartfragment.findViewById(R.id.motionchart_sc);
//        initview();
        return mmotionchartfragment;

    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }

    private void initSpeed() {
        /**得到总时间 总距离配速**/
        String totalPs = gpsPoint.getArrspeed().replace("Infinity","");         // TODO ---- 速度值 GpsPointDetailData
        Log.e("rq","totalPs=="+totalPs);
        choicetime=gpsPoint.getTimeMillis();

//        if(!StringUtils.isEmpty(totalPs) && totalPs.contains("&")){
            String[] arrPs = totalPs.split("&");
            if(arrPs==null||arrPs.length==0){
                return ;
            }
            int psSize = arrPs.length;
            for (int i = 0; i < psSize; i++) {
                Float psValue = Utils.tofloat(arrPs[i]);
                if(!isMetric){
                    psValue = (float)Utils.getUnit_mile(psValue);
                }
                speedList.add(psValue);
            }
            speedmaxs = Utils.tofloat(Collections.max(speedList) + "");//得到集合中最大值
            speedmins = Utils.tofloat(Collections.min(speedList) + "");//得到集合中最小值

            if((speedmaxs == 1 && speedmins == 1)){
                speedmaxs = 2;
                speedmins = 1;
//                speedmins = 0;
            }
//        }

//        String stime = gpsPoint.getsTime();  // 629.0

        double sTime = Double.parseDouble(gpsPoint.getsTime());  // 629.0
        int dss = (int)sTime;  // 629
        minute = (int)Double.parseDouble(gpsPoint.getsTime()) / 60;  // 10
//        minute = Utils.toint(gpsPoint.getsTime()) / 60;
        if (minute < 1) {
            minute = 1;
        }
    }
    private void initcadence() {

        String totalca= gpsPoint.getArrcadence();   // TODO --- 步频值
        Log.e("rq","totalca=="+totalca);
        String[] arrca = totalca.split("&");
        if(arrca==null||arrca.length==0){
            return ;
        }
        int caSize = arrca.length;
        for (int i = 0; i < caSize; i++) {
            Float psValue = Utils.tofloat(arrca[i]);

                cadencelist.add(psValue);

        }
        if(cadencelist.size() == 1){
            cadencemaxs = Utils.tofloat(Collections.max(cadencelist) + ""); //得到集合中最大值
            cadencemins = Utils.tofloat(cadencelist.get(0)/3 + "");   //最小值
        }else {
            cadencemaxs = Utils.tofloat(Collections.max(cadencelist) + "");//得到集合中最大值
            cadencemins = Utils.tofloat(Collections.min(cadencelist) + "");//得到集合中最小值
        }


    }
    private void initheart() {

        String totalht= gpsPoint.getArrheartRate();  // 心率值
        Log.e("rq","totalht=="+totalht);
        String[] arrht = totalht.split("&");
        Log.e("rq",arrht.toString()+"=="+arrht.length);
        if(arrht==null||arrht.length==0){
            return ;
        }
        int caSize = arrht.length;
        for (int i = 0; i < caSize; i++) {
            Float psValue = Utils.tofloat(arrht[i]);
                heartlist.add(psValue);
        }
        if(heartlist.size() == 1){
            heartmaxs = Utils.tofloat(Collections.max(heartlist) + ""); //得到集合中最大值
            heartmins = Utils.tofloat(heartlist.get(0)/3 + "");   //最小值
        }else {
            heartmaxs = Utils.tofloat(Collections.max(heartlist) + "");//得到集合中最大值
            heartmins = Utils.tofloat(Collections.min(heartlist) + "");//得到集合中最小值
        }
    }
    private void initaltitude() {

        String totalal= gpsPoint.getArraltitude();  // 海拔值   地图得到的海拔值 260.15902099774047&256.7814860623313&258.2029664825295&263.0911206371336&237.06402266995303&
        Log.e("rq","totalal=="+totalal);
        String[] arral = totalal.split("&");
        if(arral==null||arral.length==0){
            return ;
        }
        int caSize = arral.length;
        for (int i = 0; i < caSize; i++) {
            Float psValue = Utils.tofloat(arral[i]);
            if(!isMetric){
                psValue = (float)Utils.getUnit_mile(psValue);
            }
                altitudeList.add(psValue);     // TODO --- 海拔数组
        }
        if(altitudeList.size() == 1){
            altitudemaxs = Utils.tofloat(Collections.max(altitudeList) + ""); //得到集合中最大值
            altitudemins = Utils.tofloat(altitudeList.get(0)/3 + "");   //最小值
        }else {
            altitudemaxs = Utils.tofloat(Collections.max(altitudeList) + "");//得到集合中最大值  TODO --- 海拔
            altitudemins = Utils.tofloat(Collections.min(altitudeList) + "");//得到集合中最小值
        }


    }
    private void initview() {

//        motionchart_sc = (ScrollView) mmotionchartfragment.findViewById(R.id.motionchart_sc);

        if(speedList==null||speedList.size()==0){
            mmotionchartfragment.findViewById(R.id.tv_nodata_speed).setVisibility(View.VISIBLE);
            mmotionchartfragment.findViewById(R.id.rl_spped_head).setBackgroundResource(R.drawable.nodata_bg);    // todo  --- 速度图表
            mmotionchartfragment.findViewById(R.id.view_line_speed).setVisibility(View.GONE);
        }
        if(altitudeList==null||altitudeList.size()==0){
            mmotionchartfragment.findViewById(R.id.tv_nodata_altitude).setVisibility(View.VISIBLE);
            mmotionchartfragment.findViewById(R.id.rl_altitude_head).setBackgroundResource(R.drawable.nodata_bg);   // todo  --- 海拔图表
            mmotionchartfragment.findViewById(R.id.view_line_altitude).setVisibility(View.GONE);
        }
        if(heartlist==null||heartlist.size()==0){
            mmotionchartfragment.findViewById(R.id.tv_nodata_heart_rate).setVisibility(View.VISIBLE);

            mmotionchartfragment.findViewById(R.id.rl_heart_rate_head).setBackgroundResource(R.drawable.nodata_bg);      // todo  --- 心率图表
            mmotionchartfragment.findViewById(R.id.view_line_heart_rate).setVisibility(View.GONE);
        }
        if( cadencelist==null|| cadencelist.size()==0){
            mmotionchartfragment.findViewById(R.id.tv_nodata_cadence).setVisibility(View.VISIBLE);
            mmotionchartfragment.findViewById(R.id.rl_cadence_head).setBackgroundResource(R.drawable.nodata_bg);      // todo  --- 步频图表
            mmotionchartfragment.findViewById(R.id.view_line_cadence).setVisibility(View.GONE);
        }

        if(speedList.size() <=0 || ((speedList.size() ==1) )){ // && speedList.get(0) == 0
            mmotionchartfragment.findViewById(R.id.rl_spped_head).setVisibility(View.GONE);    // todo  --- 速度图表
        }else {
            speedchartview = (MotionChartView) mmotionchartfragment.findViewById(R.id.sppedchartview);
            speedchartview.setDataToShow(speedList, Utils.tofloat(Utils.setformat(1, speedmins)), Utils.tofloat(Utils.setformat(1, speedmaxs)), 0, (int)Float.parseFloat(gpsPoint.getsTime()));// H1传的运动时间为 129.0秒
        }

        if(altitudeList.size()<=0 || ((altitudeList.size() ==1))){ //  && altitudeList.get(0) == 0
            mmotionchartfragment.findViewById(R.id.rl_altitude_head).setVisibility(View.GONE);   // todo  --- 海拔图表
        }else {
            altitudechartview = (MotionChartView) mmotionchartfragment.findViewById(R.id.altitudechartview);
            altitudechartview.setDataToShow(altitudeList, Utils.tofloat(Utils.setformat(0, altitudemins)), Utils.tofloat(Utils.setformat(0, altitudemaxs)), 1,(int)Float.parseFloat(gpsPoint.getsTime()));  // todo 设置海拔图表
        }

        if(heartlist.size() <= 0 || ((heartlist.size() ==1) )){ // && heartlist.get(0) == 0
            mmotionchartfragment.findViewById(R.id.rl_heart_rate_head).setVisibility(View.GONE);      // todo  --- 心率图表
        }else {
            heart_ratechartview = (MotionChartView) mmotionchartfragment.findViewById(R.id.heart_ratechartview);
            heart_ratechartview.setDataToShow(heartlist,Utils.tofloat(Utils.setformat(0, heartmins)), Utils.tofloat(Utils.setformat(0, heartmaxs)), 2, (int)Float.parseFloat(gpsPoint.getsTime()));
        }

        if(cadencelist.size()<=0 || ((cadencelist.size() ==1) )){ // && cadencelist.get(0) == 0
            mmotionchartfragment.findViewById(R.id.rl_cadence_head).setVisibility(View.GONE);      // todo  --- 步频图表
        }else {
            cadencechartview = (MotionChartView) mmotionchartfragment.findViewById(R.id.cadencechartview);
            cadencechartview.setDataToShow(cadencelist, Utils.tofloat(Utils.setformat(0,cadencemins)), Utils.tofloat(Utils.setformat(0, cadencemaxs)), 3, (int)Float.parseFloat(gpsPoint.getsTime()));
        }
    }



/*

    private void  getgpspointdata(){
        if (db == null) {
            db = DBHelper.getInstance(getActivity());
        }
        Query query=null;
            query = db.getGpsPointDao().queryBuilder()
             .where(GpsPointDao.Properties.TimeMillis.eq(choicetime))
                   // .orderAsc(GpsPointDao.Properties.ArrAltitude)排序
                    .build()
                    ;
        List list = query.list();
        speedList=new ArrayList<Float>();
        altitudeList=new ArrayList<Float>();
        cadencelist=new ArrayList<Float>();
        if (list != null && list.size() >= 1) {
            for (int j = 0; j < list.size(); j++) {
                GpsPointData gpspointdao = (GpsPointData) list.get(j);
                Log.e("rq",j+"================="+gpspointdao.toString());
                float s=Utils.tofloat(gpspointdao.getArrSpeed());
                float a=Utils.tofloat(gpspointdao.getArrAltitude());
                float c=Utils.tofloat(gpspointdao.getArrBuPing());
                if(j==0){
                    speedmaxs=s;
                    speedmins=s;
                    altitudemaxs=a;
                    altitudemins=a;
                    cadencemaxs=c;
                    cadencemins=c;
                }
                if (speedmaxs < s) {
                    speedmaxs = s;
                }
                if (speedmins > s) {
                    speedmins = s;
                }

                if (altitudemaxs < a) {
                    altitudemaxs =a;
                }
                if (altitudemins > a) {
                    altitudemins = a;
                }

                if (cadencemaxs < c) {
                    cadencemaxs =c;
                }
                if (cadencemins > c) {
                    cadencemins = c;
                }
                speedList.add(s);
                altitudeList.add(a);
                cadencelist.add(c);
            }
        }
    }*/
public void onDestroyView() {
// TODO Auto-generated method stub
    super.onDestroyView();

    try {
        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
        childFragmentManager.setAccessible(true);
        childFragmentManager.set(this, null);


    } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
    }
}
}
