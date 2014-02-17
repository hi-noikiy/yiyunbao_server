package com.maogousoft.wuliu.common.json;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yangfan
 */
public class JsonParam implements Param {

	private static final Logger log = LoggerFactory.getLogger(JsonParam.class);

	private JSONObject json;

	public JsonParam(JSONObject json) {
		this.json = json;
	}

	@Override
	public boolean isParam(String key) {
		Object obj = json.get(key);
		if (obj instanceof JSONObject) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isListParam(String key) {
		Object obj = json.get(key);
		if (obj instanceof JSONArray) {
			return true;
		}
		return false;
	}

	@Override
	public Object getObject(String index) {
		Object obj = json.get(index);
		if (obj instanceof JSONObject) {
			return new JsonParam((JSONObject) obj);
		}
		if (obj instanceof JSONArray) {
			return new JsonListParam((JSONArray) obj);
		}
		if (obj instanceof JSONNull) {
			return null;
		}
		return obj;
	}

	@Override
	public ListParam getListParam(String key) {
		JSONArray array = json.getJSONArray(key);
		return new JsonListParam(array);
	}

	@Override
	public Param getParam(String index) {
		JSONObject obj = json.getJSONObject(index);
		return new JsonParam(obj);
	}

	@Override
	public boolean getBooleanValue(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toBooleanValue(value, false);
	}

	@Override
	public boolean getBooleanValue(String key, boolean defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toBooleanValue(value, defaultValue);
	}

	@Override
	public Integer getInteger(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toInteger(value);
	}

	@Override
	public int getIntValue(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toIntValue(value, 0);
	}

	@Override
	public int getIntValue(String key, int defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toIntValue(value, defaultValue);
	}

	@Override
	public Long getLong(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toLong(value);
	}

	@Override
	public long getLongValue(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toLongValue(value, 0);
	}

	@Override
	public long getLongValue(String key, long defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toLongValue(value, defaultValue);
	}

	@Override
	public Float getFloat(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toFloat(value);
	}

	@Override
	public float getFloatValue(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toFloatValue(value, 0);
	}

	@Override
	public float getFloatValue(String key, float defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toFloatValue(value, defaultValue);
	}

	@Override
	public Double getDouble(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toDouble(value);
	}

	@Override
	public double getDoubleValue(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toDoubleValue(value, 0);
	}

	@Override
	public double getDoubleValue(String key, double defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toDoubleValue(value, defaultValue);
	}

	@Override
	public String getString(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toString(value, null);
	}

	@Override
	public String getString(String key, String defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toString(value, defaultValue);
	}

	@Override
	public Date getDate(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toDate(value, null);
	}

	@Override
	public Date getDate(String key, Date defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toDate(value, defaultValue);
	}

	@Override
	public java.sql.Date getSqlDate(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toSqlDate(value, null);
	}

	@Override
	public java.sql.Date getSqlDate(String key, java.sql.Date defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toSqlDate(value, defaultValue);
	}

	@Override
	public Timestamp getTimestamp(String key) {
		Object value = json.get(key);
		return JsonValueUtil.toTimestamp(value, null);
	}

	@Override
	public Timestamp getTimestamp(String key, Timestamp defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toTimestamp(value, defaultValue);
	}

	@Override
	public int getRequiredInt(String key) {
		Integer value = getInteger(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public long getRequiredLong(String key) {
		Long value = getLong(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public float getRequiredFloat(String key) {
		Float value = getFloat(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public double getRequiredDouble(String key) {
		Double value = getDouble(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public String getRequiredString(String key) {
		String value = getString(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public int[] getIntegerArray(String key) {
		if (json.get(key) == null) {
			return new int[0];
		}
		try {
			JSONArray arr = json.getJSONArray(key);
			int size = arr.size();
			int[] result = new int[size];
			for (int i = 0; i < size; i++) {
				Object obj = arr.get(i);
				Integer value = JsonValueUtil.toInteger(obj);
				if (value == null) {
					throw new Exception("Can't convert the value to int,value=" + obj + ",index=" + i);
				}
				result[i] = value;
			}
			return result;
		} catch (Exception ex) {
			log.error("Can't get int array from json,key=" + key + ",json=" + json, ex);
			return new int[0];
		}
	}

	@Override
	public long[] getLongArray(String key) {
		if (json.get(key) == null) {
			return new long[0];
		}
		try {
			JSONArray arr = json.getJSONArray(key);
			int size = arr.size();
			long[] result = new long[size];
			for (int i = 0; i < size; i++) {
				Object obj = arr.get(i);
				Long value = JsonValueUtil.toLong(obj);
				if (value == null) {
					throw new Exception("Can't convert the value to long,value=" + obj + ",index=" + i);
				}
				result[i] = value;
			}
			return result;
		} catch (Exception ex) {
			log.error("Can't get long array from json,key=" + key + ",json=" + json, ex);
			return new long[0];
		}
	}

	@Override
	public String[] getStringArray(String key) {
		if (json.get(key) == null) {
			return new String[0];
		}
		try {
			JSONArray arr = json.getJSONArray(key);
			int size = arr.size();
			String[] result = new String[size];
			for (int i = 0; i < size; i++) {
				Object obj = arr.get(i);
				String value = JsonValueUtil.toString(obj, null);
				if (value == null) {
					throw new Exception("Can't convert the value to String,value=" + obj + ",index=" + i);
				}
				result[i] = value;
			}
			return result;
		} catch (Exception ex) {
			log.error("Can't get String array from json,key=" + key + ",json=" + json, ex);
			return new String[0];
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<String> keys() {
		return json.keys();
	}

	@Override
	public byte[] getBytesFromBase64(String key) {
		String value = getString(key);
		return JsonValueUtil.toBytesFromBase64(value);
	}

	@Override
	public String toString() {
		return this.json.toString();
	}

}
