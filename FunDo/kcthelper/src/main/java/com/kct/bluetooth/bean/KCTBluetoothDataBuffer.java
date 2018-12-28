package com.kct.bluetooth.bean;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kct.bluetooth.utils.HexUtil;
import com.kct.bluetooth.utils.LogUtil;

import java.util.ArrayList;
import java.util.UUID;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/10/20
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTBluetoothDataBuffer {

    private KCTBLE_L1Bean kctble_l1Bean;

    private byte[] bytes;    //byte数据流

    private boolean isReceive;

    public boolean burDataBegin;

    public boolean burDataEnd;

    private ArrayList<byte[]> list;

    private int dataSize;   //接收数据长度

    private int dataMaxSize;   //总数据长度

    private Handler handler;

    public KCTBluetoothDataBuffer(Handler handler){
        kctble_l1Bean = new KCTBLE_L1Bean();
        list = new ArrayList<byte[]>();
        this.handler = handler;
    }

    public synchronized Object setDataBuffer(byte[] buffer,UUID serviceUuid){
        if(null == buffer || buffer.length < 0){
            Log.d("[KCTBluetoothData]","buffer is null");
            clear();
            return null;
        }
        if(buffer[0] != KCTBLEConstant.PROTOCOL_MARK && !isReceive){
            Log.d("[KCTBluetoothData]","the buffer is error : " + buffer[0] + " ;  isR = " + isReceive);
            clear();
            return null;
        }else{
            if(buffer[0] == KCTBLEConstant.PROTOCOL_MARK && !isReceive){
                kctble_l1Bean.L1UnPack(buffer);
                setDataLength();
                checkData();
            }
            dataSize += buffer.length;
            Log.d("[KCTBluetoothData]","the data length = " + dataSize + " ; maxLength = " + dataMaxSize);
            LogUtil.d("[KCTBluetoothData]","the bytes length = " + bytes.length + " ; buffer = " + buffer.length );
            if(dataSize > bytes.length){
                Log.d("[KCTBluetoothData]","the data length is too long ");
                clear();
                return null;
            }
            if(dataSize - buffer.length >= bytes.length){
                Log.d("[KCTBluetoothData]","the data length is too long ArrayIndexOutOfBoundsException");
                clear();
                return null;
            }
            try {
                System.arraycopy(buffer, 0, bytes, dataSize - buffer.length, buffer.length);
            }catch (Exception e){
                Log.d("[KCTBluetoothData]","ArrayIndexOutOfBoundsException");
                clear();
                return null;
            }
            if(dataSize == dataMaxSize){
                if(burDataBegin){
                    list.add(bytes);
                    LogUtil.d("[KCTBluetoothData]","list add bytes");
                    updateDataBuffer(4);
                    isReceive = false;
                    dataMaxSize = 0;
                    dataSize = 0;

                    if(burDataEnd){
                        if(!checkBurData()){
                            return list;
                        }else {
                            clear();
                        }
                    }else{
                        if(serviceUuid.equals(KCTGattAttributes.RX_SERVICE_872_UUID)
                                || serviceUuid.equals(KCTGattAttributes.RX_SERVICE_872_UUID_SCAN)){
                            if(checkBufferData() && bytes.length > 13
                                    && bytes[8] == (byte)0x07 && bytes[10] == (byte)0x71) {
                                list.remove(list.size() - 1);
                                return bytes;
                            }
                        }
                        updateDataBuffer(5);
                    }
                }else {
                    if(checkBufferData()){
                        clear();
                        return bytes;
                    }else {
                        clear();
                    }
                }

            }else if(dataSize < dataMaxSize){
                isReceive = true;
            }
        }
        return null;
    }

    private void setDataLength(){
        dataMaxSize = kctble_l1Bean.getPayloadLength() + 8;
        bytes = new byte[dataMaxSize];
    }


    private boolean checkBufferData(){
        kctble_l1Bean.L1UnPack(bytes);
        if(kctble_l1Bean.getAckFlag() == 1 && kctble_l1Bean.getErrFlag() == 1){ //正确的ACK和ERR
            if(kctble_l1Bean.getL2Playload().length <= 200){
                if(kctble_l1Bean.getCrc() == HexUtil.CRC8(kctble_l1Bean.getL2Playload())){
                    Log.d("[KCTBluetoothData]","check buffer data is true");
                    return true;
                }else{
                    Log.d("[KCTBluetoothData]","the c is error :  getC = " + kctble_l1Bean.getCrc() + " ;  c = " + HexUtil.CRC8(kctble_l1Bean.getL2Playload()));
                    return false;
                }
            }else{
                Log.d("[KCTBluetoothData]","check buffer data is true");
                return true;
            }
        }else if(kctble_l1Bean.getAckFlag() == 0 && kctble_l1Bean.getErrFlag() == 1){  //设备端发送的命令
            return true;
        }else{
            Log.d("[KCTBluetoothData]","the a or e is error");
            return false;
        }
    }


    private boolean checkBurData() {
        boolean isError = false;                    //判断数据是否有对错
        for (int i = 0; i < list.size(); i++) {
            kctble_l1Bean.L1UnPack(list.get(i));
            if(!checkBufferData()){
                break;
            }
        }
        return isError;
    }


    private void checkData(){
        if(kctble_l1Bean.getReserve() == 1){  //有下一个数据包
            burDataBegin = true;
            updateDataBuffer(5);
        }else {
            if(burDataBegin){
                burDataEnd = true;
            }
        }
        Log.d("[KCTBluetoothData]","burDataBegin = " + burDataBegin + " ; burDataEnd = " + burDataEnd);
    }


    public void clear(){
        if(burDataBegin && !burDataEnd){
            return;
        }
        isReceive = false;
        burDataBegin = false;
        burDataEnd = false;
        if(list != null) {
            list.clear();
        }
        dataMaxSize = 0;
        dataSize = 0;
    }

    private void updateDataBuffer(int what){
        Message message = Message.obtain();
        message.what = what;
        handler.sendMessage(message);
    }

}
