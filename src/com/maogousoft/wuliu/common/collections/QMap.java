package com.maogousoft.wuliu.common.collections;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于快速创建一个Map<br/>usage:
 * <p>
 * <pre>
 * //直接在构造函数中使用
 * QMap&lt;String, Object&gt; map = new QMap&lt;String, Object&gt;(&quot;username&quot;, &quot;user1&quot;, &quot;age&quot;, 18);
 *
 * //逐个添加
 * map.add(&quot;birthday&quot;, "2012-12-22");
 *
 * //一次性添加多个
 * map.add(&quot;key1&quot;, &quot;value1&quot;, &quot;key2&quot;, &quot;value2&quot;);
 *
 * //链式调用添加
 * map.add(&quot;key3&quot;, &quot;value3&quot;)
 *    .add(&quot;key4&quot;, &quot;value4&quot;);
 *
 * //打印到控制台
 * System.out.println(map);
 * <b>输出结果：</b>
 * <blockquote>{username=aaa, age=18, birthday=2012-12-22, userType=1, key1=value1, key2=value2, key3=value3, key4=value4}</blockquote>
 *
 * //以url里的querystring形式取出
 * String params = map.getAsQueryString(&quot;utf-8&quot;);
 * System.out.println(params);
 * <b>输出结果：</b>
 * <blockquote>username=aaa&age=18&birthday=2012-12-22&userType=1&key1=value1&key2=value2&key3=value3&key4=value4</blockquote>
 *
 * //添加到url末尾,自动识别是否有问号
 * String baseUrl = "http://localhost:8080/admin/list.do";
 * String url = map.appendToUrl(baseUrl);
 * System.out.println(url);
 * <b>输出结果：</b>
 * <blockquote>http://localhost:8080/admin/list.do?username=aaa&age=18&birthday=2012-12-22&userType=1&key1=value1&key2=value2&key3=value3&key4=value4</blockquote>
 * </pre>
 * </p>
 * @author yangfan 2011-12-2
 * @param <K>
 * @param <V>
 */
public class QMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(QMap.class);

	public QMap() {
		super();
	}

	public QMap(Map<K, V> map) {
		super(map);
	}

	public QMap(Object... args) {
		super(args.length / 2 + 1);
		this.add(args);
	}

	@SuppressWarnings("unchecked")
	public QMap<K, V> add(Object... args) {
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException("The arguments count must be even, current arguments length is " + args.length);
		}
		for (int i = 0; i < args.length; i += 2) {
			this.put((K) args[i], (V) args[i + 1]);
		}
		return this;
	}

	public QMap<K, V> add(K key, V value) {
		this.put(key, value);
		return this;
	}

	public String getAsQueryString(String encoding) {
		StringBuilder sb = new StringBuilder(50);
		for (Object key : this.keySet()) {
			Object value = this.get(key);
			if (value != null)
				appendValue(sb, String.valueOf(key), value, encoding);
		}
		String s = sb.toString();
		if (s.startsWith("&"))
			s = s.substring(s.indexOf('&') + 1);
		return s;
	}

	private void appendValue(StringBuilder sb, String key, Object value, String encoding) {
		String valueToUse = String.valueOf(value);
		try {
			valueToUse = URLEncoder.encode(valueToUse, encoding);
		} catch (UnsupportedEncodingException e) {
			log.warn("unsupported Encoding:" + encoding + ", cause: " + e.getMessage());
		}
		sb.append("&").append(key).append("=").append(valueToUse);
	}


	public String appendToUrl(String baseUrl) {
		return appendToUrl(baseUrl,"utf-8");
	}

	public String appendToUrl(String baseUrl, String encoding){
		String queryString = getAsQueryString(encoding);
		String urlToUse = (baseUrl == null)? "" : baseUrl;
		if(urlToUse.endsWith("?"))
			urlToUse += queryString;			// http://www.abc.com/test.html? //NOSONAR
		else if(urlToUse.indexOf("?") != -1)
			urlToUse += "&" + queryString;		// http://www.abc.com/test.html?a=1&b=2 //NOSONAR
		else
			urlToUse += "?" + queryString;		// http://www.abc.com/test.html //NOSONAR
		return urlToUse;
	}

	public static void main(String[] args) {
		//直接在构造函数中使用
		QMap<String, Object> map = new QMap<String, Object>("username", "aaa", "age", 18);

		//逐个添加
		map.add("birthday", "2012-12-22");
		map.add("userType", 1);

		//一次性添加多个
		map.add("key1", "value1", "key2", "value2");

		//链式调用
		map.add("key3", "value3")
			.add("key4", "value4");

		//输出到控制台
		log.debug("map={}",map);

		//以url参数的形式输出
		String params = map.getAsQueryString("utf-8");
		log.debug(params);

		//添加到url末尾,自动识别是否有问号
		String baseUrl = "http://localhost:8080/admin/list.do";
		String url = map.appendToUrl(baseUrl);
		log.debug(url);
	}

}
