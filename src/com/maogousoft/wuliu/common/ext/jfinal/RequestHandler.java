package com.maogousoft.wuliu.common.ext.jfinal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class RequestHandler extends Handler {

	private static final ThreadLocal<HttpServletRequest> req = new ThreadLocal<HttpServletRequest>();

	public static HttpServletRequest getRequest() {
		return req.get();
	}

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		try {
			req.set(request);
			nextHandler.handle(target, request, response, isHandled);
		}finally {
			req.set(null);
		}
	}
}
