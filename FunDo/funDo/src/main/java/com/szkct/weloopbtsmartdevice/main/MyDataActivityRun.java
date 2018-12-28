package com.szkct.weloopbtsmartdevice.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.HealthReportContantsData;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.view.PickerView;
import com.szkct.weloopbtsmartdevice.view.StrericWheelAdapter;
import com.szkct.weloopbtsmartdevice.view.WheelView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kct on 2017/2/9.
 */

public class MyDataActivityRun extends AppCompatActivity implements android.view.View.OnClickListener {

    private static final int TAKE_PICTURE = 4; //    0
    private static final int RESULT_LOAD_IMAGE = 5;// 1
    private static final int CUT_PHOTO_REQUEST_CODE = 6;// 2

    private TextView etName;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvBirth;
    private TextView sexIcon;
    private TextView tvUnit;
//    private RoundImageView headPhoto;

    private String sex = "";
    private String name = "";
    private String setSexStr = "";
    private String setWeightStr = "";
    private String setHeightStr = "";
    private String setBirthdayStr = "";
    private boolean isAlertShow = false;

    private String picName = "";
    private Uri photoUri = null;
    private String imageUrl;
    private Bitmap uploadBitmap = null;
    private float dp;
    private String path = "";

    private PopupWindow mPopupWindow;
    private LinearLayout mScrollView;
    private LinearLayout mUnitConversion;
    private WheelView wheelView;

    public static final int KCT_STYLE_WHITE = 0;
    public static final int KCT_STYLE_BLACK = 1;
    public static final int KCT_STYLE_AUTO = 2;
    public static final String KCT_STYLE_PREFERENCE = "style_preference";
    public static final String KCT_STYLE = "select_style";
    public static final String KCT_UNIT = "unit_conversion";
    public static final String KCT_STYLE_SWITCH_ACTION = "KCT_STYLE_SWITCH_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* SharedPreferences sharedPreferences = getSharedPreferences(SettingHelpFragment.KCT_STYLE_PREFERENCE,
                Context.MODE_APPEND);
        int currentStyle = sharedPreferences.getInt(SettingHelpFragment.KCT_STYLE, SettingHelpFragment.KCT_STYLE_BLACK);
        if (currentStyle == SettingHelpFragment.KCT_STYLE_BLACK) {
            setTheme(R.style.KCTStyleBlack);
        } else if (currentStyle == SettingHelpFragment.KCT_STYLE_WHITE) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            SimpleDateFormat todayHour = new SimpleDateFormat("HH");
            int todayH = Integer.parseInt(todayHour.format(System.currentTimeMillis()));
            if (todayH < 6 || todayH > 17) {
                setTheme(R.style.KCTStyleBlack);
            } else {
                setTheme(R.style.KCTStyleWhite);
            }
        }*/
        setContentView(R.layout.my_data_run);
        init();
    }

    private void downLoadPersonInfoData() {
    }

    private void init() {
//        SharedPreferences sharedPreferences = getSharedPreferences(SettingHelpFragment.KCT_STYLE_PREFERENCE, Context.MODE_APPEND);
//        int currentStyle = sharedPreferences.getInt(SettingHelpFragment.KCT_STYLE, SettingHelpFragment.KCT_STYLE_BLACK);
        mSexArr = new String[] { getString(R.string.my_man), getString(R.string.my_woman) };
        mSexValue = (Integer) SharedPreferencesUtilsRun.getParam(this, HealthReportContantsData.USER_SEX_KEY, 0);
        mAgeValue = (Integer) SharedPreferencesUtilsRun.getParam(this, HealthReportContantsData.USER_AGE_KEY, 18);
        mStatureValue = (Integer) SharedPreferencesUtilsRun.getParam(this, HealthReportContantsData.USER_STATURE_KEY, 170);
        mWeightValue = (Integer) SharedPreferencesUtilsRun.getParam(this, HealthReportContantsData.USER_WEIGHT_KEY, 60);
        mWeightPointValue_kg = (Integer) SharedPreferencesUtilsRun.getParam(this,
                HealthReportContantsDataRun.USER_WEIGHT_POINT_KEY_KG, 0);
        mStatureFootValue = (Integer) SharedPreferencesUtilsRun.getParam(this,
                HealthReportContantsDataRun.USER_STATURE_FOOT_KEY, 6);
        mStatureInchValue = (Integer) SharedPreferencesUtilsRun.getParam(this,
                HealthReportContantsDataRun.USER_STATURE_INCH_KEY, 0);
        mWeightPoundValue = (Integer) SharedPreferencesUtilsRun.getParam(this,
                HealthReportContantsDataRun.USER_WEIGHT_POUND_KEY, 120);
        mWeightPointValue = (Integer) SharedPreferencesUtilsRun.getParam(this,
                HealthReportContantsDataRun.USER_WEIGHT_POINT_KEY, 0);

        findViewById(R.id.re_sex).setOnClickListener(new MyOnClickEvent());
        findViewById(R.id.re_height).setOnClickListener(new MyOnClickEvent());
        findViewById(R.id.re_weight).setOnClickListener(new MyOnClickEvent());  //体重
        findViewById(R.id.re_birth).setOnClickListener(new MyOnClickEvent());
        findViewById(R.id.re_nickname).setOnClickListener(new MyOnClickEvent());
        findViewById(R.id.back).setOnClickListener(new MyOnClickEvent());

//        headPhoto = (RoundImageView) findViewById(R.id.iv_my_headphoto);
//        headPhoto.setOnClickListener(new MyOnClickEvent());

        String picName = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE);
        if (!picName.equals(null) && !picName.equals("")) {
//            Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(FileUtils.SDPATH + picName);
//            headPhoto.setImageBitmap(bitmap);
        } else {
            if (mSexValue == 0) {
//                if (currentStyle == SettingHelpFragment.KCT_STYLE_WHITE) {
//                    headPhoto.setImageResource(R.drawable.my_head_default_male);
//                } else {
//                    headPhoto.setImageResource(R.drawable.my_head_default_male_black);
//                }
            } else {
//                if (currentStyle == SettingHelpFragment.KCT_STYLE_WHITE) {
//                    headPhoto.setImageResource(R.drawable.my_head_default_female);
//                } else {
//                    headPhoto.setImageResource(R.drawable.my_head_default_female_black);
//                }
            }
        }

        mScrollView = (LinearLayout) findViewById(R.id.li_myData);
        etName = (TextView) findViewById(R.id.tv_my_name);
        tvHeight = (TextView) findViewById(R.id.tv_height);
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        tvBirth = (TextView) findViewById(R.id.tv_birth);
        sexIcon = (TextView) findViewById(R.id.iv_sex_icon);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        mUnitConversion = (LinearLayout) findViewById(R.id.unit_conversion_tv);
        // TODO
        mUnitConversion.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] p = new String[] { getString(R.string.metric_units), getString(R.string.imperial_units) };
                View view = LayoutInflater.from(MyDataActivityRun.this).inflate(R.layout.pop_menu, null);
                wheelView = (WheelView) view.findViewById(R.id.targetWheel);
                wheelView.setAdapter(new StrericWheelAdapter(p));
                SharedPreferences preferences = getSharedPreferences("unit_preference", Context.MODE_APPEND);
                wheelView.setCurrentItem(preferences.getInt(KCT_UNIT, 0));
                wheelView.setCyclic(false);
                wheelView.setInterpolator(new AnticipateOvershootInterpolator());

                view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPopupWindow != null && mPopupWindow.isShowing()) {
                            mPopupWindow.dismiss();
                        }
                    }
                });
                view.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPopupWindow != null && mPopupWindow.isShowing()) {
                            SharedPreferences.Editor editor = getSharedPreferences("unit_preference", Context.MODE_APPEND).edit();
                            editor.putInt(KCT_UNIT, wheelView.getCurrentItem());
                            editor.commit();
                            Intent intent = new Intent("unit_preference");
                            intent.putExtra("unit_conversion", wheelView.getCurrentItem());
                            sendBroadcast(intent);
                            initUnit();
                            mPopupWindow.dismiss();
                        }
                    }
                });

                mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT, true);
//                mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);    99999999999999999999999999999999999999999999
                mPopupWindow.showAtLocation(mScrollView, Gravity.BOTTOM, 0, 0);
            }
        });

        sexIcon.setText(mSexArr[mSexValue]);
        initUnit();

        if (SharedPreUtil.readPre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.BIRTH).equals("")) {
            tvBirth.setText("2009-12-31");
        } else {
            tvBirth.setText(SharedPreUtil.readPre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.BIRTH));
        }

        if (SharedPreUtil.readPre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.NAME).equals("")) {
//            etName.setText(getString(R.string.not_set_info));
        } else {
            etName.setText(SharedPreUtil.readPre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.NAME));
        }
    }

    private void initUnit() {
        SharedPreferences sharedPreferences = getSharedPreferences("unit_preference", Context.MODE_APPEND);
        if (sharedPreferences.getInt(KCT_UNIT, 0) == 0) {
            tvUnit.setText(getString(R.string.metric_units));
            tvHeight.setText(String.valueOf(mStatureValue) + "cm");
            tvWeight.setText(String.valueOf(mWeightValue) + "." + String.valueOf(mWeightPointValue_kg) + "kg");
        } else {
            tvUnit.setText(getString(R.string.imperial_units));
            tvHeight.setText(String.valueOf(mStatureFootValue) + "'" + String.valueOf(mStatureInchValue) + "\"");
            tvWeight.setText(String.valueOf(mWeightPoundValue) + "." + String.valueOf(mWeightPointValue)
                    + getString(R.string.imperial_pound));
        }
    }

    class MyOnClickEvent implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.tv_save:
                    break;

                case R.id.iv_my_headphoto:
                    showChooseDialog();
                    break;

//                case R.id.my_big_head_photo:
//                    break;

                case R.id.re_nickname:
                    showSetName(etName.getText().toString());
                    break;

                case R.id.re_sex:
                    showPopupWindow(0);
                    // if (!isAlertShow) {
                    // showSetMyBodyInformation("sex");
                    // }
                    break;

                case R.id.re_height:
                    showPopupWindow(2);
                    // if (!isAlertShow) {
                    // showSetMyBodyInformation("height");
                    // }
                    break;

                case R.id.re_weight:
                    showPopupWindow(3);
                    // if (!isAlertShow) {
                    // showSetMyBodyInformation("weight");
                    // }
                    break;

                case R.id.re_birth:
                    if (!isAlertShow) {
                        showSetMyBirthdayInformation();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void showChooseDialog() {
       /* AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.please_select);
        alert.setItems(R.array.photograph, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 0) {
                    photo();
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });
        alert.show();*/
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == 0) {
                    break;
                }
                picName = startPhotoZoom(photoUri);
                break;

            case RESULT_LOAD_IMAGE:
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        picName = startPhotoZoom(uri);
                    }
                }
                break;

            case CUT_PHOTO_REQUEST_CODE:
//                if (resultCode == RESULT_OK && null != data) {// 裁剪返回
//                    Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(imageUrl);
//                    FileUtils.deleteDir(FileUtils.SDPATH);
//                    uploadBitmap = ImageCacheUtil.createFramedPhoto(480, 480, bitmap, (int) (dp * 1.6f));
//                    FileUtils.saveBitmap(uploadBitmap, picName);
//                    File file = new File(FileUtils.SDPATH, picName + ".JPEG");
//                    headPhoto.setImageBitmap(uploadBitmap);
//                    SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE,
//                            picName + ".JPEG");
//                }
                break;
        }
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
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String startPhotoZoom(Uri uri) {
       /* try {
            // 获取系统时间 然后将裁剪后的图片保存至指定的文件夹
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            String address = sDateFormat.format(new java.util.Date());
            if (!FileUtils.isFileExist("")) {
                FileUtils.createSDDir("");

            }
            imageUrl = FileUtils.SDPATH + address + ".JPEG";
            Uri imageUri = Uri.fromFile(new File(imageUrl));

            final Intent intent = new Intent("com.android.camera.action.CROP");

            // 照片URL地址
            intent.setDataAndType(uri, "image*//*");

            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 480);
            intent.putExtra("outputY", 480);
            // 输出路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // 输出格式
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            // 不启用人脸识别
            intent.putExtra("noFaceDetection", false);
            intent.putExtra("return-data", false);
            startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    private void showSetName(String str) {
        AlertDialog.Builder setNameDialog = new AlertDialog.Builder(MyDataActivityRun.this);
        setNameDialog.setTitle(R.string.my_set_name);
        final EditText ed = new EditText(this);
        ed.setMaxLines(1);
        ed.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
        if (!SharedPreUtil.readPre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.NAME).equals("")) {
            ed.setText(str);
        }
        setNameDialog.setView(ed);
        setNameDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = ed.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.nickname_null), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                SharedPreUtil.savePre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.NAME, name);
                etName.setText(name);
            }
        });
        setNameDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        setNameDialog.show();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.quit_login_dialog);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // initFile();
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID, "");
                // finish();
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showSetMyBodyInformation(String str) {
        setWeightStr = "";
        setHeightStr = "";
        setSexStr = "";
        View view = LayoutInflater.from(this).inflate(R.layout.set_user_body_information_dialog_run, null);
        TextView tvCompany = (TextView) view.findViewById(R.id.tv_my_data_set_body_company);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_my_data_dialog_title);
        Button btOk = (Button) view.findViewById(R.id.bt_my_data_set_ok);
        Button btCancel = (Button) view.findViewById(R.id.bt_my_data_set_cancel);
        btOk.setOnClickListener(new DialogSetBodyInformationOnClick());
        btCancel.setOnClickListener(new DialogSetBodyInformationOnClick());
        PickerView pv = (PickerView) view.findViewById(R.id.picker_my_data_body);
        if (str.equals("weight")) {
            tvTitle.setText(R.string.my_weight);
            tvCompany.setText("kg");
            List<String> dataWeight = new ArrayList<String>();
            for (int i = 20; i < 151; i++) {
                dataWeight.add("" + i);
            }
            pv.setData(dataWeight);
            int weight;
            try {
                weight = Integer
                        .parseInt(SharedPreUtil.readPre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.WEIGHT));
                if (weight < 151 && weight >= 20) {
                    pv.setSelected(weight - 20);
                }
            } catch (NumberFormatException e) {
                weight = 60;
                pv.setSelected(weight - 20);
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.WEIGHT, weight + "");
            }
            pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    setWeightStr = text;
                }
            });
        } else if (str.equals("height")) {
            tvTitle.setText(R.string.my_stature);
            tvCompany.setText("cm");
            List<String> dataHeight = new ArrayList<String>();
            for (int i = 100; i < 211; i++) {
                dataHeight.add("" + i);
            }
            pv.setData(dataHeight);
            int saveHeight;
            try {
                saveHeight = Integer
                        .parseInt(SharedPreUtil.readPre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT));
                if (saveHeight >= 100) {
                    pv.setSelected(saveHeight - 100);
                }
            } catch (NumberFormatException e) {
                saveHeight = 170;
                pv.setSelected(saveHeight - 100);
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.HEIGHT,
                        saveHeight + "");
            }
            pv.setOnSelectListener(new PickerView.onSelectListener() {

                @Override
                public void onSelect(String text) {
                    setHeightStr = text;
                }
            });
        } else if (str.equals("sex")) {
            tvTitle.setText(R.string.my_sex);
            tvCompany.setText("");

            List<String> dataHeight = new ArrayList<String>();

            dataHeight.add(

                    getString(R.string.my_man));
            dataHeight.add(getString(R.string.my_woman));
            pv.setData(dataHeight);
            int saveHeight;
            try {
                saveHeight = Integer
                        .parseInt(SharedPreUtil.readPre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.SEX));
                pv.setSelected(saveHeight);
            } catch (NumberFormatException e) {
                saveHeight = 0;
                pv.setSelected(0);
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.SEX, saveHeight + "");
            }
            pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    setSexStr = text;
                }
            });
        }
        mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            public void onDismiss() {
                isAlertShow = false;
            }

        });
        mPopupWindow.showAtLocation(mScrollView, Gravity.BOTTOM, 0, 0);
        isAlertShow = true;
    }

    private void showSetMyBirthdayInformation() {
        View view = LayoutInflater.from(this).inflate(R.layout.set_user_birthday_information_dialog_run, null);
        final DatePicker dpBirthday = (DatePicker) view.findViewById(R.id.dp_my_data_set_birthday);
        dpBirthday.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        dpBirthday.setMaxDate(System.currentTimeMillis());
        if (!tvBirth.getText().toString().equals("")) {
            String birhStr = tvBirth.getText().toString();
            // String[] birthStrings = birhStr.split("\\.");
            String[] birthStrings = birhStr.split("-");
            int year_int = Integer.parseInt(birthStrings[0]);
            int month_int = Integer.parseInt(birthStrings[1]);
            int day_int = Integer.parseInt(birthStrings[2]);
            dpBirthday.updateDate(year_int, month_int - 1, day_int);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Button btOk = (Button) view.findViewById(R.id.bt_my_data_set_birthday_ok);
        Button btCancel = (Button) view.findViewById(R.id.bt_my_data_set_birthday_cancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpBirthday.clearFocus();
                setBirthdayStr = dpBirthday.getYear() + "-" + (dpBirthday.getMonth() + 1) + "-"
                        + dpBirthday.getDayOfMonth() + "";
                tvBirth.setText(setBirthdayStr);
                SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.BIRTH, setBirthdayStr);
                SharedPreferences preferences = getSharedPreferences("unit_preference", Context.MODE_APPEND);
                Intent intent = new Intent("unit_preference");
                intent.putExtra("unit_conversion", preferences.getInt(KCT_UNIT, 0));
                sendBroadcast(intent);
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }
        });
        btCancel.setOnClickListener(new DialogSetBodyInformationOnClick());
        isAlertShow = true;

        mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                isAlertShow = false;
            }
        });
        mPopupWindow.showAtLocation(mScrollView, Gravity.BOTTOM, 0, 0);
    }

    class DialogSetBodyInformationOnClick implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_my_data_set_ok:
                    if (!setWeightStr.equals("")) {
                        SharedPreUtil.savePre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.WEIGHT, setWeightStr);
                        tvWeight.setText(setWeightStr + " kg");
                    }
                    if (!setHeightStr.equals("")) {
                        SharedPreUtil.savePre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT, setHeightStr);
                        tvHeight.setText(setHeightStr + " cm");
                    }
                    if (!setSexStr.equals("")) {
                        if (setSexStr.equals(getString(R.string.my_man))) {
                            SharedPreUtil.savePre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.SEX, "0");
                        } else {
                            SharedPreUtil.savePre(MyDataActivityRun.this, SharedPreUtil.USER, SharedPreUtil.SEX, "1");
                        }
                        sexIcon.setText(setSexStr);
                    }
                    isAlertShow = false;
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                    break;

                case R.id.bt_my_data_set_birthday_cancel:
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

    private NumberPicker mAgePicker, mSexPicker, mStaturePicker, mWeightPicker;
    private NumberPicker stature_picker_imperial, stature_inch_picker_imperial, weight_picker_imperial,
            weight_point_picker_imperial, weight_point_picker;
    private View mUserInfo;
    private TextView mSure, mCancel;
    private LinearLayout health_report_weight;
    private LinearLayout health_report_stature;
    private LinearLayout health_report_stature_ll;
    private LinearLayout health_report_weight_ll;
    private String[] mSexArr = new String[2];
    private int mSexValue, mAgeValue, mStatureValue, mWeightValue, mWeightPointValue_kg;
    private int mStatureFootValue, mStatureInchValue, mWeightPoundValue, mWeightPointValue;

    private void showPopupWindow(int i) {
        if (mUserInfo == null) {
            mUserInfo = LayoutInflater.from(this).inflate(R.layout.popup_user_info_run, null);
            mAgePicker = (NumberPicker) mUserInfo.findViewById(R.id.age_picker);
            mSexPicker = (NumberPicker) mUserInfo.findViewById(R.id.sex_picker);
            mStaturePicker = (NumberPicker) mUserInfo.findViewById(R.id.stature_picker);
            mWeightPicker = (NumberPicker) mUserInfo.findViewById(R.id.weight_picker);
            stature_picker_imperial = (NumberPicker) mUserInfo.findViewById(R.id.stature_picker_imperial);
            stature_inch_picker_imperial = (NumberPicker) mUserInfo.findViewById(R.id.stature_inch_picker_imperial);
            weight_picker_imperial = (NumberPicker) mUserInfo.findViewById(R.id.weight_picker_imperial);
            weight_point_picker_imperial = (NumberPicker) mUserInfo.findViewById(R.id.weight_point_picker_imperial);
            weight_point_picker = (NumberPicker) mUserInfo.findViewById(R.id.weight_point_picker);
            health_report_stature = (LinearLayout) mUserInfo.findViewById(R.id.health_report_stature);
            health_report_weight = (LinearLayout) mUserInfo.findViewById(R.id.health_report_weight);
            health_report_stature_ll = (LinearLayout) mUserInfo.findViewById(R.id.health_report_stature_);
            health_report_weight_ll = (LinearLayout) mUserInfo.findViewById(R.id.health_report_weight_);
            mSure = (TextView) mUserInfo.findViewById(R.id.tv_ok);
            mSure.setOnClickListener(this);
            mCancel = (TextView) mUserInfo.findViewById(R.id.tv_cancel);
            mCancel.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            mSexPicker.setDisplayedValues(mSexArr);
            mSexPicker.setMaxValue(mSexArr.length - 1);
            mSexPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            mAgePicker.setMinValue(18);
            mAgePicker.setMaxValue(70);
            mAgePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            mStaturePicker.setMinValue(100);
            mStaturePicker.setMaxValue(210);
            mStaturePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            mWeightPicker.setMinValue(20);
            mWeightPicker.setMaxValue(150);
            mWeightPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            stature_picker_imperial.setMinValue(4);
            stature_picker_imperial.setMaxValue(8);
            stature_picker_imperial.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            stature_inch_picker_imperial.setMinValue(0);
            stature_inch_picker_imperial.setMaxValue(11);
            stature_inch_picker_imperial.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            weight_picker_imperial.setMinValue(40);
            weight_picker_imperial.setMaxValue(500);
            weight_picker_imperial.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            weight_point_picker_imperial.setMinValue(0);
            weight_point_picker_imperial.setMaxValue(9);
            weight_point_picker_imperial.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            weight_point_picker.setMinValue(0);
            weight_point_picker.setMaxValue(9);
            weight_point_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            mSexPicker.setValue(mSexValue);
            mAgePicker.setValue(mAgeValue);
            mStaturePicker.setValue(mStatureValue);
            mWeightPicker.setValue(mWeightValue);
            weight_point_picker.setValue(0);
            stature_picker_imperial.setValue(mStatureFootValue);
            stature_inch_picker_imperial.setValue(mStatureInchValue);
            weight_picker_imperial.setValue(mWeightPoundValue);
            weight_point_picker_imperial.setValue(mWeightPointValue);
//			setNumberPickerTextColor(mSexPicker, getResources().getColor(R.color.white));
//			setNumberPickerTextColor(mAgePicker, getResources().getColor(R.color.white));
//			setNumberPickerTextColor(mStaturePicker, getResources().getColor(R.color.white));
//			setNumberPickerTextColor(mWeightPicker, getResources().getColor(R.color.white));
//			setNumberPickerTextColor(weight_point_picker, getResources().getColor(R.color.white));
//			setNumberPickerTextColor(stature_picker_imperial, getResources().getColor(R.color.white));
//			setNumberPickerTextColor(stature_inch_picker_imperial, getResources().getColor(R.color.white));
//			setNumberPickerTextColor(weight_picker_imperial, getResources().getColor(R.color.white));
//			setNumberPickerTextColor(weight_point_picker_imperial, getResources().getColor(R.color.white));
        }
        if (i == 0) {
            mSexPicker.setVisibility(View.VISIBLE);
            mAgePicker.setVisibility(View.GONE);
            health_report_stature.setVisibility(View.GONE);
            health_report_weight.setVisibility(View.GONE);
            health_report_stature_ll.setVisibility(View.GONE);
            health_report_weight_ll.setVisibility(View.GONE);
        } else if (i == 1) {
            mSexPicker.setVisibility(View.GONE);
            mAgePicker.setVisibility(View.VISIBLE);
            health_report_stature.setVisibility(View.GONE);
            health_report_weight.setVisibility(View.GONE);
            health_report_stature_ll.setVisibility(View.GONE);
            health_report_weight_ll.setVisibility(View.GONE);
        } else if (i == 2) {
            SharedPreferences sharedPreferences = getSharedPreferences("unit_preference", Context.MODE_APPEND);
            if (sharedPreferences.getInt(KCT_UNIT, 0) == 0) {
                mSexPicker.setVisibility(View.GONE);
                mAgePicker.setVisibility(View.GONE);
                health_report_stature.setVisibility(View.GONE);
                health_report_weight.setVisibility(View.GONE);
                health_report_stature_ll.setVisibility(View.VISIBLE);
                health_report_weight_ll.setVisibility(View.GONE);
            } else {
                mSexPicker.setVisibility(View.GONE);
                mAgePicker.setVisibility(View.GONE);
                health_report_stature.setVisibility(View.VISIBLE);
                health_report_weight.setVisibility(View.GONE);
                health_report_stature_ll.setVisibility(View.GONE);
                health_report_weight_ll.setVisibility(View.GONE);
            }
        } else if (i == 3) {
            SharedPreferences sharedPreferences = getSharedPreferences("unit_preference", Context.MODE_APPEND);
            if (sharedPreferences.getInt(KCT_UNIT, 0) == 0) {
                mSexPicker.setVisibility(View.GONE);
                mAgePicker.setVisibility(View.GONE);
                health_report_stature.setVisibility(View.GONE);
                health_report_weight.setVisibility(View.GONE);
                health_report_stature_ll.setVisibility(View.GONE);
                health_report_weight_ll.setVisibility(View.VISIBLE);
            } else {
                mSexPicker.setVisibility(View.GONE);
                mAgePicker.setVisibility(View.GONE);
                health_report_stature.setVisibility(View.GONE);
                health_report_weight.setVisibility(View.VISIBLE);
                health_report_stature_ll.setVisibility(View.GONE);
                health_report_weight_ll.setVisibility(View.GONE);
            }
        }
        mPopupWindow = new PopupWindow(mUserInfo, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
//        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.showAtLocation(mScrollView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                // 获取设置好的用户信息参数
                int sexValue = mSexPicker.getValue();
                int ageValue = mAgePicker.getValue();
                int statureValue = mStaturePicker.getValue();
                int statureFootValue = stature_picker_imperial.getValue();
                int statureInchValue = stature_inch_picker_imperial.getValue();
                int weightValue = mWeightPicker.getValue();
                int weightPointValue_kg = weight_point_picker.getValue();
                int weightPoundValue = weight_picker_imperial.getValue();
                int weightPointValue = weight_point_picker_imperial.getValue();
                // 性别
                if (sexValue != mSexValue) {
                    SharedPreferences sharedPreferences = getSharedPreferences(KCT_STYLE_PREFERENCE, Context.MODE_APPEND);
                    int currentStyle = sharedPreferences.getInt(KCT_STYLE, KCT_STYLE_BLACK);
                    mSexValue = sexValue;
                    sexIcon.setText(mSexArr[mSexValue]);
                    SharedPreferencesUtilsRun.setParam(this, HealthReportContantsData.USER_SEX_KEY, mSexValue);
                    String picName = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE);
                    if (picName.equals(null) || picName.equals("")) {
                        if (mSexValue == 0) {
//                            if (currentStyle == KCT_STYLE_WHITE) {
//                                headPhoto.setImageResource(R.drawable.my_head_default_male);
//                            } else {
//                                headPhoto.setImageResource(R.drawable.my_head_default_male_black);
//                            }
                        } else {
//                            if (currentStyle == SettingHelpFragment.KCT_STYLE_WHITE) {
//                                headPhoto.setImageResource(R.drawable.my_head_default_female);
//                            } else {
//                                headPhoto.setImageResource(R.drawable.my_head_default_female_black);
//                            }
                        }
                    }
                }
                // 身高 体重
                SharedPreferences sharedPreferences = getSharedPreferences("unit_preference", Context.MODE_APPEND);
                if (sharedPreferences.getInt(KCT_UNIT, 0) == 0) {
                    if (statureValue != mStatureValue) {
                        mStatureValue = statureValue;
                        tvHeight.setText(String.valueOf(mStatureValue) + "cm");
                        SharedPreferencesUtilsRun.setParam(this, HealthReportContantsData.USER_STATURE_KEY, mStatureValue);
                    }
                    mWeightValue = weightValue;
                    mWeightPointValue_kg = weightPointValue_kg;
                    tvWeight.setText(String.valueOf(mWeightValue) + "." + String.valueOf(mWeightPointValue_kg) + "kg");
                    SharedPreferencesUtilsRun.setParam(this, HealthReportContantsDataRun.USER_WEIGHT_KEY, mWeightValue);
                    SharedPreferencesUtilsRun.setParam(this, HealthReportContantsDataRun.USER_WEIGHT_POINT_KEY_KG,
                            mWeightPointValue_kg);
                } else {
                    mStatureFootValue = statureFootValue;
                    mStatureInchValue = statureInchValue;
                    mWeightPoundValue = weightPoundValue;
                    mWeightPointValue = weightPointValue;
                    tvHeight.setText(String.valueOf(mStatureFootValue) + "'" + String.valueOf(mStatureInchValue) + "\"");
                    tvWeight.setText(String.valueOf(mWeightPoundValue) + "." + String.valueOf(mWeightPointValue)
                            + getString(R.string.imperial_pound));
                    SharedPreferencesUtilsRun.setParam(this, HealthReportContantsDataRun.USER_STATURE_FOOT_KEY,
                            mStatureFootValue);
                    SharedPreferencesUtilsRun.setParam(this, HealthReportContantsDataRun.USER_STATURE_INCH_KEY,
                            mStatureInchValue);
                    SharedPreferencesUtilsRun.setParam(this, HealthReportContantsDataRun.USER_WEIGHT_POUND_KEY,
                            mWeightPoundValue);
                    SharedPreferencesUtilsRun.setParam(this, HealthReportContantsDataRun.USER_WEIGHT_POINT_KEY,
                            mWeightPointValue);

                }
                SharedPreferences preferences = getSharedPreferences("unit_preference", Context.MODE_APPEND);
                Intent intent = new Intent("unit_preference");
                intent.putExtra("unit_conversion", preferences.getInt(KCT_UNIT, 0));
                sendBroadcast(intent);
                mPopupWindow.dismiss();
                break;

            default:
                break;
        }
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
}

// private static InputFilter emojiFilter = new InputFilter() {
// Pattern emoji = Pattern
// .compile(
//
// "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
//
// Pattern.UNICODE_CASE|Pattern.CASE_INSENSITIVE);
//
// @Override public CharSequence filter(CharSequence source,int start,int
// end,Spanned dest,int dstart,int dend){int
// sourceLen=source.toString().length();int
// destLen=dest.toString().length();if(sourceLen>11){return"";}if(sourceLen+destLen>12){return
// source.subSequence(0,12-destLen);}
//
// Matcher emojiMatcher=emoji.matcher(source);
//
// if(emojiMatcher.find()){
//
// return"";
//
// }return null;}};
// public InputFilter[] emojiFilters = {emojiFilter};

