package com.maogousoft.wuliu.common.json;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.processors.JsonValueProcessor;

import com.jfinal.plugin.activerecord.Model;


/**
 * @author yangfan(kenny0x00@gmail.com) 2013-3-30 上午12:08:19
 */
public class Result {

	private Map<String, Object> map = new LinkedHashMap<String, Object>();

	public Result() {
		map.put("status", ResultCode.SUCCESS);
	}

	public static Result fail() {
		Result result = new Result();
		return result.data("status", ResultCode.ERROR);
	}

	public static Result fail(String message) {
		Result result = new Result();
		result.data("status", ResultCode.ERROR);
		result.msg(message);
		return result;
	}

	public Result msg(String message) {
		return this.data("message", message);
	}

	public static Result success() {
		Result result = new Result();
		return result.data("status", ResultCode.SUCCESS);
	}

//	public Result data(Object data){
//		this.map.put("data", data) ;
//		return this ;
//	}

	@SuppressWarnings("rawtypes")
	public Result data(String key, Object data){
		if(data instanceof Model) {
			Model model = (Model)data;
			LinkedHashMap<String, Object> mydata = new LinkedHashMap<String, Object>();
			String[] columnNames = model.getAttrNames();
			Arrays.sort(columnNames);
			for (String columnName : columnNames) {
				Object obj = model.get(columnName);
				if(obj != null) {
					mydata.put(columnName, obj);
				}else {
					mydata.put(columnName, "");
//					mydata.put(columnName, JSONNull.getInstance());
				}
			}
			data = mydata;
		}
		this.map.put(key, data) ;
		return this;
	}

	public Object getData() {
		return this.map;
	}

	public String toJsonString() {
		JSONObject json = toJson();
		return json.toString();
	}

	public JSONObject toJson() {
		JSONObject json = JsonUtil.toJSONObject(this.map);
		return json;
	}

	public boolean isSuccess() {
		Integer code = (Integer) this.map.get("status");
		return  code != null && code.intValue() == ResultCode.SUCCESS;
	}

	public static void main(String[] args) {
		String str = Result.fail().toJsonString();
		System.out.println(str);
		System.out.println(Result.fail().isSuccess());
	}

	public static Result success(String message) {
		Result result = new Result();
		result.data("status", ResultCode.SUCCESS);
		result.msg(message);
		return result;
	}

}
