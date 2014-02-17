package com.maogousoft.wuliu.common.util;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串处理
 *
 * @author yangfan 2013-3-25 上午10:01:14
 */
public class StrUtil {

	private static final Logger log = LoggerFactory.getLogger(StrUtil.class);

	/**
	 * 将下划线样式的字符串转换为CamelCase
	 * <p>
	 * toCamelCase("user_name",true) -&gt; UserName<br>
	 * toCamelCase("user_name",false) -&gt; userName<br>
	 * toCamelCase("username",true) -&gt; Username<br>
	 * </p>
	 */
	public static String toCamelCase(String str, boolean capitalize) {
		if (str == null)
			str = "";
		StringBuffer buffer = new StringBuffer(str.length());
		// char list[] = name.toLowerCase().toCharArray();
		char list[] = str.toCharArray();
		for (int i = 0; i < list.length; i++) {
			if (i == 0 && capitalize) {
				buffer.append(Character.toUpperCase(list[i]));
			} else if (list[i] == '_' && (i + 1) < list.length && i != 0) {
				buffer.append(Character.toUpperCase(list[++i]));
			} else
				buffer.append(list[i]);
		}
		return buffer.toString();
	}

	public static String toCamelCase(String str) {
		return toCamelCase(str, false);
	}

	/**
	 * 将CamelCase的字符串改为下划线的形式
	 * <p>
	 * toUnderscoreCase("userName") -&gt; user_name<br>
	 * toUnderscoreCase("UserName") -&gt; user_name<br>
	 * </p>
	 *
	 * @param str
	 * @return
	 */
	public static String toUnderscoreCase(String str) {
		StringBuilder result = new StringBuilder();
		if (str != null && str.length() > 0) {
			result.append(str.substring(0, 1).toLowerCase());
			for (int i = 1; i < str.length(); i++) {
				String s = str.substring(i, i + 1);
				if (s.equals(s.toUpperCase())) {
					result.append("_");
					result.append(s.toLowerCase());
				} else {
					result.append(s);
				}
			}
		}
		return result.toString();
	}

	public static String format(String msg, Object... args) {
		try {
			String str = String.format(msg, args);
			return str;
		} catch (Exception ex) {
			log.error("Error on formating msg:" + msg + ",args=" + ArrayUtils.toString(args), ex);
			return msg;
		}
	}

//	public static String hexToString(byte[] bytes) {
//		String str = Hex.encodeHexString(bytes);
//		return str;
//	}

//	public static byte[] stringToHex(String str) {
//		try {
//			byte[] bytes = Hex.decodeHex(str.toCharArray());
//			return bytes;
//		} catch (DecoderException e) {
//			log.error(e.getMessage(),e);
//			return null;
//		}
//	}

	public static void main(String[] args) throws Exception {
		log.debug(toUnderscoreCase("UserName")); // ->user_name
//		log.debug(hexToString("abc".getBytes("utf-8")));
	}
}
