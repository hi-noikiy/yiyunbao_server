package com.maogousoft.wuliu.common.json;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yangfan
 */
public class JsonListParam implements ListParam {
	private static Logger log = LoggerFactory.getLogger(JsonListParam.class);

	private JSONArray json;

	public JsonListParam(JSONArray jsonArray) {
		this.json = jsonArray;
	}

	@Override
	public int size() {
		return json.size();
	}

	@Override
	public boolean isEmpty() {
		return json.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Object> iterator() {
		return new TransformIterator(json.iterator(), new Transformer() {
			@Override
			public Object transform(Object input) {
				return toObject(input);
			}
		});
	}

	@Override
	public Object getObject(Integer index) {
		Object obj = json.get(index);
		return toObject(obj);
	}

	protected Object toObject(Object obj) {
		if (obj instanceof JSONObject) {
			return new JsonParam((JSONObject) obj);
		}
		if (obj instanceof JSONArray) {
			return new JsonListParam((JSONArray) obj);
		}
		return obj;
	}

	@Override
	public boolean isParam(Integer key) {
		Object obj = json.get(key);
		if (obj instanceof JSONObject) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isListParam(Integer key) {
		Object obj = json.get(key);
		if (obj instanceof JSONArray) {
			return true;
		}
		return false;
	}

	@Override
	public ListParam getListParam(Integer key) {
		JSONArray array = json.getJSONArray(key);
		return new JsonListParam(array);
	}

	@Override
	public Param getParam(Integer index) {
		JSONObject obj = json.getJSONObject(index);
		return new JsonParam(obj);
	}

	@Override
	public boolean getBooleanValue(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toBooleanValue(value, false);
	}

	@Override
	public boolean getBooleanValue(Integer key, boolean defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toBooleanValue(value, defaultValue);
	}

	@Override
	public Integer getInteger(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toInteger(value);
	}

	@Override
	public int getIntValue(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toIntValue(value, 0);
	}

	@Override
	public int getIntValue(Integer key, int defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toIntValue(value, defaultValue);
	}

	@Override
	public Long getLong(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toLong(value);
	}

	@Override
	public long getLongValue(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toLongValue(value, 0);
	}

	@Override
	public long getLongValue(Integer key, long defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toLongValue(value, defaultValue);
	}

	@Override
	public Float getFloat(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toFloat(value);
	}

	@Override
	public float getFloatValue(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toFloatValue(value, 0);
	}

	@Override
	public float getFloatValue(Integer key, float defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toFloatValue(value, 0);
	}

	@Override
	public Double getDouble(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toDouble(value);
	}

	@Override
	public double getDoubleValue(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toDoubleValue(value, 0);
	}

	@Override
	public double getDoubleValue(Integer key, double defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toDoubleValue(value, defaultValue);
	}

	@Override
	public String getString(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toString(value, null);
	}

	@Override
	public String getString(Integer key, String defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toString(value, defaultValue);
	}

	@Override
	public Date getDate(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toDate(value, null);
	}

	@Override
	public Date getDate(Integer key, Date defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toDate(value, defaultValue);
	}

	@Override
	public java.sql.Date getSqlDate(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toSqlDate(value, null);
	}

	@Override
	public java.sql.Date getSqlDate(Integer key, java.sql.Date defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toSqlDate(value, defaultValue);
	}

	@Override
	public Timestamp getTimestamp(Integer key) {
		Object value = json.get(key);
		return JsonValueUtil.toTimestamp(value, null);
	}

	@Override
	public Timestamp getTimestamp(Integer key, Timestamp defaultValue) {
		Object value = json.get(key);
		return JsonValueUtil.toTimestamp(value, defaultValue);
	}

	@Override
	public int getRequiredInt(Integer key) {
		Integer value = getInteger(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public long getRequiredLong(Integer key) {
		Long value = getLong(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public float getRequiredFloat(Integer key) {
		Float value = getFloat(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public double getRequiredDouble(Integer key) {
		Double value = getDouble(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public String getRequiredString(Integer key) {
		String value = getString(key);
		JsonValueUtil.verifyIsNull(key, value);
		return value;
	}

	@Override
	public int[] getIntegerArray(Integer key) {
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
	public long[] getLongArray(Integer key) {
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
	public String[] getStringArray(Integer key) {
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

	@Override
	public byte[] getBytesFromBase64(Integer key) {
		String value = getString(key);
		return JsonValueUtil.toBytesFromBase64(value);
	}

	@Override
	public String toString() {
		return this.json.toString();
	}
}
