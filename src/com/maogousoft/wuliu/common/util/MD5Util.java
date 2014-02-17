/**
 * @filename MD5Util.java
 */
package com.maogousoft.wuliu.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description MD5加密
 * @author shevliu
 * @email shevliu@gmail.com
 * Jul 28, 2012 2:23:45 PM
 */
public class MD5Util {

	public static String MD5(String plaintext)  {
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plaintext.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
}
