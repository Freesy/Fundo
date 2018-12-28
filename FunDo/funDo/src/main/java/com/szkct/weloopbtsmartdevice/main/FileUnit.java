package com.szkct.weloopbtsmartdevice.main;

import android.os.Environment;

import java.io.File;

/**
 * Created by vers on 12/5/16.
 */

public class FileUnit {

    public static final String TAG = "FileUnit";

    public static String getScriptFilePath()
    {
        File localFile = new File(getFilePath() + "/script");
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        return getFilePath() + "/script";
    }

    public static String getReportFilePath()
    {
        File localFile = new File(getFilePath() + "/report");
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        return getFilePath() + "/report";
    }

    public static String getOTAFilePath()    // todo --- 获取本地固件包的路径
    {
        File localFile = new File(getSDPath() + "/Download");
        if (!localFile.exists()) {
            boolean aaa = localFile.mkdir();
        }
        return getSDPath() + "/Download";
    }

    private static String getFilePath() {
        File localFile = new File(getSDPath() + "/BLEMesh");
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        return getSDPath() + "/BLEMesh";
    }

    public static String getSDPath() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return Environment.getExternalStorageDirectory().toString();
        }
        else
        {
            return Environment.getDownloadCacheDirectory().toString();
        }
    }
}
