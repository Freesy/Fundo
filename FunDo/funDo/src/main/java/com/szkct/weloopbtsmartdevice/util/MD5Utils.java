package com.szkct.weloopbtsmartdevice.util;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 加密工具，对称加密算法，
 * 
 * 
 * @Description TODO
 * @author Robin
 * @date 
 * @Copyright:
 */
public class MD5Utils {
	
	/** 解密是否成功的校验码 */
	private static String CHECK_CODE = "http://www.kct.cn/";
	public static final String MD5_KEY = "key_kcttech" ;

	/**
	 * 加密数据
	 * 
	 * 
	 * @Description TODO
	 * @author Robin
	 * @date 
	 * @Copyright:
	 */
	public static String Encrypt(String key, String plaintext) {
		
		if (TextUtils.isEmpty(plaintext)) {// 如果明文为空
			return "";
		}
		
		StringBuffer buf = new StringBuffer();

		key = CHECK_CODE + key;//  私钥加上校验位
		plaintext = CHECK_CODE + plaintext; // 明文加上效验位
		
		byte[] keys = md5Encrypt(key).getBytes(); // 32位MD5私钥
		for (int i = 0; i < plaintext.length(); i++) {
			buf.append((char) (plaintext.charAt(i) ^ keys[i % 32]));
		}

		String ciphertext = buf.toString();
		return ciphertext;

	}

	
	/**
	 * 解密密文
	 * 
	 * 
	 * @Description TODO
	 * @author Robin
	 * @date 
	 * @Copyright:
	 */
	public static String DEcrypt(String key, String ciphertext) {
		
		if (TextUtils.isEmpty(ciphertext)) {// 如果密文为空
			return "";
		}
		
		StringBuffer buf = new StringBuffer();

		key = CHECK_CODE + key;//  私钥加上校验位
		byte[] keys = md5Encrypt(key).getBytes(); // 32位MD5私钥
		for (int i = 0; i < ciphertext.length(); i++) {
			buf.append((char) (ciphertext.charAt(i) ^ keys[i % 32]));
		}   
		String plaintext = buf.toString(); // 解密后的明文
		if (!plaintext.startsWith(CHECK_CODE)) {// 解密失败
			plaintext = "";
		} else {
			plaintext = plaintext.substring(CHECK_CODE.length()); // 解密成功，去除校验位
		}
		return plaintext;
		
	}
	
	/**
	 * 解密密文(使用MD5私钥解密)
	 * 
	 * 
	 * @Description TODO
	 * @author Robin
	 * @date 
	 * @Copyright:
	 */
	public static String DEcryptForMd5Key(String md5Key, String ciphertext) {
		
		StringBuffer buf = new StringBuffer();
		
		byte[] keys = md5Key.getBytes(); // 32位MD5私钥
		for (int i = 0; i < ciphertext.length(); i++) {
			buf.append((char) (ciphertext.charAt(i) ^ keys[i % 32]));
		}
		String plaintext = buf.toString(); // 解密后的明文
		if (!plaintext.startsWith(CHECK_CODE)) {// 解密失败
			plaintext = "";
		} else {
			plaintext = plaintext.substring(CHECK_CODE.length()); // 解密成功，去除校验位
		}
		
		return plaintext;
		
	}
	
	/**
	 * 获取MD5 串
	 * 
	 * 
	 * @Description TODO
	 * @author Robin
	 * @date 
	 * @Copyright:
	 */
	public static String getMD5Key(String plaintext){
		return md5Encrypt(CHECK_CODE + plaintext);
	}

	/**
	 * MD5加密
	 * 
	 * @Description TODO
	 * @author Robin
	 * @date 
	 * @Copyright:
	 */
	public static String md5Encrypt(String plaintext) {// 保持编码为UTF-8
		if (TextUtils.isEmpty(plaintext)) {
			plaintext = CHECK_CODE;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plaintext.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getMD5(String password)  {  //throws NoSuchAlgorithmException
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("md5");
			//		password = "123456";
			byte [] bytes =  digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			for(byte b: bytes){
				int number = b & 0xff;//加盐
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					buffer.append("0");
				}
				buffer.append(hex);
			}
			//md5加密后的值
//		System.out.println(buffer);
			return  buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

}
