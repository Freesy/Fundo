package com.szkct.weloopbtsmartdevice.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.Constants;

/**
 * Created by minjibing on 2017/7/17.
 */

public class VersionDetail extends AppCompatActivity {
    private TextView versionName;
    private TextView versionCode;
    private TextView versionShortName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_detail);
        versionName = (TextView)findViewById(R.id.versionNameText);
        versionCode = (TextView)findViewById(R.id.versionCodeText);
        versionShortName = (TextView)findViewById(R.id.versionShortNameText);

        versionName.setText("VersionDetail:"+"V1.4.2.2");
        versionCode.setText("VersionCode:"+getVersionCode());
        versionShortName.setText("VersionName:"+getVersionName());
    }

    public String getVersionShortName() {
        String versionShortName;
        try {
            versionShortName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Set them by default value
            versionShortName = Constants.NULL_TEXT_NAME;
            e.printStackTrace();
        }

        return versionShortName ;
    }

    public String getVersionName() {
        String versionName;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Set them by default value
            versionName = Constants.NULL_TEXT_NAME;
            e.printStackTrace();
        }
        return versionName ;
    }

    public String getVersionCode() {
        int versionCode;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Set them by default value
            versionCode = 0;
            e.printStackTrace();
        }
        return Integer.toString(versionCode) ;
    }
}
