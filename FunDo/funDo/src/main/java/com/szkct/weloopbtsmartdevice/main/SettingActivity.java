package com.szkct.weloopbtsmartdevice.main;




import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.DailogUtils;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.view.StrericWheelAdapter;
import com.szkct.weloopbtsmartdevice.view.WheelView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class SettingActivity extends AppCompatActivity implements OnClickListener{
	private static final String TAG = "SettingActivity";
	
	private WheelView wheelView;
	private PopupWindow mPopupWindow;
	LinearLayout switchSkinll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
	
		// TODO
		super.onCreate(savedInstanceState);
		if(SharedPreUtil.readPre(SettingActivity.this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			setTheme(R.style.KCTStyleWhite);
		}else{
			setTheme(R.style.KCTStyleBlack);
		}
		setContentView(R.layout.fragment_settinghelp);
		initContorl();

	}
	/**
	 * 初始化控件
	 */
	private void initContorl() {
		// TODO Auto-generated method stub
		
		 findViewById(R.id.user_settings_tv).setOnClickListener(this);
		 findViewById(R.id.contacts_tv).setOnClickListener(this);
		 findViewById(R.id.soscall_tv).setOnClickListener(this);
		
		 findViewById(R.id.function_userhelp_tv).setOnClickListener(this);
		 findViewById(R.id.function_about_tv).setOnClickListener(this);;
		 findViewById(R.id.back).setOnClickListener(this);
		 switchSkinll=(LinearLayout)findViewById(R.id.switchSkinll);
		 switchSkinll.setOnClickListener(this);
	}

	
	

	/**
	 * mtk add
	 */
	private long mLastClickTime = 0L;

	private boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long slotT = 0;
		slotT = time - mLastClickTime;
		mLastClickTime = time;
		if (0 < slotT && slotT < 800) {
			return true;
		}
		return false;
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.switchSkinll:
		{
			final String[] p = new String[]{ getString(R.string.theme_white), getString(R.string.theme_black)};
			View view = LayoutInflater.from(this).inflate(R.layout.pop_menu, null);
			wheelView = (WheelView) view.findViewById(R.id.targetWheel);
			wheelView.setAdapter(new StrericWheelAdapter(p));
			
			if(SharedPreUtil.readPre(SettingActivity.this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
				wheelView.setCurrentItem(0);
			}else {
				wheelView.setCurrentItem(1);
			}
			
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
						if(SharedPreUtil.readPre(SettingActivity.this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals(wheelView.getCurrentItem()+"")){
							mPopupWindow.dismiss();
						}else{
							SharedPreUtil.savePre(SettingActivity.this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE, wheelView.getCurrentItem()+"");
							sendSwitchStyleBrocast(wheelView.getCurrentItem());
							mPopupWindow.dismiss();
							finish();
						}
						
						
						
					}
				}
			});

			mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
			mPopupWindow.showAtLocation(switchSkinll, Gravity.BOTTOM, 0, 0);
		
			
		}
			break;
		case R.id.user_settings_tv:
		{
			String mid = SharedPreUtil.readPre(getApplication(),
					SharedPreUtil.USER, SharedPreUtil.MID);
		//	if (mid != "") {
				Intent mIntent = new Intent(getApplication(),
						MyDataActivity.class);
			//	mIntent.putExtra("userSeting", "");
				startActivity(mIntent);
			/*} else {
				Toast.makeText(getActivity(),
						R.string.my_data_not_logged, Toast.LENGTH_SHORT)
						.show();
				Intent mIntent = new Intent(getActivity(),
						LoginActivity.class);
				startActivity(mIntent);
			}*/
		}
			break;
		case R.id.contacts_tv:
			if (isFastDoubleClick()) {
				return;
			}
			/*new Thread() {
				@Override
				public void run() {
					super.run();
					
					//Bitmap bitma=	getImageFromAssetsFile(bgimg0);	
	            	//String ssssString=bitmaptoString(bitma);
	            	String ssssString=	imgToBase64(bgimg0);
	            	MainService.getInstance().sendMessage(ssssString);
				}
			}.start();*/
			
			   DailogUtils.SynchronizeContacts(this);
			/*if (!WearableManager.getInstance().isAvailable()) {
				Toast.makeText(getActivity(),
						R.string.no_connect, Toast.LENGTH_LONG).show();
				return;
			}
			if (SOSController.getInstance().getKeyCount() < 1) {
				Toast.makeText(getActivity(),
						R.string.cant_enter_sos, Toast.LENGTH_LONG).show();
				return;
			} else if (SOSController.getInstance().getKeyCount() == 1) {
				startActivity(new Intent(getActivity(),
						OneKeySOSActivity.class));
			} else {
				startActivity(new Intent(getActivity(),
						MultiKeySOSActivity.class));
			}*/
		//	startActivity(new Intent(getActivity(),
			//		MultiKeySOSActivity.class));
			break;
		case R.id.soscall_tv:
		{
			Intent intentAlert = new Intent(getApplication(),
					SoscallActivity.class);
			startActivity(intentAlert);
		}
			break;
		case R.id.back:
		{
			onBackPressed();
		}
			break;
	
		case R.id.function_userhelp_tv:
		{
			Intent mIntent = new Intent(getApplication(),
					UserHelpActivity.class);
			startActivity(mIntent);
			
		}
		break;	
		case R.id.function_about_tv:
		{
			Intent mIntent = new Intent(getApplication(),
					AboutActivity.class);
			startActivity(mIntent);
			
		}
		break;	
		default:
			break;
		}
	}

	String bgimg0 = "clock/clock1.png";
	  
	/** 
     *  
     * @param imgPath 
     * @return
     */  
    public  String imgToBase64(String imgPath ) {  
    	Bitmap bitmap=null;
        if (imgPath !=null && imgPath.length() > 0) {  
            bitmap = getImageFromAssetsFile(imgPath);  
        }  
        if(bitmap == null){  
            //bitmap not found!!  
        }  
        ByteArrayOutputStream out = null;  
        try {  
            out = new ByteArrayOutputStream();  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
  
            out.flush();  
            out.close();  
  
            byte[] imgBytes = out.toByteArray();  
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            return null;  
        } finally {  
            try {  
                out.flush();  
                out.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    } 
	  private Bitmap getImageFromAssetsFile(String fileName)  
	  {  
	      Bitmap image = null;  
	      AssetManager am = getResources().getAssets();  
	      try  
	      {  
	          InputStream is = am.open(fileName);  
	          image = BitmapFactory.decodeStream(is);  
	          is.close();  
	      }  
	      catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }  
	  
	      return image;  
	  
	  }  
	  public String bitmaptoString(Bitmap bitmap){

		//将Bitmap转换成字符串
		    String string=null;
		    ByteArrayOutputStream bStream=new ByteArrayOutputStream();
		    bitmap.compress(CompressFormat.PNG,100,bStream);
		    byte[]bytes=bStream.toByteArray();
		    string=Base64.encodeToString(bytes,Base64.DEFAULT);
		    return string;
		    }
	  
	  
	  private void sendSwitchStyleBrocast(int index){
			Intent intent = new Intent();
			intent.setAction(MainService.ACTION_THEME_CHANGE);
		
			sendBroadcast(intent);
		}
}
