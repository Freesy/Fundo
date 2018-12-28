package com.szkct.adapter;


import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;


import java.util.List;
import java.util.Locale;

public class SpeedListViewAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private List<Float> datalist;
    private LayoutInflater listContainer;           //视图容器
    private  int State=-1;                   //状态
    private  String ssid="";
    float max,min;
    TypedArray a = null; //状态
    public final class ListItemView{                //自定义控件集合
        public TextView speed_no;
        public TextView speed_speed;
        LinearLayout ll_list;
        public TextView speed_time;
    }


    public SpeedListViewAdapter(Context context, List<Float> datalist,float max,float min) {  // Context context, List<Float> datalist,long max,long min
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.datalist = datalist;
        this.max = max;
        this.min = min;
    }

    public int getCount() {
        return datalist.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    /**
     * ListView Item设置
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        final int selectID = position;
        //自定义视图
        ListItemView  listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.fragment_speed_team_list, null);
            //获取控件对象
            listItemView.speed_no = (TextView)convertView.findViewById(R.id.tv_speed_no);
            listItemView.speed_speed = (TextView)convertView.findViewById(R.id.tv_speed_speed);
            listItemView.speed_time = (TextView)convertView.findViewById(R.id.tv_speed_time);  //耗时

            listItemView.ll_list = (LinearLayout)convertView.findViewById(R.id.ll_listbg);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        listItemView.speed_no.setText(position + 1 + ""); // todo -- 公里
        Float peisu = datalist.get(position);  // Utils.setformat(2,(maxs-mins)/2+mins+"")
        String psStr = Utils.setformat(2, peisu + "");
        String[] peisuStr = psStr.split("\\.");
//        listItemView.speed_speed.setText(Utils.getPeisu(datalist.get(position)));
        listItemView.speed_speed.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''",(int)Math.round(Double.parseDouble(peisuStr[0])) , (int)Math.round(Double.parseDouble(peisuStr[1].substring(0,2)))));  // todo -- 配速

//        long xxx = 0;
//        for (int s = 0; s < position + 1; s++) {
//            xxx += datalist.get(s);   // 累加所有的配速值
//        }
//        int totalSec = 0;
//        int yunshu = 0;
//        totalSec = (int)xxx / 60;
//        yunshu = (int) xxx % 60;
//        int mai = totalSec / 60;
//        int sec = totalSec % 60;

        int psFen = (int)Math.round(Double.parseDouble(peisuStr[0])); // 总分钟数
        int psMiao = (int)Math.round(Double.parseDouble(peisuStr[1].substring(0,2)));// 总秒数
        int mai = psFen / 60;  // 小时
        int sec = psFen % 60;  // 分钟
//        yunshu = (int) psFen % 60;
        try {
            listItemView.speed_time.setText(String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", mai, sec, psMiao));//todo --- 耗时 时间
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (datalist.get(position) == max) {

            if(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){

                listItemView.ll_list.setBackgroundResource(R.drawable.speedlist_max_bg_white);
            }else{
                listItemView.ll_list.setBackgroundResource(R.drawable.speedlist_max_bg);
            }
        }
        if (datalist.get(position) == min) {

            if(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){

                listItemView.ll_list.setBackgroundResource(R.drawable.speedlist_min_bg_white);
            }else{
                listItemView.ll_list.setBackgroundResource(R.drawable.speedlist_min_bg);
            }
        }
        return convertView;
    }
}  