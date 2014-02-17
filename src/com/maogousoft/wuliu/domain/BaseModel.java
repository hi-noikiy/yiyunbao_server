package com.maogousoft.wuliu.domain;

import java.math.BigDecimal;

import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.cache.ICache;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-4-8 下午10:51:21
 */
@SuppressWarnings("rawtypes")
public abstract class BaseModel<M extends BaseModel> extends Model<M>{

	private static final long serialVersionUID = 1L;

	/**
	 * 从缓存中获取对象,如果不存在,则根据SQL条件获取对象
	 * @param cacheName
	 * @param key
	 * @param sql
	 * @param paras
	 * @return
	 */
	public M findFirstByCache(String cacheName, Object key, String sql, Object... paras) {
		ICache cache = DbKit.getCache();
		M result = cache.get(cacheName, key);
		if (result == null) {
			result = findFirst(sql, paras);
			cache.put(cacheName, key, result);
		}
//		Area area = CacheKit.get(WuliuConstants.CACHE_AREA, start_province, new IDataLoader() {
//
//		@Override
//		public Object load() {
//			return Area.dao.findById(start_province);
//		}
//	});
		return result;
	}

	/**
	 * 根据ID获取对象（如果缓存中有该对象，则通过缓存；如果不存在，则通过数据库，并同时放入缓存）
	 * @param cacheName
	 * @param key
	 * @param id
	 * @return
	 */
	public M findByIdAndCache(String cacheName, Object key, Object id) {
		ICache cache = DbKit.getCache();
		M result = cache.get(cacheName, key);
		if (result == null) {
			result = findById(id);
			cache.put(cacheName, key, result);
		}
		return result;
	}

	/**
	 * 将数据作为Double返回(不会抛出异常)，如果无法获取到，则返回null
	 * @param attr
	 * @return
	 */
	public Double asDouble(String attr) {
		Object obj = this.get(attr);
		if(obj == null) {
			return null;
		}
		if (obj instanceof Double) {
			Double value = (Double) obj;
			return value;
		}
		if (obj instanceof BigDecimal) {
			BigDecimal value = (BigDecimal) obj;
			return value.doubleValue();
		}
		if (obj instanceof Number) {
			Number value = (Number) obj;
			return value.doubleValue();
		}
		if (obj instanceof String) {
			String value = (String) obj;
			try {
				return Double.valueOf(value);
			}catch(Exception ex) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 将属性作为double值返回,不会抛出异常.同时根据类型自动转换BigDecimal,避免BigDecimal和double的问题
	 * @param attr
	 * @param defaultValue
	 * @return
	 */
	public double asDoubleValue(String attr, double defaultValue) {
		Double d = asDouble(attr);
		if(d != null) {
			return d;
		}
		return defaultValue;
	}

}
