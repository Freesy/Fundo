package com.szkct.weloopbtsmartdevice.main;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
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
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.ImageCacheUtil;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author chendalin 说明：ActionBarActivit对于最新的sdk
 *         20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 */
@SuppressWarnings("ResourceType")
public class MyRegisterDataActivity extends AppCompatActivity implements View.OnClickListener ,TakePhoto.TakeResultListener, InvokeListener {  //个人资料页面
	// private static final int PHOTO_CODE = 1;
	// private static final int XIANGCE_CODE = 2;
	public static final int PHTOT_UPLOAD = 5;
	private final int QQImgWhat = 3;
	private final int SETNAME = 4;
	private static final int SETWEIGHT = 6;
	private static final int SETHEIGHT = 7;
	private static final int SETSEX = 8;
	private static final int SETBIRTH = 9;
	private static final int GETUERINFO = 10;
	private static final int SETNULLNAME = 11;
	private HTTPController hc = null;
	private TextView tvLogout;
	private TextView tv_Metric,etName;
//	private EditText etName;
	private TextView tvHeight;
	private TextView tvWeight;
	private TextView tvBirth;
	private ImageView headPhoto;
	private TextView sexIcon;

	private LinearLayout mScrollView;
	// private Toolbar toolbar;
	private Intent intent;
	private PopupWindow popupWindow;
	private View photoView = null;
	private String name = "";
	private String sex = "";

	private String picName = "";
	private Bitmap uploadBitmap = null;

	private DisplayMetrics dm;
	private String TAG = "MyDataActivity";

	private static final int TAKE_PICTURE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int CUT_PHOTO_REQUEST_CODE = 2;

	private String[] mSexArr = new String[2];
	private String[] mMetricArr = new String[2];
	private static final int HEIGHT_US = 0;
	private static final int HEIGHT= 1;
	private static final int WEIGHT = 2;
	private static final int WEIGHT_US = 3;
	private static final int SEX = 4;
	private static final int METRIC = 5;
	int showpopsel=-1;
	private String path = "";
	private Uri photoUri = null;
	private float dp;
	private String imageUrl;
	// private boolean isMyData = true;
	boolean isFirstReadHelp = false;
	private boolean isAlertShow = false;
	private AlertDialog.Builder setBodyBuilder;
	/* 取得默认的蓝牙适配器 */
	private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	private PopupWindow mPopupWindow;
	 public  InputFilter[] emojiFilters = {emojiFilter};

	 NumberPicker pv_in,pv_ft,pv_all;

	private TakePhoto takePhoto;
	private InvokeParam invokeParam;

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.e("MyDataActivity", "handler what" + msg.what);
			switch (msg.what) {
			case QQImgWhat:
				Bitmap bitmap = (Bitmap) msg.obj;
				FileUtils.saveBitmap(
						bitmap,
						FileUtils.SDPATH
								+ SharedPreUtil.readPre(
										getApplicationContext(),
										SharedPreUtil.USER, SharedPreUtil.FACE));
				headPhoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));
				break;

			case SETNAME:
				String nameStr = (String) msg.obj;
				try {
					JSONObject jsonObj = new JSONObject(nameStr);
					String s = jsonObj.getString("result");
					if (s.equals("0")) {
						Toast.makeText(getApplicationContext(),
								R.string.save_successfully, Toast.LENGTH_SHORT)
								.show();
						if (!name.equals("")) {
							SharedPreUtil.savePre(MyRegisterDataActivity.this,
									SharedPreUtil.USER, SharedPreUtil.NAME,
									name);
							etName.setText(name);
						}
						if (!sex.equals("")) {
							SharedPreUtil.savePre(MyRegisterDataActivity.this,
									SharedPreUtil.USER, SharedPreUtil.SEX, sex);
						} else {
							SharedPreUtil.savePre(MyRegisterDataActivity.this,
									SharedPreUtil.USER, SharedPreUtil.SEX, "0");
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}
				break;

			case PHTOT_UPLOAD:
				String strUpload = (String) msg.obj;
				System.out.println("strUpload = " + strUpload);
				SharedPreUtil.savePre(getApplicationContext(),
						SharedPreUtil.USER, SharedPreUtil.FACEURL, strUpload);
				headPhoto.setImageBitmap(ImageCacheUtil
						.toRoundBitmap(uploadBitmap));
				SharedPreUtil.savePre(getApplicationContext(),
						SharedPreUtil.USER, SharedPreUtil.FACE, picName
								+ ".JPEG");
				Toast.makeText(getApplicationContext(),
						R.string.save_successfully, Toast.LENGTH_SHORT).show();
				break;

			case SETNULLNAME:
				etName.setText(name);
				break;

			default:
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			setTheme(R.style.KCTStyleWhite);
		}else{
			setTheme(R.style.KCTStyleBlack);
		}
		setContentView(R.layout.my_data);  //my_data  activity_personaldata
		init();
		// ----laiQinglin add---//
		downLoadPersonInfoData();// 下载
		// -----add end-----

		getTakePhoto().onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		getTakePhoto().onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tvLogout = (TextView) findViewById(R.id.tv_save);
		if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MID)
				.equals("")) {
			tvLogout.setText(getString(R.string.slidingmenu_login_registered_title));
		} else {
			tvLogout.setText(getString(R.string.write_off));
		}
		tvLogout.setOnClickListener(new MyOnClickEvent());

	}

//	private float XPosition = 0;
//	private float YPosition = 0;
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			XPosition = event.getX();
//			YPosition = event.getY();
//			break;
//
//		case MotionEvent.ACTION_MOVE:
//			if (event.getY() - YPosition > 50 || YPosition - event.getY() > 50) {
//				break;
//			}
//			if (event.getX() - XPosition > 80) {
//				finish();
//				overridePendingTransition(R.anim.push_left_in,
//						R.anim.push_right_out);
//			}
//			break;
//
//		default:
//			break;
//		}
//		return super.onTouchEvent(event);
//	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}

	private void downLoadPersonInfoData() {
		// TODO Auto-generated method stub
		// 获取用户资料的接口：http://www.fundo.cc/export/user_info.php？mid=
		String mid = SharedPreUtil.readPre(MyRegisterDataActivity.this,
				SharedPreUtil.USER, SharedPreUtil.MID);
		String getinfourl = Constants.URLGETUSERINFO + "mid=" + mid;
		if (mid.equals("")) {
			return;
		}
		/*
		 * if (NetWorkUtils.isConnect(MyDataActivity.this)) { if (hc == null) {
		 * hc = HTTPController.getInstance(); hc.open(getApplicationContext());
		 * } hc.getNetworkData(getinfourl, handler, GETUERINFO);
		 *
		 * } else { Toast.makeText(MyDataActivity.this,
		 * getString(R.string.no_net_noweather), Toast.LENGTH_SHORT) .show(); }
		 */
	}

	private void init() {
		mSexArr = new String[] { getString(R.string.my_man),
				getString(R.string.my_woman) };
		mMetricArr = new String[] { getString(R.string.metric_units),
				getString(R.string.imperial_units) };

		setBodyBuilder = new AlertDialog.Builder(this);
		intent = getIntent();
		// 初始化ToolBar
		// 用户信息控件
		findViewById(R.id.re_sex).setOnClickListener(new MyOnClickEvent());  // addlx  --- 性别
		findViewById(R.id.re_height).setOnClickListener(new MyOnClickEvent());
		findViewById(R.id.re_weight).setOnClickListener(new MyOnClickEvent());
		findViewById(R.id.re_birth).setOnClickListener(new MyOnClickEvent());
		findViewById(R.id.re_nickname).setOnClickListener(new MyOnClickEvent());
		findViewById(R.id.re_Metric).setOnClickListener(new MyOnClickEvent());   // addlx  --- 度量单位
		findViewById(R.id.back).setOnClickListener(new MyOnClickEvent());

		mScrollView = (LinearLayout) findViewById(R.id.li_myData);
		headPhoto = (ImageView) findViewById(R.id.iv_my_headphoto);   //addlx 项目中被设置了一张默认的图片

		etName = (TextView) findViewById(R.id.tv_my_name);
//		etName = (EditText) findViewById(R.id.tv_my_name);
		tvHeight = (TextView) findViewById(R.id.tv_height);
		tvWeight = (TextView) findViewById(R.id.tv_weight);
		tvBirth = (TextView) findViewById(R.id.tv_birth);
		sexIcon = (TextView) findViewById(R.id.iv_sex_icon);
		tv_Metric= (TextView) findViewById(R.id.tv_Metric);

	 initheightAndWeight();
		if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
				SharedPreUtil.BIRTH).equals("")) {
			tvBirth.setText("2009.12.31");
		} else {
			tvBirth.setText(SharedPreUtil.readPre(MyRegisterDataActivity.this,
					SharedPreUtil.USER, SharedPreUtil.BIRTH));
		}

		/*
		 * toolbar = (Toolbar) findViewById(R.id.toolbar); if (intent != null &&
		 * intent.hasExtra("userSeting")) { isMyData = false;
		 *
		 * toolbar.setTitle(R.string.user_settings); }
		 * setSupportActionBar(toolbar);
		 * toolbar.setNavigationIcon(R.drawable.action_back_normal);
		 * toolbar.setTitleTextColor
		 * (getResources().getColor(android.R.color.white));
		 */
		sex = SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
				SharedPreUtil.SEX);
		if ("0".equals(sex)) {
			sexIcon.setText(getString(R.string.my_man));
		} else if ("1".equals(sex)) {
			sexIcon.setText(getString(R.string.my_woman));
		}

		// 初始化监听事件


		etName.setText(SharedPreUtil.readPre(MyRegisterDataActivity.this,
				SharedPreUtil.USER, SharedPreUtil.NAME));
		if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
				SharedPreUtil.NAME).equals("")) {
			etName.setText(getString(R.string.not_set_info));

		}

		headPhoto.setOnClickListener(new MyOnClickEvent());

		setHeadPhoto();
		// 状态栏与标题栏一体
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,
					mScrollView, R.color.trajectory_bg);
		}
		// showPhoto(user.getFace(),user.getFaceUrl());
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	private void initheightAndWeight() {
		// TODO Auto-generated method stub
		if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
				SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) {
			tv_Metric.setText(getString(R.string.imperial_units));
			String ft, in;
			if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
					SharedPreUtil.HEIGHT_FT).equals("")) {
				ft = "6";
			} else {
				ft = SharedPreUtil.readPre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT);
			}
			if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
					SharedPreUtil.HEIGHT_IN).equals("")) {
				in = "0";
			} else {
				in = SharedPreUtil.readPre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN);
			}
			tvHeight.setText(ft +" "+ getString(R.string.imperial_foot) + in +" "
					+ getString(R.string.imperial_inch));
			if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
					SharedPreUtil.WEIGHT_US).equals("")) {
				tvWeight.setText("120 lbs");
			} else {
				tvWeight.setText(SharedPreUtil.readPre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.WEIGHT_US)
						+ " "
						+ getString(R.string.imperial_pound));
			}

		} else {
			tv_Metric.setText(getString(R.string.metric_units));
			if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
					SharedPreUtil.HEIGHT).equals("")) {
				tvHeight.setText("170 "+getString(R.string.centimeter));
			} else {
				tvHeight.setText(SharedPreUtil.readPre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.HEIGHT) + " "+getString(R.string.centimeter));
			}
			if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
					SharedPreUtil.WEIGHT).equals("")) {
				tvWeight.setText("60 "+getString(R.string.kilogram));
			} else {
				tvWeight.setText(SharedPreUtil.readPre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.WEIGHT) + " "+getString(R.string.kilogram));
			}
		}

	}

	class MyOnClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.tv_save:
					showDialog();
				break;

			case R.id.iv_my_headphoto:
				// showPhoto();
				showChooseDialog();
				break;

			case R.id.my_big_head_photo:
				popupWindow.dismiss();
				break;
			case R.id.re_nickname:
				showSetName(etName.getText().toString());
				break;

			case R.id.re_sex:
				if (!isAlertShow) {
					// showSetMySexInformation();
					showSetMyBodyInformation("sex");
				}
				break;

			case R.id.re_height:
				// Log.e(TAG, "isAlertShow ="+isAlertShow);
				if(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.METRIC).equals(SharedPreUtil.NO)){
					showSetMyheight_us();
				}else{
					showSetMyBodyInformation("height");
				}

				break;

			case R.id.re_Metric:
				// Log.e(TAG, "isAlertShow ="+isAlertShow);
				if (!isAlertShow) {
					showSetMyBodyInformation("metric");
				}

				break;

			case R.id.re_weight:
				if (!isAlertShow) {
						showSetMyBodyInformation("weight");
				}
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

	private void showSetName(String str) {
		AlertDialog.Builder setNameDialog = new AlertDialog.Builder(
				MyRegisterDataActivity.this);
		setNameDialog.setTitle(R.string.my_set_name);

		View sView= LayoutInflater.from(this).inflate(R.layout.usernameeditlayout, null);
		final EditText ed = (EditText)sView.findViewById(R.id.tv_dialog_name);
		ed.setText(str);
		ed.setFilters(emojiFilters);
		setNameDialog.setView(sView);
		setNameDialog.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				name = ed.getText().toString().trim();
				if (name.equals("")) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.nickname_null),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (SharedPreUtil.readPre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {
					Message msg = handler.obtainMessage();
					msg.what = SETNULLNAME;

					handler.sendMessage(msg);

					SharedPreUtil.savePre(MyRegisterDataActivity.this,
							SharedPreUtil.USER, SharedPreUtil.NAME, name);
					setheadnamebroadcast();
					return;
				}
				try {
					if (!name.equals("")) {
						String mid = SharedPreUtil.readPre(MyRegisterDataActivity.this,
								SharedPreUtil.USER, SharedPreUtil.MID);
						sex = SharedPreUtil.readPre(MyRegisterDataActivity.this,
								SharedPreUtil.USER, SharedPreUtil.SEX);
						if (sex.equals("")) {
							sex = "0";
						}
						/*
						 * String urlName = URLEncoder.encode(name, "UTF-8");
						 * String url = Constants.URLREVISEINFOPREFIX + "mid=" +
						 * mid + "&sex=" + sex + "&name=" + urlName; if (hc ==
						 * null) { hc = HTTPController.getInstance();
						 * hc.open(getApplicationContext()); }
						 * hc.getNetworkData(url, handler, SETNAME);
						 */
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
			}
		});
		setNameDialog.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		setNameDialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.e("EverydayDataActivity", "点击了fanhui按钮");
			finish();
			break;

		default:
			break;
		}
		return false;
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.quit_login_dialog);
		builder.setPositiveButton(R.string.ok,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// initFile();
						SharedPreUtil.savePre(getApplicationContext(),
								SharedPreUtil.USER, SharedPreUtil.MID, "");
						// finish();
						dialog.dismiss();
					}
				});
		builder.setNeutralButton(R.string.cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
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
		alert.setItems(R.array.photograph, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				/*if (arg1 == 0) {
					photo();
				} else {
					Intent i = new Intent(
							Intent.ACTION_PICK,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					*//*Intent it = new Intent(Intent.ACTION_PICK);
					it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*"); *//*
					startActivityForResult(i, RESULT_LOAD_IMAGE);
				}*/

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
		case RESULT_LOAD_IMAGE:
			Log.e("RESULT_LOAD_IMAGE", "photoUri=");
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					picName = startPhotoZoom(uri);
				}
			}
			break;
		case CUT_PHOTO_REQUEST_CODE:
			Log.e("CUT_PHOTO_REQUEST_CODE", "photoUri=");
			if (resultCode == RESULT_OK && null != data) {// 裁剪返回
				Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(imageUrl);
				System.out.println("bitmap = " + bitmap);
				FileUtils.deleteDir(FileUtils.SDPATH);
				uploadBitmap = ImageCacheUtil.createFramedPhoto(480, 480,
						bitmap, (int) (dp * 1.6f));
				// picName = startPhotoZoom(photoUri);
				FileUtils.saveBitmap(uploadBitmap, picName);
				File file = new File(FileUtils.SDPATH, picName + ".JPEG");
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
				headPhoto.setImageBitmap(ImageCacheUtil
						.toRoundBitmap(uploadBitmap));

				SharedPreUtil.savePre(getApplicationContext(),
						SharedPreUtil.USER, SharedPreUtil.FACE, picName
								+ ".JPEG");
				setheadnamebroadcast();
			}
			break;
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
			startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
			return address;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * private void showPhoto() { if (photoView == null) { photoView =
	 * LayoutInflater.from(MyDataActivity.this).inflate( R.layout.photo_dialog,
	 * null); } ImageView imgHeadPhoto = (ImageView) photoView
	 * .findViewById(R.id.my_big_head_photo);
	 * imgHeadPhoto.setOnClickListener(new MyOnClickEvent()); String photoName =
	 * SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER,
	 * SharedPreUtil.FACE); String path = FileUtils.SDPATH + photoName;
	 * Log.e("NavigationDrawerFragment ", " 显示的图片路径：" + path); File file = new
	 * File(path); if (file.exists()) { Bitmap photoBitmap =
	 * BitmapFactory.decodeFile(path); imgHeadPhoto.setImageBitmap(photoBitmap);
	 * } popupWindow = new PopupWindow(photoView, LayoutParams.MATCH_PARENT,
	 * LayoutParams.MATCH_PARENT, true); ColorDrawable dw = new
	 * ColorDrawable(0x90000000);
	 *
	 * popupWindow.setBackgroundDrawable(dw);
	 * popupWindow.showAtLocation(findViewById(R.id.iv_my_headphoto),
	 * Gravity.CENTER, 0, 0); // 让popupWindow获得焦点
	 * popupWindow.setFocusable(true); popupWindow.setOutsideTouchable(true); //
	 * 设置动画 popupWindow.setAnimationStyle(R.style.lookPhotoDialogWindowAnim);
	 * popupWindow.update(); }
	 */
	private void setHeadPhoto() {
		String mid = SharedPreUtil.readPre(getApplicationContext(),
				SharedPreUtil.USER, SharedPreUtil.MID);
		if (!mid.equals("")) {
			String photoName = SharedPreUtil.readPre(getApplicationContext(),
					SharedPreUtil.USER, SharedPreUtil.FACE);
			String photoUrl = SharedPreUtil.readPre(getApplicationContext(),
					SharedPreUtil.USER, SharedPreUtil.FACEURL);
			String path = FileUtils.SDPATH + photoName;
			Log.e("MyDataActivity ", " 显示的图片路径：" + path);
			File file = new File(path);
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(path);     // addlx
				headPhoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));
				Log.e("MyDataActivity ", " 显示本地图片");
			} else {
				if (NetWorkUtils.isConnect(getApplicationContext())
						&& !mid.equals("")) {
					if (hc == null) {
						hc = HTTPController.getInstance();
						hc.open(getApplicationContext());
					}
					hc.downloadImage(photoUrl, photoName, handler, QQImgWhat);
					Log.e("MyDataActivity ", " 下载图片");
				}
			}
		} else {

			String photoName = SharedPreUtil.readPre(getApplicationContext(),
					SharedPreUtil.USER, SharedPreUtil.FACE);
			if (photoName == "") {
				TypedArray a = obtainStyledAttributes(new int[] {
		                R.attr.im_head_women, R.attr.im_head_men});

				if(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.SEX).equals("1")){

					headPhoto.setImageDrawable(a.getDrawable(0));
				}else{
					headPhoto.setImageDrawable(a.getDrawable(1));
				}
				a.recycle();
				return;
			}
			String path = FileUtils.SDPATH + photoName;
			Log.e("MyDataActivity ", " 显示的图片路径：" + path);
			File file = new File(path);
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				headPhoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));    // addlx
				Log.e("MyDataActivity ", " 显示本地图片");
			}

		}
	}

	// 上传头像的方法
	private void uploadPhoto(File f) {
		if (hc == null) {
			hc = HTTPController.getInstance();
			hc.open(getApplicationContext());
		}
		String mid = SharedPreUtil.readPre(getApplicationContext(),
				SharedPreUtil.USER, SharedPreUtil.MID);
		String url = Constants.URLREVISEPHTOTPREFIX + "mid=" + mid + "&";
		System.out.println("上传头像  url = " + url);
		// hc.upload(url, f, handler);
	}

	private void initFile() {

		FileUtils.deleteDir(FileUtils.SDPATH);
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.MID, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.RUN_DAY, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.RUN_WEEK, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.RUN_MONTH, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.FEET_RUN, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.FEET_WALK, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.EMAIL, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.NAME, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.FACE, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.SEX, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.SCORE, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.MSG, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.WEIGHT, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.HEIGHT, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.OPENID, "");
		SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.TOKEN, "");
	}

	public void photo() {
		try {
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			String sdcardState = Environment.getExternalStorageState();
			String sdcardPathDir = Environment
					.getExternalStorageDirectory().getPath() + "/tempImage/";
			File file = null;
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				// 有sd卡，是否有myImage文件夹
				File fileDir = new File(sdcardPathDir);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				// 是否有headImg文件
				file = new File(sdcardPathDir + System.currentTimeMillis()
						+ ".JPEG");
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



	// private AlertDialog setBodyDialog;
	// private AlertDialog setSexDialog;
	// private AlertDialog setBirthdayDialog;
	// private ImageView imgDialogMen;
	// private ImageView imgDialogWomen;

	private void showSetMyBodyInformation(String str) {
		// AlertDialog.Builder setBodyBuilder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(
				R.layout.set_user_body_information_dialog, null);
	//	set_user_height_us_information_dialog
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

				tvTitle.setText(R.string.my_weight);
				tvCompany.setText("lbs");

				pv_all.setMaxValue(500);
				pv_all.setMinValue(50);
				int selnumber;
				selnumber=Utils.toint(SharedPreUtil.readPre(
							MyRegisterDataActivity.this, SharedPreUtil.USER,
							SharedPreUtil.WEIGHT_US));
				if(selnumber==0){
					selnumber=120;
				}
				pv_all.setValue(selnumber);


			}else{
				showpopsel=WEIGHT;
				tvTitle.setText(R.string.my_weight);
				tvCompany.setText("kg");

				pv_all.setMaxValue(150);
				pv_all.setMinValue(20);
				int selnumber;
				selnumber=Utils.toint(SharedPreUtil.readPre(
							MyRegisterDataActivity.this, SharedPreUtil.USER,
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
						MyRegisterDataActivity.this, SharedPreUtil.USER,
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
						MyRegisterDataActivity.this, SharedPreUtil.USER,
						SharedPreUtil.SEX));

			pv_all.setValue(selnumber);
		}else if (str.equals("metric")) {
			showpopsel=METRIC;
			tvTitle.setText(R.string.metric);
			tvCompany.setText("");
			pv_all.setDisplayedValues(mMetricArr);
			pv_all.setMaxValue(mMetricArr.length - 1);
			int selnumber;
			if (SharedPreUtil.readPre(MyRegisterDataActivity.this, SharedPreUtil.USER,
					SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) {
				selnumber = 1;
			}else{
				selnumber = 0;
			}

			pv_all.setValue(selnumber);
		}
		/*
		 * //setBodyBuilder.setView(view); setBodyBuilder.setCancelable(false);
		 * setBodyDialog = setBodyBuilder.show(); Point size = new Point();
		 * getWindowManager().getDefaultDisplay().getSize(size); int width =
		 * size.x;
		 *
		 * WindowManager.LayoutParams params =
		 * setBodyDialog.getWindow().getAttributes(); params.width = width;
		 * params.height = LayoutParams.WRAP_CONTENT; params.gravity =
		 * Gravity.BOTTOM;
		 *
		 * Window window = setBodyDialog.getWindow();
		 * window.setAttributes(params); window.setContentView(view);
		 * setBodyDialog.getWindow().setContentView(view);
		 */
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
	//////////////////////////////////////////////////////
	private void showSetMyheight_us() {

		 showpopsel=HEIGHT_US;
		// AlertDialog.Builder setBodyBuilder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(
				R.layout.set_user_height_us_information_dialog, null);

		TextView tvTitle = (TextView) view
				.findViewById(R.id.tv_my_data_dialog_title);
		Button btOk = (Button) view.findViewById(R.id.bt_my_data_set_ok);
		Button btCancel = (Button) view
				.findViewById(R.id.bt_my_data_set_cancel);

		btOk.setOnClickListener(this);
		btCancel.setOnClickListener(new DialogSetBodyInformationOnClick());
		pv_ft = (NumberPicker) view
				.findViewById(R.id.picker_my_data_body);

		 pv_in = (NumberPicker) view
				.findViewById(R.id.picker_my_data_in);

				tvTitle.setText(R.string.my_stature);
				pv_ft.setMinValue(4);
				pv_ft.setMaxValue(8);
				pv_in.setMinValue(0);
				pv_in.setMaxValue(11);

				int height_in,height_ft;
				height_ft=Utils.toint(SharedPreUtil.readPre(
							MyRegisterDataActivity.this, SharedPreUtil.USER,
							SharedPreUtil.HEIGHT_FT));
				if(height_ft==0){
					pv_ft.setValue(6);
				}else{
					pv_ft.setValue(height_ft);
				}
				height_in=Utils.toint(SharedPreUtil.readPre(
						MyRegisterDataActivity.this, SharedPreUtil.USER,
						SharedPreUtil.HEIGHT_IN));

				pv_in.setValue(height_in);

				 TypedArray a = obtainStyledAttributes(new int[] {
			                R.attr.global_text_color});

		setNumberPickerTextColor(pv_in, a.getColor(0,Color.parseColor("#4c5157")));
		setNumberPickerTextColor(pv_ft, a.getColor(0,Color.parseColor("#4c5157")));
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
	/*
	 * private void showSetMySexInformation() { setSexStr = "";
	 * AlertDialog.Builder setSexBuilder = new AlertDialog.Builder(this); View
	 * view = LayoutInflater.from(this).inflate(
	 * R.layout.set_user_sex_information_dialog, null); TextView tvTitle =
	 * (TextView) view .findViewById(R.id.tv_my_data_set_sex_dialog_title);
	 * //imgDialogMen = (ImageView)
	 * view.findViewById(R.id.img_my_data_set_sex_men); // imgDialogWomen =
	 * (ImageView) view.findViewById(R.id.img_my_data_set_sex_women); Button
	 * btOk = (Button) view.findViewById(R.id.bt_my_data_set_sex_ok); Button
	 * btCancel = (Button) view .findViewById(R.id.bt_my_data_set_sex_cancel);
	 * tvTitle.setText(R.string.my_sex); initSexImag(sex);
	 * btOk.setOnClickListener(new DialogSetBodyInformationOnClick());
	 * btCancel.setOnClickListener(new DialogSetBodyInformationOnClick());
	 * //imgDialogMen.setOnClickListener(new DialogSetBodyInformationOnClick());
	 * //imgDialogWomen.setOnClickListener(new
	 * DialogSetBodyInformationOnClick()); setSexBuilder.setView(view);
	 * setSexBuilder.setCancelable(false); //setSexDialog =
	 * setSexBuilder.show(); isAlertShow = true; }
	 */

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
			String[] birthStrings = birhStr.split("\\.");

			Log.e("birhStr", birhStr + birthStrings.length);
			int year_int = Integer.parseInt(birthStrings[0]);
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
				// TODO Auto-generated method stub

				dpBirthday.clearFocus();
				// timePicker.clearFocus();

			String	setBirthdayStr = dpBirthday.getYear() + "."
						+ (dpBirthday.getMonth() + 1) + "."
						+ dpBirthday.getDayOfMonth() + "";
				Log.e("选择生日", "setBirthdayStr = " + setBirthdayStr);
				tvBirth.setText(setBirthdayStr);
				SharedPreUtil
						.savePre(getApplicationContext(), SharedPreUtil.USER,
								SharedPreUtil.BIRTH, setBirthdayStr);
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

	class DialogSetBodyInformationOnClick implements
			View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String sex = SharedPreUtil.readPre(MyRegisterDataActivity.this,
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
				String mid = SharedPreUtil.readPre(MyRegisterDataActivity.this,
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

	private void updateInformation() {
		tvHeight.setText(SharedPreUtil.readPre(MyRegisterDataActivity.this,
				SharedPreUtil.USER, SharedPreUtil.HEIGHT) + " cm");
		tvWeight.setText(SharedPreUtil.readPre(MyRegisterDataActivity.this,
				SharedPreUtil.USER, SharedPreUtil.WEIGHT) + " kg");
		tvBirth.setText(SharedPreUtil.readPre(MyRegisterDataActivity.this,
				SharedPreUtil.USER, SharedPreUtil.BIRTH));
		if ("0".equals(sex)) {
			sexIcon.setText(getString(R.string.my_woman));
		} else if ("1".equals(sex)) {
			sexIcon.setText(getString(R.string.my_man));
		}
		;

	}


	public void setbroadcast() {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MainService.ACTION_USERDATACHANGE);
		sendBroadcast(broadcastIntent);
	}
	public void setheadnamebroadcast() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MainService.ACTION_MYINFO_CHANGE);
		sendBroadcast(broadcastIntent);
	}
	
	private static InputFilter emojiFilter = new InputFilter() {

		Pattern emoji = Pattern
				.compile(
						"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
						Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			int sourceLen = source.toString().length();
			int destLen = dest.toString().length();
			if (destLen  > 11) {
				return "";
			}
			if (sourceLen + destLen > 12) {
				return source.subSequence(0, 12-destLen);
			}

			Matcher emojiMatcher = emoji.matcher(source);

			if (emojiMatcher.find()) {

				return "";

			}
			return null;

		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case  R.id.bt_my_data_set_ok:
			/*private static final int HEIGHT_US = 0;
			private static final int HEIGHT= 1;
			private static final int WEIGHT = 2;
			private static final int WEIGHT_US = 3;
			private static final int SEX = 4;
			private static final int METRIC = 5;*/
			switch (showpopsel) {
			case HEIGHT_US:
				SharedPreUtil.savePre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN,
						pv_in.getValue()+"");
				SharedPreUtil.savePre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT,
						pv_ft.getValue()+"");
				tvHeight.setText(pv_ft.getValue() + " " + getString(R.string.imperial_foot) + pv_in.getValue() + " " + getString(R.string.imperial_inch));
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
						SharedPreUtil.CHANGE_TIME, System.currentTimeMillis()+"");
				break;
			case HEIGHT:

				SharedPreUtil.savePre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.HEIGHT,
						pv_all.getValue()+"");
				tvHeight.setText(pv_all.getValue() + " cm");
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
						SharedPreUtil.CHANGE_TIME, System.currentTimeMillis() + "");
				break;
				
			case WEIGHT:
				
				SharedPreUtil.savePre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.WEIGHT,
						pv_all.getValue()+"");
				tvWeight.setText(pv_all.getValue() + " kg");
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
						SharedPreUtil.CHANGE_TIME, System.currentTimeMillis() + "");
				break;
				
				
			case WEIGHT_US:
				
				SharedPreUtil.savePre(MyRegisterDataActivity.this,
						SharedPreUtil.USER, SharedPreUtil.WEIGHT_US,
						pv_all.getValue()+"");
				tvWeight.setText(pv_all.getValue() + " lbs");
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
						SharedPreUtil.CHANGE_TIME, System.currentTimeMillis() + "");
				break;
				
				
			case SEX:

				if(Utils.toint(SharedPreUtil.readPre(MyRegisterDataActivity.this,
							SharedPreUtil.USER, SharedPreUtil.SEX))!=pv_all.getValue()){
					SharedPreUtil.savePre(MyRegisterDataActivity.this,
							SharedPreUtil.USER, SharedPreUtil.SEX, pv_all.getValue()+"");
				
				setHeadPhoto();
				setheadnamebroadcast();
				sexIcon.setText(mSexArr[pv_all.getValue()]);
				}

				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
						SharedPreUtil.CHANGE_TIME, System.currentTimeMillis()+"");
			
				break;
				
			case METRIC:

				if (mMetricArr[pv_all.getValue()].equals(getString(R.string.metric_units))) {
					SharedPreUtil.savePre(MyRegisterDataActivity.this,
							SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES);
				} else {
					SharedPreUtil.savePre(MyRegisterDataActivity.this,
							SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.NO);
				}
				SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
						SharedPreUtil.CHANGE_TIME, System.currentTimeMillis()+"");
				initheightAndWeight();
			
				

				break;
			default:
				break;
			}
			
			break;

		default:
			break;
		}
		
		setbroadcast();
		mPopupWindow.dismiss();
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

	@Override
	public void takeSuccess(TResult result) {
//        Logg.e(TAG, "takeSuccess: " + result + "  :" + result.getImages().size());
		Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(result.getImage().getOriginalPath());
		headPhoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));

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

}
