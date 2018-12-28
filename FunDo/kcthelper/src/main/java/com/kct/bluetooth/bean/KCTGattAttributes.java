package com.kct.bluetooth.bean;

import java.util.UUID;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/10/18
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTGattAttributes {

    public static final UUID DESC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("C3E6FEA0-E966-1000-8000-BE99C223DF6A");
    public static final UUID RX_CHAR_UUID = UUID.fromString("C3E6FEA1-E966-1000-8000-BE99C223DF6A");
    public static final UUID TX_CHAR_UUID = UUID.fromString("C3E6FEA2-E966-1000-8000-BE99C223DF6A");

    public final static UUID BLE_YDS_UUID = UUID.fromString("0000fea0-0000-1000-8000-00805f9b34fb");//手环
    public final static UUID BLE_YDS_UUID_HUAJING = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");//手环(华晶心率芯片)
    public final static UUID MTK_YDS_2502_UUID = UUID.fromString("00002502-0000-1000-8000-00805f9b34fb");//2502mtk
    public final static UUID MTK_YDS_2503_UUID = UUID.fromString("00002503-0000-1000-8000-00805f9b34fb");//2503mtk

    public static final UUID RX_SERVICE_872_UUID_SCAN = UUID.fromString("0783B03E-8535-B5A0-7140-A304D2495CB7");
    public static final UUID RX_SERVICE_872_UUID = UUID.fromString("b75c49d2-04a3-4071-a0b5-35853eb08307");
    public static final UUID RX_CHAR_872_UUID = UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cba");
    public static final UUID TX_CHAR_872_UUID = UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb8");
}
