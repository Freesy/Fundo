package com.szkct.weloopbtsmartdevice.view;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.Temperature;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TemperatureAdapter extends BaseQuickAdapter<Temperature, BaseViewHolder> {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public TemperatureAdapter(int layoutResId, @Nullable List<Temperature> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Temperature item) {
        calendar.setTimeInMillis(Long.parseLong(item.getBinTime()));
        String date = mSimpleDateFormat.format(calendar.getTime());
        helper.setText(R.id.tv_time, date);
        helper.setText(R.id.tv_heart, item.getTemperatureValue());
    }
}
