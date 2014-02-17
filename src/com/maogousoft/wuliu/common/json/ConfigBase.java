package com.maogousoft.wuliu.common.json;
import java.sql.Timestamp;
import java.util.Date;

public interface ConfigBase<T>
{

	public Object getObject(T key);

//	public Boolean getBoolean(T key);

	public boolean getBooleanValue(T key);

	public boolean getBooleanValue(T key, boolean defaultValue);

	public Integer getInteger(T key);

	public int getIntValue(T key);

	public int getIntValue(T key, int defaultValue);

	public int getRequiredInt(T key);

	public Long getLong(T key);

	public long getLongValue(T key);

	public long getLongValue(T key, long defaultValue);

	public long getRequiredLong(T key);

	public Float getFloat(T key);

	public float getFloatValue(T key);

	public float getFloatValue(T key, float defaultValue);

	public float getRequiredFloat(T key);

	public Double getDouble(T key);

	public double getDoubleValue(T key);

	public double getDoubleValue(T key, double defaultValue);

	public double getRequiredDouble(T key);

	public String getString(T key);

	public String getString(T key, String defaultValue);

	public String getRequiredString(T key);

	public Date getDate(T key);

	public Date getDate(T key, Date defaultValue);

	public java.sql.Date getSqlDate(T key);

	public java.sql.Date getSqlDate(T key, java.sql.Date defaultValue);

	public Timestamp getTimestamp(T key);

	public Timestamp getTimestamp(T key, Timestamp defaultValue);

	public byte[] getBytesFromBase64(T key);
}
