package com.maogousoft.wuliu.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-4-5 下午11:11:18
 */
public class SessionImpl implements Session{

	public static final String KEY_PHONE = "phone";

	public static final String KEY_USER_TYPE = "user_type";

	final private Date createTime;
	final private String token;
	final private Map<String, Object> attrs = new LinkedHashMap<String, Object>();
	private int userId;
	private String phone;
	private int userType;
	private long lastAccessedTime;

	public SessionImpl(String token) {
		this.token = token;
		this.createTime = new Date();
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	@Override
	public <T> void setAttr(String name, T value) {
		attrs.put(name, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttr(String name) {
		return (T) attrs.get(name);
	}

	@Override
	public long getLastAccessTime() {
		return this.lastAccessedTime;
	}

	public void updateLastAccessTime()
	{
		this.lastAccessedTime = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "SessionImpl [createTime=" + createTime + ", token=" + token + ", userId=" + userId + ", phone=" + phone + ", userType="
				+ userType + "]";
	}

}
