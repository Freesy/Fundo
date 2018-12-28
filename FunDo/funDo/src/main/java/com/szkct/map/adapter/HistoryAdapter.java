package com.szkct.map.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.szkct.weloopbtsmartdevice.util.Utils.dateInversion;

public class HistoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<GpsPointDetailData> dataList = new ArrayList<GpsPointDetailData>();
    private LayoutInflater inflater;
    private double maxSportData, minSportData;

    //TODO 以下为 手机端 运动模式 对应的图片和文字   /**获取运动类型  0.健走 1.户外跑 2.登山跑 3.越野跑 4.室内跑 5.半马 6.全马**/   ----- 0.健走 1.户外跑 2.室内跑 3.登山跑 4.越野跑 5.半马 6.全马
   /* private static final int[] SECTION_DRAWABLE = {R.drawable.sportshistory_jianzou,
                                                            R.drawable.sportshistory_huwaipao,
                                                            R.drawable.sportshistory_dengshan,
                                                            R.drawable.sportshistory_yueyepao,
                                                            R.drawable.sportshistory_shineipao,
                                                            R.drawable.sportshistory_banma,
                                                            R.drawable.sportshistory_quanma,};

    private static final int[] SECTION_STRINGS = {R.string.sportshistory_jianzou,
                                                        R.string.sportshistory_huwaipao,
                                                        R.string.sportshistory_dengshan,
                                                        R.string.sportshistory_yueyepao,
                                                        R.string.sportshistory_shineipao,
                                                        R.string.sportshistory_banma,
                                                        R.string.sportshistory_quanma,};

    */
    /** 获取运动类型  0.健走 1.户外跑 2.登山跑 3.越野跑 4.室内跑 5.半马 6.全马 **//*
    private static final int[] SECTION_DRAWABLE_WHITE = {R.drawable.sportshistory_jianzou_white,
	                                                            R.drawable.sportshistory_huwaipao_white,
	                                                            R.drawable.sportshistory_dengshan_white,
                                                                R.drawable.sportshistory_yueyepao_white,
                                                                R.drawable.sportshistory_shineipao_white,
                                                                R.drawable.sportshistory_banma_white,
                                                                R.drawable.sportshistory_quanma_white,};*/

    //TODO --- mtk(G703)  mode=运动模式，0：走路模式，1：跑步模式，2：骑行模式，3：登山模式，4：室内跑步模式，5：越野跑模式  --- 目前只用了   0：走路模式，1：跑步模式，2：骑行模式

    //TODO 以下为 手表端 运动模式 对应的图片和文字     /**获取运动类型  0.健走 1.户外跑 2.室内跑 3.登山跑 4.越野跑 5.半马 6.全马**/
    private static final int[] SECTION_DRAWABLE_WATCH = {R.drawable.sportshistory_jianzou,
            R.drawable.sportshistory_huwaipao,
            R.drawable.sportshistory_shineipao,
            R.drawable.sportshistory_dengshan,
            R.drawable.sportshistory_yueyepao,
            R.drawable.sportshistory_banma,
            R.drawable.sportshistory_quanma,
            R.drawable.sportshistory_tiaoshen,
            R.drawable.sportshistory_yumaoqiu,
            R.drawable.sportshistory_lanqiu,
            R.drawable.sportshistory_qixing,
            R.drawable.sportshistory_huabing,
            R.drawable.sportshistory_jiansheng,
            R.drawable.sportshistory_yujia,
            R.drawable.sportshistory_wangqiu,
            R.drawable.sportshistory_pingpang,
            R.drawable.sportshistory_zuqiu,
            R.drawable.sportshistory_youyong,
            R.drawable.sportshistory_xingai,};

    private static final int[] SECTION_DRAWABLE_WATCH_MTK = {R.drawable.sportshistory_jianzou,   // mtk 运动模式图片（黑色背景）
            R.drawable.sportshistory_huwaipao,
            R.drawable.sportshistory_shineipao,
            R.drawable.sportshistory_dengshan,
            R.drawable.sportshistory_yueyepao,
            R.drawable.sportshistory_banma,
            R.drawable.sportshistory_quanma,
            R.drawable.sportshistory_tiaoshen,
            R.drawable.sportshistory_yumaoqiu,
            R.drawable.sportshistory_lanqiu,
            R.drawable.sportshistory_qixing,
            R.drawable.sportshistory_huabing,
            R.drawable.sportshistory_jiansheng,
            R.drawable.sportshistory_yujia,
            R.drawable.sportshistory_wangqiu,
            R.drawable.sportshistory_pingpang,
            R.drawable.sportshistory_zuqiu,
            R.drawable.sportshistory_youyong,
            R.drawable.sportshistory_xingai,
            R.drawable.fanhang,   // todo  --- 暂用 替换
    };

    private static final int[] SECTION_STRINGS_WATCH = {R.string.sportshistory_jianzou,
            R.string.sportshistory_huwaipao,
            R.string.sportshistory_shineipao,
            R.string.sportshistory_dengshan,
            R.string.sportshistory_yueyepao,
            R.string.sportshistory_banma,
            R.string.sportshistory_quanma,
            R.string.sportshistory_tiaosheng,
            R.string.sportshistory_yumaoqiu,
            R.string.sportshistory_lanqiu,
            R.string.sportshistory_qixing,
            R.string.sportshistory_huabing,
            R.string.sportshistory_jianshen,
            R.string.sportshistory_yujia,
            R.string.sportshistory_wangqiu,
            R.string.sportshistory_pingpang,
            R.string.sportshistory_zuqiu,
            R.string.sportshistory_youyong,
            R.string.sportshistory_xingai,};

    private static final int[] SECTION_STRINGS_WATCH_MTK = {R.string.sportshistory_jianzou,     // mtk 运动模式文字描述      0：走路模式，1：跑步模式，2：骑行模式
            R.string.sportshistory_huwaipao,
            R.string.sportshistory_shineipao,
            R.string.sportshistory_dengshan,
            R.string.sportshistory_yueyepao,
            R.string.sportshistory_banma,
            R.string.sportshistory_quanma,
            R.string.sportshistory_tiaosheng,
            R.string.sportshistory_yumaoqiu,
            R.string.sportshistory_lanqiu,
            R.string.sportshistory_qixing,
            R.string.sportshistory_huabing,
            R.string.sportshistory_jianshen,
            R.string.sportshistory_yujia,
            R.string.sportshistory_wangqiu,
            R.string.sportshistory_pingpang,
            R.string.sportshistory_zuqiu,
            R.string.sportshistory_youyong,
            R.string.sportshistory_xingai,
            R.string.sportshistory_fanhang,
    };

    /** 获取运动类型  0.健走 1.户外跑 2.室内跑 3.登山跑 4.越野跑 5.半马 6.全马 **/
    private static final int[] SECTION_DRAWABLE_WHITE_WATCH = {R.drawable.sportshistory_jianzou_white,
            R.drawable.sportshistory_huwaipao_white,
            R.drawable.sportshistory_shineipao_white,
            R.drawable.sportshistory_dengshan_white,
            R.drawable.sportshistory_yueyepao_white,
            R.drawable.sportshistory_banma_white,
            R.drawable.sportshistory_quanma_white,
            R.drawable.sportshistory_tiaoshen_white,
            R.drawable.sportshistory_yumaoqiu_white,
            R.drawable.sportshistory_lanqiu_white,
            R.drawable.sportshistory_qixing_white,
            R.drawable.sportshistory_huabing_white,
            R.drawable.sportshistory_jiansheng_white,
            R.drawable.sportshistory_yujia_white,
            R.drawable.sportshistory_wangqiu_white,
            R.drawable.sportshistory_pingpang_white,
            R.drawable.sportshistory_zuqiu_white,
            R.drawable.sportshistory_youyong_white,
            R.drawable.sportshistory_xingai_white,};

    private static final int[] SECTION_DRAWABLE_WHITE_WATCH_MTK = {R.drawable.sportshistory_jianzou_white,   // mtk 运动模式图片（白色背景）
            R.drawable.sportshistory_huwaipao_white,
            R.drawable.sportshistory_shineipao_white,
            R.drawable.sportshistory_dengshan_white,
            R.drawable.sportshistory_yueyepao_white,
            R.drawable.sportshistory_banma_white,
            R.drawable.sportshistory_quanma_white,
            R.drawable.sportshistory_tiaoshen_white,
            R.drawable.sportshistory_yumaoqiu_white,
            R.drawable.sportshistory_lanqiu_white,
            R.drawable.sportshistory_qixing_white,
            R.drawable.sportshistory_huabing_white,
            R.drawable.sportshistory_jiansheng_white,
            R.drawable.sportshistory_yujia_white,
            R.drawable.sportshistory_wangqiu_white,
            R.drawable.sportshistory_pingpang_white,
            R.drawable.sportshistory_zuqiu_white,
            R.drawable.sportshistory_youyong_white,
            R.drawable.sportshistory_xingai_white,
            R.drawable.fanhang,    // todo  -- 暂作为 返航的图标
    };

    private boolean isMetric;
    String[] month_of_year;

    public HistoryAdapter(Context context, List<GpsPointDetailData> list, double maxSportData, double minSportData) {
        mContext = context;
        this.dataList = list;
        month_of_year = mContext.getResources().getStringArray(R.array.month_of_year);
        this.inflater = LayoutInflater.from(context);
        this.maxSportData = maxSportData;
        this.minSportData = minSportData;
        Log.e("HistoryAdapter", "dataList: " + dataList);
    }

    public void removeItem(int position) {
        dataList.remove(position);
        notifyDataSetChanged();
    }

    public void setDataList(List<GpsPointDetailData> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GpsPointDetailData gpsData = dataList.get(position);

        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_sporthistory, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (gpsData.getId() == null) {   // 有id时，为条目上部
            viewHolder.ll_type1.setVisibility(View.GONE);
            viewHolder.ll_type2.setVisibility(View.VISIBLE);

            viewHolder.item_month.setText(month_of_year[Utils.toint(gpsData.getDate(), 1) - 1]);  // 设置月份
            if (SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
                viewHolder.item_allde.setText(Utils.setformat(1, gpsData.getMile() / 1000 + "") + "km");
            } else {
                viewHolder.item_allde.setText(Utils.setformat(1, Utils.getUnit_km(gpsData.getMile() / 1000) + "") + "mile");
            }
            int totalSec = 0;
            int yunshu = 0;
            totalSec = (int) (Utils.tofloat(gpsData.getsTime()) / 60);
            yunshu = (int) (Utils.tofloat(gpsData.getsTime()) % 60);  // 0
            int mai = totalSec / 60;   // 0
            int sec = totalSec % 60;  // 0
            try {
                viewHolder.item_alltime.setText(String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", mai, sec, yunshu));// 时间
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {   // 没有id，为整月条目的下部
            viewHolder.ll_type1.setVisibility(View.VISIBLE);
            viewHolder.ll_type2.setVisibility(View.GONE);

            if (Utils.isDe()) {
                viewHolder.item_date.setText(dateInversion(gpsData.getDate()));
            } else {
                viewHolder.item_date.setText(gpsData.getDate());
            }

//            viewHolder.item_date.setText(gpsData.getDate());
            //  8 9 10 12 13 14 15 16 17 18 19 -- 这些运动模式均显示时间
           if(!gpsData.getSportType().contains("end")){
               if(Integer.valueOf(gpsData.getSportType()) == 8 || Integer.valueOf(gpsData.getSportType()) == 9 || Integer.valueOf(gpsData.getSportType()) == 10 || Integer.valueOf(gpsData.getSportType()) == 12
                       || Integer.valueOf(gpsData.getSportType()) == 13 || Integer.valueOf(gpsData.getSportType()) == 14 || Integer.valueOf(gpsData.getSportType()) == 15 || Integer.valueOf(gpsData.getSportType()) == 16 || Integer.valueOf(gpsData.getSportType()) == 17
                       || Integer.valueOf(gpsData.getSportType()) == 18 || Integer.valueOf(gpsData.getSportType()) == 19){ // 运动距离为0 显示运动时长     viewHolder.item_time.setText(gpsData.getSportTime() + "");  // TODO 运动时间
                   if((SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2"))
                           && Integer.valueOf(gpsData.getSportType()) == 18 && !StringUtils.isEmpty(gpsData.getArrTotalSpeed())){
                       //todo --- 手环游泳模式显示距离
                       if(dataList.size() > 3) {
                           if(Integer.valueOf(gpsData.getSportType()) != 8 && Integer.valueOf(gpsData.getSportType()) != 9
                                   && Integer.valueOf(gpsData.getSportType()) != 10 && Integer.valueOf(gpsData.getSportType()) != 12
                                   && Integer.valueOf(gpsData.getSportType()) != 13 && Integer.valueOf(gpsData.getSportType()) != 14
                                   && Integer.valueOf(gpsData.getSportType()) != 15 && Integer.valueOf(gpsData.getSportType()) != 16
                                   && Integer.valueOf(gpsData.getSportType()) != 17
                                   && Integer.valueOf(gpsData.getSportType()) != 19) {
                               if (gpsData.getMile() == maxSportData) {
                                   viewHolder.item_mile.setTextColor(Color.GREEN);
                               } else if (gpsData.getMile() == minSportData) {
                                   viewHolder.item_mile.setTextColor(Color.RED);
                               } else {
                                   viewHolder.item_mile.setTextColor(Color.WHITE);
                               }
                           }
                       }
                       if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))) {
                           viewHolder.item_mile.setText(Utils.decimalTo2(Double.valueOf(gpsData.getMile()) / 1000, 2) + "");
                           viewHolder.tv_danwei.setText(mContext.getString(R.string.kilometer));
                       }else{
                           viewHolder.item_mile.setText(Utils.decimalTo2(Utils.getUnit_km(Double.valueOf(gpsData.getMile()) / 1000) , 2) + "");
                           viewHolder.tv_danwei.setText(mContext.getString(R.string.unit_mi));   //
                       }
                   }else {
                       String mtime = gpsData.getSportTime();    // todo ---- 18 游泳
                       String[] times = mtime.split(":");
                       int hour = Integer.valueOf(times[0]);
                       int min = Integer.valueOf(times[1]);
                       int allmins = hour*60 + min;
                       viewHolder.item_mile.setText(allmins + "");
                       viewHolder.tv_danwei.setText(mContext.getString(R.string.everyday_show_unit));
                   }
               } else {
                   if((SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") && gpsData.getSportType().equals("11"))
                           ||  (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") && gpsData.getSportType().equals("11"))
                           ||  (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") && gpsData.getSportType().equals("4") && gpsData.getDeviceType().equals("2"))
                           ||  (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") && gpsData.getSportType().equals("4")  && gpsData.getDeviceType().equals("2"))){  //X2骑行模式---显示分钟   //TODO 去除登山模式显示分钟数，改为显示公里数(除手环)
                       String mtime = gpsData.getSportTime();    // sportTime = {String@830037423360} "00:03:22"
                       String[] times = mtime.split(":");
                       int hour = Integer.valueOf(times[0]);
                       int min = Integer.valueOf(times[1]);
                       int allmins = hour*60 + min;
                       viewHolder.item_mile.setText(allmins + "");
                       viewHolder.tv_danwei.setText(mContext.getString(R.string.everyday_show_unit));
                   }else {
                       if(dataList.size() > 3) {
                           if((Integer.valueOf(gpsData.getSportType()) != 8 && Integer.valueOf(gpsData.getSportType()) != 9
                                   && Integer.valueOf(gpsData.getSportType()) != 10 && Integer.valueOf(gpsData.getSportType()) != 12
                                   && Integer.valueOf(gpsData.getSportType()) != 13 && Integer.valueOf(gpsData.getSportType()) != 14
                                   && Integer.valueOf(gpsData.getSportType()) != 15 && Integer.valueOf(gpsData.getSportType()) != 16
                                   && Integer.valueOf(gpsData.getSportType()) != 17 && Integer.valueOf(gpsData.getSportType()) != 18
                                   && Integer.valueOf(gpsData.getSportType()) != 19) ) {  // ||  (Integer.valueOf(gpsData.getSportType()) == 18  && !StringUtils.isEmpty(gpsData.getArrTotalSpeed()))
                               if (gpsData.getMile() == maxSportData) {
                                   viewHolder.item_mile.setTextColor(Color.GREEN);
                               } else if (gpsData.getMile() == minSportData) {
                                   viewHolder.item_mile.setTextColor(Color.RED);
                               } else {
                                   viewHolder.item_mile.setTextColor(Color.WHITE);
                               }
                           }
                       }
                       if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))) {
                           viewHolder.item_mile.setText(Utils.decimalTo2(Double.valueOf(gpsData.getMile()) / 1000, 2) + "");
                           viewHolder.tv_danwei.setText(mContext.getString(R.string.kilometer));
                       }else{
                           viewHolder.item_mile.setText(Utils.decimalTo2(Utils.getUnit_km(Double.valueOf(gpsData.getMile()) / 1000) , 2) + "");
//                           viewHolder.item_mile.setText(Utils.decimalTo2(Double.valueOf(gpsData.getMile()) / 1000 * 0.62 , 2) + "");
                            viewHolder.tv_danwei.setText(mContext.getString(R.string.unit_mi));   //

//                           mtv_showdic.setText(Utils.decimalTo2(Utils.getUnit_km(mile / 1000), 2) + "");  //  Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
//            mtv_showdic.setText(Utils.decimalTo2((mile / 1000*0.62), 2) + "");  // todo --- 不能用这个算法 会与设备端有0.01的误差
                        }
                    }
                }
            }

            if (MainService.getInstance().getState() == 3) { // TODO -- 设备连接时
                if (gpsData.getDeviceType().equals("2")) {
                    if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {  // X2手环端运动模式数据
                        //viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                        if (gpsData.getArrLat().equals("0") && gpsData.getArrLng().equals("0")) {
                            viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                        }
                    } else if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {

                        String speed = gpsData.getSpeed();
                        String curspeed = gpsData.getmCurrentSpeed();

                        String arrLat = gpsData.getArrLat();  //纬度数组
                        String arrLng = gpsData.getArrLng();  //经度数组

                        if (gpsData.getSportType().equals("4") || gpsData.getSportType().equals("20")) { // 登山
                            if (!StringUtils.isEmpty(speed) && speed.equals("0.000") && !StringUtils.isEmpty(curspeed) && curspeed.equals("0")) {
                                viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                            } else {
                                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NJY-L3_LE")
                                        || SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NB1_LE")) {
                                    viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                                } else {
                                    viewHolder.iv_rightarrow.setVisibility(View.VISIBLE);
                                }
                            }                                                                                                                                                                                                    //  // todo ---- 18 游泳
                        } else if (!gpsData.getSportType().equals("1") && !gpsData.getSportType().equals("2") && !gpsData.getSportType().equals("11") && !gpsData.getSportType().equals("3") && !gpsData.getSportType().equals("20") && !gpsData.getSportType().equals("18")) {   //1:健走，2：跑步 11：骑行  TODO----  G703S_LE 3：(跑步机，室内跑)
                            viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                        } else {
                            if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NJY-L3_LE")
                                    || SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NB1_LE")) {
                                viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                            } else {
                                viewHolder.iv_rightarrow.setVisibility(View.VISIBLE);
                            }

                            if (gpsData.getSportType().equals("18")) {
                                viewHolder.ll_kllhb.setVisibility(View.GONE);
                            }

                        }
                    } else {
                        viewHolder.iv_rightarrow.setVisibility(View.VISIBLE);
                    }
                } else {
                    viewHolder.iv_rightarrow.setVisibility(View.VISIBLE);
                }
            } else {    // TODO -- 设备未连接时
                String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH);
                if (gpsData.getDeviceType().equals("2")) {
                    if (!StringUtils.isEmpty(tempWatchType)) {
                        if (tempWatchType.equals("2")) {  // X2手环端运动模式数据
                            //viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                            if (gpsData.getArrLat().equals("0") && gpsData.getArrLng().equals("0")) {
                                viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                            }
                        } else if (tempWatchType.equals("3")) {

                            String speed = gpsData.getSpeed();  // 0.000 ---
                            String curspeed = gpsData.getmCurrentSpeed();  // 0

                            String arrLat = gpsData.getArrLat();  //纬度数组 空时为&
                            String arrLng = gpsData.getArrLng();  //经度数组 空时为&

                            if (gpsData.getSportType().equals("4") || gpsData.getSportType().equals("20")) { // 登山
                                if (!StringUtils.isEmpty(speed) && speed.equals("0.000") && !StringUtils.isEmpty(curspeed) && curspeed.equals("0")) {
                                    viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                                } else {
                                    if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NJY-L3_LE")
                                            || SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NB1_LE")) {
                                        viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                                    } else {
                                        viewHolder.iv_rightarrow.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else if (!gpsData.getSportType().equals("1") && !gpsData.getSportType().equals("2") && !gpsData.getSportType().equals("3") && !gpsData.getSportType().equals("11") && !gpsData.getSportType().equals("20") && !gpsData.getSportType().equals("18")) {  // todo  -- 添加游泳
                                viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                            } else {
                                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NJY-L3_LE")
                                        || SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("NB1_LE")) {
                                    viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
                                } else {
                                    viewHolder.iv_rightarrow.setVisibility(View.VISIBLE);
                                }

                                if (gpsData.getSportType().equals("18")) {
                                    viewHolder.ll_kllhb.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
//                    else {
//                        viewHolder.iv_rightarrow.setVisibility(View.VISIBLE);   //todo ----
//                    }
                } else {
                    viewHolder.iv_rightarrow.setVisibility(View.VISIBLE);
                }
            }

           /* if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") && gpsData.getDeviceType().equals("2")) {  // X2手环端运动模式数据
                viewHolder.iv_rightarrow.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.iv_rightarrow.setVisibility(View.VISIBLE); 
            }*/

            Integer ele = Utils.toint(gpsData.getAltitude());
            String eleString;
            if (SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
                if (ele >= 0) {
                    eleString = "+" + String.valueOf(ele);
                } else {
                    eleString = String.valueOf(ele) + "";
                }
                viewHolder.item_rl.setText(gpsData.getCalorie() + "");  // 设置卡路里值，从本地数据库 读取
            } else {
                if (ele >= 0) {
                    ele = (int) Utils.getUnit_mile(ele);
                    eleString = "+" + String.valueOf(ele);
                } else {
                    eleString = String.valueOf(ele) + "";
                }
                viewHolder.item_rl.setText(Utils.decimalTo2(Utils.getUnit_kal(Double.parseDouble(gpsData.getCalorie())), 1) + "");  // 设置卡路里值，从本地数据库 读取(英制)
            }
            //viewHolder.item_rl.setText(gpsData.getCalorie() + "");  // 设置卡路里值，从本地数据库 读取
            //Integer ele = Utils.toint(gpsData.getAltitude());
            //String eleString;
            /*if (ele >= 0) {
                eleString = "+" + String.valueOf(ele);
            } else {
                eleString = String.valueOf(ele) + "";
            }*/
            viewHolder.item_haiba.setText(eleString + "");
            String timesss = gpsData.getSportTime();
            viewHolder.item_time.setText(gpsData.getSportTime() + "");  // TODO 运动时间

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            if ((SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") && gpsData.getDeviceType().equals("2"))
                    || (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3") && gpsData.getDeviceType().equals("2"))) { //mtk
//                if (gpsData.getDeviceType().equals("2")) {   // 设备类型    2：手表   1： 手机   TODO---- 手表端
                String avgSpeed = gpsData.getmCurrentSpeed();//获取当前配速（总用时/总距离）    setArrTotalSpeed
                if (!StringUtils.isEmpty(avgSpeed)) {
                    int avgPeisu = (int) Math.round(Double.parseDouble(avgSpeed));

                    //    if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))) {
                    if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {  // SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))     isMetric
                        avgPeisu = Utils.getUnit_pace(avgPeisu);
                    }

                    int fen = avgPeisu / 60;
                    int miao = avgPeisu % 60;
                    if (fen > 1000) {
                        viewHolder.item_peisu.setTextSize(10);
                    }
//                        viewHolder.item_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen, miao) + "");
                        viewHolder.item_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen, miao));
                    }
//                }
            } else if ((SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") &&
                    gpsData.getDeviceType().equals("2")) || (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER,
                    SharedPreUtil.TEMP_WATCH).equals("1") && gpsData.getDeviceType().equals("2"))) {  // todo ---
                //  1011.7647058823528   -----  1012.0 = 60*16 = 960 + 52
                double countTime = Math.round(Utils.getPaceForWatch1(gpsData.getsTime(), String.valueOf(gpsData.getMile())));//得到配速 数组   1：gpsData.getsTime()：运动时间  2： gpsData.getMile() 运动距离    配速 = 运动时间/距离
//                String m = arrPs[0];//分    -----    8
//                String s = "0." + arrPs[1].substring(0,1);// 将小数点后面的数转换成时间进制（60）  -----   0.916666666666668
//                double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数    -----  55.0
                if (Integer.valueOf((int) (countTime / 60.0)) > 1000) {
                    viewHolder.item_peisu.setTextSize(10);   //  --- todo  ---
                }
                viewHolder.item_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf((int)(countTime/60.0)), Integer.valueOf((int)(countTime%60))));    // 设置配速的值

//                float countTime = [model.time integerValue]/[model.kilometer floatValue];
//                self.paceLabel.text = [NSString stringWithFormat:@"%0d'%02.0f\"",(int)(countTime/60.0),fmod(countTime,60)]; //model.kilometer

            } else {
                if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE).equals("415")) {
                    String avgSpeed = gpsData.getmCurrentSpeed();//获取当前配速（总用时/总距离）    setArrTotalSpeed
                    if (!StringUtils.isEmpty(avgSpeed)) {
                        int avgPeisu = (int) Math.round(Double.parseDouble(avgSpeed));

                        //    if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))) {
                        if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {  // SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))     isMetric
                            avgPeisu = Utils.getUnit_pace(avgPeisu);
                        }

                        int fen = avgPeisu / 60;
                        int miao = avgPeisu % 60;
                        if (fen > 1000) {
                            viewHolder.item_peisu.setTextSize(10);
                        }
//                        viewHolder.item_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen, miao) + "");


                        if (Integer.valueOf(gpsData.getSportType()) == 18 && !StringUtils.isEmpty(gpsData.getArrTotalSpeed())) {
                            viewHolder.img_peisu.setImageResource(R.drawable.swim_th);
                            viewHolder.img_haiba.setImageResource(R.drawable.swim_trip);

                            viewHolder.item_peisu.setText(dataList.get(position).getStep() + "");
                            viewHolder.item_haiba.setText(dataList.get(position).getmCurrentSpeed() + "");
                        } else {
                            viewHolder.img_peisu.setImageResource(R.drawable.peisu);
                            viewHolder.img_haiba.setImageResource(R.drawable.haiba);

                            viewHolder.item_peisu.setText(String.format(Locale.ENGLISH, "%1$02d'%2$02d''", fen, miao));
                        }


                    }
                } else {
                    String arrPs[] = Utils.getPace(gpsData.getsTime(), String.valueOf(gpsData.getMile()));//得到配速 数组   1：gpsData.getsTime()：运动时间  2： gpsData.getMile() 运动距离    配速 = 运动时间/距离
                    String m = arrPs[0];//分
                    String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
                    double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
                    if (Integer.valueOf(m) > 1000) {
                        viewHolder.item_peisu.setTextSize(10);
                    }


                    if (Integer.valueOf(gpsData.getSportType()) == 18 && !StringUtils.isEmpty(gpsData.getArrTotalSpeed())) {
                        viewHolder.img_peisu.setImageResource(R.drawable.swim_th);
                        viewHolder.img_haiba.setImageResource(R.drawable.swim_trip);

                    } else {
                        viewHolder.img_peisu.setImageResource(R.drawable.peisu);
                        viewHolder.img_haiba.setImageResource(R.drawable.haiba);

                        viewHolder.item_peisu.setText(String.format(Locale.ENGLISH, "%1$02d'%2$02d''", Integer.valueOf(m), (int) sec));    // 设置配速的值
                    }

                }


               /* String arrPs[] = Utils.getPace(gpsData.getsTime(), String.valueOf(gpsData.getMile()));//得到配速 数组   1：gpsData.getsTime()：运动时间  2： gpsData.getMile() 运动距离    配速 = 运动时间/距离
                String m = arrPs[0];//分
                String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
                double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
                if(Integer.valueOf(m) > 1000){
                    viewHolder.item_peisu.setTextSize(10);
                }
                viewHolder.item_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int)sec));    // 设置配速的值*/
                // G703直接传的秒数   //		String.format("%1$02d:%2$02d:%3$02d", mai, sec, yunshu)
            }
            //////////////////////////////////////////////////////////////////////////////////////////////
            if (position == dataList.size() - 1) {
                if (dataList.get(position - 1).getId() == null) {
                    viewHolder.line1.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.line1.setVisibility(View.GONE);
                }
                viewHolder.line2.setVisibility(View.VISIBLE);
                viewHolder.line3.setVisibility(View.GONE);
            } else {
                if (dataList.get(position - 1).getId() == null) {
                    viewHolder.line1.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.line1.setVisibility(View.GONE);

                }
                if (dataList.get(position + 1).getId() == null) {
                    viewHolder.line2.setVisibility(View.VISIBLE);
                    viewHolder.line3.setVisibility(View.GONE);
                } else {
                    viewHolder.line2.setVisibility(View.GONE);
                    viewHolder.line3.setVisibility(View.VISIBLE);
                }
            }
            int type = Utils.toint(dataList.get(position).getSportType()) - 1;    // 13

            if (type < 0) {
                type = 0;
            }

            if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {   // todo --- 设置 运动模式的图标   ---白色背景
                if (gpsData.getDeviceType().equals("1")) {  // 手机端数据
                    viewHolder.item_ico.setImageResource(SECTION_DRAWABLE_WHITE_WATCH[type]);
                } else { // 手表端数据
                    if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //MTK
                        viewHolder.item_ico.setImageResource(SECTION_DRAWABLE_WHITE_WATCH_MTK[type]);   // mtk白色运动模式图标
                    } else {
                        viewHolder.item_ico.setImageResource(SECTION_DRAWABLE_WHITE_WATCH[type]);
                    }
                }
            } else {  // todo --- 黑色背景
                if (gpsData.getDeviceType().equals("1")) {  // 手机端数据
                    viewHolder.item_ico.setImageResource(SECTION_DRAWABLE_WATCH[type]);
                } else { // 手表端数据
                    if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //MTK   type = 1
                        viewHolder.item_ico.setImageResource(SECTION_DRAWABLE_WATCH_MTK[type]);     // mtk黑色运动模式图标
                    } else {
                        viewHolder.item_ico.setImageResource(SECTION_DRAWABLE_WATCH[type]);
                    }
                }

            }

            String language = (BTNotificationApplication.getInstance().getResources().getConfiguration().locale).getLanguage();
            if (language.endsWith("ru")) {//俄文
                viewHolder.item_name.setTextSize(6);
            } else if (language.endsWith("pt")) {//葡萄牙语 (巴西)
                viewHolder.item_name.setTextSize(6);
            } else if (language.endsWith("fa")) {//法语
                viewHolder.item_name.setTextSize(6);
            } else if (language.endsWith("pl")) {//波兰
                viewHolder.item_name.setTextSize(6);
            } else if (language.endsWith("es")) { //西班牙语
                viewHolder.item_name.setTextSize(6);
            }
            // todo --- 设置 运动模式的名字
            if (gpsData.getDeviceType().equals("1")) {  // 手机端数据
                viewHolder.item_name.setText(mContext.getString(SECTION_STRINGS_WATCH[type]));
            } else { // 手表端数据
                if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //MTK
                    viewHolder.item_name.setText(mContext.getString(SECTION_STRINGS_WATCH_MTK[type]));   // SECTION_STRINGS_WATCH_MTK
                } else {
                    viewHolder.item_name.setText(mContext.getString(SECTION_STRINGS_WATCH[type]));
                }
            }

            if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {   // todo --- 设置 运动模式的图标   ---白色背景
                viewHolder.item_from.setImageResource(gpsData.getDeviceType().equals("1") ? R.drawable.icon_phone_w : R.drawable.icon_watch_w); //   // 设备类型    2：手表   1： 手机
            } else {
                viewHolder.item_from.setImageResource(gpsData.getDeviceType().equals("1") ? R.drawable.icon_phone : R.drawable.icon_watch); //   // 设备类型    2：手表   1： 手机
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv_rightarrow;  // 向右的箭头
        TextView tv_danwei;
        ImageView item_ico;
        TextView item_mile;
        TextView item_name;   // 运动类型的名字
        TextView item_peisu;  // 配速
        TextView item_rl;
        TextView item_haiba;
        TextView item_date;  // 运动日期

        ImageView item_from;  // 运动模式  数据来源
        TextView item_month; // 月份
        TextView item_allde;  // 总距离
        TextView item_alltime; // 总时间

        TextView item_time;
        View line1;
        View line2;
        View line3;
        LinearLayout ll_type1;
        RelativeLayout ll_type2;    // ll_kllhb
        LinearLayout ll_kllhb;

        ImageView img_peisu, img_haiba;


        ViewHolder(View rootView) {
            this.item_haiba = (TextView) rootView
                    .findViewById(R.id.item_haiba);
            this.item_mile = (TextView) rootView
                    .findViewById(R.id.item_mile);
            this.item_name = (TextView) rootView
                    .findViewById(R.id.item_name);
            this.item_peisu = (TextView) rootView
                    .findViewById(R.id.item_peisu);
            this.item_rl = (TextView) rootView
                    .findViewById(R.id.item_rl);
            this.item_date = (TextView) rootView
                    .findViewById(R.id.item_date);

            this.line1 = (View) rootView
                    .findViewById(R.id.line1);
            this.line2 = (View) rootView
                    .findViewById(R.id.line2);
            this.line3 = (View) rootView
                    .findViewById(R.id.line3);
            this.item_ico = (ImageView) rootView
                    .findViewById(R.id.item_ico);

            this.ll_type1 = (LinearLayout) rootView
                    .findViewById(R.id.ll_type1);
            this.ll_type2 = (RelativeLayout) rootView
                    .findViewById(R.id.ll_type2);

            this.item_month = (TextView) rootView
                    .findViewById(R.id.item_month);
            this.item_allde = (TextView) rootView
                    .findViewById(R.id.item_allde);
            this.item_alltime = (TextView) rootView
                    .findViewById(R.id.item_alltime);

            this.item_time = (TextView) rootView
                    .findViewById(R.id.item_time);
            this.item_from = (ImageView) rootView.findViewById(R.id.item_from);
            this.tv_danwei = (TextView) rootView
                    .findViewById(R.id.tv_danwei);

            this.iv_rightarrow = (ImageView) rootView.findViewById(R.id.iv_rightarrow);

            this.ll_kllhb = (LinearLayout) rootView.findViewById(R.id.ll_kllhb);

            this.img_peisu = rootView.findViewById(R.id.img_peisu);
            this.img_haiba = rootView.findViewById(R.id.img_haiba);
        }
    }
}
