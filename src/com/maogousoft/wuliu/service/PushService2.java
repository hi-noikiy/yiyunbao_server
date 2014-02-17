package com.maogousoft.wuliu.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import cn.jpush.api.ErrorCodeEnum;
import cn.jpush.api.MessageResult;
import cn.jpush.api.SimpleJPushClient;

import com.maogousoft.wuliu.BaseConfig;
import com.maogousoft.wuliu.domain.Driver;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-6-26 下午10:40:31
 */
public class PushService2 {

	private static final Logger log = LoggerFactory.getLogger(PushService2.class);

	private static final IdGenerator ID_GENERATOR = new IdGenerator(3);

	private static SimpleJPushClient jpush;

	public synchronized static SimpleJPushClient getJpush() {
		String jpushAppKey = BaseConfig.me().getProperty("jpush.app.key");
		String jpushAppPassword = BaseConfig.me().getProperty("jpush.app.password");
		Assert.hasText(jpushAppKey, "请配置:jpush.app.key");
		Assert.hasText(jpushAppPassword, "请配置:jpush.app.password");

		if(jpush == null) {
			jpush = new SimpleJPushClient(jpushAppPassword, jpushAppKey);
		}
		return jpush;
	}

	public static void pushSysMsgByDriverId(int[] driverIdList, String msgBody) {
		// System.out.println(conn.getAccountManager().getAccountAttributes());
		try {
			for (int driverId : driverIdList) {
				try {
					pushSysMsgByDriverId(driverId, msgBody);
				}catch(Throwable t) {
					log.error("无法将消息发送到" + driverId, t);
				}
			}
		}catch(Throwable t) {
			log.error("群发消息失败:" + Arrays.toString(driverIdList), t);
		}
	}

	/**
	 * 推送系统消息
	 * @param driverPhones 司机电话列表
	 * @param msgBody
	 */
	public static void pushSysMsgByDriverPhone(String[] driverPhones, String msgBody) {
		List<Integer> idList = new ArrayList<Integer>();
		for (String driverPhone : driverPhones) {
			Driver driver = Driver.dao.findByPhone(driverPhone);
			if(driver == null) {
				log.error("司机[" + driverPhone + "]不存在.");
			}
			idList.add(driver.getInt("id"));
		}
		Integer[] ids = idList.toArray(new Integer[0]);
		int[] idArr = ArrayUtils.toPrimitive(ids);
		pushSysMsgByDriverId(idArr, msgBody);
	}

	/**
	 * 发送消息给所有人
	 * @param msgBody
	 * @param msgType
	 */
	public static void pushToAll(String msgBody) {
		try {
			MessageResult msgResult = getJpush().sendCustomMessageWithAppKey(getMsgID(), msgBody);

			if (null != msgResult) {
				log.debug("jpush服务器返回数据: " + msgResult.toString());
				if (msgResult.getErrcode() == ErrorCodeEnum.NOERROR.value()) {
					log.debug("jpush发送成功， sendNo=" + msgResult.getSendno());
				} else {
					log.debug("jpush发送失败， 错误代码=" + msgResult.getErrcode() + ", 错误消息=" + msgResult.getErrmsg());
				}
			} else {
				log.debug("jpush无法获取数据，检查网络是否超时");
			}
		} catch (Exception e) {
			log.error("群发消息失败:" + e.getMessage(), e);
		}
	}

	/**
	 * 将消息发送给司机（通过ID确定司机帐号）
	 * @param driverId
	 * @param msgBody
	 * @param msgType
	 * @throws XMPPException
	 */
	private static void pushSysMsgByDriverId(int driverId, String msgBody) {
		final String driver_jid = "d" + driverId;
		pushMsgByUID(driver_jid, msgBody);
	}


	/**
	 * 根据UID推送系统消息
	 * @param uid
	 * @param msgBody
	 */
	public static void pushMsgByUID(final String uid, String msgBody) {
		MessageResult msgResult = getJpush().sendCustomMessageWithAlias(getMsgID(), uid, msgBody);

		if (null != msgResult) {
			log.debug("jpush服务器返回数据: " + msgResult.toString() + ",uid=" + uid);
			if (msgResult.getErrcode() == ErrorCodeEnum.NOERROR.value()) {
				log.debug("jpush发送成功， sendNo=" + msgResult.getSendno() + ",uid=" + uid);
			} else {
				log.debug("jpush发送失败， 错误代码=" + msgResult.getErrcode() + ", 错误消息=" + msgResult.getErrmsg() + ",uid=" + uid + ",msg=" + msgBody);
			}
		} else {
			log.debug("jpush无法获取数据，检查网络是否超时" + ",uid=" + uid + ",msg=" + msgBody);
		}
	}

	private static int getMsgID() {
		return ID_GENERATOR.nextId();
	}

	public static void main(String[] arags) {
		testJPush();
	}

	private static void testJPush() {
		SimpleJPushClient jpushClient = new SimpleJPushClient("fb50b56db2daeb7db84b28ba", "852bf542bb7bed2f67e935c4");

		int sendNo = 103;
		String txt = "你好啊。。。。";
		//String alias = "alias";
		//String tag = "tag";
		//String imei = "1234567890";

		MessageResult msgResult = jpushClient.sendCustomMessageWithAlias(sendNo, "user1", txt);
		//simpleJpush.sendNotificationWithAlias(sendNo, alias, txt);
		//simpleJpush.sendNotificationWithTag(sendNo, tag, txt);
		//simpleJpush.sendNotificationWithImei(sendNo, imei, txt);

		if (null != msgResult) {
			System.out.println("服务器返回数据: " + msgResult.toString());
			if (msgResult.getErrcode() == ErrorCodeEnum.NOERROR.value()) {
				System.out.println("发送成功， sendNo=" + msgResult.getSendno());
			} else {
				System.out.println("发送失败， 错误代码=" + msgResult.getErrcode() + ", 错误消息=" + msgResult.getErrmsg());
			}
		} else {
			System.out.println("无法获取数据，检查网络是否超时");
		}
	}
}
