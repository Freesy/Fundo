package com.szkct.weloopbtsmartdevice.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.mtk.app.applist.FileUtils;
import com.szkct.takephoto.app.TakePhoto;
import com.szkct.takephoto.app.TakePhotoImpl;
import com.szkct.takephoto.model.CropOptions;
import com.szkct.takephoto.model.InvokeParam;
import com.szkct.takephoto.model.TContextWrap;
import com.szkct.takephoto.model.TResult;
import com.szkct.takephoto.permission.InvokeListener;
import com.szkct.takephoto.permission.PermissionManager;
import com.szkct.takephoto.permission.TakePhotoInvocationHandler;
import com.szkct.weloopbtsmartdevice.data.greendao.UserInfo;
import com.szkct.weloopbtsmartdevice.util.ImageCacheUtil;
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
import com.szkct.weloopbtsmartdevice.util.RequestCallBackEx;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.util.XHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import android.app.AlertDialog;

/**
 * Created by kct on 2017/2/5.
 */
public class AddPersonaldataActivity extends AppCompatActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener { // extends AppCompatActivity   extends Activity     ????????     ,IRequestListener

    private TextView tv_alert_content;
    private TextView btn_login,btn_next;
    private Button btn_complete;
    private ImageView back,iv_my_headphoto;
    private RadioButton ib_male,ib_female;
    private FrameLayout fl_weight,fl_height,fl_birth;
    private boolean isAlertShow = false;
    private PopupWindow mPopupWindow;
    NumberPicker pv_in,pv_ft,pv_all;
    int showpopsel=-1;

    private TextView tvWeight,tvHeight,tvBirth;

    private static final int HEIGHT_US = 0;
    private static final int HEIGHT= 1;
    private static final int WEIGHT = 2;
    private static final int WEIGHT_US = 3;
    private static final int SEX = 4;
    private static final int METRIC = 5;

    private static final int TAKE_PICTURE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int CUT_PHOTO_REQUEST_CODE = 2;

    private String[] mSexArr = new String[2];

    private LinearLayout mScrollView;
    private RadioGroup rg_sex;
    private String path = "";
    private Uri photoUri = null;

    private String picName = "";
    private String imageUrl;
    private Bitmap uploadBitmap = null;
    private float dp;
    private String sexStr;
    private EditText tv_my_name;

    private File file;
    private MyLoadingDialog myLoadingDialog;
    private String mNickName;
    private String mWeight;
    private String mHeight;
    private String mBirth;

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }

        setContentView(R.layout.activity_personaldata);

        // TODO ----- 先只考虑公制 数据的保存
       /* // 身高
        SharedPreUtil.savePre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT, pv_all.getValue() + "");  // 替换pv_all.getValue() + ""  为 后台传递的值
        tvHeight.setText(pv_all.getValue() + " cm");
        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CHANGE_TIME, System.currentTimeMillis() + "");    //保存身高到本地
        // 体重
        SharedPreUtil.savePre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.WEIGHT, pv_all.getValue() + "");  // 替换pv_all.getValue() + ""  为 后台传递的值
        tvWeight.setText(pv_all.getValue() + " kg");
        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CHANGE_TIME, System.currentTimeMillis() + "");   //保存体重到本地
//        生日
        SharedPreUtil.savePre(getApplicationContext(),SharedPreUtil.USER,SharedPreUtil.BIRTH,  "2012.03.21");  // 替换pv_all.getValue() + ""  为 后台传递的值*/


        initView();
        getTakePhoto().onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    public void initView() { //Bundle savedInstanceState
//        btn_login = (TextView) findViewById(R.id.btn_login);
//        btn_login.setOnClickListener(new View.OnClickListener() {  // 登录
//            @Override
//            public void onClick(View v) {
//                showDialog("用户名或密码错误");
//            }
//        });

        tvWeight = (TextView) findViewById(R.id.tv_weight);
        tvHeight = (TextView) findViewById(R.id.tv_height);
        tvBirth = (TextView) findViewById(R.id.tv_birth);


        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
        ib_male = (RadioButton) findViewById(R.id.ib_male);
//        ib_male.setOnClickListener(this);
        ib_female = (RadioButton) findViewById(R.id.ib_female);
//        ib_female.setOnClickListener(this);

        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int viewId) {
                if (viewId == R.id.ib_male) {

                }else {

                }
            }
        });

        mScrollView = (LinearLayout) findViewById(R.id.li_myData_register);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        iv_my_headphoto = (ImageView) findViewById(R.id.iv_my_headphoto);   // 点击添加头像  headPhoto
        iv_my_headphoto.setOnClickListener(this);



        fl_weight = (FrameLayout) findViewById(R.id.fl_weight);
        fl_weight.setOnClickListener(this);

        fl_height = (FrameLayout) findViewById(R.id.fl_height);
        fl_height.setOnClickListener(this);

        fl_birth = (FrameLayout) findViewById(R.id.fl_birth);
        fl_birth.setOnClickListener(this);

        btn_complete = (Button) findViewById(R.id.btn_complete);  // 完成按钮
        btn_complete.setOnClickListener(this);

        tv_my_name = (EditText) findViewById(R.id.tv_my_name);   // 昵称
        tv_my_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tv_my_name.setBackgroundResource(R.drawable.login_input_s);
                } else {
                    tv_my_name.setBackgroundResource(R.drawable.login_input_n);
                }
            }
        });

        initheightAndWeight();
        if (SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER,
                SharedPreUtil.BIRTH).equals("")) {
            tvBirth.setText("2009-12-31");   // 生日为空，设置默认值
        } else {
            tvBirth.setText(SharedPreUtil.readPre(AddPersonaldataActivity.this,
                    SharedPreUtil.USER, SharedPreUtil.BIRTH));
        }

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
            myDialog = new AlertDialog.Builder(AddPersonaldataActivity.this).create();
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

            case  R.id.bt_my_data_set_ok:
			/*private static final int HEIGHT_US = 0;
			private static final int HEIGHT= 1;
			private static final int WEIGHT = 2;
			private static final int WEIGHT_US = 3;
			private static final int SEX = 4;
			private static final int METRIC = 5;*/
                switch (showpopsel) {
                    case HEIGHT_US:
                        SharedPreUtil.savePre(AddPersonaldataActivity.this,
                                SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN,
                                pv_in.getValue()+"");
                        SharedPreUtil.savePre(AddPersonaldataActivity.this,
                                SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT,
                                pv_ft.getValue()+"");
                        tvHeight.setText(pv_ft.getValue() + " " + getString(R.string.imperial_foot) + pv_in.getValue() + " " + getString(R.string.imperial_inch));
                        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
                                SharedPreUtil.CHANGE_TIME, System.currentTimeMillis()+"");
                        break;
                    case HEIGHT:
                        SharedPreUtil.savePre(AddPersonaldataActivity.this,
                                SharedPreUtil.USER, SharedPreUtil.HEIGHT,
                                pv_all.getValue()+"");
                        tvHeight.setText(pv_all.getValue() + " cm");
                        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
                                SharedPreUtil.CHANGE_TIME, System.currentTimeMillis() + "");    //保存身高到本地
                        break;

                    case WEIGHT:

                        SharedPreUtil.savePre(AddPersonaldataActivity.this,
                                SharedPreUtil.USER, SharedPreUtil.WEIGHT,
                                pv_all.getValue()+"");
                        tvWeight.setText(pv_all.getValue() + " kg");
                        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
                                SharedPreUtil.CHANGE_TIME, System.currentTimeMillis() + "");   //保存体重到本地
                        break;


                    case WEIGHT_US:

                        SharedPreUtil.savePre(AddPersonaldataActivity.this,
                                SharedPreUtil.USER, SharedPreUtil.WEIGHT_US,
                                pv_all.getValue()+"");
                        tvWeight.setText(pv_all.getValue() + " lbs");
                        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
                                SharedPreUtil.CHANGE_TIME, System.currentTimeMillis() + "");
                        break;


                    case SEX:

                       /* if(Utils.toint(SharedPreUtil.readPre(AddPersonaldataActivity.this,
                                SharedPreUtil.USER, SharedPreUtil.SEX))!=pv_all.getValue()){
                            SharedPreUtil.savePre(AddPersonaldataActivity.this,
                                    SharedPreUtil.USER, SharedPreUtil.SEX, pv_all.getValue()+"");

                            setHeadPhoto();
                            setheadnamebroadcast();
                            sexIcon.setText(mSexArr[pv_all.getValue()]);
                        }

                        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
                                SharedPreUtil.CHANGE_TIME, System.currentTimeMillis()+"");*/

                        break;

                    case METRIC:

                     /*   if (mMetricArr[pv_all.getValue()].equals(getString(R.string.metric_units))) {
                            SharedPreUtil.savePre(AddPersonaldataActivity.this,
                                    SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES);
                        } else {
                            SharedPreUtil.savePre(AddPersonaldataActivity.this,
                                    SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.NO);
                        }
                        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
                                SharedPreUtil.CHANGE_TIME, System.currentTimeMillis()+"");
                        initheightAndWeight();*/



                        break;
                    default:
                        break;
                }

                setbroadcast();
                mPopupWindow.dismiss();

                break;

            case R.id.btn_complete:  // 点击完成按钮   ------ 目前做法  点击完成时，只将头像上传给后台
                //TODO 填写个人资料完成，进入主页   ---- 对应的各个条目的值的处理（1：保存到本地:2：传递给下一个页面 3：传给后台）
                if(ib_male.isChecked()){
//                    sexStr = getString(R.string.my_man); // 男性
                    sexStr = "0";
                }else {
//                    sexStr = getString(R.string.my_woman); // 女性
                    sexStr = "1";
                }
                Log.e("sexStr----- ", sexStr);

                mNickName = tv_my_name.getText().toString();  //昵称
                mWeight = tvWeight.getText().toString();  // 体重
                mHeight = tvHeight.getText().toString();  // 身高
                mBirth  = tvBirth.getText().toString();   // 生日


// TODO 不在点击完成按钮这里 上传头像   2：个人资料上传成功后，才将数据本地保存，然后进入主页
                showLoadingDialogNew("上传数据中");
                toModifyUserInfo(new RequestCallBackEx<String>() {  // getBytes(file).toString(),   name,
                    @Override
                    public void onSuccessEx(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        parseModifyUserInfoData(responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(AddPersonaldataActivity.this, "信息上传失败了,请重试", Toast.LENGTH_SHORT).show();
                        dismissLoadingDialog();
                    }
                });
//                Intent intent = new Intent(this, MainActivity.class);   // 点击完成按钮进入主页
//                startActivity(intent);
//                finish();
                break;

            case R.id.back:
                finish();
                break;

            case R.id.iv_my_headphoto:
                //TODO 点击添加头像
                showChooseDialog();
                break;

            case R.id.ib_male:
                //TODO 点击选择男
                if(ib_male.isChecked()){
                    ib_male.setChecked(false);
                    ib_female.setChecked(true);
                }else {
                    ib_male.setChecked(true);
                    ib_female.setChecked(false);
                }
                break;

            case R.id.ib_female:
                //TODO 点击选择女
                if(ib_female.isChecked()){
                    ib_female.setChecked(false);
                    ib_male.setChecked(true);
                }else {
                    ib_female.setChecked(true);
                    ib_male.setChecked(false);
                }
                break;

            case R.id.fl_weight:
                //TODO 点击体重
                showSetMyBodyInformation("weight");
//                showDialog2("哈哈哈");

                break;

            case R.id.fl_height:
                //TODO 点击身高
//                if(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.METRIC).equals(SharedPreUtil.NO)){
//                    showSetMyheight_us();
//                }else{
                    showSetMyBodyInformation("height");
//                }
                break;

            case R.id.fl_birth:
                //TODO 点击生日
                showSetMyBirthdayInformation();
                break;
        }
    }

    public InputStream getInputStream(FileInputStream fileInput) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = -1;
        InputStream inputStream = null;
        try {
            while ((n=fileInput.read(buffer)) != -1) {
                baos.write(buffer, 0, n);

            }
            byte[] byteArray = baos.toByteArray();
            inputStream = new ByteArrayInputStream(byteArray);
            return inputStream;


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void toUploadPhoto(String userId,RequestCallBackEx<String> respon){ // ,String repwd  String file,

        try {
//            reParams.addQueryStringParameter("mid", 1063024 + "");
//            reParams.addBodyParameter("Images",file);
            com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
            reParams.addQueryStringParameter("mid", userId);
            reParams.addBodyParameter("Images", file);
            String url = ServerConfig.UPLOAD_PHOTO;
            if (url.contains(" ")){
                if(url.substring(url.length()-1)==" "){
                    url= url.substring(0,url.length()-1);
                }else{
                    url= url.replace(" ","%20");
                }
            }
            if (url.contains("\"")){
                url= url.replace("\"","%22");
            }
            if (url.contains("{")){
                url= url.replace("{","%7B");
            }
            if (url.contains("}")){
                url= url.replace("{","%7D");
            }
            XHttpUtils.getInstance().send(HttpRequest.HttpMethod.POST, url, reParams, respon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setheadnamebroadcast() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainService.ACTION_MYINFO_CHANGE);   //更新昵称（没有更新头像）
        sendBroadcast(broadcastIntent);
    }

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    private void showChooseDialog() {
//        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        AlertDialog.Builder alert =  new AlertDialog.Builder(this);
        alert.setTitle(R.string.please_select);

        alert.setItems(R.array.photograph, new DialogInterface.OnClickListener() { //crash   DialogInterface.OnClickListener()
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                /*if (arg1 == 0) {
                    photo();   // 拍照获取头像
                } else {   // 从相册获取头像
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent it = new Intent(Intent.ACTION_PICK);
                    it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image");
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }*/

                ////////////////////////////////////////////////////////////////////////////////////////////////
                if (arg1 == 0) {
                    getTakePhoto().onPickFromCaptureWithCrop(getPhotoUri(), getBuilder());
                } else {
                    getTakePhoto().onPickFromGalleryWithCrop(getPhotoUri(), getBuilder());
                }
                //////////////////////////////////////////////////////////////////////////////////////////////////////////

            }
        });
        alert.show();
    }

    private Uri getPhotoUri() {
        String sdcardState = Environment.getExternalStorageState();
        String sdcardPathDir = FileUtils.SDPATH;

        SimpleDateFormat sDateFormat = Utils.setSimpleDateFormat("yyyyMMddhhmmss");
        picName = sDateFormat.format(new java.util.Date());

        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            File fileDir = new File(sdcardPathDir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            file = new File(sdcardPathDir + picName + ".JPEG");
        }
        return Uri.fromFile(file);
    }

    @NonNull
    private CropOptions getBuilder() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(400).setOutputY(400);
        builder.setAspectX(400).setAspectY(400);
        builder.setWithOwnCrop(false);
        return builder.create() ;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                Log.e("TAKE_PICTURE", "photoUri=" + photoUri.toString());
                if (resultCode == 0) {
                    break;
                }
                picName = startPhotoZoom(photoUri);

                break;
            case RESULT_LOAD_IMAGE: // 从相册裁剪头像
                Log.e("RESULT_LOAD_IMAGE", "photoUri=");
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        picName = startPhotoZoom(uri);
                    }
                }
                break;
            case CUT_PHOTO_REQUEST_CODE:   // TODO 裁剪照片回调(拍照和从相册裁剪图片后的回调)
                Log.e("CUT_PHOTO_REQUEST_CODE", "photoUri=");
                if (resultCode == RESULT_OK && null != data) {// 裁剪返回
                    Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(imageUrl);
                    System.out.println("bitmap = " + bitmap);
                    FileUtils.deleteDir(FileUtils.SDPATH);
                    uploadBitmap = ImageCacheUtil.createFramedPhoto(480, 480, bitmap, (int) (dp * 1.6f));
                    // picName = startPhotoZoom(photoUri);
                    FileUtils.saveBitmap(uploadBitmap, picName);
//                    File file = new File(FileUtils.SDPATH, picName + ".JPEG");
                    file = new File(FileUtils.SDPATH, picName + ".JPEG");  //TODO 裁剪后获取到头像照片后，开始上传图片
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    showLoadingDialogNew("上传头像中");
                    String userId =  SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);  // 1063024
// TODO ----- 裁剪完图片后，将图片上传
                    toUploadPhoto(userId, new RequestCallBackEx<String>() {  // getBytes(file).toString(),
                        @Override
                        public void onSuccessEx(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            parseUploadPhotoData(responseInfo.result);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Toast.makeText(AddPersonaldataActivity.this, "上传失败了,请重试", Toast.LENGTH_SHORT).show();
                            dismissLoadingDialog();
                        }
                    });
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				/*
				 * if (NetWorkUtils.isConnect(getApplicationContext())) { if
				 * (file.exists()) { uploadPhoto(file);
				 * headPhoto.setImageBitmap(ImageCacheUtil
				 * .toRoundBitmap(uploadBitmap));
				 *
				 * SharedPreUtil.savePre(getApplicationContext(),
				 * SharedPreUtil.USER, SharedPreUtil.FACE, picName + ".JPEG"); }
				 * } else { Toast.makeText(getApplicationContext(),
				 * R.string.my_network_disconnected, Toast.LENGTH_SHORT).show();
				 * }
				 */

//                    iv_my_headphoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(uploadBitmap));   // 更新ImageView的头像
//                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE, picName + ".JPEG");     // 保存裁剪的照片到本地

//                    setheadnamebroadcast();    //更新昵称（没有更新头像）
                }
                break;
        }
    }

    public  void parseUploadPhotoData(String content) {
        try {
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");
            if(nRetCode == 0){
                JSONObject obj = jsonObj.optJSONObject("data");
                String newUserid = obj.optString("mid");
//                SharedPreUtil.savePre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.MID, newUserid);  // 保存新的用户id
//                Log.e("MD555555", "--获取到验证码时间---：" + currentTime);
                dismissLoadingDialog();
                iv_my_headphoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(uploadBitmap));   // 更新ImageView的头像
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE, picName + ".JPEG");     // 保存裁剪的照片到本地
//                Toast.makeText(AddPersonaldataActivity.this, "上传头像成功了", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 1){
                dismissLoadingDialog();
                Toast.makeText(AddPersonaldataActivity.this, "mid为空", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 2){
                Toast.makeText(AddPersonaldataActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 3){
                dismissLoadingDialog();
                Toast.makeText(AddPersonaldataActivity.this, "未上传文件", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 4){
                dismissLoadingDialog();
                Toast.makeText(AddPersonaldataActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }else if(nRetCode == 5){
                dismissLoadingDialog();
                Toast.makeText(AddPersonaldataActivity.this, "上传文件类型不符合", Toast.LENGTH_SHORT).show();
            }
            else if(nRetCode == 6){
                dismissLoadingDialog();
                Toast.makeText(AddPersonaldataActivity.this, "文件超出规定大小", Toast.LENGTH_SHORT).show();
            }

//            JSONObject obj = jsonObj.optJSONObject("data");
//            int mEmailVerifiCode = obj.optInt("codes");
           /* errStr[0] = jsonObj.optString("ErrorMsg");
            if (0 == nRetCode) {
                user = new UserModel();
                JSONObject jsonResult = jsonObj.getJSONObject("Result");
                user.sessionKey = jsonResult.optString("sessionKey");
                user.userType = jsonResult.optInt("userType");
                user.ddPushIP = jsonResult.optString("DDPushIP");
                user.ddPushPort = jsonResult.optInt("DDPushPort");
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        return user;
    }

    public void photo() {
        try {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String sdcardState = Environment.getExternalStorageState();
            String sdcardPathDir = android.os.Environment.getExternalStorageDirectory().getPath() + "/tempImage/";
            File file = null;
            if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                // 有sd卡，是否有myImage文件夹
                File fileDir = new File(sdcardPathDir);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                // 是否有headImg文件
                file = new File(sdcardPathDir + System.currentTimeMillis() + ".JPEG");
            }
            if (file != null) {
                path = file.getPath();
                photoUri = Uri.fromFile(file);
                Log.e("=====", "获取到的图片地址 ：" + photoUri);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String startPhotoZoom(Uri uri) {
        try {
            Log.e("====", "uri = " + uri);
            // 获取系统时间 然后将裁剪后的图片保存至指定的文件夹
            SimpleDateFormat sDateFormat = Utils.setSimpleDateFormat(
                    "yyyyMMddhhmmss");
            String address = sDateFormat.format(new java.util.Date());
            if (!FileUtils.isFileExist("")) {
                FileUtils.createSDDir("");

            }
            imageUrl = FileUtils.SDPATH + address + ".JPEG";
            Uri imageUri = Uri.fromFile(new File(imageUrl));

            final Intent intent = new Intent("com.android.camera.action.CROP");
            // 照片URL地址
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 480);
            intent.putExtra("outputY", 480);
            intent.putExtra("scaleUpIfNeeded", true);
            // 输出路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // 输出格式
            intent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());
            // 不启用人脸识别
            intent.putExtra("noFaceDetection", false);
            intent.putExtra("return-data", false);
            startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);  //开始裁剪照片
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showSetMyBirthdayInformation() {

        // AlertDialog.Builder setBirthdayBuilder = new
        // AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.set_user_birthday_information_dialog, null);
        final DatePicker dpBirthday = (DatePicker) view
                .findViewById(R.id.dp_my_data_set_birthday);
        TextView tvTitle = (TextView) view
                .findViewById(R.id.tv_my_data_dialog_title);
        tvTitle.setText(getString(R.string.my_birthday));
        // setMinDate
        dpBirthday.setMaxDate(System.currentTimeMillis());
		/*
		 * TextView tvTitle = (TextView) view
		 * .findViewById(R.id.tv_my_data_birthday_dialog_title);
		 */
        // 设置时间选择器的默认选中时间
        if (!tvBirth.getText().toString().equals("")) {
            String birhStr = tvBirth.getText().toString();
            Log.e("birhStr", birhStr);
            String[] birthStrings = birhStr.split("\\-");

            Log.e("birhStr", birhStr + birthStrings.length);
            int year_int = Integer.parseInt(birthStrings[0]);  // crash
            int month_int = Integer.parseInt(birthStrings[1]);
            int day_int = Integer.parseInt(birthStrings[2]);
            // Log.e("设置生日选着器", "年 ="+year_int+"月 ="+month_int+"日 ="+day_int);
            dpBirthday.updateDate(year_int, month_int - 1, day_int);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Button btOk = (Button) view.findViewById(R.id.bt_my_date_set_ok);
        Button btCancel = (Button) view
                .findViewById(R.id.bt_my_data_set_cancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpBirthday.clearFocus();
                String	setBirthdayStr = dpBirthday.getYear() + "-" + (dpBirthday.getMonth() + 1) + "-" + dpBirthday.getDayOfMonth() + "";
                Log.e("选择生日", "setBirthdayStr = " + setBirthdayStr);
                tvBirth.setText(setBirthdayStr);
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.BIRTH, setBirthdayStr);    //将生日保存到本地
				/*
				 * if (NetWorkUtils.isConnect(MyDataActivity.this)) { if
				 * (!setBirthdayStr.equals("")&&!SharedPreUtil.readPre(
				 * MyDataActivity.this, SharedPreUtil.USER,
				 * SharedPreUtil.MID).equals("")) { String sexMid =
				 * SharedPreUtil.readPre( MyDataActivity.this,
				 * SharedPreUtil.USER, SharedPreUtil.MID); String bodyUrl =
				 * Constants.URLREVISEINFOPREFIX + "mid=" + sexMid + "&sex=" +
				 * sex + "&birth=" + setBirthdayStr; Log.e("PickerView",
				 * " bodyUrl ：" + bodyUrl); if (hc == null) { hc =
				 * HTTPController.getInstance();
				 * hc.open(getApplicationContext()); }
				 * hc.getNetworkData(bodyUrl, handler, SETBIRTH); }
				 *
				 * } else { Toast.makeText(MyDataActivity.this,
				 * getString(R.string.userdata_synerror),
				 * Toast.LENGTH_SHORT).show(); }
				 */
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                setbroadcast();

            }
        });
        btCancel.setOnClickListener(new DialogSetBodyInformationOnClick());
        isAlertShow = true;

        mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            public void onDismiss() {
                // TODO Auto-generated method stub
                isAlertShow = false;
            }
        });
        mPopupWindow.showAtLocation(mScrollView, Gravity.BOTTOM, 0, 0);
    }

    private void showDialog2(final String content) {
        final AlertDialog myDialog;
        myDialog = new AlertDialog.Builder(AddPersonaldataActivity.this).create();
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

    private void showSetMyBodyInformation(String str) {
//        System.out.println("hahah");
//        Toast.makeText(AddPersonaldataActivity.this, "hahahahha", Toast.LENGTH_SHORT).show();
//        View view = LayoutInflater.from(this).inflate(R.layout.lx_test_dialog,null);
//        TextView tv_alert_content = (TextView) view
//                .findViewById(R.id.tv_alert_content);
//        tv_alert_content.setText("留校");

//        View view = LayoutInflater.from(this).inflate(
//                R.layout.set_user_body_information_dialog_test, null);

        View view = LayoutInflater.from(this).inflate(R.layout.set_user_body_information_dialog,null);

        // AlertDialog.Builder setBodyBuilder = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.set_user_body_information_dialog,null);
//        View view = LayoutInflater.from(AddPersonaldataActivity.this).inflate(
//                R.layout.set_user_body_information_dialog, null);
        //	set_user_height_us_information_dialog
//        System.out.println("hahah");
        TextView tvCompany = (TextView) view
                .findViewById(R.id.tv_my_data_set_body_company);
        TextView tvTitle = (TextView) view
                .findViewById(R.id.tv_my_data_dialog_title);
        Button btOk = (Button) view.findViewById(R.id.bt_my_data_set_ok);
        Button btCancel = (Button) view
                .findViewById(R.id.bt_my_data_set_cancel);
        btOk.setOnClickListener(this);
        btCancel.setOnClickListener(new DialogSetBodyInformationOnClick());
        pv_all = (NumberPicker) view
                .findViewById(R.id.picker_my_data_body);
        if (str.equals("weight")) {
            showpopsel=WEIGHT_US;
            if(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.METRIC).equals(SharedPreUtil.NO)){
//                tvTitle.setText("haha");
                tvTitle.setText(R.string.my_weight);
                tvCompany.setText("lbs");
                pv_all.setMaxValue(500);
                pv_all.setMinValue(50);
                int selnumber;
                selnumber= Utils.toint(SharedPreUtil.readPre(
                        AddPersonaldataActivity.this, SharedPreUtil.USER,
                        SharedPreUtil.WEIGHT_US));
                if(selnumber==0){
                    selnumber=120;
                }
                pv_all.setValue(selnumber);

            }else{
                showpopsel=WEIGHT;
//                tvTitle.setText("hhhhh");
                tvTitle.setText(R.string.my_weight);
                tvCompany.setText("kg");
                pv_all.setMaxValue(150);
                pv_all.setMinValue(20);
                int selnumber;
                selnumber=Utils.toint(SharedPreUtil.readPre(
                        AddPersonaldataActivity.this, SharedPreUtil.USER,
                        SharedPreUtil.WEIGHT));
                if(selnumber==0){
                    selnumber=60;
                }
                pv_all.setValue(selnumber);
            }
        } else if (str.equals("height")) {
            showpopsel=HEIGHT;
            tvTitle.setText(R.string.my_stature);
            tvCompany.setText("cm");
            pv_all.setMaxValue(210);
            pv_all.setMinValue(100);
            int selnumber;
            selnumber=Utils.toint(SharedPreUtil.readPre(
                    AddPersonaldataActivity.this, SharedPreUtil.USER,
                    SharedPreUtil.HEIGHT));
            if(selnumber==0){
                selnumber=170;
            }
            pv_all.setValue(selnumber);
        } else if (str.equals("sex")) {
            showpopsel=SEX;
            tvTitle.setText(R.string.my_sex);
            tvCompany.setText("");
            pv_all.setDisplayedValues(mSexArr);
            pv_all.setMaxValue(mSexArr.length - 1);
            int selnumber;
            selnumber=Utils.toint(SharedPreUtil.readPre(
                    AddPersonaldataActivity.this, SharedPreUtil.USER,
                    SharedPreUtil.SEX));

            pv_all.setValue(selnumber);
        }else if (str.equals("metric")) {
//            showpopsel=METRIC;
//            tvTitle.setText(R.string.metric);
//            tvCompany.setText("");
//            pv_all.setDisplayedValues(mMetricArr);
//            pv_all.setMaxValue(mMetricArr.length - 1);
//            int selnumber;
//            if (SharedPreUtil.readPre(MyDataActivity.this, SharedPreUtil.USER,
//                    SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) {
//                selnumber = 1;
//            }else{
//                selnumber = 0;
//            }
//            pv_all.setValue(selnumber);
        }

//		 * //setBodyBuilder.setView(view); setBodyBuilder.setCancelable(false);
//		 * setBodyDialog = setBodyBuilder.show(); Point size = new Point();
//		 * getWindowManager().getDefaultDisplay().getSize(size); int width =
//		 * size.x;
//		 *
//		 * WindowManager.LayoutParams params =
//		 * setBodyDialog.getWindow().getAttributes(); params.width = width;
//		 * params.height = LayoutParams.WRAP_CONTENT; params.gravity =
//		 * Gravity.BOTTOM;
//		 *
//		 * Window window = setBodyDialog.getWindow();
//		 * window.setAttributes(params); window.setContentView(view);
//		 * setBodyDialog.getWindow().setContentView(view);

        TypedArray a = obtainStyledAttributes(new int[] {
                R.attr.global_text_color});

        setNumberPickerTextColor(pv_all, a.getColor(0,Color.parseColor("#4c5157")));

        mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            public void onDismiss() {
                // TODO Auto-generated method stub
                isAlertShow = false;
            }
        });
        mPopupWindow.showAtLocation(mScrollView, Gravity.BOTTOM, 0, 0);
        isAlertShow = true;
    }

    class DialogSetBodyInformationOnClick implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String sex = SharedPreUtil.readPre(AddPersonaldataActivity.this,
                    SharedPreUtil.USER, SharedPreUtil.SEX);
            switch (v.getId()) {
			/*
			 * case R.id.img_my_data_set_sex_men: setSexStr = "1";
			 * initSexImag(setSexStr); break;
			 *
			 * case R.id.img_my_data_set_sex_women: setSexStr = "0";
			 * initSexImag(setSexStr); break;
			 */
                case R.id.bt_my_data_set_ok:
                    String bodyUrl = "";
                    String mid = SharedPreUtil.readPre(AddPersonaldataActivity.this,
                            SharedPreUtil.USER, SharedPreUtil.MID);
                    // updateInformation();
				/*
				 * if (NetWorkUtils.isConnect(MyDataActivity.this)) { if
				 * (!setWeightStr.equals("")) {
				 *
				 * bodyUrl = Constants.URLREVISEINFOPREFIX + "mid=" + mid +
				 * "&sex=" + sex + "&weight=" + setWeightStr;
				 * Log.e("PickerView", " bodyUrl ：" + bodyUrl); if (hc == null)
				 * { hc = HTTPController.getInstance();
				 * hc.open(getApplicationContext()); }
				 * hc.getNetworkData(bodyUrl, handler, SETWEIGHT);
				 * //tvWeight.setText(setWeightStr + " kg"); try {
				 * tvWeight.setText(setWeightStr + " kg"); } catch (Exception e)
				 * { // TODO: handle exception Log.e(TAG,
				 * "numberFormatException()"); } } else if
				 * (!setHeightStr.equals("")) {
				 *
				 * bodyUrl = Constants.URLREVISEINFOPREFIX + "mid=" + mid +
				 * "&sex=" + sex + "&height=" + setHeightStr;
				 * Log.e("PickerView", " bodyUrl ：" + bodyUrl); if (hc == null)
				 * { hc = HTTPController.getInstance();
				 * hc.open(getApplicationContext()); }
				 * hc.getNetworkData(bodyUrl, handler, SETHEIGHT); try {
				 * tvHeight.setText(setHeightStr + " cm"); } catch (Exception e)
				 * { // TODO: handle exception Log.e(TAG,
				 * "numberFormatException()"); }
				 *
				 * }else if(!setSexStr.equals("")){
				 *
				 *
				 * Log.e("PickerView", " 性别 选择了 ：" + setSexStr); String sexMid =
				 * SharedPreUtil.readPre( MyDataActivity.this,
				 * SharedPreUtil.USER, SharedPreUtil.MID); bodyUrl =
				 * Constants.URLREVISEINFOPREFIX + "mid=" + sexMid + "&sex=" +
				 * setSexStr; Log.e("PickerView", " bodyUrl ：" + bodyUrl); if
				 * (hc == null) { hc = HTTPController.getInstance();
				 * hc.open(getApplicationContext()); }
				 * hc.getNetworkData(bodyUrl, handler, SETSEX); isAlertShow
				 * =false; setBodyDialog.dismiss();
				 *
				 * return ;
				 *
				 * }
				 *
				 * } else { Toast.makeText(MyDataActivity.this,
				 * getString(R.string.my_network_disconnected),
				 * Toast.LENGTH_SHORT).show(); }
				 */
                    isAlertShow = false;
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                    setbroadcast();
                    break;

                case R.id.bt_my_data_set_cancel:
                    isAlertShow = false;
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public void setbroadcast() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainService.ACTION_USERDATACHANGE);
        sendBroadcast(broadcastIntent);
    }

    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initheightAndWeight() {
        // TODO  // 英制单位
        if (SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER,
                SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) {   //根据度量单位给身高，体重赋初始值
//            tv_Metric.setText(getString(R.string.imperial_units));
            String ft, in;
            if (SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER,
                    SharedPreUtil.HEIGHT_FT).equals("")) {
                ft = "6";    //身高为空，设置默认值
            } else {
                ft = SharedPreUtil.readPre(AddPersonaldataActivity.this,
                        SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT);
            }
            if (SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER,
                    SharedPreUtil.HEIGHT_IN).equals("")) {
                in = "0";     //身高为空，设置默认值
            } else {
                in = SharedPreUtil.readPre(AddPersonaldataActivity.this,
                        SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN);
            }
            tvHeight.setText(ft +" "+ getString(R.string.imperial_foot) + in +" "
                    + getString(R.string.imperial_inch));
            if (SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER,
                    SharedPreUtil.WEIGHT_US).equals("")) {
                tvWeight.setText("120 lbs");     //体重为空，设置默认值
            } else {
                tvWeight.setText(SharedPreUtil.readPre(AddPersonaldataActivity.this,
                        SharedPreUtil.USER, SharedPreUtil.WEIGHT_US)
                        + " "
                        + getString(R.string.imperial_pound));
            }

        } else {    // 公制单位
            if (SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER,
                    SharedPreUtil.HEIGHT).equals("")) {
                tvHeight.setText("170 "+getString(R.string.centimeter));
            } else {
                tvHeight.setText(SharedPreUtil.readPre(AddPersonaldataActivity.this,
                        SharedPreUtil.USER, SharedPreUtil.HEIGHT) + " "+getString(R.string.centimeter));
            }
            if (SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER,
                    SharedPreUtil.WEIGHT).equals("")) {
                tvWeight.setText("60 "+getString(R.string.kilogram));
            } else {
                tvWeight.setText(SharedPreUtil.readPre(AddPersonaldataActivity.this,
                        SharedPreUtil.USER, SharedPreUtil.WEIGHT) + " "+getString(R.string.kilogram));
            }
        }
    }

    //获得指定文件的byte数组
    public  byte[] getBytes(File file){  // String file
        byte[] buffer = null;
        try {
//                    File file = new File(file);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private void showLoadingDialogNew(final String content) {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(AddPersonaldataActivity.this);
//            mSpotsDialog.setWaitingTitle(getString(R.string.dialog_waitting_text));   setWaitingTitle
            myLoadingDialog.setWaitingTitle(content);
        }
        myLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
            myLoadingDialog.dismiss();
        }
        myLoadingDialog = null;
    }

    private void toModifyUserInfo(RequestCallBackEx<String> respon) { // 根据用户id获取用户信息   String userinfo,
        try {
//            String userId =  SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);
            String userId = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MID);  // 1063024   AddPersonaldataActivity.this
            com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
            reParams.addQueryStringParameter("mid", userId);  //  用户ID TODO 这里发的get请求
            reParams.addQueryStringParameter("name", mNickName);  // 修改昵称
            reParams.addQueryStringParameter("birth", mBirth);            // 修改出生年月
            reParams.addQueryStringParameter("sex", sexStr);
            reParams.addQueryStringParameter("weight", mWeight);
            reParams.addQueryStringParameter("height", mHeight);
//			reParams.addBodyParameter("area_name", "深圳");  // 用户所在地  ---- 不是必须
//			reParams.addBodyParameter("tag", "运动达人");  // 个人标签		---- 不是必须
//			reParams.addBodyParameter("feet_walk", "0.8");  // 走路步长		---- 不是必须
//			reParams.addBodyParameter("feet_run", "0.9");   // 跑步步长		---- 不是必须
//			reParams.addBodyParameter("week", "60000");      // 每周运动目标  ---- 不是必须
//			reParams.addBodyParameter("goal", "12000");      // 目标			---- 不是必须
//			reParams.addBodyParameter("month", "180000");     // 每月运动目标  ---- 不是必须
//			reParams.addBodyParameter("day", "12000");       // 每日运动目标   ---- 不是必须

            String url = ServerConfig.MODIFY_USER_PHOTO;
            if (url.contains(" ")) {
                if (url.substring(url.length() - 1) == " ") {
                    url = url.substring(0, url.length() - 1);
                } else {
                    url = url.replace(" ", "%20");
                }
            }
            if (url.contains("\"")) {
                url = url.replace("\"", "%22");
            }
            if (url.contains("{")) {
                url = url.replace("{", "%7B");
            }
            if (url.contains("}")) {
                url = url.replace("{", "%7D");
            }
            XHttpUtils.getInstance().send(HttpRequest.HttpMethod.GET, url, reParams, respon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseModifyUserInfoData(String content) {
        try {
            String result = content;
            JSONObject jsonObj = new JSONObject(content);
            int nRetCode = jsonObj.optInt("msg");
            if (nRetCode == 0) {
                ////////////////////////////////////////////////////////////////////////////////////
                UserInfo userInfo = new UserInfo();
                JSONObject obj = jsonObj.optJSONObject("data");
                String newUserid = obj.optString("mid");
// TODO ---- 数据解析成功后，将上传成功的数据本地保存
                SharedPreUtil.savePre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.SEX, sexStr);
                if(!StringUtils.isEmpty(mNickName)){
                    SharedPreUtil.savePre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.NAME, mNickName);
                    setheadnamebroadcast();
                }
                SharedPreUtil.savePre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT, mHeight);
                SharedPreUtil.savePre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.WEIGHT, mWeight);
                SharedPreUtil.savePre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.BIRTH, mBirth);
                String img_path = file.getAbsolutePath();
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE, picName + ".JPEG");   //  保存的图片的名字 TODO 上传头像成功后，保存一份到本地
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACEPATH, img_path);   // 保存的图片的路径
//                SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.EMAIL, userEmail);
//                SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.EXPERIENCE, userExperience);
                // 点击完成按钮   ------ 目前做法  点击完成时，只将头像上传给后台
                String userId =  SharedPreUtil.readPre(AddPersonaldataActivity.this, SharedPreUtil.USER, SharedPreUtil.MID);  // 1063024
//				SharedPreUtil.savePre(MyDataActivity.this, SharedPreUtil.USER, SharedPreUtil.FACE, userFace);
                dismissLoadingDialog();
// TODO ---- 数据解析上传成功后，进入主页
                Intent intent = new Intent(this, MainActivity.class);   // 点击完成按钮进入主页
                startActivity(intent);
                finish();
            } else if (nRetCode == 1) {
                dismissLoadingDialog();
                Toast.makeText(AddPersonaldataActivity.this, "mid为空", Toast.LENGTH_SHORT).show();
            } else if (nRetCode == 2) {
                dismissLoadingDialog();
                Toast.makeText(AddPersonaldataActivity.this, "用户未注册", Toast.LENGTH_SHORT).show();
            } else if (nRetCode == 504) {
                dismissLoadingDialog();
                Toast.makeText(AddPersonaldataActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void takeSuccess(TResult result) {
//        Logg.e(TAG, "takeSuccess: " + result + "  :" + result.getImages().size());
        Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(result.getImage().getOriginalPath());
        iv_my_headphoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));

        FileUtils.saveBitmap(bitmap, picName);
        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE, picName + ".JPEG");
        setheadnamebroadcast();
    }

    @Override
    public void takeFail(TResult result, String msg) {
//        Logg.e(TAG, "takeFail: " + result + "  msg=" + msg);
    }

    @Override
    public void takeCancel() {
//        Logg.e(TAG, "takeCancel: ");
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
//                Log.e("EverydayDataActivity", "点击了fanhui按钮");
                finish();
                break;

            default:
                break;
        }
        return false;
    }
}
