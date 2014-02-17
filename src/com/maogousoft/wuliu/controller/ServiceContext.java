package com.maogousoft.wuliu.controller;

import javax.servlet.http.HttpServletRequest;

import com.maogousoft.wuliu.WuliuConstants;
import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.domain.Driver;
import com.maogousoft.wuliu.service.Session;
import com.maogousoft.wuliu.service.UserSessionManager;

/**
 * 获取当前的context,包括当前的action、token等
 * @author yangfan(kenny0x00@gmail.com) 2013-4-5 下午4:39:59
 */
public class ServiceContext {

	private final static ThreadLocal<ServiceContext>  local = new ThreadLocal<ServiceContext>();

	private String action;
	private String jsonStr;
	private String token;
	private HttpServletRequest request;

	public static ServiceContext getServiceContext() {
		return local.get();
	}

	public static void setServiceContext(ServiceContext context) {
		local.set(context);
	}

	public Session getCurrentSession() {
		Session session = UserSessionManager.me().getSession(getToken());
		return session;
	}

	public String getPhone() {
		assertLogin();
		Session session = getCurrentSession();
//		if(session == null) {
//			return null;
//		}
		return session.getPhone();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getJsonStr() {
		return jsonStr;
	}
	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getUserId() {
		assertLogin();
		return this.getCurrentSession().getUserId();
	}

	/**
	 * 断言用户已经登录
	 */
	public static void assertLogin() {
		ServiceContext ctx = ServiceContext.getServiceContext();
		if(ctx.getToken() == null) {
			throw new BusinessException("缺少token参数,请登录以获取该数据.");
		}
		if(ctx.getCurrentSession() == null) {
			throw new BusinessException("无效的接口请求(token已过期?),token=" + ctx.getToken() + ",action=" + ctx.getAction());
		}
	}

	/**
	 * 断言用户为已经登录的货主
	 */
	public static void assertLoginAsUser() {
		assertLogin();
		ServiceContext ctx = ServiceContext.getServiceContext();
		Session session = ctx.getCurrentSession();
		if(session.getUserType() != Session.USER_TYPE_USER) {
			throw new BusinessException("用户" + session.getPhone() + "不是有效的货主");
		}
	}

	/**
	 * 断言用户为已经登录的货主
	 */
	public static void assertLoginAsDriver() {
		assertLogin();
		ServiceContext ctx = ServiceContext.getServiceContext();
		Session session = ctx.getCurrentSession();
		if(session.getUserType() != Session.USER_TYPE_DRIVER) {
			throw new BusinessException("用户" + session.getPhone() + "不是有效的司机");
		}
		int driver_id = session.getUserId();
		Driver driver = Driver.dao.findByIdAndCache(WuliuConstants.CACHE_DRIVER, driver_id, driver_id);
		if(driver == null) {
			throw new BusinessException("无效的司机编号:" + driver_id);
		}
		// XXX 20130514 王胜韬电话要求更改
//		//-1已删除, 0 待审核 ,1-审核通过 ,2-审核不通过
//		if(driver.getInt("status") != Driver.STATUS_VALID) {
//			throw new BusinessException("司机" + ctx.getPhone() + "未通过审核.");
//		}
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}
