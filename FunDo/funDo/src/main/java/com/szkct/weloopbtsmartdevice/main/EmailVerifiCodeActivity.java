package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;

/**
 * Created by kct on 2017/2/5.
 */
public class EmailVerifiCodeActivity extends Activity implements View.OnClickListener{

    private TextView tv_alert_content;
    private TextView btn_login,btn_next;
    private EditText et_phonenumber;
    private ImageView back;

    private TextView tv_register_email;

    private String  mRegisterEmail;// 注册的邮箱
    private long countdownStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emailverificode);

        initView();

    }

    public void initView() { //Bundle savedInstanceState
//        btn_login = (TextView) findViewById(R.id.btn_login);
//        btn_login.setOnClickListener(new View.OnClickListener() {  // 登录
//            @Override
//            public void onClick(View v) {
//                showDialog("用户名或密码错误");
//            }
//        });

        String mEmailVerifiCode = getIntent().getStringExtra("EmailVerifiCode");
        mRegisterEmail = getIntent().getStringExtra("RegisterEmail");

        long mTime  = getIntent().getLongExtra("CountdownStartTime", 0);
        countdownStartTime = mTime;



        tv_register_email = (TextView) findViewById(R.id.tv_register_email);
        tv_register_email.setText(mRegisterEmail);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

      /*  et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);  //应该为输入手机号码或邮箱
        et_phonenumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String username = et_phonenumber.getText().toString();
                    if (!StringUtils.isEmpty(username)) {
                        if (PublicTools.IsValidMobileNo(username) || StringUtils.isEmail(username)) {
                            et_phonenumber.setText(username);
                        } else {
                            Toast.makeText(EmailVerifiCodeActivity.this, "用户名输入内容格式不正确", Toast.LENGTH_SHORT).show();
//                        et_phonenumber.setText("");
                        }
                    } else {
                        Toast.makeText(EmailVerifiCodeActivity.this, "输入内容不能为空或空格字符", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });*/

        btn_next = (TextView) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

//        tv_bottom_health = (TextView) findViewById(R.id.tv_bottom_health);
//        tv_bottom_reward = (TextView) findViewById(R.id.tv_bottom_reward);
//        tv_bottom_set = (TextView) findViewById(R.id.tv_bottom_set);
//        iv_bottom_talk = (ImageView) findViewById(R.id.iv_bottom_talk);
//        iv_bottom_talkButton = (AudioRecordButton)findViewById(R.id.iv_bottom_talkButton);
//        tv_bottom_guard.setOnClickListener(onClickListener);
//        tv_bottom_health.setOnClickListener(onClickListener);
//        tv_bottom_reward.setOnClickListener(onClickListener);
//        tv_bottom_set.setOnClickListener(onClickListener);
//        iv_bottom_talk.setOnClickListener(onClickListener);
//        initFragment(); // savedInstanceState
//        setIconBottomStyle(0);//默认选中守护
//        SpeakingFragment.newInstance("").setAudioRecordButton(iv_bottom_talkButton);
    }

    private void showDialog(final String content) {
            final AlertDialog myDialog;
            myDialog = new AlertDialog.Builder(EmailVerifiCodeActivity.this).create();
            myDialog.show();
            myDialog.getWindow().setContentView(R.layout.alert_fence_dialog);
            tv_alert_content = (TextView) myDialog.getWindow().findViewById(R.id.tv_alert_content);
            tv_alert_content.setText(content);
            myDialog.setView(tv_alert_content);
            myDialog.setCancelable(false);
            ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
            myDialog.getWindow().setBackgroundDrawable(dw);
            myDialog.getWindow()
                    .findViewById(R.id.btn_fence_pop_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
//                String username = et_phonenumber.getText().toString();

                Intent intent = new Intent(this, RegisterTwoActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                intent.putExtra("RegisterEmail",mRegisterEmail);
                intent.putExtra("CountdownStartTime",countdownStartTime);
//                intent.putExtra("number",et_phonenumber.getText().toString());
                startActivity(intent);
                finish();
               /* if (!StringUtils.isEmpty(username)) {
                    if (PublicTools.IsValidMobileNo(username)) {
                        et_phonenumber.setText(username);
                        Intent intent = new Intent(this, FindPasswordTwoActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                        intent.putExtra("number",et_phonenumber.getText().toString());
                        startActivity(intent);
                    } else if(StringUtils.isEmail(username)){
                        et_phonenumber.setText(username);
                        Intent intent = new Intent(this, FindPasswordTwoActivity.class);  //当为手机找回密码时，进入FindPasswordTwoActivity，当为邮箱找回密码时，进入邮箱验证码页面
                        intent.putExtra("number",et_phonenumber.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(EmailVerifiCodeActivity.this, "输入内容格式不正确", Toast.LENGTH_SHORT).show();
//                        et_phonenumber.setText("");
                    }
                } else {
                    Toast.makeText(EmailVerifiCodeActivity.this, "输入内容不能为空或空格字符", Toast.LENGTH_SHORT).show();
                }*/
                break;

            case R.id.back:
                finish();
                break;
        }
    }
}
