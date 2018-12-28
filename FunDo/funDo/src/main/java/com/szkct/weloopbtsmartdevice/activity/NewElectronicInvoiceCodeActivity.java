package com.szkct.weloopbtsmartdevice.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.ElektronikFaturaDao;
import com.szkct.weloopbtsmartdevice.util.NewUploadDataUtil;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.XHttpUtils;
import com.szkct.weloopbtsmartdevice.view.AlertDialog;

import org.json.JSONObject;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.http.HttpCallBack;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import static com.szkct.weloopbtsmartdevice.activity.NewElectronicInvoiceActivity.KEY_DEFINDEX_NEWELECTRONICINVOICEACTIVITY;

//import android.support.v7.app.AlertDialog;

//import com.szkct.weloopbtsmartdevice.dialog.Gdata;
//import com.szkct.weloopbtsmartdevice.view.AlertDialog;

//import com.kct.fundobeta.btnotification.R;

public class NewElectronicInvoiceCodeActivity extends AppCompatActivity {

    ImageView iv_invoice_code;//二维码
    TextView tv_name,tv_tax_id,tv_address,tv_phone,tv_bank_name,tv_bank_account,tv_remarks;
    Button bt_modify,bt_def;

    ElektronikFaturaDao mElektronikFaturaDao;//具体是哪一个
    KJDB mKJDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_new_electronic_invoice_code);
        mKJDB = KJDB.create(this,NewSaveElectronicInvoiceActivity.tab_Name);
        mElektronikFaturaDao=new Gson().fromJson(getIntent().getStringExtra("model_json"),ElektronikFaturaDao.class);
        initview();

    }

    private void initview() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override//删除
            public void onClick(View v) {
                new AlertDialog.Builder(NewElectronicInvoiceCodeActivity.this)
                        .setTitle(getString(R.string.prompt))
                        .setMessage(getString(R.string.tip_sure_delete_electronic_invoice))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mKJDB.delete(mElektronikFaturaDao);
                                setResult(NewElectronicInvoiceActivity.delect_resultCode, getIntent());
                                finish();
                                /*HashMap map = new HashMap();
//                                map.put("mid", Gdata.getMid());    // //todo --- 临时注释
                                map.put("id", mElektronikFaturaDao.getServer_id());
                                String url = XHttpUtils.newGet(ServerConfig.INVOICE_DELECT, map);
                                NewUploadDataUtil.mKJHttp.get(url, new HttpCallBack() {
                                    @Override
                                    public void onSuccess(String t) {
                                        super.onSuccess(t);
                                        Log.e("hrj", "删除电子发票onSuccess: "+ t );
                                        try {
                                            JSONObject jsonObject = new JSONObject(t);
                                            if(jsonObject.getInt("msgCode")==0) {
                                                mKJDB.delete(mElektronikFaturaDao);
                                                setResult(NewElectronicInvoiceActivity.delect_resultCode, getIntent());
                                                finish();
                                            }else{
                                                Toast.makeText(getApplicationContext(),R.string.ssdk_sms_dialog_error_desc_100,Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int errorNo, String strMsg) {
                                        super.onFailure(errorNo, strMsg);
                                        Toast.makeText(getApplicationContext(),R.string.ssdk_sms_dialog_error_desc_100,Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                            }
                        })
                        .setNegativeButton(R.string.cancel,null)
                        .show();
            }
        });
        iv_invoice_code= (ImageView) findViewById(R.id.iv_invoice_code);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_tax_id= (TextView) findViewById(R.id.tv_tax_id);
        tv_address= (TextView) findViewById(R.id.tv_address);
        tv_phone= (TextView) findViewById(R.id.tv_phone);
        tv_bank_name= (TextView) findViewById(R.id.tv_bank_name);
        tv_bank_account= (TextView) findViewById(R.id.tv_bank_account);
        tv_remarks= (TextView) findViewById(R.id.tv_remarks);
        bt_modify= (Button) findViewById(R.id.bt_modify);
        bt_modify.setOnClickListener(new View.OnClickListener() {
            @Override//修改
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NewSaveElectronicInvoiceActivity.class);
                intent.putExtra("is_modify",true);
                intent.putExtra("model_json",new Gson().toJson(mElektronikFaturaDao));
                startActivityForResult(intent,8);
            }
        });
        //设置为默认
        bt_def= (Button) findViewById(R.id.bt_def);
        bt_def.setOnClickListener(new View.OnClickListener() {
            @Override//默认发票
            public void onClick(View v) {
                List<ElektronikFaturaDao> allByWhere = mKJDB.findAllByWhere(ElektronikFaturaDao.class, "isDefault=" + 1);
                if(allByWhere != null && allByWhere.size() > 0){
                    for (int i = 0; i < allByWhere.size(); i++) {
                        allByWhere.get(i).setIsDefault(0);
                        mKJDB.update(allByWhere.get(i));
                    }
                }
                mElektronikFaturaDao.setIsDefault(1);
                mKJDB.update(mElektronikFaturaDao);
                //默认发票
                SharedPreUtil.savePre(getApplicationContext(),"kct",KEY_DEFINDEX_NEWELECTRONICINVOICEACTIVITY,getIntent().getIntExtra("index",1)+"");

                finish();
               /* HashMap map = new HashMap();
//                map.put("mid", Gdata.getMid());  //todo --- 临时注释
                map.put("id", mElektronikFaturaDao.getServer_id());
                String url = XHttpUtils.newGet(ServerConfig.INVOICE_DEF, map);
                NewUploadDataUtil.mKJHttp.get(url, new HttpCallBack() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        Log.e("hrj", "设置默认电子发票onSuccess: "+t );
                        try {
                            JSONObject jsonObject = new JSONObject(t);
                            if(jsonObject.getInt("code")==0) {
                                SharedPreUtil.savePre(getApplicationContext(),"kct",KEY_DEFINDEX_NEWELECTRONICINVOICEACTIVITY,getIntent().getIntExtra("index",1)+"");
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),R.string.ssdk_sms_dialog_error_desc_100,Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Toast.makeText(getApplicationContext(),R.string.ssdk_sms_dialog_error_desc_100,Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_name.setText(mElektronikFaturaDao.getCompany_letterhead());
        tv_tax_id.setText(mElektronikFaturaDao.getTax_id());
        tv_address.setText(mElektronikFaturaDao.getAddress());
        tv_phone.setText(mElektronikFaturaDao.getPhone());
        tv_bank_name.setText(mElektronikFaturaDao.getBank());
        tv_bank_account.setText(mElektronikFaturaDao.getBank_account());
        tv_remarks.setText(mElektronikFaturaDao.getRemarks());

        Bitmap qrBitmap = generateBitmap(mElektronikFaturaDao.getEncode_str(),400, 400);
        iv_invoice_code.setImageBitmap(qrBitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==8&&resultCode==8)
            mElektronikFaturaDao=new Gson().fromJson(data.getStringExtra("model_json"),ElektronikFaturaDao.class);
    }

    private Bitmap generateBitmap(String url, int width, int height) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Log.e("hrj", "generateBitmap: "+url);
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
