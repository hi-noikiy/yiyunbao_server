package com.maogousoft.wuliu.controller;

import java.sql.SQLException;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.maogousoft.wuliu.common.json.JsonParam;
import com.maogousoft.wuliu.common.json.Param;
import com.maogousoft.wuliu.common.json.Result;
import com.maogousoft.wuliu.common.util.IPUtil;
import com.maogousoft.wuliu.service.CommonService;
import com.maogousoft.wuliu.service.DriverOrderService;
import com.maogousoft.wuliu.service.DriverService;
import com.maogousoft.wuliu.service.OrderService;
import com.maogousoft.wuliu.service.Session;
import com.maogousoft.wuliu.service.UserService;
import com.maogousoft.wuliu.service.UserSessionManager;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-3-25 下午11:32:59
 */
public class ServiceController extends Controller {

	private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

	@Before(Tx.class)
	public void index() throws SQLException {
		String jsonStr = getPara("json", "{}");
		String token = getPara("token", null);
		String action = getPara("action", null);
		Object data;
		try {
			if(StringUtils.isBlank(action)){
				throw new IllegalArgumentException("action参数不能为空,ip=" + IPUtil.getIP(getRequest()));
			}
			//将相关信息放入context
			ServiceContext context = new ServiceContext();
			context.setAction(action);
			context.setJsonStr(jsonStr);
			context.setToken(token);
			context.setRequest(this.getRequest());
			Session session = UserSessionManager.me().getSession(token);
			if(session != null) {
				//XXX userId、电话作为默认属性，放到session里面了，在登录或者注册的自动登录时，数据已保存到session
//				String userPhone = session.getAttr(Session.SESSION_PHONE);
//				context.setUserPhone(userPhone);
//				Integer userId = session.getAttr(Session.SESSION_USER_ID);
//				if(userId == null) {
//					userId = 0;
//				}
//				context.setUserId(userId);
			}
			ServiceContext.setServiceContext(context);

			UserSessionManager.me().accessSession(token);//更新session最后访问时间

			data = dispatch(jsonStr, token, action);
		} catch (ActiveRecordException e) {
			log.error(e.getMessage() + "\n\taction=" + action + "\n\tdata=" + jsonStr + "\n\ttoken=" + token, e);
			data = Result.fail("数据库错误:" + e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage() + "\n\taction=" + action + "\n\tdata=" + jsonStr + "\n\ttoken=" + token, e);
			data = Result.fail(e.getMessage());
		} finally {
			ServiceContext.setServiceContext(null);//清除context
		}
		renderData(data,jsonStr,token,action);
	}

	protected void renderData(Object data, String jsonStr, String token, String action) {
		final Result result;
		if (data instanceof String) {
			renderJson(data);
			return;
		}
		if(data == null) {
			result = Result.success();
		}else if (data instanceof Result) {
			result = (Result) data;
		} else {
			result = Result.success().data("data", data);
		}
		if(!result.isSuccess()) {
			log.warn("Service Failed: action={}, token={}, json={}", new Object[] {action, token, jsonStr});
		}
		renderJson(result.toJsonString());
	}

	protected Object dispatch(String data, String token, String action) {
		JSONObject json = JSONObject.fromObject(data);
		Param param = new JsonParam(json);
		Object result;
		if (action.equals("userlogin"))
		{
			result = UserService.login(param);
		}
		else if(action.equals("user_reg_getcode"))
		{
			result = UserService.user_reg_getcode(param);
		}
		else if(action.equals("user_reg"))
		{
			result = UserService.user_reg(param);
		}
		else if(action.equals("user_reg_optional"))
		{
			result = UserService.user_reg_optional(param);
		}
		else if(action.equals("publish_order"))
		{
			result = OrderService.publish_order(param);
		}
		else if(action.equals("query_order"))
		{
			result = OrderService.query_order(param);
		}
		else if(action.equals("get_order_detail"))
		{
			result = OrderService.get_order_detail(param);
		}
		else if(action.equals("get_order_detail_driver_list"))
		{
			result = OrderService.get_order_detail_driver_list(param);
		}
		else if(action.equals("add_order_price"))
		{
			result = OrderService.add_order_price(param);
		}
		else if(action.equals("cancel_order"))
		{
			result = OrderService.cancel_order(param);
		}
		else if(action.equals("accept_order"))
		{
			result = OrderService.accept_order(param);
		}
		else if(action.equals("rating_to_driver"))
		{
			result = OrderService.rating_to_driver(param);
		}
		else if(action.equals("get_id_card_info"))
		{
			result = OrderService.get_id_card_info(param);
		}
		else if(action.equals("get_license_info"))
		{
			result = OrderService.get_license_info(param);
		}
		else if(action.equals("get_registration_info"))
		{
			result = OrderService.get_registration_info(param);
		}
		else if(action.equals("query_in_shipping"))
		{
			result = OrderService.query_in_shipping(param);
		}
		else if(action.equals("user_pay_history"))
		{
			result = UserService.query_pay_history(param);
		}

		//---------------司机端----------------------------------
		else if(action.equals("driver_reg_getcode"))
		{
			result = DriverService.driver_reg_getcode(param);
		}
		else if(action.equals("driver_reg"))
		{
			result = DriverService.driver_reg(param);
		}
		else if(action.equals("driver_reg_optional"))
		{
			result = DriverService.driver_reg_optional(param);
		}
		else if(action.equals("driverlogin"))
		{
			result = DriverService.driverlogin(param);
		}
		else if(action.equals("query_source_order"))
		{
			result = DriverOrderService.query_source_order(param);
		}
		else if(action.equals("get_source_order_detail"))
		{
			result = DriverOrderService.get_source_order_detail(param);
		}
		else if(action.equals("place_source_order"))
		{
			result = DriverOrderService.place_source_order(param);
		}
		else if(action.equals("attention_source_order"))
		{
			result = DriverOrderService.attention_source_order(param);
		}
		else if(action.equals("query_pending_source_order"))
		{
			result = DriverOrderService.query_pending_source_order(param);
		}
		else if(action.equals("cancel_place_source_order"))
		{
			result = DriverOrderService.cancel_place_source_order(param);
		}
		else if(action.equals("query_pending_source_order_in_shipping"))
		{
			result = DriverOrderService.query_pending_source_order_in_shipping(param);
		}
		else if(action.equals("shipping_order_update_status"))
		{
			result = DriverOrderService.shipping_order_update_status(param);
		}
		else if(action.equals("shipping_order_update_location"))
		{
			result = DriverOrderService.shipping_order_update_location(param);
		}
		else if(action.equals("validate_receipt_password"))
		{
			result = DriverOrderService.validate_receipt_password(param);
		}
		else if(action.equals("driver_query_finished_order"))
		{
			result = DriverOrderService.driver_query_finished_order(param);
		}
		else if(action.equals("rating_to_user"))
		{
			result = DriverOrderService.rating_to_user(param);
		}
		else if(action.equals("driver_profile"))
		{
			result = DriverService.driverProfile(param);
		}
		else if(action.equals("driver_pay_history"))
		{
			result = DriverService.query_pay_history(param);
		}
		else if(action.equals("add_vender"))
		{
			result = DriverService.add_vender(param);
		}
		else if(action.equals("query_vender"))
		{
			result = DriverService.query_vender(param);
		}
		else if(action.equals("add_vender_reply"))
		{
			result = DriverService.add_vender_reply(param);
		}
		else if(action.equals("query_vender_reply"))
		{
			result = DriverService.query_vender_reply(param);
		}
		else if(action.equals("driver_update_line"))
		{
			result = DriverService.driver_update_line(param);
		}
		else if(action.equals("driver_coupon"))
		{
			result = DriverService.driver_coupon(param);
		}

		//---------------------公共接口---------------------
		else if(action.equals("common_get_dict_list"))
		{
			result = CommonService.common_get_dict_list(param);
		}
		else if(action.equals("change_password"))
		{
			result = CommonService.change_password(param);
		}
		else if(action.equals("post_feedback"))
		{
			result = CommonService.post_feedback(param);
		}
		else if(action.equals("get_account_gold"))
		{
			result = CommonService.get_account_gold(param);
		}
		else if(action.equals("query_message"))
		{
			result = CommonService.query_message(param);
		}
		else if(action.equals("get_driver_info"))
		{
			result = CommonService.get_driver_info(param);
		}
		else if(action.equals("get_user_info"))
		{
			result = CommonService.get_user_info(param);
		}
		else if(action.equals("get_user_reply"))
		{
			result = CommonService.get_user_reply(param);
		}
		else if(action.equals("get_driver_reply"))
		{
			result = CommonService.get_driver_reply(param);
		}
		else if(action.equals("get_password_vcode"))
		{
			result = CommonService.get_password_vcode(param);
		}
		else if(action.equals("get_password"))
		{
			result = CommonService.get_password(param);
		}
		else if(action.equals("common_request_pay"))
		{
			result = CommonService.common_request_pay(param);
		}
		else if(action.equals("common_get_business"))
		{
			result = CommonService.common_get_business(param);
		}
		else
		{
			String msg = "无效的接口请求,action=" + action + ",token=" + token;
			log.error(msg);
			result = Result.fail(msg);
		}
		return result;
	}

}
