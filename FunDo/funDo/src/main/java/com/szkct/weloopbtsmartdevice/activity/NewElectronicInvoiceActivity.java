package com.szkct.weloopbtsmartdevice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.ElektronikFaturaDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.ServerElektronikFaturaDao;
import com.szkct.weloopbtsmartdevice.util.Base64;
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

//import com.szkct.weloopbtsmartdevice.dialog.Gdata;
//import com.szkct.weloopbtsmartdevice.util.Base64;
//import com.szkct.weloopbtsmartdevice.util.Fapiao;

//import com.kct.fundobeta.btnotification.R;

public class NewElectronicInvoiceActivity extends AppCompatActivity {    // todo --- 电子发票

    public static final String KEY_DEFINDEX_NEWELECTRONICINVOICEACTIVITY="KEY_DEFINDEX_NEWELECTRONICINVOICEACTIVITY";

    private static final String TAG = "hrj";
    Button bt_synchronize;
    TextView tv_addNew;
    ListView listview;
    KJDB mKJDB;
    MyAdapter myAdapter;

    List<ElektronikFaturaDao> list_data=new ArrayList<>();
//    int def_index=0;//默认的电子发票是那个
    final public static int requestCode=123;
    final public static int delect_resultCode=456;//删除发票

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_new_electronic_invoice);
        mKJDB = KJDB.create(this,NewSaveElectronicInvoiceActivity.tab_Name);
        initview();
    }

    private void initview() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_addNew= (TextView) findViewById(R.id.tv_addNew);  // todo -- 添加按钮
        tv_addNew.setOnClickListener(new View.OnClickListener() {
            @Override//添加新发票
            public void onClick(View v) {
                if(list_data.size()>=5) {
                    Toast.makeText(getApplicationContext(), R.string.limit_electronic_invoices,Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getApplicationContext(),NewSaveElectronicInvoiceActivity.class));
            }
        });
        listview= (ListView) findViewById(R.id.listview);
        myAdapter=new MyAdapter();
        listview.setAdapter(myAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==list_data.size())
                    return;
                Log.e(TAG, "onItemClick: "+ list_data.get(position).toString());
                Intent intent=new Intent(getApplicationContext(),NewElectronicInvoiceCodeActivity.class);
                intent.putExtra("model_json",new Gson().toJson(list_data.get(position)));
                intent.putExtra("index",position);
                startActivityForResult(intent,requestCode);
            }
        });
        bt_synchronize= (Button) findViewById(R.id.bt_synchronize);  // todo -- 同步按钮
        bt_synchronize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSynchronize();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        //默认的发票
//        def_index=Integer.parseInt(SharedPreUtil.readPre(getApplicationContext(),"kct",KEY_DEFINDEX_NEWELECTRONICINVOICEACTIVITY,"0"));
        //先查询本地的数据显示
        list_data=mKJDB.findAll(ElektronikFaturaDao.class);
        myAdapter.notifyDataSetChanged();
        //查询服务器电子发票数据
        //单机，去掉服务器
        /*HashMap map = new HashMap();
//        map.put("mid", Gdata.getMid());    // todo  --- 临时注释
        String url = XHttpUtils.newGet(ServerConfig.INVOICE_SELECTALL, map);
        NewUploadDataUtil.mKJHttp.get(url, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e(TAG, "查询服务器电子发票onSuccess: "+t );
                ServerElektronikFaturaDao mServerElektronikFaturaDao=new Gson().fromJson(t, ServerElektronikFaturaDao.class);
                if(!mServerElektronikFaturaDao.getFlag().equals("success"))
                    return;
                updateSql(mServerElektronikFaturaDao);
            }
        });*/
    }

    /**
     * 对比服务器数据，更新本地数据库
     * @param mServerElektronikFaturaDao
     */
    public void updateSql(ServerElektronikFaturaDao mServerElektronikFaturaDao){
//        int i=0;
        for(ServerElektronikFaturaDao.Data mdata:mServerElektronikFaturaDao.getData()){
            ElektronikFaturaDao mElektronikFaturaDao = new ElektronikFaturaDao();
            mElektronikFaturaDao.setCompany_letterhead(mdata.getName());
            mElektronikFaturaDao.setTax_id(mdata.getTaxNumber());
            mElektronikFaturaDao.setAddress(mdata.getUnitAddress());
            mElektronikFaturaDao.setPhone(mdata.getPhoneNumber());
            mElektronikFaturaDao.setBank(mdata.getBankOfDeposit());
            mElektronikFaturaDao.setBank_account(mdata.getBankAccount());
            mElektronikFaturaDao.setRemarks(mdata.getRemarks());
            mElektronikFaturaDao.setServer_id(mdata.getId()+"");
            mElektronikFaturaDao.setEncode_str(Fapiao.getEncode(mElektronikFaturaDao.toString()));
            mElektronikFaturaDao.setIsDefault(mdata.getIfDefault());
//            if(mdata.getIfDefault()==1){//默认发票？
//                def_index=i;
//            }
//            i++;
            if(mKJDB.findAllByWhere(ElektronikFaturaDao.class,"tax_id='"+mElektronikFaturaDao.getTax_id()+"'").size()>0)
                mKJDB.update(mElektronikFaturaDao,"tax_id='"+mElektronikFaturaDao.getTax_id()+"'");//更新
            else
                mKJDB.save(mElektronikFaturaDao);//保存新的
        }
        //再次查询本地的数据显示
        list_data=mKJDB.findAll(ElektronikFaturaDao.class);
        myAdapter.notifyDataSetChanged();
    }

    class MyAdapter extends BaseAdapter{

        int size=0;

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
                convertView=getLayoutInflater().inflate(R.layout.listitem_activity_new_electronic_invoice,parent,false);
            if(position==size-1) {//优化显示效果
                convertView.findViewById(R.id.ll_show).setVisibility(View.GONE);
                return convertView;
            }else
                convertView.findViewById(R.id.ll_show).setVisibility(View.VISIBLE);

            ElektronikFaturaDao mElektronikFaturaDao=list_data.get(position);
            TextView tv_name= (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_tax_id= (TextView) convertView.findViewById(R.id.tv_tax_id);
            TextView tv_address= (TextView) convertView.findViewById(R.id.tv_address);
            TextView tv_phone= (TextView) convertView.findViewById(R.id.tv_phone);
            TextView tv_bank_name= (TextView) convertView.findViewById(R.id.tv_bank_name);
            TextView tv_bank_account= (TextView) convertView.findViewById(R.id.tv_bank_account);
            TextView tv_remarks= (TextView) convertView.findViewById(R.id.tv_remarks);
            tv_name.setText(mElektronikFaturaDao.getCompany_letterhead());
            tv_tax_id.setText(mElektronikFaturaDao.getTax_id());
            tv_address.setText(mElektronikFaturaDao.getAddress());
            tv_phone.setText(mElektronikFaturaDao.getPhone());
            tv_bank_name.setText(mElektronikFaturaDao.getBank());
            tv_bank_account.setText(mElektronikFaturaDao.getBank_account());
            tv_remarks.setText(mElektronikFaturaDao.getRemarks());

            TextView tv_1=(TextView) convertView.findViewById(R.id.tv_1);
            TextView tv_def_invoice=(TextView) convertView.findViewById(R.id.tv_def_invoice);
            if(mElektronikFaturaDao.getIsDefault()==1){//默认发票
                tv_1.setVisibility(View.INVISIBLE);
                tv_def_invoice.setVisibility(View.VISIBLE);
            }else{
                tv_1.setVisibility(View.VISIBLE);
                tv_def_invoice.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            size=list_data.size()+1;
            super.notifyDataSetChanged();
        }
    }

    /**
     * 发送同步发票指令
     */
    public void sendSynchronize(){
        ArrayList<String> lists_abs=new ArrayList<>();
        ArrayList<String> lists_data=new ArrayList<>();
        int def_index=0,i=0;
        for(ElektronikFaturaDao mElektronikFaturaDao:list_data){
            lists_abs.add(Base64.encode(mElektronikFaturaDao.getRemarks()));
            lists_data.add(mElektronikFaturaDao.getEncode_str());
            if(mElektronikFaturaDao.getIsDefault()==1){//默认发票
                def_index=i;
            }
            i++;
        }
        if(lists_data.size()>0)
            BluetoothMtkChat.getInstance().sendInvoiceDataS(def_index+1,lists_abs,lists_data);
        else
            BluetoothMtkChat.getInstance().sendClearInvoiceDataS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==NewElectronicInvoiceActivity.requestCode&&resultCode==NewElectronicInvoiceActivity.delect_resultCode){
            //删除的是那个? 重新修改指向
//            int index=data.getIntExtra("index",0);
//            if(index<=def_index) {
//                def_index--;
//            }
//            def_index=0;
            SharedPreUtil.savePre(getApplicationContext(),"kct",KEY_DEFINDEX_NEWELECTRONICINVOICEACTIVITY,"0");
            if(list_data.size()==0)
                return;
            //默认发票
            /*HashMap map = new HashMap();
//            map.put("mid", Gdata.getMid());   //todo --- 临时注释
            map.put("id", list_data.get(0).getServer_id());
            String url = XHttpUtils.newGet(ServerConfig.INVOICE_DEF, map);
            NewUploadDataUtil.mKJHttp.get(url, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    Log.e(TAG, "设置默认电子发票onSuccess: "+t );
                }
            });*/
        }
    }
}
