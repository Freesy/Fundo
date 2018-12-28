package com.szkct.weloopbtsmartdevice.view;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class LanTingBoldestBlackView extends TextView {

	public LanTingBoldestBlackView(Context context) {
		super(context);
		init(context);
	}

	public LanTingBoldestBlackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}	

	public LanTingBoldestBlackView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context){
		setTypeface(BTNotificationApplication.getInstance().lanTingBoldestBlackTypeface);
	}
}
