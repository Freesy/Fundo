package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.InputStream;
import java.util.ArrayList;

public class BTcallActivity extends Activity {

	private ViewPager userhelpPager;
	private ArrayList<View> mViews;
	private ViewPagerAdapter userhelpViewAdapter;
	private Button finishbtn,btn_ok_set;
	private int currentIndex;
	private ImageView back;
	private TextView tv_set_later,tv_vp_explain;
	boolean isFirstReadHelp = false;
//	private ImageView[] guideDots;

	private InternalHandler mHandler;

	private final int images[] = { R.drawable.btcall_page_1,
			R.drawable.btcall_page_2,R.drawable.btcall_page_3
			};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			setTheme(R.style.KCTStyleWhite);
		}else{
			setTheme(R.style.KCTStyleBlack);
		}

		setContentView(R.layout.btcall_activity);

		initView();

//		initDot();
	}

	private void initView() {
		back = (ImageView) findViewById(R.id.back);
		btn_ok_set = (Button) findViewById(R.id.btn_ok_set);
		tv_set_later = (TextView) findViewById(R.id.tv_set_later);

		tv_vp_explain = (TextView) findViewById(R.id.tv_vp_explain);
		String languageLx = Utils.getLanguage();
		if (!languageLx.equals("zh")) {  // en
			tv_vp_explain.setTextSize(10);
			btn_ok_set.setTextSize(12);
		}



		userhelpPager = (ViewPager) findViewById(R.id.userhelp_view_pager);
		mViews = new ArrayList<View>();

		for (int i = 0; i < images.length; i++) {
			ImageView imv = new ImageView(BTcallActivity.this);
		//	imv.setBackgroundResource(images[i]);
			imv.setScaleType(ScaleType.FIT_XY);
			imv.setImageBitmap(readBitMap(BTcallActivity.this,images[i]));
			mViews.add(imv);
		}
//		View view = LayoutInflater.from(BTcallActivity.this).inflate(R.layout.userhelp_content_view, null);
//		ImageView userhrlp_conten_image=(ImageView)view.findViewById(R.id.userhrlp_conten_image);
//		userhrlp_conten_image.setImageBitmap(readBitMap(BTcallActivity.this,R.drawable.userhelp_page_4));
//		mViews.add(view);
//
//		finishbtn = (Button) view.findViewById(R.id.finish_userhelp_btn);

		userhelpViewAdapter = new ViewPagerAdapter(mViews);
		userhelpPager.setAdapter(userhelpViewAdapter);
		userhelpPager.setCurrentItem(0);
		userhelpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
//				setCurrentDot(arg0);
				if (arg0 == 0) {
					tv_vp_explain.setText(getString(R.string.bluetooth_call_guide1));
				} else if (arg0 == 1) {
					tv_vp_explain.setText(getString(R.string.bluetooth_call_guide2));
				} else if (arg0 == 2) {
					tv_vp_explain.setText(getString(R.string.bluetooth_call_guide3));
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		mHandler = new InternalHandler();
//		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(new AutoSwitchPagerRunnable(), 4000);

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btn_ok_set.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivity(intent);
			}
		});

		tv_set_later.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	class AutoSwitchPagerRunnable implements Runnable {

		@Override
		public void run() {
			mHandler.obtainMessage().sendToTarget();
		}
	}

	class TopNewItemTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

					mHandler.removeCallbacksAndMessages(null);
					break;

				case MotionEvent.ACTION_UP:

					mHandler.postDelayed(new AutoSwitchPagerRunnable(), 4000);
					break;

				default:
					break;
			}
			return true;
		}
	}

	class InternalHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			int currentItem = userhelpPager.getCurrentItem() + 1;
			userhelpPager.setCurrentItem(currentItem % images.length);

//			ll_point_group.getChildAt(previousEnabledPosition).setEnabled(false);
//			ll_point_group.getChildAt(adv_pager.getCurrentItem()).setEnabled(true);
//			previousEnabledPosition = adv_pager.getCurrentItem();

			mHandler.postDelayed(new AutoSwitchPagerRunnable(), 4000);
		}
	}

	private void initDot() {
		// 找到放置小点的布局
		LinearLayout layout = (LinearLayout) findViewById(R.id.userhelp_dots);

		// 初始化小点数组
//		guideDots = new ImageView[mViews.size()];

		// 循环取得小点图片，让每个小点都处于正常状态
		for (int i = 0; i < mViews.size(); i++) {
//			guideDots[i] = (ImageView) layout.getChildAt(i);
//			guideDots[i].setSelected(false);
		}

		// 初始化第一个小点为选中状态
		currentIndex = 0;
//		guideDots[currentIndex].setSelected(true);
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > mViews.size() - 1
				|| currentIndex == position) {
			return;
		}

//		guideDots[position].setSelected(true);
//		guideDots[currentIndex].setSelected(false);

		currentIndex = position;
	}

	private class ViewPagerAdapter extends PagerAdapter {

		private final ArrayList<View> mViews;

		public ViewPagerAdapter(ArrayList<View> views) {
			mViews = views;
		}

		// 返回页面数目
		@Override
		public int getCount() {
			if (mViews != null) {
				return mViews.size();
			}
			return 0;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}


		@Override
		public Object instantiateItem(View view, int position) {

			ImageView iv = new ImageView(BTcallActivity.this);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(R.drawable.btcall_page_1);
			iv.setOnTouchListener(new TopNewItemTouchListener());
			((ViewPager) view).addView(mViews.get(position), 0);
			return mViews.get(position);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		// 销毁position位置的界面
		@Override
		public void destroyItem(View view, int position, Object arg2) {
			((ViewPager) view).removeView(mViews.get(position));
		}
	}



	@SuppressWarnings("deprecation")
	public Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);

	}
}
