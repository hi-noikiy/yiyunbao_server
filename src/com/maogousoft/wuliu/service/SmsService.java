/**
 * @filename SmsService.java
 */
package com.maogousoft.wuliu.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * @description 短信发送
 * @author shevliu
 * @email shevliu@gmail.com
 * May 9, 2013 9:40:50 PM
 */
public class SmsService {
	
	private static final Log log = LogFactory.getLog(SmsService.class);
	
	private static final String sn = "SDK-SZW-010-00007";
	
	private static final String pwd = "17EE4FEE74FF00D79442D032A7376D85";
	
	private static final String serviceURL = "http://sdk105.entinfo.cn/webservice.asmx";
	
	/**
	 * 
	 * @description 群发短信短信
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 9, 2013 10:03:40 PM
	 * @param phones
	 * @param content
	 */
	public static void send(String phones[] , String content){
		String phoneStr = "";
		for(String phone : phones){
			phoneStr += phone ;
			phoneStr += "," ;
		}
		
		send(phoneStr.substring(0 , phoneStr.length() -1) , content);
	}

		
	/**
	 * 
	 * @description 发送单条短信
	 * @author shevliu
	 * @email shevliu@gmail.com
	 * May 11, 2013 1:34:48 PM
	 * @param mobile
	 * @param content
	 * @return
	 */
	public static String send(String mobile, String content ) {
		content += "【易运宝】";
		String result = "";
		String soapAction = "http://tempuri.org/mt";
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
		xml += "<soap:Body>";
		xml += "<mt xmlns=\"http://tempuri.org/\">";
		xml += "<sn>" + sn + "</sn>";
		xml += "<pwd>" + pwd + "</pwd>";
		xml += "<mobile>" + mobile + "</mobile>";
		xml += "<content>" + content + "</content>";
		xml += "<ext></ext>";
		xml += "<stime></stime>";
		xml += "<rrid></rrid>";
		xml += "</mt>";
		xml += "</soap:Body>";
		xml += "</soap:Envelope>";

		URL url;
		try {
			url = new URL(serviceURL);

			URLConnection connection = url.openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xml.getBytes("GBK"));
			//如果您的系统是utf-8,这里请改成bout.write(xml.getBytes("GBK"));

			byte[] b = bout.toByteArray();
			httpconn.setRequestProperty("Content-Length", String
					.valueOf(b.length));
			httpconn.setRequestProperty("Content-Type",
					"text/xml; charset=gb2312");
			httpconn.setRequestProperty("SOAPAction", soapAction);
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			OutputStream out = httpconn.getOutputStream();
			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(httpconn
					.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String inputLine;
			while (null != (inputLine = in.readLine())) {
				Pattern pattern = Pattern.compile("<mtResult>(.*)</mtResult>");
				Matcher matcher = pattern.matcher(inputLine);
				while (matcher.find()) {
					result = matcher.group(1);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
}
