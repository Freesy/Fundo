package com.szkct.weloopbtsmartdevice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.ElektronikFaturaDao;
//import com.szkct.weloopbtsmartdevice.dialog.Gdata;
import com.szkct.weloopbtsmartdevice.util.Fapiao;
import com.szkct.weloopbtsmartdevice.util.NewUploadDataUtil;
import com.szkct.weloopbtsmartdevice.util.ServerConfig;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.XHttpUtils;

import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.http.HttpCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static u.aly.av.aa;

//import com.kct.fundobeta.btnotification.R;

public class NewSaveElectronicInvoiceActivity extends AppCompatActivity {

    final static String tab_Name="NewSaveElectronicInvoiceActivity";
    KJDB mKJDB;

    EditText et_company_letterhead,et_tax_id,et_address,et_phone,et_bank,et_bank_account,et_remarks;
    TextView tv_1,tv_2,tv_3,tv_4,tv_5,tv_6,tv_7;
    ArrayList<TextView> list_tvs=new ArrayList<>();
    ArrayList<EditText> list_ets=new ArrayList<>();
    Button bt_save;

    Boolean is_modify=false;//true修改界面  false保存界面
    ElektronikFaturaDao mElektronikFaturaDao=new ElektronikFaturaDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_new_save_electronic_invoice);
        mKJDB = KJDB.create(this,tab_Name);
        is_modify = getIntent().getBooleanExtra("is_modify", false);
        if(is_modify)
            mElektronikFaturaDao = new Gson().fromJson(getIntent().getStringExtra("model_json"), ElektronikFaturaDao.class);
        initview();
        initdata();
    }

    private void initdata() {
        if(!is_modify)
            return;
        et_company_letterhead.setText(mElektronikFaturaDao.getCompany_letterhead());
        et_tax_id.setText(mElektronikFaturaDao.getTax_id());
        et_address.setText(mElektronikFaturaDao.getAddress());
        et_phone.setText(mElektronikFaturaDao.getPhone());
        et_bank.setText(mElektronikFaturaDao.getBank());
        et_bank_account.setText(mElektronikFaturaDao.getBank_account());
        et_remarks.setText(mElektronikFaturaDao.getRemarks());
        et_company_letterhead.setSelection(et_company_letterhead.getText().toString().length());
        et_tax_id.setSelection(et_tax_id.getText().toString().length());
        et_address.setSelection(et_address.getText().toString().length());
        et_phone.setSelection(et_phone.getText().toString().length());
        et_bank.setSelection(et_bank.getText().toString().length());
        et_bank_account.setSelection(et_bank_account.getText().toString().length());
        et_remarks.setSelection(et_remarks.getText().toString().length());
    }

    private void initview() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_company_letterhead= (EditText) findViewById(R.id.et_company_letterhead);
        et_tax_id= (EditText) findViewById(R.id.et_tax_id);
        et_address= (EditText) findViewById(R.id.et_address);
        et_phone= (EditText) findViewById(R.id.et_phone);
        et_bank= (EditText) findViewById(R.id.et_bank);
        et_bank_account= (EditText) findViewById(R.id.et_bank_account);
        et_remarks= (EditText) findViewById(R.id.et_remarks);
        list_ets.add(et_company_letterhead);
        list_ets.add(et_tax_id);
        list_ets.add(et_address);
        list_ets.add(et_phone);
        list_ets.add(et_bank);
        list_ets.add(et_bank_account);
        list_ets.add(et_remarks);
        tv_1= (TextView) findViewById(R.id.tv_1);
        tv_2= (TextView) findViewById(R.id.tv_2);
        tv_3= (TextView) findViewById(R.id.tv_3);
        tv_4= (TextView) findViewById(R.id.tv_4);
        tv_5= (TextView) findViewById(R.id.tv_5);
        tv_6= (TextView) findViewById(R.id.tv_6);
        tv_7= (TextView) findViewById(R.id.tv_7);
        list_tvs.add(tv_1);
        list_tvs.add(tv_2);
        list_tvs.add(tv_3);
        list_tvs.add(tv_4);
        list_tvs.add(tv_5);
        list_tvs.add(tv_6);
        list_tvs.add(tv_7);
        for(TextView tv:list_tvs){
            tv.setVisibility(View.INVISIBLE);
        }
        bt_save= (Button) findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    public void save(){
        boolean isok=true;
        for(int i=0;i<list_ets.size();i++) {
            final EditText et = list_ets.get(i);
            final TextView tv = list_tvs.get(i);
            if (TextUtils.isEmpty(et.getText())) {
                isok = false;
                tv.setVisibility(View.VISIBLE);
                et.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (et.length() == 0)
                            tv.setVisibility(View.VISIBLE);
                        else
                            tv.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
        if(!isok)//没填写完成
            return;

        //保存数据库
        et_company_letterhead= (EditText) findViewById(R.id.et_company_letterhead);
        et_tax_id= (EditText) findViewById(R.id.et_tax_id);
        et_address= (EditText) findViewById(R.id.et_address);
        et_phone= (EditText) findViewById(R.id.et_phone);
        et_bank= (EditText) findViewById(R.id.et_bank);
        et_bank_account= (EditText) findViewById(R.id.et_bank_account);
        et_remarks= (EditText) findViewById(R.id.et_remarks);
        mElektronikFaturaDao.setCompany_letterhead(et_company_letterhead.getText().toString());
        mElektronikFaturaDao.setTax_id(et_tax_id.getText().toString());
        mElektronikFaturaDao.setAddress(et_address.getText().toString());
        mElektronikFaturaDao.setPhone(et_phone.getText().toString());
        mElektronikFaturaDao.setBank(et_bank.getText().toString());
        mElektronikFaturaDao.setBank_account(et_bank_account.getText().toString());
        mElektronikFaturaDao.setRemarks(et_remarks.getText().toString());
        mElektronikFaturaDao.setEncode_str(Fapiao.getEncode(mElektronikFaturaDao.toString()));
        //删除服务器
        if(is_modify) { //修改？  更新数据库
            mKJDB.update(mElektronikFaturaDao);
            Intent intent = new Intent();
            intent.putExtra("model_json",new Gson().toJson(mElektronikFaturaDao));
            setResult(8,intent);
            //上传服务器
           /* HashMap map = new HashMap();
//            map.put("mid", Gdata.getMid());   // todo -- 临时注释
            map.put("id", mElektronikFaturaDao.getServer_id());
            map.put("name",mElektronikFaturaDao.getCompany_letterhead());
            map.put("taxNumber",mElektronikFaturaDao.getTax_id());
            map.put("unitAddress",mElektronikFaturaDao.getAddress());
            map.put("phoneNumber",mElektronikFaturaDao.getPhone());
            map.put("bankOfDeposit",mElektronikFaturaDao.getBank());
            map.put("bankAccount",mElektronikFaturaDao.getBank_account());
            map.put("remarks",mElektronikFaturaDao.getRemarks());
            String url = XHttpUtils.newGetURLEncoder(ServerConfig.INVOICE_CHANGE, map);
            NewUploadDataUtil.mKJHttp.get(url, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    Log.e("hrj", "修改发票onSuccess: "+ t );
                }
            });*/
        }else {
            if(mKJDB.findAllByWhere(ElektronikFaturaDao.class,"tax_id='"+mElektronikFaturaDao.getTax_id()+"'").size()>0){
                Toast.makeText(getApplicationContext(), R.string.tax_id_already_exists,Toast.LENGTH_SHORT).show();
                return;
            }

            mKJDB.save(mElektronikFaturaDao);
            //上传服务器
            /*HashMap map = new HashMap();
//            map.put("mid", Gdata.getMid());    // todo -- 临时注释
            map.put("name",mElektronikFaturaDao.getCompany_letterhead());
            map.put("taxNumber",mElektronikFaturaDao.getTax_id());
            map.put("unitAddress",mElektronikFaturaDao.getAddress());
            map.put("phoneNumber",mElektronikFaturaDao.getPhone());
            map.put("bankOfDeposit",mElektronikFaturaDao.getBank());
            map.put("bankAccount",mElektronikFaturaDao.getBank_account());
            map.put("remarks",mElektronikFaturaDao.getRemarks());
            String url = XHttpUtils.newGetURLEncoder(ServerConfig.INVOICE_ADD, map);
            NewUploadDataUtil.mKJHttp.get(url, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    Log.e("hrj", "添加新发票onSuccess: "+ t );
                }
            });*/
        }
        finish();
    }

}
