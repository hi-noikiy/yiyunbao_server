/**
 * @filename Business.java
 */
package com.maogousoft.wuliu.domain;

import java.util.Date;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

/**
 * @description 交易记录
 * @author shevliu
 * @email shevliu@gmail.com
 * Mar 31, 2013 5:28:32 PM
 */
public class Business extends Model<Business>{

	private static final long serialVersionUID = -261562420381703712L;
	
	/**
	 * 交易对象类型：货主
	 */
	public static final int TARGET_TYPE_USER = 0 ;
	
	/**
	 * 交易对象类型：司机
	 */
	public static final int TARGET_TYPE_DRIVER = 1;

	/**
	 * 交易类型“充值
	 */
	public static final int BUSINESS_TYPE_RECHARGE = 0 ;
	
	
	/**
	 * 违约扣除
	 */
	public static final int BUSINESS_TYPE_VIOLATE = 2 ;
	
	/**
	 * 成功交易扣除
	 */
	public static final int BUSINESS_TYPE_DEAL = 3 ;
	
	/**
	 * 验证身份扣除
	 */
	public static final int BUSINESS_TYPE_VERIFY = 4 ;
	
	/**
	 * 冻结
	 */
	public static final int BUSINESS_TYPE_FREEZE = 5 ;
	
	/**
	 * 保证金扣除
	 */
	public static final int BUSINESS_TYPE_DEPOSIT = 6 ;
	
	/**
	 * 短信扣除
	 */
	public static final int BUSINESS_TYPE_SMS = 7 ;
	
	/**
	 * 保证金返还
	 */
	public static final int BUSINESS_TYPE_DEPOSIT_RETURN= 8 ;
	
	/**
	 * 对方违约返还
	 */
	public static final int BUSINESS_TYPE_VIOLATE_RETURN= 9 ;
	
	/**
	 * 手机定位扣除
	 */
	public static final int BUSINESS_TYPE_LOCATION= 10 ;
	
	/**
	 * 成功交易奖励
	 */
	public static final int BUSINESS_TYPE_AWARD = 11 ;

	/**
	 * 对方违约赔付
	 */
	public static final int BUSINESS_TYPE_VIOLATE_PAID = 12 ;
	
	public static Business dao = new Business();
	
	/**
	 * 
	 * @description 货主交易记录 
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 26, 2013 10:30:18 PM
	 * @param userId
	 * @param businessType
	 * @param amount
	 * @param beforeMoney
	 * @param afterMoney
	 */
	public void addUserBusiness(int userId , int businessType , double amount , double  beforeMoney , double afterMoney ){
		String businessSql = "insert into logistics_business (business_target , account , business_type , business_amount , before_balance , after_balance , create_time , system_user_id) values (?,?,?,?,?,?,?,?)" ;
		Db.update(businessSql , Business.TARGET_TYPE_USER , userId , businessType , amount , beforeMoney , afterMoney , new Date(),null);
	}
	
	/**
	 * 
	 * @description 司机交易记录 
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 26, 2013 10:30:41 PM
	 * @param userId
	 * @param businessType
	 * @param amount
	 * @param beforeMoney
	 * @param afterMoney
	 */
	public void addDriverBusiness(int userId , int businessType , double amount , double  beforeMoney , double afterMoney ){
		String businessSql = "insert into logistics_business (business_target , account , business_type , business_amount , before_balance , after_balance , create_time , system_user_id) values (?,?,?,?,?,?,?,?)" ;
		Db.update(businessSql , Business.TARGET_TYPE_DRIVER , userId , businessType , amount , beforeMoney , afterMoney , new Date(),null);
	}
	
}
