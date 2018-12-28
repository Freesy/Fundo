package com.szkct.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.UTIL;

import java.util.List;
import java.util.Map;

public class NewTimelineAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> list;
	private LayoutInflater inflater;

	public NewTimelineAdapter(Context context, List<Map<String, Object>> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.item_report_timeline, null);  // 时间轴 UI
			viewHolder = new ViewHolder();
			viewHolder.mLineLayout = (LinearLayout) convertView.findViewById(R.id.timeline_line);						// 开始时间下面的竖线
			viewHolder.mStepsLayout = (LinearLayout) convertView.findViewById(R.id.timeline_steps_ll);					//具体的数据内容  (50步)
			viewHolder.mActionImageLayout = (LinearLayout) convertView.findViewById(R.id.timeline_actionimage_ll);   // 时间右边的横线,状态模式的图片,状态模式图片下的竖线
			viewHolder.mStartTimeTv = (TextView) convertView.findViewById(R.id.timeline_starttime);  					// 开始时间
			viewHolder.mEndTimeTv = (TextView) convertView.findViewById(R.id.timeline_endtime);							// 结束时间
			viewHolder.mActionTv = (TextView) convertView.findViewById(R.id.timeline_action_tv);						// 动作内容描述
			viewHolder.mActionTimeTv = (TextView) convertView.findViewById(R.id.timeline_action_time_tv);				// 动作时间描述
			viewHolder.mStepsTv = (TextView) convertView.findViewById(R.id.timeline_steps_tv);							//步数的数值------- 单位：步数或时间
			viewHolder.mUnknownTv = (TextView) convertView.findViewById(R.id.timeline_unknown_status_tv);				// 数据未知或缺失   （默认隐藏）
			viewHolder.mSleepQualityIv = (ImageView) convertView.findViewById(R.id.timeline_sleepquality);			// 睡眠质量的图片 （默认隐藏）
			viewHolder.mActionImgIv = (ImageView) convertView.findViewById(R.id.timeline_action_iv);				// 状态模式的图片
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (list.get(position).size() == 4) {  //一个Map集合 元素的个数固定为4
			if (position != 0) {
				viewHolder.mStartTimeTv.setVisibility(View.GONE);
				viewHolder.mLineLayout.setVisibility(View.GONE);
			} else {			// 只有第一个条目才有开始时间
				viewHolder.mStartTimeTv.setVisibility(View.VISIBLE);
				viewHolder.mLineLayout.setVisibility(View.VISIBLE);
			}
			if (list.get(position).get("action").equals("sport")) {  //运动
				viewHolder.mActionTv.setText(R.string.timeline_walking);
				viewHolder.mActionImgIv.setImageResource(R.drawable.timeline_action_walk);
			} else if (list.get(position).get("action").equals("sleep")) {   // 睡眠
				viewHolder.mActionTv.setText(R.string.timeline_sleep);
				viewHolder.mActionImgIv.setImageResource(R.drawable.timeline_action_sleep);
			} else if (list.get(position).get("action").equals("unknown")) {  // 未知类型
				viewHolder.mActionTv.setText(R.string.status);
				viewHolder.mActionImgIv.setImageResource(R.drawable.timeline_action_unknown);
			} else if (list.get(position).get("action").equals("stationary")) {   // 静止
				viewHolder.mActionTv.setText(R.string.timeline_stationary);
				viewHolder.mActionImgIv.setImageResource(R.drawable.timeline_action_stationary);
			}
			String startTime = list.get(position).get("startTime").toString();  // 设置开始时间
			String endTime = list.get(position).get("endTime").toString();	  // 设置结束时间
			viewHolder.mStartTimeTv.setText(startTime);
			viewHolder.mEndTimeTv.setText(endTime);

			if (list.get(position).get("action").equals("unknown")) {  // 未知类型
				viewHolder.mActionTimeTv.setText(R.string.unknown);
				viewHolder.mSleepQualityIv.setVisibility(View.GONE);
				viewHolder.mStepsLayout.setVisibility(View.GONE);
				viewHolder.mUnknownTv.setVisibility(View.VISIBLE);
			} else {
				int time = (Integer.parseInt(endTime.split(":")[0]) * 60 + Integer.parseInt(endTime.split(":")[1])) - (Integer.parseInt(startTime.split(":")[0]) * 60 + Integer.parseInt(startTime.split(":")[1]));
				if (time > 30) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(UTIL.dip2px(context, 48), UTIL.dip2px(context, 128));
					params.setMargins(UTIL.dip2px(context, 10), 0, 0, 0);
					params.addRule(RelativeLayout.BELOW, R.id.timeline_starttime);
					viewHolder.mActionImageLayout.setLayoutParams(params);
				} else {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(UTIL.dip2px(context, 48), UTIL.dip2px(context, 75));
					params.setMargins(UTIL.dip2px(context, 10), 0, 0, 0);
					params.addRule(RelativeLayout.BELOW, R.id.timeline_starttime);
					viewHolder.mActionImageLayout.setLayoutParams(params);
				}

				viewHolder.mActionTimeTv.setText((time / 60) + context.getString(R.string.hour) + (time % 60) + context.getString(R.string.minute));     // 动作时间描述
				if (list.get(position).get("action").equals("sport")) {  // 运动类型
					viewHolder.mSleepQualityIv.setVisibility(View.GONE);
					viewHolder.mStepsLayout.setVisibility(View.VISIBLE);
					viewHolder.mUnknownTv.setVisibility(View.GONE);
					viewHolder.mStepsTv.setText(list.get(position).get("sportOrSleepData").toString());
				} else if (list.get(position).get("action").equals("sleep")) {  // 睡眠类型
					viewHolder.mSleepQualityIv.setVisibility(View.VISIBLE);
					viewHolder.mStepsLayout.setVisibility(View.GONE);
					viewHolder.mUnknownTv.setVisibility(View.GONE);
					float sleepQuality = Integer.parseInt(list.get(position).get("sportOrSleepData").toString()) / (float) time;
					if (sleepQuality > 0.25) {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_5);  // 睡眠质量5星
					} else if (sleepQuality < 0.25 && sleepQuality >= 0.2) {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_4);  // 睡眠质量4星
					} else if (sleepQuality < 2 && sleepQuality >= 0.15) {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_3);  // 睡眠质量3星
					} else if (sleepQuality < 0.15 && sleepQuality >= 0.1) {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_2);
					} else {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_1);
					}
				} else if (list.get(position).get("action").equals("stationary")) { // 静止
					viewHolder.mSleepQualityIv.setVisibility(View.GONE);
					viewHolder.mStepsLayout.setVisibility(View.VISIBLE);
					viewHolder.mUnknownTv.setVisibility(View.GONE);
					viewHolder.mStepsTv.setText(list.get(position).get("sportOrSleepData").toString());
				}
			}
		}
		return convertView;
	}

	static class ViewHolder {
		public LinearLayout mLineLayout;
		public LinearLayout mStepsLayout;
		public LinearLayout mActionImageLayout;
		public TextView mStartTimeTv;
		public TextView mEndTimeTv;
		public TextView mActionTv;
		public TextView mActionTimeTv;
		public TextView mStepsTv;
		public TextView mUnknownTv;
		public ImageView mSleepQualityIv;
		public ImageView mActionImgIv;
	}

}
