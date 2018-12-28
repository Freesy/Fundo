package com.szkct.weloopbtsmartdevice.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import java.util.ArrayList;
import java.util.List;

public class IntentWrapper {

    //Android 7.0+ Doze 模式
    protected static final int DOZE = 98;
    //华为 自启管理
    protected static final int HUAWEI = 99;
    //华为 锁屏清理
    protected static final int HUAWEI_GOD = 100;
    //小米 自启动管理
    protected static final int XIAOMI = 101;
    //小米 神隐模式
    protected static final int XIAOMI_GOD = 102;
    //三星 5.0/5.1 自启动应用程序管理
    protected static final int SAMSUNG_L = 103;
    //魅族 自启动管理
    protected static final int MEIZU = 104;
    //魅族 待机耗电管理
    protected static final int MEIZU_GOD = 105;
    //Oppo 自启动管理
    protected static final int OPPO = 106;
    //三星 6.0+ 未监视的应用程序管理
    protected static final int SAMSUNG_M = 107;
    //Oppo 自启动管理(旧版本系统)
    protected static final int OPPO_OLD = 108;
    //Vivo 允许后台高耗电
    protected static final int VIVO_GOD = 109;
    //金立 应用自启
    protected static final int GIONEE = 110;
    //乐视 自启动管理
    protected static final int LETV = 111;
    //乐视 应用保护
    protected static final int LETV_GOD = 112;
    //酷派 自启动管理
    protected static final int COOLPAD = 113;
    //联想 后台管理
    protected static final int LENOVO = 114;
    //联想 后台耗电优化
    protected static final int LENOVO_GOD = 115;
    //中兴 自启管理
    protected static final int ZTE = 116;
    //中兴 锁屏加速受保护应用
    protected static final int ZTE_GOD = 117;

    //努比亚 自启管理
    protected static final int NUBIYA = 118;
    //努比亚 锁屏加速受保护应用
    protected static final int NUBIYA_GOD = 119;

    //Vivo   加速白名单 设置
    protected static final int VIVO = 120;

    protected static List<IntentWrapper> sIntentWrapperList;

    public static List<IntentWrapper> getIntentWrapperList() {
        if (sIntentWrapperList == null) {
            sIntentWrapperList = new ArrayList<>();
            
            //Android 7.0+ Doze 模式   TODO   ---  6.0 以上 即有此权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {    //  Build.VERSION_CODES.N ---- 24    M--23
//                PowerManager pm = (PowerManager) DaemonEnv.sApp.getSystemService(Context.POWER_SERVICE);   // 华为 7.0
                PowerManager pm = (PowerManager) BTNotificationApplication.getInstance().getSystemService(Context.POWER_SERVICE);
//                boolean ignoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(DaemonEnv.sApp.getPackageName());   // 是否忽略电池优化
                boolean ignoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(BTNotificationApplication.getInstance().getPackageName());
                if (!ignoringBatteryOptimizations) {
                    Intent dozeIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);  //Android 系统广播
//                    dozeIntent.setData(Uri.parse("package:" + DaemonEnv.sApp.getPackageName()));
                    dozeIntent.setData(Uri.parse("package:" + BTNotificationApplication.getInstance().getPackageName()));
                    sIntentWrapperList.add(new IntentWrapper(dozeIntent, DOZE));
                }

            }

            //华为 自启管理
            Intent huaweiIntent = new Intent();
            huaweiIntent.setAction("huawei.intent.action.HSM_BOOTAPP_MANAGER");
            sIntentWrapperList.add(new IntentWrapper(huaweiIntent, HUAWEI));

            //华为 锁屏清理             componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            Intent huaweiGodIntent = new Intent();
            huaweiGodIntent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            sIntentWrapperList.add(new IntentWrapper(huaweiGodIntent, HUAWEI_GOD));

            //小米 自启动管理
            Intent xiaomiIntent = new Intent();
            xiaomiIntent.setAction("miui.intent.action.OP_AUTO_START");
            xiaomiIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sIntentWrapperList.add(new IntentWrapper(xiaomiIntent, XIAOMI));

            //小米 神隐模式
            Intent xiaomiGodIntent = new Intent();
            xiaomiGodIntent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
//            xiaomiGodIntent.putExtra("package_name", DaemonEnv.sApp.getPackageName());     // 内存优化白名单
            xiaomiGodIntent.putExtra("package_name", BTNotificationApplication.getInstance().getPackageName());
            xiaomiGodIntent.putExtra("package_label", getApplicationName());
            sIntentWrapperList.add(new IntentWrapper(xiaomiGodIntent, XIAOMI_GOD));

            //三星 5.0/5.1 自启动应用程序管理
//            Intent samsungLIntent = DaemonEnv.sApp.getPackageManager().getLaunchIntentForPackage("com.samsung.android.sm");
            Intent samsungLIntent =  BTNotificationApplication.getInstance().getPackageManager().getLaunchIntentForPackage("com.samsung.android.sm");
            if (samsungLIntent != null) sIntentWrapperList.add(new IntentWrapper(samsungLIntent, SAMSUNG_L));

            //三星 6.0+ 未监视的应用程序管理
            Intent samsungMIntent = new Intent();
            samsungMIntent.setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.BatteryActivity"));
            sIntentWrapperList.add(new IntentWrapper(samsungMIntent, SAMSUNG_M));

            //魅族 自启动管理
           /* Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");  //     componentName = new ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity");  todo  ----  自启动管理
            meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
//            meizuIntent.putExtra("packageName", DaemonEnv.sApp.getPackageName());
            meizuIntent.putExtra("packageName", BTNotificationApplication.getInstance().getPackageName());
            sIntentWrapperList.add(new IntentWrapper(meizuIntent, MEIZU));*/

            Intent meizuIntent = new Intent();
            meizuIntent.setComponent(ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity"));
            sIntentWrapperList.add(new IntentWrapper(meizuIntent, MEIZU));

            //魅族 待机耗电管理
            Intent meizuGodIntent = new Intent();    //  componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");  todo  ----  通知应用权限
            meizuGodIntent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.powerui.PowerAppPermissionActivity"));
            sIntentWrapperList.add(new IntentWrapper(meizuGodIntent, MEIZU_GOD));

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            if (getMobileType().endsWith("OPPO")) { // OPPO R8205
//                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
//                Intent OPPO = new Intent();
//                OPPO.setClassName("com.oppo.safe/.permission.startup", "StartupAppListActivity");
//                if (context.getPackageManager().resolveActivity(OPPO, 0) == null) {
//                    componentName =ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
//                }
//            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Oppo 点击耗电保护
            Intent oppoIntent = new Intent();  // com.coloros.safecenter/.startupapp.StartupAppListActivity
//            oppoIntent.setComponent(ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity"));   //oppo R9 OK
//            oppoIntent.setComponent(ComponentName.unflattenFromString("com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")); // 耗电保护详情页，无法引导
            oppoIntent.setComponent(ComponentName.unflattenFromString("com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerConsumptionActivity"));  // 设置的 电池页面 可以引导   （点击耗电保护  分动 （后台冻结，检测））
            sIntentWrapperList.add(new IntentWrapper(oppoIntent, OPPO));

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            if (getMobileType().endsWith("OPPO")) { // OPPO R8205
//                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");         // TODO  ---  ，自启动   OPPO R11 不支持     OPPOR9 --- 有自启动管理
//                Intent OPPO = new Intent();
//                OPPO.setClassName("com.oppo.safe/.permission.startup", "StartupAppListActivity");
//                if (context.getPackageManager().resolveActivity(OPPO, 0) == null) {
//                    componentName =ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
//                }
//            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Oppo 自启动管理(旧版本系统)
           /* Intent oppoOldIntent = new Intent();
//            oppoOldIntent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.permission.startup.StartupAppListActivity"));
//            oppoOldIntent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.startupapp.StartupAppListActivity"));

            oppoOldIntent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.startupapp.StartupAppListActivity"));
            sIntentWrapperList.add(new IntentWrapper(oppoOldIntent, OPPO_OLD));*/

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            //Vivo TODO   设置 加速白名单 设置
            Intent vivoIntent = new Intent();  //  componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
            vivoIntent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));   // todo  ----  vivo X7Plus OK （5.1.1）  vivi--- Y13iL (4.4)   OK
//            componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");    //todo   vivo --- 自启动管理   vivo X7Plus  引导不了
            sIntentWrapperList.add(new IntentWrapper(vivoIntent, VIVO));
            ////////////////////////////////////////////////////////////

            //Vivo TODO   设置 允许后台高耗电
            Intent vivoGodIntent = new Intent();
            vivoGodIntent.setComponent(new ComponentName("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity"));     // todo  ----  vivo X7Plus OK （5.1.1）  vivi--- Y13iL (4.4)  引导不了
            sIntentWrapperList.add(new IntentWrapper(vivoGodIntent, VIVO_GOD));

            //金立 应用自启
            Intent gioneeIntent = new Intent();
            gioneeIntent.setComponent(new ComponentName("com.gionee.softmanager", "com.gionee.softmanager.MainActivity"));
            sIntentWrapperList.add(new IntentWrapper(gioneeIntent, GIONEE));

            //乐视 自启动管理
            Intent letvIntent = new Intent();
            letvIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            sIntentWrapperList.add(new IntentWrapper(letvIntent, LETV));

            //乐视 应用保护
            Intent letvGodIntent = new Intent();
            letvGodIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.BackgroundAppManageActivity"));
            sIntentWrapperList.add(new IntentWrapper(letvGodIntent, LETV_GOD));

            //酷派 自启动管理
            Intent coolpadIntent = new Intent();
            coolpadIntent.setComponent(new ComponentName("com.yulong.android.security", "com.yulong.android.seccenter.tabbarmain"));
            sIntentWrapperList.add(new IntentWrapper(coolpadIntent, COOLPAD));

            //联想 后台管理
            Intent lenovoIntent = new Intent();
            lenovoIntent.setComponent(new ComponentName("com.lenovo.security", "com.lenovo.security.purebackground.PureBackgroundActivity"));
            sIntentWrapperList.add(new IntentWrapper(lenovoIntent, LENOVO));

            //联想 后台耗电优化
            Intent lenovoGodIntent = new Intent();
            lenovoGodIntent.setComponent(new ComponentName("com.lenovo.powersetting", "com.lenovo.powersetting.ui.Settings$HighPowerApplicationsActivity"));
            sIntentWrapperList.add(new IntentWrapper(lenovoGodIntent, LENOVO_GOD));

            //中兴 自启管理
            Intent zteIntent = new Intent();
            zteIntent.setComponent(new ComponentName("com.zte.heartyservice", "com.zte.heartyservice.autorun.AppAutoRunManager"));
            sIntentWrapperList.add(new IntentWrapper(zteIntent, ZTE));

            //中兴 锁屏加速受保护应用
            Intent zteGodIntent = new Intent();
            zteGodIntent.setComponent(new ComponentName("com.zte.heartyservice", "com.zte.heartyservice.setting.ClearAppSettingsActivity"));
            sIntentWrapperList.add(new IntentWrapper(zteGodIntent, ZTE_GOD));

            ///////////////////////////////////////////////////////////////////////////////////////////
            //努比亚 自启管理
            Intent nubiyaIntent = new Intent();
            nubiyaIntent.setComponent(ComponentName.unflattenFromString("cn.nubia.security2/cn.nubia.security.appmanage.selfstart.ui.SelfStartActivity"));
            sIntentWrapperList.add(new IntentWrapper(nubiyaIntent, NUBIYA));

            //努比亚 锁屏加速受保护应用
            Intent nubiyaGodIntent = new Intent();     //todo ---- 引导到努比亚手机的 耗电保护白名单 页面 待研究中 。。。。。     cn.nubia.security2/cn.nubia.security.powermanage.ui.AbnormalPowerWhiteListActivity
            nubiyaGodIntent.setComponent(ComponentName.unflattenFromString("cn.nubia.security2/cn.nubia.security.powermanage.ui.PowerManageActivity"));
            sIntentWrapperList.add(new IntentWrapper(nubiyaGodIntent, NUBIYA_GOD));
            ////////////////////////////////////////////////////////////////////////////
        }
        return sIntentWrapperList;
    }

    protected static String sApplicationName;

    public static String getApplicationName() {
        if (sApplicationName == null) {
            PackageManager pm;
            ApplicationInfo ai;
            try {
//                pm = DaemonEnv.sApp.getPackageManager();
//                ai = pm.getApplicationInfo(DaemonEnv.sApp.getPackageName(), 0);
                pm = BTNotificationApplication.getInstance().getPackageManager();
                ai = pm.getApplicationInfo(BTNotificationApplication.getInstance().getPackageName(), 0);
                sApplicationName = pm.getApplicationLabel(ai).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                sApplicationName = BTNotificationApplication.getInstance().getPackageName();
            }
        }

        String languageLx = Utils.getLanguage();
        if (languageLx.equals("zh")) {  // en
            sApplicationName = "分动";
        }

        return sApplicationName;
    }

    /**
     * 处理白名单.
     * @return 弹过框的 IntentWrapper.
     */
    @NonNull
    public static List<IntentWrapper> whiteListMatters(final Activity a, String reason) {   // this, "轨迹跟踪服务的持续运行"
        try{
            List<IntentWrapper> showed = new ArrayList<>();
//        if (reason == null) reason = "Fundo应用保活";    //       getString(R.string.app_keepAlive)
            if (reason == null) reason = BTNotificationApplication.getInstance().getString(R.string.app_keepAlive);
            List<IntentWrapper> intentWrapperList = getIntentWrapperList();    // todo ---- 获取白名单启动 Intent
            for (final IntentWrapper iw : intentWrapperList) {    // todo   ---   $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                //如果本机上没有能处理这个Intent的Activity，说明不是对应的机型，直接忽略进入下一次循环。
                if (!iw.doesActivityExists()) continue;
                switch (iw.type) {
                    case DOZE:   //todo ----  //Android 7.0+ Doze 模式  (其实 6.0 开始 就有 忽略电池优化的 操作)      -----  该属性设置过后，不会重复提示
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // Build.VERSION_CODES.N(24)    M()23
                            PowerManager pm = (PowerManager) a.getSystemService(Context.POWER_SERVICE);
                            if (pm.isIgnoringBatteryOptimizations(a.getPackageName())) break;
                            new AlertDialog.Builder(a)
                                    .setCancelable(false)
                                    .setTitle(a.getString(R.string.sweet_warn))  // + getApplicationName() + a.getString(R.string.app_battma)
                                    .setMessage(reason + a.getString(R.string.app_need) +    //  + getApplicationName() + a.getString(R.string.app_hlmd)
                                            a.getString(R.string.app_dhkxzshi))//todo  -----   7.0以上   忽略电池优化
                                    .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int w) {
                                            iw.startActivitySafely(a);  // Intent { act=android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS dat=package:com.kct.fundo.btnotification }   电池优化  ---- OK oppo7.0
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            showed.add(iw);
                        }
                        break;
                    case HUAWEI:  // todo v---- //华为 自启管理      华为6.0   OK
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))  //     "需要允许 " + getApplicationName() + " 自动启动"
                                .setMessage(reason + a.getString(R.string.app_setting_ziqidong) +  // "需要允许 " + getApplicationName() + " 的自动启动。\n\n"
                                        a.getString(R.string.app_setting_ziqidongkg))  // "请点击『确定』，在弹出的『自启管理』中，将 " + getApplicationName() + " 对应的开关打开。"
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;
                    case ZTE_GOD:  //todo ---- //中兴 锁屏加速受保护应用
                    case HUAWEI_GOD: // todo ----  //华为 锁屏清理
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))  // getApplicationName() + " 需要加入锁屏清理白名单"
                                .setMessage(reason + a.getString(R.string.app_huawei_bmd) +
                                        a.getString(R.string.app_huawei_bmd_setting))
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;
                    case XIAOMI_GOD:  // todo ---     //小米（省电白名单） 神隐模式
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))    // a.getString(R.string.sweet_warn) 9999999999999999999999999   "需要设置 " + getApplicationName() + " 的省电白名单吗？"
                                .setMessage(a.getString(R.string.app_setting_baimingdan) +
                                        a.getString(R.string.app_setting_baimingdan_caozuo))     //TODO 小米白名单设置引导    "请点击『确定』，在弹出的 " + getApplicationName() + " 设置中，选择『无限制』，然后选择『允许定位』。"
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })    //todo    
                                .show();
                        showed.add(iw);
                        break;

             /*   case SAMSUNG_L:  // todo ----   //三星 5.0/5.1 自启动应用程序管理    三星手机暂无内存优化的东西
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName() + " 的自启动")
                            .setMessage(reason + "需要 " + getApplicationName() + " 在屏幕关闭时继续运行。\n\n" +
                                    "请点击『确定』，在弹出的『智能管理器』中，点击『内存』，选择『自启动应用程序』选项卡，将 " + getApplicationName() + " 对应的开关打开。")   //todo  ---  sanxing  6.0
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int w) {
                                    iw.startActivitySafely(a);
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case SAMSUNG_M:  // todo ---   //三星 6.0+ 未监视的应用程序管理
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName() + " 的自启动")
                            .setMessage(reason + "需要 " + getApplicationName() + " 在屏幕关闭时继续运行。\n\n" +
                                    "请点击『确定』，在弹出的『电池』页面中，点击『未监视的应用程序』->『添加应用程序』，勾选 " + getApplicationName() + "，然后点击『完成』。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int w) {
                                    iw.startActivitySafely(a);
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;*/
                    case MEIZU:  //todo ----   //魅族 自启动管理
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))
                                .setMessage(reason + a.getString(R.string.app_vivo_htbh) +
                                        a.getString(R.string.app_meizu_setting))
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;
                   /* case MEIZU_GOD: // todo ----  //魅族 待机耗电管理   ----20171208 -- 暂无手机验证，注释掉
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(getApplicationName() + " 需要在待机时保持运行")
                                .setMessage(reason + "需要 " + getApplicationName() + " 在待机时保持运行。\n\n" +
                                        "请点击『确定』，在弹出的『待机耗电管理』中，将 " + getApplicationName() + " 对应的开关打开。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;*/
                    case ZTE:        //中兴 自启管理
                    case LETV:      //乐视 自启动管理
                    case XIAOMI:    //小米 自启动管理
                    case NUBIYA:      //TODO  努比亚 add  20171201   自启动    ---- OK
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))  // "需要允许 " + getApplicationName() + " 的自启动吗？"
                                .setMessage(reason + a.getString(R.string.app_setting_ziqidong) +    // "需要 " + getApplicationName() + " 加入到自启动白名单。\n\n"
                                        a.getString(R.string.app_setting_ziqidongkg))    // "请点击『确定』，在弹出的『自启动管理』中，将 " + getApplicationName() + " 对应的开关打开。"
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {    // Intent { cmp=com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity }   todo   --- 耗电保护页面无法进入
                                        iw.startActivitySafely(a); // Intent { cmp=com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity } -- xxxx
                                        dialog.dismiss();
                                    }   // todo --- 安全地启动一个Activity
                                })
                                .show();
                        showed.add(iw);
                        break;

                    case OPPO:       //Oppo 自启动管理
                    case OPPO_OLD:   //Oppo 自启动管理(旧版本系统)     // 设置的 电池页面 可以引导   （点击耗电保护  分动 （后台冻结，检测））
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))    // "需要允许 " + getApplicationName() + " 的耗电保护吗？"
                                .setMessage(reason + a.getString(R.string.app_oppo_bmd) +  // "需要关闭 " + getApplicationName() + " 耗电保护开关。\n\n"
                                        a.getString(R.string.app_oppo_bmd_setting)) // "请点击『确定』，在弹出的『电池页面』中，点击耗电保护，将 " + getApplicationName() + " 对应的开关关闭。"
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {    // Intent { cmp=com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity }   todo   --- 耗电保护页面无法进入
                                        iw.startActivitySafely(a); // Intent { cmp=com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity } -- xxxx
                                        dialog.dismiss();
                                    }   // todo --- 安全地启动一个Activity
                                })
                                .show();
                        showed.add(iw);

                        break;

                  /*  case COOLPAD:   //酷派 自启动管理    ----20171208 -- 暂无手机验证，注释掉
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle("需要允许 " + getApplicationName() + " 的自启动")
                                .setMessage(reason + "需要允许 " + getApplicationName() + " 的自启动。\n\n" +
                                        "请点击『确定』，在弹出的『酷管家』中，找到『软件管理』->『自启动管理』，取消勾选 " + getApplicationName() + "，将 " + getApplicationName() + " 的状态改为『已允许』。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;*/

                    case VIVO:   //Vivo TODO    加速白名单  设置
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn)) // "需要设置 " + getApplicationName() + " 的加速白名单"
                                .setMessage(reason + a.getString(R.string.app_vivo_bmd) +
                                        a.getString(R.string.app_vivo_bmd_setting))
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;


                    case VIVO_GOD:   //Vivo TODO  允许 后台高耗电
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))   // "需要允许 " + getApplicationName() + " 的后台高耗电"
                                .setMessage(reason + a.getString(R.string.app_vivo_htbh) +
                                        a.getString(R.string.app_vivo_htbh_setting))
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;

                   /* case GIONEE:      //金立 应用自启     ----20171208 -- 暂无手机验证，注释掉
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(getApplicationName() + " 需要加入应用自启和绿色后台白名单")
                                .setMessage(reason + "需要允许 " + getApplicationName() + " 的自启动和后台运行。\n\n" +
                                        "请点击『确定』，在弹出的『系统管家』中，分别找到『应用管理』->『应用自启』和『绿色后台』->『清理白名单』，将 " + getApplicationName() + " 添加到白名单。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;*/
                    case LETV_GOD:        //乐视 应用保护
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))        // "需要禁止 " + getApplicationName() + " 被自动清理"
                                .setMessage(reason + a.getString(R.string.app_letv_bmd) +
                                        a.getString(R.string.app_letv_bmd_setting))
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;

                   /* case LENOVO:       //联想 后台管理     ----20171208 -- 暂无手机验证，注释掉
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle("需要允许 " + getApplicationName() + " 的后台运行")
                                .setMessage(reason + "需要允许 " + getApplicationName() + " 的后台自启、后台 GPS 和后台运行。\n\n" +
                                        "请点击『确定』，在弹出的『后台管理』中，分别找到『后台自启』、『后台 GPS』和『后台运行』，将 " + getApplicationName() + " 对应的开关打开。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;*/

                    case LENOVO_GOD:      //联想 后台耗电优化
                    case NUBIYA_GOD:      //TODO  努比亚 add  20171201   耗电保护白名单   ----进不来
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle(a.getString(R.string.sweet_warn))  // "需要关闭 " + getApplicationName() + " 的后台耗电优化"
                                .setMessage(reason + a.getString(R.string.app_nubiya_bmd) +  // "需要设置 " + getApplicationName() + " 的电量节省相关选项。\n\n"
                                        a.getString(R.string.app_nubiya_bmd_setting)) // "请点击『确定』，在弹出的『电量节省』页面中，设置相应的选项。"
                                .setPositiveButton(a.getString(R.string.app_sure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int w) {
                                        iw.startActivitySafely(a);
                                        int dddd = 66;
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        showed.add(iw);
                        break;

//                default:
//                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                    BTNotificationApplication.getInstance().startActivity(intent);
//                    break;

                }
            }
            return showed;
        }catch (Exception E){
            List<IntentWrapper> showed = new ArrayList<>();
            E.printStackTrace();

            return showed;
        }
    }

    /**
     * 防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
     */
    public static void onBackPressed(Activity a) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        a.startActivity(launcherIntent);
    }

    protected Intent intent;
    protected int type;

    protected IntentWrapper(Intent intent, int type) {
        this.intent = intent;
        this.type = type;
    }

    /**
     * 判断本机上是否有能处理当前Intent的Activity
     */
    protected boolean doesActivityExists() {
//        if (!DaemonEnv.sInitialized) return false;
        PackageManager pm = BTNotificationApplication.getInstance().getPackageManager();    //  BTNotificationApplication.getInstance()
//        PackageManager pm = DaemonEnv.sApp.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    /**
     * 安全地启动一个Activity
     */
    protected void startActivitySafely(Activity activityContext) {
        try { activityContext.startActivity(intent); } catch (Exception e) { e.printStackTrace(); }
    }
}
