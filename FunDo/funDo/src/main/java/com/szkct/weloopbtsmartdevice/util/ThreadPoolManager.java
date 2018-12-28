package com.szkct.weloopbtsmartdevice.util;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/12/4
 * 描述: ${VERSION}
 * 修订历史：
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理
 */
public class ThreadPoolManager {
    /**
     *  单例私有化构造方法
     */
    private static ThreadPoolManager mInstance = null;  // new ThreadPoolManager()
    public static ThreadPoolManager getInstance() {

        if(null == mInstance){
            mInstance = new ThreadPoolManager();
        }

        return mInstance;
    }


    private int corePoolSize;//核心线程池的数量，同时能够执行的线程数量
    private int maximumPoolSize;//最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
    private long keepAliveTime = 1;//存活时间
    private TimeUnit unit = TimeUnit.HOURS;
    private ThreadPoolExecutor executor = null;

//    private ExecutorService mExecutorService = null; // 线程池对象

    private ThreadPoolManager() {
       /* if(null == mExecutorService){

//            int dd  = Runtime.getRuntime().availableProcessors()*2+1;
//            android.util.Log.d("CPU", "处理器的个数为----->..." + dd);
            mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);
        }*/

//        corePoolSize = Runtime.getRuntime().availableProcessors()*2+1;
//        corePoolSize = Runtime.getRuntime().availableProcessors() + 2;
        corePoolSize = 3;
        maximumPoolSize = 5; //虽然maximumPoolSize用不到，但是需要赋值，否则报错

        if(null == executor){
            executor = new ThreadPoolExecutor(
                    corePoolSize, //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                    maximumPoolSize, //5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                    keepAliveTime, //表示的是maximumPoolSize当中等待任务的存活时间
                    unit,
                    new LinkedBlockingQueue<Runnable>(), //缓冲队列，用于存放等待任务，Linked的先进先出
                    Executors.defaultThreadFactory(), //创建线程的工厂
                    new ThreadPoolExecutor.AbortPolicy() //用来对超出maximumPoolSize的任务的处理策略
            );
        }

    }
    /**
     * 执行任务
     */
    public void execute(Runnable runnable){
        if(runnable==null)return;

//        mExecutorService.execute(runnable);
        executor.execute(runnable);
    }
    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable runnable){
        if(runnable==null)return;
        executor.remove(runnable);
    }
}
