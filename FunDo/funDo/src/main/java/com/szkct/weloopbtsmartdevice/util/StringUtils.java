package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {
	public final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	public final static SimpleDateFormat TRACK_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.ENGLISH);
	public final static SimpleDateFormat TRACK_HOUR_FORMAT = new SimpleDateFormat(
			"HH:mm", Locale.ENGLISH);

	public final static String TRACK_DATE_FORMAT_STRING = "yyyy-MM-dd";
	public final static String TRACK_HOUR_FORMAT_STRING = "HH:mm";


	/**
	 * 将字符串转移成整数.
	 * 
	 * @param num
	 *            the num
	 * @return the int
	 */
	public static int toInt(String num) {
		try {
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 判断字符串是否为null或者为空.
	 * 
	 * @param str
	 *            the str
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String str) {
		if (null == str || "" ==  str || str.trim().equals("") || str.trim().equals("null")){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 返回一个StringBuffer对象.
	 * 
	 * @return the buffer
	 */
	public static StringBuffer getBuffer() {
		return new StringBuffer(50);
	}

	/**
	 * 返回一个StringBuffer对象.
	 * 
	 * @param length
	 *            the length
	 * @return the buffer
	 */
	public static StringBuffer getBuffer(int length) {
		return new StringBuffer(length);
	}

	public static Date parseStrToDate(String dateStr) {
		try {
			Date date = SIMPLE_DATE_FORMAT.parse(dateStr);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseStrToDate(String dateStr,SimpleDateFormat format){ //转换成日期格式
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(dateStr);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Calendar parseStrToCalendar(String dateStr) { //转换成日历格式
		Calendar calendar = null;
		Date parseDate = parseStrToDate(dateStr);
		if (parseDate != null) {
			calendar = Calendar.getInstance();
			calendar.setTime(parseDate);
			return calendar;
		}
		return calendar;
	}

	/**
	 * 格式一个日期.
	 *
	 * @param longDate
	 *            需要格式日期的长整数的字符串形式
	 * @param format
	 *            格式化参数
	 * @return 格式化后的日期
	 */
	public static String getStrDate(String longDate, String format) {
		if (isEmpty(longDate))
			return "";
		long time = Long.parseLong(longDate);
		Date date = new Date(time);
		return getStrDate(date, format);
	}

	public static String getStrDateTime(long time) {
		Date date = new Date(time);
		return SIMPLE_DATE_FORMAT.format(date);
	}

	/**
	 * 格式一个日期.
	 *
	 * @param time
	 *            the time
	 * @param format
	 *            格式化参数
	 * @return 格式化后的日期
	 */
	public static String getStrDate(long time, String format) {
		Date date = new Date(time);
		return getStrDate(date, format);
	}

	/**
	 * 返回当前日期的格式化（yyyy-MM-dd）表示.
	 *
	 * @return the str date
	 */
	public static String getStrDate() {
		SimpleDateFormat dd = SIMPLE_DATE_FORMAT;
		return dd.format(new Date());
	}

	/**
	 * 返回当前日期的格式化表示.
	 *
	 * @param date
	 *            指定格式化的日期
	 * @param formate
	 *            格式化参数
	 * @return the str date
	 */
	public static String getStrDate(Date date, String formate) {
		SimpleDateFormat dd = new SimpleDateFormat(formate, Locale.ENGLISH);
		return dd.format(date);
	}

	/**
	 * sql特殊字符转义.
	 *
	 * @param keyWord
	 *            关键字
	 * @return the string
	 */
	public static String sqliteEscape(String keyWord) {
		keyWord = keyWord.replace("/", "//");
		keyWord = keyWord.replace("'", "''");
		keyWord = keyWord.replace("[", "/[");
		keyWord = keyWord.replace("]", "/]");
		keyWord = keyWord.replace("%", "/%");
		keyWord = keyWord.replace("&", "/&");
		keyWord = keyWord.replace("_", "/_");
		keyWord = keyWord.replace("(", "/(");
		keyWord = keyWord.replace(")", "/)");
		return keyWord;
	}

	/**
	 * sql特殊字符反转义.
	 *
	 * @param keyWord
	 *            关键字
	 * @return the string
	 */
	public static String sqliteUnEscape(String keyWord) {
		keyWord = keyWord.replace("//", "/");
		keyWord = keyWord.replace("''", "'");
		keyWord = keyWord.replace("/[", "[");
		keyWord = keyWord.replace("/]", "]");
		keyWord = keyWord.replace("/%", "%");
		keyWord = keyWord.replace("/&", "&");
		keyWord = keyWord.replace("/_", "_");
		keyWord = keyWord.replace("/(", "(");
		keyWord = keyWord.replace("/)", ")");
		return keyWord;
	}

	/**
	 * 保留字符数
	 *
	 * @param str
	 *            原始字符串
	 * @param length
	 *            保留字符数
	 * @param isPoints
	 *            是否加省略号
	 * @return 格式化后的日期
	 */
	public static String getStrFomat(String str, int length, boolean isPoints) {
		String result = "";

		if (str.length() > length) {
			result = str.substring(0, length);
			if (isPoints) {
				result = result + "...";
			}
		} else {
			result = str;
		}

		return result;

	}

	public static boolean containsEmoji(String source) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (isEmojiCharacter(codePoint)) {
				// 如果不能匹配,则该字符是Emoji表情
				return true;
			}
		}
		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
				|| (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}

	/**
	 * utf-8 转unicode
	 *
	 * @param inStr
	 * @return String
	 */
	public static String utf8ToUnicode(String inStr) {
		char[] myBuffer = inStr.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < inStr.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
			if (ub == UnicodeBlock.BASIC_LATIN) {
				sb.append(myBuffer[i]);
			} else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				int j = (int) myBuffer[i] - 65248;
				sb.append((char) j);
			} else {
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s);
				String unicode = "\\u" + hexS;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	public static ByteBuffer UTF8ToUnicode(String strChar) {
		try {
			byte[] arrByUTF8 = strChar.getBytes("utf-8");

			ByteBuffer arrByUnicode = ByteBuffer.allocate(arrByUTF8.length * 2);

			int nBuf = 0;
			int nBufPos = 0;
			int nNeed = 0;
			int nCur = 0;
			int i = 0;
			int nLen = arrByUTF8.length;
			while (i < nLen) {
				nCur = arrByUTF8[i];
				nCur &= 0xff;
				i++;
				// -----单字节
				if ((nCur & 0x80) == 0) {
					nNeed = 1;
					nBuf = 0;
					arrByUnicode.put((byte) nCur);
					arrByUnicode.put((byte) 0x00);
					continue;
				}

				// -----多字节
				else {
					// 头一个字节
					if ((nCur & 0x40) != 0) {
						nBuf = 0;
						nBufPos = 1;

						// 单个字符需要2个utf-8字节
						if ((nCur & 0x20) == 0) {
							nNeed = 2;
							nBuf = 0;

							nCur &= 0x1f;
							nCur <<= 6;
							nBuf += nCur;
						}
						// 单个字符需要3个utf-8字节
						else if ((nCur & 0x10) == 0) {
							nNeed = 3;
							nBuf = 0;

							nCur &= 0x0f;
							nCur <<= 12;
							nBuf += nCur;
						}
						// 单个字符需要4个utf-8字节
						else if ((nCur & 0x08) == 0) {
							nNeed = 4;
							nBuf = 0;

							nCur &= 0x07;
							nCur <<= 18;
							nBuf += nCur;
						}
						// 单个字符需要5个utf-8字节
						else if ((nCur & 0x04) == 0) {
							nNeed = 5;
							nBuf = 0;

							nCur &= 0x03;
							nCur <<= 24;
							nBuf += nCur;
						}
						// 单个字符需要6个utf-8字节
						else if ((nCur & 0x02) == 0) {
							nNeed = 6;
							nBuf = 0;

							nCur &= 0x01;
							nCur <<= 30;
							nBuf += nCur;
						}
					}

					// 多字节后面的字节
					else {
						nCur &= 0x3f;

						nBufPos++;
						nCur <<= (nNeed - nBufPos) * 6;
						nBuf += nCur;

						// 一个UNICODE字符接收完成
						if (nBufPos >= nNeed) {
							nBuf = InvertUintBit(nBuf);
							if (nNeed <= 3) {
								nBuf >>= 16;
								arrByUnicode.putShort((short) nBuf);
							} else {
								arrByUnicode.putInt(nBuf);
							}
							nBuf = 0;
						}
					}
				}
			}
			return arrByUnicode;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String UnicodeToUTF8(ByteBuffer arrByUnicode) {
		ByteBuffer arrByUTF8 = null;
		if (arrByUnicode.capacity() < 2) {
			return "";
		}
		// arrByUnicode.position(0);
		arrByUTF8 = ByteBuffer.allocate(arrByUnicode.capacity()
				- arrByUnicode.position());

		int byHeigh = 0;
		int byLow = 0;
		int u1 = 0;
		int u2 = 0;
		int u3 = 0;
		int nSum = 0;

		int nLen = 0;
		try {
			while (true && (arrByUnicode.position() < arrByUnicode.capacity())){
				// 服务器返回的数据，低位在前
				byLow = arrByUnicode.get();
				byLow &= 0xff;
				byHeigh = arrByUnicode.get();
				byHeigh &= 0xff;

				nLen++;

				// 结尾
				if (byLow == 0 && byHeigh == 0) {
					break;
				}

				nSum = 0;
				nSum = (byHeigh << 8) & 0xff00;
				nSum += byLow;

				// 1位
				if (nSum <= 0x7f) {
					arrByUTF8.put((byte) nSum);
				}

				// 2位
				else if (nSum >= 0x80 && nSum <= 0x07ff) {
					u1 = 0xc0;
					u1 += ((nSum >> 6) & 0x1f);

					u2 = 0x80;
					u2 += (nSum & 0x3f);

					arrByUTF8.put((byte) u1);
					arrByUTF8.put((byte) u2);
				}

				// 3位
				else if (nSum >= 0x0800 && nSum <= 0xffff) {
					u1 = 0xe0;
					u1 += ((nSum >> 12) & 0x0f);

					u2 = 0x80;
					u2 += ((nSum >> 6) & 0x3f);

					u3 = 0x80;
					u3 += (nSum & 0x3f);

					arrByUTF8.put((byte) u1);
					arrByUTF8.put((byte) u2);
					arrByUTF8.put((byte) u3);
				}

			}

			String str = new String(arrByUTF8.array());
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// 把无符号整形4个字节低高位倒置, 0x01 02 03 04 ---> 0x04 03 02 01
	public static int InvertUintBit(int nNum) {
		int nResult = 0;
		int nTemp = 0;

		// 0x00 00 00 04 ---> 0x04 00 00 00
		nTemp = nNum;
		nTemp <<= 24;
		nTemp &= 0xff000000;
		nResult += nTemp;

		// 0x00 00 03 00 ---> 0x00 03 00 00
		nTemp = nNum;
		nTemp <<= 8;
		nTemp &= 0xff0000;
		nResult += nTemp;

		// 0x00 02 00 00 ---> 0x00 00 02 00
		nTemp = nNum;
		nTemp >>= 8;
		nTemp &= 0xff00;
		nResult += nTemp;

		// 0x01 00 00 00 ---> 0x00 00 00 01
		nTemp = nNum;
		nTemp >>= 24;
		nTemp &= 0xff;
		nResult += nTemp;

		return nResult;
	}

	// --------------------------------------
	// 把有符号整形4个字节低高位倒置, 0x01 02 03 04 ---> 0x04 03 02 01
	public int InvertIntBit(int nNum) {
		int nResult = 0;
		int nTemp = 0;

		// 0x00 00 00 04 ---> 0x04 00 00 00
		nTemp = nNum;
		nTemp <<= 24;
		nTemp &= 0xff000000;
		nResult += nTemp;

		// 0x00 00 03 00 ---> 0x00 03 00 00
		nTemp = nNum;
		nTemp <<= 8;
		nTemp &= 0xff0000;
		nResult += nTemp;

		// 0x00 02 00 00 ---> 0x00 00 02 00
		nTemp = nNum;
		nTemp >>= 8;
		nTemp &= 0xff00;
		nResult += nTemp;

		// 0x01 00 00 00 ---> 0x00 00 00 01
		nTemp = nNum;
		nTemp >>= 24;
		nTemp &= 0xff;
		nResult += nTemp;

		return nResult;
	}

	public static String binary(byte[] bytes, int radix) {
		return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
	}

	public static SpannableStringBuilder createTextBold(String content) {
		SpannableStringBuilder spanBuilder = new SpannableStringBuilder(content);
		// style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
		// size 为0 即采用原始的正常的 size大小
		spanBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, content.length(),
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		return spanBuilder;
	}

	public static boolean isEmail(String strEmail) {
		String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	public static boolean isPhone(String strPhone) {
		String strPattern = "^1[0-9]{10}$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strPhone);
		return m.matches();
	}

	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		String expression = "(\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$";

		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}


	public static String getFilePath(Context context,String fileName) {
		String dirName = null;
		try {
			String sdStatus = Environment.getExternalStorageState();
			if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					dirName = Environment.getExternalStorageDirectory().getAbsolutePath();
					dirName = dirName + File.separator + context.getPackageName();
				} else {
					dirName = context.getFilesDir().getAbsolutePath();
				}
				File f = new File(dirName);
				if (!f.exists()) {
					f.mkdir();
				}
				if (!StringUtils.isEmpty(fileName)) {
					if(!fileName.startsWith(File.separator)){
						dirName += File.separator;
					}
					dirName = dirName + fileName + File.separator;
					f = new File(dirName);
					if (!f.exists()) {
						f.mkdir();
					}
				}
			}
		} catch (Exception e) {
			dirName = null;
			e.printStackTrace();
		}
		return dirName;
	}

	// 获得本机ＣＰＵ大小端
	public static boolean isBigendian() {
		short i = 0x1;
		boolean bRet = ((i >> 8) == 0x1);
//		L.i("bRet = " + bRet);
		return bRet;
	}
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 字符串转日期
	 *
	 * @param String
	 */
	public static Date StringToDate(String String) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date time = null;
		try {
			time = format.parse(String);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * ����ת�����ַ���
	 *
	 * @param date
	 * @return str
	 */
	public static String DateToString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		String string = format.format(date);
		return string;
	}

	/**
	 * ����ת����
	 *
	 * @param date
	 * @return Calendar
	 */
	public static Calendar DateToCalendar(Date date) {
		Calendar startdate = Calendar.getInstance();
		startdate.setTime(date);
		return startdate;
	}

	/**
	 * ����ת����
	 *
	 * @param calendar
	 * @return Date
	 */
	public static Date CalendarToDate(Calendar calendar) {
		Date date = calendar.getTime();
		return date;
	}

	/* 将10 or 13 位时间戳转为日期字符串
     * convert the number 1407449951 1407499055617 to date/time format timestamp
     */
	public static String timestamp2Date(String str_num) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		if (str_num.length() == 13) {
			String date = sdf.format(new Date(toLong(str_num)));
//	        LogUtil.d(Constant.TAG + "将13位时间戳:" + str_num + "转化为字符串:", date);
			System.out.println("将13位时间戳: str_num 转化为字符串:-------" + date);
			return date;
		} else {
			String date = sdf.format(new Date(toInt2(str_num) * 1000L));
//	        LogUtil.d(Constant.TAG + "将10位时间戳:" + str_num + "转化为字符串:", date);
			System.out.println("将10位时间戳: str_num 转化为字符串:-------" + date);
			return date;
		}
	}

	public static String timestamp2Datemonth(String str_num) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);  // yyyy-MM
		if (str_num.length() == 13) {  // 13位时间戳
			String date = sdf.format(new Date(toLong(str_num)));
//	        LogUtil.d(Constant.TAG + "将13位时间戳:" + str_num + "转化为字符串:", date);
			System.out.println("将13位时间戳: str_num 转化为字符串:-------" + date);
			return date;
		} else {    // 10位时间戳
			String mtimes = "";
			if(str_num.length() == 12){    //todo --- 临时处理
				mtimes = str_num.substring(0,9);
			}else {
				mtimes = str_num;
			}
			/////////////////////////////////////////////////////////////////////
			String dateString = StringUtils.timestamp2Date(mtimes);
			return dateString.substring(0,7);
		}
	}

	/**
	 * String转long
	 *
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 对象转整
	 *
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt2(String obj) {
		if (obj == null)
			return 0;
		return Integer.parseInt(obj);
	}

	public static float divideToFloat(int a,int b,int c){
		BigDecimal bigDecimal = new BigDecimal(a);
		return bigDecimal.divide(new BigDecimal(b),c,BigDecimal.ROUND_HALF_UP).floatValue();
	}
}
