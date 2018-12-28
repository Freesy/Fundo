package com.szkct.weloopbtsmartdevice.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.data.PushPicContent;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.FileInfo;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.json.JSONObject;
import org.kymjs.kjframe.KJBitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：lx
 * 版本：
 * 创建日期：2018/3/15
 * 描述: ${VERSION}
 * 修订历史：
 */

public class WatchPushActivityNew extends Activity {    // AppCompatActivity

    private static final String TAG = WatchPushActivityNew.class.getSimpleName();
    private GridView gridView;
    private GridItemAdapter gridItemAdapter;
    private int click;
    private String[] sendData;
    private HTTPController hc;

    public List<PushPicContent> mlistPic = new ArrayList<>();

    public static KJBitmap mKJBitmap;//全局的图片加载

    public static byte[] fileByte ;

    private MyBroadcast mbroadcast = null;
    private File file;   // 保存的文件 路径
    File files;   // 下载的固件包存放本地的 路径
    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fendoDial/";   // 存放下载固件包的路径    Environment.getExternalStorageDirectory().getAbsolutePath()+"/fendoBk/"

    private ArrayList<String> mFileList;

    private LoadingDialog loadingDialog = null;
    private long syncStartTime = 0;
    private final int SNYBTDATAFAIL = 16;

    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MainService.ACTION_PUSHPIC_FINISH)){
                try {
                    if (null != loadingDialog) {
                        if(loadingDialog.isShowing()){
                            loadingDialog.setCancelable(true);
                            loadingDialog.dismiss();
                            loadingDialog = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(intent.getAction().equals(MainService.ACTION_GETPUSHPIC_SUCCESS)){

            }/////////////////////
        }
    }


    static {
        mKJBitmap=new KJBitmap();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null != loadingDialog) {
                if (System.currentTimeMillis() - syncStartTime > 600 * 1000) {  //  90 * 1000
                    Message msg = handler.obtainMessage(SNYBTDATAFAIL);  // 数据同步失败,稍后重试
                    handler.sendMessage(msg);
                    return;
                }
            }
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SNYBTDATAFAIL:  // 同步失败
                    try {
                        if (null != loadingDialog) {
                            if(loadingDialog.isShowing()){
                                loadingDialog.setCancelable(true);
                                loadingDialog.dismiss();
                                loadingDialog = null;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(WatchPushActivityNew.this,getString(R.string.dialpush_fail), Toast.LENGTH_SHORT).show();  // getString(R.string.profile_downerror)

                    break;

                case 18:
                    String result = msg.obj.toString();
                    Log.e("a", "----------" + result + "-----------");

                   /*
                   返回结果（json）：
                   {"code":0,
                   "data":[{"dialId":84,"adaptiveNumber":301,"dialPictureUrl":"http://wx.funos.cn:8080/fundo-dialPic/Chrysanthemum.jpg","dialFileUrl":"http://wx.funos.cn:8080/fundo-dialFile/test.zip","dialName":"测试1"},
                           {"dialId":85,"adaptiveNumber":301,"dialPictureUrl":"http://wx.funos.cn:8080/fundo-dialPic/Chrysanthemum.jpg","dialFileUrl":"http://wx.funos.cn:8080/fundo-dialFile/test.zip","dialName":"测试2"}],
                           "message":"请求成功"}
                    */
                    try {
                        JSONObject jo = new JSONObject(result);
                        int resultcode = jo.getInt("code");
                        if (resultcode == 0 && jo.has("data")) {
                            String data = jo.getString("data");
                            mlistPic = Utils.getObjectList(data,PushPicContent.class);
                            int d = 999;

                            if(null != mlistPic){
//                                Intent intent = new Intent();
//                                intent.setAction(MainService.ACTION_GETPUSHPIC_SUCCESS);   //TODO  ---  发广播，
//                                sendBroadcast(intent);

                                gridItemAdapter = new GridItemAdapter(mlistPic,WatchPushActivityNew.this);
                                gridView.setAdapter(gridItemAdapter);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        syncStartTime = System.currentTimeMillis();
                                        if(null == loadingDialog ){  // && !loadingDialog.isShowing()
                                            loadingDialog = new LoadingDialog(WatchPushActivityNew.this,R.style.Custom_Progress,getString(R.string.dialpush_ing)); // getString(R.string.esim_init)
                                            loadingDialog.show();
                                            handler.postDelayed(runnable, 1000 * 601);// 打开定时器，执行操作   5分钟
                                        }

                                        final int dialId = mlistPic.get(position).getDialId();  // todo --- 表盘id  3
                                        String filePath = mlistPic.get(position).getDialFileUrl();     //   http://wx.funos.cn:8080/fundo-dialFile/out.bin
                                        String[] sssd = filePath.split("/");
                                        if (!TextUtils.isEmpty(filePath)) {
                                            int type = Integer.parseInt(SharedPreUtil.readPre(WatchPushActivityNew.this, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE));   // TODO -- 升级的平台类型
                                            file = new File(Environment.getExternalStorageDirectory() + "/fendoDial/", sssd[sssd.length - 1]);    //  /storage/emulated/0/fendoDial/out.bin
                                            if (filePath.contains(" ")) {   // http://app.fundo.xyz:8001/version/files/180203034834_B22XR_X2_NRF51822_60_V0.2.0_20180202.zip
                                                if (filePath.substring(filePath.length() - 1) == " ") {
                                                    filePath = filePath.substring(0, filePath.length() - 1);
                                                } else {
                                                    filePath = filePath.replace(" ", "%20");
                                                }
                                            }
                                            if (filePath.contains("\"")) {
                                                filePath = filePath.replace("\"", "%22");
                                            }
                                            if (filePath.contains("{")) {
                                                filePath = filePath.replace("{", "%7B");
                                            }
                                            if (filePath.contains("}")) {
                                                filePath = filePath.replace("{", "%7D");
                                            }

                                            Log.i(TAG, "file.getName =2 " + filePath);

                                            HttpUtils httpUtils = new HttpUtils();
                                            httpUtils.download(filePath, file.getPath(), new RequestCallBack<File>() {
                                                @Override
                                                public void onLoading(long total, long current, boolean isUploading) {
                                                }

                                                @Override
                                                public void onStart() {
                                                }

                                                @Override
                                                public void onSuccess(final ResponseInfo<File> responseInfo) {
                                                    try {
                                                        Log.i(TAG, "file.getName = " + file.getName() + "   file.getPath = " + file.getPath());

                                                        files = new File(path);//     /storage/emulated/0/fendo      ------    /storage/emulated/0/fendoDial2
                                                        if (!files.exists()) {
                                                            file.mkdir();
                                                        }
                                                        File lFolderFile = new File(path);     // todo  ---  获取 本地固件包       // todo --- getOTAFilePath ---- 获取本地固件包的路径       /storage/emulated/0/fendoDial2
                                                        mFileList = new ArrayList<String>();
                                                        if (lFolderFile.listFiles() != null) {
                                                            int size = lFolderFile.listFiles().length;
                                                            for (int forCount = 0; forCount < size; forCount++) {
                                                                File lFile = lFolderFile.listFiles()[forCount];
                                                                FileInfo lFileInfo = new FileInfo(lFile.toString(), lFile.getName());
                                                                if (lFileInfo.getFileName().endsWith(".bin")) {
                                                                    mFileList.add(lFileInfo.getFileName());
                                                                }
                                                            }
                                                        }
                                                        String filePath2 = path + mFileList.get(0).toString();   // TODO  -- 必须要   只取了第一个文件的 名字      /storage/emulated/0/fendoDial//out.bin
                                                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                        fileByte = Utils.readFile(file.getPath());
                                                        int mlen = fileByte.length;   // 32264

//                                        byte[] fileByte2 = Utils.readFile(filePath2);
//                                        int mlen2 = fileByte2.length;  // 32264
                                                        int ddd = 6666;
                                                        byte[] value = new byte[9];
                                                        value[0] = (byte) 0;

//                                                        value[1] = (byte) (dialId & 0xff);
//                                                        value[2] = (byte) (dialId >> 8);
//                                                        value[3] = (byte) (dialId >> 16);
//                                                        value[4] = (byte) (dialId >> 24);
                                                        value[4] = (byte) (dialId & 0xff);
                                                        value[3] = (byte) (dialId >> 8);
                                                        value[2] = (byte) (dialId >> 16);
                                                        value[1] = (byte) (dialId >> 24);

//                                                        value[5] = (byte) (mlen & 0xff);
//                                                        value[6] = (byte) (mlen >> 8);
//                                                        value[7] = (byte) (mlen >> 16);
//                                                        value[8] = (byte) (mlen >> 24);
                                                        value[8] = (byte) (mlen & 0xff);
                                                        value[7] = (byte) (mlen >> 8);
                                                        value[6] = (byte) (mlen >> 16);
                                                        value[5] = (byte) (mlen >> 24);

                                                        L2Send.sendPushDialPicData(value);
                                                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                                    } catch (Exception e) {

                                                    }
                                                }

                                                @Override
                                                public void onFailure(HttpException e, String s) {
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    } catch (Exception e) {
                        Log.e("a", "----------" + e.toString() + "-----------");
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                toMain();
//                            }
//                        }, 1000);

                    } finally {

                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_watchpush);

        gridView = (GridView) findViewById(R.id.watch_push_gridView);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (mbroadcast == null) {
            mbroadcast = new MyBroadcast();
        }
        IntentFilter filter = new IntentFilter();
//        filter.addAction(MainService.ACTION_GETPUSHPIC_SUCCESS);   //
        filter.addAction(MainService.ACTION_PUSHPIC_FINISH);
        registerReceiver(mbroadcast, filter);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE); //TODO -- 设备序号
        hc = HTTPController.getInstance();
        hc.open(this);
        if (NetWorkUtils.isConnect(this)) {
            String url = Constants.FUNDO_UNIFIED_DOMAIN_test + Constants.BIAOPAN_PUSH + code;//0:分动；1：分动手环，2：分动穿戴,3:funfit,4:funrun         http://wx.funos.cn:8080/fundo/dial/getDialList.do?adaptiveNumber=427
            hc.getNetworkStringData(url, handler, 18);
        }else{
            Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                toMain();
//                            }
//                        }, 1600);
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    class GridItem {
        private int imageId;
        public GridItem() {
            super();
        }
        public GridItem(int imageId) {
            super();
            this.imageId = imageId;
        }
        public int getImageId() {
            return imageId;
        }
    }


    public class GridItemAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<PushPicContent> gridItemList;

        public GridItemAdapter(List<PushPicContent> mlist, Context context) {  //     int[] images
            super();
            gridItemList = new ArrayList<>();
            gridItemList = mlist;
            inflater = LayoutInflater.from(context);
//            for (int i = 0; i < mlist.size(); i++) {
////                GridItem picture = new GridItem(images[i]);
//                gridItemList.add(mlist.get(i));
//            }
        }

        @Override
        public int getCount() {
            if (null != gridItemList) {
                return gridItemList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return gridItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.grid_item_new, null);
                convertView = inflater.inflate(R.layout.grid_item, null);
                viewHolder = new ViewHolder();
                viewHolder.dialName = (TextView) convertView.findViewById(R.id.title);
                viewHolder.dialimg = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//            viewHolder.dialName.setText("NO." + String.format(Locale.ENGLISH, "%03d", position));
            viewHolder.dialName.setText(mlistPic.get(position).getDialName());

//            viewHolder.dialimg.setImageResource(allimages[gridItemList.get(position).getImageId() - 1]);
            //            viewHolder.dialimg.setImageURL(mlistPic.get(position).getDialPictureUrl());
            mKJBitmap.display(viewHolder.dialimg, mlistPic.get(position).getDialPictureUrl());

//            if (click == position) {
//                viewHolder.dialimg.setBackgroundResource(R.drawable.pushdial_bg);
//            } else {
//                viewHolder.dialimg.setBackgroundResource(R.color.pushdial_list_item_bg);
//            }
            return convertView;
        }

    }

    static class ViewHolder {
        TextView dialName;
        ImageView dialimg;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mbroadcast != null){
            unregisterReceiver(mbroadcast);
        }
    }
}
