package com.szkct.bluetoothtool;

public class AppName {
    
    private  String VIEW_ITEM_NAME_CODE ="com.android.email";   
    private  String VIEW_ITEM_ACT_CODE = "com.android.email.activity.Welcome";
    private  String HOST_ID="system_app";
    private  boolean FLAG=true;
    
    private static final  AppName INSTANCE = new  AppName();
    private AppName(){
        
    }
    public static AppName getInstance() {
        return INSTANCE;
    }
    public String getHOST_ID() {
        return HOST_ID;
    }
    public void setHOST_ID(String hOST_ID) {
        HOST_ID = hOST_ID;
    }
    public boolean isFLAG() {
        return FLAG;
    }
    public void setFLAG(boolean fLAG) {
        FLAG = fLAG;
    }
    public String getVIEW_ITEM_NAME_CODE() {
        return VIEW_ITEM_NAME_CODE;
    }

    public void setVIEW_ITEM_NAME_CODE(String vIEW_ITEM_NAME_CODE) {
        VIEW_ITEM_NAME_CODE = vIEW_ITEM_NAME_CODE;
    }

    public String getVIEW_ITEM_ACT_CODE() {
        return VIEW_ITEM_ACT_CODE;
    }

    public void setVIEW_ITEM_ACT_CODE(String vIEW_ITEM_ACT_CODE) {
        VIEW_ITEM_ACT_CODE = vIEW_ITEM_ACT_CODE;
    }

    //add by edman xie
     @Override
    public String toString() {
    	return ("[id:"+ HOST_ID + ","+ VIEW_ITEM_NAME_CODE + ","+ VIEW_ITEM_ACT_CODE);
    }
}
