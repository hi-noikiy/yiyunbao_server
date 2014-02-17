package com.maogousoft.wuliu;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.maogousoft.wuliu.common.ext.jfinal.RequestHandler;
import com.maogousoft.wuliu.common.syslog.ServiceLog;
import com.maogousoft.wuliu.controller.IndexController;
import com.maogousoft.wuliu.controller.ServiceController;
import com.maogousoft.wuliu.domain.Area;
import com.maogousoft.wuliu.domain.Business;
import com.maogousoft.wuliu.domain.Coupon;
import com.maogousoft.wuliu.domain.Dict;
import com.maogousoft.wuliu.domain.Driver;
import com.maogousoft.wuliu.domain.DriverReply;
import com.maogousoft.wuliu.domain.Msg;
import com.maogousoft.wuliu.domain.Order;
import com.maogousoft.wuliu.domain.OrderExecute;
import com.maogousoft.wuliu.domain.OrderLocation;
import com.maogousoft.wuliu.domain.OrderLog;
import com.maogousoft.wuliu.domain.OrderVie;
import com.maogousoft.wuliu.domain.Pay;
import com.maogousoft.wuliu.domain.User;
import com.maogousoft.wuliu.domain.UserReply;
import com.maogousoft.wuliu.domain.Vender;
import com.maogousoft.wuliu.domain.VenderReply;
import com.maogousoft.wuliu.interceptor.ExceptionInterceptor;
import com.maogousoft.wuliu.service.UserSessionManager;
import com.maogousoft.wuliu.service.VcodeManagerPlugin;

/**
 * 基础配置信息
 */
public class BaseConfig extends JFinalConfig {

	private static BaseConfig me;

	/**
	 *
	 */
	@Override
	public void configConstant(Constants me) {
		loadPropertyFile("config.properties");
//		me.setFreeMarkerViewExtension(".ftl");
		me.setBaseViewPath("/WEB-INF/views");
		boolean devMode = getPropertyToBoolean("jfinal.devMode", false);
		me.setDevMode(devMode);
//		me.setUrlParaSeparator("_");
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new RequestHandler());
	}

	@Override
	public void afterJFinalStart() {
		super.afterJFinalStart();
		me = this;
	}

	public static BaseConfig me() {
		return me;
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new ExceptionInterceptor());
		me.add(new SessionInViewInterceptor());
	}

	@Override
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		C3p0Plugin c3p0Plugin = new C3p0Plugin(getProperty("jdbc.url"), getProperty("jdbc.user"), getProperty("jdbc.password"), getProperty("jdbc.driverClass"));
		me.add(c3p0Plugin);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		boolean showSql = getPropertyToBoolean("jdbc.showSql", false);
		arp.setShowSql(showSql);
		me.add(arp);

		arp.addMapping("logistics_user", User.class);
		arp.addMapping("log_service_log", ServiceLog.class);
		arp.addMapping("logistics_driver", Driver.class);
		arp.addMapping("logistics_driver_reply", DriverReply.class);
		arp.addMapping("logistics_order", Order.class);
		arp.addMapping("logistics_order_vie", OrderVie.class);
		arp.addMapping("logistics_area", Area.class);
		arp.addMapping("logistics_dict", Dict.class);
		arp.addMapping("logistics_order_location", OrderLocation.class);
		arp.addMapping("logistics_order_status", OrderExecute.class);
		arp.addMapping("logistics_user_reply", UserReply.class);
		arp.addMapping("log_order", OrderLog.class);
		arp.addMapping("logistics_pay", Pay.class);
		arp.addMapping("logistics_sys_msg", Msg.class);
		arp.addMapping("logistics_business", Business.class);
		arp.addMapping("logistics_vender", Vender.class);
		arp.addMapping("logistics_vender_reply", VenderReply.class);
		arp.addMapping("logistics_coupon", Coupon.class);

		//Session管理
		final int sessionTimeout = getPropertyToInt("session.timeout", 30);
		UserSessionManager sessionManager = new UserSessionManager(sessionTimeout);
		me.add(sessionManager);


		//手机验证码管理
		final int vcodeTimeout = getPropertyToInt("vcode.timeout", 30);
		VcodeManagerPlugin vcodeManagerPlugin = new VcodeManagerPlugin(vcodeTimeout);
		me.add(vcodeManagerPlugin);

		//配置缓存
		EhCachePlugin cachePlugin = new EhCachePlugin();
		me.add(cachePlugin);
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/" , IndexController.class);
		me.add("/user" , ServiceController.class);
		me.add("/driver" , ServiceController.class);
		me.add("/common" , ServiceController.class);
	}

}
