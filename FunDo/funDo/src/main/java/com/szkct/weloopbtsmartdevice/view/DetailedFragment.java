package com.szkct.weloopbtsmartdevice.view;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.mtk.app.applist.FileUtils;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.login.Gdata;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.ImageCacheUtil;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.kct.fundo.btnotification.R.id.detail_zdsyl;
import static com.kct.fundo.btnotification.R.id.traject_peisu;

/**
 * Created by Kct on 2016/12/17.
 */
@SuppressWarnings("ResourceType")
public class DetailedFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private Typeface dintf;
    private float mins, maxs;
    private GpsPointDetailData gpsPoint;   // 关键数据
    private TextView mdetail_date;
    private TextView mtv_showdic, mdetail_zdbf, mdetail_zxbf, mdetail_pjbf;
    private TextView mtv_showtime, mdetail_czsd, mdetail_zdxl, mdetail_zxxl, mdetail_pjxl;
    private TextView mdetail_sudu, mdetail_pjps, mdetail_ljps, mdetail_ljxj;
    private TextView mdetail_peisu, mdetail_xlqd, mdetail_zgps, mdetail_zdps;
    private TextView mdetail_xiaohao, mdetail_bushu, mdetail_zdsyl;
    private TextView mtv_showdic_up,mdetail_sudu_up,mdetail_peisu_up,mdetail_xiaohao_up,mdetail_zgps_up,mdetail_zdps_up
            ,mdetail_pjps_up,mdetail_ljps_up,mdetail_ljxj_up,mdetail_czsd_up,mdetail_zdbf_up, mdetail_zxbf_up, mdetail_pjbf_up;
    private TextView total_length_id,realTime_hms_id,detailed_mileage_id;
    private boolean isMetric;
    private FragmentActivity mContext;
    private SharedPreferences preferences;
    private List<Double> psList = new ArrayList<Double>();//配速集合
    private List<Integer> xlList = new ArrayList<Integer>();//心率集合
    private List<Integer> watchPsList = new ArrayList<Integer>();//手表配速集合
    private List<Integer> buPinglist = new ArrayList<Integer>();//步频集合
    private TextView mdetail_zdbp, mdetail_zxbp, mdetail_pjbp, mdetail_sjsc, mdetail_ztsc, mdetail_ztcs;
    private ImageView detail_icon;
    private String choicetime;
    private DBHelper db = null;
//    private String maxBp = "--";
//    private String minBp = "--";
    private String minBf = "--";
    private String maxBf = "--";
    private TextView detail_name,sportdataerror_tv;
    private String sex;
    private Float cadencemaxs;
    private Float cadencemins;

    private ImageView sportmode_twopage_logo_iv;

    // 下面3个字段都为 手机端数据
    private int maxBp = 0; //最大步频
    private  int minBp; // 最小步频
    private  String pjPs; // 平均配速

    public static ScrollView detailfragment_sc;   //
    @Override
    public void onClick(View view) {

    }

    public static DetailedFragment newInstance() {
        DetailedFragment fragment = new DetailedFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        // TODO
        mView = inflater.inflate(R.layout.fragment_detailed_data, null);
        gpsPoint = (GpsPointDetailData) getActivity().getIntent().getSerializableExtra("Vo");
        choicetime = gpsPoint.getTimeMillis();//时间毫秒数
        if (db == null) {
            db = DBHelper.getInstance(mContext);
        }
        preferences = mContext.getSharedPreferences("userinfo", MODE_MULTI_PROCESS);

//        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES));

        initview();
//        setheadp();
        setUserName();
        return mView;
    }

    private void initview() {

        detailfragment_sc = (ScrollView) mView.findViewById(R.id.detailfragment_sc);

        mdetail_date = (TextView) mView.findViewById(R.id.detail_date);    // 运动结束的最后日期 ： 2017.02.28 11.08
        mtv_showdic = (TextView) mView.findViewById(R.id.tv_showdic);     // 运动的距离   ---- 公里数
        mtv_showtime = (TextView) mView.findViewById(R.id.tv_showtime);    // 运动结束时的时长
        mdetail_sudu = (TextView) mView.findViewById(R.id.detail_sudu);  // 速度
        mdetail_peisu = (TextView) mView.findViewById(R.id.detail_peisu);  // 配速
        mdetail_xiaohao = (TextView) mView.findViewById(R.id.detail_xiaohao); // 消耗卡路里
        mdetail_bushu = (TextView) mView.findViewById(R.id.detail_bushu);  // 步数
        mdetail_zdsyl = (TextView) mView.findViewById(detail_zdsyl);        // 最大摄氧量
        mdetail_xlqd = (TextView) mView.findViewById(R.id.detail_xlqd);     // 训练强度
        mdetail_zgps = (TextView) mView.findViewById(R.id.detail_zgps);     //  最高配速
        mdetail_zdps = (TextView) mView.findViewById(R.id.detail_zdps);     //  最低配速
        mdetail_pjps = (TextView) mView.findViewById(R.id.detail_pjps);     //  平均配速
        mdetail_ljps = (TextView) mView.findViewById(R.id.detail_ljps);     //   累计攀爬
        mdetail_ljxj = (TextView) mView.findViewById(R.id.detail_ljxj);     //   累计下降
        mdetail_czsd = (TextView) mView.findViewById(R.id.detail_czsd);     //   垂直速度
        mdetail_zdxl = (TextView) mView.findViewById(R.id.detail_zdxl);     //   最大心率
        mdetail_zxxl = (TextView) mView.findViewById(R.id.detail_zxxl);     //   最小心率
        mdetail_pjxl = (TextView) mView.findViewById(R.id.detail_pjxl);     //   平均心率
        mdetail_zdbf = (TextView) mView.findViewById(R.id.detail_zdbf);     //   最大步副
        mdetail_zxbf = (TextView) mView.findViewById(R.id.detail_zxbf);       //   最小步副
        mdetail_pjbf = (TextView) mView.findViewById(R.id.detail_pjbf);       //   平均步副
        mdetail_zdbp = (TextView) mView.findViewById(R.id.detail_zdbp);       //   最大频率
        mdetail_zxbp = (TextView) mView.findViewById(R.id.detail_zxbp);      //   最小频率
        mdetail_pjbp = (TextView) mView.findViewById(R.id.detail_pjbp);      //   平均频率
        mdetail_sjsc = (TextView) mView.findViewById(R.id.detail_sjsc);      //   实际时长
        mdetail_ztsc = (TextView) mView.findViewById(R.id.detail_ztsc);      //   暂停时长
        mdetail_ztcs = (TextView) mView.findViewById(R.id.detail_ztcs);      //    暂停次数
        detail_icon = (ImageView) mView.findViewById(R.id.detail_icon);      // 用户头像
        detail_name = (TextView) mView.findViewById(R.id.detail_name);       // 用户名字

        sportdataerror_tv = (TextView) mView.findViewById(R.id.sportdataerror_tv);

        sportmode_twopage_logo_iv = (ImageView) mView.findViewById(R.id.sportmode_twopage_logo_iv);
        if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){  // 白色背景
            sportmode_twopage_logo_iv.setImageResource(R.drawable.sportmode_logo_w);
        }else{
            sportmode_twopage_logo_iv.setImageResource(R.drawable.sportmode_logo_b);
        }


        mtv_showdic_up = (TextView) mView.findViewById(R.id.tv_showdic_up);     //距离单位
        mdetail_sudu_up = (TextView) mView.findViewById(R.id.detail_sudu_up);  // 速度单位
        mdetail_peisu_up = (TextView) mView.findViewById(R.id.detail_peisu_up);  // 配速单位
        mdetail_xiaohao_up = (TextView) mView.findViewById(R.id.detail_xiaohao_up); // 消耗卡路里单位
        mdetail_zgps_up = (TextView) mView.findViewById(R.id.detail_zgps_up);     //  最高配速单位
        mdetail_zdps_up = (TextView) mView.findViewById(R.id.detail_zdps_up);     //  最低配速单位
        mdetail_pjps_up = (TextView) mView.findViewById(R.id.detail_pjps_up);     //  平均配速单位
        mdetail_ljps_up = (TextView) mView.findViewById(R.id.detail_ljps_up);     //   累计攀爬单位
        mdetail_ljxj_up = (TextView) mView.findViewById(R.id.detail_ljxj_up);     //   累计下降单位
        mdetail_czsd_up = (TextView) mView.findViewById(R.id.detail_czsd_up);     //   垂直速度单位
        mdetail_zdbf_up = (TextView) mView.findViewById(R.id.detail_zdbf_up);     //   最大步副单位
        mdetail_zxbf_up = (TextView) mView.findViewById(R.id.detail_zxbf_up);       //   最小步副单位
        mdetail_pjbf_up = (TextView) mView.findViewById(R.id.detail_pjbf_up);       //   平均步副单位

        total_length_id = (TextView) mView.findViewById(R.id.total_length_id);
        realTime_hms_id = (TextView) mView.findViewById(R.id.realTime_hms_id);
        detailed_mileage_id = (TextView) mView.findViewById(R.id.detailed_mileage_id);

        if(Utils.getLanguage().contains("ru") || Utils.getLanguage().contains("it") || Utils.getLanguage().contains("pt") || Utils.getLanguage().contains("de")  || Utils.getLanguage().contains("es")){
            total_length_id.setTextSize(15);
            detailed_mileage_id.setTextSize(15);
            realTime_hms_id.setTextSize(10);
            mtv_showdic_up.setTextSize(10);
        }

        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));

        if(!isMetric) {
            mtv_showdic_up.setText(getActivity().getString(R.string.unit_mi));
            mdetail_sudu_up.setText(getActivity().getString(R.string.unit_mi_hour));
            mdetail_peisu_up.setText(getActivity().getString(R.string.unit_min_mi));
            mdetail_xiaohao_up.setText(getActivity().getString(R.string.unit_kj));
            mdetail_zgps_up.setText(getActivity().getString(R.string.unit_min_mi));
            mdetail_zdps_up.setText(getActivity().getString(R.string.unit_min_mi));
            mdetail_pjps_up.setText(getActivity().getString(R.string.unit_min_mi));
            mdetail_ljps_up.setText(getActivity().getString(R.string.unit_ft));
            mdetail_ljxj_up.setText(getActivity().getString(R.string.unit_ft));
            mdetail_czsd_up.setText(getActivity().getString(R.string.unit_ft));
            mdetail_zdbf_up.setText(getActivity().getString(R.string.unit_in));
            mdetail_zxbf_up.setText(getActivity().getString(R.string.unit_in));
            mdetail_pjbf_up.setText(getActivity().getString(R.string.unit_in));
        }

//        initnamehead();//添加姓名

        /**得到步数**/
        int height2 = Integer.valueOf(Utils.gethight(mContext));//获取用户身高
        int feetweek2 = (int)(height2*0.45);// 走路步长   170*0.45  = 76.5
        double mile22 = (int) gpsPoint.getMile();
        int gps_bushu = (int)(mile22 * 100 / feetweek2);//得到步数 （路程/步长）

        /**计算配速**/
        String arrPs[] = Utils.getPace(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile()));//得到配速 数组  todo ---- 这里面有 公英制转换  ？？？？？？？？？？？？？？？
        String m = arrPs[0];//分
        String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
        double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
        /**计算最大摄氧量**/
        //推测公式为：Vo2max=6.70-2.28 x 性别+0.056 x 时间（s）(健康成人，其中性别：男=1，女=2)
        sex = SharedPreUtil.readPre(mContext, SharedPreUtil.USER,
                SharedPreUtil.SEX);
        int sexInt = 1;
        if ("0".equals(sex) || "".equals(sex)) {
            sexInt = 1;
        } else if ("1".equals(sex)) {
            sexInt = 2;
        }
        Double maxSyl = Utils.decimalTo2(6.70 - 2.28 * sexInt + 0.056 * Double.valueOf(gpsPoint.getsTime()), 2);

        /**根据海拔计算累计上升高度和累计下降**/
        List<String> altitude = new ArrayList<String>();//海拔集合
        /**得到所有的海拔*/
        String mLtitude = gpsPoint.getArraltitude().trim();
        if (!mLtitude.equals("")) {
            String[] arrLat = mLtitude.split("&");
            int latSize = arrLat.length;
            for (int i = 0; i < latSize; i++) {
                altitude.add(arrLat[i]);
            }
        }

        int up = 0;
        int down = 0;
        for (int i = 1; i < altitude.size(); i++) {
            double c = Double.valueOf(altitude.get(i)) - Double.valueOf(altitude.get(i - 1));
            if (c > 0) {//计算海拔累计上升
                up += c;
            } else if (c < 0) {//计算海拔累计下降
                down += c;
            }
        }
        down=Math.abs(down);
        /**计算垂直速度  min/米**/
        int total=up+down;
//        int countTime = Integer.valueOf(gpsPoint.getsTime())/60;    double sTime = Double.parseDouble(gpsPoint.getsTime());
        int countTime = (int)Double.parseDouble(gpsPoint.getsTime())/60;
        if (countTime<1){
            countTime =1;
        }
        int vSpeed=total/countTime;


        Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
        Double time = Double.valueOf(gpsPoint.getsTime()) / 3600;
        Double sudu = 0.00;
        if(isMetric) {
            sudu = Utils.decimalTo2(mile / 1000 / time, 2); //小时每公里   速度值   TODO  ---- 一个值，总距离/总时间
        }else{
            sudu = Utils.decimalTo2(Utils.getUnit_km(mile / 1000)/ time, 2); //小时每公里   速度值   TODO  ---- 一个值，总距离/总时间
        }

        /**得到总时间 总距离配速**/
        String totalPs = gpsPoint.getArrTotalSpeed();
        if (!totalPs.equals("") && !totalPs.equals("0")) {//TODO 手机配速
            Double psSum = 0.00;
            if(totalPs.indexOf("&") !=-1) {
                String[] arrPs2 = totalPs.split("&");
                int psSize = arrPs2.length;
                for (int i = 0; i < psSize; i++) {
                    if(arrPs2[i].contains("'")){
                        String[] ss = arrPs2[i].split("'");
                        String psok = ss[0] + "." + ss[1];
                        Double psValue = Double.valueOf(psok);  //  Double psValue = Double.valueOf(arrPs[i]);
                        if (psValue > 0) {
                            psList.add(psValue);
                            psSum += psValue;
                        }
                    }else {
                        Double psValue = Double.valueOf(arrPs2[i]);
                        if (psValue > 0) {
                            psList.add(psValue);
                            psSum += psValue;
                        }
                    }
                }

                double max = Collections.min(psList);//得到集合中最小值
                double min = Collections.max(psList);//得到集合中最大值

                String maxPs = getPeisu(String.valueOf(max));
                String minPs = getPeisu(String.valueOf(min));
                pjPs = getPeisu(String.valueOf(psSum/psList.size()));  // 应该过滤掉 配速为 0 的
                /**平均配速**/
                mdetail_pjps.setText(pjPs);
                mdetail_zgps.setText(maxPs + "");  // minPs
                mdetail_zdps.setText(minPs + "");
            }
        }else{//TODO  --- 手表配速
            /**得到手表配速 手表只有每公里的配速**/
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")
                    || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")){ //mtk
                String avgSpeed =  gpsPoint.getmCurrentSpeed();//获取平均配速    setArrTotalSpeed
                String arrSpeed =  gpsPoint.getArrTotalSpeed(); // 获取配速数组  ---- 平均配速是否根据配速数组来计算的  ？？？？？？？？
                if(!StringUtils.isEmpty(arrSpeed) || arrSpeed.equals("0")){
                    mdetail_zgps.setText("--");
                    mdetail_zdps.setText("--");
                    mdetail_pjps.setText("--");
                }else {
                    if(arrSpeed.contains("&")){
                        String[] arrWatchPs = arrSpeed.split("&");
                        watchPsList.clear();
                        for (int i = 0; i < arrWatchPs.length; i++) {
                            if (!arrWatchPs[i].equals("")) {
                                watchPsList.add((int)Math.round(Double.parseDouble(arrWatchPs[i])));
                            }
                        }
                        int max = Collections.min(watchPsList);//得到集合中最小值
                        int min = Collections.max(watchPsList);//得到集合中最大值
                        mdetail_zgps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", max / 60, max % 60) + "");  // min，miao
                        mdetail_zdps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", min / 60, min % 60) + "");
                        mdetail_pjps.setText(avgSpeed);
                    }
                }
            }else { // H872等
                String watchPs = gpsPoint.getSpeed();
                if (!watchPs.equals("")) {
                    String[] arrWatchPs = watchPs.split("&");
                    for (int i = 0; i < arrWatchPs.length; i++) {
                        if (!arrWatchPs[i].equals("")) {
                            watchPsList.add((int)Math.round(Double.parseDouble(arrWatchPs[i])));
                        }
                    }
                    int max = Collections.min(watchPsList);//得到集合中最小值
                    int min = Collections.max(watchPsList);//得到集合中最大值

                    int totalWatchPs = 0;
                    for (int j = 0; j < watchPsList.size(); j++) {
                        totalWatchPs += watchPsList.get(j);
                    }
                    int pjPs = totalWatchPs / watchPsList.size();//平均配速
                    /**平均配速**/
                    mdetail_pjps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''",pjPs/60,pjPs%60) + "");
                    mdetail_zgps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", max / 60, max % 60) + "");  // min，miao
                    mdetail_zdps.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", min / 60, min % 60) + "");
                }
            }
        }

        try {
            if (Integer.parseInt(mdetail_zdps.getText().toString().split("'")[0]) > 1000) {
                mdetail_zdps.setTextSize(24);
            }

            if (Integer.parseInt(mdetail_zgps.getText().toString().split("'")[0]) > 1000) {
                mdetail_zgps.setTextSize(24);
            }

            if (Integer.parseInt(mdetail_pjps.getText().toString().split("'")[0]) > 1000) {
                mdetail_pjps.setTextSize(24);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        mdetail_date.setText(Utils.date2De(gpsPoint.getDate()));    // 运动结束的时间
        if(isMetric){
            mtv_showdic.setText(Utils.decimalTo2(mile / 1000, 2) + "");   //  Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
            mdetail_xiaohao.setText(gpsPoint.getCalorie() + "");//千卡

//            viewHolder.item_mile.setText(Utils.decimalTo2(Double.valueOf(gpsData.getMile()) / 1000, 2) + "");
//            viewHolder.tv_danwei.setText(mContext.getString(R.string.kilometer));
        }else{
            mtv_showdic.setText(Utils.decimalTo2(Utils.getUnit_km(mile / 1000), 2) + "");  //  Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
//            mtv_showdic.setText(Utils.decimalTo2((mile / 1000*0.62), 2) + "");  // todo --- 不能用这个算法 会与设备端有0.01的误差
            mdetail_xiaohao.setText(Utils.decimalTo2(Utils.getUnit_kal(Double.parseDouble(gpsPoint.getCalorie())),1)+ "");//千焦

//            viewHolder.item_mile.setText(Utils.decimalTo2(Double.valueOf(gpsData.getMile()) / 1000 * 0.62 , 2) + "");
//            viewHolder.tv_danwei.setText(mContext.getString(R.string.unit_mi));
        }
        //mtv_showdic.setText(Utils.decimalTo2(mile / 1000, 2) + "");
        mtv_showtime.setText(gpsPoint.getSportTime() + "");   // 运动结束是的运动时长
        //mdetail_xiaohao.setText(gpsPoint.getCalorie() + "");//千卡

        if(gpsPoint.getDeviceType().equals("1")){  //   2：手表   1： 手机
            mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int) sec) + "");// 配速  TODO--- 手机端 配速公英制 OK
        }else { // 手表
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
                String avgSpeed =  gpsPoint.getmCurrentSpeed();//获取平均配速    setArrTotalSpeed     TODO ---- 1360
                if(!StringUtils.isEmpty(avgSpeed)){
                   /* if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(BTNotificationApplication.getInstance().getApplicationContext(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))){
                        int avgPeisu = (int)Math.round(Double.parseDouble(avgSpeed));
                        int fen = avgPeisu/60;
                        int miao  = avgPeisu%60 ;
                        mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen , miao) + "");
                    }else {
                        int avgPeisu = (int)Math.round(Double.parseDouble(avgSpeed)/(0.62));
                        int fen = avgPeisu/60;
                        int miao  = avgPeisu%60 ;
                        mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen , miao) + "");    // todo  ----  999999999999999999
                    }*/

                    int avgPeisu = (int)Math.round(Double.parseDouble(avgSpeed));
                    if(!isMetric) {
                        avgPeisu = Utils.getUnit_pace(avgPeisu);
                    }
                    int fen = avgPeisu/60;
                    int miao  = avgPeisu%60 ;
                    mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen , miao) + "");
                }else {
                    mdetail_peisu.setText("--");
                }
            }else {
                if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){  // todo --- 智能机配速

                    double countTimeMiao =  Math.round(Utils.getPaceForWatch1(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile())));//得到配速 数组   1：gpsData.getsTime()：运动时间  2： gpsData.getMile() 运动距离    配速 = 运动时间/距离
                    if(Integer.valueOf((int)(countTimeMiao/60.0)) > 1000){
                        mdetail_peisu.setTextSize(10);   //  --- todo  ---
                    }
                    mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf((int)(countTimeMiao/60.0)), Integer.valueOf((int)(countTimeMiao%60))));    // 设置配速的值
//                    mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), Math.round(sec)));   969999999999999
                }else{

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE).equals("415")){
                        String avgSpeed =  gpsPoint.getmCurrentSpeed();//获取平均配速    setArrTotalSpeed     TODO ---- 1360
                        if(!StringUtils.isEmpty(avgSpeed)){
                            int avgPeisu = (int)Math.round(Double.parseDouble(avgSpeed));
                            if(!isMetric) {
                                avgPeisu = Utils.getUnit_pace(avgPeisu);
                            }
                            int fen = avgPeisu/60;
                            int miao  = avgPeisu%60 ;
                            mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen , miao) + "");
                        }else {
                            mdetail_peisu.setText("--");
                        }
                    }else{
                        mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int) sec) + "");// 配速
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//                    mdetail_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int) sec) + "");// 配速
                }

            }
        }
        //速度
        mdetail_sudu.setText(sudu + "");  //TODO ----  速度值
        if(gpsPoint.getDeviceType().equals("1")) {   //   2：手表   1： 手机
            mdetail_bushu.setText(gps_bushu + "");
        }else{
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")
                    || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")){

                if(gpsPoint.getSportType().equals("11")){ // todo -- 骑行模式无步数
                    mdetail_bushu.setText("--");
                }else {
                    mdetail_bushu.setText(gpsPoint.getStep().equals("0") ? gps_bushu + "" : gpsPoint.getStep());
                }
//                mdetail_bushu.setText(gpsPoint.getStep().equals("0") ? gps_bushu + "" : gpsPoint.getStep());
            }else{
                if((SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) && (gpsPoint.getSportType().equals("11") || gpsPoint.getSportType().equals("20"))){
                    mdetail_bushu.setText("--");  //todo  --- 11 MTK设备骑行模式不需要步数 , 20 骑行返航模式
                }else {
                    String step = gpsPoint.getStep();
                    if(step.equals("0")) {
                        mdetail_bushu.setText(gps_bushu + "");
                    }else{
                        mdetail_bushu.setText(step + "");
                    }
                }

            }
        }

        mdetail_zdsyl.setText(maxSyl + "");//最大摄氧量
        mdetail_xlqd.setText("--" + "");//最大摄氧量
        if (gpsPoint.getSportType().trim().equals("4")) {//登山跑 TODO--- 之前为3 ，现在为 5
            mdetail_ljps.setText(up + "");
            mdetail_ljxj.setText(down + "");
            mdetail_czsd.setText(vSpeed + "");
        } else {
            mdetail_ljps.setText("--" + "");
            mdetail_ljxj.setText("--" + "");
            mdetail_czsd.setText("--" + "");
        }
        String arrHeartRate = gpsPoint.getArrheartRate();

        if (gpsPoint.getDeviceType().equals("1")) {//手机心率
            mdetail_zdxl.setText("--" + "");
            mdetail_zxxl.setText("--" + "");
            mdetail_pjxl.setText("--" + "");
        } else {//手表心率值
            List<Integer> xlList = getHeartRate(arrHeartRate);
            if (xlList.size() > 0) {
                int max = Collections.max(xlList);//得到集合中最大值
                int min = Collections.min(xlList);//得到集合中最小值
                int rate = 0;
                for (int i = 0; i < xlList.size(); i++) {
                    rate += xlList.get(i);
                }
                int pjRate = rate / xlList.size();//得到集合中平均值
                mdetail_zdxl.setText(max + "");
                mdetail_zxxl.setText(min + "");
                mdetail_pjxl.setText(pjRate + "");
                String strength ;
                if (pjRate<=120){
                    strength = "C";
                }else if(120<=pjRate&&pjRate<150){
                    strength = "B";
                }else {
                    strength = "A";
                }
                mdetail_xlqd.setText(strength+ "");   // 训练强度赋值
            } else {
                mdetail_zdxl.setText("--" + "");
                mdetail_zxxl.setText("--" + "");
                mdetail_pjxl.setText("--" + "");
                mdetail_xlqd.setText("--" + "");
            }
        }

        if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //mtk
            if(gpsPoint.getDeviceType().equals("2")){   // 设备类型    2：手表   1： 手机   TODO---- 手表端步幅
                mdetail_zdbf.setText("--" + "");   //手表端步幅需要*100
                mdetail_zxbf.setText("--" + "");
                if(isMetric) {
                    mdetail_pjbf.setText(Float.valueOf(gpsPoint.getAve_step_width()) + "");
                }else{
                    mdetail_pjbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getAve_step_width()))) + "");
                }
            }else {   // TODO---- 手机端步幅
                if(Integer.valueOf(gpsPoint.getMin_step_width()) < 30 ){
                    gpsPoint.setMin_step_width("30");
                }
                if(Integer.valueOf(gpsPoint.getMax_step_width()) > 160 ){
                    gpsPoint.setMax_step_width("160");
                }

                if(!isMetric){
                    gpsPoint.setMin_step_width((int)(Utils.getUnit_cm(Double.parseDouble(gpsPoint.getMin_step_width()))) + "");
                    gpsPoint.setMax_step_width((int)(Utils.getUnit_cm(Double.parseDouble(gpsPoint.getMax_step_width()))) + "");
                    gpsPoint.setAve_step_width((int)(Utils.getUnit_cm(Double.parseDouble(gpsPoint.getAve_step_width()))) + "");
                }
                mdetail_zdbf.setText(gpsPoint.getMax_step_width() + "");
                mdetail_zxbf.setText(gpsPoint.getMin_step_width() + "");
                mdetail_pjbf.setText(gpsPoint.getAve_step_width() + "");
            }
        }else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")
                 || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")){
            mdetail_zdbf.setText(gpsPoint.getMax_step_width().equals("0") ? "30" : gpsPoint.getMax_step_width() + "");
            mdetail_zxbf.setText(gpsPoint.getMin_step_width().equals("0") ? "30" : gpsPoint.getMin_step_width() + "");
            mdetail_pjbf.setText(gpsPoint.getAve_step_width().equals("0") ? "30" : gpsPoint.getAve_step_width() + "");
        }else {
            if(gpsPoint.getDeviceType().equals("2")){   // 设备类型    2：手表   1： 手机   TODO---- 手表端步幅

                if(Float.valueOf(gpsPoint.getMin_step_width()) < 0.30 ){
                    gpsPoint.setMin_step_width("0.30");
                }
                if(Integer.valueOf(gpsPoint.getMax_step_width()) > 1.50 ){
                    gpsPoint.setMax_step_width("1." + "50");
                }
                if(isMetric) {
                    mdetail_zdbf.setText(Float.valueOf(gpsPoint.getMax_step_width()) * 100 + "");   //手表端步幅需要*100
                    mdetail_zxbf.setText(Float.valueOf(gpsPoint.getMin_step_width()) * 100 + "");
                    mdetail_pjbf.setText(Float.valueOf(gpsPoint.getAve_step_width()) * 100 + "");
                }else{
                    mdetail_zdbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getMax_step_width()) * 100)) + "");   //手表端步幅需要*100
                    mdetail_zxbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getMin_step_width()) * 100)) + "");
                    mdetail_pjbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getAve_step_width()) * 100)) + "");
                }
            }else {   // TODO---- 手机端步幅

                if(Integer.valueOf(gpsPoint.getMin_step_width()) < 30 ){
                    gpsPoint.setMin_step_width("30");
                }
                if(Integer.valueOf(gpsPoint.getMax_step_width()) > 160 ){
                    gpsPoint.setMax_step_width("160");
                }
                if(isMetric) {
                    mdetail_zdbf.setText(gpsPoint.getMax_step_width() + "");
                    mdetail_zxbf.setText(gpsPoint.getMin_step_width() + "");
                    mdetail_pjbf.setText(gpsPoint.getAve_step_width() + "");
                }else{
                    mdetail_zdbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getMax_step_width()))) + "");
                    mdetail_zxbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getMin_step_width()))) + "");
                    mdetail_pjbf.setText((int)(Utils.getUnit_cm(Float.valueOf(gpsPoint.getAve_step_width())))+ "");
                }
            }
        }

        if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //mtk

            if(gpsPoint.getDeviceType().equals("2")) {   //todo --- 手表端数据 设备类型    2：手表   1： 手机
                String mcadence = gpsPoint.getArrcadence();  // G703S手表端无 步频数组
                mdetail_zdbp.setText("--" + "");
                mdetail_zxbp.setText("--" + "");
                mdetail_pjbp.setText(mcadence);
            }else { // 手机端步频
                String totalca = gpsPoint.getArrcadence();   //TODO 手机端步频值很大 ，待跟进，可以是调试运动轨迹引起
                buPinglist = getBuPing(totalca);
                if (buPinglist.size() > 0) {
                    maxBp = Collections.max(buPinglist);//得到集合中最大值
                    minBp = Collections.min(buPinglist);//得到集合中最小值
                    int rate = 0;
                    for (int i = 0; i < buPinglist.size(); i++) {
                        rate += buPinglist.get(i);
                    }
                    int pjBuping = rate / buPinglist.size();//得到集合中平均值
                    mdetail_zdbp.setText(maxBp + "");
                    mdetail_zxbp.setText(minBp + "");
                    mdetail_pjbp.setText(pjBuping + "");
                } else {
                    mdetail_zdbp.setText("--" + "");
                    mdetail_zxbp.setText("--" + "");
                    mdetail_pjbp.setText("--" + "");
                }
            }
        }else { // H872
            // todo 得到步频
            String totalca = gpsPoint.getArrcadence();
            buPinglist = getBuPing(totalca);
            if (buPinglist.size() > 0) {
                maxBp = Collections.max(buPinglist);//得到集合中最大值
                minBp = Collections.min(buPinglist);//得到集合中最小值
                int rate = 0;
                for (int i = 0; i < buPinglist.size(); i++) {
                    rate += buPinglist.get(i);
                }
                int pjBuping = rate / buPinglist.size();//得到集合中平均值
                mdetail_zdbp.setText(maxBp + "");
                mdetail_zxbp.setText(minBp + "");
                mdetail_pjbp.setText(pjBuping + "");
            } else {
                mdetail_zdbp.setText("--" + "");
                mdetail_zxbp.setText("--" + "");
                mdetail_pjbp.setText("--" + "");
            }
        }
        mdetail_sjsc.setText(gpsPoint.getSportTime() + "");
        mdetail_ztsc.setText(gpsPoint.getPauseTime() + "");
        int pauseNumber = 0;
        try {
            pauseNumber = Integer.parseInt(gpsPoint.getPauseNumber()) < 0
                    ? 0 : Integer.parseInt(gpsPoint.getPauseNumber());
        }catch (Exception e){
            e.printStackTrace();
        }
        mdetail_ztcs.setText(pauseNumber + "");

        int psInt = 0;
        if(!StringUtils.isEmpty(pjPs) && pjPs.contains("'")){  // %1$02d'%2$02d''
            String[] ps = pjPs.split("'");
            psInt = Integer.valueOf(ps[0]);
        }
        if(maxBp > 200 || psInt <= 3 && psInt> 0){ //最大步频大于200 或 平均配速小于3分钟
            sportdataerror_tv.setVisibility(View.VISIBLE);
//            maxBp = 0;
//            psInt = 0;
        }else {
            sportdataerror_tv.setVisibility(View.GONE);
        }
    }


    /**
     * 获取步频集合
     *
     * @return
     */
    private List<Integer> getBuPing(String cadencelist) {
        if (!cadencelist.equals("")) {//得到步频
            String[] arrRate = cadencelist.split("&");
            int psSize = arrRate.length;
            for (int i = 0; i < psSize; i++) {
                double psValue = (Double.valueOf(arrRate[i]));
                int value = (int)psValue;
                if (value > 0) {
                    buPinglist.add(value);
                }
            }
        }
        return buPinglist;
    }

    /**
     * 获取心率集合
     *
     * @param arrHeartRate
     * @return
     */
    private List<Integer> getHeartRate(String arrHeartRate) {
        if (!arrHeartRate.equals("")) {//得到心率
            String[] arrRate = arrHeartRate.split("&");
            int psSize = arrRate.length;
            for (int i = 0; i < psSize; i++) {
                int psValue = (int)Math.round(Double.parseDouble(arrRate[i]));
                if (psValue > 0) {
                    xlList.add(psValue);
                }
            }
        }
        return xlList;
    }

    void initnamehead() {
        detail_name.setText(SharedPreUtil.readPre(getActivity(),
                SharedPreUtil.USER, SharedPreUtil.NAME));

        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                SharedPreUtil.NAME).equals("")) {

            detail_name.setText(getString(R.string.not_set_info));
        }
        try {
            setheadp();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private String getPeisu(String ps) {
        String arrPs[] = ps.split("\\.");
        String m = arrPs[0];//分
        String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
        double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
        if(!isMetric){
            int second = Utils.getUnit_pace(Integer.parseInt(m) * 60 + (int)sec);
            m = second / 60 + "";
            sec = second % 60;
        }
        String peisu = String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int) sec);
        return peisu;
    }


    private void setUserName() {
        if (Gdata.getMid() != Gdata.NOT_LOGIN) {
            String photoName = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.FACE);
            String path = FileUtils.SDPATH + photoName;
            Log.e("MyDataActivity ", " 显示的图片路径：" + path);
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if(bitmap != null)
                    detail_icon.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));
                Log.e("MyDataActivity ", " 显示本地图片");
            }
            detail_name.setText(Gdata.getPersonData().getUsername());
        }else{
            detail_icon.setImageResource(R.drawable.head_men);
            detail_name.setText(getString(R.string.not_set_info));
        }
    }

    private void setheadp() {
        String photoName = SharedPreUtil.readPre(getActivity(),
                SharedPreUtil.USER, SharedPreUtil.FACE);
        if (photoName == "") {
            TypedArray a = getActivity().obtainStyledAttributes(new int[]{
                    R.attr.im_head_women, R.attr.im_head_men});

            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SEX).equals("1")) {
                detail_icon.setImageDrawable(a.getDrawable(0));
            } else {
                detail_icon.setImageDrawable(a.getDrawable(1));
            }
            a.recycle();
            return;
        }
        String path = FileUtils.SDPATH + photoName;
        Log.e("MyDataActivity ", " 显示的图片路径：" + path);
        File file = new File(path);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            detail_icon.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));
            Log.e("MyDataActivity ", " 显示本地图片");
        }
    }
    public void onDestroyView() {
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

