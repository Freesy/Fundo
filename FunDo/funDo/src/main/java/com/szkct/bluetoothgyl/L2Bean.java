package com.szkct.bluetoothgyl;

import android.util.Log;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/2/20
 * 描述: ${VERSION}
 * 修订历史：
 */
public class L2Bean {
    private final static String TAG = "L2Bean";
    public final static int L2_HEADER_VERSION = 0;
    private int version;
    private int command;
    private int key;
    private int length;
    private byte[] keyValue;
    private int l2length;


    public int getCommand() {
        return command;
    }
    public void setCommand(int cmd) {
        command = cmd;
    }

    public void setVersion(int ver) {
        version = ver;
    }
    public int getVersion() {
        return version;
    }

    public void setKey(int key) {
        this.key = key;
    }
    public int getKey() {
        return key;
    }

    public void setLength(int length) {
        this.length = length;
    }
    public int getLength() {
        return length;
    }

    public void setkeyValue(byte[] value) {
        this.keyValue = value;
    }
    public byte[] getkeyValue() {
        return keyValue;
    }

    public void setL2length(int l2length) {
        this.l2length = l2length;
    }
    public int getL2length() {
        return l2length;
    }


    public byte[] L2Pack(byte cmd,byte key,byte[] keyValue){
        /**保留字段*/
        int reserve = 0;
        int ver = L2_HEADER_VERSION;
        int length = (keyValue == null)?0:keyValue.length;
        /**L2*/
        byte[] L2_send_data = new byte[length + 5];
        /**L2的没数据包头*/
        L2_send_data[0] = cmd;
        L2_send_data[1] = (byte) ((ver << 2) | reserve);
        L2_send_data[2] = key;
        L2_send_data[3] = (byte)((length>>8)); //  & 0x01
        L2_send_data[4] = (byte)(length & 0xFF);
        if(keyValue!=null)
            System.arraycopy(keyValue, 0, L2_send_data, 5, length);
        return L2_send_data;
    }

    public void L2UnPack(byte[] msg){
        setCommand(msg[0]);
        Log.d(TAG, "L2UnPack:command:" + getCommand());
        setVersion((msg[1]&0xF0)>>4);
        Log.d(TAG, "L2UnPack:version:" + getVersion());
        setKey(msg[2]);
        Log.d(TAG, "L2UnPack:Key:" + getKey());
        setLength((((msg[3]&0x01)<<8)|msg[4]));
        Log.d(TAG, "L2UnPack:Length:" + getLength());
        if(getLength() != (msg.length -5)){
            Log.d(TAG, "L2UnPack: length error");
            return ;//err packet
        }
        byte[] tmp = new byte[getLength()];
        System.arraycopy(msg, 5, tmp, 0, getLength());
        setkeyValue(tmp);
        Log.d(TAG, "L2UnPack:keyValue:" + UtilsLX.bytesToHexString(getkeyValue()));
    }

    public byte[] MessagePacket(byte command, byte key,byte[] value){
        L2Bean l2Bean = new L2Bean();
        return l2Bean.L2Pack(command,key,value);
    }
}
