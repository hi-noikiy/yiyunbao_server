package com.maogousoft.wuliu.common.syslog;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Model;
import com.maogousoft.wuliu.common.collections.KV;
import com.maogousoft.wuliu.common.json.JsonUtil;
import com.maogousoft.wuliu.common.util.StrUtil;
import com.maogousoft.wuliu.controller.ServiceContext;
import com.maogousoft.wuliu.service.Session;
import com.maogousoft.wuliu.service.UserSessionManager;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-4-5 下午4:26:57
 */
public class ServiceLog extends Model<ServiceLog> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ServiceLog.class);

	private static final int LEVEL_INFO = 1;
	private static final int LEVEL_WARN = 2;
	private static final int LEVEL_ERROR = 3;

	private KV data = new KV();

	public static ServiceLog info(String msg, Object... args) {
		return doLog(LEVEL_INFO, msg, args);
	}
	public static ServiceLog warn(String msg, Object... args) {
		return doLog(LEVEL_WARN, msg, args);
	}
	public static ServiceLog error(String msg, Object... args) {
		return doLog(LEVEL_ERROR, msg, args);
	}

	protected static ServiceLog doLog(int level, String msg, Object... args) {
		ServiceLog log = new ServiceLog();
		log.set("log_level",level);
		String message = StrUtil.format(msg, args);
		log.set("message", message);
		return log;
	}

	public ServiceLog data(String name, Object value) {
		this.data.add(name, value);
		return this;
	}

	public ServiceLog data(Object... params) {
		this.data.add(params);
		return this;
	}

//	public ServiceLog withToken(String token) {
//		this.set("token", token);
//		return this;
//	}
//
//	public ServiceLog withAction(String action) {
//		this.set("action", action);
//		return this;
//	}

	@Override
	public boolean save() {
		if(!this.data.isEmpty()) {
			String p = JsonUtil.toJSONString(this.data);
			this.set("log_data", p);
		}

		ServiceContext ctx = ServiceContext.getServiceContext();
		if(ctx != null) {
			this.set("log_action", ctx.getAction());
			this.set("log_token", ctx.getToken());
//			this.set("log_json", ctx.getJsonStr()); //暂不记录，有图片上传的base64，很大
			Session session = UserSessionManager.me().getSession(ctx.getToken());
			if(session != null) {
				String phone = session.getPhone();
				this.set("log_phone", phone);
				Integer userType = session.getUserType();
				if(userType != null) {
					this.set("user_type", userType);
				}
			}
		}

		this.set("log_time", new Date());

		return super.save();
	}
}
