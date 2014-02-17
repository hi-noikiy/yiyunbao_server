package com.maogousoft.wuliu.common.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-3-30 上午12:31:36
 */
public class UUIDUtil {

	private static AtomicLong i = new AtomicLong(1);

	public static String getUUID() {
		String str = UUID.randomUUID().toString();
		return MD5Util.MD5(str);
	}

	public static void main(String[] args) {
		System.out.println(getUUID());
	}
}
