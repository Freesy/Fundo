package com.szkct.weloopbtsmartdevice.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.weloopbtsmartdevice.activity.LinkBleActivity;
import com.szkct.weloopbtsmartdevice.main.MainActivity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PushdialFragment extends Fragment {    // 表盘推送

    Bluttoothbroadcast blutbroadcast;
    private GridView gridView;
    // 图片的第一行文字
    // 图片的第二行文字
    private static int[] allimages = {R.drawable.clock_skin_model1,
            R.drawable.clock_skin_model2, R.drawable.clock_skin_model3,
            R.drawable.clock_skin_model4, R.drawable.clock_skin_model5,
            R.drawable.clock_skin_model6, R.drawable.clock_skin_model7,
            R.drawable.clock_skin_model8, R.drawable.clock_skin_model9,
            R.drawable.clock_skin_model10, R.drawable.clock_skin_model11,
            R.drawable.clock_skin_model12,
            R.drawable.clock_skin_model13,
            R.drawable.clock_skin_model14, R.drawable.clock_skin_model15,
            R.drawable.clock_skin_model16, R.drawable.clock_skin_model17,
            R.drawable.clock_skin_model18, R.drawable.clock_skin_model19,
            R.drawable.clock_skin_model20, R.drawable.clock_skin_model21,
            R.drawable.clock_skin_model22, R.drawable.clock_skin_model23,
            R.drawable.clock_skin_model24, R.drawable.clock_skin_model25,
            R.drawable.clock_skin_model26, R.drawable.clock_skin_model27,
            R.drawable.clock_skin_model28, R.drawable.clock_skin_model29,
            R.drawable.clock_skin_model30, R.drawable.clock_skin_model31,
            R.drawable.clock_skin_model32, R.drawable.clock_skin_model33,
            R.drawable.clock_skin_model34, R.drawable.clock_skin_model35,
            R.drawable.clock_skin_model36, R.drawable.clock_skin_model37,
            R.drawable.clock_skin_model38, R.drawable.clock_skin_model39,
            R.drawable.clock_skin_model40, R.drawable.clock_skin_model41,
            R.drawable.clock_skin_model42, R.drawable.clock_skin_model43,
            R.drawable.clock_skin_model44, R.drawable.clock_skin_model45
            , R.drawable.clock_skin_model46, R.drawable.clock_skin_model47
            , R.drawable.clock_skin_model48, R.drawable.clock_skin_model49
            , R.drawable.clock_skin_model50, R.drawable.clock_skin_model51
            , R.drawable.clock_skin_model52, R.drawable.clock_skin_model53
            , R.drawable.clock_skin_model54, R.drawable.clock_skin_model55
            , R.drawable.clock_skin_model56, R.drawable.clock_skin_model57
            , R.drawable.clock_skin_model58, R.drawable.clock_skin_model59
            , R.drawable.clock_skin_model60, R.drawable.clock_skin_model61
            , R.drawable.clock_skin_model62, R.drawable.clock_skin_model63
            , R.drawable.clock_skin_model64, R.drawable.clock_skin_model65
            , R.drawable.clock_skin_model66};
    private static int[] images = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43};

    private static int[] imagesdm = {12, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 55, 41};
    private static int[] imagesdma = {12, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 41};
    private View pushdialView;
    private TextView blelink;
    int click = 100;
    GridItemAdapter adapter;

    public static PushdialFragment newInstance(String title) {
        PushdialFragment fragment = new PushdialFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pushdialView = inflater.inflate(R.layout.pushdiallist, null);
        init();
        // initGridView();
        return pushdialView;
    }

    private void init() {
        pushdialView.findViewById(R.id.watch_help_ti).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message message = new Message();
                        message.what = 5;
                        MainActivity.mMainActivity.myHandler.sendMessage(message);
                    }
                });
        gridView = (GridView) pushdialView.findViewById(R.id.gridview);
        blelink = (TextView) pushdialView.findViewById(R.id.blelink);
        if (MainService.getInstance().getState() == 3) {
            blelink.setText(getString(R.string.ble_connected));
        } else {
            blelink.setText(getString(R.string.push_ble_not_connected));
        }
        blelink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (MainService.getInstance().getState() != 3) {
                    startActivity(new Intent(getActivity(), LinkBleActivity.class));
                }
            }
        });
        String clock_skin_model = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL);
        if (clock_skin_model.equals("")) {
            String watchtype = SharedPreUtil.getwatchtype(getActivity());
            if (watchtype.equals("DM")) {
                adapter = new GridItemAdapter(imagesdm, getActivity());
            } else if (watchtype.equals("DMA")) {
                adapter = new GridItemAdapter(imagesdma, getActivity());
            } else if (watchtype.equals("F")) {
                adapter = new GridItemAdapter(images, getActivity());
            } else {
                adapter = new GridItemAdapter(images, getActivity());
            }
        } else {
            String[] mmString = clock_skin_model.split("#");
            int[] ss = new int[mmString.length];
            for (int sb = 0; sb < mmString.length; sb++) {
                ss[sb] = Utils.toint(mmString[sb]) - 1;
            }
            adapter = new GridItemAdapter(ss, getActivity());
        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {   // TODO ---- 表盘推送时，点击某个表盘，只是通过蓝牙发送一个命令，其参数为对应的表盘的序号
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (MainService.getInstance().getState() == 3) {
                    click = position;
                    adapter.notifyDataSetChanged();
//                    MainService.getInstance().sendMessage("dial" + position);

//                    byte[] value = new byte[1];
//                    value[0] = 6;
                    String keyValue = position + "";
                    byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.DIAL_REQUEST,keyValue.getBytes());  //   04 4E -- 表盘推送，发送对应的表盘序号
                    MainService.getInstance().writeToDevice(l2, true);

                }
            }
        });
        blutbroadcast = new Bluttoothbroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainService.ACTION_BLEDISCONNECTED);
        intentFilter.addAction(MainService.ACTION_BLECONNECTED);
        intentFilter.addAction(MainService.ACTION_BLETYPE);
        intentFilter.addAction(MainService.ACTION_CLOCK_SKIN_MODEL_CHANGE);
        getActivity().registerReceiver(blutbroadcast, intentFilter);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (blutbroadcast != null) {
            getActivity().unregisterReceiver(blutbroadcast);
        }
    }

    class GridItem {

        private int imageId;

        public GridItem() {
            super();
        }

        public GridItem(int imageId) {
            super();

            this.imageId = imageId;

        }


        public int getImageId() {
            return imageId;
        }
    }

    public class GridItemAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<GridItem> gridItemList;

        public GridItemAdapter(int[] images, Context context) {
            super();
            gridItemList = new ArrayList<GridItem>();
            inflater = LayoutInflater.from(context);
            for (int i = 0; i < images.length; i++) {
                GridItem picture = new GridItem(images[i]);
                gridItemList.add(picture);
            }
        }

        @Override
        public int getCount() {
            if (null != gridItemList) {
                return gridItemList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return gridItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grid_item, null);
                viewHolder = new ViewHolder();
                viewHolder.dialName = (TextView) convertView
                        .findViewById(R.id.title);
                viewHolder.dialimg = (ImageView) convertView
                        .findViewById(R.id.image);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // viewHolder.dialName.setText(gridItemList.get(position).getTitle());

            viewHolder.dialName.setText("NO." + String.format(Locale.ENGLISH, "%03d", position + 1));
            if (gridItemList.get(position).getImageId() > allimages.length - 1) {
                viewHolder.dialimg.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.dialimg.setImageResource(allimages[gridItemList.get(position)
                        .getImageId()]);
                viewHolder.dialimg.setVisibility(View.VISIBLE);
            }

            if (click == position) {
                viewHolder.dialimg.setBackgroundResource(R.drawable.pushdial_bg);
            } else {
                viewHolder.dialimg.setBackgroundResource(R.color.pushdial_list_item_bg);
            }

            return convertView;
        }

    }

    static class ViewHolder {
        TextView dialName;
        ImageView dialimg;
    }


    public class Bluttoothbroadcast extends BroadcastReceiver {
        /*
		 * State wifiState = null; State mobileState = null;
		 */

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.e("rq", action);
            if (MainService.ACTION_BLECONNECTED.equals(action)) {
                if (null != blelink) {
                    blelink.setText(getString(R.string.ble_connected));
                }
            }
            if (MainService.ACTION_CLOCK_SKIN_MODEL_CHANGE.equals(action)) {
                String clock_skin_model = SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL);
                String[] mmString = clock_skin_model.split("#");
                int[] ss = new int[mmString.length];
                for (int sb = 0; sb < mmString.length; sb++) {
                    ss[sb] = Utils.toint(mmString[sb]) - 1;

                }
                adapter = new GridItemAdapter(ss, getActivity());
                gridView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                return;
            }
            if (MainService.ACTION_BLETYPE.equals(action)) {
                String watchtype = SharedPreUtil.getwatchtype(getActivity());
                Log.e("watchtype", watchtype);
                if (watchtype.equals("F")) {
                    adapter = new GridItemAdapter(images, getActivity());
                    gridView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    return;
                }

                if (watchtype.equals("DM")) {
                    adapter = new GridItemAdapter(imagesdm, getActivity());
                    gridView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    return;
                }
                if (watchtype.equals("DMA")) {
                    adapter = new GridItemAdapter(imagesdma, getActivity());
                    gridView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    return;
                }
            }
            if (MainService.ACTION_BLEDISCONNECTED.equals(action)) {
                if (null != blelink) {
                    blelink.setText(getString(R.string.push_ble_not_connected));
                }
            }

        }

    }

    @Override
    public void onDestroyView() {
// TODO Auto-generated method stub
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);


        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}