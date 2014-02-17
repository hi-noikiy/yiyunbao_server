package com.maogousoft.wuliu.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.jfinal.plugin.IPlugin;

/**
 * 手机验证码管理器
 *
 * @author yangfan(kenny0x00@gmail.com) 2013-4-6 上午10:44:37
 */
public class VcodeManagerPlugin implements IPlugin {

	private static final Logger log = LoggerFactory.getLogger(VcodeManagerPlugin.class);

	private static VcodeManagerPlugin me;

	private ScheduledExecutorService scheduledExecutorService;

	private ConcurrentHashMap<String, Vcode> vcodes = new ConcurrentHashMap<String, Vcode>();

	private int vcodeTimeout;

	public VcodeManagerPlugin(int vcodeTimeout) {
		this.vcodeTimeout = vcodeTimeout;
	}

	public void putVcode(String phone, String vcode) {
		Vcode v = new Vcode(vcode, System.currentTimeMillis());
		vcodes.put(phone, v);
	}

	public boolean verifyVcode(String phone, String vcode) {
		Assert.hasText(vcode, "手机验证码不能为空.");
		Vcode v = this.vcodes.get(phone);
		if(v == null) {
			return false;
		}
		if(vcode.equals(v.getCode())) {
			return true;
		}
		return false;
	}

	public void removeVode(String phone) {
		this.vcodes.remove(phone);
	}

	/**
	 * 超时检测
	 * @param timeout
	 */
	protected void timeoutCheck(int timeout) {
		final long now = System.currentTimeMillis();
		for (String phone : vcodes.keySet()) {
			Vcode vcode = vcodes.get(phone);
			long createTime = vcode.getCreateTime();
			if ((now - createTime) > timeout*1000) {
				vcodes.remove(phone);
			}
		}
	}

	@Override
	public boolean start() {
		this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				timeoutCheck(vcodeTimeout);
			}
		}, 1, vcodeTimeout / 2, TimeUnit.SECONDS);
		log.debug("手机验证码超时检测已启动,超时时间:" + vcodeTimeout + "s");
		me = this;
		return true;
	}

	@Override
	public boolean stop() {
		scheduledExecutorService.shutdownNow();
		me = null;
		return true;
	}

	public static VcodeManagerPlugin me() {
		return me;
	}

	private static class Vcode {
		private final String code;
		private final long createTime;

		public Vcode(String code, long createTime) {
			this.code = code;
			this.createTime = createTime;
		}

		public String getCode() {
			return code;
		}

		public long getCreateTime() {
			return createTime;
		}

		@Override
		public String toString() {
			return "Vcode [code=" + code + ", createTime=" + createTime + "]";
		}
	}

}
