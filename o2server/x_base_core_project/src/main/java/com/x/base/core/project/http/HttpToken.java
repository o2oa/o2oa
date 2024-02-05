package com.x.base.core.project.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DomainTools;
import com.x.base.core.project.tools.URLTools;

public class HttpToken {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpToken.class);

	public static final String X_AUTHORIZATION = "authorization";
	public static final String X_PERSON = "x-person";
	public static final String X_DISTINGUISHEDNAME = "x-distinguishedName";
	public static final String X_REQUESTBODY = "x-requestBody";
	public static final String X_CLIENT = "x-client";
	public static final String CLIENT_APP = "app";
	public static final String CLIENT_H5 = "h5";
	public static final String X_DEBUGGER = "x-debugger";
	public static final String COOKIE_ANONYMOUS_VALUE = "anonymous";
	public static final String SET_COOKIE = "Set-Cookie";

	private static final String REGULAREXPRESSION_IP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
	private static final String REGULAREXPRESSION_TOKEN = "^(anonymous|user|manager|cipher|systemManager|securityManager|auditManager)([2][0][1-9][0-9][0-1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9])(h5|moa|app)?(\\S{1,})$";

	private static final String COOKIE_PART_MIDDLE = "; path=/; domain=";
	private static final String COOKIE_PART_HTTPONLY = "; HttpOnly";
	private static final String COOKIE_PART_SECURE = "; Secure";

	public EffectivePerson who(HttpServletRequest request, HttpServletResponse response, String key) throws Exception {
		EffectivePerson effectivePerson = this.who(this.getToken(request), key, remoteAddress(request));
		effectivePerson.setRemoteAddress(HttpToken.remoteAddress(request));
		effectivePerson.setUserAgent(this.userAgent(request));
		effectivePerson.setUri(request.getRequestURI());
		// 加入调试标记
		Object debugger = request.getHeader(HttpToken.X_DEBUGGER);
		effectivePerson.setDebugger((null != debugger) && BooleanUtils.toBoolean(Objects.toString(debugger)));
		// this.setAttribute(request, effectivePerson);
		setToken(request, response, effectivePerson);
		return effectivePerson;
	}

	public EffectivePerson whoNotRefreshToken(HttpServletRequest request, HttpServletResponse response, String key) throws Exception {
		String token = this.getToken(request);
		EffectivePerson effectivePerson = this.who(token, key, remoteAddress(request));
		effectivePerson.setRemoteAddress(HttpToken.remoteAddress(request));
		effectivePerson.setUserAgent(this.userAgent(request));
		effectivePerson.setUri(request.getRequestURI());
		// 加入调试标记
		Object debugger = request.getHeader(HttpToken.X_DEBUGGER);
		effectivePerson.setDebugger((null != debugger) && BooleanUtils.toBoolean(Objects.toString(debugger)));
		effectivePerson.setToken(token);
		setToken(request, response, effectivePerson);
		return effectivePerson;
	}

	public EffectivePerson who(String token, String key, String address) {
		if (StringUtils.length(token) < 16) {
			/* token应该是8的倍数有可能前台会输入null空值等可以通过这个过滤掉 */
			return EffectivePerson.anonymous();
		}
		try {
			String plain = "";
			try {
				plain = Crypto.decrypt(token, key, Config.person().getEncryptType());
			} catch (Exception e) {
			    LOGGER.warn("can not decrypt token:{}, {}, remote address:{}.", token, e.getMessage(), address);
				return EffectivePerson.anonymous();
			}
			Pattern pattern = Pattern.compile(REGULAREXPRESSION_TOKEN, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(plain);
			if (!matcher.find()) {
				// 不报错,跳过错误,将用户设置为anonymous
			    LOGGER.warn("token format error:{}, remote address:{}.", plain, address);
				return EffectivePerson.anonymous();
			}
			Date date = DateUtils.parseDate(matcher.group(2), DateTools.formatCompact_yyyyMMddHHmmss);
			TokenType tokenType = TokenType.valueOf(matcher.group(1));
			long diff = (System.currentTimeMillis() - date.getTime());
			diff = Math.abs(diff);
			String client = matcher.group(3);
			String userName = matcher.group(4);
			if (TokenType.user.equals(tokenType) || TokenType.manager.equals(tokenType)
					|| TokenType.systemManager.equals(tokenType) || TokenType.auditManager.equals(tokenType)
					|| TokenType.securityManager.equals(tokenType)) {
				// 启用安全删除
				if (BooleanUtils.isTrue(Config.person().getEnableSafeLogout())) {
					String user = URLDecoder.decode(userName, StandardCharsets.UTF_8.name());
					Date threshold = Config.resource_node_tokenThresholds().get(user);
					if ((null != threshold) && threshold.after(date)) {
					    LOGGER.warn("token expired by safe logout, user:{}, token:{}, remote address:{}.", user, plain,
								address);
						return EffectivePerson.anonymous();
					}
				}
				int expiredMinutes = CLIENT_APP.equals(client) ? Config.person().getAppTokenExpiredMinutes() : Config.person().getTokenExpiredMinutes();
				if (diff > (60000L * expiredMinutes)) {
					// 不报错,跳过错误,将用户设置为anonymous
				    LOGGER.warn("token expired, user:{}, token:{}, remote address:{}.",
							URLDecoder.decode(userName, StandardCharsets.UTF_8.name()), plain, address);
					return EffectivePerson.anonymous();
				}
			}
			if (TokenType.cipher.equals(tokenType) && (diff > (60000 * 20))) {
				// 不报错,跳过错误,将用户设置为anonymous
				return EffectivePerson.anonymous();
			}
			return new EffectivePerson(URLDecoder.decode(userName, StandardCharsets.UTF_8.name()), tokenType, client,
					key, Config.person().getEncryptType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EffectivePerson.anonymous();
	}

	public void deleteToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			// String cookie = X_Token + "=; path=/; domain=" +
			// this.domain(request) + "; max-age=0
			String cookie = Config.person().getTokenName() + "=" + COOKIE_ANONYMOUS_VALUE + COOKIE_PART_MIDDLE
					+ this.domain(request)
					+ (BooleanUtils.isTrue(Config.person().getTokenCookieHttpOnly()) ? COOKIE_PART_HTTPONLY : "");
			response.setHeader(SET_COOKIE, cookie);
			response.setHeader(Config.person().getTokenName(), COOKIE_ANONYMOUS_VALUE);
		} catch (Exception e) {
			throw new IllegalAccessException("delete Token cookie error.");
		}
	}

	public void setToken(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson)
			throws Exception {
		this.setAttribute(request, effectivePerson);
		switch (effectivePerson.getTokenType()) {
		case anonymous:
			deleteToken(request, response);
			break;
		case user:
			this.setResponseToken(request, response, effectivePerson);
			break;
		case manager:
			this.setResponseToken(request, response, effectivePerson);
			break;
		case systemManager:
			this.setResponseToken(request, response, effectivePerson);
			break;
		case securityManager:
			this.setResponseToken(request, response, effectivePerson);
			break;
		case auditManager:
			this.setResponseToken(request, response, effectivePerson);
			break;
		case cipher:
			this.deleteToken(request, response);
			break;
		default:
			break;
		}
	}

	private void setResponseToken(HttpServletRequest request, HttpServletResponse response,
			EffectivePerson effectivePerson) throws Exception {
		if (!StringUtils.isEmpty(effectivePerson.getToken())) {
			String cookie = Config.person().getTokenName() + "=" + effectivePerson.getToken() + COOKIE_PART_MIDDLE
					+ this.domain(request)
					+ (BooleanUtils.isTrue(Config.person().getTokenCookieSecure()) ? COOKIE_PART_SECURE : "")
					+ (BooleanUtils.isTrue(Config.person().getTokenCookieHttpOnly()) ? COOKIE_PART_HTTPONLY : "");
			response.setHeader(SET_COOKIE, cookie);
			response.setHeader(Config.person().getTokenName(), effectivePerson.getToken());
		}
	}

	public void setResponseToken(HttpServletRequest request, HttpServletResponse response, String tokenName,
			String token) throws Exception {
		if (!StringUtils.isEmpty(token)) {
			String cookie = tokenName + "=" + token + COOKIE_PART_MIDDLE + this.domain(request)
					+ (BooleanUtils.isTrue(Config.person().getTokenCookieSecure()) ? COOKIE_PART_SECURE : "")
					+ (BooleanUtils.isTrue(Config.person().getTokenCookieHttpOnly()) ? COOKIE_PART_HTTPONLY : "");
			response.setHeader(SET_COOKIE, cookie);
			response.setHeader(tokenName, token);
		}
	}

	public String getToken(HttpServletRequest request) throws Exception {
		String token = null;
		token = URLTools.getQueryStringParameter(request.getQueryString(), Config.person().getTokenName());
		if (StringUtils.isEmpty(token) && (null != request.getCookies())) {
			for (Cookie c : request.getCookies()) {
				if (StringUtils.equals(Config.person().getTokenName(), c.getName())) {
					token = c.getValue();
					break;
				}
			}
		}
		if (StringUtils.isEmpty(token) || COOKIE_ANONYMOUS_VALUE.equals(token)) {
			token = request.getHeader(Config.person().getTokenName());
		}

//		if (BooleanUtils.isTrue(Config.token().getCsrfProtectionEnable()) && (!StringUtils
//				.equals(Objects.toString(token, ""), Objects.toString(request.getHeader(X_AUTHORIZATION), "")))) {
//			throw new IllegalAccessException("CSFT protection denied.");
//		}

		if (StringUtils.isEmpty(token)) {
			String value = request.getHeader(X_AUTHORIZATION);
			// 如果使用oauth bearer 通过此传递认证信息.需要进行判断,格式为 Bearer xxxxxxx
			// wps格式为WPS-2:xxxx
			if (!StringUtils.contains(value, " ") && !StringUtils.contains(value, ":")) {
				token = value;
			}
		}
		return token;
	}

	private String domain(HttpServletRequest request) {
		String str = request.getServerName();
		if (StringUtils.contains(str, ".")) {
			Pattern pattern = Pattern.compile(REGULAREXPRESSION_IP);
			Matcher matcher = pattern.matcher(str);
			if (!matcher.find()) {
				if (StringUtils.equalsIgnoreCase(DomainTools.getMainDomain(str), str)) {
					return str;
				} else {
					return "." + StringUtils.substringAfter(str, ".");
				}
			}
		}
		return str;
	}

	private void setAttribute(HttpServletRequest request, EffectivePerson effectivePerson) {
		request.setAttribute(X_PERSON, effectivePerson);
		request.setAttribute(X_DISTINGUISHEDNAME, effectivePerson.getDistinguishedName());
	}

	public static String remoteAddress(HttpServletRequest request) {
		String value = Objects.toString(request.getHeader("X-Forwarded-For"), "");
		if (StringUtils.isEmpty(value)) {
			value = Objects.toString(request.getRemoteAddr(), "");
		}
		return value;
	}

	public static String getClient(HttpServletRequest request) {
		if(request == null){
			return CLIENT_H5;
		}
		String xClient = request.getHeader(X_CLIENT);
		if (StringUtils.isNotBlank(xClient)) {
			xClient = xClient.toLowerCase();
			if (xClient.indexOf("android") != -1) {
				return CLIENT_APP;
			}
			if (xClient.indexOf("ios") != -1) {
				return CLIENT_APP;
			}
		}
		return CLIENT_H5;
	}

	private String userAgent(HttpServletRequest request) {
		return Objects.toString(request.getHeader("User-Agent"), "");
	}

}
