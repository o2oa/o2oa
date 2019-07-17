package com.x.base.core.project.http;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;

public class EffectivePerson extends GsonPropertyObject {

	public static final String ANONYMOUS = "anonymous";
	public static final String CIPHER = "cipher";

	private static Pattern person_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@P$");

	private TokenType tokenType;
	private String token = "";
	private String name = "";
	private String unique = "";
	private String distinguishedName = "";
	private Boolean debugger = false;

	private String remoteAddress = "";
	private String uri = "";
	private String userAgent = "";

	private EffectivePerson() {

	}

	public void setUri(String uri) {
		this.uri = Objects.toString(uri, "");
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = Objects.toString(userAgent, "");
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = Objects.toString(remoteAddress, "");
	}

	private void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
		Matcher matcher = person_distinguishedName_pattern.matcher(distinguishedName);
		if (matcher.find()) {
			this.name = matcher.group(1);
			this.unique = matcher.group(2);
		} else {
			this.name = distinguishedName;
			this.unique = distinguishedName;
		}
	}

	public EffectivePerson(String distinguishedName, TokenType tokenType, String key) throws Exception {
		this.setDistinguishedName(distinguishedName);
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
						+ URLEncoder.encode(this.getDistinguishedName(), "utf-8")),
				key);
	}

	public String getDistinguishedName() {
		if (TokenType.cipher.equals(tokenType)) {
			return "cipher";
		}
		return this.distinguishedName;
	}

	public static EffectivePerson anonymous() {
		EffectivePerson effectivePerson = new EffectivePerson();
		effectivePerson.token = "";
		effectivePerson.setDistinguishedName(ANONYMOUS);
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

	public boolean isAnonymous() {
		switch (this.tokenType) {
		case anonymous:
			return true;
		default:
			return false;
		}
	}

	public boolean isCipher() {
		if (Objects.equals(this.tokenType, TokenType.cipher)) {
			return true;
		}
		return false;
	}

	public boolean isNotManager() {
		return !this.isManager();
	}

	public boolean isPerson(Collection<String> names) {
		if (Objects.equals(TokenType.user, this.getTokenType())
				|| Objects.equals(TokenType.manager, this.getTokenType())) {
			if (null != names) {
				List<String> list = new ArrayList<>(names);
				if (list.contains(this.distinguishedName)) {
					return true;
				}
				if (list.contains(this.unique)) {
					return true;
				}
				for (String str : list) {
					if (StringUtils.isNotEmpty(str)) {
						Matcher matcher = person_distinguishedName_pattern.matcher(str);
						if (matcher.find()) {
							if (StringUtils.equalsIgnoreCase(matcher.group(2), this.unique)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isPerson(String... names) {
		return this.isPerson(Arrays.asList(names));
	}

	public boolean isNotPerson(String... names) {
		return !this.isPerson(names);
	}

	public boolean isNotPerson(Collection<String> names) {
		return !this.isPerson(names);
	}

	public String getToken() {
		return token;
	}

	private void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public String getName() {
		return this.name;
	}

	public Boolean getDebugger() {
		return debugger;
	}

	public void setDebugger(Boolean debugger) {
		this.debugger = debugger;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getUri() {
		return uri;
	}

}