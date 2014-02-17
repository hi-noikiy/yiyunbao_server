package com.maogousoft.wuliu.common.json;


/**
 *
 * @author yangfan
 */
public interface ListParam extends Iterable<Object>, ParamBase<Integer>
{
	public int size();

	public boolean isEmpty();
}
