package com.szkct.weloopbtsmartdevice.main;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.Utils;

public class UserHelpActivity extends Activity {

	private ViewPager userhelpPager;
	private ArrayList<View> mViews;
	private ViewPagerAdapter userhelpViewAdapter;
	private Button finishbtn;
	private int currentIndex;
	boolean isFirstReadHelp = false;
	private ImageView[] guideDots;
	private final int images[] = { R.drawable.userhelp_page_1,
			R.drawable.userhelp_page_2,R.drawable.userhelp_page_3
			};
	private View view;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_help);

		initView();

		initDot();

		userhelpPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				setCurrentDot(arg0);
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

		finishbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private void initView() {
		// TODO Auto-generated method stub
		userhelpPager = (ViewPager) findViewById(R.id.userhelp_view_pager);
		mViews = new ArrayList<View>();

		for (int i = 0; i < images.length; i++) {
			ImageView imv = new ImageView(UserHelpActivity.this);
		//	imv.setBackgroundResource(images[i]);
			imv.setScaleType(ScaleType.FIT_XY);
			imv.setImageBitmap(readBitMap(UserHelpActivity.this,images[i]));
			mViews.add(imv);
		}
		if(Utils.getLanguage().contains("zh") && Utils.getCountry().contains("CN")){
		//	ImageView imv = new ImageView(UserHelpActivity.this);
			//	imv.setBackgroundResource(images[i]);
		//	imv.setScaleType(ScaleType.FIT_XY);
		//	imv.setImageBitmap(readBitMap(UserHelpActivity.this,R.drawable.userhelp_page_4));
		//	mViews.add(imv);

			view = LayoutInflater.from(UserHelpActivity.this).inflate(
					R.layout.userhelp_content_view, null);
			ImageView userhrlp_conten_image=(ImageView)view.findViewById(R.id.userhrlp_conten_image);
			userhrlp_conten_image.setImageBitmap(readBitMap(UserHelpActivity.this,R.drawable.userhelp_page_4));
			mViews.add(view);
		}else {
			view = LayoutInflater.from(UserHelpActivity.this).inflate(
					R.layout.userhelp_content_view, null);
			ImageView userhrlp_conten_image = (ImageView) view.findViewById(R.id.userhrlp_conten_image);
			userhrlp_conten_image.setImageBitmap(readBitMap(UserHelpActivity.this, R.drawable.userhelp_page_4));
			mViews.add(view);
		}


		finishbtn = (Button) view.findViewById(R.id.finish_userhelp_btn);

		userhelpViewAdapter = new ViewPagerAdapter(mViews);

		userhelpPager.setAdapter(userhelpViewAdapter);
	}

	private void initDot() {
		// 找到放置小点的布局
		LinearLayout layout = (LinearLayout) findViewById(R.id.userhelp_dots);

	/*	if(Utils.getLanguage().contains("zh") && Utils.getCountry().contains("CN")) {
			findViewById(R.id.guide_round_ch).setVisibility(View.VISIBLE);
		}*/

		// 初始化小点数组
		guideDots = new ImageView[mViews.size()];

		// 循环取得小点图片，让每个小点都处于正常状态
		for (int i = 0; i < mViews.size(); i++) {
			guideDots[i] = (ImageView) layout.getChildAt(i);
			guideDots[i].setSelected(false);
		}

		// 初始化第一个小点为选中状态
		currentIndex = 0;
		guideDots[currentIndex].setSelected(true);
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > mViews.size() - 1
				|| currentIndex == position) {
			return;
		}

		guideDots[position].setSelected(true);
		guideDots[currentIndex].setSelected(false);

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

		// 初始化position位置的页面
		@Override
		public Object instantiateItem(View view, int position) {
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
