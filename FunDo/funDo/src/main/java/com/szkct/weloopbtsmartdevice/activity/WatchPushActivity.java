package com.szkct.weloopbtsmartdevice.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/3/15
 * 描述: ${VERSION}
 * 修订历史：
 */

public class WatchPushActivity extends AppCompatActivity{

    private static final String TAG = WatchPushActivity.class.getSimpleName();
    private GridView gridView;
    private GridItemAdapter gridItemAdapter;
    private int click;
    private String[] sendData;
    private static int[] allimages = {R.drawable.clock_skin_model1,
            R.drawable.clock_skin_model2, R.drawable.clock_skin_model3,
            R.drawable.clock_skin_model4, R.drawable.clock_skin_model5,
            R.drawable.clock_skin_model6, R.drawable.clock_skin_model7,
            R.drawable.clock_skin_model8, R.drawable.clock_skin_model9,
            R.drawable.clock_skin_model10, R.drawable.clock_skin_model11,
            R.drawable.clock_skin_model12, R.drawable.clock_skin_model13,
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
            R.drawable.clock_skin_model44, R.drawable.clock_skin_model45,
            R.drawable.clock_skin_model46, R.drawable.clock_skin_model47,
            R.drawable.clock_skin_model48, R.drawable.clock_skin_model49,
            R.drawable.clock_skin_model50, R.drawable.clock_skin_model51,
            R.drawable.clock_skin_model52, R.drawable.clock_skin_model53,
            R.drawable.clock_skin_model54, R.drawable.clock_skin_model55,
            R.drawable.clock_skin_model56, R.drawable.clock_skin_model57,
            R.drawable.clock_skin_model58, R.drawable.clock_skin_model59,
            R.drawable.clock_skin_model60, R.drawable.clock_skin_model61,
            R.drawable.clock_skin_model62, R.drawable.clock_skin_model63,
            R.drawable.clock_skin_model64, R.drawable.clock_skin_model65,
            R.drawable.clock_skin_model66, R.drawable.clock_skin_model67,
            R.drawable.clock_skin_model68, R.drawable.clock_skin_model69,
            R.drawable.clock_skin_model70, R.drawable.clock_skin_model71,
            R.drawable.clock_skin_model72, R.drawable.clock_skin_model73,
            R.drawable.clock_skin_model74, R.drawable.clock_skin_model75,
            R.drawable.clock_skin_model76, R.drawable.clock_skin_model77,
            R.drawable.clock_skin_model78, R.drawable.clock_skin_model79,
            R.drawable.clock_skin_model80, R.drawable.clock_skin_model81,
            R.drawable.clock_skin_model82, R.drawable.clock_skin_model83,
            R.drawable.clock_skin_model84, R.drawable.clock_skin_model85,
            R.drawable.clock_skin_model86, R.drawable.clock_skin_model87,
            R.drawable.clock_skin_model88, R.drawable.clock_skin_model89,
            R.drawable.clock_skin_model90, R.drawable.clock_skin_model91,
            R.drawable.clock_skin_model92, R.drawable.clock_skin_model93,
            R.drawable.clock_skin_model94, R.drawable.clock_skin_model95,
            R.drawable.clock_skin_model96, R.drawable.clock_skin_model97,
            R.drawable.clock_skin_model98, R.drawable.clock_skin_model99,
            R.drawable.clock_skin_model100, R.drawable.clock_skin_model101,
            R.drawable.clock_skin_model102, R.drawable.clock_skin_model103,
            R.drawable.clock_skin_model104, R.drawable.clock_skin_model105,
            R.drawable.clock_skin_model106, R.drawable.clock_skin_model107,
            R.drawable.clock_skin_model108, R.drawable.clock_skin_model109,
            R.drawable.clock_skin_model110, R.drawable.clock_skin_model111,
            R.drawable.clock_skin_model112, R.drawable.clock_skin_model113,
            R.drawable.clock_skin_model114, R.drawable.clock_skin_model115,
            R.drawable.clock_skin_model116, R.drawable.clock_skin_model117,
            R.drawable.clock_skin_model118, R.drawable.clock_skin_model119,
            R.drawable.clock_skin_model120, R.drawable.clock_skin_model121,
            R.drawable.clock_skin_model122, R.drawable.clock_skin_model123,
            R.drawable.clock_skin_model124, R.drawable.clock_skin_model125,
            R.drawable.clock_skin_model126, R.drawable.clock_skin_model127,
            R.drawable.clock_skin_model128, R.drawable.clock_skin_model129,
            R.drawable.clock_skin_model130, R.drawable.clock_skin_model131,
            R.drawable.clock_skin_model132, R.drawable.clock_skin_model133,
            R.drawable.clock_skin_model134, R.drawable.clock_skin_model135,
            R.drawable.clock_skin_model136, R.drawable.clock_skin_model137,
            R.drawable.clock_skin_model138, R.drawable.clock_skin_model139,
            R.drawable.clock_skin_model140, R.drawable.clock_skin_model141,
            R.drawable.clock_skin_model142, R.drawable.clock_skin_model143,
            R.drawable.clock_skin_model144, R.drawable.clock_skin_model145,
            R.drawable.clock_skin_model146, R.drawable.clock_skin_model147,
            R.drawable.clock_skin_model148, R.drawable.clock_skin_model149,
            R.drawable.clock_skin_model150, R.drawable.clock_skin_model151,
            R.drawable.clock_skin_model152, R.drawable.clock_skin_model153,
            R.drawable.clock_skin_model154, R.drawable.clock_skin_model155,
            R.drawable.clock_skin_model156, R.drawable.clock_skin_model157,
            R.drawable.clock_skin_model158, R.drawable.clock_skin_model159,
            R.drawable.clock_skin_model160, R.drawable.clock_skin_model161,
            R.drawable.clock_skin_model162,R.drawable.clock_skin_model163,
            R.drawable.clock_skin_model164,R.drawable.clock_skin_model165,
            R.drawable.clock_skin_model166,R.drawable.clock_skin_model167,
            R.drawable.clock_skin_model168,R.drawable.clock_skin_model169,
            R.drawable.clock_skin_model170,R.drawable.clock_skin_model171,
            R.drawable.clock_skin_model172,R.drawable.clock_skin_model173
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_watchpush);

        gridView = (GridView) findViewById(R.id.watch_push_gridView);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String clock_skin_model = (String) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL,"");
        if(!TextUtils.isEmpty(clock_skin_model)){
            sendData = clock_skin_model.split("#");
            int[] ss = new int[sendData.length];
            for (int sb = 0; sb < sendData.length; sb++) {
                ss[sb] = Utils.toint(sendData[sb]);
            }
            gridItemAdapter = new GridItemAdapter(ss,this);
        }else{
            gridItemAdapter = new GridItemAdapter(allimages,this);
        }

        gridView.setAdapter(gridItemAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                click = position;
                gridItemAdapter.notifyDataSetChanged();
                L2Send.sendWatchPushData((position + "").getBytes());
            }
        });

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

            viewHolder.dialName.setText("NO." + String.format(Locale.ENGLISH, "%03d", position));

            int mIndex = gridItemList.get(position).getImageId() - 1;
            if(mIndex>= 173){
                viewHolder.dialimg.setImageResource(allimages[172]);
            }else {
                viewHolder.dialimg.setImageResource(allimages[gridItemList.get(position).getImageId() - 1]);
            }



            /*if (gridItemList.get(position).getImageId() > allimages[position]) {
                viewHolder.dialimg.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.dialimg.setImageResource(allimages[gridItemList.get(position)
                        .getImageId()]);
                viewHolder.dialimg.setVisibility(View.VISIBLE);
            }*/

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
}
