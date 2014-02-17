package com.maogousoft.wuliu.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.IPlugin;
import com.maogousoft.wuliu.common.util.UUIDUtil;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-3-30 上午12:38:11
 */
public class UserSessionManager implements IPlugin{

	private static final Logger log = LoggerFactory.getLogger(UserSessionManager.class);

	private static UserSessionManager instance;

	private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	private ScheduledExecutorService scheduledExecutorService;

	private int sessionTimeout;

	public UserSessionManager(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public static UserSessionManager me() {
		return instance;
	}

	public Session createSession(int userId, String phone, int userType) {
		String sessionId = UUIDUtil.getUUID();
		SessionImpl s = new SessionImpl(sessionId);
		s.setUserId(userId);
		s.setPhone(phone);
		s.setUserType(userType);
		s.updateLastAccessTime();
		this.sessions.put(sessionId, s);
		return s;
	}

	public Session getSession(String token) {
		if(StringUtils.isBlank(token)) {
			return null;
		}
		return this.sessions.get(token);
	}

	public void accessSession(String token) {
		SessionImpl session = (SessionImpl) this.getSession(token);
		if(session == null) {
			return;
		}
		session.updateLastAccessTime();
	}

	/**
	 * session超时检测
	 * @param timeout
	 */
	protected void timeoutCheck(int timeout) {
		final long now = System.currentTimeMillis();
		for (String token : sessions.keySet()) {
			Session session = getSession(token);
			long createTime = session.getLastAccessTime();
			if ((now - createTime) > timeout*60*1000) {
				removeSession(token);
			}
		}
	}

	public void removeSession(String token) {
		if(StringUtils.isBlank(token)) {
			return;
		}
		this.sessions.remove(token);
	}

	@Override
	public boolean start() {
		this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				timeoutCheck(sessionTimeout);
			}
		}, 1, sessionTimeout / 2, TimeUnit.SECONDS);
		log.debug("Session超时检测已启动,超时时间:" + sessionTimeout + "分钟");
		instance = this;
		return true;
	}

	@Override
	public boolean stop() {
		scheduledExecutorService.shutdownNow();
		instance = null;
		return true;
	}

}
