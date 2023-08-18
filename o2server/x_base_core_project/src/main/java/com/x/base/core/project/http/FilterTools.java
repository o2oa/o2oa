package com.x.base.core.project.http;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HttpHeaders;
import com.x.base.core.project.config.Config;

public class FilterTools {

	private FilterTools() {

	}

	public static Heads getHeads() {
		return HeadsHolder.INSTANCE;
	}

	private static class HeadsHolder {

		private static final Heads INSTANCE = new Heads();

	}

	private static class Heads extends LinkedHashMap<String, String> {

		private static final long serialVersionUID = -7420327905254994471L;

		private boolean accessControlAllowOriginCustomized = false;

		private Heads() {
			try {
				if (StringUtils.isNotBlank(Config.general().getAccessControlAllowOrigin())) {
					this.put(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, Config.general().getAccessControlAllowOrigin());
					accessControlAllowOriginCustomized = true;
				}
				if (StringUtils.isNotBlank(Config.general().getContentSecurityPolicy())) {
					this.put(HttpHeaders.CONTENT_SECURITY_POLICY, Config.general().getContentSecurityPolicy());
				}
				this.put(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_VALUE);
				this.put(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
						ACCESS_CONTROL_ALLOW_HEADERS_VALUE + ", " + Config.person().getTokenName());
				this.put(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
				this.put(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "c-token" + ", " + Config.person().getTokenName());
				this.put(HttpHeaders.ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE_VALUE);
				this.put(HttpHeaders.X_XSS_PROTECTION, X_XSS_PROTECTION_VALUE);
				this.put(HttpHeaders.X_CONTENT_TYPE_OPTIONS, X_CONTENT_TYPE_OPTIONS_VALUE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static final String ORIGIN = "Origin";
	public static final String ACCESS_CONTROL_ALLOW_METHODS_VALUE = "GET, POST, OPTIONS, PUT, DELETE, HEAD, TRACE";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS_VALUE = "x-requested-with, x-request, c-token, Content-Type, Content-Length, x-cipher, x-client, x-debugger, Authorization, P-User-Id, P-Request-Id, P-Page-Id";
	public static final String ACCESS_CONTROL_MAX_AGE_VALUE = "86400";
	public static final String X_XSS_PROTECTION_VALUE = "1; mode=block";
	public static final String X_CONTENT_TYPE_OPTIONS_VALUE = "nosniff";

	public static void allow(HttpServletRequest request, HttpServletResponse response) {
		getHeads().entrySet().forEach(o -> response.addHeader(o.getKey(), o.getValue()));
		if (!getHeads().accessControlAllowOriginCustomized) {
			String origin = request.getHeader(HttpHeaders.ORIGIN);
			if (StringUtils.isNotBlank(origin)) {
				response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			}
		}
	}

	public static final String APPLICATION_NOT_INITIALIZED_JSON = "{\"type\": \"error\", \"message\": \"application not initialized.\"}";

	public static final String APPLICATION_NOT_CIPHERMANAGERUSER_JSON = "{\"type\": \"error\", \"message\": \"not cipher or manager or user.\"}";

	public static final String APPLICATION_NOT_CIPHERMANAGER_JSON = "{\"type\": \"error\", \"message\": \"not cipher or manager.\"}";

	public static final String APPLICATION_NOT_ANONYMOUS_JSON = "{\"type\": \"error\", \"message\": \"not anonymous.\"}";

	public static final String APPLICATION_NOT_MANAGERUSER_JSON = "{\"type\": \"error\", \"message\": \"not manager or user.\"}";

	public static final String APPLICATION_NOT_USER_JSON = "{\"type\": \"error\", \"message\": \"not user.\"}";

	public static final String APPLICATION_NOT_CIPHER_JSON = "{\"type\": \"error\", \"message\": \"not cipher.\"}";

	public static final String APPLICATION_403_JSON = "{\"type\": \"error\", \"message\": \"403\"}";

}
