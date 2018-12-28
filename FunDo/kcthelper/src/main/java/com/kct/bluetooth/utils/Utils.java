package com.kct.bluetooth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/11/17
 * 描述: ${VERSION}
 * 修订历史：
 */

public class Utils {

    private static final String FILE_NAME = "sdk_info";

    public static List<UUID> parseFromAdvertisementData(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0) break;
            byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2) {
                        uuids.add(UUID.fromString(String.format(Locale.ENGLISH,
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;

                default:
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }
        return uuids;
    }


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






    // 从byte数组的index处的连续4个字节获得一个int
    public static int getInt(byte[] arr, int index) {
        return (0xff000000 & (arr[index + 0] << 24)) |
                (0xff0000 & (arr[index + 1] << 16)) |
                (0xff00 & (arr[index + 2] << 8)) |
                (0xff & arr[index + 3]);
    }

    public static String fullToHalf(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) //全角空格
            {
                c[i] = (char) 32;
                continue;
            }

            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    public static String getCurrentTimeZone()
    {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(false,false,tz.getRawOffset());
    }
    private static String createGmtOffsetString(boolean includeGmt,
                                                boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        String sign = "";
        if (offsetMinutes < 0) {
            sign = "-";
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 0, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
            appendNumber(builder, 2, offsetMinutes % 60);
        }
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }


    public static String getInfo(int n) {
        String result = "";
        for(int i = 6;i >= 0; i--){
            result += n >>> i & 1;
        }
        return result;
    }


    public static int getInts(String ss){
        int sum = 0;
        char[] c = ss.toCharArray();
        boolean isFound = false;
        for (int i = 0; i < c.length; i++) {
            if(c[i] != '0'){
                if(c[i] != '1') {
                    sum = Integer.parseInt(ss);
                    isFound = true;
                    break;
                }
            }
        }
        if(!isFound) {
            sum = Integer.parseInt(ss, 2);
        }
        return sum;
    }


    public static byte getFbyte(String r) {
        int b  = 0;
        char[]c = r.toCharArray();
        for (int i = 0 ;i<c.length;i++){
            if (c[i] == '1'){
                if(i == 0){
                    b +=  1;
                }
                if(i == 1){
                    b +=  2;
                }
                if(i == 2){
                    b +=  4;
                }
                if(i == 3){
                    b +=  8;
                }
                if(i == 4){
                    b +=  16;
                }
                if(i == 5){
                    b +=  32;
                }
                if(i == 6){
                    b +=  64;
                }
//                b += (int) Math.pow(2,i);
            }
        }
        return (byte) b;
    }


    public static void saveObject(Context context, String key, Object obj) {
        try {
            // 保存对象
            SharedPreferences.Editor sharedata = context.getSharedPreferences(FILE_NAME, 0).edit();
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            sharedata.putString(key, bytesToHexString);
            sharedata.commit();
        } catch (IOException e) {
            e.printStackTrace();
            android.util.Log.i("Util", "save obj fail");
        }
    }

    /**
     * desc:获取保存的Object对象
     *
     * @param context
     * @param key
     * @return modified:
     */
    public static Object readObject(Context context, String key) {
        try {
            SharedPreferences sharedata = context.getSharedPreferences(FILE_NAME, 0);
            if (sharedata.contains(key)) {
                String string = sharedata.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //所有异常返回null
        return null;

    }


    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
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


    /**
     * desc:将16进制的数据转为数组
     * <p>创建人：聂旭阳 , 2014-5-25 上午11:08:33</p>
     *
     * @param data
     * @return modified:
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }


    /**
     * 获取系统默认语言 chendalin add
     *
     * @return
     */
    public static String getCountry() {
        // 获取系统当前使用的语言
        Locale locale = Locale.getDefault();  // zh_HK   ---- zh_TW
        String country = locale.getCountry(); // HK     ---- TW
        return country;
    }

    /**
     * 获取系统默认语言 chendalin add
     *
     * @return
     */
    public static String getLanguage() {
        // 获取系统当前使用的语言
        Locale locale = Locale.getDefault();  // zh_HK  --- zh_TW
        String language = locale.getLanguage(); // zh
        return language;
    }
}
