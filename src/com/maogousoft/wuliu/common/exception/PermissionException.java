package com.maogousoft.wuliu.common.exception;

/**
 *
 * 权限异常
 */
public class PermissionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PermissionException(String msg) {
		super(msg);
	}

	public PermissionException(String msg, Throwable t) {
		super(msg, t);
	}

}
