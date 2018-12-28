package com.szkct.weloopbtsmartdevice.trajectory;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;

/*
 * 自定义对话框
 */
public class MyAlertDialog
{
	private Context mContext;
	private AlertDialog mDialog;
	// 对话框的标题
	private TextView mTitleView;
	// 对话框的内容
	private TextView mMessageView;
	// 对话框的标题和内容
	public LinearLayout mDialogContextLayout;
	// 对话框的按键
	// private LinearLayout mDialogBtnLayout;
	// 对话框的按键
	private TextView mNoButton;
	private TextView mYesButton;
	private TextView mCenterButton;
	// 对话框的按键竖线
	// 对话框的按键竖线
	private View mDiaoView1;
	private View mDiaoView2;
	private View mDiaoView3;
	private View mDiaoView4;

	public MyAlertDialog(Context context)
	{

		if (null != context)
		{
			mContext = context;
			mDialog = new AlertDialog.Builder(mContext).create();
			// 2015-02-28yh 添加
			mDialog.setCancelable(false);// 不可以取消
			mDialog.show();
			// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
			Window window = mDialog.getWindow();
			window.setContentView(R.layout.builder_layout);
			mTitleView = (TextView) window.findViewById(R.id.dialog_title);
			mMessageView = (TextView) window.findViewById(R.id.dialog_message);

			mDialogContextLayout = (LinearLayout) window
					.findViewById(R.id.dialogcontext_layout);
			// mDialogContextLayout.getBackground().setAlpha(215);

			// mDialogBtnLayout = (LinearLayout) window
			// .findViewById(R.id.dialog_buttonLayout);
			// mDialogBtnLayout.getBackground().setAlpha(215);

			mCenterButton = (TextView) window.findViewById(R.id.dialog_center);
			// mCenterButton.getBackground().setAlpha(0);
			mNoButton = (TextView) window.findViewById(R.id.dialog_no);
			// mNoButton.getBackground().setAlpha(0);
			mDiaoView1 = (View) window.findViewById(R.id.dialog_view1);
			mDiaoView1.getBackground().setAlpha(215);
			mDiaoView1.setVisibility(View.GONE);
			mDiaoView2 = (View) window.findViewById(R.id.dialog_view2);
			mDiaoView2.getBackground().setAlpha(215);
			mDiaoView3 = (View) window.findViewById(R.id.dialog_view3);
			mDiaoView3.getBackground().setAlpha(215);
			mDiaoView4 = (View) window.findViewById(R.id.dialog_view4);
			mDiaoView4.getBackground().setAlpha(215);
			mYesButton = (TextView) window.findViewById(R.id.dialog_yes);
			// mYesButton.getBackground().setAlpha(0);
		}
	}

	public void setTitle(int resId, float size)
	{
		mTitleView.setText(resId);
		size = size <= 0 ? 14f : size;
		mTitleView.setTextSize(size);
		mMessageView.setTextSize(size);
		mTitleView.setVisibility(View.VISIBLE);
	}

	public void setTitle(String title, float size)
	{
		mTitleView.setText(title);
		size = size <= 0 ? 14f : size;
		mTitleView.setTextSize(size);
		mMessageView.setTextSize(size);
		mTitleView.setVisibility(View.VISIBLE);
	}

	public void setMessage(int resId)
	{
		mMessageView.setText(resId);

	}

	public void setMessage(String message)
	{
		mMessageView.setText(message);
	}

	/**
	 * 设置按钮 确定
	 * 
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(int resId, View.OnClickListener listener)
	{
		mYesButton.setText(resId);
		mYesButton.setVisibility(View.VISIBLE);
		mDiaoView3.setVisibility(View.VISIBLE);
		mYesButton.setOnClickListener(listener);
	}

	/**
	 * 设置按钮 取消
	 * 
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(int resId, View.OnClickListener listener)
	{
		mNoButton.setText(resId);
		mNoButton.setVisibility(View.VISIBLE);
		mNoButton.setOnClickListener(listener);
	}

	/**
	 * 设置按钮 单独的按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setCenterButton(int resId, View.OnClickListener listener,
			int what)
	{
		if (1 == what)
		{
			mDiaoView4.setVisibility(View.VISIBLE);
		}
		mCenterButton.setText(resId);
		mCenterButton.setVisibility(View.VISIBLE);
		mCenterButton.setOnClickListener(listener);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss()
	{
		mDialog.dismiss();
	}
}
