package com.szkct.weloopbtsmartdevice.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.kct.fundo.btnotification.R;
import com.mtk.app.applist.FileUtils;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.takephoto.app.TakePhoto;
import com.szkct.takephoto.app.TakePhotoImpl;
import com.szkct.takephoto.model.CropOptions;
import com.szkct.takephoto.model.InvokeParam;
import com.szkct.takephoto.model.TContextWrap;
import com.szkct.takephoto.model.TResult;
import com.szkct.takephoto.permission.InvokeListener;
import com.szkct.takephoto.permission.PermissionManager;
import com.szkct.takephoto.permission.TakePhotoInvocationHandler;
import com.szkct.weloopbtsmartdevice.login.Logg;
import com.szkct.weloopbtsmartdevice.util.IRequestListener;
import com.szkct.weloopbtsmartdevice.util.ImageCacheUtil;
import com.szkct.weloopbtsmartdevice.util.NewUploadDataUtil;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.kymjs.kjframe.KJBitmap;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;

//import com.kct.fundobeta.btnotification.R;

public class ReceivingCodeActivity extends AppCompatActivity  implements IRequestListener, TakePhoto.TakeResultListener, InvokeListener {

    int flag;//0微信 1支付宝

    Button bt_synchronize,bt_change_img;
    ImageView iv_weixin_img,iv_alipay_img;
//    View rl_pop_change;

    private static final int TAKE_PICTURE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int CUT_PHOTO_REQUEST_CODE = 2;
    String picName;
    final static String WEIXIN_KEYCODE="TWO_WEIXIN_KEYCODE";
    final static String ALIPAY_KEYCODE="TWO_ALIPAY_KEYCODE";
    final static String WEIXIN_KEY_IMG_PATH="WEIXIN_KEY_IMG_PATH";
    final static String ALIPAY_IMG_PATH="ALIPAY_IMG_PATH";
    String weixin_msg,alipay_msg;

    private Uri photoUri = null;
    private String path = "";
    private String imageUrl;

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_receiving_code);
        initview();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    private void initview() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_synchronize=(Button)findViewById(R.id.bt_synchronize);
        iv_weixin_img=(ImageView)findViewById(R.id.iv_weixin_img);
        iv_alipay_img=(ImageView)findViewById(R.id.iv_alipay_img);
        bt_change_img=(Button)findViewById(R.id.bt_change_img);
//        rl_pop_change=findViewById(R.id.rl_pop_change);
//        rl_pop_change.setVisibility(View.INVISIBLE);
//        rl_pop_change.setOnClickListener(new View.OnClickListener() {
//            @Override//关闭弹出view
//            public void onClick(View v) {
//                rl_pop_change.setVisibility(View.INVISIBLE);
//            }
//        });
        bt_change_img.setOnClickListener(new View.OnClickListener() {
            @Override//更换图片 清除图片
            public void onClick(View v) {
//                rl_pop_change.setVisibility(View.INVISIBLE);
//                selectImg();
                iv_weixin_img.setImageResource(0);
                iv_alipay_img.setImageResource(0);
                SharedPreUtil.delPre(getApplicationContext(),SharedPreUtil.USER, WEIXIN_KEY_IMG_PATH);
                SharedPreUtil.delPre(getApplicationContext(),SharedPreUtil.USER, ALIPAY_IMG_PATH);
                SharedPreUtil.delPre(getApplicationContext(),SharedPreUtil.USER, ALIPAY_KEYCODE);
                SharedPreUtil.delPre(getApplicationContext(),SharedPreUtil.USER, WEIXIN_KEYCODE);
            }
        });
        findViewById(R.id.iv_select_weixin).setOnClickListener(new View.OnClickListener() {
            @Override//微信
            public void onClick(View v) {
//                rl_pop_change.setVisibility(View.VISIBLE);
                flag=0;
                selectImg();
            }
        });
        findViewById(R.id.iv_select_alipay).setOnClickListener(new View.OnClickListener() {
            @Override//支付宝
            public void onClick(View v) {
//                rl_pop_change.setVisibility(View.VISIBLE);
                flag=1;
                selectImg();
            }
        });
        bt_synchronize.setOnClickListener(new View.OnClickListener() {
            @Override//同步
            public void onClick(View v) {
                if(alipay_msg!=null)
                    BluetoothMtkChat.getInstance().sendPayment(0,alipay_msg);
                else
                    BluetoothMtkChat.getInstance().sendClearPayment(0);
                if(weixin_msg!=null)
                    BluetoothMtkChat.getInstance().sendPayment(1,weixin_msg);
                else
                    BluetoothMtkChat.getInstance().sendClearPayment(1);
                Log.e("hrj", "onClick: "+alipay_msg+" "+weixin_msg);
            }
        });

        //加载本地二维码显示
        String path1=SharedPreUtil.readPre(getApplicationContext(),SharedPreUtil.USER, WEIXIN_KEY_IMG_PATH,null);
        if(path1!=null)
            new KJBitmap().display(iv_weixin_img, path1);
        String path2=SharedPreUtil.readPre(getApplicationContext(),SharedPreUtil.USER, ALIPAY_IMG_PATH,null);
        if(path2!=null)
            new KJBitmap().display(iv_alipay_img, path2);

        //加载二维码内容
        alipay_msg=SharedPreUtil.readPre(getApplicationContext(),SharedPreUtil.USER, ALIPAY_KEYCODE,null);
        weixin_msg=SharedPreUtil.readPre(getApplicationContext(),SharedPreUtil.USER, WEIXIN_KEYCODE,null);
    }

    /**
     * 选择图片
     */
    public void selectImg(){
        showChooseDialog();
        /*Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);*/
    }


    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    private void showChooseDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.please_select);
        alert.setItems(R.array.photograph, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                if (arg1 == 0) {
//                    photo();
//                } else {
//                    Intent i = new Intent(
//                            Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(i, RESULT_LOAD_IMAGE);
//                }

                ////////////////////////////////////////////////////////////////////////////////////////////////
                if (arg1 == 0) {
                    getTakePhoto().onPickFromCaptureWithCrop(getPhotoUri(), getBuilder());
                } else {
                    getTakePhoto().onPickFromGalleryWithCrop(getPhotoUri(), getBuilder());
                }
            }
        });
        alert.show();
    }

    public void photo() {
        try {
            Intent openCameraIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);

            String sdcardState = Environment.getExternalStorageState();
            String sdcardPathDir = android.os.Environment
                    .getExternalStorageDirectory().getPath() + "/tempImage/";
            File file = null;
            if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                File fileDir = new File(sdcardPathDir);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }

                file = new File(sdcardPathDir + System.currentTimeMillis()
                        + ".JPEG");
            }
            if (file != null) {
                path = file.getPath();
                photoUri = Uri.fromFile(file);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示二维码
     * @param path
     */
    public void showImgCode(String path){
        Result result_codemsg=Utils.decodeBarcodeRGB(path);//二维码
        Log.e("hrj", "showImgCode: "+path);
        if(result_codemsg==null||result_codemsg.getText()==null){//识别出错了
            Toast.makeText(getApplicationContext(),getString(R.string.qr_code_error),Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("hrj", result_codemsg.getText());
        if(flag==0){
            if(result_codemsg.getText().contains("wxp://")) {
                NewUploadDataUtil.showUserImg(getApplicationContext(), iv_weixin_img, path);
                SharedPreUtil.savePre(getApplicationContext(),SharedPreUtil.USER, WEIXIN_KEY_IMG_PATH, path);
                weixin_msg=result_codemsg.getText();
                SharedPreUtil.savePre(getApplicationContext(),SharedPreUtil.USER, WEIXIN_KEYCODE, weixin_msg);
            }else
                Toast.makeText(getApplicationContext(),getString(R.string.not_wc_qr_code),Toast.LENGTH_SHORT).show();
        }else{
            if(result_codemsg.getText().contains("HTTPS://QR.ALIPAY.COM")) {
                NewUploadDataUtil.showUserImg(getApplicationContext(), iv_alipay_img, path);
                SharedPreUtil.savePre(getApplicationContext(),SharedPreUtil.USER, ALIPAY_IMG_PATH, path);
                alipay_msg=result_codemsg.getText();
                SharedPreUtil.savePre(getApplicationContext(),SharedPreUtil.USER, ALIPAY_KEYCODE, alipay_msg);
            }else
                Toast.makeText(getApplicationContext(),getString(R.string.not_zfb_qr_code),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        getTakePhoto().onActivityResult(requestCode, resultCode, data);  // todo  ?????
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == 0) {
                    break;
                }
                if(null!=photoUri){
                    picName = startNewPhotoZoom(photoUri);
                }
                break;
            case RESULT_LOAD_IMAGE:
                Log.e("RESULT_LOAD_IMAGE", "photoUri=");
                if (data != null && !data.toString().equals("Intent {  }")) {  //进入选择图片界面，未选定图片点击返回时data = Intent {  }
                    Uri uri = data.getData();
                    if (uri != null) {
                        picName = startNewPhotoZoom(uri);
                    }
                }
                break;
            case CUT_PHOTO_REQUEST_CODE:
                if(NewCropperActivity.bitmap!=null&&resultCode==1){
                    Bitmap bitmap = NewCropperActivity.bitmap;
                    FileUtils.saveBitmap(bitmap, picName);
                    showImgCode(FileUtils.SDPATH+picName+ ".JPEG");
                }
                break;
        }
    }

    private String startNewPhotoZoom(Uri uri) {
        try{
            String path="";
            if(uri.toString().contains("content://")) {
                String[] projection = {MediaStore.Video.Media.DATA};
                Cursor cursor = managedQuery(uri, projection, null, null, null);
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                cursor.moveToFirst();
                path=cursor.getString(column_index);
            }else{
                path=new File(new URI(uri.toString())).getAbsolutePath();
            }
            Intent intent = new Intent(getApplicationContext(), NewCropperActivity.class);
            intent.putExtra("path",path);
            startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
            return Utils.setSimpleDateFormat("yyyyMMddhhmmss").format(new java.util.Date());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
//        builder.setOutputX(480).setOutputY(480);
//        builder.setAspectX(480).setAspectY(480);
//        builder.setWithOwnCrop(true);
        return builder.create() ;
    }

    @Override
    public void takeSuccess(TResult result) {
        Logg.e("gkj", "takeSuccess: " + result + "  :" + result.getImages().size());
        Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(result.getImage().getOriginalPath());
//        iv_my_headphoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));

//        if(NewCropperActivity.bitmap!=null&&resultCode==1){
//            Bitmap bitmap = NewCropperActivity.bitmap;
            FileUtils.saveBitmap(bitmap, picName);
            showImgCode(FileUtils.SDPATH+picName+ ".JPEG");
//        }
//
//        FileUtils.saveBitmap(bitmap, picName);
//        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE, picName + ".JPEG");
//        setheadnamebroadcast();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Logg.e("gkj", "takeFail: " + result + "  msg=" + msg);
        String ds = "lkl";
    }

    @Override
    public void takeCancel() {
        Logg.e("gkj", "takeCancel: ");
        String ds = "lkl";
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

    @Override
    public void onPrepare() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onSuccess(String content) {
        String sss = content;
        int i = 9;
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onException(Exception e) {

    }

}
