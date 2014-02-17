/**
 * @filename Order.java
 */
package com.maogousoft.wuliu.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.controller.ServiceContext;

/**
 * @description 订单
 * @author shevliu
 * @email shevliu@gmail.com
 * Mar 18, 2013 9:50:56 PM
 */
public class Order extends BaseModel<Order>{

	private static final long serialVersionUID = 1L;

	/**
	 * 已删除
	 */
	public static int STATUS_DELETED = -1 ;

	/**
	 * 已创建，待审核
	 */
	public static int STATUS_CREATED = 0 ;

	/**
	 * 审核通过，待抢单
	 */
	public static int STATUS_PASS = 1 ;

	/**
	 * 审核未通过
	 */
	public static int STATUS_REJECT = 2 ;

	/**
	 * 已中标，订单执行中
	 */
	public static int STATUS_DEAL = 3;

	/**
	 * 未达成条件，已正常取消
	 */
	public static int STATUS_CANCEL = 4;

	/**
	 * 订单已完成
	 */
	public static int STATUS_FINISH = 99;

	/**
	 * 订单已过期
	 */
	public static int STATUS_EXPIRED = 98 ;

	public static final Order dao = new Order();

	/**
	 *
	 * @description 获取除删除以外的所有状态list
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Mar 18, 2013 10:02:06 PM
	 * @return
	 */
	public static List<Map<String , String>> getAllStatus(){
		List<Map<String , String>> list = new ArrayList<Map<String,String>>();
		list.add(createStatus(STATUS_CREATED, "已创建"));
		list.add(createStatus(STATUS_PASS, "审核通过"));
		list.add(createStatus(STATUS_REJECT, "审核未通过"));
		list.add(createStatus(STATUS_CANCEL, "已取消"));
		list.add(createStatus(STATUS_DEAL, "已中标，进行中"));
		list.add(createStatus(STATUS_FINISH, "订单已完成"));
		return list ;
	}

	/**
	 * 获取订单状态的描述
	 * @param status
	 * @return
	 */
	public static String getStatusDesc(int status){
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, "到达装货地点");
		map.put(2, "启程");
		map.put(3, "在途");
		map.put(4, "到达目的地");
		map.put(5, "卸货");
		map.put(6, "回单密码完成");
		return map.get(status);
	}

	private static Map<String , String> createStatus(int status , String text){
		Map<String , String> map = new HashMap<String , String>();
		map.put("status", status + "") ;
		map.put("text", text);
		return map ;
	}

	public Order loadOrder(int orderId) {
		Order order = findById(orderId);
		if(order == null) {
			throw new BusinessException("货单" + orderId + "不存在");
		}
		return order;
	}

	/**
	 * 调整抢单人数
	 * @param i
	 */
	public void adjustVieCount(int i) {
		int vie_driver_count = this.getInt("vie_driver_count");
		vie_driver_count += i;
		this.set("vie_driver_count", vie_driver_count);
		this.update();
	}

	/**
	 * 判断订单是否在特定状态
	 * @param expectedStatus
	 * @return
	 */
	public boolean isStauts(int expectedStatus) {
		final int orderStatus = this.getInt("status");
		return orderStatus == expectedStatus;
	}

	/**
	 * 判断订单的货主拥有者
	 * @param user_id
	 * @return
	 */
	public boolean isOwner(int user_id) {
		final int orderUserId = this.getInt("user_id");
		if(orderUserId == user_id) {
			return true;
		}
		return false;
	}

	/**
	 * 判断货单的司机
	 * @param driver_id
	 * @return
	 */
	public boolean isDriver(int driver_id) {
		final int order_driver_id = this.getInt("driver_id");
		if(order_driver_id == driver_id) {
			return true;
		}
		return false;
	}

	/**
	 * 判断订单是否已过期
	 * @return
	 */
	public boolean isInValidateTime() {
		Date validate_time = this.getTimestamp("validate_time");
		if(validate_time == null) {
			throw new BusinessException("订单状态不正确,订单有效期validate_time为null.");
		}
		if(validate_time.getTime() > System.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取信息费，每笔为运费的3%，最高不超过200
	 * @return
	 */
	public double getFee() {
		double price = this.asDoubleValue("price",0);
		return Math.min(price * 0.03, 200);
	}
}
