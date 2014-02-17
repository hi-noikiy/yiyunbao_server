/**
 * @filename Coupon.java
 */
package com.maogousoft.wuliu.domain;

/**
 * @description 充值卡
 * @author shevliu
 * @email shevliu@gmail.com
 * Jun 12, 2013 11:26:49 PM
 */
public class Coupon extends BaseModel<Coupon>{

	private static final long serialVersionUID = -6316282338157958254L;
	
	/**
	 * 新创建，未发送
	 */
	public static final int STATUS_CREATED = 0 ;
	
	/**
	 * 已发送
	 */
	public static final int STATUS_SENDED = 1 ;
	
	/**
	 * 已消费
	 */
	public static final int STATUS_USED = 2 ;
	
	public static Coupon dao = new Coupon();
}
