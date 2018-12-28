package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

/**
 * Created by Kct on 2016/12/19.
 */

public class DIN1451EF_EngNeuTypefaceTextView extends TextView {

    public DIN1451EF_EngNeuTypefaceTextView(Context context) {
        super(context);
        init(context);
    }

    public DIN1451EF_EngNeuTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DIN1451EF_EngNeuTypefaceTextView(Context context, AttributeSet attrs,
                                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setTypeface(BTNotificationApplication.getInstance().dIN1451EF_EngNeuTypeface);
    }
}
