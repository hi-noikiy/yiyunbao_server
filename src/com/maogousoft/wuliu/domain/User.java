/**
 * @filename Dict.java
 */
package com.maogousoft.wuliu.domain;

import com.jfinal.plugin.activerecord.Db;
import com.maogousoft.wuliu.common.exception.BusinessException;

/**
 * @description 货主
 * @author shevliu
 * @email shevliu@gmail.com
 * Mar 15, 2013 9:02:30 PM
 */
public class User extends BaseModel<User>{

	private static final long serialVersionUID = 2230573683269292447L;

	public static final User dao = new User();

	public boolean existsPhone(String phone) {
		Long count = Db.queryLong("select count(1) from logistics_user where phone=?", phone);
		return count > 0;
	}

	public User findByPhone(String phone) {
		return dao.findFirst("select * from logistics_user where phone=?", phone);
	}

	/**
	 * 根据ID载入货主信息，如果不存在，则抛出异常
	 * @param driver_id
	 * @return
	 */
	public User loadUserById(int user_id) {
		User user = dao.findById(user_id);
		if(user == null) {
			throw new BusinessException("货主不存在[" + user_id + "]");
		}
		return user;
	}

	/**
	 * 调整货主物流币
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

	//不需要了，数据库有score字段
//	public double getScore() {
//		Integer s1 = this.getInt("score1");
//		Integer s2 = this.getInt("score2");
//		Integer s3 = this.getInt("score3");
//		if(s1 == null) {
//			s1 = 0;
//		}
//		if(s2 == null) {
//			s2 = 0;
//		}
//		if(s3 == null) {
//			s3 = 0;
//		}
//		double avg = ((s1 + s2 + s3)+0.1) / 3;
//		return avg;
//	}
}
