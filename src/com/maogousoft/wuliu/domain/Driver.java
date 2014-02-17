/**
 * @filename Driver.java
 */
package com.maogousoft.wuliu.domain;

import com.jfinal.plugin.activerecord.Db;
import com.maogousoft.wuliu.common.exception.BusinessException;


/**
 * @description 司机
 * @author shevliu
 * @email shevliu@gmail.com
 * Jul 28, 2012 4:58:30 PM
 */
public class Driver extends BaseModel<Driver>{

	private static final long serialVersionUID = 1L;

	/**
	 * 已删除
	 */
	public static final int STATUS_DELETED = -1 ;

	/**
	 * 待审核
	 */
	public static final int STATUS_PENDING_AUDIT = 0 ;

	/**
	 * 有效，审核通过
	 */
	public static final int STATUS_VALID = 1 ;


	/**
	 * 无效，审核未通过
	 */
	public static final int STATUS_INVALID = 2 ;

	public static final Driver dao = new Driver();

	/**
	 * 判断司机号码是否已存在
	 * @param phone
	 * @return
	 */
	public boolean existsPhone(String phone) {
		Long count = Db.queryLong("select count(1) from logistics_driver where phone=?", phone);
		return count > 0;
	}

	/**
	 * 根据号码获取司机信息
	 * @param phone
	 * @return
	 */
	public Driver findByPhone(String phone) {
		return dao.findFirst("select * from logistics_driver where phone=?", phone);
	}

	/**
	 * 根据ID载入司机信息，如果不存在，则抛出异常
	 * @param driver_id
	 * @return
	 */
	public Driver loadDriverById(int driver_id) {
		Driver driver = dao.findById(driver_id);
		if(driver == null) {
			throw new BusinessException("司机不存在[" + driver_id + "]");
		}
		return driver;
	}

	/**
	 * 调整司机物流币
	 * @param amount
	 * @return 调整前与调整后的金额
	 */
	public GoldResult adjustGold(double amount) {
		GoldResult result = new GoldResult();
		double gold = this.asDoubleValue("gold", 0);
		result.setBeforeGold(gold);

		gold = gold + amount;
		this.set("gold", gold);
		this.update();

		result.setAfterGold(gold);

		return result;
	}
}
