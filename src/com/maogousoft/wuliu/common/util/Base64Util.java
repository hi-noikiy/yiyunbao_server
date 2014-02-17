package com.maogousoft.wuliu.common.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maogousoft.wuliu.common.exception.BusinessException;

/**
 * Base64工具类
 * @author yangfan(kenny0x00@gmail.com) 2013-4-6 下午4:59:09
 */
public class Base64Util {

	private static final Logger log = LoggerFactory.getLogger(Base64Util.class);
	private static final String DEFAULT_ENCODING = "utf-8";

	public static byte[] decodeToBytes(String value) {
		return decodeToBytes(value, DEFAULT_ENCODING);
	}

	public static byte[] decodeToBytes(String value, String encoding) {
		byte[] bytes;
		try {
			bytes = Base64.decodeBase64(value.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException("Can't decode from base64 by encoding:" + encoding, e);
		}
		return bytes;
	}

	public static String decodeToString(String value) {
		return decodeToString(value, DEFAULT_ENCODING);
	}

	public static String decodeToString(String value, String encoding) {
		byte[] bytes;
		try {
			bytes = Base64.decodeBase64(value.getBytes(encoding));
			return new String(bytes, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException("Can't decode base64 by encoding:" + encoding, e);
		}
	}

	public static String encodeFromBytes(byte[] bytes) {
		return encodeFromBytes(bytes, DEFAULT_ENCODING);
	}

	public static String encodeFromBytes(byte[] bytes, String encoding) {
		try {
			byte[] reuslts = Base64.encodeBase64(bytes);
			return new String(reuslts, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException("Can't encode base64 by encoding:" + encoding, e);
		}
	}

	public static String encodeFromString(String value) {
		return encodeFromString(value, DEFAULT_ENCODING);
	}

	public static String encodeFromString(String value, String encoding) {
		try {
			byte[] bytes = value.getBytes(encoding);
			byte[] reuslts = Base64.encodeBase64(bytes);
			return new String(reuslts, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException("Can't encode base64 by encoding:" + encoding, e);
		}
	}

	public static void main(String[] args) {
		String str = "abc";
		log.debug(Base64Util.encodeFromString(str, "utf-8"));
	}
}
