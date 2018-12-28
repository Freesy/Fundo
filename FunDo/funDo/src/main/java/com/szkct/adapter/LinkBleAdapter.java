package com.szkct.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.LinkBleData;

import java.util.List;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/9/9
 * 描述: ${VERSION}
 * 修订历史：
 */

public class LinkBleAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private static final String TAG = LinkBleAdapter.class.getSimpleName();
    private Context mContext;
    private List<LinkBleData> mList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener itemClickListener;

    public LinkBleAdapter(Context context, List<LinkBleData> list){
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.listitem_ble,parent,false);
        view.setOnClickListener(this);
        return new LinkBleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LinkBleViewHolder viewHolder = (LinkBleViewHolder) holder;
        BluetoothDevice device = mList.get(position).getBluetoothDevice();
        if (device != null) {
            String deviceName = mList.get(position).getDeviceName();
            if (!TextUtils.isEmpty(deviceName) && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
                viewHolder.deviceMacName.setText(device.getAddress());
            } else {
                viewHolder.deviceName.setText(R.string.unknown_device);
                viewHolder.deviceMacName.setText(device.getAddress());
            }
            viewHolder.deviceimg.setVisibility(View.GONE);
            viewHolder.deviceimg.clearAnimation();
        } else {
            viewHolder.deviceName.setText(R.string.unknown_device);
        }
        viewHolder.itemView.setTag(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick(v, (LinkBleData) v.getTag());
    }

    public class LinkBleViewHolder extends RecyclerView.ViewHolder{
        public TextView deviceName;
        public TextView deviceMacName;
        public ImageView deviceimg;
        public RelativeLayout relativeLayout;

        public LinkBleViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.bangde_rt);
            deviceimg = (ImageView) itemView.findViewById(R.id.devive_imag);
            deviceMacName = (TextView) itemView.findViewById(R.id.device_mac_name);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view , LinkBleData linkBleData);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        itemClickListener = listener;
    }
}
