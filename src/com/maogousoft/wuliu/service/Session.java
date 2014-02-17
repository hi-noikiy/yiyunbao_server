package com.maogousoft.wuliu.service;

import java.util.Date;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-3-30 上午12:40:43
 */
public interface Session {

	public int USER_TYPE_USER = 0;

	public int USER_TYPE_DRIVER = 1;

	public String KEY_USER_INFO = "user";

	public String SESSION_USER_ID = "SESSION_USER_ID";

	public String SESSION_PHONE = "SESSION_PHONE";

	public Date getCreateTime();

	public long getLastAccessTime();

	public String getToken();

	public <T> void setAttr(String name, T value);

	public <T> T getAttr(String name);

	public int getUserType();

	public String getPhone();

	public int getUserId();
}
