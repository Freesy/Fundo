package com.szkct.weloopbtsmartdevice.main;

/**
 * Created by vers on 2017/8/1.
 */

public class UUIDCheckList {
    private boolean isUUIDChecked, isNotifyChecked, isIndicateChecked;
    private  boolean canWrite, canNotify, canIndicate;

    public UUIDCheckList()
    {
        canIndicate = false;
        canNotify = false;
        canWrite = false;
        isUUIDChecked = false;
        isNotifyChecked = false;
        isIndicateChecked = false;
    }

    public void setWriteFlag (boolean flag)
    {
        canWrite = flag;
    }

    public void setNotifyFlag (boolean flag)
    {
        canNotify = flag;
    }

    public void setIndicateFlag (boolean flag)
    {
        canIndicate = flag;
    }


}
