
package com.maogousoft.wuliu.common.json;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.common.util.Base64Util;
import com.maogousoft.wuliu.common.util.NumberUtil;
import com.maogousoft.wuliu.common.util.TimeUtil;

public class JsonValueUtil
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(JsonValueUtil.class);

	public static Integer toInteger(Object value)
	{
		if (value == null)
		{
			return null;
		}

		if (value instanceof Integer)
		{
			return (Integer) value;
		}

		if (value instanceof Number)
		{
			return ((Number) value).intValue();
		}

		if (value instanceof String)
		{
			String strVal = (String) value;
			return NumberUtil.toInteger(strVal);
		}

		return null;
	}

	public static Integer toIntValue(Object value, int defaultValue)
	{
		Integer i = JsonValueUtil.toInteger(value);
		if (i == null)
		{
			return defaultValue;
		}
		return i;
	}

	public static Long toLong(Object value)
	{
		if (value == null)
		{
			return null;
		}

		if (value instanceof Long)
		{
			return (Long) value;
		}

		if (value instanceof Number)
		{
			return ((Number) value).longValue();
		}

		if (value instanceof String)
		{
			String strVal = (String) value;
			return NumberUtil.toLong(strVal);
		}

		return null;
	}

	public static long toLongValue(Object value, long defaultValue)
	{
		Long v = toLong(value);
		if (v == null)
		{
			return defaultValue;
		}
		return v;
	}

	public static Float toFloat(Object value)
	{
		if (value == null)
		{
			return null;
		}

		if (value instanceof Float)
		{
			return (Float) value;
		}

		if (value instanceof Number)
		{
			return ((Number) value).floatValue();
		}

		if (value instanceof String)
		{
			String strVal = (String) value;
			return NumberUtil.toFloat(strVal);
		}

		return null;
	}

	public static float toFloatValue(Object value, float defaultValue)
	{
		Float v = toFloat(value);
		if (v == null)
		{
			return defaultValue;
		}
		return v;
	}

	public static Double toDouble(Object value)
	{
		if (value == null)
		{
			return null;
		}

		if (value instanceof Double)
		{
			return (Double) value;
		}

		if (value instanceof Number)
		{
			return ((Number) value).doubleValue();
		}

		if (value instanceof String)
		{
			String strVal = (String) value;
			return NumberUtil.toDouble(strVal);
		}

		return null;
	}

	public static double toDoubleValue(Object value, double defaultValue)
	{
		Double v = toDouble(value);
		if (v == null)
		{
			return defaultValue;
		}
		return v;
	}

	public static String toString(Object value, String defaultValue)
	{
		if (value == null)
		{
			return defaultValue;
		}
		//json-lib
		if(value instanceof net.sf.json.JSON)
		{
			return defaultValue;
		}
		//fastjson
		if(value instanceof Map)
		{
			return defaultValue;
		}
		if(value instanceof Collection)
		{
			return defaultValue;
		}
		return value.toString();
	}

	public static Date toDate(Object value, Date defaultValue)
	{
		if (value == null)
		{
			return defaultValue;
		}
		Long millis = toLong(value);
		if (millis == null)
		{
			return defaultValue;
		}
		Date d = TimeUtil.toDate(millis);
		return d;
	}

	public static Timestamp toTimestamp(Object value, Timestamp defaultValue)
	{
		if (value == null)
		{
			return defaultValue;
		}
		Long millis = toLong(value);
		if (millis == null)
		{
			return defaultValue;
		}
		Timestamp t = TimeUtil.toTimestamp(millis);
		return t;
	}

	public static java.sql.Date toSqlDate(Object value, java.sql.Date defaultValue)
	{
		if (value == null)
		{
			return defaultValue;
		}
		Long millis = toLong(value);
		if (millis == null)
		{
			return defaultValue;
		}
		java.sql.Date d = new java.sql.Date(millis);
		return d;
	}

	public static Boolean toBoolean(Object value)
	{
		if (value == null)
		{
			return null;
		}

		if (value instanceof Boolean)
		{
			return (Boolean) value;
		}

		// if (value instanceof Number) {
		// return ((Number) value).intValue() == 1;
		// }

		if (value instanceof String)
		{
			String str = (String) value;
			if (StringUtils.isBlank(str))
			{
				return null;
			}

			if ("true".equals(str))
			{
				return Boolean.TRUE;
			}
			if ("false".equals(str))
			{
				return Boolean.FALSE;
			}
			// if ("1".equals(str)) {
			// return Boolean.TRUE;
			// }
		}

		return null;
	}

	public static boolean toBooleanValue(Object value, boolean defaultValue)
	{
		Boolean b = toBoolean(value);
		if(b == null)
		{
			return defaultValue;
		}
		return b.booleanValue();
	}

	public static <K,V> void verifyIsNull(K key, V value)
	{
		if(value == null)
		{
			throw new BusinessException("参数\"" + key + "\"不能为空");
		}
	}

	public static byte[] toBytesFromBase64(String value) {
		if(value == null) {
			return null;
		}
		byte[] bytes = Base64Util.decodeToBytes(value);
		return bytes;
	}
}
