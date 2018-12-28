package com.szkct.weloopbtsmartdevice.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.DateTimePickDialogUtil;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

public class HistoryMonthSleepPager extends Fragment {

	private static SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat(
			"yyyy-MM-dd");
	private View view;
	private LineChartView monthsleepchart;
	private LineChartData lineData;
	private LinearLayout includeHistoryDate;
	private TextView weektimetv, changetimetv;
	private ImageView datedownimg, dateupimg;
	private String curtime, changedate, getchangeDate;
	private static String TAG = "HistoryWeekDataPager";
	private static String[] xmonths = new String[] { "0", "0", "0", "0", "0",
			"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
			"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };// 月数据中的x轴设置
	private float[] monthsleepdata = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; // 月数据中的每日数据设置

	private HTTPController hc = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.historymonth_sleeppager, null);

		init();

		return view;

	}

	private void init() {
		// TODO Auto-generated method stub
		monthsleepchart = (LineChartView) view
				.findViewById(R.id.historymonth_sleepchart);

		includeHistoryDate = (LinearLayout) view
				.findViewById(R.id.include_historymonth_sleep);
		weektimetv = (TextView) includeHistoryDate
				.findViewById(R.id.curdate_tv);
		datedownimg = (ImageView) includeHistoryDate
				.findViewById(R.id.data_bt_downturning);
		dateupimg = (ImageView) includeHistoryDate
				.findViewById(R.id.data_bt_upturning);
		changetimetv = (TextView) view.findViewById(R.id.monthsleep_tv);

		// 获取系统当前时间，第一次设定一周的时间范围；
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		curtime = getDateFormat.format(curDate);
		Log.e(TAG, "  curtime = " + curtime);
		changetimetv.setText(curtime);
		// 加载统计图
		generateInitialLineData();
		aweekdate(curtime);

		datedownimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 清空一周的数据
				for (int i = 0; i < monthsleepdata.length; i++) {
					monthsleepdata[i] = 0;
				}
				getchangeDate = changetimetv.getText().toString();
				changedate = DateTimePickDialogUtil.dealDateDown(getchangeDate);
				changetimetv.setText(changedate);
				// generateColumnData();
				aweekdate(changedate);

			}
		});
		dateupimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 清空一周的数据
				for (int i = 0; i < monthsleepdata.length; i++) {
					monthsleepdata[i] = 0;
				}
				getchangeDate = changetimetv.getText().toString();
				if (getchangeDate.equals(curtime)) {
					Toast.makeText(getActivity(), R.string.tomorrow_no,
							Toast.LENGTH_SHORT).show();
				} else {
					changedate = DateTimePickDialogUtil
							.dealDateUp(getchangeDate);
					changetimetv.setText(changedate);
					// generateColumnData();
					aweekdate(changedate);

				}
			}
		});

	}

	private void aweekdate(String curtime) {
		// TODO Auto-generated method stub
		xmonths[29] = curtime.substring(5, 10);
		// 设置一周每天的柱状数据
		String endtime = curtime;

		for (int i = 0; i < 29; i++) {
			curtime = DateTimePickDialogUtil.dealDateDown(curtime);

			String nomthandday_str = curtime.substring(5, 10);
			xmonths[28 - i] = nomthandday_str;
		}

		String text = xmonths[0] + " - " + xmonths[29];
		weektimetv.setText(text);

		String bintime = curtime;
		requestaWeekData(bintime, endtime);

	}

	private void requestaWeekData(String bintime, String endtime) {
		// TODO Auto-generated method stub

		if (hc == null) {
			hc = HTTPController.getInstance();
			hc.open(getActivity());

		}
		if (NetWorkUtils.isConnect(getActivity())) {

			String mid = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MID);
			String url = "http://www.fundo.cc/export/sleep_count1.php?"
					+ "mid=" + mid + "&bin_time=" + bintime + "&end_time="
					+ endtime;
			hc.getNetworkData(url, handler, 0);

		}

	}

	/**
	 * Generates initial data for line chart. At the begining all Y values are
	 * equals 0. That will change when user will select value on column chart.
	 */
	private void generateInitialLineData() {
		int numValues = xmonths.length;

		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		List<PointValue> values = new ArrayList<PointValue>();
		for (int i = 0; i < numValues; ++i) {
			// values.add(new PointValue(i, 0));
			values.add(new PointValue(i, monthsleepdata[i]));
			axisValues.add(new AxisValue(i).setLabel(xmonths[i]));
		}

		Line line = new Line(values);
		line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

		List<Line> lines = new ArrayList<Line>();
		lines.add(line);

		lineData = new LineChartData(lines);
		lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
		lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

		monthsleepchart.setLineChartData(lineData);

		// For build-up animation you have to disable viewport recalculation.
		monthsleepchart.setViewportCalculationEnabled(false);

		// And set initial max viewport and current viewport- remember to set
		// viewports after data.
		Viewport v = new Viewport(0, 24, 29, 0); // 坐标点的个数。
		monthsleepchart.setMaximumViewport(v);
		monthsleepchart.setCurrentViewport(v);

		monthsleepchart.setZoomType(ZoomType.HORIZONTAL);
	}

	private void generateLineData(int color, float range) {
		// Cancel last animation if not finished.
		monthsleepchart.cancelDataAnimation();

		// Modify data targets
		Line line = lineData.getLines().get(0);// For this example there is
												// always only one line.
		line.setColor(color);
		for (PointValue value : line.getValues()) {
			// Change target only for Y value.
			value.setTarget(value.getX(), (float) Math.random() * range);
		}

		// Start new data animation with 300ms duration;
		monthsleepchart.startDataAnimation(300);
	}

	/**
	 * Generates initial data for line chart. At the begining all Y values are
	 * equals 0. That will change when user will select value on column chart.
	 */

	private class ValueTouchListener implements
			ColumnChartOnValueSelectListener {

		@Override
		public void onValueSelected(int columnIndex, int subcolumnIndex,
				SubcolumnValue value) {
			generateLineData(value.getColor(), 100);

		}

		@Override
		public void onValueDeselected() {

			generateLineData(ChartUtils.COLOR_GREEN, 0);
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {

			case 0:
				String SleepReturnStr = (String) msg.obj;

				if (SleepReturnStr != null) {
					dealWeekDatas(SleepReturnStr);
				}

				break;

			default:
				break;
			}
			return true;
		}

	});

	protected void dealWeekDatas(String datas) {
		// TODO Auto-generated method stub

		JSONObject jsonObj;

		try {
			jsonObj = new JSONObject(datas);
			JSONArray jsonArr = jsonObj.getJSONArray("datas");
			for (int i = 0; i < jsonArr.length(); i++) {

				monthsleepdata[i] = jsonArr.getInt(i) / 3600; // 睡眠时间。单位：小时
				if (monthsleepdata[i] < 0) {
					monthsleepdata[i] = Math.abs(monthsleepdata[i]);
				}
			}

			/*
			 * for (int i = 0; i < monthsleepdata.length; i++) { Log.e(TAG,
			 * "dealdatas monthsleepdata[" + i + "] =" + monthsleepdata[i]); }
			 */

			generateInitialLineData();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
