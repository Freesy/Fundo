package com.szkct.weloopbtsmartdevice.util;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;

public class MyFriendAdapter extends
		RecyclerView.Adapter<MyFriendAdapter.PersonViewHolder> {

	public static interface OnRecyclerViewListener {
		void onItemClick(int position);

		boolean onItemLongClick(int position);
	}

	private OnRecyclerViewListener onRecyclerViewListener;

	public void setOnRecyclerViewListener(
			OnRecyclerViewListener onRecyclerViewListener) {
		this.onRecyclerViewListener = onRecyclerViewListener;
	}

	private static final String TAG = "MyFriendAdapter";
	private ArrayList<NearByFriendInfo> personList;
	private Context context;
	private BitmapTools bitmaptools;
	private String mid = "";

	public MyFriendAdapter(Context context,
			ArrayList<NearByFriendInfo> personList) {
		this.context = context;
		this.personList = personList;
		bitmaptools = new BitmapTools(context);
	}

	// 这个方法主要生成为每个Item inflater出一个View //创建新View，被LayoutManager所调用
	public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		// Log.e(TAG, "onCreateViewHolder, i: " + i);
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.myfriend_recycleview_item, null);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(lp);
		return new PersonViewHolder(view);
	}

	// 适配数据到view中
	public void onBindViewHolder(PersonViewHolder viewHolder, int i) {
		// Log.e(TAG, "onBindViewHolder, i: " + i + ", viewHolder: " +
		// viewHolder);
		mid = SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MID);
		PersonViewHolder holder = (PersonViewHolder) viewHolder;
		holder.position = i;
		NearByFriendInfo person = personList.get(i);
		holder.nameTv.setText(person.getName());
		if (Utils.getLanguage().equals("en")
				|| Utils.getLanguage().equals("es")
				|| Utils.getLanguage().equals("ja")
				|| Utils.getLanguage().equals("fi")
				|| Utils.getLanguage().equals("nl")
				|| Utils.getLanguage().equals("pl")
				|| Utils.getLanguage().equals("pt")
				|| Utils.getLanguage().equals("ru")
				|| Utils.getLanguage().equals("sv")
				|| Utils.getLanguage().equals("it")){
			
			holder.friendrankTv.setText(person.getRank());
			
		}else{
			holder.friendrankTv.setText(context.getString(R.string.rank_chease)+person.getRank());
		}
		
		
		if (mid.equals(person.getId())) {
			//Log.e(TAG, "获取的id ="+person.getId()+"排名："+person.getRank()+"mid 222="+mid);
			holder.merankIV.setVisibility(View.VISIBLE);
		}else {
			holder.merankIV.setVisibility(View.GONE);
		}

		holder.rangeTv.setText(context.getString(R.string.friends_level)
				+ person.getLevel());

		bitmaptools.DisplayImage(person.getHeadIcon(), holder.headImage);

	}

	public int getItemCount() {
		return personList.size();
	}

	// 自定义的ViewHolder，持有每个Item的的所有界面元素
	class PersonViewHolder extends RecyclerView.ViewHolder implements
			View.OnClickListener, View.OnLongClickListener {
		public View rootView;
		public TextView nameTv;
		public TextView rangeTv;
		public ImageView headImage;
		public ImageView goNextImage;
		public TextView friendrankTv;
		public ImageView merankIV;
		public int position;

		public PersonViewHolder(View itemView) {
			super(itemView);
			nameTv = (TextView) itemView.findViewById(R.id.myfriends_name);
			rangeTv = (TextView) itemView.findViewById(R.id.myfirends_level);
			goNextImage = (ImageView) itemView.findViewById(R.id.friend_item_gonextIV);
			friendrankTv = (TextView) itemView.findViewById(R.id.friend_rank_tv);
			merankIV = (ImageView) itemView.findViewById(R.id.friend_item_meIV);
			headImage = (ImageView) itemView
					.findViewById(R.id.nearby_friends_icon);
			rootView = itemView
					.findViewById(R.id.nearby_friend_item_rootlayout);
			rootView.setOnClickListener(this);
			// rootView.setOnLongClickListener(this);
			if (context.getClass().getName().equals("com.szkct.weloopbtsmartdevice.main.PlayerRankActivity")) {
				goNextImage.setVisibility(View.GONE);
				friendrankTv.setVisibility(View.VISIBLE);
				
	
			}
				
			
		}

		// 设置的Item点击响应事件处理。
		@Override
		public void onClick(View v) {
			if (null != onRecyclerViewListener) {
				onRecyclerViewListener.onItemClick(position);
				//Log.e(TAG, " onClick " + position);
			}
		}

		@Override
		public boolean onLongClick(View v) {
			if (null != onRecyclerViewListener) {
				// Log.e(TAG, " onLongClick "+position);
				return onRecyclerViewListener.onItemLongClick(position);

			}
			return false;
		}
	}

}
