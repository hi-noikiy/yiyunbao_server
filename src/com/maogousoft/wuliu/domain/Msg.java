/**
 * @filename Msg.java
 */
package com.maogousoft.wuliu.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.common.util.WuliuStringUtils;
import com.maogousoft.wuliu.service.PushService;
import com.maogousoft.wuliu.service.PushService2;

/**
 * @description 系统消息
 * @author shevliu
 * @email shevliu@gmail.com
 * May 6, 2013 9:28:13 PM
 */
public class Msg extends Model<Msg>{

	private static final Logger log = LoggerFactory.getLogger(Msg.class);

	private static final long serialVersionUID = -1048009202020508585L;

	/**
	 * 公告
	 */
	public static final int TYPE_NOTICE = 0 ;

	/**
	 * 促销
	 */
	public static final int TYPE_AD = 1 ;

	/**
	 * 交易
	 */
	public static final int TYPE_ORDER = 2 ;

	/**
	 * 账户变化
	 */
	public static final int TYPE_BUSINIESS = 3 ;

	/**
	 * 排名
	 */
	public static final int TYPE_RANK = 4 ;


	public static final Msg dao = new Msg();

	/**
	 *
	 * @description 添加货主消息
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 2, 2013 10:09:44 PM
	 * @param msg_type
	 * @param msg_title
	 * @param msg_content
	 * @param u_id
	 */
	public void addUserMsg(int msg_type , String msg_title , String msg_content , int userId){
		String uid = "u" + userId ;
		addMsg(msg_type, msg_title, msg_content, uid);
	}

	/**
	 *
	 * @description 添加司机消息
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * Jun 2, 2013 10:15:35 PM
	 * @param msg_type
	 * @param msg_title
	 * @param msg_content
	 * @param driverId
	 */
	public void addDriverMsg(int msg_type , String msg_title , String msg_content , int driverId){
		String uid = "d" + driverId ;
		addMsg(msg_type, msg_title, msg_content, uid);
	}

	private void addMsg(int msg_type, String msg_title, String msg_content, String uid) {
		Msg msg = new Msg();
		msg.set("msg_type", msg_type);
		msg.set("msg_title", msg_title);
		msg.set("msg_content", msg_content);
		msg.set("msg_time", new Date());
		msg.set("u_id", uid);
		msg.set("status", 0);
		msg.save();

		try {
			Date last_read_msg = Msg.getLastReadMsgByUID(uid);
			int msg_count = Msg.findNewMsgCount(uid, last_read_msg);
			JSONObject json = new JSONObject();
			json.put("msg_type", PushService.TYPE_NEW_PUSH_MSG);
			json.put("msg_count", msg_count);
			json.put("last_read_msg", last_read_msg == null? 0: last_read_msg.getTime());
			log.debug("推送消息给:" + uid + ",msg=" + json + ",content=" + msg_title + "---" + msg_content);
//			PushService.pushSysMsgByUID(uid, json.toString(), PushService.TYPE_NEW_PUSH_MSG);
			PushService2.pushMsgByUID(uid, json.toString());
			log.debug("推送成功");
		} catch (Throwable t) {
			log.error(t.getMessage(),t);
			log.error("无法将信息推送到:" + uid + ", cause: " + t.getMessage(), t);
		}
	}

	/**
	 * 获取新消息总数
	 * @param uid
	 * @param last_read_msg
	 * @return
	 */
	public static int findNewMsgCount(String uid, Date last_read_msg) {
		if(last_read_msg != null) {
			return Db.queryLong("select count(*) from logistics_sys_msg where u_id=? and status=0 and msg_time>?", uid, last_read_msg).intValue();
		}else {
			return Db.queryLong("select count(*) from logistics_sys_msg where u_id=? and status=0", uid).intValue();
		}
	}

	/**
	 * 获取用户最后读取信息的时间
	 * @param uid
	 * @return
	 */
	public static Date getLastReadMsgByUID(String uid) {
		if(StringUtils.startsWith(uid, "u")) {
			int user_id = WuliuStringUtils.parseInt(StringUtils.substringAfter(uid, "u"));
			Date last_read_msg = Db.queryTimestamp("select last_read_msg from logistics_user where id=?", user_id);
			return last_read_msg;
		}else if(StringUtils.startsWith(uid, "d")) {
			int driver_id = WuliuStringUtils.parseInt(StringUtils.substringAfter(uid, "u"));
			Date last_read_msg = Db.queryTimestamp("select last_read_msg from logistics_driver where id=?", driver_id);
			return last_read_msg;
		}
		throw new BusinessException("无效的uid:" + uid);
	}
}
