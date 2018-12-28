package com.szkct.bluetoothgyl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import com.mtk.app.notification.AppList;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

//import demo.bt.com.btdemo.mtk.notification.AppList;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/2/23
 * 描述: ${VERSION}
 * 修订历史：
 */
public class UtilsLX {

    public final static String MAC="MAC";
    public final static String ADDRESS="ADDRESS";
    public final static String USER="USER";

    /**
     * 把字节数组转换成16进制字符串
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        if(bArray == null)
            return null;
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte crcSum(byte[] buffer)
    {
        byte crc= 0;
        for (int i = 0; i < buffer.length; i++) {
            crc += buffer[i];
        }
        return crc;
    }

    public static int getCrc(byte[] data) {  //16位

        StringBuffer stringBuffer2 = new StringBuffer();
        for(int i=0;i<data.length;i++){
            stringBuffer2.append(String.format(Locale.ENGLISH,"0x%02X", data[i]));
            stringBuffer2.append(",");
        }
        String t4 = stringBuffer2.toString();  // 0x0A,0x00,0xA0,0x00,0x00,

        int high;
        int flag;

        // 16位寄存器，所有数位均为1
        int wcrc = 0xffff;
        for (int i = 0; i < data.length; i++) {
            // 16 位寄存器的高位字节
            high = wcrc >> 8;
            // 取被校验串的一个字节与 16 位寄存器的高位字节进行“异或”运算
            wcrc = high ^ data[i];

            for (int j = 0; j < 8; j++) {
                flag = wcrc & 0x0001;
                // 把这个 16 寄存器向右移一位
                wcrc = wcrc >> 1;
                // 若向右(标记位)移出的数位是 1,则生成多项式 1010 0000 0000 0001 和这个寄存器进行“异或”运算
                if (flag == 1)
                    wcrc ^= 0xa001;
            }
        }

        return wcrc;  // -49538
    }

    public static int CRC8(byte [] buffer){
        int crci =0xFF;
        int length = buffer.length;
        for (int i = 0; i < length; i++) {
            crci^= buffer[i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                if((crci & 1) !=0){
                    crci >>=1;
                    crci^=0xB8;
                }else{
                    crci>>=1;
                }
            }
        }
        return Integer.valueOf(crci);
    }




    /**连接两个byte数组*/
    public static byte[] arraycat(byte[] buf1,byte[] buf2)
    {
        byte[] bufret=null;
        int len1=0;
        int len2=0;
        if(buf1!=null)
            len1=buf1.length;
        if(buf2!=null)
            len2=buf2.length;
        if(len1+len2>0)
            bufret=new byte[len1+len2];
        if(len1>0)
            System.arraycopy(buf1, 0, bufret, 0, len1);
        if(len2>0)
            System.arraycopy(buf2, 0, bufret, len1, len2);
        return bufret;
    }

    /** 得到蓝牙适配器 */
    public static BluetoothAdapter getDefaultAdapter(Context context) {
        BluetoothAdapter adapter = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        return adapter;
    }


    /**
     * Lookup contact name from phonebook by phone number.
     *
     * @param context
     * @param phoneNum
     * @return the contact name
     */
    public static String getContactName(Context context, String phoneNum) {
        // Lookup contactName from phonebook by phoneNum
        if (phoneNum == null) {
            return null;
        } else if (phoneNum.equals("")) {
            return null;
        } else {
            String contactName = phoneNum;
            try {
                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(contactName));
                Cursor cursor = context.getContentResolver().query(uri, new String[] {
                        "display_name"
                }, null, null, null);
                if ((cursor != null) && cursor.moveToFirst()) {
                    contactName = cursor.getString(0);
                }
                cursor.close();
                return contactName;
            } catch (Exception e) {
                return contactName;
            }
        }
    }



    public static String getKeyFromValue(CharSequence charSequence) {
        Map<Object, Object> appList = AppList.getInstance().getAppList();
        Set<?> set = appList.entrySet();
        Iterator<?> it = set.iterator();
        String key = null;
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue() != null && entry.getValue().equals(charSequence)) {
                key = entry.getKey().toString();
                break;
            }
        }
        return key;
    }

    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            sbu.append(value.getBytes());
        }
        return sbu.toString();
    }


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     *            （DisplayMetrics类中属性density） //3.0
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    public static float dip2pxfloat(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }



    public static void savePre(Context context,String name,String key,String value){
        SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String readPre(Context context,String name,String key){
        if(context==null){
            Log.e("", "共享参数context为空");
            return "";
        }
        SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
        return preference.getString(key, "");
    }

    public static void delPre(Context context,String name,String key){
        SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preference.edit();
        if(key==null||"".equals(key)){
            editor.clear();
        }else{
            editor.remove(key);
        }
        editor.commit();
    }

    //获得系统时区
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    public static String createGmtOffsetString(boolean includeGmt, boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }
}
