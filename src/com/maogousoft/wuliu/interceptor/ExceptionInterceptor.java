/**
 * @filename AuthInterceptor.java
 */
package com.maogousoft.wuliu.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;

/**
 * @description 异常拦截器
 * @author shevliu
 * @email shevliu@gmail.com
 * Jul 26, 2012 9:47:40 PM
 */
public class ExceptionInterceptor implements Interceptor{

	private Log log = LogFactory.getLog(ExceptionInterceptor.class) ;


	public void intercept(ActionInvocation ai) {
		try{
			ai.invoke();
		}catch(Exception re){
			log.error("拦截器处理异常:" , re) ;
			Controller c = ai.getController();
			c.setAttr("message", re.getMessage());
			c.setAttr("detail", re);
			log.debug(ai.getViewPath());
//			c.renderError500();
			c.render("/WEB-INF/views/error.html");
		}
	}

}
