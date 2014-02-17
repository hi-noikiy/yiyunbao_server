package com.maogousoft.wuliu.common.json;

import java.util.Iterator;

/**
 *
 * @author yangfan
 */
public interface Param extends ParamBase<String>
{

	/**
	 * 获取int参数
	 * @param name 参数名称
	 * @param defaultValue 默认值
	 * @return int
	 */
//	public int getInt(String name, int defaultValue);

	public Iterator<String> keys();

}