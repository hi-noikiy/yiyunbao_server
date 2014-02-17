package com.maogousoft.wuliu.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.IPlugin;

/**
 *

		ServiceManager serviceManager = new ServiceManager();
		serviceManager.add(new UserService());
		serviceManager.add(new DriverService());
		serviceManager.add(new CommonService());
		me.add(serviceManager);
		@deprecated 未完成
 * @author yangfan(kenny0x00@gmail.com) 2013-4-14 下午5:14:00
 */
public class ServiceManager implements IPlugin {

	private List<Object> services = new ArrayList<Object>();

	@Override
	public boolean start() {
		for (Object service : services) {

		}
		return false;
	}

	@Override
	public boolean stop() {
		return false;
	}

	public void add(Object service) {
		this.services.add(service);
	}

}
