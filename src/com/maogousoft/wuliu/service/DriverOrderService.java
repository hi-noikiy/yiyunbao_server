package com.maogousoft.wuliu.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.common.json.Param;
import com.maogousoft.wuliu.common.json.Result;
import com.maogousoft.wuliu.common.syslog.ServiceLog;
import com.maogousoft.wuliu.common.util.StrUtil;
import com.maogousoft.wuliu.controller.ServiceContext;
import com.maogousoft.wuliu.domain.Area;
import com.maogousoft.wuliu.domain.Business;
import com.maogousoft.wuliu.domain.Dict;
import com.maogousoft.wuliu.domain.Driver;
import com.maogousoft.wuliu.domain.GoldResult;
import com.maogousoft.wuliu.domain.Msg;
import com.maogousoft.wuliu.domain.Order;
import com.maogousoft.wuliu.domain.OrderExecute;
import com.maogousoft.wuliu.domain.OrderLocation;
import com.maogousoft.wuliu.domain.OrderLog;
import com.maogousoft.wuliu.domain.OrderVie;
import com.maogousoft.wuliu.domain.User;
import com.maogousoft.wuliu.domain.UserReply;

/**
 * 司机端货单服务接口
 * @author yangfan(kenny0x00@gmail.com) 2013-4-16 上午12:26:27
 */
public class DriverOrderService {

	/**
	 * 新货源列表查询
	 * @param param
	 * @return
	 */
	public static Object query_source_order(Param param) {
		ServiceContext.assertLoginAsDriver();

		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		final int driver_id = ServiceContext.getServiceContext().getUserId();
		Driver driver = Driver.dao.loadDriverById(driver_id);

		String select = "";
		select += "SELECT a.id,a.start_province,a.start_city,a.start_district,a.end_province,a.end_city,";
		select += "a.end_district,a.cargo_desc,a.cargo_type,a.car_length,a.cargo_number,a.cargo_unit,a.unit_price,a.price,a.ship_type,";
		select += "a.car_type,a.cargo_photo1,a.cargo_photo2,a.cargo_photo3,a.loading_time,a.cargo_remark,";
		select += "a.validate_time,a.user_bond,push_drvier_count,vie_driver_count,b.score,a.create_time,";
		select += "(CASE WHEN last_read_new_order>a.create_time THEN 1 ELSE 0 END) as read_status";//订单阅读状态0-未读,1-已读

		String sqlExceptSelect = " from logistics_order a ";
		sqlExceptSelect += " LEFT JOIN logistics_user b ON b.id=a.user_id";
		sqlExceptSelect += " LEFT JOIN logistics_driver c ON c.id=?";
		sqlExceptSelect += " where a.status=1 and validate_time>NOW()";//1-审核通过(进行中)
		//忽略已投标的订单数据
		sqlExceptSelect += " and not exists(select 1 from logistics_order_vie b where b.order_id=a.id and b.driver_id=? and (b.status=0 or b.status=1))";
		
		// 车型
		Integer car_type = driver.getInt("car_type");
		// 车长
        Double car_length = driver.getDouble("car_length");
        if(null != car_type){
        	sqlExceptSelect +=" and a.car_type = "+car_type+" ";
        }
        if(null != car_length){
        	sqlExceptSelect +=" and a.car_length="+car_length+" ";
        }


		//start 非主营路线货源不要出现在新货源中
		sqlExceptSelect += " and ((1=2)";
		Integer start_province = driver.getInt("start_province");
		Integer end_province = driver.getInt("end_province");
		if(start_province != null && end_province != null) {
			String start = getRelatedProvinces(start_province);
			String end = getRelatedProvinces(end_province);
			sqlExceptSelect += " or (a.start_province in (" + start + ") and a.end_province in (" + end + "))";
			sqlExceptSelect += " or (a.start_province in (" + end + ") and a.end_province in (" + start + "))";
		}

		Integer start_province2 = driver.getInt("start_province2");
		Integer end_province2 = driver.getInt("end_province2");
		if(start_province2 != null && end_province2 != null) {
			String start2 = getRelatedProvinces(start_province2);
			String end2 = getRelatedProvinces(end_province2);
			sqlExceptSelect += " or (a.start_province in (" + start2 + ") and a.end_province in (" + end2 + "))";
			sqlExceptSelect += " or (a.start_province in (" + end2 + ") and a.end_province in (" + start2 + "))";
		}

		Integer start_province3 = driver.getInt("start_province3");
		Integer end_province3 = driver.getInt("end_province3");
		if(start_province3 != null && end_province3 != null) {
			String start3 = getRelatedProvinces(start_province3);
			String end3 = getRelatedProvinces(end_province3);
			sqlExceptSelect += " or (a.start_province in (" + start3 + ") and a.end_province in (" + end3 + "))";
			sqlExceptSelect += " or (a.start_province in (" + end3 + ") and a.end_province in (" + start3 + "))";
		}

		sqlExceptSelect += ")";
		//end 非主营路线货源不要出现在新货源中


		sqlExceptSelect += " order by a.id desc";
		Page<Record> pageObj = Db.paginate(page, pageSize, select, sqlExceptSelect, driver_id, driver_id);
		List<Record> list = pageObj.getList();
		Dict.fillDictToRecords(list);

		//设置关注状态
		for (Record record : list) {
			long cnt = Db.queryLong("select count(1) from logistics_favorite where order_id=? and driver_id=? and status=0" , record.getInt("id") , driver_id);
			if(cnt > 0) {
				record.set("favorite_status", 1);
			}else {
				record.set("favorite_status", 0);
			}
		}

		//更新查看新货源时间
		Db.update("update logistics_driver set last_read_new_order=NOW() where id=?", driver_id);

		return Result.success().data("items", list);
	}

	/**
	 * 地区进行分组，获取相关的地区
	 * 江苏、浙江、上海
	 * 北京、天津、河北
	 * @param province_id
	 * @return
	 */
	private static String getRelatedProvinces(int province_id) {
		String start = province_id + "";
		if(province_id == Area.ID_Beijing || province_id == Area.ID_TianJin || province_id == Area.ID_HeBei) {
			start = Area.ID_Beijing + "," + Area.ID_TianJin + "," + Area.ID_HeBei;
		}
		if(province_id == Area.ID_ShangHai || province_id == Area.ID_JiangShu || province_id == Area.ID_ZheJiang) {
			start = Area.ID_ShangHai + "," + Area.ID_JiangShu + "," + Area.ID_ZheJiang;
		}
		return start;
	}

	/**
	 * 新货源详情查询
	 * @param param
	 * @return
	 */
	public static Object get_source_order_detail(Param param) {
		ServiceContext.assertLoginAsDriver();

		int orderId = param.getRequiredInt("order_id");
		Order order = Order.dao.findById(orderId);
		if(order == null) {
			return Result.fail("货单" + orderId + "不存在");
		}
		if(!order.isStauts(1)) {
			throw new BusinessException("货源" + orderId + "不处于新货源状态,status=" + order.getInt("status"));
		}
		User orderOwner = User.dao.loadUserById(order.getInt("user_id"));
		Dict.fillDictToModel(order);
		order.put("score1", orderOwner.getDouble("score1"));
		order.put("score2", orderOwner.getDouble("score2"));
		order.put("score3", orderOwner.getDouble("score3"));
		order.put("score", orderOwner.getDouble("score"));
		order.put("user_phone", orderOwner.getStr("phone"));
		order.remove("status");
		return Result.success().data("item", order);
	}
	
	/**
	 * 新货源详情查询 版本2 升级版
	 * @param param
	 * @return
	 */
	public static Object get_source_order_detail_2(Param param) {
		ServiceContext.assertLoginAsDriver();
		
		int orderId = param.getRequiredInt("order_id");
		Order order = Order.dao.findById(orderId);
		if(order == null) {
			return Result.fail("货单" + orderId + "不存在");
		}
		if(!order.isStauts(1)) {
			throw new BusinessException("货源" + orderId + "不处于新货源状态,status=" + order.getInt("status"));
		}
		User orderOwner = User.dao.loadUserById(order.getInt("user_id"));
		Dict.fillDictToModel(order);
		order.put("score1", orderOwner.getDouble("score1"));
		order.put("score2", orderOwner.getDouble("score2"));
		order.put("score3", orderOwner.getDouble("score3"));
		order.put("score", orderOwner.getDouble("score"));
		order.put("user_phone", orderOwner.getStr("phone"));
		
		StringBuffer html = montageHTML(order);
		order.set("html", html);
		
		order.remove("status");
		return Result.success().data("item", order);
	}
	
	/**
	 * 拼接HTML字符串
	 * @param order
	 * @return
	 */
	private static StringBuffer montageHTML(Order order){
		
		StringBuffer suff = new StringBuffer();
		suff.append("<html><body><table>");
		suff.append("<tr>");
		suff.append("<td>货物名称：</td>");
		suff.append("<td>"+order.getStr("cargo_desc")+"</td>");
		suff.append("</tr>");
		suff.append("<tr>");
		suff.append("<td>货物类型：</td>");
		suff.append("<td>"+order.getStr("cargo_type")+"</td>");
		suff.append("<td>运输方式：</td>");
		suff.append("<td>"+order.getStr("ship_type")+"</td>");
		suff.append("</tr>");
		suff.append("<tr>");
		suff.append("<td>体积重量：</td>");
		suff.append("<td>"+order.getStr("cargo_number")+order.getStr("cargo_unit")+"</td>");
		suff.append("<td>运输总价：</td>");
		suff.append("<td>"+order.getStr("price")+"元</td>");
		suff.append("</tr>");
		suff.append("<tr>");
		suff.append("<td>需要车型：</td>");
		suff.append("<td>"+order.getStr("car_type")+"</td>");
		suff.append("<td>需要车长：</td>");
		suff.append("<td>"+order.getStr("car_length")+"米</td>");
		suff.append("</tr>");
		suff.append("<tr>");
		suff.append("td>装车时间：</td>");
		suff.append("<td>"+order.getStr("loading_time")+" 之前</td>");
		suff.append("</tr>");
		suff.append("<tr>");
		suff.append("<td>有效时间：</td>");
		suff.append("<td>"+order.getStr("validate_time")+"</td>");
		suff.append("</tr>");
		suff.append("<tr>");
		suff.append("<td>补充说明：</td>");
		suff.append("<td><textarea>"+order.getStr("cargo_remark")+"</textarea></td>");
		suff.append("</tr>");
		suff.append("</table></body></html>");
		
		return suff;
	}
	

	/**
	 * 新货源-抢单
	 * @param param
	 * @return
	 */
	@Before(Tx.class)
	public static Object place_source_order(Param param) {
		ServiceContext.assertLoginAsDriver();
		int driver_id = ServiceContext.getServiceContext().getUserId();
		Driver driver = Driver.dao.loadDriverById(driver_id);
		if(driver.getInt("status") == Driver.STATUS_PENDING_AUDIT){
			throw new BusinessException("司机未通过审核，不能抢单。请完善资料，经易运宝审核通过后才能抢单。");
		}
		int order_id = param.getRequiredInt("order_id");
		double driver_bond = param.getRequiredDouble("driver_bond");
		double driver_price = param.getRequiredDouble("driver_price");

		Order order = Order.dao.loadOrder(order_id);

		if(OrderVie.dao.hasVieByDriver(order_id,driver_id)) {
			throw new BusinessException("不能重复抢单,order_id=" + order_id + ",driver_id=" + driver_id);
		}

		Order dealOrder = Order.dao.findFirst("select * from logistics_order where driver_id = ? and status = ?" , driver_id , Order.STATUS_DEAL);
		if(dealOrder != null){
			throw new BusinessException("已经承接了其他货单，不能再抢单了");
		}

		final int orderStatus = order.getInt("status");
		if(orderStatus != 1) { //1-审核通过(进行中)
			throw new BusinessException("货源" + order_id + "不处于新货源状态,无法抢单,status=" + order.getInt("status"));
		}

		//判断订单是否已过期
		if(!order.isInValidateTime()) {
			throw new BusinessException("订单已过有效期");
		}

		

		//判断保证金必须大于等于货主的保证金
		final double user_bond = order.asDoubleValue("user_bond", 0);
		if(driver_bond < user_bond) {
			throw new BusinessException("保证金须与发货方相同或者高于发货方,金额:" + user_bond);
		}

		// 余额扣除，保证金
		double gold = driver.asDoubleValue("gold", 0);
		if(gold < driver_bond){
			return Result.fail("余额不足以支付保证金:" + driver_bond + ",请先充值");
		}
		GoldResult gr = driver.adjustGold(-driver_bond);

		//增加抢单记录
		OrderVie ov = new OrderVie();
		ov.set("order_id", order_id);
		ov.set("driver_id", driver_id);
		ov.set("driver_bond", driver_bond);
		ov.set("driver_price", driver_price);
		ov.set("create_time", new Date());
		ov.set("status", 0);//0-竞标中
		ov.save();

		//调整抢单人数
		order.adjustVieCount(1);

		//记录订单变更
		OrderLog.logOrder(order_id, "司机[" + driver.getStr("name") + "]", "进行了抢单.");

		//记录司机帐户变化
		Business.dao.addDriverBusiness(driver_id, Business.BUSINESS_TYPE_DEPOSIT, driver_bond, gr.getBeforeGold(), gr.getAfterGold());

		//给司机发送帐户变化信息
		Msg.dao.addDriverMsg(Msg.TYPE_BUSINIESS, "帐户变化", "参加抢单，货源编号:" + order_id + "，扣除保证金：" + driver_bond, driver_id);

		//给货主发送信息
		Msg.dao.addUserMsg(Msg.TYPE_ORDER, "有人抢单", "编号为:" + order_id + "的货单已有人抢单，司机姓名:" + driver.get("name"), order.getInt("user_id"));

		//接口调用记录
		ServiceLog.info("司机[%s]进行抢单，订单号：[%s]", ServiceContext.getServiceContext().getPhone(), order_id).save();

		return Result.success();
	}

	/**
	 * 待定货源列表查询
	 * @param param
	 * @return
	 */
	public static Object query_pending_source_order(Param param) {
		ServiceContext.assertLoginAsDriver();

		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		final int driver_id = ServiceContext.getServiceContext().getUserId();

		String select = "";
		select += "SELECT a.id,start_province,start_city,start_district,end_province,end_city,";
		select += "end_district,cargo_desc,cargo_type,car_length,cargo_weight,price,ship_type,";
		select += "car_type,cargo_photo1,cargo_photo2,cargo_photo3,loading_time,cargo_remark,";
		select += "validate_time,user_bond,push_drvier_count,vie_driver_count,a.create_time";

		String sqlExceptSelect = "from logistics_order a ";
		sqlExceptSelect += " where a.status=1";//1-审核通过(进行中)
		//只获取已投标的订单数据
		sqlExceptSelect += " and exists(select 1 from logistics_order_vie b where b.order_id=a.id and b.driver_id=? and b.status=0) order by a.id desc";
		Page<Record> pageObj = Db.paginate(page, pageSize, select, sqlExceptSelect, driver_id);
		List<Record> list = pageObj.getList();
		Dict.fillDictToRecords(list);
		return Result.success().data("items", list);
	}

	/**
	 * 待定货源-取消抢单
	 * @param param
	 * @return
	 */
	@Before(Tx.class)
	public static Object cancel_place_source_order(Param param) {
		ServiceContext.assertLoginAsDriver();

		int order_id = param.getRequiredInt("order_id");

		Order order = Order.dao.loadOrder(order_id);
		int driver_id = ServiceContext.getServiceContext().getUserId();

		if(!OrderVie.dao.hasVieByDriver(order_id,driver_id)) {
			throw new BusinessException("没有抢单,无法取消,order_id=" + order_id + ",driver_id=" + driver_id);
		}

		OrderVie ov = OrderVie.dao.getVieByOrderAndDriver(order_id,driver_id);
		//0-竞标中，1-中标，2-退出竞标
		final int vieStauts = ov.getInt("status");
		if(vieStauts == 0) {
			ov.set("status", 2);
			ov.update();
			//调整抢单人数
			order.adjustVieCount(-1);

			//退出抢单后，将原来扣除的保证金加上
			double driver_bond = ov.asDoubleValue("driver_bond", 0);
			Driver driver = Driver.dao.findById(driver_id);
			GoldResult gr = driver.adjustGold(driver_bond);
			driver.update();

			//记录订单变更
			OrderLog.logOrder(order_id, "司机[" + driver.getStr("name") + "]", "退出了抢单.");

			//记录司机帐户变化
			Business.dao.addDriverBusiness(driver_id, Business.BUSINESS_TYPE_DEPOSIT_RETURN, driver_bond, gr.getBeforeGold(), gr.getAfterGold());

			//给司机发送帐户变化信息
			Msg.dao.addDriverMsg(Msg.TYPE_BUSINIESS, "帐户变化", "退出抢单，货源编号:" + order_id + "，返回保证金：" + driver_bond, driver_id);

			//给货主发送信息
			Msg.dao.addUserMsg(Msg.TYPE_ORDER, "退出抢单", "编号为:" + order_id + "的货单有人退出抢单，司机姓名:" + driver.get("name"), order.getInt("user_id"));

			//记录接口日志
			ServiceLog.info("司机[%s]取消抢单[%s]成功", ServiceContext.getServiceContext().getPhone(), order_id).save();
		}
		else if(vieStauts == 1) {
			throw new BusinessException("已中标[" + order_id + "],无法退出抢单.");
		}
		else if(vieStauts == 2) {
			throw new BusinessException("已退出抢单[" + order_id + "],无法再次退出.");
		}


		return Result.success();
	}

	/**
	 * 在途货源列表查询
	 * @param param
	 * @return
	 */
	public static Object query_pending_source_order_in_shipping(Param param) {
		ServiceContext.assertLoginAsDriver();

		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		final int driver_id = ServiceContext.getServiceContext().getUserId();

		String select = "";
		select += "SELECT a.* ,";
		select += " b.name as user_name, b.company_name , b.telcom as user_telcom,";
		select += "b.phone as user_phone, b.id as user_id ";

		String sqlExceptSelect = " from logistics_order a left join logistics_user b on a.user_id = b.id";
		sqlExceptSelect += " where a.driver_id = ? and  a.status=3 order by a.id desc";//3-已中标
		Page<Record> pageObj = Db.paginate(page, pageSize, select, sqlExceptSelect, driver_id);
		List<Record> list = pageObj.getList();

		for (Record record : list) {
			Record executeRecord = Db.findFirst("select * from logistics_order_status a where order_id =? order by a.id desc" , record.getInt("id"));
			record.set("execute_status", executeRecord == null ? -1 : executeRecord.getInt("status"));
		}


		Dict.fillDictToRecords(list);
		return Result.success().data("items", list);
	}

	/**
	 * 新货源-关注
	 * @param param
	 * @return
	 */
	public static Object attention_source_order(Param param) {
		ServiceContext.assertLoginAsDriver();
		final int driverId = ServiceContext.getServiceContext().getUserId();
		int order_id = param.getRequiredInt("order_id");
		Long count = Db.queryLong("select count(*) from logistics_favorite where order_id=? and driver_id=? and status=0",order_id, driverId);
		if(count > 0) {
			throw new BusinessException("已关注货单[" + order_id + "]");
		}
		String sql = "insert into logistics_favorite (order_id , driver_id , create_time , status) values (?,?,?,?)";
		Db.update(sql , order_id , driverId , new Date(), 0);
		return Result.success();
	}

	/**
	 * 在途货源-状态变更
	 * @param param
	 * @return
	 */
	public static Object shipping_order_update_status(Param param) {
		ServiceContext.assertLoginAsDriver();
		int order_id = param.getRequiredInt("order_id");
		final int status = param.getRequiredInt("status");
		final int driverId = ServiceContext.getServiceContext().getUserId();
		final Order order = getAndVerifyOrder(order_id);
		try{
			String sql = "insert into logistics_order_status(order_id,status,create_time) values(?,?,?)";
			Db.update(sql , order_id , status , new Date());

			//记录订单变更
			Driver driver = Driver.dao.loadDriverById(ServiceContext.getServiceContext().getUserId());
			OrderLog.logOrder(order_id, "司机[" + driver.getStr("name") + "]", "上报货源状态:" + Order.getStatusDesc(status));

			return Result.success();
		}catch(RuntimeException re){
			ServiceLog.error("司机[%s]修改在途货源状态失败", driverId).data("message",re.getMessage()).save();
			return Result.fail("修改在途货源状态失败");
		}
	}

	/**
	 * 在途货源-位置汇报
	 * @param param
	 * @return
	 */
	public static Object shipping_order_update_location(Param param) {
		ServiceContext.assertLoginAsDriver();
		final int driver_id = ServiceContext.getServiceContext().getUserId();
		int order_id = param.getIntValue("order_id", -1);
//		String driver_phone = param.getRequiredString("driver_phone");
		String location = param.getRequiredString("location");
		double longitude = param.getRequiredDouble("longitude");
		double latitude = param.getRequiredDouble("latitude");

		if(order_id != -1) {
			getAndVerifyOrder(order_id);
		}
		OrderLocation orderLocation = new OrderLocation();
		orderLocation.set("order_id", order_id);
		orderLocation.set("driver_id", driver_id);
		orderLocation.set("location", location);
		orderLocation.set("longitude", longitude);
		orderLocation.set("latitude", latitude);
		orderLocation.set("create_time", new Date());
		orderLocation.save();

		//同时更新司机表经纬度
		String sql = "update logistics_driver set longitude=? , latitude=?, last_position=?,last_position_time=? where id = ?";
		Db.update(sql , longitude,latitude,location,new Date(),driver_id);
		return Result.success();
	}

	//与订单位置汇报合并
//	/**
//	 * 在途货源-位置汇报
//	 * @param param
//	 * @return
//	 */
//	public static Object driver_report_location(Param param) {
//		final int driver_id = ServiceContext.getServiceContext().getUserId();
//		String location = param.getRequiredString("location");
//		long longitude = param.getRequiredLong("longitude");
//		long latitude = param.getRequiredLong("latitude");
//
//		//更新司机表经纬度
//		String sql = "update logistics_driver set longitude=? , latitude=?, last_position=?,last_position_time=? where id = ?";
//		Db.update(sql , longitude,latitude,location,new Date(),driver_id);
//		return Result.success();
//	}

	/**
	 *
	 * @description 验证回单密码。如果回单密码正确，表示订单结束。修改订单状态，同时修改货物状态
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 3:00:10 PM
	 * @param param
	 * @return
	 */
	@Before(Tx.class)
	public static Object validate_receipt_password(Param param) {
		ServiceContext.assertLoginAsDriver();
		int orderId = param.getRequiredInt("order_id");
		String receipt_password = param.getRequiredString("receipt_password");
		Order order = getAndVerifyOrder(orderId);
		if(order == null) {
			return Result.fail("货单" + orderId + "不存在");
		}
		if(order.getInt("status")!=Order.STATUS_DEAL){
			return Result.fail("货单" + orderId + "状态不正确");
		}
		if(!receipt_password.equals(order.getStr("receipt_password"))){
			return Result.fail("回单密码不正确");
		}
		else{
			String sql = "insert into logistics_order_status(order_id,status,create_time) values(?,?,?)";
			Db.update(sql , orderId , OrderExecute.STATUS_RECEIPT , new Date());

			String orderSQL = "update logistics_order set status = ?, finish_time = ? where id = ?" ;
			Db.update(orderSQL , Order.STATUS_FINISH , new Date(),  orderId);
			User user = User.dao.loadUserById(order.getInt("user_id"));
			user.set("order_count", user.getInt("order_count") + 1) ; //将成交订单数增加
			user.update();

			//返还货主保证金
			double user_bond = order.getDouble("user_bond");
			GoldResult gUserBond = user.adjustGold(user_bond);
			Business.dao.addUserBusiness(order.getInt("user_id"), Business.BUSINESS_TYPE_DEPOSIT_RETURN, user_bond, gUserBond.getBeforeGold(), gUserBond.getAfterGold());

			//奖励货主
			double fee = order.getFee();
			double award = fee * 0.15;//按信息费的15%奖励给发货方
			GoldResult gAward = user.adjustGold(award);
			Business.dao.addUserBusiness(order.getInt("user_id"), Business.BUSINESS_TYPE_AWARD, award, gAward.getBeforeGold(), gAward.getAfterGold());

			//返还司机保证金
			double driver_bond = order.getDouble("driver_bond");
			Driver driver = Driver.dao.findById(order.getInt("driver_id"));
			driver.set("order_count", driver.getInt("order_count") + 1) ; //将成交订单数增加
			driver.update();
			GoldResult grDriver = driver.adjustGold(driver_bond);
			//记录帐户信息
			Business.dao.addDriverBusiness(order.getInt("driver_id"), Business.BUSINESS_TYPE_DEPOSIT_RETURN, driver_bond, grDriver.getBeforeGold(), grDriver.getAfterGold());


			// 以下是推荐人提成
			String recommender = driver.getStr("recommender");
			if(StringUtils.hasText(recommender)){
				//如果是货主，获得信息费的15%物流币奖励, 最高30个物流币
				double recommenderUserAward = Math.min(award, 30);
				User recommenderUser = User.dao.findFirst("select * from logistics_user where phone = ?" , recommender);
				if(recommenderUser != null && recommenderUser.getInt("payoff_time") < 10){
					System.out.println("fee......" + fee);
					System.out.println("recommenderUserAward......" + recommenderUserAward);
					System.out.println("recommenderUser.getDouble(gold) + recommenderUserAward......" + (recommenderUser.getDouble("gold") + recommenderUserAward));
					recommenderUser.set("payoff_time", recommenderUser.getInt("payoff_time") + 1);
					recommenderUser.update();

					GoldResult gr = recommenderUser.adjustGold(recommenderUserAward);
					Business.dao.addUserBusiness(recommenderUser.getInt("id"), Business.BUSINESS_TYPE_AWARD, recommenderUserAward, gr.getBeforeGold(), gr.getAfterGold());
					Msg.dao.addUserMsg(Msg.TYPE_BUSINIESS, "推荐朋友获得奖励", "您推荐的朋友" + driver.getStr("name") + "完成了交易，您获得奖励：" + recommenderUserAward, recommenderUser.getInt("id"));
				}
				//如果是司机，获得信息费的30%物流币奖励, 最高60个物流币
				double recommenderDriverAward = Math.min(fee * 0.3, 60);
				Driver recommenderDriver = Driver.dao.findFirst("select * from logistics_driver where phone = ?" , recommender);
				if(recommenderDriver != null && recommenderDriver.getInt("payoff_time") < 10){
					recommenderDriver.set("payoff_time", recommenderDriver.getInt("payoff_time") + 1);
					recommenderDriver.update();
					recommenderDriver.set("gold", recommenderDriver.getDouble("gold") + recommenderDriverAward);
					GoldResult gr = recommenderDriver.adjustGold(recommenderDriverAward);
					Business.dao.addDriverBusiness(recommenderDriver.getInt("id"), Business.BUSINESS_TYPE_AWARD, recommenderDriverAward, gr.getBeforeGold(), gr.getAfterGold());
					Msg.dao.addDriverMsg(Msg.TYPE_BUSINIESS, "推荐朋友获得奖励", "您推荐的朋友" + driver.getStr("name") + "完成了交易，您获得奖励：" + recommenderDriverAward, recommenderDriver.getInt("id"));
				}
			}

			//记录订单变更
			OrderLog.logOrder(orderId, "司机[" + driver.getStr("name") + "]", "验证回单密码，订单完成.");
			//推送信息
			Msg.dao.addDriverMsg(Msg.TYPE_ORDER, "订单已完成", "订单已经完成，订单编号：" + orderId + ",保证金：" + driver_bond + "已返还", order.getInt("driver_id"));
			Msg.dao.addUserMsg(Msg.TYPE_ORDER, "订单已完成", "订单已经完成，订单编号：" + orderId + ",保证金：" + user_bond + "已返还,同时获得：" + award + "信息费作为奖励", order.getInt("user_id"));

			return Result.success("回单密码验证成功");
		}
	}

	/**
	 *
	 * @description 司机完成交易后对货主评价
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 5:53:28 PM
	 * @param param
	 * @return
	 */
	public static Object rating_to_user(Param param) {
		ServiceContext.assertLoginAsDriver();
		final int driver_id = ServiceContext.getServiceContext().getUserId();
		int orderId = param.getRequiredInt("order_id");
		int score1 = param.getRequiredInt("score1");
		int score2 = param.getRequiredInt("score2");
		int score3 = param.getRequiredInt("score3");
		String reply_content = param.getString("reply_content");

		Order order = Order.dao.findById(orderId);
		if(order == null) {
			return Result.fail("货单" + orderId + "不存在");
		}
		if(order.getInt("status") != Order.STATUS_FINISH) {
			return Result.fail("货单" + orderId + "还未完成，不能评价");
		}
		if(order.getInt("driver_id") != driver_id){
			return Result.fail("货单" + orderId + "并非该司机[" + driver_id + "]承运，无权限评价");
		}
		String sql = "select count(0) from logistics_user_reply where order_id = ?" ;
		long replyCount = Db.queryLong(sql , orderId);
		if(replyCount > 0){
			return Result.fail("货单" + orderId + "已评价过了");
		}
		UserReply reply = new UserReply();
		reply.put("order_id", orderId);
		reply.put("score1", score1);
		reply.put("score2", score2);
		reply.put("score3", score3);
		reply.put("reply_content", reply_content);
		reply.put("user_id", order.getInt("user_id"));
		reply.put("driver_id", driver_id);
		reply.put("user_reply", 1);//已评价
		reply.put("reply_time", new Date());
		reply.save();

		//更新评价情况
		order.set("user_reply", 1);//司机对货主评价完成
		order.update();

		//给货主推送消息
		Msg.dao.addUserMsg(Msg.TYPE_ORDER, "货单已评价", "货单:" + orderId + "已评价", order.getInt("user_id"));

		//记录订单变更
		Driver driver = Driver.dao.loadDriverById(driver_id);
		OrderLog.logOrder(orderId, "司机[" + driver.getStr("name") + "]", "进行了评价");


		return Result.success("评价成功");
	}

	/**
	 *
	 * @description 查询已完成订单
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 9:02:53 PM
	 * @param param
	 * @return
	 */
	public static Object driver_query_finished_order(Param param) {
		ServiceContext.assertLoginAsDriver();
		final int driver_id = ServiceContext.getServiceContext().getUserId();

		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);

		String select = "";
		select += "SELECT a.id,start_province,start_city,start_district,end_province,end_city,";
		select += "end_district,cargo_desc,cargo_type,car_length,cargo_weight,price,ship_type,";
		select += "car_type,cargo_photo1,cargo_photo2,cargo_photo3,loading_time,cargo_remark,";
		select += "validate_time,user_bond,push_drvier_count,vie_driver_count,a.create_time , b.name as user_name, ";
		select += "b.phone as user_phone, b.id as user_id,b.company_name";

		String sqlExceptSelect = " from logistics_order a left join logistics_user b on a.user_id = b.id";
		sqlExceptSelect += " where a.driver_id = ? and  a.status=? order by a.id desc ";
		Page<Record> pageObj = Db.paginate(page, pageSize, select, sqlExceptSelect, driver_id , Order.STATUS_FINISH);
		List<Record> list = pageObj.getList();

		for (Record record : list) {
			Record replyRecord = Db.findFirst("select * from logistics_user_reply where order_id =? and driver_id = ?" , record.getInt("id") , driver_id);
			if(replyRecord != null){
				record.set("reply_flag", 1);
				record.set("score1", replyRecord.getInt("score1"));
				record.set("score2", replyRecord.getInt("score2"));
				record.set("score3", replyRecord.getInt("score3"));
				record.set("reply_content", replyRecord.getStr("reply_content"));
				record.set("reply_time", replyRecord.getTimestamp("reply_time"));
			}else{
				record.set("reply_flag", 0);
				record.set("score1", -1);
				record.set("score2", -1);
				record.set("score3", -1);
				record.set("reply_content", "");
				record.set("reply_time", "");
			}
			//卸货时间
			Record orderStatus = Db.findFirst("select * from logistics_order_status where order_id =? and status = ?" , record.getInt("id") , OrderExecute.STATUS_DISBURDEN );
			if(orderStatus != null){
				record.set("disburden_time", orderStatus.getTimestamp("create_time"));
			}
			else{
				record.set("disburden_time", 0);
			}
		}


		Dict.fillDictToRecords(list);
		return Result.success().data("items", list);
	}

	private static Order getAndVerifyOrder(int orderId) {
		ServiceContext.assertLoginAsDriver();
		Order order = Order.dao.findById(orderId);
		if(order == null) {
			throw new BusinessException("货单" + orderId + "不存在");
		}

		//校验订单权限
		final int currentDriverId = ServiceContext.getServiceContext().getUserId();
		if(!order.isDriver(currentDriverId)) {
			String msg = StrUtil.format("[%s]没有订单[%s]权限", ServiceContext.getServiceContext().getPhone(), orderId);
			ServiceLog.error(msg).save();
			throw new BusinessException(msg);
		}

		return order;
	}
}
