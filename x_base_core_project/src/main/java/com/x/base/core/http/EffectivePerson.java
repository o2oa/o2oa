package com.x.base.core.http;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.x.base.core.Crypto;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.utils.DateTools;

public class EffectivePerson extends GsonPropertyObject {

	public static final String ANONYMOUS = "anonymous";
	public static final String CIPHER = "cipher";

	private String name = "";
	private TokenType tokenType;
	private String token;

	private EffectivePerson() {

	}

	public EffectivePerson(String name, TokenType tokenType, String key) throws Exception {
		this.name = name;
		this.tokenType = tokenType;
		switch (this.tokenType) {
		case anonymous:
			this.token = null;
			break;
		case user:
			this.token = this.concreteToken(key);
			break;
		case manager:
			this.token = this.concreteToken(key);
			break;
		case cipher:
			this.token = this.concreteToken(key);
			break;

		}
	}

	private String concreteToken(String key) throws Exception {
		return Crypto.encrypt(this.getTokenType().toString()
				+ (DateFormatUtils.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss)
						+ URLEncoder.encode(this.getName(), "utf-8")),
				key);
	}

	public String getName() {
		if (TokenType.cipher.equals(tokenType)) {
			return "cipher";
		}
		return name;
	}

	public static EffectivePerson anonymous() {
		EffectivePerson effectivePerson = new EffectivePerson();
		effectivePerson.setToken(null);
		effectivePerson.setName(ANONYMOUS);
		effectivePerson.setTokenType(TokenType.anonymous);
		return effectivePerson;
	}

	public static EffectivePerson cipher(String key) throws Exception {
		EffectivePerson effectivePerson = new EffectivePerson(CIPHER, TokenType.cipher, key);
		return effectivePerson;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public boolean isManager() {
		switch (this.tokenType) {
		case manager:
			return true;
		case cipher:
			return true;
		default:
			return false;
		}
	}

	public boolean isNotManager() {
		return !this.isManager();
	}

	public boolean isUser(String... names) {
		/* 仅判断普通用户,管理员单独判断 */
		if (Objects.equals(TokenType.user, this.getTokenType())) {
			return ArrayUtils.contains(names, this.name);
		}
		return false;
	}

	public boolean isNotUser(String... names) {
		return !this.isUser(names);
	}

	public boolean isUser(Collection<String> names) {
		/* 仅判断普通用户,管理员单独判断 */
		if (Objects.equals(TokenType.user, this.getTokenType())) {
			if (null != names) {
				for (String str : names) {
					if (StringUtils.equals(str, this.name)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isNotUser(Collection<String> names) {
		return !this.isUser(names);
	}

	public String getToken() {
		return token;
	}

	private void setToken(String token) {
		this.token = token;
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

}