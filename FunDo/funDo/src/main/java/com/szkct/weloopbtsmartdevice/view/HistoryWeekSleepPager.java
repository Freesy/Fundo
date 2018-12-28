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

public class HistoryWeekSleepPager extends Fragment {

	private static SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat(
			"yyyy-MM-dd");
	private View view;
	private LineChartView weeksleepchart;
	private LineChartData lineData;
	private LinearLayout includeHistoryDate;
	private TextView weektimetv, changetimetv;
	private ImageView datedownimg, dateupimg;
	private String curtime, changedate, getchangeDate;
	private static String TAG = "HistoryWeekSleepPager";
	private static String[] xWeeks = new String[] { "0", "0", "0", "0", "0",
			"0", "0" }; // 周睡眠数据中的x轴设置
	private float[] weeksleepdata = { 0, 0, 0, 0, 0, 0, 0 }; // 周睡眠数据中的每日数据设置
	private HTTPController hc = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.historyweek_sleeppager, null);

		init();

		return view;

	}

	private void init() {
		// TODO Auto-generated method stub
		weeksleepchart = (LineChartView) view
				.findViewById(R.id.historyweek_sleepchart);

		includeHistoryDate = (LinearLayout) view
				.findViewById(R.id.include_historyweek_sleep);
		weektimetv = (TextView) includeHistoryDate
				.findViewById(R.id.curdate_tv);
		datedownimg = (ImageView) includeHistoryDate
				.findViewById(R.id.data_bt_downturning);
		dateupimg = (ImageView) includeHistoryDate
				.findViewById(R.id.data_bt_upturning);
		changetimetv = (TextView) view.findViewById(R.id.sleepchangetime_tv);

		// 获取系统当前时间，第一次设定一周的时间范围；
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		curtime = getDateFormat.format(curDate);

		changetimetv.setText(curtime);
		// 加载统计图
		generateInitialLineData();
		aweekdate(curtime);

		datedownimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 清空一周的数据
				for (int i = 0; i < weeksleepdata.length; i++) {
					weeksleepdata[i] = 0;
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
				for (int i = 0; i < weeksleepdata.length; i++) {
					weeksleepdata[i] = 0;
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
		xWeeks[6] = curtime.substring(5, 10);
		// 设置一周每天的柱状数据
		String endtime = curtime;

		for (int i = 0; i < 6; i++) {
			curtime = DateTimePickDialogUtil.dealDateDown(curtime);

			String nomthandday_str = curtime.substring(5, 10);
			xWeeks[5 - i] = nomthandday_str;
		}

		String text = xWeeks[0] + " - " + xWeeks[6];
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
		int numValues = 7;

		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		List<PointValue> values = new ArrayList<PointValue>();
		for (int i = 0; i < numValues; ++i) {
			// values.add(new PointValue(i, 0));
			values.add(new PointValue(i, weeksleepdata[i]));
			axisValues.add(new AxisValue(i).setLabel(xWeeks[i]));
		}

		Line line = new Line(values);
		line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

		List<Line> lines = new ArrayList<Line>();
		lines.add(line);

		lineData = new LineChartData(lines);

		// 横纵轴的描述。

		/*
		 * if (hasAxes) { Axis axisX = new Axis(); Axis axisY = new
		 * Axis().setHasLines(true); if (hasAxesNames) {
		 * axisX.setName("Axis X"); axisY.setName("Axis Y"); }
		 * lineData.setAxisXBottom(axisX); lineData.setAxisYLeft(axisY); }
		 */

		lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
		lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

		weeksleepchart.setLineChartData(lineData);

		// For build-up animation you have to disable viewport recalculation.
		weeksleepchart.setViewportCalculationEnabled(false);

		// And set initial max viewport and current viewport- remember to set
		// viewports after data.
		Viewport v = new Viewport(0, 24, 6, 0);
		weeksleepchart.setMaximumViewport(v);
		weeksleepchart.setCurrentViewport(v);

		weeksleepchart.setZoomType(ZoomType.HORIZONTAL);
	}

	private void generateLineData(int color, float range) {
		// Cancel last animation if not finished.
		weeksleepchart.cancelDataAnimation();

		// Modify data targets
		Line line = lineData.getLines().get(0);// For this example there is
												// always only one line.
		line.setColor(color);
		for (PointValue value : line.getValues()) {
			// Change target only for Y value.
			value.setTarget(value.getX(), (float) Math.random() * range);
		}

		// Start new data animation with 300ms duration;
		weeksleepchart.startDataAnimation(300);
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
				// Log.e(TAG," 服务器下载到的数据 = "+RunReturnStr);
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

				weeksleepdata[i] = jsonArr.getInt(i) / 3600;
				if (weeksleepdata[i] <0) {
					weeksleepdata[i] = Math.abs(weeksleepdata[i]);
					
				}

			}
			/*
			 * for (int i = 0; i < weeksleepdata.length; i++) { Log.e(TAG,
			 * "dealdatas weeksleepdata[" + i + "] =" + weeksleepdata[i]); }
			 */

			generateInitialLineData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
