package com.szkct.weloopbtsmartdevice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.view.QuicLocationBar;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class NewSelectCityListActivity extends AppCompatActivity {

    private static final String TAG = "hrj";
    ListView listview;
    ArrayList<String> list_key=new ArrayList<>();
    LinkedHashMap<String,Integer> globalRoamingMap= BTNotificationApplication.getInstance().getGlobalRoamingMap();//获取短信支持的国家区号
    MyAdapter myAdapter;
    EditText et_search;
    View iv_search, tv_title;
    QuicLocationBar quicLocationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_new_select_city_list);
        initview();
        list_key = getList_key(globalRoamingMap);
    }

    /**
     * @param globalRoamingMap_list
     * @return
     */
    public ArrayList<String> getList_key(LinkedHashMap<String, Integer> globalRoamingMap_list) {
        ArrayList<String> list_key = new ArrayList<>();

        for (String key : globalRoamingMap_list.keySet()) {
//            Log.e(TAG, "initview: "+key+" "+globalRoamingMap.get(key)+" "+getPinYinHeadChar(key).substring(0,1));
            list_key.add(key);
        }
        String str_zm = "";
//        list_key.add(0,"A");
        for(int i=0;i<list_key.size();i++){//添加动态字母
            String key=list_key.get(i);
            if(key.length()==1)
                continue;
            String zm=getPinYinHeadChar(key).substring(0,1).toUpperCase();
            if(!zm.equals(str_zm)) {
                list_key.add(i, zm);
                str_zm=zm;
            }
        }

        return list_key;
    }

    private void initview() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listview= (ListView) findViewById(R.id.listview);
        myAdapter = new MyAdapter();
        listview.setAdapter(myAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer value=globalRoamingMap.get(list_key.get(position));
                if(value==null)
                    return;
                Intent data=new Intent();
                data.putExtra("key",list_key.get(position));
                data.putExtra("value",(int)value);
                setResult(123,data);
                finish();
            }
        });
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "beforeTextChanged: " + s);

                LinkedHashMap<String, Integer> globalRoamingMap_list = (LinkedHashMap<String, Integer>) globalRoamingMap.clone();
                ArrayList<String> keys = new ArrayList<>();
                for (String key : globalRoamingMap_list.keySet()) {
                    keys.add(key);
                }
                for (String key : keys) {
                    if (!key.contains(s))
                        globalRoamingMap_list.remove(key);
                }
                list_key = getList_key(globalRoamingMap_list);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        iv_search = findViewById(R.id.iv_search);
        tv_title = findViewById(R.id.tv_title);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setVisibility(View.VISIBLE);
                iv_search.setVisibility(View.INVISIBLE);
                tv_title.setVisibility(View.INVISIBLE);
            }
        });
        quicLocationBar= (QuicLocationBar) findViewById(R.id.quicLocationBar);
        quicLocationBar.setOnTouchLitterChangedListener(new QuicLocationBar.OnTouchLetterChangedListener() {
            @Override
            public void touchLetterChanged(String s) {
                int size=list_key.size();
                for(int i=0;i<size;i++){
                    if(list_key.get(i).equals(s)){
                        listview.setSelection(i);
                        return;
                    }
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list_key.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        private LayoutInflater inflater;
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Integer value=globalRoamingMap.get(list_key.get(position));

            ViewHolder holder =null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.listitem_selectcity, parent, false);
                holder.tv_city = (TextView) convertView.findViewById(R.id.tv_city);
                holder.tv_quhao = (TextView) convertView.findViewById(R.id.tv_quhao);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_city.setText(list_key.get(position));
            holder.tv_quhao.setText("+" + value);

            if (value==null){
                holder.tv_quhao.setVisibility(View.INVISIBLE);
                convertView.setBackgroundColor(getResources().getColor(R.color.background_content));
            }else {
                holder.tv_quhao.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(getResources().getColor(R.color.background));
            }

            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (list_key.size() == 0) {//为0时隐藏
                listview.setVisibility(View.GONE);
            } else {
                listview.setVisibility(View.VISIBLE);
            }
        }

         class ViewHolder {
            public TextView tv_city;
            public TextView tv_quhao;
        }
    }

    /**
     * 得到中文首字母
     *
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {

        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data=new Intent();
//        data.putExtra("key",list_key.get(position));
//        data.putExtra("value",(int)value);
        setResult(123,data);
        finish();
    }
}
