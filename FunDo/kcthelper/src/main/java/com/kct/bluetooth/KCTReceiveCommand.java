package com.kct.bluetooth;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kct.bluetooth.bean.KCTBluetoothDataBuffer;
import com.kct.bluetooth.utils.LogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/10/20
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTReceiveCommand extends Thread{


    /** False if you want to cancel this thread.	*/
    private boolean mIsRun;
    /**	What the Handler is traced to BleHandler is an input parameter when you get the instance of KCTReceiveCommand	*/
    private Handler mHandler;
    /** The inner lock */
    private Lock mInnerLock;
    /** The inner condition corresponding to the inner lock */
    private Condition mInnerCondition;

    private Queue<Object> nReceiveQueue;

    private KCTBluetoothDataBuffer dataBuffer;

    public KCTReceiveCommand(Handler handler){
        mIsRun = true;
        mInnerLock = new ReentrantLock();
        mInnerCondition = mInnerLock.newCondition();
        nReceiveQueue = new LinkedList<Object>();
        this.mHandler = handler;
        dataBuffer = new KCTBluetoothDataBuffer(handler);
    }

    public boolean addDataBuffer(byte[] buffer, UUID serviceUuid){
        if (null == buffer) {
            LogUtil.d("KCTCommand", "buffer is null");
            return false;
        }
        if (buffer.length <= 0) {
            LogUtil.d("KCTCommand", "buffer length is null");
            return false;
        }
        Object o = dataBuffer.setDataBuffer(buffer,serviceUuid);
        if(null == o){
            return false;
        }
        synchronized (nReceiveQueue) {
            nReceiveQueue.offer(o);
            mInnerLock.lock();
            mInnerCondition.signalAll();    // UnLock
            mInnerLock.unlock();
            return true;
        }
    }


    @Override
    public void run() {
        while (mIsRun){
            Object buffer = getMessageFromBuffer();
            if(null != buffer){
                if(buffer instanceof byte[]) {
                    broadcastUpdate(1, (byte[]) buffer);
                }else if(buffer instanceof ArrayList){
                    ArrayList<byte[]> list = (ArrayList<byte[]>) buffer;
                    for (int i = 0; i < list.size(); i++) {
                        broadcastUpdate(1, list.get(i));
                    }
                    dataBuffer.clear();
                }
            }else{
                mInnerLock.lock();
                try {
                    LogUtil.d("KCTCommand","waiting the Command.");
                    mInnerCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("KCTCommand","await() is fails.");
                }
                mInnerLock.unlock();
            }
        }
    }

    private Object getMessageFromBuffer(){
        synchronized(nReceiveQueue){
            if(!nReceiveQueue.isEmpty()) {
                return nReceiveQueue.poll();
            }else{
                return null;
            }
        }
    }

    private void broadcastUpdate(final int what, byte[] message){
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }


    public void cancel(){
        if(mInnerLock != null && mInnerCondition != null) {
            mInnerLock.lock();
            mInnerCondition.signalAll();    // UnLock
            mInnerLock.unlock();
        }

        mIsRun = false;
        nReceiveQueue.clear();
        dataBuffer.clear();
    }

    public void dataClear(){
        if(dataBuffer != null) {
            dataBuffer.clear();
        }
    }

    public KCTBluetoothDataBuffer getDataBuffer(){
        return dataBuffer;
    }
}
