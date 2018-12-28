package com.szkct.weloopbtsmartdevice.main;

/**
 * Created by vers on 2017/8/2.
 */

public class CheckboxEvent {

    private int mGroupPosition;
    private int mChildPosition;
    private int mIndex;
    private boolean mFlag;
    private int mDisconnect;

    public CheckboxEvent(int groupPosition, int childPostion, int index, boolean flag)
    {
        mGroupPosition = groupPosition;
        mChildPosition = childPostion;
        mIndex = index;
        mFlag = flag;
    }

    public CheckboxEvent(int x)
    {
        mDisconnect = x;
    }

    public CheckboxEvent(boolean flag)
    {
        mFlag = flag;
    }


    public int getGroupPosition()
    {
        return mGroupPosition;
    }

    public int getChildPostion()
    {
        return mChildPosition;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public boolean getFlag()
    {
        return mFlag;
    }

    public int getmDisconnect()
    {
        return mDisconnect;
    }
}
