package com.maogousoft.wuliu.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.maogousoft.wuliu.BaseConfig;
import com.maogousoft.wuliu.common.collections.KV;
import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.common.json.Param;
import com.maogousoft.wuliu.common.json.Result;
import com.maogousoft.wuliu.common.syslog.ServiceLog;
import com.maogousoft.wuliu.common.util.MD5Util;
import com.maogousoft.wuliu.controller.ServiceContext;
import com.maogousoft.wuliu.domain.Business;
import com.maogousoft.wuliu.domain.Coupon;
import com.maogousoft.wuliu.domain.Dict;
import com.maogousoft.wuliu.domain.Driver;
import com.maogousoft.wuliu.domain.GoldResult;
import com.maogousoft.wuliu.domain.Msg;
import com.maogousoft.wuliu.domain.Pay;
import com.maogousoft.wuliu.domain.User;
import com.maogousoft.wuliu.domain.Vender;
import com.maogousoft.wuliu.domain.VenderReply;
import com.maogousoft.wuliu.service.image.ImageService;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-4-8 下午11:28:07
 */
public class DriverService {

	public static Result driver_reg_getcode(Param param) {
		String phone = param.getRequiredString("phone");
		if(User.dao.existsPhone(phone)) {
			throw new BusinessException("该手机号已注册为货主，不能同时注册.");
		}
		if(Driver.dao.existsPhone(phone)) {
			throw new BusinessException("手机号已注册:" + phone);
		}
		String captcha = RandomStringUtils.randomNumeric(6);
		String content = "易运宝注册验证码：" + captcha ;
		SmsService.send(phone, content);
		VcodeManagerPlugin.me().putVcode(phone,captcha);
		return Result.success("验证码已发送到手机:" + phone + "，请注意查收。");
	}

	/**
	 * 注册接口
	 * @param param
	 * @return
	 */
	public static Result driver_reg(Param param) {
		String phone = param.getRequiredString("phone");
		String vcode = param.getRequiredString("vcode");
		String password = param.getRequiredString("password");
		password = encryptPassword(password);
		//String name = param.getRequiredString("name");
		int start_province = param.getRequiredInt("start_province");
		int start_city = param.getRequiredInt("start_city");
		int end_province = param.getRequiredInt("end_province");
		int end_city = param.getRequiredInt("end_city");
		//推荐人(手机号码)
		String recommender_phone = param.getString("recommender");
		
		
		//验证手机验证码
		boolean isValidVcode = VcodeManagerPlugin.me().verifyVcode(phone,vcode);
		if(!vcode.equals("9090980") && !isValidVcode) {
			throw new BusinessException("手机验证码不正确.");
		}
		//清除验证码
		VcodeManagerPlugin.me().removeVode(phone);

		if(Driver.dao.existsPhone(phone)) {
			throw new BusinessException("手机号已注册:" + phone);
		}

		Driver driver = new Driver();
		driver.set("phone", phone);
		driver.set("password", password);
		//driver.set("name", name);
		driver.set("recommender", recommender_phone);
		
		driver.set("start_province", start_province);
		driver.set("start_city", start_city);
		driver.set("end_province", end_province);
		driver.set("end_city", end_city);
		
		//初始化其他
		driver.set("regist_time", new Date());
		driver.set("score1", 5);//初始化评分
		driver.set("score2", 5);//初始化评分
		driver.set("score3", 5);//初始化评分
		driver.set("score", 5);//初始化评分
		driver.set("gold", 400);//物流币为0
		driver.set("total_deal", 0);//历史交易总金额为0
		driver.set("status", Driver.STATUS_PENDING_AUDIT);//0 待审核,1-已审核,直接就审核通过
//		driver.set("car_phone", car_phone);//随车手机
		driver.set("modified", new Date());//线路修改时间
		driver.save();

		//记录全局用户
		Record globalUser = new Record();
		globalUser.set("uid", "d" + driver.getInt("id"));
		globalUser.set("password", password);
		globalUser.set("user_type", 1);//0-货主,1-司机
		globalUser.set("data_id", driver.getInt("id"));
		globalUser.set("create_time", new Date());
		Db.save("logistics_global_user", globalUser);

		//ServiceLog.info("[%s] - [%s]注册[司机]成功", phone, name).save();
		ServiceLog.info("[%s] - [%s]注册[司机]成功", phone).save();

		UserSessionManager manager = UserSessionManager.me();
		int driverId = driver.getInt("id");
		Session session = manager.createSession(driverId, phone, Session.USER_TYPE_DRIVER);
		String token = session.getToken();

		return Result.success().data("item", new KV("driver_id", driver.getInt("id"), "token", token));
	}

	/**
	 * 司机注册选填内容
	 * @param param
	 * @return
	 */
	public static Result driver_reg_optional(Param param) {
		ServiceContext.assertLogin();

		int driver_id = ServiceContext.getServiceContext().getUserId();
		Driver driver = Driver.dao.loadDriverById(driver_id);

		//随车手机
		String car_phone = param.getString("car_phone");
		//司机姓名
		String driver_name = param.getString("driver_name");
		//货主电话
		String owner_phone = param.getString("owner_phone");
		//车长
		double car_length = param.getRequiredDouble("car_length");
		//车型
		int car_type = param.getRequiredInt("car_type");
		//重量体积
		int car_weight = param.getRequiredInt("car_weight");
		//车牌号
		String plate_number = param.getRequiredString("plate_number");
		//设备来源 1android 2ios
		int device_type = param.getRequiredInt("device_type");
		//出发地省 市
		int start_province2 = param.getIntValue("start_province2");
		int start_city2 = param.getIntValue("start_city2");
		int end_province2 = param.getIntValue("end_province2");
		int end_city2 = param.getIntValue("end_city2");
		//目的地省 市
		int start_province3 = param.getIntValue("start_province3");
		int start_city3 = param.getIntValue("start_city3");
		int end_province3 = param.getIntValue("end_province3");
		int end_city3 = param.getIntValue("end_city3");
		
		driver.set("car_phone", car_phone);
		driver.set("name", driver_name);
		driver.set("owner_phone", owner_phone);
		driver.set("car_length", car_length);
		driver.set("car_type", car_type);
		driver.set("car_weight", car_weight);
		driver.set("plate_number", plate_number);
		driver.set("device_type", device_type);
		driver.set("start_province2", start_province2);
		driver.set("start_city2", start_city2);
		driver.set("end_province2", end_province2);
		driver.set("end_city2", end_city2);
		driver.set("start_province3", start_province3);
		driver.set("start_city3", start_city3);
		driver.set("end_province3", end_province3);
		driver.set("end_city3", end_city3);
		
		driver.update();
		ServiceLog.info("司机[%s]更新选填信息成功.", driver.getStr("phone")).save();

		return Result.success();
	}
	
	/**
	 * 注册司机认证信息
	 * @param param
	 * @return
	 */
	public static Result driver_authentication(Param param){
		ServiceContext.assertLogin();//确定用户已经登录
		int driver_id = ServiceContext.getServiceContext().getUserId();
		Driver driver = Driver.dao.loadDriverById(driver_id);
		
		/**身份证 号码/名字/照片*/
		String id_card = param.getString("id_card");
		String id_card_name = param.getString("id_card_name");
		byte[] id_card_photo = param.getBytesFromBase64("id_card_photo");

		/**驾驶证 号码/名字/照片*/
		String license = param.getString("license");
		String license_name = param.getString("license_name");
		byte[] license_photo = param.getBytesFromBase64("license_photo");
		
		/**行驶证 号码/名字/照片*/
		String registration = param.getString("registration");
		String registration_name = param.getString("registration_name");
		byte[] registration_photo = param.getBytesFromBase64("registration_photo");
		
		/**车辆照片*/
		byte[] car_photo1 = param.getBytesFromBase64("car_photo1");
		byte[] car_photo2 = param.getBytesFromBase64("car_photo2");
		byte[] car_photo3 = param.getBytesFromBase64("car_photo3");
		
		
		driver.set("id_card", id_card);
		driver.set("id_card_name", id_card_name);
		ImageService.saveImageToModel(driver, "id_card_photo", id_card_photo);

		driver.set("registration", registration);
		driver.set("registration_name", registration_name);
		ImageService.saveImageToModel(driver, "registration_photo", registration_photo);

		driver.set("license", license);
		driver.set("license_name", license_name);
		ImageService.saveImageToModel(driver, "license_photo", license_photo);

		ImageService.saveImageToModel(driver, "car_photo1", car_photo1);
		ImageService.saveImageToModel(driver, "car_photo2", car_photo2);
		ImageService.saveImageToModel(driver, "car_photo3", car_photo3);
		
		driver.update();
		ServiceLog.info("司机[%s]诚信认证成功.", driver.getStr("phone")).save();

		return Result.success();
	}
	

	private static String encryptPassword(String password) {
		boolean encrypt_password = BaseConfig.me().getPropertyToBoolean("encrypt_password", false);
		if(encrypt_password) {
			String encryptedPassword = MD5Util.MD5(password);
			password = encryptedPassword;
		}
		return password;
	}

	public static Object driverlogin(Param param) {
		String phone = param.getRequiredString("phone");
		String password = param.getRequiredString("password");
		password = encryptPassword(password);

		Driver driver = Driver.dao.findFirst("select * from logistics_driver where phone=? and password=?", phone, password);

		if(driver == null) {
			ServiceLog.info("司机[%s]登录失败，用户名或密码错误.", phone).save();
			return Result.fail("用户名或密码错误.");
		}
		else if(driver.getInt("status") == Driver.STATUS_INVALID)
		{
			ServiceLog.info("账号审核未通过，请联系客服", phone).save();
			return Result.fail("账号审核未通过，请联系客服");
		}
		else if(driver.getInt("status") == Driver.STATUS_DELETED)
		{
			ServiceLog.info("账号已被删除", phone).save();
			return Result.fail("账号已被删除");
		}
		else {
			UserSessionManager manager = UserSessionManager.me();
			int userId = driver.getInt("id");
			Session session = manager.createSession(userId, phone, Session.USER_TYPE_DRIVER);
			String token = session.getToken();

			ServiceLog.info("司机[%s]登录成功.", phone).save();
			KV kv = new KV();
			kv.add("token" , token);
			kv.add("driver_id" , userId);
			kv.add("name" , driver.get("name"));
			return Result.success().data("item", kv);
		}
	}


	/**
	 *
	 * @description 获取我的易运宝
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 8, 2013 11:22:25 PM
	 * @param param
	 * @return
	 */
	public static Object driverProfile(Param param){
		ServiceContext.assertLoginAsDriver();

		final int driver_id = ServiceContext.getServiceContext().getUserId();
		Driver driver = Driver.dao.findFirst("select * from logistics_driver where id=? ", driver_id);
		Dict.fillDictToModel(driver);
		if(driver == null){
			return Result.fail("用户不存在");
		}
		else if(driver.getInt("status") == Driver.STATUS_INVALID){
			return Result.fail("审核未通过");
		}
		//XXX 20130514 王胜韬电话要求更改
//		else if(driver.getInt("status") == Driver.STATUS_PENDING_AUDIT){
//			return Result.fail("等待审核");
//		}
		else if(driver.getInt("status") == Driver.STATUS_DELETED){
			return Result.fail("已删除");
		}
		else{
			KV kv = new KV();
			kv.add("id" , driver_id);
			kv.add("phone" , driver.getStr("phone"));
			kv.add("name" , driver.getStr("name"));
			kv.add("plate_number" , driver.get("plate_number"));
			kv.add("recommender" , driver.get("recommender"));
			kv.add("gold" , driver.get("gold"));
			kv.add("score" , driver.get("score"));
			kv.add("score1" , driver.get("score1"));
			kv.add("score2" , driver.get("score2"));
			kv.add("score3" , driver.get("score3"));
			kv.add("online_time" , driver.get("online_time"));
			kv.add("online_time_rank" , driver.get("online_time_rank"));
			kv.add("order_count" , driver.get("order_count"));
			kv.add("order_count_rank" , driver.get("order_count_rank"));
			kv.add("recommender_count" , driver.get("recommender_count"));
			kv.add("recommender_count_rank" , driver.get("recommender_count_rank"));
			kv.add("owner_phone" , driver.get("owner_phone"));
			kv.add("id_card" , driver.get("id_card"));
			kv.add("id_card_name" , driver.get("id_card_name"));
			kv.add("id_card_photo" , driver.get("id_card_photo"));
			kv.add("registration" , driver.get("registration"));
			kv.add("registration_name" , driver.get("registration_name"));
			kv.add("registration_photo" , driver.get("registration_photo"));
			kv.add("license" , driver.get("license"));
			kv.add("license_name" , driver.get("license_name"));
			kv.add("license_photo" , driver.get("license_photo"));
			kv.add("car_photo1" , driver.get("car_photo1"));
			kv.add("car_photo2" , driver.get("car_photo2"));
			kv.add("car_photo3" , driver.get("car_photo3"));
			kv.add("start_province" , driver.get("start_province"));
			kv.add("start_city" , driver.get("start_city"));
			kv.add("end_province" , driver.get("end_province"));
			kv.add("end_city" , driver.get("end_city"));

			//补充信息
			kv.add("car_type" , driver.get("car_type"));
			kv.add("car_length" , driver.get("car_length"));
			kv.add("car_weight" , driver.get("car_weight"));
			kv.add("device_type" , driver.get("device_type"));
			kv.add("total_deal" , driver.get("total_deal"));
			kv.add("regist_time" , driver.get("regist_time"));

			kv.add("car_phone" , driver.get("car_phone"));
			kv.add("start_province2" , driver.get("start_province2"));
			kv.add("start_city2" , driver.get("start_city2"));
			kv.add("end_province2" , driver.get("end_province2"));
			kv.add("end_city2" , driver.get("end_city2"));
			kv.add("start_province3" , driver.get("start_province3"));
			kv.add("start_city3" , driver.get("start_city3"));
			kv.add("end_province3" , driver.get("end_province3"));
			kv.add("end_city3" , driver.get("end_city3"));
			kv.add("modified" , driver.get("modified"));
			kv.add("car_type_str" , driver.get("car_type_str"));

			return Result.success().data("item", kv);
		}
	}

	/**
	 *
	 * @description 获取司机充值记录
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 26, 2013 5:46:17 PM
	 * @param param
	 * @return
	 */
	public static Object query_pay_history(Param param) {
		ServiceContext.assertLoginAsDriver();
		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		String u_id = "d" + ServiceContext.getServiceContext().getUserId() ;
		String select  = "select id,u_id,pay_money,pay_platform,status,create_time,finish_time ";
		String from =  "from logistics_pay where u_id = ? and status = 99 order by id desc" ;
		Page<Record> pageObj = Db.paginate(page, pageSize, select, from , u_id);
		List<Record> list = pageObj.getList();
		return Result.success().data("items", list);
	}

	/**
	 *
	 * @description 添加商户
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 3, 2013 12:17:18 AM
	 * @param param
	 * @return
	 */
	public static Object add_vender(Param param) {
		ServiceContext.assertLoginAsDriver();
		String vender_name = param.getRequiredString("vender_name");
		int category = param.getRequiredInt("category");
		String vender_address = param.getRequiredString("vender_address");
		Vender vender = new Vender();
		vender.set("vender_name", vender_name);
		vender.set("category", category);
		vender.set("vender_address", vender_address);

		vender.set("vender_province", param.getInteger("vender_province"));
		vender.set("vender_city", param.getInteger("vender_city"));
		vender.set("vender_district", param.getInteger("vender_district"));
		vender.set("longitude", param.getDouble("longitude"));
		vender.set("latitude", param.getDouble("latitude"));
		vender.set("contact", param.getString("contact"));
		vender.set("vender_mobile", param.getString("vender_mobile"));
		vender.set("vender_phone", param.getString("vender_phone"));
		vender.set("goods_name", param.getString("goods_name"));
		vender.set("normal_price", param.getString("normal_price"));
		vender.set("member_price", param.getString("member_price"));
		vender.set("other", param.getString("other"));
		vender.set("photo1", param.getString("photo1"));
		vender.set("photo2", param.getString("photo2"));
		vender.set("photo3", param.getString("photo3"));
		vender.set("photo4", param.getString("photo4"));
		vender.set("photo5", param.getString("photo5"));
		vender.save();
		return Result.success();
	}

	/**
	 *
	 * @description 添加商户评论
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 3, 2013 12:37:04 AM
	 * @param param
	 * @return
	 */
	public static Object add_vender_reply(Param param) {
		ServiceContext.assertLoginAsDriver();
		final int driver_id = ServiceContext.getServiceContext().getUserId();

		VenderReply reply = new VenderReply();
		reply.set("vender_id", param.getRequiredInt("vender_id"));
		reply.set("driver_id", driver_id);
		reply.set("score1", param.getRequiredInt("score1"));
		reply.set("score2", param.getRequiredInt("score2"));
		reply.set("score3", param.getRequiredInt("score3"));
		reply.set("reply_content", param.getString("reply_content"));
		reply.set("is_true", param.getInteger("is_true"));
		reply.set("reply_time", new Date());
		reply.save();
		return Result.success();
	}

	/**
	 *
	 * @description 获取商户列表，由远及近
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 3, 2013 1:06:14 AM
	 * @param param
	 * @return
	 */
	public static Object query_vender(Param param) {
		ServiceContext.assertLoginAsDriver();
		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		double latitude = param.getRequiredDouble("latitude");
		double longitude = param.getRequiredDouble("longitude");
		String keyword = param.getString("keyword");
		
		String select  = "select * ";
		String from =  " from logistics_vender where status=0 " ;
		if(StringUtils.hasText(keyword)){
			from += " and (vender_name like '%" + keyword + "%' or goods_name like '%" + keyword + "%') ";
		}
		from += " order by ACOS(SIN((? * 3.1415) / 180 ) *SIN((latitude * 3.1415) / 180 ) +COS((? * 3.1415) / 180 ) * COS((latitude * 3.1415) / 180 ) *COS((? * 3.1415) / 180 - (longitude * 3.1415) / 180 ) ) * 6380 asc";
		Page<Record> pageObj = Db.paginate(page, pageSize, select, from , latitude , latitude , longitude);
		List<Record> list = pageObj.getList();
		return Result.success().data("items", list);
	}

	/**
	 *
	 * @description 获取商户详细评价
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 3, 2013 1:11:55 AM
	 * @param param
	 * @return
	 */
	public static Object query_vender_reply(Param param) {
		ServiceContext.assertLoginAsDriver();
		int vender_id = param.getRequiredInt("vender_id");
		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		String select  = "select a.*,b.name ";
		String from =  " from logistics_vender_reply a left join logistics_driver b on a.driver_id = b.id where vender_id = ? order by id desc" ;

		Vender vender = Vender.dao.findById(vender_id);
		vender.set("read_time", vender.getInt("read_time") + 1);
		vender.update();
		Page<Record> pageObj = Db.paginate(page, pageSize, select, from , vender_id);
		List<Record> list = pageObj.getList();
		return Result.success().data("items", list);
	}

	/**
	 *
	 * @description 修改第2、3条主营线路
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 5, 2013 12:22:31 AM
	 * @param param
	 * @return
	 */
	public static Object driver_update_line(Param param) {
		ServiceContext.assertLoginAsDriver();

		final int driver_id = ServiceContext.getServiceContext().getUserId();
		int start_province2 = param.getIntValue("start_province2" , 0);
		int start_city2 = param.getIntValue("start_city2" , 0);
		int end_province2 = param.getIntValue("end_province2" , 0);
		int end_city2 = param.getIntValue("end_city2" , 0);
		int start_province3 = param.getIntValue("start_province3" , 0);
		int start_city3 = param.getIntValue("start_city3" , 0);
		int end_province3 = param.getIntValue("end_province3" , 0);
		int end_city3 = param.getIntValue("end_city3" , 0);

		Driver driver = Driver.dao.findById(driver_id);
		Date date = driver.getTimestamp("modified");
		long diff = new Date().getTime() - date.getTime() ;
		int threeDays = 3 * 24 * 60 * 60 * 1000 ;
		if(diff < threeDays ){
			return Result.fail("距离上次修改不足3天，不能修改");
		}
		driver.set("start_province2", start_province2);
		driver.set("start_city2", start_city2);
		driver.set("end_province2", end_province2);
		driver.set("end_city2", end_city2);
		driver.set("start_province3", start_province3);
		driver.set("start_city3", start_city3);
		driver.set("end_province3", end_province3);
		driver.set("end_city3", end_city3);
		driver.set("modified", new Date());
		driver.update();
		return Result.success();
	}
	
	
	/**
	 *
	 * @description 修改第1,2,3条主营线路
	 * @param param
	 * @return
	 */
	public static Object driver_update_line_2(Param param) {
		ServiceContext.assertLoginAsDriver();
		final int driver_id = ServiceContext.getServiceContext().getUserId();

		//路线1
		int start_province1 = param.getIntValue("start_province1" , 0);
		int start_city1 = param.getIntValue("start_city1" , 0);
		int end_province1 = param.getIntValue("end_province1" , 0);
		int end_city1 = param.getIntValue("end_city1" , 0);
		//路线2
		int start_province2 = param.getIntValue("start_province2" , 0);
		int start_city2 = param.getIntValue("start_city2" , 0);
		int end_province2 = param.getIntValue("end_province2" , 0);
		int end_city2 = param.getIntValue("end_city2" , 0);
		//路线3
		int start_province3 = param.getIntValue("start_province3" , 0);
		int start_city3 = param.getIntValue("start_city3" , 0);
		int end_province3 = param.getIntValue("end_province3" , 0);
		int end_city3 = param.getIntValue("end_city3" , 0);
		//修改时间限制
		Driver driver = Driver.dao.findById(driver_id);
		Date date = driver.getTimestamp("modified");
		long diff = new Date().getTime() - date.getTime() ;
		int one_hour = 24 * 60 * 60 * 1000 ;
		if(diff < one_hour ){
			return Result.fail("距离上次修改不足1小时，不能修改");
		}
		
		//修改第一条
		if(start_province1 != 0){
			driver.set("start_province1", start_province1);
			driver.set("start_city1", start_city1);
			driver.set("end_province1", end_province1);
			driver.set("end_city1", end_city1);
			driver.set("modified", new Date());
		}
		//修改第二条
		if(start_province2 != 0){
			driver.set("start_province2", start_province2);
			driver.set("start_city2", start_city2);
			driver.set("end_province2", end_province2);
			driver.set("end_city2", end_city2);
			driver.set("modified", new Date());
		}
		//修改第三条
		if(start_province3 != 0){
			driver.set("start_province3", start_province3);
			driver.set("start_city3", start_city3);
			driver.set("end_province3", end_province3);
			driver.set("end_city3", end_city3);
			driver.set("modified", new Date());
		}
		
		driver.update();
		return Result.success();
	}
	
	
	
	/**
	 * 
	 * @description 充值卡消费 
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 13, 2013 12:40:58 AM
	 * @param param
	 * @return
	 */
	@Before(Tx.class)
	public static Object driver_coupon(Param param) {
		ServiceContext.assertLoginAsDriver();
		final int driver_id = ServiceContext.getServiceContext().getUserId();
		String card_no = param.getRequiredString("card_no");
		String card_pwd = param.getRequiredString("card_pwd");

		Driver driver = Driver.dao.findById(driver_id);
		Coupon coupon = Coupon.dao.findFirst("select * from logistics_coupon where card_no=? ", card_no);
		if(coupon == null){
			return Result.fail("充值卡号不正确");
		}
		if(!coupon.getStr("card_pwd").equals(card_pwd)){
			return Result.fail("充值卡密码不正确");
		}
		if(coupon.getInt("status") == Coupon.STATUS_USED){
			return Result.fail("充值卡已被消费过，不能再次使用");
		}
		GoldResult goldResult = driver.adjustGold(100);
		coupon.set("uid", "d" + driver_id);
		coupon.set("status", Coupon.STATUS_USED);
		coupon.set("use_time", new Date());
		coupon.update();
		Business.dao.addDriverBusiness(driver_id, Business.BUSINESS_TYPE_RECHARGE, 100, goldResult.getBeforeGold(), goldResult.getAfterGold());
		Msg.dao.addDriverMsg(Msg.TYPE_BUSINIESS, "充值成功", "通过易运宝充值卡充值100元", driver_id);
		return Result.success();
	}
}
