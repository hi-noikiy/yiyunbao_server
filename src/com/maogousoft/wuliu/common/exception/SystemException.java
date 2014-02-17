package com.maogousoft.wuliu.common.exception;


/**
 * 系统级别的错误，比如当发现缺少配置文件、启动参数错误等，系统出现致命错误，无法启动等情况。
 * @author kenny(kenny0x00@gmail.com) 2011-5-5 下午04:27:20
 */
public class SystemException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public SystemException(String message){
		super(message);
	}

	public SystemException(String message , Throwable cause) {
		super(message ,cause);
	}
}
