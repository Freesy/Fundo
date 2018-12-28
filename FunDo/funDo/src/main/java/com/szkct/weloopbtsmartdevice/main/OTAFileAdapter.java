package com.szkct.weloopbtsmartdevice.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.kct.fundo.btnotification.R;

import java.util.ArrayList;
import java.util.HashMap;



//import no.nordicsemi.android.dfu.internal.manifest.FileInfo;     nordic专用

/**
 * Created by lenovo on 2017/9/27.
 */

public class OTAFileAdapter extends BaseAdapter {

    private final String TAG = OTAFileAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;

    public static HashMap<Integer, Boolean> isSelected;
    private HashMap<String, FileInfo> mOTAFileList = null;   //  todo ---   // TODO  --- FileInfo 必须倒 bk 专用  FileInfo（）非nordic专用的
    private ArrayList<String> mFileList = null;

    public OTAFileAdapter(Context context, HashMap<String, FileInfo> list,
                          int resource, ArrayList<String> arrayList) {
        mContext = context;
        mOTAFileList = list;
        mFileList = arrayList;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < mOTAFileList.size(); i++) {
            isSelected.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return mOTAFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mOTAFileList.get(mFileList.get(position).toString());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckBox mCheckBox;
        if(convertView != null)
        {
            mCheckBox = (CheckBox) convertView.getTag();
        }else
        {
            convertView = mInflater.inflate(R.layout.listitem_script, null);
            mCheckBox = (CheckBox) convertView.findViewById(R.id.script_item);
            convertView.setTag(mCheckBox);
        }

        Log.i(TAG, mFileList.get(position));
        mCheckBox.setText(mOTAFileList.get(mFileList.get(position)).getFileName());
        mCheckBox.setChecked(isSelected.get(position));

        return convertView;
    }

    public String getCheckedFileName()
    {
        for(int forCount = 0; forCount < mOTAFileList.size(); forCount++)
        {
            if(isSelected.get(forCount))
            {
                String lFileName = mFileList.get(forCount).toString();
                return lFileName;
            }
        }
        return null;
    }
}
