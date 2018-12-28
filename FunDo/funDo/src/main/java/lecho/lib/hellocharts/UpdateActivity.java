package lecho.lib.hellocharts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import lecho.lib.hellocharts.model.UpdateBean;

/**
 * Created by Ronny on 2018/7/7.
 */

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String UPDATE_BEAN = "update_bean";
    private TextView tvCancel;
    private TextView tvUpdate;
    private TextView tvDes;

    private UpdateBean bean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        // 500 * 592
//        attributes.width = (int) (500 / 750f * Utils.getScreenWidth(this));
//        attributes.height = (int) (592 / 1334f * Utils.getScreenHeight(this));
        getWindow().setAttributes(attributes);

        tvCancel = findViewById(R.id.tv_cancel);
        tvUpdate = findViewById(R.id.tv_update);
        tvDes = findViewById(R.id.tv_des);
        tvCancel.setOnClickListener(this);
        tvUpdate.setOnClickListener(this);

        if(getIntent().hasExtra(UPDATE_BEAN)){
            bean = getIntent().getParcelableExtra(UPDATE_BEAN);
            if(bean.getDescription() != null){
                tvDes.setText(bean.getDescription());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
               clickCancel();
                break;
            case R.id.tv_update:
                toUpdate();
                break;
        }
    }

    private void clickCancel() {
        if(bean == null){
            finish();
            return;
        }
        int status = bean.getStatus();//todo 0:升级开关关闭，1：普通升级，2：强制升级
        if(status == 2){
            //todo 退出App
            try {
                System.exit(0);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            finish();
        }
    }

    private void toUpdate() {
        String url = null;
        if(bean != null){
            url = bean.getAppMarketUrl(); //apk下载链接
        }
        //todo 立即更新
        if (NetWorkUtils.isConnect(this)) {
            try {
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT, "0");//     0：未评论 1：已评论
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.PAGE_WELCOME_STARTNUM, "0");// 将启动次数置为0
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                this.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, R.string.net_error_tip, Toast.LENGTH_SHORT).show();
        }

    }

    public static void startSelf(UpdateBean updateBean){
        BTNotificationApplication context = BTNotificationApplication.getInstance();
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtra(UPDATE_BEAN, updateBean);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
