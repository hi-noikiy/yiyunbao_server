package com.maogousoft.wuliu.common.json;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-3-31 下午5:41:44
 */
public class JsonUtil {

	private static JsonConfig cfg = initJsonConfig();

	public static String toJSONString(Object obj) {
		return toJSONObject(obj).toString();
	}

	public static JSONObject toJSONObject(Object obj)
	{
		JSONObject json = JSONObject.fromObject(obj,cfg);
		return json;
	}

	public static JSONArray toJSONArray(Collection<?> list)
	{
		JSONArray json  = JSONArray.fromObject(list,cfg);
		return json;
	}

	private static JsonConfig initJsonConfig()
	{
		JsonConfig jsonConfig = new JsonConfig();

		JsonValueProcessor processor = new JsonDateValueProcessor();
		jsonConfig.registerJsonValueProcessor(Date.class, processor);
		jsonConfig.registerJsonValueProcessor(java.sql.Date.class, processor);
		jsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, processor);
		jsonConfig.registerJsonValueProcessor(Integer.class, new NullJsonValueProcessor(0));
		jsonConfig.registerJsonValueProcessor(Long.class, new NullJsonValueProcessor(0));
		jsonConfig.registerJsonValueProcessor(Double.class, new NullJsonValueProcessor(0.0));
		jsonConfig.registerJsonValueProcessor(Float.class, new NullJsonValueProcessor(0.0f));
		jsonConfig.registerJsonValueProcessor(String.class, new NullJsonValueProcessor(""));
		jsonConfig.registerJsonValueProcessor(Object.class, new NullJsonValueProcessor(""));

		RecordJsonProcessor recordProcessor = new RecordJsonProcessor();
		jsonConfig.registerJsonBeanProcessor(Record.class, recordProcessor);
		jsonConfig.registerJsonValueProcessor(Record.class, new RecordJsonProcessor());
		jsonConfig.registerJsonValueProcessor(Model.class, new RecordJsonProcessor());
		jsonConfig.registerJsonBeanProcessor(Model.class, new RecordJsonProcessor());

		return jsonConfig;
	}

	public static void main(String[] args) {
		Record record = new Record();
		record.set("username", null);
		record.set("age", null);

		System.out.println(JsonUtil.toJSONString(record));
	}
}
