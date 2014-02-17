package com.maogousoft.wuliu.common.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-5-12 下午6:41:02
 */
public class IPUtil {

	/**
	 * 获取IP地址
	 * @param request
	 * @return
	 */
	public static String getIP(HttpServletRequest request){
		String remoteip = request.getHeader("x-forwarded-for");
		if(StringUtils.isNotBlank(remoteip)){
			if(remoteip.indexOf(",")!=-1){
				//return the first ip
				return remoteip.split(",")[0];
			}else{
				return remoteip;
			}
		}else{
			return request.getRemoteAddr();
		}
	}
}
