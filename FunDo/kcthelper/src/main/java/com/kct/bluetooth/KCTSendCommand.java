package com.kct.bluetooth;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kct.bluetooth.bean.KCTBleCmdBean;
import com.kct.bluetooth.utils.HexUtil;
import com.kct.bluetooth.utils.LogUtil;

import java.util.LinkedList;
import java.util.Queue;
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

public class KCTSendCommand extends Thread{

    /** False if you want to cancel this thread.	*/
    private boolean mIsRun;

    /** The inner lock */
    private Lock mInnerLock;
    /** The inner condition corresponding to the inner lock */
    private Condition mInnerCondition;

    private Queue<KCTBleCmdBean> nSendQueue = new LinkedList<KCTBleCmdBean>();

    private static int sequenceId = 0;

    private Handler handler;

    private boolean isDownTimer = false;

    public KCTSendCommand(Handler handler){
        mIsRun = true;
        this.handler = handler;
        mInnerLock = new ReentrantLock();
        mInnerCondition = mInnerLock.newCondition();
        Log.d("KCTSend","KCTSend is run");
    }


    public synchronized void addCommand(byte[] bytes){
        if(bytes.length <= 0 || bytes == null){
            Log.d("KCTSend","buffer is null");
            return;
        }
        KCTBleCmdBean bean = new KCTBleCmdBean();
        sequenceId ++;
        bean.content = bytes;
        bean.retry = 0;
        bean.sequence_id = sequenceId;

        synchronized (nSendQueue) {
            nSendQueue.add(bean);
        }

        if(!isDownTimer) {
            mInnerLock.lock();
            mInnerCondition.signalAll();
            mInnerLock.unlock();
        }
    }

    @Override
    public void run() {
        while (mIsRun){
            KCTBleCmdBean bean = getDataBuffer();
            if(null != bean && !isDownTimer){
                updateDataBuffer(bean.content,2);
                if(!isDownTimer) {
                    updateDataBuffer(5);
                }
                isDownTimer = true;
            }
            mInnerLock.lock();
            try {
                LogUtil.d("KCTSend", "waiting the sendCommand.");
                mInnerCondition.await();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("KCTSend", "await() is fails.");
            } finally {
                mInnerLock.unlock();
            }
        }
    }

    /**
     * If you attempt to close the APP or when the connection has disconnected, you should
     * cancel the thread through calling this function.
     */
    public void cancel(){
        if(mInnerLock != null && mInnerCondition != null) {
            mInnerLock.lock();
            mInnerCondition.signalAll();    // UnLock
            mInnerLock.unlock();
        }

        mIsRun = false;
        nSendQueue.clear();
        Log.d("KCTSend","KCTSend is stop");
    }


    public KCTBleCmdBean getDataBuffer(){
        synchronized (nSendQueue) {
            KCTBleCmdBean bean = nSendQueue.peek();
            if (null == bean) {
                return null;
            }
            if (null == bean.content) {
                return null;
            }
            if (bean.content.length <= 0) {
                return null;
            }
            if (bean.retry > 2) {
                nSendQueue.poll();
            } else {
                bean.retry++;
            }
            bean = nSendQueue.peek();
            return bean;
        }
    }


    private void updateDataBuffer(byte[] bytes,int what){
        Message message = Message.obtain();
        message.what = what;
        message.obj = bytes;
        handler.sendMessage(message);
    }

    private void updateDataBuffer(int what){
        Message message = Message.obtain();
        message.what = what;
        handler.sendMessage(message);
    }


    public void reCancel(boolean isCancel){
        if(isCancel) {
            if(nSendQueue.peek() != null) {
                nSendQueue.poll();
            }
            LogUtil.d("KCTSend","timer is cancel");
            isDownTimer = false;
        }else{
            updateDataBuffer(3);
            updateDataBuffer(4);
            Log.d("KCTSend","again send command");
            isDownTimer = false;
        }
        if(mInnerLock != null && mInnerCondition != null) {
            mInnerLock.lock();
            mInnerCondition.signalAll();
            mInnerLock.unlock();
        }
    }



}
