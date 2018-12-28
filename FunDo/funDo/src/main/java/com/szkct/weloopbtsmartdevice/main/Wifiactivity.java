package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.adapter.ListViewAdapter;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.WifiDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;


public class Wifiactivity extends Activity implements OnClickListener {
    ArrayList<WifiDao> wifiList = new ArrayList<WifiDao>();
    ;
    private ListView listwifi;
    ListViewAdapter adapter;
    kctwifiBroadcast sBroadcast;
    private PopupWindow mPopupWindow = null;
    int DISCONNECTED = 0;
    int CONNECTED = 1;
    int CONNECTING = 2;
    int state = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_setwifi_listmain);

        initViews();
        sBroadcast = new kctwifiBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainService.ACTION_WIFIINFO);  // WiFi的信息
        filter.addAction(MainService.ACTION_WIFI_STATE); // WiFi的状态

        registerReceiver(sBroadcast, filter);
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.tv_pushmsg_wifi).setOnClickListener(new OnClickListener() {  // 刷新WiFi列表
            @Override
            public void onClick(View v) {
                getbtwifilist();
            }
        });

        getbtwifilist();  //TODO ---- 进入WiFi 列表页面 ，发 命令 获取 WiFi 列表
    }

    private void initViews() {
        listwifi = (ListView) findViewById(R.id.wifi_list);
        listwifi.setOnItemClickListener(new OnItemClickListener() {  // WiFi 列表条目点击
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (wifiList.get(arg2).getNetworkId() == -1) {
                    if (wifiList != null && wifiList.get(arg2).getLOCK()) {
                        if (!isFastDoubleClick()) {
                            setpop(wifiList.get(arg2).getSSID());
                        }
                    } else {
                        if (MainService.getInstance().getState() == 3) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("SSID", wifiList.get(arg2).getSSID().toString().trim());
                               // MainService.getInstance().sendMessage("w012" + jsonObject.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplication(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (MainService.getInstance().getState() == 3) {
                        //MainService.getInstance().sendMessage("w010" + wifiList.get(arg2).getNetworkId());
                    } else {
                        Toast.makeText(getApplication(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void getbtwifilist() {
        if (MainService.getInstance().getState() == 3) {
//			MainService.getInstance().sendMessage("w001");   // 发 命令 刷新 WiFi 列表

            byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.SYN_WIFI, null);  // 手表WiFi  04 4c    ---- 发送WiFi 命令
            MainService.getInstance().writeToDevice(l2, true);
        } else {
            Toast.makeText(getApplication(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    void setpop(final String SSID) {
        View mUserInfo = LayoutInflater.from(Wifiactivity.this).inflate(R.layout.input_text_view, null);
        final EditText window_title = (EditText) mUserInfo.findViewById(R.id.bleinput_edit);

        mUserInfo.findViewById(R.id.bleinput_button_no).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        mUserInfo.findViewById(R.id.bleinput_button_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainService.getInstance().getState() != 3) {
                    Toast.makeText(getApplication(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                }

                if (window_title.length() < 8) {
                    Toast.makeText(getApplication(), getString(R.string.password_lenth), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("SSID", SSID);
                    jsonObject.put("pass", window_title.getText().toString().trim());
                    //MainService.getInstance().sendMessage("w011" + jsonObject.toString());   // 点击连接WiFi
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow = new PopupWindow(mUserInfo,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.showAtLocation(Wifiactivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View v) {

    }

    private class kctwifiBroadcast extends BroadcastReceiver {   // 通过广播 ，更新 WiFi 列表
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.e("WeatherService ", " 服务 接受广播");
            if (intent.getAction().equals(MainService.ACTION_WIFIINFO)) {
                try {
                    String wifiinfo = intent.getStringExtra("wifidata");
                    state = MainService.warchwifistate;  // ？？ ??
                    Log.e("wifiinfo", wifiinfo);

                    JSONArray arr;
                    arr = (JSONArray) new JSONTokener(wifiinfo).nextValue();
                    wifiList.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object02 = (JSONObject) arr.get(i);
                        WifiDao wDao = new WifiDao();
                        wDao.setBSSID(object02.getString("BSSID"));
                        wDao.setSSID(object02.getString("SSID"));
                        wDao.setNetworkId(object02.getInt("NetworkId"));
                        wDao.setLevel(object02.getInt("level"));
                        wDao.setLinking(object02.getBoolean("linking"));
                        wDao.setLOCK(object02.getBoolean("LOCK"));
                        wifiList.add(wDao);
                    }

                    adapter = new ListViewAdapter(Wifiactivity.this, wifiList);
                    listwifi.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (intent.getAction().equals(MainService.ACTION_WIFI_STATE)) {  //TODO --WiFi的连接状态
                try {
                    int wifiinfo = intent.getIntExtra("wifistate", 0);
                    adapter.setstate(wifiinfo);
                    state = wifiinfo;
                    if (wifiinfo == DISCONNECTED) {  // WiFi断开连接
                        adapter.setssid("");
                    }
                    if (wifiinfo == CONNECTED) {   // wifi连接成功
                        if (adapter != null) {
                            adapter.setssid(intent.getStringExtra("SSID"));
                        }
                    }
                    if (wifiinfo == CONNECTING) {  // WiFi连接中
                        if (adapter != null) {
                            adapter.setssid(intent.getStringExtra("SSID"));
                        }
                    }
                    listwifi.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private long mLastClickTime = 0L;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = 0;
        slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 1000) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sBroadcast);
    }

}