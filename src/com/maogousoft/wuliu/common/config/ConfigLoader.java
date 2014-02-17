package com.maogousoft.wuliu.common.config;

import java.io.FileNotFoundException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.maogousoft.wuliu.common.exception.SystemException;

/**
 * 用于载入配置文件，并且会自动刷新。如：
 * <pre>
 * XMLPropertiesConfiguration config = ConfigLoader.loadXMLPropertiesConfiguration("classpath:config.xml",60);
 * int sysuserid = config.getInt("sys.userid", 10001);
 * boolean devmode = config.getBoolean("dev.mode",false);
 * double xxx = config.getDouble("xxxx");
 * ...
 * </pre>
 * @author kenny(kenny0x00@gmail.com) 2011-5-5 下午04:25:26
 */
public class ConfigLoader {

	private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

	/**
	 * 载入指定路径的配置文件
	 * @param configFile 文件路径，如"classpath:config.xml"、"file:///c:/config.xml"<br>
	 * 该文件形如：
	 * <pre>
	 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
	 * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
	 * &lt;properties&gt;
	 *	&lt;comment&gt;配置文件说明&lt;/comment&gt;
	 *	&lt;entry key="jspath"&gt;/html/js&lt;/entry&gt;
	 *	&lt;entry key="csspath"&gt;/html/css&lt;/entry&gt;
	 *	&lt;entry key="sys.userid"&gt;100001&lt;/entry&gt;
	 *	&lt;entry key="dev.mode"&gt;true&lt;/entry&gt;
	 * &lt;/properties&gt;
	 * </pre>
	 * @param refreshSeconds 刷新配置文件的时间，单位是秒
	 * @return XMLPropertiesConfiguration
	 * @throws SystemException 当无法找到配置文件、或者读取错误时
	 */
	public static XMLPropertiesConfiguration loadXMLPropertiesConfiguration(String configFile, int refreshSeconds) throws SystemException
	{
		//最少是1秒钟刷新一次
		if(refreshSeconds < 1) {
			refreshSeconds = 1;
		}
		URL url;
		try {
			url = ResourceUtils.getURL(configFile);
		} catch (FileNotFoundException e) {
			log.error("缺少" + configFile,e);
			throw new SystemException("缺少配置文件:" + configFile);
		}
		XMLPropertiesConfiguration config;
		try {
			config = new XMLPropertiesConfiguration(url);
		} catch (ConfigurationException e) {
			log.error("配置文件错误, file: " + configFile + ", cause :" + e.getMessage(),e);
			throw new SystemException("配置文件错误, file: " + configFile + ", cause :" + e.getMessage());
		}
		FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
		long refreshDelay = 1000 * refreshSeconds;
		reloadingStrategy.setRefreshDelay(refreshDelay);
		config.setReloadingStrategy(reloadingStrategy);
		return config;
	}
}
