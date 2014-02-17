package com.maogousoft.wuliu.common.json;

import java.util.Date;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 *
 * @author yangfan
 */
public class JsonDateValueProcessor implements JsonValueProcessor {

	@Override
	public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
		return process(value, jsonConfig);
	}

	@Override
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return process(value, jsonConfig);
	}

	protected Object process(Object bean, JsonConfig jsonConfig) {
		if (bean instanceof java.sql.Date) {
			long time = ((java.sql.Date)bean).getTime();
			return time;
		}
		if (bean instanceof java.sql.Timestamp) {
			long time = ((java.sql.Timestamp)bean).getTime();
			return time;
		}
		if (bean instanceof Date) {
			long time = ((Date) bean).getTime();
			return time;
		}
		return new JSONObject(true);
	}
}
