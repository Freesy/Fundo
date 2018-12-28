package com.szkct.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.AlarmClockData;
import com.szkct.weloopbtsmartdevice.util.Log;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/6/28
 * 描述: ${VERSION}
 * 修订历史：
 */
public class SMListViewAdapter extends BaseAdapter {
    public static final String TAG = SMListViewAdapter.class.getSimpleName();
    private Context context;
    private List<AlarmClockData> clockList;
    private LayoutInflater mInflater;
    private MyClickListener mListener;

    public SMListViewAdapter(Context context, List<AlarmClockData> clockList, MyClickListener listener) {
        this.context = context;
        this.clockList = clockList;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @Override
    public int getCount() {
        return clockList.size();
    }

    @Override
    public Object getItem(int position) {
        return clockList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();

            convertView = mInflater.inflate(R.layout.listview_alarm_clock_item, null);
            holder.textAlarmTime = (TextView) convertView.findViewById(R.id.tv_time_alarm_clock);
            holder.textTypeFormat = (TextView) convertView.findViewById(R.id.tv_cycle_alarm_clock);
            holder.checkImageView = (ImageView) convertView.findViewById(R.id.tb_alarm_clock);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.textAlarmTime.setText(clockList.get(position).getTime().toString());

        String languageLx  = Utils.getLanguage();
        if (languageLx.equals("tr")) {
            holder.textTypeFormat.setTextSize(10);
        }

        holder.textTypeFormat.setText(Utils.getFrequency(context,clockList.get(position).getCycle()));
        holder.checkImageView.setImageResource(Utils.getTBState(clockList.get(position).getType()) ? R.drawable.switch_bg_on : R.drawable.switch_bg_off);

        holder.checkImageView.setOnClickListener(mListener);
        holder.checkImageView.setTag(position);
        return convertView;
    }

    public static abstract class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }
        public abstract void myOnClick(int position, View v);
    }

    public final class ViewHolder {
        public TextView textAlarmTime;
        public TextView textTypeFormat;
        public ImageView checkImageView;
    }

}
