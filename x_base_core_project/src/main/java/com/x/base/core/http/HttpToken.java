package com.x.base.core.http;

import java.net.URLDecoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.Crypto;
import com.x.base.core.utils.DateTools;

public class HttpToken {

	public static final String X_Token = "x-token";
	public static final String X_Person = "x-person";
	private static final String RegularExpression_IP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
	private static final String RegularExpression_Token = "^(anonymous|user|manager|cipher)([2][0][1-2][0-9][0-1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9])(\\S{1,})$";

	public EffectivePerson who(HttpServletRequest request, HttpServletResponse response, String key) throws Exception {
		EffectivePerson effectivePerson = this.who(this.getToken(request), key);
		setAttribute(request, effectivePerson);
		setToken(request, response, effectivePerson);
		return effectivePerson;
	}

	public EffectivePerson who(String token, String key) {
		if (StringUtils.isBlank(token)) {
			return EffectivePerson.anonymous();
		}
		try {
			token = Crypto.decrypt(token, key);
			Pattern pattern = Pattern.compile(RegularExpression_Token, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(token);
			if (!matcher.find()) {
				throw new Exception("token format error." + token);
			}
			Date date = DateUtils.parseDate(matcher.group(2), DateTools.formatCompact_yyyyMMddHHmmss);
			TokenType tokenType = TokenType.valueOf(matcher.group(1));
			long diff = (new Date().getTime() - date.getTime());
			diff = Math.abs(diff);
			if (TokenType.user.equals(tokenType) || TokenType.manager.equals(tokenType)) {
				if (diff > (60000 * 60 * 12)) {
					throw new Exception("token expired." + token);
				}
			}
			if (TokenType.cipher.equals(tokenType)) {
				if (diff > (60000 * 20)) {
					throw new Exception("cipher token expired." + token);
				}
			}
			EffectivePerson effectivePerson = new EffectivePerson(URLDecoder.decode(matcher.group(3), "utf-8"),
					tokenType, key);
			return effectivePerson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EffectivePerson.anonymous();
	}

	public void deleteToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String cookie = X_Token + "=; path=/; domain=" + this.domain(request) + "; max-age=0";
			response.setHeader("Set-Cookie", cookie);
		} catch (Exception e) {
			throw new Exception("delete Token cookie error.", e);
		}
	}

	public void setToken(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson)
			throws Exception {
		switch (effectivePerson.getTokenType()) {
		case anonymous:
			this.deleteToken(request, response);
			break;
		case user:
			this.setResponseToken(request, response, effectivePerson);
			break;
		case manager:
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
		String cookie = X_Token + "=" + effectivePerson.getToken() + "; path=/; domain=" + this.domain(request);
		response.setHeader("Set-Cookie", cookie);
		response.setHeader(X_Token, effectivePerson.getToken());
	}

	public String getToken(HttpServletRequest request) throws Exception {
		String token = null;
		if (null != request.getCookies()) {
			for (Cookie c : request.getCookies()) {
				if (StringUtils.equals(X_Token, c.getName())) {
					token = c.getValue();
					break;
				}
			}
		}
		if (StringUtils.isEmpty(token)) {
			token = request.getParameter(X_Token);
		}
		if (StringUtils.isEmpty(token)) {
			token = request.getHeader(X_Token);
		}
		if (StringUtils.isEmpty(token)) {
			token = request.getParameter(X_Token);
		}
		return token;
	}

	private String domain(HttpServletRequest request) throws Exception {
		String str = request.getServerName();
		if (StringUtils.contains(str, ".")) {
			Pattern pattern = Pattern.compile(RegularExpression_IP);
			Matcher matcher = pattern.matcher(str);
			if (!matcher.find()) {
				return "." + StringUtils.substringAfter(str, ".");
			}
		}
		return str;
	}

	private void setAttribute(HttpServletRequest request, EffectivePerson effectivePerson) {
		request.setAttribute(X_Person, effectivePerson);
	}
}