package com.maogousoft.wuliu.common.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.maogousoft.wuliu.common.exception.BusinessException;

/**
 * 数字格式化工具类
 * @author yangfan
 */
public class NumberUtil
{
	private static final Logger log = LoggerFactory.getLogger(NumberUtil.class);

	/**
	 * 格式化数字
	 *
	 * <pre>
	 * format(11.22, "000.000")) => "011.220"
	 * </pre>
	 *
	 * @param number
	 * @param format
	 * @return
	 * @see DecimalFormat
	 * @see NumberFormat
	 */
	public static String format(Number number, String format)
	{
		DecimalFormat df = new DecimalFormat(format);
		return df.format(number);
	}

	/**
	 * 按照指定格式转换数字
	 *
	 * <pre>
	 * float percent = parse("90%","#0%").floatValue(); ==> 0.9
	 * </pre>
	 *
	 * @param str
	 * @param pattern
	 * @return
	 * @see DecimalFormat
	 * @see NumberFormat
	 */
	public static Number parse(String str, String pattern)
	{
		DecimalFormat format = new DecimalFormat(pattern);
		Number number;
		try
		{
			number = format.parse(str);
			return number;
		}
		catch (ParseException e)
		{
			log.error("parse数字失败.pattern=" + pattern + ",str=" + str, e);
			throw new BusinessException("parse数字失败.pattern=" + pattern + ",str=" + str + ",cause:" + e.getMessage());
		}
	}

	/**
	 * 数字左边补零，以及正负号
	 *
	 * <pre>
	 * assertEquals(&quot;+01&quot;, leftpadZeroWithPrefix(1, 3));
	 * assertEquals(&quot;-01&quot;, leftpadZeroWithPrefix(-1, 3));
	 * assertEquals(&quot;+15&quot;, leftpadZeroWithPrefix(15, 3));
	 * assertEquals(&quot;-15&quot;, leftpadZeroWithPrefix(-15, 3));
	 * assertEquals(&quot;+00&quot;, leftpadZeroWithPrefix(0, 3));
	 * </pre>
	 *
	 * @param number
	 * @param size
	 *            包含正负号在内的字符串长度
	 * @return
	 */
	public static String leftpadZeroWithPrefix(long number, final int size)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size - 1; i++)
		{
			sb.append("0");
		}
		String format = sb.toString();
		// "+00;-00"
		DecimalFormat df = new DecimalFormat("+" + format + ";-" + format);
		return df.format(number);
	}

	/**
	 * 数字左边补零(如果是负数，前面会多一位符号)
	 *
	 * <pre>
	 * assertEquals(&quot;001&quot;, leftpadZero(1, 3));
	 * assertEquals(&quot;-001&quot;, leftpadZero(-1, 3));
	 * assertEquals(&quot;015&quot;, leftpadZero(15, 3));
	 * assertEquals(&quot;-015&quot;, leftpadZero(-15, 3));
	 * assertEquals(&quot;000&quot;, leftpadZero(0, 3));
	 * </pre>
	 *
	 * @param number
	 * @param size
	 *            字符串长度
	 * @return
	 */
	public static String leftpadZero(long number, final int size)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++)
		{
			sb.append("0");
		}
		String format = sb.toString();
		// "00"
		DecimalFormat df = new DecimalFormat(format);
		return df.format(number);
	}

	/**
	 * 将字符串转换为Integer包装类,如果无法转换,返回null
	 *
	 * <pre>
	 * toInteger("15") ==> 15
	 * toInteger("15.0") ==> 15
	 * toInteger("abc") ==> null
	 * toInteger("s123") ==> null
	 * toInteger("15.7") ==> 15
	 * toInteger("+15.7") ==> 15
	 * toInteger("-15.7") ==> -15
	 * </pre>
	 *
	 * @param str
	 * @return
	 */
	public static Integer toInteger(String str)
	{
		if (StringUtils.isBlank(str))
		{
			return null;
		}
		try
		{
			Double d = Double.valueOf(str);
			return d.intValue();
		}
		catch (NumberFormatException e2)
		{
			return null;
		}
		/**
		 * try { return Integer.valueOf(str); } catch(NumberFormatException ex)
		 * { // return null; try { Double d = Double.valueOf(str); return
		 * d.intValue(); } catch(NumberFormatException e2) { return null; } }
		 */
	}

	/**
	 * 将字符串转换为int
	 *
	 * <pre>
	 * toIntValue("15",0) ==> 15
	 * toIntValue("15.0",0) ==> 15
	 * toIntValue("abc",0) ==> 0
	 * toIntValue("s123",0) ==> 0
	 * toIntValue("15.7",0) ==> 15
	 * toIntValue("+15.7",0) ==> 15
	 * toIntValue("-15.7",0) ==> -15
	 * </pre>
	 *
	 * @param str
	 * @param defaultValue
	 * @return 如果无法转换,返回 defaultValue
	 */
	public static Integer toIntValue(String str, int defaultValue)
	{
		Integer i = toInteger(str);
		if (i == null)
		{
			return defaultValue;
		}
		return i;
	}

	public static Long toLong(String str)
	{
		if (StringUtils.isBlank(str))
		{
			return null;
		}
		try
		{
			Double d = Double.valueOf(str);
			return d.longValue();
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	public static Float toFloat(String str)
	{
		if (StringUtils.isBlank(str))
		{
			return null;
		}
		if (str == null)
		{
			return null;
		}
		try
		{
			return Float.parseFloat(str);
		}
		catch(NumberFormatException ex)
		{
			return null;
		}
	}

	public static Double toDoubleRequired(String str)
	{
		Double d = toDouble(str);
		Assert.isTrue(d!=null && !Double.isNaN(d) && !Double.isInfinite(d), str);
		return d;
	}

	public static Double toDouble(String str)
	{
		if (StringUtils.isBlank(str))
		{
			return null;
		}
		if (str == null)
		{
			return null;
		}
		try
		{
			return Double.parseDouble(str);
		}
		catch(NumberFormatException ex)
		{
			return null;
		}
	}
}
