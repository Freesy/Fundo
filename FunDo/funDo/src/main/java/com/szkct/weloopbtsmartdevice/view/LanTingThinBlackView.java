package com.szkct.weloopbtsmartdevice.view;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class LanTingThinBlackView extends TextView {

	public LanTingThinBlackView(Context context) {
		super(context);
		init(context);
	}

	public LanTingThinBlackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}	

	public LanTingThinBlackView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context){
		setTypeface(BTNotificationApplication.getInstance().lanTingThinBlackTypeface);
	}
}
