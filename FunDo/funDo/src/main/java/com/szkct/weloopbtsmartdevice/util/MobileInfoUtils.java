package com.szkct.weloopbtsmartdevice.util;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mtk.app.notification.NotificationAppListActivity;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

/**
 * Mobile Info Utils
 * create by wyl at 2017年9月8日
 */
public class MobileInfoUtils {

    /**
     * Get Mobile Type
     *
     * @return
     */
    public static String getMobileType() {
        return Build.MANUFACTURER;
    }

    /**
     * GoTo Open Self Setting Layout
     * Compatible Mainstream Models
     *
     * @param context
     */
    static Intent intent = new Intent();
    public static void jumpStartInterface(Context context,int type) {

        if(1==type){
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("HLQ_Struggle", "******************Type：" + getMobileType());
            ComponentName componentName = null;
            if (getMobileType().endsWith("Xiaomi")) { // red Note4
                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
            } else if (getMobileType().endsWith("Letv")) { // Letv 2
                intent.setAction("com.letv.android.permissionautoboot");
            } else if (getMobileType().endsWith("samsung")) { // samsung
                componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");
            }else if (getMobileType().endsWith("HUAWEI")) { // HUAWEI
                componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            } else if (getMobileType().endsWith("vivo")) { // VIVO
               componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
            } else if (getMobileType().endsWith("Meizu")) { //Meizu
                componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");
            } else if (getMobileType().endsWith("OPPO")) { // OPPO R8205
                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
                Intent OPPO = new Intent();
                OPPO.setClassName("com.oppo.safe/.permission.startup", "StartupAppListActivity");
                if (context.getPackageManager().resolveActivity(OPPO, 0) == null) {
                    componentName =ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
                }
            } else if (getMobileType().endsWith("ulong")) { // 360
                componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");
            } else if (getMobileType().endsWith("nubia")) { // nubiya
                componentName = ComponentName.unflattenFromString("cn.nubia.security2/cn.nubia.security.appmanage.selfstart.ui.SelfStartActivity");
            } else {
                if (Build.VERSION.SDK_INT >= 9) {
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                    intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                }
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(BTNotificationApplication.getInstance(),BTNotificationApplication.getInstance().getResources().getString(R.string.notsupported),Toast.LENGTH_SHORT).show();
           Log.e("CUOWU","SSSSSSSSSSSSSSSSSSSSSSSSSS");
            /*intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);*/
        }
    }else{
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName componentName = null;
                if (getMobileType().endsWith("Xiaomi")) { // red Note4
                    componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                } else if (getMobileType().endsWith("samsung")) { // samsung Note5
                    componentName = new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
                }else if (getMobileType().endsWith("HUAWEI")) { // HUAWEI
                    componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                } else if (getMobileType().equalsIgnoreCase("vivo")) { // VIVO
                    componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
                } else if (getMobileType().endsWith("Meizu")) { //Meizu
                    componentName = new ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity");
                } else if (getMobileType().endsWith("OPPO")) { // OPPO R8205
                    Toast.makeText(BTNotificationApplication.getInstance(),BTNotificationApplication.getInstance().getResources().getString(R.string.notsupported),Toast.LENGTH_SHORT).show();
                   /* componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
                    Intent OPPO = new Intent();
                    OPPO.setClassName("com.oppo.safe/.permission.startup", "StartupAppListActivity");
                    if (context.getPackageManager().resolveActivity(OPPO, 0) == null) {
                        componentName =ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
                    }*/
                } else if (getMobileType().endsWith("ulong")) { // 360
                    componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");
                }else if (getMobileType().endsWith("nubia")) { // nubia
                    componentName = ComponentName.unflattenFromString("cn.nubia.processmanager/.ui.ProcessWhiteListActivity");
                } else {
                    if (Build.VERSION.SDK_INT >= 9) {
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                        intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                    }
                }
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(BTNotificationApplication.getInstance(),BTNotificationApplication.getInstance().getResources().getString(R.string.notsupported),Toast.LENGTH_SHORT).show();
               /* intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);*/
            }
        }
    }
}