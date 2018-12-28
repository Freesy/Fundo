package com.kct.bluetooth.bean;

import java.util.Arrays;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/10/20
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTBLE_L1Bean {

    public static int HEADER_SIZE = 8;
    private int HEADER_VERSION = 0;
    private int mark;
    private int version ;
    private int errFlag;
    private int ackFlag;
    private int length;
    private int crc;
    private int sequenceId;
    private byte[] payl2load;
    private int reserve;

    private int value(byte[] msg,int offset,int length){
        int tmp = 0x00000000;
        int len = length;
        for(int i =0;i<length;i++){
            len--;
            tmp |= (msg[i+offset]&0xFF)<<(8*len);
        }
        return tmp;
    }

    public void setMark(byte[] msg){this.mark = value(msg,0,1);}

    public int getMark(){return mark;}

    public byte[] getL2Playload() {
        return payl2load;
    }

    public void setL2Playload(byte[] payload) {
        this.payl2load = payload;
    }

    public int getVersion() {
        return version;
    }

    private void setVersion(byte[] msg) {
        this.version = value(msg,1,1)&0x00000001;
    }

    public int getErrFlag() {
        return errFlag;
    }

    private void setErrFlag(byte[] msg) {
        this.errFlag = (value(msg,1,1)&0x00000020)>>5;
    }

    public int getAckFlag() {
        return ackFlag;
    }

    private void setAckFlag(byte[] msg) {
        this.ackFlag =  (value(msg,1,1)&0x00000010)>>4;
    }

    public int getPayloadLength() {
        return length;
    }

    private void setPayloadLength(byte[] msg) {
        this.length = value(msg,2,2);
    }

    public int getCrc() {
        return crc;
    }

    private void setCrc(byte[] msg) {
        this.crc = value(msg,4,2);
    }

    public int getSequenceId() {
        return sequenceId;
    }

    private void setSequenceId(byte[] msg) {
        this.sequenceId = value(msg,6,2);
    }

    public int getReserve() {
        return reserve;
    }

    private void setReserve(byte[] msg) {
        this.reserve = (value(msg,1,1)&0x00000040)>>6;
    }


    public byte[] MessagePack(int sequenceId,byte[] l2Playload){
        return L1Pack(1,0,sequenceId,l2Playload);
    }

    public byte[] L1Pack(int err,int ack,int sequenctId,byte[] l2Playload){
        byte[] L1_header = new byte[HEADER_SIZE];
        int playload_length = (l2Playload == null)?0:l2Playload.length;
        int crc = (l2Playload == null)?0:CRC8(l2Playload);
        if(crc < 0){
            crc = -crc;
        }
        int Reserve = 0;
        int ver = HEADER_VERSION;
        L1_header[0] = (byte) 0xBA;
        L1_header[1] = (byte) ((Reserve << 6) | (err << 5) | (ack << 4) | (ver));
        L1_header[2] = (byte) ((playload_length >> 8) & 0xff);
        L1_header[3] = (byte) (playload_length & 0xff);
        L1_header[4] = (byte) ((crc >> 8) & 0xff);
        L1_header[5] = (byte) (crc & 0xff);
        L1_header[6] = (byte) ((sequenctId >> 8) & 0xff);
        L1_header[7] = (byte) (sequenctId & 0xff);
        return L1_header;
    }

    public void L1UnPack(byte[] msg){
        if((msg == null) ||(msg.length < 8)){//err packet
            return;
        }else if(msg.length == 8){//ack packet
            setVersion(msg);
            setAckFlag(msg);
            setErrFlag(msg);
            setPayloadLength(msg);
            setCrc(msg);
            setSequenceId(msg);
            setReserve(msg);
        }else{
            setVersion(msg);
            setAckFlag(msg);
            setErrFlag(msg);
            setPayloadLength(msg);
            setCrc(msg);
            setSequenceId(msg);
            setReserve(msg);
            byte[] payload = new byte[msg.length-8];
            System.arraycopy(msg, 8, payload, 0, msg.length - 8);
            setL2Playload(payload);
        }
    }


    private int CRC8(byte [] buffer){
        int crc =0xFF;
        int length = buffer.length;
        for (int i = 0; i < length; i++) {
            crc^= buffer[i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                if((crc & 1) !=0){
                    crc >>=1;
                    crc^=0xB8;
                }else{
                    crc>>=1;
                }
            }
        }
        return Integer.valueOf(crc);
    }


    @Override
    public String toString() {
        return "BLE_Bean{" +
                "HEADER_SIZE=" + HEADER_SIZE +
                ", HEADER_VERSION=" + HEADER_VERSION +
                ", mark=" + mark +
                ", version=" + version +
                ", errFlag=" + errFlag +
                ", ackFlag=" + ackFlag +
                ", length=" + length +
                ", crc=" + crc +
                ", sequenceId=" + sequenceId +
                ", payl2load=" + Arrays.toString(payl2load) +
                ", reserve=" + reserve +
                '}';
    }


    public static byte[] getL2Payload(byte[]  bytes){
        if(bytes.length > 8){
            byte[] payload = new byte[bytes.length-8];
            System.arraycopy(bytes, 8, payload, 0, bytes.length - 8);  // 去掉包头
            return payload;
        }else{
            return bytes;
        }

    }
}
