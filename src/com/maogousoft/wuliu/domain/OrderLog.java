package com.maogousoft.wuliu.domain;

import java.util.Date;

/**
 * 订单记录
 * @author yangfan(kenny0x00@gmail.com) 2013-5-19 下午4:27:34
 */
public class OrderLog extends BaseModel<OrderLog> {

	private static final long serialVersionUID = 1L;

	public final static OrderLog dao = new OrderLog();

	public static void logOrder(int orderId, String operator, String operation) {
		OrderLog orderLog = new OrderLog();
		orderLog.set("order_id", orderId);
		orderLog.set("operator", operator);
		orderLog.set("operation", operation);
		orderLog.set("create_time", new Date());
		orderLog.save();
	}
}
