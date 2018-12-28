
package com.mtk.app.notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.Intent;
import com.kct.fundo.btnotification.R;
import com.szkct.adapter.ViewPagerAdapter;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainActivity;
import com.szkct.weloopbtsmartdevice.util.IntentWrapper;
import com.szkct.weloopbtsmartdevice.util.MobileInfoUtils;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NotificationAppListActivity extends AppCompatActivity implements OnPageChangeListener, OnClickListener {
    // Debugging
    private static final String TAG = "AppManager/NotificationAppList";

    // Tab tag enum
    private static final String TAB_TAG_PERSONAL_APP = "personal_app";   //个人应用

    private static final String TAB_TAG_SYSTEM_APP = "system_app";      //系统应用

    private LayoutInflater mInflater;

    private Context mContext;

    // View item filed
    private static final String VIEW_ITEM_INDEX = "item_index";

    private static final String VIEW_ITEM_ICON = "package_icon";

    private static final String VIEW_ITEM_TEXT = "package_text";

    private static final String VIEW_ITEM_CHECKBOX = "package_switch";

    private static final String VIEW_ITEM_NAME = "package_name"; // Only for
    // save to
    // ignore list

    private TabHost mTabHost = null;

    private ListView mPersonalAppListView;

    private ListView mSystemAppListView;

    private List<Map<String, Object>> mPersonalAppList = null;   //个人应用集合

    private List<Map<String, Object>> mBlockAppList = null;      //

    // For system app list
    private List<Map<String, Object>> mSystemAppList = null;

    private SystemAppListAdapter mSystemAppAdapter = null;

    // private int mPersonalAppSelectedCount = 0;
    private PersonalAppListAdapter mPersonalAppAdapter = null;

    //  private LinearLayout mLinearNotification;
    // private Toolbar toolbar;
    // For select all button
    private int mPersonalAppSelectedCount = 0;
    private int mSystemAppSelectedCount = 0;
    private Button mSelectAllPersonalAppButton = null;
    private Button mSelectAllSystemAppButton = null;
    ViewPager vPager;
    ViewPagerAdapter vpAdapter;
    ArrayList<View> views;
    private TextView null_ti, per_ti, sys_ti,tv_appbaohuo;

    private int arg = 0;

    private ToggleButton tb, tb2,tbKeepAlive;
    private Intent upservice;
    private PopupWindow popupwindow;

    private boolean isSMS = true;
    private boolean isCall = true;
    private boolean isPerson;
    private boolean isSystem;

    private boolean isClickSelectAllFirst = false; // 先点击了全选开关
    private boolean isClickSingleAppFirst = false; // 先点击了单选APP开关

    private boolean isListViewRefresh = false; // 只取一次开关的效果

    private boolean isClicked = false; //todo 改动的标志

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.notification_app_list);

        isSMS = (boolean) SharedPreUtil.getParam(NotificationAppListActivity.this, SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, true);
        isCall = (boolean) SharedPreUtil.getParam(NotificationAppListActivity.this, SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, true);

        Utils.getPhoneInfo();
        if("nubia".equals(Utils.mtyb)){
            final android.support.v7.app.AlertDialog.Builder normalDialog = new android.support.v7.app.AlertDialog.Builder(NotificationAppListActivity.this);
            normalDialog.setTitle(getResources().getString(R.string.sweet_warn));
            normalDialog.setMessage(getResources().getString(R.string.off_thenotification_switch));
            normalDialog.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity( new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        }
                    });
            normalDialog.show();
        }

        /**
         * 开启通知栏
         */
        if (!"Lenovo".equals(Utils.mtyb) || !"HUAWEI".equals(Utils.mtyb)) {
            if (!Utils.isEnabled(NotificationAppListActivity.this)) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivityForResult(intent,1);
            }
        }
        /**
         * 是否开启辅助服务设置可收到消息
         */
        upservice = new Intent(NotificationAppListActivity.this, NeNotificationService.class);
        updateServiceStatus(true);


        mContext = this;

        final LoadPackageTask loadPackageTask = new LoadPackageTask(this);
        loadPackageTask.execute();
        new Thread(){
            public void run() {
                try {
                    /**
                     * 在这里你可以设置超时的时间
                     * 切记：这段代码必须放到线程中执行，因为不放单独的线程中执行的话该方法会冻结UI线程
                     * 直接导致onPreExecute()方法中的弹出框不会立即弹出。
                     */
                    loadPackageTask.get(10000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                } catch (TimeoutException e) {
                    /**
                     * 如果在doInbackground中的代码执行的时间超出10000秒则会出现这个异常。
                     * 所以这里就成为你处理异常操作的唯一途径。
                     *
                     * 备注：这里是不能够处理UI操作的，如果处理UI操作则会出现崩溃异常。
                     * 你可以写一个Handler，向handler发送消息然后再Handler中接收消息并处理UI更新操作。
                     */
                }//请求超时
            }
        }.start();


        //是否有通知权限，优化后台运行
        if(Utils.notificationIsOpen(BTNotificationApplication.getInstance())!=true){
            Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.notnotification), Toast.LENGTH_SHORT).show();
        }


    }

    //更新消息服务
    private void updateServiceStatus(boolean start) {
        try{
            boolean bRunning = Utils.isServiceRunning(this, "com.mtk.app.notification.NeNotificationService");
            if (start && !bRunning) {
                this.startService(upservice);
            } else if(!start && bRunning) {
                this.stopService(upservice);
            }
            bRunning = Utils.isServiceRunning(this, "com.mtk.app.notification.NeNotificationService");
        }catch (Exception e){
            e.printStackTrace();
        }}


    private void init() {
        // TODO Auto-generated method stub
        views = new ArrayList<View>();
        View v1 = LayoutInflater.from(NotificationAppListActivity.this).inflate(R.layout.sysapplist, null);
        View v2 = LayoutInflater.from(NotificationAppListActivity.this).inflate(R.layout.sysapplist, null);
        null_ti = (TextView) findViewById(R.id.null_ti);
        per_ti = (TextView) findViewById(R.id.per_ti);
        sys_ti = (TextView) findViewById(R.id.sys_ti);

        tv_appbaohuo = (TextView) findViewById(R.id.tv_appbaohuo);

        String languageLx  = Utils.getLanguage();
        if (languageLx.equals("tr") || languageLx.equals("pl")) {
            per_ti.setTextSize(11);
            sys_ti.setTextSize(11);
            null_ti.setTextSize(11);
        }else if(languageLx.equals("ru")){
            per_ti.setTextSize(8);
            sys_ti.setTextSize(8);
            null_ti.setTextSize(8);
        } else if(!languageLx.equals("zh")){
            per_ti.setTextSize(12);
            sys_ti.setTextSize(12);
            null_ti.setTextSize(12);
        }

        if (!languageLx.equals("zh")) {
            tv_appbaohuo.setTextSize(12);
        }



        null_ti.setOnClickListener(this);
        per_ti.setOnClickListener(this);
        sys_ti.setOnClickListener(this);
        mPersonalAppListView = (ListView) v1.findViewById(R.id.list_notify_personal_app);
        mPersonalAppAdapter = new PersonalAppListAdapter(this);
        mPersonalAppListView.setAdapter(mPersonalAppAdapter);

        mSystemAppListView = (ListView) v2.findViewById(R.id.list_notify_personal_app);
        mSystemAppAdapter = new SystemAppListAdapter(this);
        mSystemAppListView.setAdapter(mSystemAppAdapter);
        views.add(v1);
        views.add(v2);
        vPager = (ViewPager) findViewById(R.id.applist_vp);
        vPager.setOnPageChangeListener(this);
        vpAdapter = new ViewPagerAdapter(views);
        vPager.setAdapter(vpAdapter);

        findViewById(R.id.back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISSELECT_ALLSYSTEMAPP,tb2.isChecked()? SharedPreUtil.YES:SharedPreUtil.NO);
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISSELECT_ALLPERSONAPP,tb.isChecked()? SharedPreUtil.YES:SharedPreUtil.NO);

                saveIgnoreList();
                saveBlockList();

                finish();
            }
        });
        findViewById(R.id.right_mu).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
              /*  if (android.os.Build.VERSION.SDK_INT < 18) {
                    startActivity(MainActivity.ACCESSIBILITY_INTENT);
                } else {
                    startActivity(MainActivity.NOTIFICATION_LISTENER_INTENT);
                }*/

                if (popupwindow != null&&popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                } else {
                    initmPopupWindowView();
                    popupwindow.showAsDropDown(v, 0, 5);
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////


        tbKeepAlive = (ToggleButton) findViewById(R.id.bt_allowbaohuo);

      /*  //        String mtype = android.os.Build.MODEL;    // PIC-AL00   ----    VTR-AL00  -- SM-C5000
        String mtyb = android.os.Build.BRAND;//手机品牌    HUAWEI   HUAWEI  ----   samsung  samsung
//        Log.d("lq3", "手机品牌：" + mtyb + " mtype:" + mtype);
        if( !StringUtils.isEmpty(mtyb)  && mtyb.equalsIgnoreCase("samsung")){   // todo  --- 不需要做隐藏
            tbKeepAlive.setVisibility(View.GONE);   // 应该是隐藏整个条目
        }else {
            tbKeepAlive.setVisibility(View.VISIBLE);
        }*/

        String isOpenKeepAlive = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISOPEN_APPKEEPALIVE, SharedPreUtil.NO);  //开启后台运行权限
        if (isOpenKeepAlive.equals("YES")) {
            tbKeepAlive.setChecked(true);
        } else {
            tbKeepAlive.setChecked(false);
        }
        tbKeepAlive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISOPEN_APPKEEPALIVE, isChecked ? SharedPreUtil.YES : SharedPreUtil.NO);
                if(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISOPEN_APPKEEPALIVE, SharedPreUtil.NO).equals("YES")){
                    try{
                        IntentWrapper.whiteListMatters(NotificationAppListActivity.this, getString(R.string.app_keepAlive));    //TODO   ----    分动保活   $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                    }catch (Exception E){
                        E.printStackTrace();
                    }
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        tb = (ToggleButton) findViewById(R.id.bt_selectAll);
        String isPersonAllSelect = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISSELECT_ALLPERSONAPP, SharedPreUtil.NO);  //个人应用
        isPerson = isPersonAllSelect.equals("YES") ? true : false;
        if (isPerson) {
            tb.setChecked(true);
        } else {
            tb.setChecked(false);
        }
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isClickSingleAppFirst){
                    isClickSingleAppFirst = false;
                }else {
                    if (arg == 0) {   // 个人应用    vPager.getCurrentItem() == 0
                        // Check or uncheck all personal app item
                        for (Map<String, Object> personalAppItem : mPersonalAppList) {
                            String appName = (String) personalAppItem.get(VIEW_ITEM_NAME);
                            if (!isChecked) {
                                IgnoreList.getInstance().addIgnoreItem(appName);
                            }else{
                                IgnoreList.getInstance().removeIgnoreItem(appName);
                                BlockList.getInstance().removeBlockItem(appName);
                            }
                            personalAppItem.remove(VIEW_ITEM_CHECKBOX);
                            personalAppItem.put(VIEW_ITEM_CHECKBOX, isChecked);  //    personalAppItem.put(VIEW_ITEM_CHECKBOX, !isAllSelected);
                        }
                        // Update list data
//                        mPersonalAppSelectedCount = (isChecked ? mPersonalAppList.size() : 0);
                        isPerson = isChecked;
                        isListViewRefresh = true;
                        mPersonalAppAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        tb2 = (ToggleButton) findViewById(R.id.bt_selectAll2);
        String isSystemAllSelect = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISSELECT_ALLSYSTEMAPP, SharedPreUtil.NO);
        isSystem = isSystemAllSelect.equals("YES") ? true : false;
        if (isSystem) {
            tb2.setChecked(true);
        } else {
            tb2.setChecked(false);
        }
        tb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isClickSingleAppFirst){
                    isClickSingleAppFirst = false;
                }else {
                    if (arg == 1) {   //系统应用
                        for (Map<String, Object> systemAppItem : mSystemAppList) {
                            String appName = (String) systemAppItem.get(VIEW_ITEM_NAME);
                            if (!isChecked) {
                                if (appName.contains(".mms")||appName.contains(".contacts")){
                                    isSMS = isChecked;
                                }else if (appName.contains(".incallui")){
                                    isCall = isChecked;
                                }
                                IgnoreList.getInstance().addIgnoreItem(appName);
                            } else {
                                if (appName.contains(".mms")||appName.contains(".contacts")){
                                    isSMS = isChecked;
                                }else if (appName.contains(".incallui")){
                                    isCall = isChecked;
                                }
                                IgnoreList.getInstance().removeIgnoreItem(appName);
                                BlockList.getInstance().removeBlockItem(appName);
                            }
                            systemAppItem.remove(VIEW_ITEM_CHECKBOX);
                            systemAppItem.put(VIEW_ITEM_CHECKBOX, isChecked);  //  systemAppItem.put(VIEW_ITEM_CHECKBOX, !isSysAllSelected);
                        }
                        // Update list data
//                        mSystemAppSelectedCount = (isChecked ? 0 : mSystemAppList.size());
                        isSystem = isChecked;
                        isListViewRefresh = true;
                        mSystemAppAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    public void initmPopupWindowView() {
        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popview_item,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView,  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
       // popupwindow.setAnimationStyle(R.style.AnimationFade);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });	/** 在这里可以实现自定义视图的功能 */
        RelativeLayout btton2 = (RelativeLayout) customView.findViewById(R.id.ietem_one);
        RelativeLayout btton3 = (RelativeLayout) customView.findViewById(R.id.ietem_two);
        TextView textone = (TextView) customView.findViewById(R.id.button2);
        TextView texttwo = (TextView) customView.findViewById(R.id.button3);
        LinearLayout LinearLayouttONE = (LinearLayout) customView.findViewById(R.id.ietem_LinearLayoutone);
        //设置非英文模式下字体大小
        if(!Utils.isZh(NotificationAppListActivity.this)){
            List<View>view=new ArrayList<View>();
            view.add(textone);
            view.add(texttwo);
            Utils.  settingAllFontsize (view,16);
        }

        if(Build.VERSION.SDK_INT>=23) {
            String packageName = NotificationAppListActivity.this.getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                btton2.setVisibility(View.VISIBLE);//6.0以上系统没有获取到权限时
                LinearLayouttONE.setVisibility(View.VISIBLE);
            }else{
                btton2.setVisibility(View.GONE);
                LinearLayouttONE.setVisibility(View.GONE);
            }
        }else{
            btton2.setVisibility(View.VISIBLE);
            LinearLayouttONE.setVisibility(View.VISIBLE);
        }


        btton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isYunOS()){
                    if(Build.VERSION.SDK_INT>=23){
                        Intent intent=new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + NotificationAppListActivity.this.getPackageName()));
                        startActivityForResult(intent, 0);
                    }else{
                        MobileInfoUtils.jumpStartInterface(NotificationAppListActivity.this,2);
                    }
                    if (popupwindow != null && popupwindow.isShowing()) {
                        popupwindow.dismiss();
                        popupwindow = null;
                    }
                }
            }
        });
        btton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                if(MobileInfoUtils.getMobileType().equalsIgnoreCase("Xiaomi")||MobileInfoUtils.getMobileType().equalsIgnoreCase("nubia")||MobileInfoUtils.getMobileType().endsWith("HUAWEI")&&Build.MANUFACTURER.contains("mate")||
                        MobileInfoUtils.getMobileType().endsWith("HUAWEI")&&Build.MANUFACTURER.contains("MATE")){
                    Toast.makeText(NotificationAppListActivity.this,getResources().getString(R.string.notsupported),Toast.LENGTH_SHORT).show();
                }else{
                    MobileInfoUtils.jumpStartInterface(NotificationAppListActivity.this,1);
                }

            }
        });
    }

    private void settitileText(int x) {
        switch (x) {
            case 0:
                if (null_ti.getText().toString().equals(getString(R.string.personal_apps_title))) {
                    null_ti.setText("");
                    per_ti.setText(getString(R.string.personal_apps_title));
                    sys_ti.setText(getString(R.string.system_apps_title));
                    vPager.setCurrentItem(0);
                }
                break;
            case 1:
                if (sys_ti.getText().toString().equals(getString(R.string.system_apps_title))) {
                    null_ti.setText(getString(R.string.personal_apps_title));
                    per_ti.setText(getString(R.string.system_apps_title));
                    sys_ti.setText("");
                    vPager.setCurrentItem(1);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.acsetting, menu);
        if (true) {
            menu.findItem(R.id.menu_acsetting).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_acsetting:
                if (android.os.Build.VERSION.SDK_INT < 18) {
                    startActivity(MainActivity.ACCESSIBILITY_INTENT);
                } else {
                    startActivity(MainActivity.NOTIFICATION_LISTENER_INTENT);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISSELECT_ALLSYSTEMAPP,tb2.isChecked()? SharedPreUtil.YES:SharedPreUtil.NO);
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_ISSELECT_ALLPERSONAPP,tb.isChecked()? SharedPreUtil.YES:SharedPreUtil.NO);

                saveIgnoreList();
                saveBlockList();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*saveIgnoreList();
        saveBlockList();*/
    }

    private void saveBlockList() {
        // Save personal app
        BlockList.getInstance().saveBlockList();
        // Load package in background
    }

    private void saveIgnoreList() {
        HashSet<String> ignoreList = new HashSet<String>();

        // Save personal app
        if (null!= mPersonalAppList &&mPersonalAppList.size()>0){
            for (Map<String, Object> personalAppItem : mPersonalAppList) {
                boolean isSelected = (Boolean) personalAppItem.get(VIEW_ITEM_CHECKBOX);
                if (!isSelected) {
                    String appName = (String) personalAppItem.get(VIEW_ITEM_NAME);
                    ignoreList.add(appName);
                    //Log.e("appName", appName);
                }
            }
        }

        // Save system app
        if (mSystemAppList.size()>0&&mSystemAppList!=null){
            for (Map<String, Object> systemAppItem : mSystemAppList) {
                boolean isSelected = (Boolean) systemAppItem.get(VIEW_ITEM_CHECKBOX);
                if (!isSelected) {
                    String appName = (String) systemAppItem.get(VIEW_ITEM_NAME);
                    ignoreList.add(appName);
                }
            }
        }

        SharedPreUtil.setParam(NotificationAppListActivity.this, SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, isSMS);
        SharedPreUtil.setParam(NotificationAppListActivity.this, SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, isCall);
        // Save to file
        Log.i(TAG, "saveIgnoreList(), ignoreList=" + ignoreList);
        IgnoreList.getInstance().saveIgnoreList(ignoreList);

        // Prompt user that have saved successfully .

        if (isClicked){
            Toast.makeText(this, R.string.save_successfully, Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isPersonalAppTabSelected() {
        return (mTabHost.getCurrentTabTag() == TAB_TAG_PERSONAL_APP);
    }

    private class PackageItemComparator implements Comparator<Map<String, Object>> {

        private final String mKey;

        public PackageItemComparator() {
            mKey = NotificationAppListActivity.VIEW_ITEM_TEXT;
        }

        /**
         * Compare package in alphabetical order.
         *
         * @see Comparator#compare(Object, Object)
         */
        @Override
        public int compare(Map<String, Object> packageItem1, Map<String, Object> packageItem2) {

            String packageName1 = (String) packageItem1.get(mKey);
            String packageName2 = (String) packageItem2.get(mKey);
            return packageName1.compareToIgnoreCase(packageName2);
        }
    }

    private class PersonalAppListAdapter extends BaseAdapter {
        private Activity activity;

        public class ViewHolder {
            public TextView tvAppName;

            public ImageView ivIcon;

            public ToggleButton swPush;
        }

        public PersonalAppListAdapter(Context context) {
            this.activity = (NotificationAppListActivity) context;
            mInflater = activity.getLayoutInflater();

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return (mPersonalAppList.size());
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            ViewHolder viewHolder = null;
            /*
             * TextView tvAppName; ImageView ivIcon; Switch swPush;
             */

            if (view == null) {
                viewHolder = new ViewHolder();

                view = mInflater.inflate(R.layout.package_list_layout, null);
                view.setPadding(0, 30, 0, 30);
                viewHolder.tvAppName = (TextView) view.findViewById(R.id.package_text);
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.package_icon);
                viewHolder.swPush = (ToggleButton) view.findViewById(R.id.package_switch);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();

                    viewHolder.tvAppName = (TextView) view.findViewById(R.id.package_text);
                    viewHolder.ivIcon = (ImageView) view.findViewById(R.id.package_icon);
                    viewHolder.swPush = (ToggleButton) view.findViewById(R.id.package_switch);
                    view.setTag(viewHolder);
                }
            }

            Map<String, Object> packageItem = null;


            viewHolder.swPush.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {   // ACTION_UP
                        isListViewRefresh = false;
                        isClickSingleAppFirst = true;
                        isClicked = true;
                    }
                    return false;
                }
            });

            viewHolder.swPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isListViewRefresh){
                        int index = position;
                        Map<String, Object> item = mPersonalAppList.get(index);   // size = 25   --- NOBIYA:53
                        if (item == null) {
                            return;
                        }
                        // Toggle item selection
                        item.remove(VIEW_ITEM_CHECKBOX);
                        item.put(VIEW_ITEM_CHECKBOX, isChecked);
                        // update list data
                        String appName = (String) item.get(VIEW_ITEM_NAME);
                        if (!isChecked) { // todo --- 关闭单个开关
                            IgnoreList.getInstance().addIgnoreItem(appName);  //todo --- 开关关闭时，添加忽略名单
                            if(isClickSingleAppFirst){
//                                tb.setChecked(false);
                                if(tb.isChecked()){
                                    tb.setChecked(false);
                                }else {
                                    isClickSingleAppFirst = false;
                                }
                            }
                        } else {      // todo --- 打开单个开关
                            IgnoreList.getInstance().removeIgnoreItem(appName);   //todo --- 开关打开时，移除忽略名单
                            BlockList.getInstance().removeBlockItem(appName);
                            if(isClickSingleAppFirst){
                                mPersonalAppSelectedCount = 0;
                                for(int i=0 ; i<mPersonalAppList.size(); i++ ){
                                    Map<String, Object>  packageItemIndex = mPersonalAppList.get(i);
                                    Boolean checked = (Boolean) packageItemIndex.get(VIEW_ITEM_CHECKBOX);
                                    if(checked){
                                        mPersonalAppSelectedCount++;
                                    }
                                }
                                if(mPersonalAppSelectedCount == mPersonalAppList.size()){
                                    tb.setChecked(true);
                                }
                                isClickSingleAppFirst = false;
                            }
                        }
                    }
                }
            });

            packageItem = mPersonalAppList.get(position);
            Drawable data = (Drawable) packageItem.get(VIEW_ITEM_ICON);
            viewHolder.ivIcon.setImageDrawable(data);

            String text = (String) packageItem.get(VIEW_ITEM_TEXT);
            viewHolder.tvAppName.setText(text);

            Boolean checked = (Boolean) packageItem.get(VIEW_ITEM_CHECKBOX);
            viewHolder.swPush.setChecked(checked);
            return view;
        }
    }

    private class SystemAppListAdapter extends BaseAdapter {
        private Activity activity;
        public class ViewHolder {
            public TextView tvAppName;
            public ImageView ivIcon;
            public ToggleButton swPush;
        }
        public SystemAppListAdapter(Context context) {
            this.activity = (NotificationAppListActivity) context;
            mInflater = activity.getLayoutInflater();
        }
        @Override
        public int getCount() {
            return mSystemAppList.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.package_list_layout, null);
                view.setPadding(0, 30, 0, 30);
                viewHolder.tvAppName = (TextView) view.findViewById(R.id.package_text);
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.package_icon);
                viewHolder.swPush = (ToggleButton) view.findViewById(R.id.package_switch);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            Map<String, Object> packageItem = null;

            viewHolder.swPush.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {   // ACTION_UP
                        isListViewRefresh = false;
                        isClickSingleAppFirst = true;
                    }
                    return false;
                }
            });

            viewHolder.swPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int index = position;
                    Map<String, Object> item = mSystemAppList.get(index);
                    if (item == null) {
                        return;
                    }
                    // Toggle item selection
                    item.remove(VIEW_ITEM_CHECKBOX);
                    item.put(VIEW_ITEM_CHECKBOX, isChecked);
                    // update list data
                    String appName = (String) item.get(VIEW_ITEM_NAME);
                    if (!isChecked) {
                        if (appName.contains(".mms") || appName.contains(".contacts")) {
                            isSMS = isChecked;
                        } else if (appName.contains(".incallui")) {
                            isCall = isChecked;
                        }
                        IgnoreList.getInstance().addIgnoreItem(appName);
                        if (isClickSingleAppFirst) {
//                            tb2.setChecked(false);
                            if(tb2.isChecked()){
                                tb2.setChecked(false);
                            }else {
                                isClickSingleAppFirst = false;
                            }
                        }
                    } else {
                            if (appName.contains(".mms") || appName.contains(".contacts")) {
                                isSMS = isChecked;
                            } else if (appName.contains(".incallui")) {
                                isCall = isChecked;
                            }
                            IgnoreList.getInstance().removeIgnoreItem(appName);
                            BlockList.getInstance().removeBlockItem(appName);
                            if(isClickSingleAppFirst){
                                mSystemAppSelectedCount = 0;
                                for(int i=0 ; i<mSystemAppList.size(); i++ ){
                                    Map<String, Object>  packageItemIndex = mSystemAppList.get(i);
                                    Boolean checked = (Boolean) packageItemIndex.get(VIEW_ITEM_CHECKBOX);
                                    if(checked){
                                        mSystemAppSelectedCount++;
                                    }
                                }
                                if(mSystemAppSelectedCount == mSystemAppList.size()){
                                    tb2.setChecked(true);
                                }
                                isClickSingleAppFirst = false;
                            }
                         }
                       }
                    });

            packageItem = mSystemAppList.get(position);
            Drawable data = (Drawable) packageItem.get(VIEW_ITEM_ICON);
            viewHolder.ivIcon.setImageDrawable(data);
            String text = (String) packageItem.get(VIEW_ITEM_TEXT);
            viewHolder.tvAppName.setText(text);
            Boolean checked = (Boolean) packageItem.get(VIEW_ITEM_CHECKBOX);
            //viewHolder.swPush.setChecked(checked);
            String name=String.valueOf(mSystemAppList.get(position).get(VIEW_ITEM_NAME));
            if (name.contains(".mms")||name.contains(".contacts")){
                viewHolder.swPush.setChecked(isSMS);
            }else if (name.contains(".incallui")){
                viewHolder.swPush.setChecked(isCall);
            }else {
                viewHolder.swPush.setChecked(checked);
            }
            return view;
        }
    }

    private class LoadPackageTask extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog mProgressDialog;

        private final Context mContext;

        public LoadPackageTask(Context context) {
            Log.i(TAG, "LoadPackageTask(), Create LoadPackageTask!");

            mContext = context;
            createProgressDialog();
        }

        /*
         * Show a ProgressDialog to prompt user to wait
         */
        private void createProgressDialog() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle(R.string.progress_dialog_title);
            mProgressDialog.setMessage(mContext.getString(R.string.progress_dialog_message));

            mProgressDialog.setCancelable(false);

            mProgressDialog.show();

            Log.i(TAG, "createProgressDialog(), ProgressDialog shows");
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Log.i(TAG, "doInBackground(), Begin load and sort package list!");

            // Load and sort package list
           try{
               loadPackageList();
               sortPackageList();}catch (Exception E){E.printStackTrace();}

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(TAG, "onPostExecute(), Load and sort package list complete!");

            // Do the operation after load and sort package list completed
            init();

            if (null  != mProgressDialog) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.dismiss();
                }
                mProgressDialog = null;
            }
        }

        private synchronized void loadPackageList() {

            mPersonalAppList = new ArrayList<Map<String, Object>>();
            mBlockAppList = new ArrayList<Map<String, Object>>();
            mSystemAppList = new ArrayList<Map<String, Object>>();
            HashSet<String> ignoreList = IgnoreList.getInstance().getIgnoreList();
            HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
            HashSet<String> exclusionList = IgnoreList.getInstance().getExclusionList();
            List<PackageInfo> packagelist = getPackageManager().getInstalledPackages(0);

            for (PackageInfo packageInfo : packagelist) {
                if (packageInfo != null) {
                    // Whether this package should be exclude;
                    if (exclusionList.contains(packageInfo.packageName)) {
                        continue;
                    }

                    /*
                     * Add this package to package list
                     */
                    Map<String, Object> packageItem = new HashMap<String, Object>();

                    // Add app icon
                    Drawable icon = mContext.getPackageManager().getApplicationIcon(
                            packageInfo.applicationInfo);
                    packageItem.put(VIEW_ITEM_ICON, icon);

                    // Add app name
                    String appName = mContext.getPackageManager()
                            .getApplicationLabel(packageInfo.applicationInfo).toString();
                    packageItem.put(VIEW_ITEM_TEXT, appName);
                    packageItem.put(VIEW_ITEM_NAME, packageInfo.packageName);


                    boolean isChecked = (ignoreList.contains(packageInfo.packageName) || blockList
                            .contains(packageInfo.packageName));
                    packageItem.put(VIEW_ITEM_CHECKBOX, !isChecked);

                    Log.i(TAG,"packageInfo.packageName = " + packageInfo.packageName + "; appName = " + appName + "; isChecked = " + isChecked);
                    // Add to package list
                    if (!Utils.isSystemApp(packageInfo.applicationInfo)) {

                        String textApp = (String) packageItem.get(VIEW_ITEM_TEXT);
                        if(!StringUtils.isEmpty(textApp) && !textApp.equals(getString(R.string.app_name))){    //todo add 20180202   com.kct.fundo.btnotification   Techmade
                            mPersonalAppList.add(packageItem);
                        }
//                        mPersonalAppList.add(packageItem);
                    } else {
                        mSystemAppList.add(packageItem);
                    }

                }
            }
        }

        private synchronized void sortPackageList() {
            // Sort package list in alphabetical order.
            PackageItemComparator comparator = new PackageItemComparator();

            // Sort personal app list
                if (mPersonalAppList != null) {
                Collections.sort(mPersonalAppList, comparator);
            }
            if (mSystemAppList != null) {
                Collections.sort(mSystemAppList, comparator);
            }

            Log.i(TAG, "sortPackageList(), PersonalAppList=" + mPersonalAppList);
            Log.i(TAG, "sortPackageList(), SystemAppList=" + mSystemAppList);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
        //Log.e(TAG,"onPageScrolled  arg0:"+arg0+"arg1:"+arg1+"arg2:"+arg2);
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        settitileText(arg0);
        //Log.e(TAG, "onPageSelected  arg0:" + arg0);
        arg = arg0;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.null_ti:   // 个人应用
                settitileText(0);
                //Log.e(TAG,"个人应用");
                tb.setVisibility(View.VISIBLE);
                tb2.setVisibility(View.GONE);

                if (isPerson) {
                    mPersonalAppSelectedCount = 0;
                    for(int i=0 ; i<mPersonalAppList.size(); i++ ){
                        Map<String, Object>  packageItemIndex = mPersonalAppList.get(i);
                        Boolean checked = (Boolean) packageItemIndex.get(VIEW_ITEM_CHECKBOX);
                        if(checked){
                            mPersonalAppSelectedCount++;
                        }
                    }
                    if(mPersonalAppSelectedCount == mPersonalAppList.size()){
                        tb.setChecked(true);
                    }

//                    tb.setChecked(true);
                } else {
                    tb.setChecked(false);
                }

                //vp.setCurrentItem(0);
                break;
            case R.id.sys_ti:  // 系统应用
                settitileText(1);
                //Log.e(TAG, "系统应用");
                //	vp.setCurrentItem(1);
                tb2.setVisibility(View.VISIBLE);
                tb.setVisibility(View.GONE);
                if (isSystem) {
                    mSystemAppSelectedCount = 0;
                    for(int i=0 ; i<mSystemAppList.size(); i++ ){
                        Map<String, Object>  packageItemIndex = mSystemAppList.get(i);
                        Boolean checked = (Boolean) packageItemIndex.get(VIEW_ITEM_CHECKBOX);
                        if(checked){
                            mSystemAppSelectedCount++;
                        }
                    }
                    if(mSystemAppSelectedCount == mSystemAppList.size()){
                        tb2.setChecked(true);
                    }
                } else {
                    tb2.setChecked(false);
                }
                break;
        }
    }
}
