package com.maogousoft.wuliu.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.common.json.Param;
import com.maogousoft.wuliu.common.json.Result;
import com.maogousoft.wuliu.common.syslog.ServiceLog;
import com.maogousoft.wuliu.common.util.StrUtil;
import com.maogousoft.wuliu.controller.ServiceContext;
import com.maogousoft.wuliu.domain.Dict;
import com.maogousoft.wuliu.domain.Driver;
import com.maogousoft.wuliu.domain.DriverReply;
import com.maogousoft.wuliu.domain.Order;
import com.maogousoft.wuliu.domain.OrderVie;
import com.maogousoft.wuliu.service.image.FileInfo;
import com.maogousoft.wuliu.service.image.ImageService;

/**
 * 货单相关服务
 * @author yangfan(kenny0x00@gmail.com) 2013-4-6 上午11:58:40
 */
public class OrderService {

	private static String SELECT_ALL_ORDER_FIELD = "";
	static {
		SELECT_ALL_ORDER_FIELD += "SELECT id,start_province,start_city,start_district,end_province,end_city,";
		SELECT_ALL_ORDER_FIELD += "end_district,cargo_desc,cargo_type,car_length,cargo_weight,price,ship_type,";
		SELECT_ALL_ORDER_FIELD += "car_type,cargo_photo1,cargo_photo2,cargo_photo3,loading_time,cargo_remark,";
		SELECT_ALL_ORDER_FIELD += "validate_time,user_bond,push_drvier_count,vie_driver_count,create_time";
	}

	public static Object publish_order(Param param) {
		ServiceContext.assertLoginAsUser();

		Integer start_province = param.getInteger("start_province");
		Integer start_city = param.getInteger("start_city");
		Integer start_district = param.getInteger("start_district");
		Integer end_province = param.getInteger("end_province");
		Integer end_city = param.getInteger("end_city");
		Integer end_district = param.getInteger("end_district");
		String cargo_desc = param.getRequiredString("cargo_desc");
		Integer cargo_type = param.getInteger("cargo_type");
		Dict.verifyDict("cargo_type", cargo_type);
		Double car_length = param.getDouble("car_length");
		Integer cargo_weight = param.getInteger("cargo_weight");
		Double price = param.getDouble("price");
		Integer ship_type = param.getInteger("ship_type");
		Dict.verifyDict("ship_type", ship_type);
		Integer car_type = param.getInteger("car_type");
		Dict.verifyDict("car_type", car_type);
		byte[] cargo_photo1 = param.getBytesFromBase64("cargo_photo1");
		byte[] cargo_photo2 = param.getBytesFromBase64("cargo_photo2");
		byte[] cargo_photo3 = param.getBytesFromBase64("cargo_photo3");
		Date loading_time = param.getDate("loading_time");
		String cargo_remark = param.getString("cargo_remark");
		long validate_time = param.getRequiredLong("validate_time");
		int user_bond = param.getIntValue("user_bond");
		double user_proportion = param.getDoubleValue("user_proportion");

		Order order = new Order();
		order.set("user_id", ServiceContext.getServiceContext().getUserId());
		order.set("start_province", start_province);
		order.set("start_city", start_city);
		order.set("start_district", start_district);
		order.set("end_province", end_province);
		order.set("end_city", end_city);
		order.set("end_district", end_district);
		order.set("cargo_desc", cargo_desc);
		order.set("cargo_type", cargo_type);
		order.set("car_length", car_length);
		order.set("cargo_weight", cargo_weight);
		order.set("price", price);
		order.set("ship_type", ship_type);
		order.set("car_type", car_type);
		order.set("loading_time", loading_time);
		order.set("cargo_remark", cargo_remark);
		order.set("validate_time", new Date(System.currentTimeMillis() + validate_time));//有效日期为当前时间+有效时间数量
		order.set("user_bond", user_bond);
		order.set("user_proportion", user_proportion);
		order.set("create_time", new Date());

		//照片相关
		FileInfo cargo_photo1Info = ImageService.saveImage(cargo_photo1);
		FileInfo cargo_photo2Info = ImageService.saveImage(cargo_photo2);
		FileInfo cargo_photo3Info = ImageService.saveImage(cargo_photo3);
		if(cargo_photo1Info != null) {
			order.set("cargo_photo1", cargo_photo1Info.getVirtualUrl());
		}
		if(cargo_photo2Info != null) {
			order.set("cargo_photo2", cargo_photo2Info.getVirtualUrl());
		}
		if(cargo_photo3Info != null) {
			order.set("cargo_photo3", cargo_photo3Info.getVirtualUrl());
		}
		//推送给相关司机
		List<Driver> pushDrivers = pushToDriver(order);
		order.set("push_drvier_count", pushDrivers.size());
		order.save();

		ServiceLog.info("货主[%s]发布货源[%s]成功", ServiceContext.getServiceContext().getPhone(), order.get("id")).save();

		return Result.success();
	}

	private static List<Driver> pushToDriver(Order order) {
		// TODO 实现推送给司机
		return new ArrayList<Driver>();
	}

	/**
	 * 待定货单列表查询
	 * @param param
	 * @return
	 */
	public static Object query_order(Param param) {
		ServiceContext.assertLoginAsUser();

		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);

		int user_id = ServiceContext.getServiceContext().getUserId();
		//-1已删除,0-已创建,1-审核通过(进行中)，2-审核未通过，3-已中标，4-已取消，99-已完成
		String sqlExceptSelect = "from logistics_order where (status=0 or status=1) and user_id=? order by id desc";
		Page<Record> pageObj = Db.paginate(page, pageSize, SELECT_ALL_ORDER_FIELD, sqlExceptSelect, user_id);
		List<Record> list = pageObj.getList();
		Dict.fillDictToRecords(list);
		return Result.success().data("items", list);
	}

	/**
	 * 待定货单详细
	 * @param param
	 * @return
	 */
	public static Object get_order_detail(Param param) {
		ServiceContext.assertLoginAsUser();

		int orderId = param.getRequiredInt("order_id");
		Order order = getAndVerifyOrder(orderId);

		//只能查询0-已创建,1-审核通过(进行中)
		if(!order.isStauts(Order.STATUS_CREATED) && !order.isStauts(Order.STATUS_PASS)) {
			throw new BusinessException("货单" + orderId + "不处于待定货单状态,status=" + order.getInt("status"));
		}

		Dict.fillDictToModel(order);
		order.remove("status");
		return Result.success().data("item", order);
	}

	/**
	 * 待定货单详细-司机列表
	 * @param param
	 * @return
	 */
	public static Object get_order_detail_driver_list(Param param) {
		ServiceContext.assertLoginAsUser();

		int orderId = param.getRequiredInt("order_id");
		Order order = getAndVerifyOrder(orderId);

		//只能查询0-已创建,1-审核通过(进行中)
		if(!order.isStauts(Order.STATUS_CREATED) && !order.isStauts(Order.STATUS_PASS)) {
			throw new BusinessException("货单" + orderId + "不处于待定货单状态,status=" + order.getInt("status"));
		}

		String sql = "select driver_id,plate_number,id_card,registration,license,car_photo1,car_photo2,car_photo3,car_type,car_length,((score1+score2+score3)/3) AS score";
		sql += " FROM logistics_order_vie a";
		sql += " LEFT JOIN logistics_driver b ON b.id=a.driver_id";
		sql += " WHERE a.order_id=? and a.status=0";//0-竞标中
		List<Record> driverList = Db.find(sql, orderId);
		Dict.fillDictToRecords(driverList);
		return Result.success().data("items", driverList);
	}

	/**
	 * 待定货单加价
	 * @param param
	 * @return
	 */
	public static Object add_order_price(Param param) {
		ServiceContext.assertLoginAsUser();

		//TODO 已有抢单司机后,就不能加价

		int orderId = param.getRequiredInt("order_id");
		int price = param.getRequiredInt("price");

		Order order = getAndVerifyOrder(orderId);

		//只能查询0-已创建,1-审核通过(进行中)
		if(!order.isStauts(Order.STATUS_CREATED) && !order.isStauts(Order.STATUS_PASS)) {
			throw new BusinessException("货单" + orderId + "不处于待定货单状态,status=" + order.getInt("status"));
		}

		if(OrderVie.dao.hasVieByOrderId(orderId)) {
			throw new BusinessException("货单" + orderId + "已有人投标,不能调整价格");
		}

		//更新订单价格
		order.set("price", price);
		order.update();

		ServiceLog.info("货主[%s]调整货单价格为[%s]", ServiceContext.getServiceContext().getPhone(), price).save();

		return Result.success();
	}

	private static Order getAndVerifyOrder(int orderId) {
		Order order = Order.dao.findById(orderId);
		if(order == null) {
			throw new BusinessException("货单" + orderId + "不存在");
		}

		//校验订单权限
		final int currentUserId = ServiceContext.getServiceContext().getUserId();
		if(!order.isOwner(currentUserId)) {
			String msg = StrUtil.format("[%s]没有订单[%s]权限", ServiceContext.getServiceContext().getPhone(), orderId);
			ServiceLog.error(msg).save();
			throw new BusinessException(msg);
		}

		return order;
	}

	public static Object cancel_order(Param param) {
		ServiceContext.assertLoginAsUser();

		int orderId = param.getRequiredInt("order_id");

		Order order = getAndVerifyOrder(orderId);

		//取消订单
		order.set("status", 4);
		order.update();

		return Result.success();
	}

	public static Object accept_order(Param param) {
		ServiceContext.assertLoginAsUser();

		int orderId = param.getRequiredInt("order_id");
		int driverId = param.getRequiredInt("driver_id");

		Order order = getAndVerifyOrder(orderId);

		//查找投标的司机信息
		OrderVie orderVie = OrderVie.dao.findFirst("select id,order_id,driver_id,driver_proportion,driver_bond,status from logistics_order_vie where status<>1 and order_id=? and driver_id=?", orderId, driverId);
		if(orderVie == null) {
			String msg = StrUtil.format("无法找到订单[%s]对应投标记录,司机编号[%s]", orderId, driverId);
			ServiceLog.error(msg).save();
			throw new BusinessException(msg);
		}
		if(orderVie.getInt("status") == 2) {//2-退出竞标
			String msg = StrUtil.format("订单[%s]投标已撤销,司机编号[%s]", orderId, driverId);
			ServiceLog.error(msg).save();
			throw new BusinessException(msg);
		}

		//设置为中标
		orderVie.set("status", 1);
		orderVie.update();

		//修改订单数据
		order.set("status", 3);//已中标
		order.set("driver_id", orderVie.getInt("driver_id"));
		order.set("driver_proportion", orderVie.getInt("driver_proportion"));
		order.set("driver_bond", orderVie.getBigDecimal("driver_bond"));
		order.set("deal_time", new Date());
		order.update();

		ServiceLog.info("订单[%s]已成交,司机ID[%s]", orderId, driverId).save();

		return Result.success();
	}

	public static Object query_in_shipping(Param param) {
		ServiceContext.assertLoginAsUser();

		int page = param.getIntValue("page", 1);
		int page_size = param.getIntValue("page_size", 10);
		int user_id = ServiceContext.getServiceContext().getUserId();
		//获取在途货物
		String select = "select a.id,a.start_province,a.start_city,a.start_district,a.end_province,a.end_city,a.end_district,b.status as order_status,plate_number,c.phone as driver_phone,a.cargo_weight,a.cargo_desc,a.loading_time,1000 as cost_time";
		String sqlExceptSelect = " from logistics_order a";
		sqlExceptSelect += " left join logistics_order_status b on b.order_id=a.id";
		sqlExceptSelect += " left join logistics_driver c on c.id=a.driver_id";
		sqlExceptSelect += " where a.status=3 and a.user_id=?"; //3-已中标
		Page<Record> pageObj = Db.paginate(page, page_size, select, sqlExceptSelect, user_id);
		List<Record> list = pageObj.getList();
		Dict.fillDictToRecords(list);
		return Result.success().data("items", list);
	}

	public static Object rating_to_driver(Param param) {
		ServiceContext.assertLoginAsUser();

		int order_id = param.getRequiredInt("order_id");
		String reply_content = param.getString("reply_content");
		int score1 = param.getRequiredInt("score1");
		int score2 = param.getRequiredInt("score2");
		int score3 = param.getRequiredInt("score3");

		//获取订单信息
		Order order = getAndVerifyOrder(order_id);
		int driverId = order.getInt("driver_id");
		DriverReply reply = new DriverReply();
		reply.set("driver_id", driverId);
		reply.set("score1", score1);
		reply.set("score2", score2);
		reply.set("score3", score3);
		reply.set("reply_content", reply_content);
		reply.set("order_id", order_id);
		reply.set("reply_time", new Date());
		reply.save();

		ServiceLog.info("货主[%s]对司机评价[%s]完成", order.getInt("user_id"), driverId).save();

		return Result.success();
	}

	public static Object get_id_card_info(Param param) {
		ServiceContext.assertLoginAsUser();
		throw new BusinessException("未实现的接口.");
	}

	public static Object get_license_info(Param param) {
		ServiceContext.assertLoginAsUser();
		throw new BusinessException("未实现的接口.");
	}

	public static Object get_registration_info(Param param) {
		ServiceContext.assertLoginAsUser();
		throw new BusinessException("未实现的接口.");
	}
}
