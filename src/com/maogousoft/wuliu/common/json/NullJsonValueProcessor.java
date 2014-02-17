package com.maogousoft.wuliu.common.json;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-5-18 下午3:42:52
 */
public class NullJsonValueProcessor implements JsonValueProcessor {

	final private Object defaultValue;

	public NullJsonValueProcessor(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
		return process(value, jsonConfig);
	}

	@Override
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return process(value, jsonConfig);
	}

	protected Object process(Object value, JsonConfig jsonConfig) {
		if (value == null) {
			return defaultValue;
		}
		return value;
	}
}