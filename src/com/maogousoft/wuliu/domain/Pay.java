/**
 * @filename Pay.java
 */
package com.maogousoft.wuliu.domain;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;

/**
 * @description 充值
 * @author shevliu
 * @email shevliu@gmail.com
 * May 19, 2013 5:38:58 PM
 */
public class Pay extends BaseModel<Pay>{

	private static final long serialVersionUID = 4939599225608805930L;
	
	/**
	 * 创建
	 */
	public static final int STATUS_CREATE = 0 ;
	
	/**
	 * 确认支付
	 */
	public static final int STATUS_CONFIRM = 1 ;
	
	/**
	 * 支付成功
	 */
	public static final int STATUS_SUCCESS = 99 ;
	
	/**
	 * 支付失败
	 */
	public static final int STATUS_FAIL = -1 ;
	
	/**
	 * 取消支付
	 */
	public static final int STATUS_CANCEL = 2 ;
	
	public static final Pay dao = new Pay();
	
	/**
	 * 
	 * @description 获取司机充值记录
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 26, 2013 4:36:59 PM
	 * @param driverId
	 * @return
	 */
	public List<Pay> getDriverPayList(int driverId){
		String u_id = "d" + driverId ;
		String sql  = "select * from logistics_pay  where u_id = ? and status = 99 order by id desc ";
		return dao.find(sql , u_id);
	}
	
	/**
	 * 
	 * @description 分页获取司机充值记录 
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 26, 2013 4:48:13 PM
	 * @param driverId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Pay> getDriverPayList(int driverId , int pageNumber, int pageSize){
		String u_id = "d" + driverId ;
		String select  = "select * ";
		String from =  "from logistics_pay where u_id = ? and status = 99  order by id desc" ;
		return dao.paginate(pageNumber, pageSize, select, from , u_id);
	}
	
	/**
	 * 
	 * @description 获取货主充值记录
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 26, 2013 4:36:59 PM
	 * @param driverId
	 * @return
	 */
	public List<Pay> getUserPayList(int userId){
		String u_id = "u" + userId ;
		String sql  = "select * from logistics_pay  where u_id = ? and status = 99  order by id desc ";
		return dao.find(sql , u_id);
	}
	
	/**
	 * 
	 * @description 分页获取货主充值记录
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 26, 2013 4:47:45 PM
	 * @param userId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Pay> getUserPayList(int userId , int pageNumber, int pageSize){
		String u_id = "u" + userId ;
		String select  = "select * ";
		String from =  "from logistics_pay where u_id = ? and status = 99  order by id desc" ;
		return dao.paginate(pageNumber, pageSize, select, from , u_id);
	}
}
