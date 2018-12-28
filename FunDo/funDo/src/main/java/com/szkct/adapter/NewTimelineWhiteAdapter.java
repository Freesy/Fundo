package com.szkct.adapter;

import java.util.List;
import java.util.Map;
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

public class NewTimelineWhiteAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> list;
	private LayoutInflater inflater;

	public NewTimelineWhiteAdapter(Context context, List<Map<String, Object>> list) {
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
			convertView = inflater.inflate(R.layout.item_report_timeline_white, null);
			viewHolder = new ViewHolder();
			viewHolder.mStartTimeLayout = (RelativeLayout) convertView.findViewById(R.id.timeline_white_starttime_rl);
			viewHolder.mStepsLayout = (LinearLayout) convertView.findViewById(R.id.timeline_white_steps_ll);
			viewHolder.mStartTimeTv = (TextView) convertView.findViewById(R.id.timeline_white_starttime);
			viewHolder.mActionTv = (TextView) convertView.findViewById(R.id.timeline_white_action_tv);
			viewHolder.mActionTimeTv = (TextView) convertView.findViewById(R.id.timeline_white_action_time_tv);
			viewHolder.mStepsTv = (TextView) convertView.findViewById(R.id.timeline_white_steps_tv);
			viewHolder.mUnknownTv = (TextView) convertView.findViewById(R.id.timeline_white_unknown_status_tv);
			viewHolder.mActionImgIv = (ImageView) convertView.findViewById(R.id.timeline_white_action_iv);
			viewHolder.mSleepQualityIv = (ImageView) convertView.findViewById(R.id.timeline_white_sleepquality);
			viewHolder.lineView = (View) convertView.findViewById(R.id.timeline_white_time_lineview);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position).size() == 4) {
			if (list.get(position).get("action").equals("sport")) {
				viewHolder.mStepsTv.setTextColor(context.getResources().getColor(R.color.timeline_white_step));
				viewHolder.mActionTv.setText(R.string.timeline_walking);
				viewHolder.mActionImgIv.setImageResource(R.drawable.timeline_action_walk_white);
				viewHolder.lineView.setBackgroundColor(context.getResources().getColor(R.color.timeline_white_step));
				viewHolder.mStartTimeTv.setBackground(context.getDrawable(R.drawable.timeline_walk_timebg));
			} else if (list.get(position).get("action").equals("sleep")) {
				viewHolder.mActionTv.setText(R.string.timeline_sleep);
				viewHolder.mActionImgIv.setImageResource(R.drawable.timeline_action_sleep_white);
				viewHolder.lineView.setBackgroundColor(context.getResources().getColor(R.color.timeline_white_sleep));
				viewHolder.mStartTimeTv.setBackground(context.getDrawable(R.drawable.timeline_sleep_timebg));
			} else if (list.get(position).get("action").equals("unknown")) {
				viewHolder.mActionTv.setText(R.string.status);
				viewHolder.mActionImgIv.setImageResource(R.drawable.timeline_action_unknown_white);
				viewHolder.lineView.setBackgroundColor(context.getResources().getColor(R.color.timeline_white_unknown));
				viewHolder.mStartTimeTv.setBackground(context.getDrawable(R.drawable.timeline_unknown_timebg));
			} else if (list.get(position).get("action").equals("stationary")) {
				viewHolder.mStepsTv.setTextColor(context.getResources().getColor(R.color.timeline_white_stationary));
				viewHolder.mActionTv.setText(R.string.timeline_stationary);
				viewHolder.mActionImgIv.setImageResource(R.drawable.timeline_action_stationary_white);
				viewHolder.lineView.setBackgroundColor(context.getResources().getColor(R.color.timeline_white_stationary));
				viewHolder.mStartTimeTv.setBackground(context.getDrawable(R.drawable.timeline_stationary_timebg));
			}
			String startTime = list.get(position).get("startTime").toString();
			String endTime = list.get(position).get("endTime").toString();
			viewHolder.mStartTimeTv.setText(startTime);
			if (list.get(position).get("action").equals("unknown")) {
				viewHolder.mActionTimeTv.setText(R.string.unknown);
				viewHolder.mSleepQualityIv.setVisibility(View.GONE);
				viewHolder.mStepsLayout.setVisibility(View.GONE);
				viewHolder.mUnknownTv.setVisibility(View.VISIBLE);
			} else {
				int time = (Integer.parseInt(endTime.split(":")[0]) * 60 + Integer.parseInt(endTime.split(":")[1])) - (Integer.parseInt(startTime.split(":")[0]) * 60 + Integer.parseInt(startTime.split(":")[1]));
				if (time > 30) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, UTIL.dip2px(context, 60));
					params.setMargins(UTIL.dip2px(context, 10), 0, 0, 0);
					viewHolder.mStartTimeLayout.setLayoutParams(params);
				} else {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, UTIL.dip2px(context, 45));
					params.setMargins(UTIL.dip2px(context, 10), 0, 0, 0);
					viewHolder.mStartTimeLayout.setLayoutParams(params);
				}
				viewHolder.mActionTimeTv.setText((time / 60) + context.getString(R.string.hour) + (time % 60) + context.getString(R.string.minute));
				if (list.get(position).get("action").equals("sport")) {
					viewHolder.mSleepQualityIv.setVisibility(View.GONE);
					viewHolder.mStepsLayout.setVisibility(View.VISIBLE);
					viewHolder.mUnknownTv.setVisibility(View.GONE);
					viewHolder.mStepsTv.setText(list.get(position).get("sportOrSleepData").toString());
				} else if (list.get(position).get("action").equals("sleep")) {
					viewHolder.mSleepQualityIv.setVisibility(View.VISIBLE);
					viewHolder.mStepsLayout.setVisibility(View.GONE);
					viewHolder.mUnknownTv.setVisibility(View.GONE);
					float sleepQuality = Integer.parseInt(list.get(position).get("sportOrSleepData").toString()) / (float) time;
					if (sleepQuality > 0.25) {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_white_5);
					} else if (sleepQuality < 0.25 && sleepQuality >= 0.2) {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_white_4);
					} else if (sleepQuality < 2 && sleepQuality >= 0.15) {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_white_3);
					} else if (sleepQuality < 0.15 && sleepQuality >= 0.1) {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_white_2);
					} else {
						viewHolder.mSleepQualityIv.setImageResource(R.drawable.timeline_sleepquality_white_1);
					}
				} else if (list.get(position).get("action").equals("stationary")) {
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
		public LinearLayout mStepsLayout;
		public RelativeLayout mStartTimeLayout;
		public TextView mStartTimeTv;
		public TextView mActionTv;
		public TextView mActionTimeTv;
		public TextView mStepsTv;
		public TextView mUnknownTv;
		public ImageView mSleepQualityIv;
		public ImageView mActionImgIv;
		public View lineView;
	}

}
