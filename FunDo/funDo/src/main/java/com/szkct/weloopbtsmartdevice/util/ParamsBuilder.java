package com.szkct.weloopbtsmartdevice.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ParamsBuilder extends ArrayList<NameValuePair>{
	
	private ParamsBuilder() {
		
	}
	
	private ParamsBuilder(Object obj) {
		this.addAll(convert2Param(obj));
	}
	
	
	public static ParamsBuilder create(){
		return new ParamsBuilder();
	}
	
	public static ParamsBuilder create(Object obj){
		return new ParamsBuilder(obj);
	}
	
	
	public ParamsBuilder addParam(String key,Object value){
		this.add(new BasicNameValuePair(key, String.valueOf(value)));
		return this;
	}
	
	private List<NameValuePair> convert2Param(Object obj){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		String[] names = getField(obj);
		for (String name : names) {
			String value = getter(obj, name);
			list.add(new BasicNameValuePair(name,value));
		}
		return list;
	}
	
	private String[] getField(Object obj){
		Field[] fields = obj.getClass().getDeclaredFields();
		String[] names = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			names[i] = field.getName();
		}
		return names;
	}
	
    /**
     * @param obj
     *            操作的对象
     * @param att
     *            操作的属性
     * */
    private String getter(Object obj, String att) {
        try {
            Method method = obj.getClass().getMethod("get" + String.valueOf(att.charAt(0)).toUpperCase()+att.substring(1));
            Object value = method.invoke(obj);
            if(value!=null){
            	return value.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
