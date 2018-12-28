package com.szkct.weloopbtsmartdevice.util;

import java.io.UnsupportedEncodingException;


public class Fapiao {
	
	static int a =0;
    static int crc16 =0x8005;

	/**
	 * 获取发票加密字符串
	 * @param input
	 * @return
	 */
	public static String getEncode(String input){
		try {
			return "$01"+Base64.encode(input+crc16(input))+"$";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

    public static void main(String[] args){
    	String input="深圳市金康特智能科技有限公司</>914403003599258513</>深圳市前海深港合作区前湾一路A栋201室 0755-27781080</>招商银行深圳源兴支行 755928965010201</>";
    	try {
			String crc=crc16(input);
			System.out.println("$01"+Base64.encode(input+crc)+"$");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	//crc16算法 
	 public static String crc16(String input) throws UnsupportedEncodingException{
			byte[] inputs = input.getBytes("UTF-8");
			for (int i = 0; i < inputs.length; i++) {
				div1(inputs[i]);
			}
			byte r = 0;
			div1(r);
			div1(r); 
			System.out.println(Integer.toHexString(a));
			return Integer.toHexString(a);
	 }
	 
	 public static void  div1(byte input){
			int temp;
			int data = input;
			for (int i = 0; i < 8; i++){
				temp =a & 0x8000;
				a = a <<1;
				a = a & 0x0000ffff;
				
				int numIn =data & 0x80 ;
				numIn =numIn >> 7;
				
				a = a ^ numIn ;
				if (temp == 0x8000) {
					a =a ^ crc16;
				}
				
				data = data <<1;
				a =a & 0x0000ffff;
			}
		}

}
