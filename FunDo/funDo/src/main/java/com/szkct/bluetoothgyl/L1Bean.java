package com.szkct.bluetoothgyl;



/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/2/17
 * 描述: ${VERSION}
 * 修订历史：
 */
public class L1Bean {

    public final static int L1_HEADER_SIZE = 8;
    public final static int L1_HEADER_VERSION = 0;
    private int mark;
    private int version ;
    private int errFlag;
    private int ackFlag;
    private int length;//L1 length
    private int crc;
    private int sequenctId;
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

    public int getSequenctId() {
        return sequenctId;
    }

    private void setSequenctId(byte[] msg) {
        this.sequenctId = value(msg,6,2);
    }

    public int getReserve() {
        return reserve;
    }

    private void setReserve(byte[] msg) {
        this.reserve = (value(msg,1,1)&0x00000040)>>6;
    }

    /**
     * Ack
     */
    public byte[] AckPacket(int L1_sequence_id,boolean isErr) {
        if(isErr) {
            return L1Pack(1, 0, L1_sequence_id, null);
        }else {
            return L1Pack(0, 0, L1_sequence_id, null);
        }
    }

    public byte[] MessagePack(int sequenctId,byte[] l2Playload){
        return L1Pack(1,0,sequenctId,l2Playload);
    }

    public byte[] L1Pack(int err,int ack,int sequenctId,byte[] l2Playload){
        /**L1*/
        byte[] L1_header = new byte[L1_HEADER_SIZE];
        int playload_length = (l2Playload == null)?0:l2Playload.length;
        int crc = (l2Playload == null)?0: UtilsLX.CRC8(l2Playload);  // -49538   CRC8  getCrc
        if(crc < 0){
            crc = -crc;
        }
        int Reserve = 0;
        int ver = L1_HEADER_VERSION;
        /**L1的包头*/
        L1_header[0] = BleContants.PROTOCOL_MARK;
        L1_header[1] = (byte) ((Reserve << 6) | (err << 5) | (ack << 4) | (ver));
        L1_header[2] = (byte) ((playload_length >> 8) & 0xff);
        L1_header[3] = (byte) (playload_length & 0xff);
        L1_header[4] = (byte) ((crc >> 8) & 0xff);  // 9600    --- 37: 25             -49538 --- 62 : 3E    : -63
        L1_header[5] = (byte) (crc & 0xff);    // -128 : 80    9600--- 0X2580                126  : 7E      ；-126
        L1_header[6] = (byte) ((sequenctId >> 8) & 0xff);
        L1_header[7] = (byte) (sequenctId & 0xff);
        return L1_header;
    }

    public void L1UnPack(byte[] msg){
        if((msg == null) ||(msg.length < 8)){//err packet
            return;
        }else if(msg.length == 8){//ack packet
            setVersion(msg);
            //Log.d("[L1UnPack]getVersion:"+getVersion());
            setAckFlag(msg);
            //Log.d("[L1UnPack]getAckFlag:"+getAckFlag());
            setErrFlag(msg);
            //Log.d("[L1UnPack]getErrFlag:"+getErrFlag());
            setPayloadLength(msg);
            //Log.d("[L1UnPack]getPayloadLength:"+getPayloadLength());
            setCrc(msg);
            //Log.d("[L1UnPack]getCrc:"+getCrc16());
            setSequenctId(msg);
            //Log.d("[L1UnPack]getSequenctId:"+getSequenctId());
            setReserve(msg);
        }else{//data
            setVersion(msg);
            //Log.d("[L1UnPack]getVersion:"+getVersion());
            setAckFlag(msg);
            //Log.d("[L1UnPack]getAckFlag:"+getAckFlag());
            setErrFlag(msg);
            //Log.d("[L1UnPack]getErrFlag:"+getErrFlag());
            setPayloadLength(msg);
            //Log.d("[L1UnPack]getPayloadLength:"+getPayloadLength());
            setCrc(msg);
            //Log.d("[L1UnPack]getCrc:"+getCrc16());
            setSequenctId(msg);
            //Log.d("[L1UnPack]getSequenctId:"+getSequenctId());
            setReserve(msg);
            byte[] payload = new byte[msg.length-8];
            /*if(getPayloadLength() != msg.length-8){
                return;//err packet
            }*/
            System.arraycopy(msg, 8, payload, 0, msg.length - 8);
            setL2Playload(payload);
            //Log.d("[L1UnPack]getL1Playload:"+Util.bytesToHexString(getL1Playload()));
        }
    }


    public static int getPayloadLengths(byte[]  bytes){
        int tmp = 0x00000000;
        int len = 2;
        for(int i =0;i<2;i++){
            len--;
            tmp |= (bytes[i+2]&0xFF)<<(8*len);
        }
        return tmp;
    }

    public static byte[] getL2Payload(byte[]  bytes){
        if(bytes.length > 8){
            byte[] payload = new byte[bytes.length-8];
            System.arraycopy(bytes, 8, payload, 0, bytes.length - 8);  // 去掉包头
            return payload;
        }else{
            return null;
        }

    }

    public static int getL2Crc(byte[]  bytes){
        int tmp = 0x00000000;
        int len = 2;
        for(int i =0;i<2;i++){
            len--;
            tmp |= (bytes[i+4]&0xFF)<<(8*len);
        }
        return tmp;

    }

    public static int getL1SequenctId(byte[]  bytes){
        int tmp = 0x00000000;
        int len = 2;
        for(int i =0;i<2;i++){
            len--;
            tmp |= (bytes[i+6]&0xFF)<<(8*len);
        }
        return tmp;

    }

}
