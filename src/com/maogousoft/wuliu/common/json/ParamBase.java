
package com.maogousoft.wuliu.common.json;


public interface ParamBase<T> extends ConfigBase<T>
{
	public enum ParamType
	{
		Param,ListParam,String,Int,Long,Double,Float,Date,SqlDate,Timestamp
	}

	public boolean isParam(T key);

	public boolean isListParam(T key);

	public Param getParam(T key);

	public ListParam getListParam(T key);

	public int[] getIntegerArray(T key);

	/**
	 * 获取long型数组
	 * @param name
	 * @return long数组，如果参数为空，则返回长度为0的数组(永远不会返回null值)
	 */
	public long[] getLongArray(T key);

	public String[] getStringArray(T key);
}
