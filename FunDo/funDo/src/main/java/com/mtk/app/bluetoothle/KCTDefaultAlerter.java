package com.mtk.app.bluetoothle;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mediatek.leprofiles.BlePxpFmpConstants;
import com.mediatek.leprofiles.fmppxp.FmpServerAlerter;
import com.szkct.weloopbtsmartdevice.main.MainService;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/2/6
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTDefaultAlerter implements FmpServerAlerter {
    private static final String TAG = KCTDefaultAlerter.class.getSimpleName();
    private static final boolean DBG = true;


    private final Context mCtx;

    public KCTDefaultAlerter(final Context ctx) {
        if (DBG) Log.e(TAG, "KCTDefaultAlerter");
        mCtx = ctx;
    }


    @Override
    public final boolean alert(final int level) {
        if (DBG) Log.i(TAG, "alert: level = " + level);
        boolean ret = false;
        switch (level) {
            case BlePxpFmpConstants.FMP_LEVEL_HIGH:
            case BlePxpFmpConstants.FMP_LEVEL_MILD:
                /*Intent intent = new Intent();
                intent.setAction(MainService.ACTION_FINDWATCHON);    //打开查找手机
                mCtx.sendBroadcast(intent);*/
                break;
            case BlePxpFmpConstants.FMP_LEVEL_NO:
                /*Intent intent1 = new Intent();
                intent1.setAction(MainService.ACTION_FINDWATCHOFF);    //打开查找手机
                mCtx.sendBroadcast(intent1);*/
                break;
            default:
                Log.e(TAG, "Invalid level");
                break;
        }
        return ret;
    }
}
