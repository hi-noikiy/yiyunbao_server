package com.maogousoft.wuliu.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.maogousoft.wuliu.BaseConfig;
import com.maogousoft.wuliu.domain.Driver;


/**
 * 消息推送服务
 * @author yangfan(kenny0x00@gmail.com) 2013-5-12 上午2:49:14
 */
public class PushService {

	private static final Logger log = LoggerFactory.getLogger(PushService.class);

	/**新消息推送*/
	public static final int TYPE_NEW_PUSH_MSG = 7;

	public static void pushSysMsgByDriverId(int[] driverIdList, String msgBody, int msgType) {
		XMPPConnection conn = null;
		// System.out.println(conn.getAccountManager().getAccountAttributes());
		try {
			conn = createConnection();

			for (int driverId : driverIdList) {
				try {
					pushSysMsgByDriverId(conn,driverId, msgBody, msgType);
				}catch(Throwable t) {
					log.error("无法将消息发送到" + driverId, t);
				}
			}
		}catch(Throwable t) {
			log.error("群发消息失败:" + Arrays.toString(driverIdList), t);
		}finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
	}

	private static XMPPConnection createConnection() {
		String xmppServer = BaseConfig.me().getProperty("xmpp.server", "www.1yunbao.com");
		ConnectionConfiguration config = new ConnectionConfiguration(xmppServer);
		config.setSASLAuthenticationEnabled(false);
		XMPPConnection conn = new XMPPConnection(config);
		try {
			conn.connect();
		} catch (XMPPException e) {
			log.error("无法连接到消息服务器,请检查相关服务是否正常(openfire?)",e);
			throw new RuntimeException("无法连接到消息服务器,请检查相关服务是否正常(openfire?)");
		}
		String username = BaseConfig.me().getProperty("xmpp.sys.username");
		String password = BaseConfig.me().getProperty("xmpp.sys.password");
		String resource = BaseConfig.me().getProperty("xmpp.sys.resource");
		try {
			Assert.hasText(username, "请配置xmpp登录用户名xmpp.sys.username");
			Assert.hasText(password, "请配置xmpp登录密码xmpp.sys.password");
			Assert.hasText(password, "请配置xmpp的resource,xmpp.sys.password");
			conn.login(username, password, resource);
		} catch (XMPPException e) {
			log.error("无法连接到消息服务器,请检查相关服务是否正常(openfire?),以及帐号是否正确:admin",e);
			throw new RuntimeException("无法连接到消息服务器,请检查相关服务是否正常(openfire?)");
		}
		return conn;
	}

	/**
	 * 根据UID推送系统消息
	 * @param uid
	 * @param msgBody
	 * @param msgType
	 */
	public static void pushSysMsgByUID(String uid, String msgBody, int msgType) {
		XMPPConnection conn = null;
		// System.out.println(conn.getAccountManager().getAccountAttributes());

		try {
			conn = createConnection();
			// System.out.println(conn.getAccountManager().getAccountAttributes());
			pushMsgByUID(conn, uid, msgBody, msgType);
		}catch(Throwable t) {
			log.error("无法将消息发送到" + uid, t);
		}finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * 将消息发送给司机（通过ID确定司机帐号）
	 * @param conn
	 * @param driverPhone
	 * @param msgBody
	 * @param msgType
	 * @throws XMPPException
	 */
	private static void pushSysMsgByDriverId(XMPPConnection conn, int driverId, String msgBody, int msgType) throws XMPPException {
		final String driver_jid = "d" + driverId;
		pushMsgByUID(conn, driver_jid, msgBody, msgType);
	}

	private static void pushMsgByUID(XMPPConnection conn, final String uid, String msgBody, int msgType) throws XMPPException {
		String domain = getXmppDomain();
		Chat chat = conn.getChatManager().createChat(uid + "@" + domain + "", new MessageListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void processMessage(Chat chat, Message msg) {
				log.debug(msg.getFrom() + " " + new Date().toLocaleString() + "说:" + msg.getBody());
			}
		});
//
//		ChatManager chatmanager = conn.getChatManager();
//		chatmanager.addChatListener(new ChatManagerListener() {
//			@Override
//			public void chatCreated(Chat chat, boolean createdLocally) {
//				if (!createdLocally)
//					chat.addMessageListener(new MessageListener() {
//
//						@Override
//						public void processMessage(Chat chat, Message message) {
//							System.out.println(message.getFrom() + " " + new Date().toLocaleString() + "对我说:" + message.getBody());
//						}
//					});
//				;
//			}
//		});

		Message msg = new Message();
		msg.setProperty("msgId", System.currentTimeMillis());
		msg.setProperty("msgType", msgType);
		msg.setBody(msgBody);
		chat.sendMessage(msg);
	}

	/**
	 * 推送系统消息
	 * @param driverPhones 司机电话列表
	 * @param msgBody
	 * @param msgType
	 */
	public static void pushSysMsgByDriverPhone(String[] driverPhones, String msgBody, int msgType) {
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
		pushSysMsgByDriverId(idArr, msgBody, msgType);
	}

	/**
	 * 发送消息给所有人
	 * @param msgBody
	 * @param msgType
	 */
	public static void pushToAll(String msgBody, int msgType) {
		XMPPConnection conn = null;
		try {
			conn = createConnection();
			String domain = getXmppDomain();
			Chat chat = conn.getChatManager().createChat("all@broadcast." + domain + "", new MessageListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void processMessage(Chat chat, Message msg) {
					log.debug(msg.getFrom() + " " + new Date().toLocaleString() + "说:" + msg.getBody());
				}
			});

			Message msg = new Message();
			msg.setProperty("msgId", System.currentTimeMillis());
			msg.setProperty("msgType", msgType);
			msg.setBody(msgBody);
			chat.sendMessage(msg);
		} catch (XMPPException e) {
			log.error("群发消息失败:" + e.getMessage(), e);
			//throw new BusinessException("群发消息失败:" + e.getMessage());
		}finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
	}

	private static String getXmppDomain() {
		String domain = BaseConfig.me().getProperty("xmpp.domain", "www.1yunbao.com");
		return domain;
	}

	public static void main(String[] args) throws Exception {
		XMPPConnection.DEBUG_ENABLED = true;
		ConnectionConfiguration config = new ConnectionConfiguration("www.1yunbao.com");
		config.setSASLAuthenticationEnabled(false);
		XMPPConnection conn = new XMPPConnection(config);
		conn.connect();
		conn.login("admin", "admin");
//		conn.login("admin", "21232f297a57a5a743894a0e4a801fc3");
//		conn.login("d3", "111111");
		String uid = "u1";
		Chat chat = conn.getChatManager().createChat("all@broadcast.www.1yunbao.com", new MessageListener() {
//		Chat chat = conn.getChatManager().createChat("u1@www.1yunbao.com", new MessageListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void processMessage(Chat chat, Message msg) {
				log.debug(msg.getFrom() + " " + new Date().toLocaleString() + "说:" + msg.getBody());
			}
		});

		ChatManager chatmanager = conn.getChatManager();
		chatmanager.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				if (!createdLocally)
					chat.addMessageListener(new MessageListener() {

						@Override
						public void processMessage(Chat chat, Message message) {
							System.out.println(message.getFrom() + " " + new Date().toLocaleString() + "对我说:" + message.getBody());
						}
					});
				;
			}
		});

		Message msg = new Message();
		msg.setProperty("msgId", System.currentTimeMillis());
		msg.setProperty("msgType", 2);
		msg.setBody("http://www.1yunbao.com/images/avatar_driver.png");
//		msg.setProperty("msgType", 3);
//		msg.setBody("http://www.1yunbao.com/file/test1.mp3");
//		msg.setProperty("msgType", 3);
//		msg.setBody("http://www.1yunbao.com/file/a.ogg");
		chat.sendMessage(msg);

		conn.disconnect();
	}

	public static void main2(String[] args) throws Exception {
		XMPPConnection.DEBUG_ENABLED = true;
//		ConnectionConfiguration config = new ConnectionConfiguration("127.0.0.1");
//		ConnectionConfiguration config = new ConnectionConfiguration("114.80.155.168");
		ConnectionConfiguration config = new ConnectionConfiguration("www.1yunbao.net");
		config.setSASLAuthenticationEnabled(false);
		XMPPConnection conn = new XMPPConnection(config);
		conn.connect();
//		XMPPConnection conn = new XMPPConnection("127.0.0.1");
//		conn.connect();

//		conn.login("138000144", "11111");
		conn.login("admin", "admin");
//		conn.login("1351", "111111");
//		System.out.println(conn.getAccountManager().getAccountAttributes());

		Chat chat = conn.getChatManager().createChat("u1@www.1yunbao.net", new MessageListener() {

			@Override
			public void processMessage(Chat chat, Message msg) {
				System.out.println(msg.getFrom() + " " + new Date().toLocaleString() + "说:" + msg.getBody());
			}
		});

		ChatManager chatmanager = conn.getChatManager();
		chatmanager.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				if (!createdLocally)
					chat.addMessageListener(new MessageListener() {

						@Override
						public void processMessage(Chat chat, Message message) {
							System.out.println(message.getFrom() + " " + new Date().toLocaleString() + "对我说:" + message.getBody());
						}
					});
				;
			}
		});

//		FileTransferManager ftManager = new FileTransferManager(conn);
//		String userID = "135"+"@"+"127.0.0.1"+"/"+"Spark";//一定注意这里必须是完整JID
//		OutgoingFileTransfer transfer = ftManager.createOutgoingFileTransfer(userID);
//		transfer.sendFile(new File("x:\\xx\\vk\\dache\\04.技术资料\\xmpptest\\src\\com\\demo\\ChatTest.java"), "ChatTest.java");
//
//		while(!transfer.isDone()) {
//			if(transfer.getStatus() == FileTransfer.Status.in_progress){
//				//可以调用transfer.getProgress();获得传输的进度
//				System.out.println(transfer.getProgress());
//			}
////			System.out.println(transfer.getProgress());
//		}


		BufferedReader cmdIn = new BufferedReader(new InputStreamReader(System.in));
        for(;;) {
          try {
             String cmd = cmdIn.readLine();
             if("!q".equalsIgnoreCase(cmd)) {
                 break;
            }
            chat.sendMessage(cmd);
            Message msg = new Message();
            msg.setProperty("msgType", 1);
            msg.setBody(cmd);
            chat.sendMessage(msg);

          }catch(Exception ex) {
        	  ex.printStackTrace();
          }
        }
        conn.disconnect();
        System.exit(0);
	}
}
