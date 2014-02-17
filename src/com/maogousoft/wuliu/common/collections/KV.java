package com.maogousoft.wuliu.common.collections;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KV主要用于表示键值对,实现了Map接口<b>KV implements Map&lt;String, Object&gt;</b> <br/>
 * usage:
 *
 * <pre>
 * KV kv = new KV(&quot;usernmae&quot;, &quot;aaa&quot;);
 * kv.add(&quot;age&quot;, 18);
 * kv.add(&quot;key3&quot;, 222.5, &quot;createTime&quot;, new DateTime(2011, 11, 11, 11, 11));
 * System.out.println(kv);
 * // {usernmae=aaa, age=18, key3=222.5, createTime=2011-11-11T11:11:00.000+08:00}
 * </pre>
 *
 * @author yangfan 2011-12-2
 */
public class KV extends QMap<String, Object> implements Map<String, Object> {

	private static final Logger log = LoggerFactory.getLogger(KV.class);

	private static final long serialVersionUID = 1L;

	public KV() {
		super();
	}

	public KV(String key, Object value) {
		super(key, value);
	}

	public KV(Object... keyValuePairs) {
		super(keyValuePairs);
	}

	@Override
	public KV add(String key, Object value) {
		super.add(key, value);
		return this;
	}

	@Override
	public KV add(Object... args) {
		super.add(args);
		return this;
	}

	public static void main(String[] args) {
		 KV kv = new KV("usernmae","aaa");
		 kv.add("age", 18); //NOSONAR
		 kv.add("key3",222.5,"createTime", "2012-12-22"); //NOSONAR
		 log.debug("kv={}", kv);
	}
}
