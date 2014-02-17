package com.maogousoft.wuliu.common.json;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;
import net.sf.json.processors.JsonValueProcessor;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-4-15 上午12:16:29
 */
public class RecordJsonProcessor implements JsonBeanProcessor, JsonValueProcessor {

	@Override
	public JSONObject processBean(Object bean, JsonConfig jsonConfig) {
		if (bean instanceof Record) {
			Record rec = (Record)bean;
			String[] columnNames = rec.getcolumnNames();
			JSONObject json = new JSONObject();
			Arrays.sort(columnNames);
			for (String columnName : columnNames) {
				Object obj = rec.get(columnName);
				if(obj != null) {
					JsonValueProcessor jsonValueProcessor = jsonConfig.findJsonValueProcessor(obj.getClass());
					if(jsonValueProcessor != null) {
						obj = jsonValueProcessor.processObjectValue(columnName, obj, jsonConfig );
					}
					json.put(columnName, obj);
				}else {
					json.accumulate( columnName, "");
				}
			}
			return json;
		}
		if(bean instanceof Model) {
			Model model = (Model)bean;
			String[] columnNames = model.getAttrNames();
			JSONObject json = new JSONObject();
			Arrays.sort(columnNames);
			for (String columnName : columnNames) {
				Object obj = model.get(columnName);
				if(obj != null) {
					JsonValueProcessor jsonValueProcessor = jsonConfig.findJsonValueProcessor(obj.getClass());
					if(jsonValueProcessor != null) {
						obj = jsonValueProcessor.processObjectValue(columnName, obj, jsonConfig );
					}
					json.put(columnName, obj);
				}else {
					json.accumulate( columnName, "");
				}
			}
			return json;
		}
		return new JSONObject(true);
	}

	@Override
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return processBean(value, jsonConfig);
	}

	@Override
	public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
		return processBean(value, jsonConfig);
	}
}
