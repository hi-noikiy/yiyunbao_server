package com.maogousoft.wuliu.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

/**
 * 时间工具
 * @author yangfan(kenny0x00@gmail.com) 2013-3-25 下午11:20:18
 */
public class TimeUtil {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TimeUtil.class);

	public static Date parse(String str, String format, Date defaultValue) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = sdf.parse(str);
			return date;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Date toDate(long time)
	{
		Date date = new Date(time);
		return date;
	}

	public static String format(Date date, String format) {
		String str = DateFormatUtils.format(date, format);
		return str;
	}

	public static Timestamp toTimestamp(Long millis) {
		Timestamp t = new Timestamp(millis);
		return t;
	}
}
