package com.szkct.weloopbtsmartdevice.util;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;

import com.kct.fundo.btnotification.R;

/**
 * 折线统计图
 */
public class GraphUtils {
	private static GraphUtils graph;
	private static ArrayList<Double> arrX =null;
	private static ArrayList<Double> arrY =null;
	public static GraphUtils getInstance() {
		if (graph == null) {
			graph = new GraphUtils();
		}
		return graph;
	}

	/**
	 * 圆滑单曲线 
	 * @param context
	 * @param 
	 * @param tag
	 * @return
	 */
	public static View getLineChartView(Context context,
			ArrayList<Double> arrayListX,ArrayList<Double> arrayListY,String tag , int YLeng ,double YDensity) {

		arrX = arrayListX;
		arrY = arrayListY;
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setBackgroundColor(Color.parseColor("#0F9CA2"));
		renderer.setApplyBackgroundColor(true);
		renderer.setMarginsColor(Color.parseColor("#0F9CA2"));
		renderer.setPanEnabled(false, false); 
		renderer.setLabelsTextSize(20f);
		renderer.setMargins(new int[] {20, 55, 15,5}); 
		renderer.setYAxisMin(0);
		renderer.setXLabels(0);
		renderer.setShowGrid(true); 
	    renderer.setGridColor(Color.parseColor("#eeeeee"));
		renderer.setPointSize(5f);
		Align align = renderer.getYAxisAlign(0);
		renderer.setYLabelsAlign(align);
		renderer.setYLabelsColor(0, Color.BLACK);
		if(tag.equals("A")){
			renderer.setYLabels(21);
		}else{
			renderer.setYLabels(2);
		}
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(YLeng); //定义Y轴坐标的长度值
		renderer.setXAxisMin(0.1);
		renderer.setXAxisMax(YDensity);	//数值越大X轴的坐标越密。
		
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setAxesColor(Color.BLACK);
		renderer.setYLabelsAlign(Align.RIGHT);
		if ("A".equals(tag)){
			renderer.setXAxisMax(12.5);
			for (int i = 0; i < 13;i++) {
				renderer.addXTextLabel(i, i*2+"h");
			}
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(250, "");
			renderer.addYTextLabel(500, "500");
			renderer.addYTextLabel(750, "");
			renderer.addYTextLabel(1000, "1000");
			renderer.addYTextLabel(1250, "");
			renderer.addYTextLabel(1500, "1500");
			renderer.addYTextLabel(1750, "");
			renderer.addYTextLabel(2000, "2000");
			renderer.addYTextLabel(2250, "");
			renderer.addYTextLabel(2500, "2500");
			renderer.addYTextLabel(2750, "");
			renderer.addYTextLabel(3000, "3000");
			renderer.addYTextLabel(3250, "");
			renderer.addYTextLabel(3500, "3500");
			renderer.addYTextLabel(3750, "");
			renderer.addYTextLabel(4000, "4000");
			renderer.addYTextLabel(4250, "");
			renderer.addYTextLabel(4500, "4500");
			renderer.addYTextLabel(4750, "");
			renderer.addYTextLabel(5000, "5000");
		}else if ("B".equals(tag)){
			String deepsleep =(String) context.getResources().getText(R.string.deepsleep);
			String lightsleep =(String) context.getResources().getText(R.string.lightsleep);
			renderer.setXAxisMax(12.5);
			for (int i = 0; i < 13;i++) {
				renderer.addXTextLabel(i, i*2+"h");
			}
			renderer.setYAxisMin(0);
			renderer.setYAxisMax(3);
			renderer.addYTextLabel(0, " ");
			renderer.addYTextLabel(1, lightsleep);
			renderer.addYTextLabel(2, deepsleep);
			renderer.addYTextLabel(3, " ");
		}
		XYMultipleSeriesDataset dataset = getXYMultipleSeriesDataset(tag,context);
		XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
		xyRenderer.setColor(Color.parseColor("#00ffff"));
		xyRenderer.setLineWidth(2f);
		renderer.setZoomEnabled(false, false);
		if(tag.equals("A")){
			xyRenderer.setDisplayChartValues(true);
			xyRenderer.setChartValuesTextSize(18f);
			xyRenderer.setDisplayChartValuesDistance(30);
		}
		xyRenderer.setPointStyle(PointStyle.CIRCLE);
		
//		//将画好的坐标下方用颜色填充
//		xyRenderer.setFillBelowLine(true);
//		xyRenderer.setFillBelowLineColor(Color.parseColor("#66FFB040"));
		
		xyRenderer.setFillPoints(true);
		
		renderer.addSeriesRenderer(xyRenderer);
		
		return ChartFactory.getCubeLineChartView(context, dataset, renderer,0.33f); 

	}

	public static XYMultipleSeriesDataset getXYMultipleSeriesDataset(String tag,Context context) {

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		if ("A".equals(tag)) {
			String characteristic=(String) context.getResources().getText(R.string.stepnumber);
			XYSeries series = new XYSeries(characteristic);
			System.out.println(arrX);
			if(arrX!=null){
				for (int i = arrX.size()-1; i >= 0; i--) {
					series.add(arrX.get(i),arrY.get(i));
				}
				dataset.addSeries(series);
			}
		} else if("B".equals(tag)){
			String characteristic=(String) context.getResources().getText(R.string.sleepbtn);
			XYSeries series = new XYSeries(characteristic);
			if(arrX!=null){
				for (int i = 0; i < arrX.size(); i++) {
					series.add(arrX.get(i),arrY.get(i));
					Log.e("GraphUtils", "entey.getKey() = "+arrX.get(i));
				}
				dataset.addSeries(series);
			}
		}
		return dataset;
	}

	  

}
