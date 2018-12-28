package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class BtInputActivity extends Activity implements OnClickListener {   //  协助输入
    EditText etv;
    Button bt_sendtocopy;
    TextView length_tv;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
        if(event.getMessage() != null){
            if(event.getMessage().equals("assistInput_success")){
                Toast.makeText(this,getString(R.string.send_firmware_date_success),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_setinput);

        EventBus.getDefault().register(this);
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etv = (EditText) findViewById(R.id.et_send);
        length_tv= findViewById(R.id.length_tv);

        bt_sendtocopy = (Button) findViewById(R.id.bt_sendtocopy);
        if (SharedPreUtil.getwatchcode(this) > 23) {
            bt_sendtocopy.setVisibility(View.VISIBLE);
            bt_sendtocopy.setOnClickListener(this);
        }

        InnnerTextWatcher watcher = new InnnerTextWatcher();
        etv.addTextChangedListener(watcher);


        findViewById(R.id.bt_send).setOnClickListener(  // 发送 协助输入 数据
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etv.length() < 1) {
                            return;
                        }
                        if (MainService.getInstance().getState() == 3) {  // MainService.getInstance().getState() == 3    1 == 1
                            try {
                               /* JSONObject jsonObject = new JSONObject();
                                jsonObject.put("SETTEXT", etv.getText().toString());
//                                MainService.getInstance().sendMessage(BleContants.XIEZHU_SEND +); //  -----  "inre" + jsonObject.toString()
                                MainService.getInstance().sendMessage("inre" + jsonObject.toString()); //  -----*/

//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("SETTEXT", etv.getText().toString());

                                String sendContent = etv.getText().toString();   // 发送的数据内容   dhhbn  ---- dhhbn刘亦菲刘肖刘德华
                                if(sendContent.getBytes().length > 0 && sendContent.getBytes().length <= 60) {
                                    byte[] keyValue = sendContent.getBytes();
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.KEY_INPUTASSIT_SEND, keyValue);  //   04 16
                                    MainService.getInstance().writeToDevice(l2, true);
                                }else if(sendContent.getBytes().length > 60){
                                    Toast.makeText(BtInputActivity.this,getString(R.string.data_too_long),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(BtInputActivity.this,getString(R.string.data_not_null),Toast.LENGTH_SHORT).show();
                                }

                               /* //数据包内容
                                StringBuffer stringBuffer = new StringBuffer();
                                for(int i=0;i<l2.length;i++){
                                    stringBuffer.append(String.format("0x%02X",l2[i]));
                                    stringBuffer.append(",");
                                }
                                String t3 = stringBuffer.toString();   // 0x04,0x00,0xBE,0x00,0x05,0x64,0x68,0x68,0x62,0x6E,    ------ dhhbn
                               测试*/
                            } catch (Exception e) {   // BA00 000A FFCB 0000 ---- 0400 BE00 0564 6868 626E
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(BtInputActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sendtocopy:   // TODO --- 发送数据到剪切板
                if (etv.length() < 1) {
                    return;
                }
                if (MainService.getInstance().getState() == 3) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("SETTEXT", etv.getText().toString());
//                        MainService.getInstance().sendMessage("incm" + jsonObject.toString());
                        if(jsonObject.toString().getBytes().length > 0 && jsonObject.toString().getBytes().length <= 60) {
                            byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.KEY_SHEAR_PLATE, jsonObject.toString().getBytes());  //   04 17
                            MainService.getInstance().writeToDevice(l2, true);
                        }else if(jsonObject.toString().getBytes().length > 60){
                            Toast.makeText(BtInputActivity.this,getString(R.string.data_too_long),Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BtInputActivity.this,getString(R.string.data_not_null),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BtInputActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO --- 进入协助输入页面时，发送 协助输入开始命令
//        MainService.getInstance().sendMessage("w101");
        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.INPUTASSIT_START,null);  //   04 4d
        MainService.getInstance().writeToDevice(l2, true);

    }

    @Override
    protected void onPause() {
        // TODO 页面关闭时，发送协助输入结束 命令
        super.onPause();
//        MainService.getInstance().sendMessage("w100");

        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.KEY_INPUTASSIT_END,null);  //   04 18
        MainService.getInstance().writeToDevice(l2, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class InnnerTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            // 根据用户输入的字符串的长度
            // 进行基本的控件的基本设置,如果输入的String长度不大于等于4的，执行禁用按钮的方法，当然也可以是其他的操作
            int userName = etv.getText().toString().getBytes().length;
            if(userName > 60){
                Toast.makeText(BtInputActivity.this,getString(R.string.data_too_long),Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int textlenght = etv.getText().toString().getBytes().length;
            /*if(textlenght > 60){
                Toast.makeText(BtInputActivity.this,getString(R.string.data_too_long),Toast.LENGTH_SHORT).show();
            }*/
            length_tv.setText(getString(R.string.text_leng1)+textlenght+getString(R.string.text_leng2));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}