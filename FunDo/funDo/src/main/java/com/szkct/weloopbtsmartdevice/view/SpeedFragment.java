package com.szkct.weloopbtsmartdevice.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.adapter.SpeedListViewAdapter;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kct on 2016/12/12.
 */
public class SpeedFragment extends Fragment implements View.OnClickListener {


    private View mspeedFragment;
    private Speedview testsview;  // todo --- 配速图表

    private float mins = 0.0f ; // 配速 最快，最慢，平均   private Long mins, maxs;
    private float maxs =  0.0f;
    private float average = 0.0f;

//    private long mins = 0 ; // 配速 最快，最慢，平均   private Long mins, maxs;
//    private long maxs =  0;
//    private long average = 0;

    private ListView mListView;
    private SpeedListViewAdapter adapter;
    private GpsPointDetailData gpsPoint;  // 关键数据
    private TextView tv_speed_round_nom, tv_speed_round_min, tv_speed_round_max,speed_time,speed_chat,tv_psbtitle;
    private DBHelper db;
    private float mdistances = 0f;
    private ImageView sportmode_threepage_logo_iv;
    private TextView run_up;

    List<Float> barValues;  // Float  Long

    public static ScrollView speedfragment_sc;

    @Override
    public void onClick(View view) {

    }

    public static SpeedFragment newInstance() {
        SpeedFragment fragment = new SpeedFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mspeedFragment = inflater.inflate(R.layout.fragment_speedy, null);
        gpsPoint = (GpsPointDetailData) getActivity().getIntent().getSerializableExtra("Vo");
        if (db == null) {
            db = DBHelper.getInstance(getActivity());
        }
        initdata();
        initview();
        return mspeedFragment;
    }
    // 19.257425742574256&3.37&2.3106796116504853&2.8584905660377355&2.0324675324675323&1.754491017964072&0.6&2.7937499999999997&1.054421768707483&0.9108910891089109&2.55&1.2574257425742574&1.2549019607843137&4.514705882352941&4.188118811881188&3.5&3.38&  todo --- 都为分钟数
    private void initdata() { // 11.21359223300971&6.339805825242719&5.0&9.554455445544555&1.9912891986062717&0.44554455445544555&2.0097087378640777&1.8316831683168318&0.8529411764705882&1.9022988505747127&3.871794871794872&0.43925233644859807&2.2399999999999998&
        String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  // 设备型号
        String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH);
        if((watch.equals("3") && gpsPoint.getDeviceType().equals("2")) || (tempWatchType.equals("3") && gpsPoint.getDeviceType().equals("2"))){ //仅对于G703手表端数据 mtk    // 设备类型    2：手表   1： 手机
            barValues = new ArrayList<Float>();
            return;

           /* String totalPs = gpsPoint.getmCurrentSpeed(); //TODO ----  平均配速  2274
            barValues = new ArrayList<Float>();  // Long  Float
            if (StringUtils.isEmpty(totalPs)) {  // TODO  ---- 手表端没有传配速值时 --- 根据运动距离和运动时间 计算   （？？？？ 是否需要区分运动模式   ￥￥￥￥￥￥￥￥￥）
                String mdistance = gpsPoint.getMile()+"";
                String mtime = gpsPoint.getsTime();     // 秒
                float min=Utils.tofloat(mtime)/60;
                float km=Utils.tofloat(mdistance)/1000;
                mdistances=km;
                Float aa = (Float) (min/km);
//                Float ss = aa*60;
                barValues.add(aa);
                mins = aa;
                maxs = aa;
                average = aa;
                return;
            }


            int avgPeisu = (int)Math.round(Double.parseDouble(totalPs));
            int fen = avgPeisu/60;
            int miao  = avgPeisu%60 ;

            String ps = fen + "." + miao;

            maxs = Float.valueOf(ps);
            mins = Float.valueOf(ps);
            barValues.add(Float.valueOf(ps));
            average = Float.valueOf(ps);    //TODO  ----  平均配速值*/
        }else if((watch.equals("2") || tempWatchType.equals("2") ) && gpsPoint.getDeviceType().equals("2")){
            String totalPs = gpsPoint.getArrTotalSpeed();
            String[] arrPs = totalPs.split("&");
            barValues = new ArrayList<Float>();
            if (totalPs.equals("")||arrPs == null || arrPs.length == 0 || totalPs.equals("0")) {  // TODO  ---- 手表端没有传配速值时 --- 根据运动距离和运动时间 计算   （？？？？ 是否需要区分运动模式   ￥￥￥￥￥￥￥￥￥）
                String mdistance = gpsPoint.getMile()+"";   // 199.78 单位 米
                String mtime = gpsPoint.getsTime();     // 秒   412
                float min=Utils.tofloat(mtime)/60;  // 6.866667 分钟
                float km=Utils.tofloat(mdistance)/1000;   // 0.19978KM
                mdistances=km;
                ///////////////////////////////////////////////////////////////////////////////////////////

                String arrPsNew[] = Utils.getPace(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile()));//得到配速 数组
                String m = arrPsNew[0];//分
                String s =  "0." + arrPsNew[1];// 将小数点后面的数转换成时间进制（60）

//                String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
                int sec = (int)Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 0);//秒数

//                mdetail_peisu.setText(String.format("%1$02d'%2$02d''", Integer.valueOf(m), (int) sec) + "");// 配速
                String peisuNew = m + "." + sec;

                Float aa = Float.valueOf(peisuNew);
                //////////////////////////////////////////////////////////////////
                barValues.add(aa);
                mins = aa;
                maxs = aa;
                average = aa;
                return;
            }
            barValues = new ArrayList<>();
            for (int i = 0; i < arrPs.length; i++) {
                if(arrPs[i].contains("'")){
                    String[] arrPss = arrPs[i].split("'");
                    float a = Float.parseFloat(arrPss[0] + "." + arrPss[1]);
                    barValues.add(a);
                }
            }
            if(barValues.size() > 0 && barValues != null){
                mins = Collections.min(barValues);
                maxs = Collections.max(barValues);
            }
        }else {
            String totalPs = gpsPoint.getSpeed(); //TODO ----  每千米的配速（配速数组数据）   ----- 61&61&61&61&61&61&61&58&58&58&58&58&58&182(手表端数据)
            Log.e("totalPs=", totalPs);
            String[] arrPs = totalPs.split("&");   // 13.32
            barValues = new ArrayList<Float>();  // Long  Float
            if (totalPs.equals("")||arrPs == null || arrPs.length == 0 || totalPs.equals("0")) {  // TODO  ---- 手表端没有传配速值时 --- 根据运动距离和运动时间 计算   （？？？？ 是否需要区分运动模式   ￥￥￥￥￥￥￥￥￥）
                String mdistance = gpsPoint.getMile()+"";   // 199.78 单位 米
                String mtime = gpsPoint.getsTime();     // 秒   412
                float min=Utils.tofloat(mtime)/60;  // 6.866667 分钟
                float km=Utils.tofloat(mdistance)/1000;   // 0.19978KM
                mdistances=km;
                ///////////////////////////////////////////////////////////////////////////////////////////

                String arrPsNew[] = Utils.getPace(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile()));//得到配速 数组
                String m = arrPsNew[0];//分
                String s =  "0." + arrPsNew[1];// 将小数点后面的数转换成时间进制（60）

//                String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
                int sec = (int)Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 0);//秒数

//                mdetail_peisu.setText(String.format("%1$02d'%2$02d''", Integer.valueOf(m), (int) sec) + "");// 配速
                String peisuNew = m + "." + sec;

                Float aa = Float.valueOf(peisuNew);
                //////////////////////////////////////////////////////////////////
                if(aa > 0) {
                    barValues.add(aa);
                    mins = aa;
                    maxs = aa;
                    average = aa;
                }
                return;
            }

            Float temp;  //    Long temp;
            Float sum = 0f;
            float sumOk = 0f;
            //TODO 配速详情图表显示
            for (int i = 0; i < arrPs.length; i++) {
//            temp = (long) (Utils.tofloat(arrPs[i]) * 60); //120.5  分钟*60=秒数    -----  配速值*60   --- 1168*60
//            temp =  (Utils.tolong(arrPs[i]));  // 对于手机端配速值该配速计算方法不对      // traject_pingjun.setText(String.format(String.format("%1$02d'%2$02d''", watchPjPs / 60, watchPjPs % 60) + "")); // 平均


//                String peisuok = String.format("%1$02d'%2$02d''", Integer.valueOf(psStr[0]), Integer.valueOf(psStr[1].substring(0, 2)) * (60)/100);
//                if(arrPs[i].contains("'")) {
//                    String[] ss = arrPs[i].split("'");


                String[] ss = Utils.setformat(2, arrPs[i]).split("\\.");  // 得到的是分钟数，需要转换为分秒
                String ps = ss[0] + "." + ss[1];
                int miao = Integer.valueOf(ss[1])*60/100;
                String psStrok  = ss[0] + "." + miao;

                temp =  Float.valueOf(psStrok);  // arrPs[i]
                sum += temp;

//                String totalKmPs = Utils.setformat(2, Utils.getPace2(String.valueOf(mKmCurrentCount), String.valueOf(mKmMile)))   Utils.setformat(2,);

                String sunStr = Utils.setformat(2,String.valueOf(sum));
                String[] sun = sunStr.split("\\.");
                int sun0 = Integer.valueOf(sun[0]);
                int sun1 = Integer.valueOf(sun[1].substring(0,2));
                if(sun1 >= 60){
                    sun0++;
                    sun1 = sun1 - 60;
                }
                String sunOk = sun0 + "." + sun1;
                sumOk = Float.valueOf(sunOk);
                sum = sumOk;

                if (i == 0) {
                    mins = temp;
                    maxs = temp;   // todo  --- 最大配速值
                }
                if (maxs < temp) {
                    maxs = temp;
                }
                if (mins > temp) {
                    mins = temp;  // todo  --- 最小配速值
                }
                barValues.add(temp);
            }

            average = sum / arrPs.length;    //TODO  ----  平均配速值     sum / arrPs.length

            String averageStr = Utils.setformat(2, String.valueOf(average));
            String[] avg = averageStr.split("\\.");
            int sun0 = Integer.valueOf(avg[0]);
            int sun1 = Integer.valueOf(avg[1].substring(0,2));
            if(sun1 >= 60){
                sun0++;
                sun1 = sun1 - 60;
            }
            String averageOk = sun0 + "." + sun1;
            sumOk = Float.valueOf(averageOk);
            average = sumOk;
        }
    }

    private void initview() {
        testsview = (Speedview) mspeedFragment.findViewById(R.id.testview);

        speed_time= (TextView) mspeedFragment.findViewById(R.id.speed_time);

        speed_time.setText(Utils.date2De(gpsPoint.getDate()));
        tv_speed_round_nom = (TextView) mspeedFragment.findViewById(R.id.tv_speed_round_nom); //平均
        tv_speed_round_max = (TextView) mspeedFragment.findViewById(R.id.tv_speed_round_max); // 最快
        tv_speed_round_min = (TextView) mspeedFragment.findViewById(R.id.tv_speed_round_min); // 最慢

        speedfragment_sc = (ScrollView) mspeedFragment.findViewById(R.id.speedfragment_sc);
        speed_chat = (TextView) mspeedFragment.findViewById(R.id.speed_chat_up);
        tv_psbtitle = (TextView) mspeedFragment.findViewById(R.id.tv_psbtitle);

        String languageLx  = Utils.getLanguage();
        if (languageLx.equals("ja") || languageLx.equals("pl") || languageLx.contains("de") ) {     // || languageLx.equals("it") || languageLx.contains("fr")
            speed_chat.setTextSize(11);
            tv_psbtitle.setTextSize(11);
        }else if(languageLx.equals("ru")){
            speed_chat.setTextSize(11);
            tv_psbtitle.setTextSize(11);
            speed_time.setTextSize(11);
        }



        sportmode_threepage_logo_iv = (ImageView) mspeedFragment.findViewById(R.id.sportmode_threepage_logo_iv);
        if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){  // 白色背景
            sportmode_threepage_logo_iv.setImageResource(R.drawable.sportmode_logo_w);
        }else{
            sportmode_threepage_logo_iv.setImageResource(R.drawable.sportmode_logo_b);
        }

        run_up = (TextView) mspeedFragment.findViewById(R.id.speed_run_up);

        if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))){
            run_up.setText(getString(R.string.kilometer));
        }else{
            run_up.setText(getString(R.string.unit_mi));
        }
//        tv_speed_round_nom.setText(getString(R.string.speed_average) + Utils.getPeisu(average));   // 平均配速    String peisu = String.format("%1$02d'%2$02d''",  Utils.toint(m), sec);
//        tv_speed_round_max.setText(getString(R.string.speed_max) + Utils.getPeisu(maxs));  // 最大配速
//        tv_speed_round_min.setText(getString(R.string.speed_min) + Utils.getPeisu(mins));   // 最小配速

        String[] avStr = Utils.setformat(2, String.valueOf(average)).split("\\."); // 3 --- 6686437  String.valueOf(average)
        String[] maxStr = Utils.setformat(2,String.valueOf(maxs)).split("\\.");
        String[] minStr = Utils.setformat(2, String.valueOf(mins)).split("\\.");  // 0 --- 43925235


        if (languageLx.equals("tr") || languageLx.equals("it") || languageLx.contains("fr")) {
            tv_speed_round_nom.setTextSize(10);
            tv_speed_round_max.setTextSize(10);
            tv_speed_round_min.setTextSize(10);
            speed_chat.setTextSize(9);
        }

        tv_speed_round_nom.setText(getString(R.string.speed_average) + String.format(Locale.ENGLISH,"%1$02d'%2$02d''",Integer.valueOf(avStr[0]) , Integer.valueOf(avStr[1].substring(0,2))));   // 平均配速  String.format("%1$02d'%2$02d''",  Utils.toint(m), sec)
        tv_speed_round_max.setText(getString(R.string.speed_max) + String.format(Locale.ENGLISH,"%1$02d'%2$02d''",Integer.valueOf(maxStr[0]) , Integer.valueOf(maxStr[1].substring(0,2))));  // 最大配速
        tv_speed_round_min.setText(getString(R.string.speed_min) + String.format(Locale.ENGLISH,"%1$02d'%2$02d''",Integer.valueOf(minStr[0]) , Integer.valueOf(minStr[1].substring(0,2))));   // 最小配速

        mListView = (ListView) mspeedFragment.findViewById(R.id.speed_list);
        testsview.setDataToShow(barValues, mins, maxs);// 配速图表赋值    58-- 182

//        adapter = new SpeedListViewAdapter(getActivity(), barValues, mins, maxs);   // 下面的listview 配速列表
        Log.e("speed","mins = " + mins  +  " ; maxs = " + maxs + "  ;  barValues = " + barValues.size());
        adapter = new SpeedListViewAdapter(getActivity(), barValues, mins, maxs);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mListView);
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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
