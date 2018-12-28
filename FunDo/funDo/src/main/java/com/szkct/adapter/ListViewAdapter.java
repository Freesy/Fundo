package com.szkct.adapter;


import java.util.List;


import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.WifiDao;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;   
import android.view.ViewGroup;   
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("ResourceType")
public class ListViewAdapter extends BaseAdapter {   
   private Context context;                        //运行上下文   
   private List< WifiDao> listItems;    //商品信息集合   
   private LayoutInflater listContainer;           //视图容器   
   private  int State=-1;                   //状态  
   private  String ssid="";  
   TypedArray a = null; //状态  
   public final class ListItemView{                //自定义控件集合     
                
           public TextView wifi_name;     
           public TextView wifi_zhuangtai;   
          
           public ImageView wifi_lv;          
    }     
      
      
   public ListViewAdapter(Context context, List<WifiDao> listItems) {   
       this.context = context;            
       listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
       this.listItems = listItems;   
       a=context.obtainStyledAttributes(new int[] {  
               R.attr.wifi_riss_iv1, R.attr.wifi_riss_iv2, R.attr.wifi_riss_iv3, R.attr.wifi_riss_iv4
               , R.attr.wifi_risslock_iv1, R.attr.wifi_risslock_iv2, R.attr.wifi_risslock_iv3, R.attr.wifi_risslock_iv4
               });
   }   
 
   public int getCount() {   
       // TODO Auto-generated method stub   
       return listItems.size();   
   }   
 
   public Object getItem(int arg0) {   
       // TODO Auto-generated method stub   
       return null;   
   }   
 
   public long getItemId(int arg0) {   
       // TODO Auto-generated method stub   
       return 0;   
   }   
      
   /**  
    * 记录勾选了哪个物品  
    * @param checkedID 选中的物品序号  
    */  
   public void setstate(int State) {   
	   this.State = State;   
   }   
      
   /**  
    * 判断物品是否选择  
    * @param checkedID 物品序号  
    * @return 返回是否选中状态  
    */  
   public void setssid(String  ssid) {   
      this.ssid=ssid;
   }   
      
 
      
         
   /**  
    * ListView Item设置  
    */  
   public View getView(int position, View convertView, ViewGroup parent) {   
       // TODO Auto-generated method stub   
    
       final int selectID = position;   
       //自定义视图   
       ListItemView  listItemView = null;   
       if (convertView == null) {   
           listItemView = new ListItemView();    
           //获取list_item布局文件的视图   
           convertView = listContainer.inflate(R.layout.activity_setwifi_team_list, null);   
           //获取控件对象   
           listItemView.wifi_name = (TextView)convertView.findViewById(R.id.wifi_name);   
           listItemView.wifi_zhuangtai = (TextView)convertView.findViewById(R.id.wifi_zhuangtai);   
           listItemView.wifi_lv = (ImageView)convertView.findViewById(R.id.wifi_image);   
           
           //设置控件集到convertView   
           convertView.setTag(listItemView);   
       }else {   
           listItemView = (ListItemView)convertView.getTag();   
       }   
       listItemView. wifi_name.setText(listItems.get(position).getSSID());
       
       if(State==0){
    	    if(listItems.get(position).getNetworkId()!=-1){
        	   listItemView. wifi_zhuangtai.setText(context.getString(R.string.Saved));  
           }else{
        	   listItemView. wifi_zhuangtai.setText(" ");  
           }  
       }
       if(State==-1){
   	    if(listItems.get(position).getNetworkId()!=-1){
       	   listItemView. wifi_zhuangtai.setText(context.getString(R.string.Saved));  
          }else{
       	   listItemView. wifi_zhuangtai.setText(" ");  
          }  
      }
       if(State==1){
    	  String ssidString= "\""+listItems.get(position).getSSID()+"\""; 
    	   if(ssidString.equals(ssid)){
        	   listItemView. wifi_zhuangtai.setText(context.getString(R.string.connected));  
           }else if(listItems.get(position).getNetworkId()!=-1){
        	   listItemView. wifi_zhuangtai.setText(context.getString(R.string.Saved));  
           }else{
        	   listItemView. wifi_zhuangtai.setText(" ");  
           }
      }
       if(State==2){
    	   String ssidString= "\""+listItems.get(position).getSSID()+"\""; 
    	   if(ssidString.equals(ssid)){
        	   listItemView. wifi_zhuangtai.setText(context.getString(R.string.connecting));  
           }else if(listItems.get(position).getNetworkId()!=-1){
        	   listItemView. wifi_zhuangtai.setText("已保存");  
           }else{
        	   listItemView. wifi_zhuangtai.setText(" ");  
           }
         }
       if(listItems.get(position).isLinking()){
    	   listItemView. wifi_zhuangtai.setText(context.getString(R.string.connected)); 
    	   MainService.warchwifistate=1;
    	   listItems.get(position).setLinking(false);
       }
		if (listItems.get(position).getLOCK()) {
			if (listItems.get(position).getLevel() > -30) {
				listItemView. wifi_lv.setImageDrawable(a.getDrawable(4));
			} else if (listItems.get(position).getLevel() > -50) {
				listItemView. wifi_lv.setImageDrawable(a.getDrawable(5));
			} else if (listItems.get(position).getLevel() > -70) {
				listItemView. wifi_lv.setImageDrawable(a.getDrawable(6));
			} else {
				listItemView. wifi_lv.setImageDrawable(a.getDrawable(7));
			}
		} else {
			
			if (listItems.get(position).getLevel() > -30) {
				listItemView. wifi_lv.setImageDrawable(a.getDrawable(0));
			} else if (listItems.get(position).getLevel() > -50) {
				listItemView. wifi_lv.setImageDrawable(a.getDrawable(1));
			} else if (listItems.get(position).getLevel() > -70) {
				listItemView. wifi_lv.setImageDrawable(a.getDrawable(2));
			} else {
				listItemView. wifi_lv.setImageDrawable(a.getDrawable(3));
			}
		}
     
	
		
       return convertView;   
   }   
}  