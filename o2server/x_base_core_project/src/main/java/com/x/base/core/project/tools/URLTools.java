package com.x.base.core.project.tools;

import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author sword
 */
public class URLTools {

	private static final String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
			"i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
			"u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
			"6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
			"I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
			"U" , "V" , "W" , "X" , "Y" , "Z"
	};

	private static final Random random = new SecureRandom();

	public static String getQueryStringParameter(String queryString, String name) {
		String value = StringUtils.substringAfter(queryString, name + "=");
		if (StringUtils.contains(value, "&")) {
			return StringUtils.substringBefore(value, "&");
		} else if (StringUtils.contains(value, "!")) {
			return StringUtils.substringBefore(value, "!");
		} else {
			return value;
		}
	}

	/**
	 * Java8 URLEncoder.encode throw Exception,Java11 URLEncoder.encode 不抛出错误
	 * 统一这两个方法.
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		try {
			return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 生成指定位数的短链接
	 * @param url
	 * @param length 短链接位数，最少4位
	 * @return
	 */
	public static String shortUrl(String url, int length) {
		if(StringUtils.isBlank(url)){
			return url;
		}
		int defaultLength = 4;
		if(length < defaultLength){
			length = defaultLength;
		}
		String hex = MD5Tool.getMD5Str(url);
		// 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
		int i = random.nextInt(26);
		String sTempSubString = hex.substring(i, i + 8);
		long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
		String outChars = "" ;
		for ( int j = 0; j < length; j++) {
			// 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
			long index = 0x0000003D & lHexLong;
			outChars += chars[( int ) index];
			// 每次循环按位右移 5 位
			lHexLong = lHexLong >> 5;
		}
		return outChars;
	}

	/**
	 * 生成6位数的短链接
	 * @param url
	 * @return
	 */
	public static String shortUrl(String url) {
		return shortUrl(url, 6);
	}

	public static void main(String[] args) {
		System.out.println(shortUrl("http://dev.o2oa.net/x_desktop/index.html"));
	}

}
