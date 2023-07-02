package com.x.base.core.project.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HttpHeaders;
import com.x.base.core.project.config.Config;

public class FilterTools {

	public static final String ORIGIN = "Origin";
	public static final String ACCESS_CONTROL_ALLOW_METHODS_VALUE = "GET, POST, OPTIONS, PUT, DELETE, HEAD, TRACE";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS_VALUE = "x-requested-with, x-request, c-token, Content-Type, Content-Length, x-cipher, x-client, x-debugger, Authorization, P-User-Id, P-Request-Id, P-Page-Id";
	public static final String ACCESS_CONTROL_MAX_AGE_VALUE = "86400";

	public static void allow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (StringUtils.isNotBlank(Config.general().getAccessControlAllowOrigin())) {
			response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, Config.general().getAccessControlAllowOrigin());
		} else {
			String origin = request.getHeader(HttpHeaders.ORIGIN);
			if (StringUtils.isNotBlank(origin)) {
				response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			}
		}
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_VALUE);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
				ACCESS_CONTROL_ALLOW_HEADERS_VALUE + ", " + Config.person().getTokenName());
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
				"c-token" + ", " + Config.person().getTokenName());
		response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE_VALUE);
	}

	public static final String Application_Not_Initialized_Json = "{\"type\": \"error\", \"message\": \"application not initialized.\"}";

	public static final String Application_Not_CipherManagerUser_Json = "{\"type\": \"error\", \"message\": \"not cipher or manager or user.\"}";

	public static final String Application_Not_CipherManager_Json = "{\"type\": \"error\", \"message\": \"not cipher or manager.\"}";

	public static final String Application_Not_Anonymous_Json = "{\"type\": \"error\", \"message\": \"not anonymous.\"}";

	public static final String Application_Not_ManagerUser_Json = "{\"type\": \"error\", \"message\": \"not manager or user.\"}";

	public static final String Application_Not_User_Json = "{\"type\": \"error\", \"message\": \"not user.\"}";

	public static final String Application_Not_Cipher_Json = "{\"type\": \"error\", \"message\": \"not cipher.\"}";

	public static final String Application_403_Json = "{\"type\": \"error\", \"message\": \"403\"}";

}
