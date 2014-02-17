package com.maogousoft.wuliu.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.maogousoft.wuliu.BaseConfig;
import com.maogousoft.wuliu.WuliuConstants;
import com.maogousoft.wuliu.common.collections.KV;
import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.common.json.Param;
import com.maogousoft.wuliu.common.json.Result;
import com.maogousoft.wuliu.common.syslog.ServiceLog;
import com.maogousoft.wuliu.common.util.MD5Util;
import com.maogousoft.wuliu.controller.ServiceContext;
import com.maogousoft.wuliu.domain.Driver;
import com.maogousoft.wuliu.domain.Pay;
import com.maogousoft.wuliu.domain.User;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-4-8 下午11:01:48
 */
public class CommonService {

	private static final Log log = LogFactory.getLog(CommonService.class);

	/**
	 * 字典数据查询
	 * @param param
	 * @return
	 */
	public static Result common_get_dict_list(Param param) {
		String dict_type = param.getString("dict_type");
		List<Record> list;
		if (StringUtils.isBlank(dict_type)) {
			String sql = "select id,name,dict_type,dict_desc from logistics_dict where status=0";
			list = Db.findByCache(WuliuConstants.CACHE_DICT, "__all__", sql);
		} else {
			String sql = "select id,name,dict_type,dict_desc from logistics_dict where status=0 and dict_type=?";
			list = Db.findByCache(WuliuConstants.CACHE_DICT, dict_type, sql,
					dict_type);
		}
		return Result.success().data("items", list);
	}

	/**
	 *
	 * @description 获取账号余额
	 * @author shevliu
	 * @email shevliu@gmail.com May 9, 2013 11:37:02 PM
	 * @param param
	 * @return
	 */
	public static Object get_account_gold(Param param) {
		final int id = ServiceContext.getServiceContext().getUserId();
		int userType = ServiceContext.getServiceContext().getCurrentSession()
				.getUserType();
		if (userType == Session.USER_TYPE_DRIVER) {
			Driver driver = Driver.dao.findById(id);
			Map<String, Double> map = new HashMap<String, Double>();
			map.put("gold", driver.getDouble("gold"));
			return Result.success().data("item", map);
		} else {
			User user = User.dao.findById(id);
			Map<String, Double> map = new HashMap<String, Double>();
			map.put("gold", user.getDouble("gold"));
			return Result.success().data("item", map);
		}
	}

	/**
	 *
	 * @description 意见反馈
	 * @author shevliu
	 * @email shevliu@gmail.com May 9, 2013 11:37:29 PM
	 * @param param
	 * @return
	 */
	public static Result post_feedback(Param param) {

		String phone = param.getRequiredString("phone");
		String suggest_content = param.getRequiredString("suggest_content");
		String sql = "insert into logistics_suggest(phone , suggest_content , status , create_time) values(?,?,?,?)";
		Db.update(sql, phone, suggest_content, 0, new Date());
		return Result.success();
	}

	/**
	 *
	 * @description 获取系统消息,调用时将最后阅读时间置为当前时间
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 9:46:46 PM
	 * @param param
	 * @return
	 */
	public static Object query_message(Param param) {
		final int id = ServiceContext.getServiceContext().getUserId();
		int userType = ServiceContext.getServiceContext().getCurrentSession()
				.getUserType();
		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);

		Date now = new Date();
		String receiverId = "" ;
		if (userType == Session.USER_TYPE_DRIVER) {
			receiverId = "d" + id;
			String sql = "update logistics_driver set last_read_msg = ? where id = ? " ;
			Db.update(sql , now ,id);
		} else {
			receiverId = "u" + id;
			String sql = "update logistics_user set last_read_msg = ? where id = ? " ;
			Db.update(sql , now ,id);
		}
		String select  = "select id, msg_type as category , msg_time as create_time , msg_title as title , msg_content as content  ";
		StringBuffer from = new StringBuffer();
		from.append("from logistics_sys_msg where ( u_id is null  or u_id = ? )  and status=0 order by id desc ") ;

		Page<Record> pageObj = Db.paginate(page, pageSize , select , from.toString() ,receiverId );
		List<Record> list = pageObj.getList();
		return Result.success().data("items", list);
	}

	/**
	 *
	 * @description 获取司机个人信息
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 10:47:20 PM
	 * @param param
	 * @return
	 */
	public static Object get_driver_info(Param param) {
		int driver_id = param.getRequiredInt("driver_id");
		Driver driver = Driver.dao.findById(driver_id);
		return Result.success().data("item", driver);
	}

	/**
	 *
	 * @description 获取货主个人信息
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 10:48:05 PM
	 * @param param
	 * @return
	 */
	public static Object get_user_info(Param param) {
		int user_id = param.getRequiredInt("user_id") ;
		User user = User.dao.findById(user_id) ;
		//XXX 字段变更,不改接口
		Object last_read_msg = user.get("last_read_msg");
		user.remove("last_read_msg");
		user.put("last_read_time", last_read_msg);
		return Result.success().data("item", user);
	}

	/**
	 *
	 * @description 获取货主详细评价记录
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 11:31:26 PM
	 * @param param
	 * @return
	 */
	public static Object get_user_reply(Param param){
		int user_id = param.getRequiredInt("user_id") ;
		String select = "select a.order_id,a.score1,a.score2,a.score3,a.reply_content,a.reply_time ,b.start_province,b.start_city,b.start_district,b.end_province,b.end_city,b.end_district,b.cargo_desc";
		String from = " from logistics_user_reply a left join logistics_order b on a.order_id = b.id where a.user_id = ?";

		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		Page<Record> pageObj = Db.paginate(page, pageSize , select , from.toString() ,user_id );
		List<Record> list = pageObj.getList();
		return Result.success().data("items", list);
	}

	/**
	 *
	 * @description 获取司机详细评价记录
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 11:34:09 PM
	 * @param param
	 * @return
	 */
	public static Object get_driver_reply(Param param){
		int driver_id = param.getRequiredInt("driver_id") ;
		String select = "select a.order_id,a.score1,a.score2,a.score3,a.reply_content,a.reply_time ,b.start_province,b.start_city,b.start_district,b.end_province,b.end_city,b.end_district,b.cargo_desc";
		String from = " from logistics_driver_reply a left join logistics_order b on a.order_id = b.id where a.driver_id = ?";

		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		Page<Record> pageObj = Db.paginate(page, pageSize , select , from.toString() ,driver_id );
		List<Record> list = pageObj.getList();
		return Result.success().data("items", list);
	}

	/**
	 * 修改密码
	 * @param param
	 * @return
	 */
	public static Object change_password(Param param) {
		ServiceContext.assertLogin();

		String old_password = param.getRequiredString("old_password");
		String new_password = param.getRequiredString("new_password");
		old_password = encryptPassword(old_password);
		new_password = encryptPassword(new_password);

		int user_id = ServiceContext.getServiceContext().getUserId();
		final int user_type = ServiceContext.getServiceContext().getCurrentSession().getUserType();
		if(user_type == Session.USER_TYPE_DRIVER) {
			Driver driver = Driver.dao.findById(user_id);
			if(driver == null) {
				throw new BusinessException("无效的司机ID:" + user_id);
			}
			if(old_password.equals(driver.getStr("password"))){
				driver.set("password", new_password);
				driver.update();
				ServiceLog.info("[%s]修改司机密码成功.", ServiceContext.getServiceContext().getPhone()).save();
			}else {
				ServiceLog.warn("[%s]修改司机密码失败,错误的原密码:%s", ServiceContext.getServiceContext().getPhone(), old_password).save();
				throw new BusinessException("错误的原始密码.");
			}
		}else if(user_type == Session.USER_TYPE_USER) {
			User user = User.dao.findById(user_id);
			if(user == null) {
				throw new BusinessException("无效的货主ID:" + user_id);
			}
			if(old_password.equals(user.getStr("password"))){
				user.set("password", new_password);
				user.update();
				ServiceLog.info("[%s]修改货主密码成功.", ServiceContext.getServiceContext().getPhone()).save();
			}else {
				ServiceLog.warn("[%s]修改司机密码失败,错误的原密码:%s", ServiceContext.getServiceContext().getPhone(), old_password).save();
				throw new BusinessException("错误的原始密码.");
			}
		}

		return Result.success();
	}

	public static Object get_password_vcode(Param param) {
		String phone = param.getRequiredString("phone");
		String captcha = RandomStringUtils.randomNumeric(6);
		String content = "易运宝找回密码验证码：" + captcha ;
		SmsService.send(phone, content);
		VcodeManagerPlugin.me().putVcode(phone,captcha);
		return Result.success("验证码已发送到手机:" + phone + "，请注意查收。");
	}

	public static Object get_password(Param param) {
		String phone = param.getRequiredString("phone");
		String vcode = param.getRequiredString("vcode");
		String password = param.getRequiredString("password");
		final int user_type = param.getRequiredInt("user_type");
		//验证手机验证码
		boolean isValidVcode = VcodeManagerPlugin.me().verifyVcode(phone,vcode);
		if(!isValidVcode) {
			throw new BusinessException("手机验证码不正确.");
		}
		//清除验证码
		VcodeManagerPlugin.me().removeVode(phone);

		//更新密码
		if(user_type == Session.USER_TYPE_USER) {
			String sql = "update logistics_user set password = ? where phone = ?" ;
			Db.update(sql , password, phone);
			ServiceLog.info("[%s]找回货主密码成功.", phone).save();
		}else if(user_type == Session.USER_TYPE_DRIVER) {
			String sql = "update logistics_driver set password = ? where phone = ?" ;
			Db.update(sql , password, phone);
			ServiceLog.info("[%s]找回司机密码成功.", phone).save();
		}

		return Result.success("修改密码成功.");
	}

	/**
	 * 3.3.12	充值请求接口
	 * @param param
	 * @return
	 */
	public static Object common_request_pay(Param param) {
		double pay_money = param.getDoubleValue("pay_money", 0);
		int pay_platform = param.getIntValue("pay_platform", 1);//1-默认易宝，2-支付宝
		String pay_channel = param.getRequiredString("pay_channel");

		Assert.isTrue(pay_money >= 0, "支付金额必须大于0");
		String uid = param.getRequiredString("uid");

		Pay pay = new Pay();
		pay.set("u_id", uid);

		if(!uid.startsWith("u") && !uid.startsWith("d")) {
			throw new BusinessException("uid参数不正确,必须以u或者d开头.");
		}

		if(uid.startsWith("u")) {//货主以"u"开头
			String str = StringUtils.substringAfter(uid, "u");
			int id = NumberUtils.toInt(str, -1);
			User user = User.dao.loadUserById(id);

			pay.set("u_phone", user.getStr("phone"));
			pay.set("u_name", user.getStr("company_name"));
		}else {//司机
			String str = StringUtils.substringAfter(uid, "d");
			int id = NumberUtils.toInt(str, -1);
			Driver driver = Driver.dao.loadDriverById(id);

			pay.set("u_phone", driver.getStr("phone"));
			pay.set("u_name", driver.getStr("name"));
		}
		pay.set("pay_money", pay_money);
		pay.set("pay_platform", pay_platform);
		pay.set("pay_channel", pay_channel);
		pay.set("status", Pay.STATUS_CREATE);
		pay.set("create_time", new Date());
		pay.save();

		int payId = pay.getInt("id");

		return Result.success().data("item", new KV("pay_order_id", payId));
	}

	/**
	 *
	 * @description 获取账户记录
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 3, 2013 9:55:13 PM
	 * @param param
	 * @return
	 */
	public static Object common_get_business(Param param){

		final int id = ServiceContext.getServiceContext().getUserId();
		int userType = ServiceContext.getServiceContext().getCurrentSession()
				.getUserType();
		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);

		String select = "select id,account,business_type,business_amount,create_time  ";
		String from = " from logistics_business where business_target = ? and account = ? order by id desc";

		Page<Record> pageObj = Db.paginate(page, pageSize , select , from.toString() ,userType , id );
		List<Record> list = pageObj.getList();
		return Result.success().data("items", list);
	}


	private static String encryptPassword(String password) {
		boolean encrypt_password = BaseConfig.me().getPropertyToBoolean("encrypt_password", false);
		if(encrypt_password) {
			String encryptedPassword = MD5Util.MD5(password);
			password = encryptedPassword;
		}
		return password;
	}
}
