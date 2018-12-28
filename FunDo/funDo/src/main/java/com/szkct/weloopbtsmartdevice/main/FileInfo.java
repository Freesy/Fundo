package com.szkct.weloopbtsmartdevice.main;

/**
 * Created by vers on 2016/12/12.
 */

public class FileInfo {    // TODO  --- bk 专用
    private String mPath, mFileName;
    private boolean isCheck;

    public FileInfo(String path, String filename) {
        mPath = path;
        mFileName = filename;
    }

    public String getPath()
    {
        return mPath;
    }

    public void setChecked(boolean flag)
    {
        isCheck = flag;
    }

    public boolean getIsCheck()
    {
        return isCheck;
    }

    public String getFileName()
    {
        return mFileName;
    }

}
