package com.szkct.weloopbtsmartdevice.view;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

public class HeartCheckHistoryAdapter extends BaseQuickAdapter<Ecg,BaseViewHolder> {
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public HeartCheckHistoryAdapter(int layoutResId, @Nullable List<Ecg> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Ecg item) {
        helper.setTypeface(R.id.tv, BTNotificationApplication.getInstance().dIN1451EF_EngNeuTypeface);
        helper.setText(R.id.tv,Utils.getHeart(item));
        String currentDate = simpleDateFormat1.format(Long.parseLong(item.getBinTime()));
        helper.setText(R.id.tv_time,currentDate);
//        HeartPathView hpv = helper.itemView.findViewById(R.id.hpv);
//        hpv.setData((ArrayList) mData,true);
    }

}
