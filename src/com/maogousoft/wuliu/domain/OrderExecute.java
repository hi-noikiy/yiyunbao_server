package com.maogousoft.wuliu.domain;

/**
 * 
 * @description 订单执行状态 
 * @author shevliu
 * @email shevliu@gmail.com
 * May 11, 2013 3:01:56 PM
 */
public class OrderExecute extends BaseModel<OrderExecute>{
	
	private static final long serialVersionUID = -181785184589752397L;
	
	/**
	 * 到达装货地点
	 */
	public static final int STATUS_REACH_START = 1;
	
	/**
	 * 启程
	 */
	public static final int STATUS_START = 2;
	
	/**
	 * 在途
	 */
	public static final int STATUS_ON_THE_WAY = 3;
	
	/**
	 * 到达目的地
	 */
	public static final int STATUS_REACH_END = 4;
	
	/**
	 * 卸货
	 */
	public static final int STATUS_DISBURDEN = 5;
	
	/**
	 * 回单密码完成
	 */
	public static final int STATUS_RECEIPT = 6;
	
	public static OrderExecute dao = new OrderExecute();
}
