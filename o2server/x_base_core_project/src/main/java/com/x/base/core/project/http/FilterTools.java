package com.x.base.core.project.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class FilterTools {

	public static final String Access_Control_Allow_Origin = "Access-Control-Allow-Origin";
	public static final String Access_Control_Allow_Methods = "Access-Control-Allow-Methods";
	public static final String Access_Control_Allow_Methods_Value = "GET, POST, OPTIONS, PUT, DELETE, HEAD, TRACE";
	public static final String Access_Control_Allow_Headers = "Access-Control-Allow-Headers";
	public static final String Access_Control_Allow_Headers_Value = "x-requested-with, x-request, x-token,Content-Type, Content-Length, x-cipher, x-client, x-debugger, Authorization";
	public static final String Access_Control_Allow_Credentials = "Access-Control-Allow-Credentials";

	public static void allow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String origin = request.getHeader("Origin");
			response.addHeader(Access_Control_Allow_Origin, StringUtils.isBlank(origin) ? "*" : origin);
			response.addHeader(Access_Control_Allow_Methods, Access_Control_Allow_Methods_Value);
			response.addHeader(Access_Control_Allow_Headers, Access_Control_Allow_Headers_Value);
			response.addHeader(Access_Control_Allow_Credentials, "true");
		} catch (Exception e) {
			throw e;
		}
	}

	public static String Application_Not_Initialized_Json = "{\"type\": \"error\", \"message\": \"application not initialized.\"}";

	public static String Application_Not_CipherManagerUser_Json = "{\"type\": \"error\", \"message\": \"not cipher or manager or user.\"}";

	public static String Application_Not_CipherManager_Json = "{\"type\": \"error\", \"message\": \"not cipher or manager.\"}";

	public static String Application_Not_Anonymous_Json = "{\"type\": \"error\", \"message\": \"not anonymous.\"}";

	public static String Application_Not_ManagerUser_Json = "{\"type\": \"error\", \"message\": \"not manager or user.\"}";

	public static String Application_Not_User_Json = "{\"type\": \"error\", \"message\": \"not user.\"}";

	public static String Application_Not_Cipher_Json = "{\"type\": \"error\", \"message\": \"not cipher.\"}";

}
