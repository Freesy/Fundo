package com.szkct.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.Utils;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/9/9
 * 描述: ${VERSION}
 * 修订历史：
 */

public class BleConnectAdapter extends BaseAdapter{

    public static final String TAG = SMListViewAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater mInflater;
    private String  deviceName;
    private int deviceState;

    public BleConnectAdapter(Context context, int deviceState, String deviceName) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.deviceName = deviceName;
        this.deviceState = deviceState;
    }


    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();

            convertView = mInflater.inflate(R.layout.recyclerview_item_layout, null);
            holder.content = (TextView) convertView.findViewById(R.id.RecyclerView_link_ble_txt);//name
            holder.state = (TextView) convertView.findViewById(R.id.RecyclerView_link_blename_txt);//state

//            if(Locale.getDefault().getLanguage().equalsIgnoreCase("ar")){ //todo ---  阿拉伯语
//
//            }else {
//
//            }
//            holder.state.setGravity(0);
//            holder.state.setLeft(100);


            holder.show = (TextView) convertView.findViewById(R.id.CENTER_ADDDEVICE);//show
            holder.layout = (LinearLayout) convertView.findViewById(R.id.item_layout);
            holder.iv = (ImageView) convertView.findViewById(R.id.recy_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        String languageLx = Utils.getLanguage();
        if (!languageLx.equals("zh")) {  // en
            if(languageLx.equals("it")){
                holder.state.setTextSize(11);
                holder.content.setTextSize(14);
            }else {
                holder.state.setTextSize(14);
                holder.content.setTextSize(14);
            }
        }
        if(!TextUtils.isEmpty(deviceName)){
            holder.content.setText(deviceName);
        }
        if(deviceState == MainService.STATE_CONNECTED){
            holder. content.setVisibility(View.VISIBLE);  //名称
            holder. state.setVisibility(View.VISIBLE);    //连接设备
            holder. show.setVisibility(View.GONE);        //添加设备
            holder. iv.setVisibility(View.VISIBLE);
            holder. state.setText(R.string.connected);
        }else if(deviceState == MainService.STATE_CONNECTING){
            holder. content.setVisibility(View.VISIBLE);  //名称
            holder. state.setVisibility(View.VISIBLE);    //连接设备
            holder. show.setVisibility(View.GONE);        //添加设备
            holder. state.setText(R.string.bluetooth_connecting);
            holder. iv.setVisibility(View.VISIBLE);
        }else if(deviceState == MainService.STATE_DISCONNECTED){
            holder. content.setVisibility(View.VISIBLE);  //名称
            holder. state.setVisibility(View.VISIBLE);    //连接设备
            holder. show.setVisibility(View.GONE);        //添加设备
            holder. state.setText(R.string.bluetooth_connecting);
            holder. iv.setVisibility(View.VISIBLE);
        }else if(deviceState == MainService.STATE_DISCONNECTEDANDUNBIND){
            holder. content.setVisibility(View.GONE);  //名称
            holder. state.setVisibility(View.GONE);    //连接设备
            holder. show.setVisibility(View.VISIBLE);        //添加设备
            holder. iv.setVisibility(View.GONE);
        }else if(deviceState == MainService.STATE_NOCONNECT){
            holder. content.setVisibility(View.VISIBLE);  //名称
            holder. state.setVisibility(View.VISIBLE);    //连接设备
            holder. show.setVisibility(View.GONE);        //添加设备
            holder. state.setText(R.string.ble_not_connected);
            holder. iv.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    public final class ViewHolder {
//        public LanTingBoldBlackTextView content,state,show;
        public TextView content,state,show;
        public LinearLayout layout;
        public ImageView iv;
    }
}
