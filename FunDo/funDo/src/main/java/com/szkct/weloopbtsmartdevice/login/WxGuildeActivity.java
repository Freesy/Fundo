package com.szkct.weloopbtsmartdevice.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.MarketUtils;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

import java.util.ArrayList;

public class WxGuildeActivity extends AppCompatActivity {

    private Button button_xzfdq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_wx_guilde);   // activity_guide_wx
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_xzfdq = (Button) findViewById(R.id.button_xzfdq);
        button_xzfdq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Utils.getLanguage().equals("zh")) { //中文环境跳转到应用宝
                boolean isInstalled = false;
                ArrayList<String> mList = MarketUtils.queryInstalledMarketPkgs(BTNotificationApplication.getInstance());
                for (String mPkg : mList) {
                    if (mPkg.equals("com.tencent.android.qqdownloader")) {
                        isInstalled = true;
                        break;
                    }
                }
                if (isInstalled) { // 安装了应用宝    @string/user_comment_1
//                        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT, "1");//     0：未评论 1：已评论
                    MarketUtils.launchAppDetail("com.kct.fundoHealth.btnotification", "com.tencent.android.qqdownloader");  // com.kct.fundoHealth.btnotification  ----  fundoHealth
                } else { // 未安装引导用户安装
                    Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.user_comment_5), Toast.LENGTH_SHORT).show();  // 请先安装应用宝
                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setData(Uri.parse(commentUrl));
                    intent.setData(Uri.parse("http://sj.qq.com/"));
                    startActivity(intent);
                }
//                }
            }
        });
    }
}
