package com.szkct.weloopbtsmartdevice.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

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

public class HistoryWeekDataPager extends Fragment {

	private static SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat(
			"yyyy-MM-dd");
	private View view;
	private ColumnChartView chartBottom;
	private ColumnChartData columnData;
	private LinearLayout includeHistoryDate;
	private boolean hasAxes = true;
	private boolean hasAxesNames = true;
	private TextView weektimetv, changetimetv;
	private ImageView datedownimg, dateupimg;
	private String curtime, changedate, getchangeDate;
	private static String TAG = "HistoryWeekDataPager";
	private static String[] xWeeks = new String[] { "0", "0", "0", "0", "0",
			"0", "0" }; // 周数据中的x轴设置
	private float[] weekdatastr = { 0, 0, 0, 0, 0, 0, 0 }; // 周数据中的每日数据设置
	private HTTPController hc = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.historydata_weekpager, null);

		// registerBroadcast(); // 注册广播。
		init();

		return view;

	}

	private void init() {
		// TODO Auto-generated method stub
		chartBottom = (ColumnChartView) view
				.findViewById(R.id.weekchart_bottom);
		// 开始动画显示条形图；
		includeHistoryDate = (LinearLayout) view
				.findViewById(R.id.include_historyweek_date);
		weektimetv = (TextView) includeHistoryDate
				.findViewById(R.id.curdate_tv);
		datedownimg = (ImageView) includeHistoryDate
				.findViewById(R.id.data_bt_downturning);
		dateupimg = (ImageView) includeHistoryDate
				.findViewById(R.id.data_bt_upturning);
		changetimetv = (TextView) view.findViewById(R.id.changetime_tv);

		// 获取系统当前时间，第一次设定一周的时间范围；
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		curtime = getDateFormat.format(curDate);
		
		changetimetv.setText(curtime);
		generateColumnData(); // 加载统计图
		aweekdate(curtime);

		datedownimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 清空一周的数据
				for (int i = 0; i < weekdatastr.length; i++) {
					weekdatastr[i] = 0;
				}
				getchangeDate = changetimetv.getText().toString();
				changedate = DateTimePickDialogUtil.dealDateDown(getchangeDate);
				changetimetv.setText(changedate);

				aweekdate(changedate);

			}
		});
		dateupimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 清空一周的数据
				for (int i = 0; i < weekdatastr.length; i++) {
					weekdatastr[i] = 0;
				}
				getchangeDate = changetimetv.getText().toString();
				if (getchangeDate.equals(curtime)) {
					Toast.makeText(getActivity(), R.string.tomorrow_no,
							Toast.LENGTH_SHORT).show();
				} else {
					changedate = DateTimePickDialogUtil
							.dealDateUp(getchangeDate);
					changetimetv.setText(changedate);

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
			String url = "http://www.fundo.cc/export/run_count_step1.php?"
					+ "mid=" + mid + "&bin_time=" + bintime + "&end_time="
					+ endtime + "&flag=" + 1;
			hc.getNetworkData(url, handler, 0);

		}

	}

	private void generateColumnData() {
		int numSubcolumns = 1;
		int numColumns = xWeeks.length;

		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		List<Column> columns = new ArrayList<Column>();
		List<SubcolumnValue> values;

		for (int i = 0; i < numColumns; ++i) {

			values = new ArrayList<SubcolumnValue>();
			for (int j = 0; j < numSubcolumns; ++j) {

				if (weekdatastr[i] >= 4000) {
					values.add(new SubcolumnValue(weekdatastr[i],
							ChartUtils.COLORS[2])); // COLOR_GREEN
				} else if ((weekdatastr[i] < 4000) && (weekdatastr[i] >= 3000)) {
					values.add(new SubcolumnValue(weekdatastr[i],
							ChartUtils.COLORS[3])); // COLOR_ORANGE
				} else {
					values.add(new SubcolumnValue(weekdatastr[i],
							ChartUtils.COLORS[0])); // COLOR_BULE
				}

			}

			axisValues.add(new AxisValue(i).setLabel(xWeeks[i])); // x轴值的设置。

			columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
		}

		columnData = new ColumnChartData(columns);

		columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
		columnData.setAxisYLeft(new Axis().setHasLines(true)
				.setMaxLabelChars(2));

		// 横纵轴的描述。

		if (hasAxes) {
			// Axis axisX = new Axis();
			Axis axisY = new Axis().setHasLines(true);
			if (hasAxesNames) {
				// axisX.setName("Axis X");
				if (!(getActivity() ==null)) {
					axisY.setName(getActivity().getString(R.string.stepnumber));
				}
				
			}
			// columnData.setAxisXBottom(axisX);
			columnData.setAxisYLeft(axisY);
		} else {
			// columnData.setAxisXBottom(null);
			columnData.setAxisYLeft(null);
		}

		chartBottom.setColumnChartData(columnData);

		// Set value touch listener that will trigger changes for chartTop.
		chartBottom.setOnValueTouchListener(new ValueTouchListener());

		// Set selection mode to keep selected month column highlighted.
		chartBottom.setValueSelectionEnabled(true);

		chartBottom.setZoomType(ZoomType.HORIZONTAL);

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

		}

		@Override
		public void onValueDeselected() {

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
				String RunReturnStr = (String) msg.obj;
				// Log.e(TAG," 服务器下载到的数据 = "+RunReturnStr);
				if (RunReturnStr != null) {
					dealWeekDatas(RunReturnStr);
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

				weekdatastr[i] = jsonArr.getInt(i);
				if (weekdatastr[i] <0) {
					weekdatastr[i] = Math.abs(weekdatastr[i]);
				}

			}
			generateColumnData();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
