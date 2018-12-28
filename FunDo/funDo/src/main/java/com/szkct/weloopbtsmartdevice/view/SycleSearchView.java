package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class SycleSearchView extends View implements  Runnable{
	private int width=200,height=200; //画布宽高
	private int srcId;
	private Bitmap src=null;
	private Context context;
	private int left=0,top=0;
	private int step=5;
	private boolean positive;
	private Thread thread;
	private boolean canStart=true;
	private int iw,ih;//图片的宽高
	public SycleSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	public SycleSearchView(Context context, AttributeSet attrs, int defStyle) {

	super(context, attrs, defStyle);

	// TODO Auto-generated constructor stub


	this.context=context;

	}


	private void init()
	{
		left=0;
		top=height/2;
	}

	/**
	* 
	* 方法: initXY 
	* 描述: TODO
	* 参数: @param x
	* 参数: @param positive true 表示向右运动
	*/

	private void initXY(int x)
	{
	left=x;
	top=height/2-(int) Math.sqrt(height*height/4-(width/2-x)*(width/2-x));
		if(!positive)
		{
		top=height-top;
		}


	}

	public void setSize(int width,int height)

	{

		this.width=width;

		this.height=height;

	}

	public void setImgRes(int id){
		this.srcId=id;
		src=BitmapFactory.decodeResource(context.getResources(), srcId);
		iw=src.getWidth();
		ih=src.getHeight();
	}

	@Override

	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(src==null){
			return;
		}
		canvas.drawBitmap(src, left, top, null);
	}


	public void startsycle(){
		canStart=true;
		Thread localThread = new Thread(this);
	    this.thread = localThread;
	    this.thread.start();
	}

	public void stopsycle(){
		canStart=false;
		if(this.thread!=null){
			this.thread.interrupt();
		}
	}


	@Override

	public void run() {

	// TODO Auto-generated method stub

		while(canStart){
			if(positive){
				if(left+step<width){
					initXY(left+step);
				}else{
					positive=!positive;
					initXY(left-step);
				}
			}else{
				if(left-step>0){
					initXY(left-step);
			
				}else{
					positive=!positive;
					initXY(left+step);
				}
			}
			postInvalidate();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
