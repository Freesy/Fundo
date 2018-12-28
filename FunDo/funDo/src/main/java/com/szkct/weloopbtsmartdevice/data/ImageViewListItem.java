package com.szkct.weloopbtsmartdevice.data;

import android.widget.ImageView;
/**
 * 
 * @author chendalin
 *  定义侧滑菜单项的图标及文字
 */
public class ImageViewListItem {

	private ImageView icon;
	private String title;
	
	public ImageViewListItem() {
		super();
	}
	
	public ImageViewListItem(ImageView icon, String title) {
		super();
		this.icon = icon;
		this.title = title;
	}

	public ImageView getIcon() {
		return icon;
	}

	public void setIcon(ImageView icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
