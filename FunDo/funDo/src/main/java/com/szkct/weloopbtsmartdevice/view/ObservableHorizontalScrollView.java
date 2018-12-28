package com.szkct.weloopbtsmartdevice.view;

import com.szkct.weloopbtsmartdevice.util.HorizontalsScrollViewListener;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class ObservableHorizontalScrollView extends HorizontalScrollView {
	public static int SCROLLFINISHED = 1;
	private Handler handler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			int i = ObservableHorizontalScrollView.this.getScrollX();
			if (ObservableHorizontalScrollView.this.lastScrollX == i) {
				ObservableHorizontalScrollView.this.lastScrollX = i;
				ObservableHorizontalScrollView.this.handler.sendMessageDelayed(
						ObservableHorizontalScrollView.this.handler
								.obtainMessage(), 5L);
			}

			if (ObservableHorizontalScrollView.this.scrollViewListener != null) {
				ObservableHorizontalScrollView.this.scrollViewListener
						.onScrollChanged(
								ObservableHorizontalScrollView.this,
								i,
								0,
								ObservableHorizontalScrollView.this.lastScrollX,
								0);
			}

		}
	};
	private int lastScrollX;
	private HorizontalsScrollViewListener scrollViewListener = null;

	public ObservableHorizontalScrollView(Context paramContext) {
		super(paramContext);
	}

	public ObservableHorizontalScrollView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public ObservableHorizontalScrollView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		if (this.scrollViewListener != null) {
			this.scrollViewListener.onScrollChanged(this, paramInt1, paramInt2,
					paramInt3, paramInt4);
		}
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		if (this.scrollViewListener != null) {
			HorizontalsScrollViewListener localHorizontalsScrollViewListener = this.scrollViewListener;
			int i = getScrollX();
			this.lastScrollX = i;
			localHorizontalsScrollViewListener
					.onScrollChanged(this, i, 0, 0, 0);
		}
		switch (paramMotionEvent.getAction()) {
		}
		for (;;) {
			this.handler.sendMessageDelayed(this.handler.obtainMessage(), 5L);

			return super.onTouchEvent(paramMotionEvent);
		}
	}

	public void removeScrollViewListener() {
		this.scrollViewListener = null;
	}

	public void setScrollViewListener(
			HorizontalsScrollViewListener paramHorizontalsScrollViewListener) {
		this.scrollViewListener = paramHorizontalsScrollViewListener;
	}
}
