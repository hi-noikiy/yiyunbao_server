package com.maogousoft.wuliu.domain;


/**
 * @author yangfan(kenny0x00@gmail.com) 2013-4-6 下午9:11:42
 */
public class OrderVie extends BaseModel<OrderVie> {

	private static final long serialVersionUID = 1L;

	public static OrderVie dao = new OrderVie();

	/**
	 * 判断是否已经抢单
	 * @param order_id
	 * @param driver_id
	 * @return
	 */
	public boolean hasVieByDriver(int order_id, int driver_id) {
		//0-竞标中，1-中标，2-退出竞标
//		String sql = "select id from logistics_order_vie where order_id=? and driver_id=? and (status=0 or status=1)";
		//edit by liupengfei 只要抢过单就算。
		String sql = "select id from logistics_order_vie where order_id=? and driver_id=? ";
		OrderVie ov = findFirst(sql, order_id, driver_id);
		return ov != null;
	}

	public OrderVie getVieByOrderAndDriver(int order_id, int driver_id) {
		String sql = "select * from logistics_order_vie where order_id=? and driver_id=?";
		OrderVie ov = findFirst(sql, order_id, driver_id);
		return ov;
	}

	/**
	 * 判断是否有人抢单
	 * @param orderId
	 * @return
	 */
	public boolean hasVieByOrderId(int order_id) {
		//0-竞标中，1-中标，2-退出竞标
		String sql = "select id from logistics_order_vie where order_id=? and (status=0 or status=1)";
		OrderVie ov = findFirst(sql, order_id);
		return ov != null;
	}
}
