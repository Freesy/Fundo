package com.szkct.weloopbtsmartdevice.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainActivity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.HttpToService;

import java.util.ArrayList;


public class DailogUtils {
    /**
     * 获取库Phon表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;


    /**
     * 联系人名称
     **/
    private static ArrayList<String> mContactsName = new ArrayList<String>();

    /**
     * 联系人头像
     **/
    private static ArrayList<String> mContactsNumber = new ArrayList<String>();

    /**
     * 联系人头像
     **/
    //  private static ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
    static Context mContext = null;

    public static void doNewVersionUpdate(final Context context) {
        MainActivity.isUpDateFlagForMain = true;
        StringBuffer sb = new StringBuffer();
        sb.append(R.string.no_or_update);
        // sb.append("检测到新版本是否更新");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.software_updates);
        builder.setMessage(R.string.no_or_update);
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT, "0");//     0：未评论 1：已评论
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.PAGE_WELCOME_STARTNUM, "0");// 将启动次数置为0
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(HttpToService.updateUrl));
                context.startActivity(intent);

            }
        }).setNegativeButton(R.string.temporarily_not_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (context instanceof MainActivity) {
                    MainActivity settingActivity = (MainActivity) context;
                }
            }
        }).create();
        /*AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();*/
        /**某些情况下会报android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@4c04488 is not valid; is your activity running*/
        if (((Activity) context).isFinishing()) {
            return;
        }
        builder.show();
    }

    // 不更新提示对话框
    public static void notNewVersionUpdate(Context context) {
//        final StringBuffer sb = new StringBuffer();
//        sb.append(R.string.the_current_version);
//        sb.append(R.string.is_the_latest_version);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.software_updates)
                .setMessage(R.string.is_the_latest_version)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
//                        Log.e("tag", sb.toString());
                        dialog.dismiss();
                    }
                }).create();
        builder.show();
    }

    public static void SynchronizeContacts(final Context context) {
        StringBuffer sb = new StringBuffer();
        sb.append(R.string.no_or_update);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.data_synchronization);
        builder.setMessage(R.string.sync_contacts);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MainService.getInstance().getState() != 3) {
                    Toast.makeText(context, context.getString(R.string.contacts_synchronization_failed), Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        mContactsName.clear();
                        mContactsNumber.clear();
                        mContext = context;
                        getPhoneContacts();
                        getSIMContacts();

//                        String contacts = "cont";
//                        for (int s = 0; s < mContactsName.size(); s++) {
//                            contacts = contacts + mContactsName.get(s) + "w50253d" + mContactsNumber.get(s) + "x50253v";
//                        }
//                        MainService.getInstance().sendMessage(contacts);

                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < mContactsNumber.size(); i++) {
                            if (mContactsNumber.size() - 1 != i) {
                                stringBuffer.append(mContactsName.get(i) + "|" + mContactsNumber.get(i) + "^");  // 名字|号码^
                            } else {
                                stringBuffer.append(mContactsName.get(i) + "|" + mContactsNumber.get(i));
                            }
                        }
                        Log.e("PhoneContacts", "Contacts =" + stringBuffer.toString());
                        Log.e("PhoneContacts", "Contacts =" + Utils.bytesToHexString(stringBuffer.toString().getBytes()));
                        mContactsName.clear();
                        mContactsNumber.clear();
//                        return  stringBuffer.toString().getBytes();

//                        byte[] value = L2Send.sendPhoneContacts(this);
                        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.SYN_ADDREST_LIST, stringBuffer.toString().getBytes());  // 手表WiFi  04 4B  TODO --- 同步联系人
                        MainService.getInstance().writeToDevice(l2, true);
                    }
                }.start();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();

            }
        }).create();
        /*AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();*/
        builder.show();
    }

    public static void logE(String tag, String content) {
        int p = 2000;
        long length = content.length();
        if (length < p || length == p)
            Log.e(tag, content);
        else {
            while (content.length() > p) {
                String logContent = content.substring(0, p);
                content = content.replace(logContent, "");
                Log.e(tag, logContent);
            }
            Log.e(tag, content);
        }
    }

    /**
     * 得到手机通讯录联系人信息
     **/
    private static void getPhoneContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                //   Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //得到联系人头像ID
                //    Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                Log.e("phoneNumber", contactName + "====" + phoneNumber);
                //得到联系人头像Bitamp
    /*    Bitmap contactPhoto = null;
        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的  
        if(photoid > 0 ) {  
            Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);  
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);  
            contactPhoto = BitmapFactory.decodeStream(input);  
        }else {  
            contactPhoto = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scan_light);  
        }  */

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber.replace(" ", ""));
                //   mContactsPhonto.add(contactPhoto);
            }

            phoneCursor.close();
        }
    }

    /**
     * 得到手机SIM卡联系人人信息
     **/
    public static void getSIMContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                //Sim卡中没有联系人头像
                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber.replace(" ", ""));
            }
            phoneCursor.close();
        }
    }

    /**
     * tie
     */
    public static void showTip(Context context, final DialogListener mListener) {
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog, null, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams.width = (int) (manager.getDefaultDisplay().getWidth() * 0.8);
        dialog.setContentView(view, layoutParams);
        TextView tv = view.findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.close();
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        dialog.show();
    }

    public interface DialogListener {
        void close();
    }

}
