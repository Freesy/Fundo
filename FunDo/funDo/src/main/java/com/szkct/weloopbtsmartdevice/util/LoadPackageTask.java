package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import com.mtk.app.notification.BlockList;
import com.mtk.app.notification.IgnoreList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.ta.utdid2.android.utils.AESUtils.TAG;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/7/25
 * 描述: ${VERSION}
 * 修订历史：
 */

public class LoadPackageTask extends AsyncTask<Void, Integer, Boolean> {

    private final Context mContext;

    private List<PackageInfo> mPersonalIgnoreAppList = null;

    private List<PackageInfo> mSystemIgnoreAppList = null;

    public LoadPackageTask(Context context) {
        Log.i(TAG, "LoadPackageTask(), Create LoadPackageTask!");
        mContext = context;
        mPersonalIgnoreAppList = new ArrayList<>();
        mSystemIgnoreAppList = new ArrayList<>();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        List<PackageInfo> packageList = mContext.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : packageList) {
            if(!packageInfo.packageName.contains(".mms") && !packageInfo.packageName.contains(".contacts")
                    && !packageInfo.packageName.contains(".incallui") && !packageInfo.packageName.contains("tencent.mm")
                    && !packageInfo.packageName.contains(".mobileqq") && !packageInfo.packageName.contains(".facebook")
                    && !packageInfo.packageName.contains(".instagram") && !packageInfo.packageName.contains(".linkedin")
                    && !packageInfo.packageName.contains(".whatsapp")){
                if (!Utils.isSystemApp(packageInfo.applicationInfo)) {
                    mPersonalIgnoreAppList.add(packageInfo);
                } else {
                    mSystemIgnoreAppList.add(packageInfo);
                }
            }
        }
        return true;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        HashSet<String> ignoreList = new HashSet<String>();
        HashSet<CharSequence> blockList = new HashSet<CharSequence>();
        for (int i = 0; i < mPersonalIgnoreAppList.size(); i++) {
            ignoreList.add(mPersonalIgnoreAppList.get(i).packageName);
            blockList.add(mPersonalIgnoreAppList.get(i).packageName);
        }
        for (int i = 0; i < mSystemIgnoreAppList.size(); i++) {
            ignoreList.add(mSystemIgnoreAppList.get(i).packageName);
            blockList.add(mSystemIgnoreAppList.get(i).packageName);
        }
        IgnoreList.getInstance().saveIgnoreList(ignoreList);
    }
}
