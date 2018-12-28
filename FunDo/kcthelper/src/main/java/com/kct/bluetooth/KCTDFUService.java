package com.kct.bluetooth;

import android.app.Activity;

import no.nordicsemi.android.dfu.DfuBaseService;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/4/9
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTDFUService extends DfuBaseService{

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return KCTDfuNotificationActivity.class;
    }
}
