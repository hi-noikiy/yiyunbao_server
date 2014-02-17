package com.maogousoft.wuliu.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.maogousoft.wuliu.BaseConfig;
import com.maogousoft.wuliu.common.collections.KV;
import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.common.json.Param;
import com.maogousoft.wuliu.common.json.Result;
import com.maogousoft.wuliu.common.syslog.ServiceLog;
import com.maogousoft.wuliu.common.util.MD5Util;
import com.maogousoft.wuliu.controller.ServiceContext;
import com.maogousoft.wuliu.domain.User;
import com.maogousoft.wuliu.service.image.ImageService;

/**
 * 货主服务
 * @author yangfan(kenny0x00@gmail.com) 2013-3-29 下午11:08:02
 */
public class UserService{

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	public static Result login(Param param) {
		String phone = param.getRequiredString("phone");
		String password = param.getRequiredString("password");
		password = encryptPassword(password);

		User user = User.dao.findFirst("select * from logistics_user where phone=? and password=?", phone, password);

		if(user == null) {
			ServiceLog.info("货主[%s]登录失败，用户名或密码错误.", phone).save();
			return Result.fail("用户名或密码错误.");
		}else if(user.getInt("status") != 0){
			ServiceLog.info("货主[%s]登录失败，用户已冻结.", phone).save();
			return Result.fail("用户已冻结");
		}else {
			UserSessionManager manager = UserSessionManager.me();
			int userId = user.getInt("id");
			Session session = manager.createSession(userId, phone, Session.USER_TYPE_USER);
			String token = session.getToken();
			KV kv = new KV();
			kv.add("token" , token);
			kv.add("id" , userId);
			kv.add("name" , user.get("name"));
			ServiceLog.info("货主[%s]登录成功.", phone).save();
			return Result.success().data("item", kv);
		}
	}

	public static Result user_reg_getcode(Param param) {
		String phone = param.getRequiredString("phone");
		if(User.dao.existsPhone(phone)) {
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
	public static Result user_reg(Param param) {
		String phone = param.getRequiredString("phone");
		String password = param.getRequiredString("password");
		String vcode = param.getRequiredString("vcode");
		password = encryptPassword(password);
		String company = param.getRequiredString("company");
		int device_type = param.getRequiredInt("device_type");
		String recommender_phone = param.getRequiredString("recommender_phone");
		String address = param.getString("address");

		//验证手机验证码
		boolean isValidVcode = VcodeManagerPlugin.me().verifyVcode(phone,vcode);
		if(!isValidVcode) {
			throw new BusinessException("手机验证码不正确,或者已过期.");
		}
		//清除验证码
		VcodeManagerPlugin.me().removeVode(phone);

		if(User.dao.existsPhone(phone)) {
			throw new BusinessException("手机号已注册:" + phone);
		}
//		User recommender = User.dao.findByPhone(recommender_phone);
//		if(recommender == null) {
//			return Result.fail("推荐人号码:" + recommender_phone + "不存在.");
//		}
//		int recommender_id = recommender.getInt("id");

		User user = new User();
		user.set("phone", phone);
		user.set("password", password);
		user.set("company_name", company);
		user.set("device_type", device_type);
		user.set("regist_time", new Date());
		user.set("score1", 0);//初始化评分
		user.set("score2", 0);//初始化评分
		user.set("score3", 0);//初始化评分
		user.set("score", 0);//初始化评分
		user.set("gold", 0);//物流币为0
		user.set("total_deal", 0);
		user.set("user_level", 0);
		user.set("status", 0);//-1 已删除，0-正常，1-冻结
		user.set("recommender", recommender_phone);
		user.set("last_read_msg", new Date());
		user.set("address", address);
		user.save();

		//记录全局用户
		Record globalUser = new Record();
		globalUser.set("uid", "u" + user.getInt("id"));
		globalUser.set("password", password);
		globalUser.set("user_type", 0);//0-货主,1-司机
		globalUser.set("data_id", user.getInt("id"));
		globalUser.set("create_time", new Date());
		Db.save("logistics_global_user", globalUser);

		ServiceLog.info("[%s]注册[货主]成功", phone).save();

		UserSessionManager manager = UserSessionManager.me();
		int userId = user.getInt("id");
		Session session = manager.createSession(userId, phone, Session.USER_TYPE_USER);
		String token = session.getToken();
		return Result.success().data("item", new KV("user_id",userId, "token", token));
	}

	public static Result user_reg_optional(Param param) {
		ServiceContext.assertLogin();

		byte[] photo = param.getBytesFromBase64("photo");
		String contact = param.getString("contact");

		int userId = ServiceContext.getServiceContext().getUserId();
		User user = User.dao.findById(userId);

//		user.set("name", contact);

		ImageService.saveImageToModel(user, "licence_photo", photo);

		user.update();

		ServiceLog.info("更新用户[%s]选填信息成功.", user.getStr("phone")).save();

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

	/**
	 * 
	 * @description 获取货主充值记录 
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 26, 2013 5:46:17 PM
	 * @param param
	 * @return
	 */
	public static Object query_pay_history(Param param) {
		ServiceContext.assertLoginAsUser();
		int page = param.getIntValue("page", 1);
		int pageSize = param.getIntValue("page_size", 10);
		String u_id = "u" + ServiceContext.getServiceContext().getUserId() ;
		String select  = "select id,u_id,pay_money,pay_platform,status,create_time,finish_time ";
		String from =  "from logistics_pay where u_id = ? and status = 99 order by id desc" ;
		Page<Record> pageObj = Db.paginate(page, pageSize, select, from , u_id);
		List<Record> list = pageObj.getList();
		return Result.success().data("items", list);
	}
}
