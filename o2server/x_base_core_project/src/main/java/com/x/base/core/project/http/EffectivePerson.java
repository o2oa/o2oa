package com.x.base.core.project.http;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;

public class EffectivePerson extends GsonPropertyObject {

	private static final Logger LOGGER = LoggerFactory.getLogger(EffectivePerson.class);

	private static final long serialVersionUID = -6961607633719115852L;

	public static final String ANONYMOUS = "anonymous";
	public static final String CIPHER = "cipher";

	private static final Pattern PERSON_DISTINGUISHEDNAME_PATTERN = Pattern.compile("^(\\S+)\\@(\\S+)\\@P$");

	private TokenType tokenType;
	private String token = "";
	private String name = "";
	private String unique = "";
	private String distinguishedName = "";
	private Boolean debugger = false;

	private String remoteAddress = "";
	private String uri = "";
	private String userAgent = "";
	private String client = "";

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
		Matcher matcher = PERSON_DISTINGUISHEDNAME_PATTERN.matcher(distinguishedName);
		if (matcher.find()) {
			this.name = matcher.group(1);
			this.unique = matcher.group(2);
		} else {
			this.name = distinguishedName;
			this.unique = distinguishedName;
		}
	}

	public EffectivePerson(String distinguishedName, TokenType tokenType, String key) throws Exception {
		this(distinguishedName, tokenType, key, Config.person().getEncryptType());
	}

	public EffectivePerson(String distinguishedName, TokenType tokenType, String key, String encryptType) throws Exception{
		this(distinguishedName, tokenType, HttpToken.CLIENT_H5, key, encryptType);
	}

	public EffectivePerson(String distinguishedName, TokenType tokenType, String client, String key, String encryptType)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		this.setDistinguishedName(distinguishedName);
		this.tokenType = tokenType;
		this.client = client;
		switch (this.tokenType) {
			case anonymous:
				this.token = null;
				break;
			case user:
				this.token = this.concreteToken(key, encryptType);
				break;
			case manager:
				this.token = this.concreteToken(key, encryptType);
				break;
			case systemManager:
				this.token = this.concreteToken(key, encryptType);
				break;
			case securityManager:
				this.token = this.concreteToken(key, encryptType);
				break;
			case auditManager:
				this.token = this.concreteToken(key, encryptType);
				break;
			case cipher:
				this.token = this.concreteToken(key, encryptType);
				break;
		}
	}

	private String concreteToken(String key, String encryptType)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return Crypto.encrypt(this.getTokenType().toString()
				+ (DateFormatUtils.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss)
						+ Objects.toString(this.client, HttpToken.CLIENT_H5)
						+ URLEncoder.encode(this.getDistinguishedName(), "utf-8")),
				key, encryptType);
	}

	public String getDistinguishedName() {
		if (TokenType.cipher.equals(tokenType)) {
			return CIPHER;
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

	public static EffectivePerson cipher(String key, String encryptType)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return new EffectivePerson(CIPHER, TokenType.cipher, HttpToken.CLIENT_H5, key, encryptType);
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public boolean isManager() {
		switch (this.tokenType) {
		case manager:
			return true;
		case systemManager:
			return true;
		case cipher:
			return true;
		default:
			return false;
		}
	}

	public boolean isSecurityManager() {
		switch (this.tokenType) {
		case manager:
			return true;
		case securityManager:
			return true;
		case cipher:
			return true;
		default:
			return false;
		}
	}

	public boolean isTernaryManager() {
		switch (this.tokenType) {
		case systemManager:
		case securityManager:
		case auditManager:
			return true;
		default:
			return false;
		}
	}

	public boolean isAnonymous() {
		return Objects.equals(this.tokenType, TokenType.anonymous);
	}

	public boolean isCipher() {
		return Objects.equals(this.tokenType, TokenType.cipher);
	}

	public boolean isNotManager() {
		return !this.isManager();
	}

	public boolean isPerson(Collection<String> names) {
		if ((Objects.equals(TokenType.user, this.getTokenType())
				|| Objects.equals(TokenType.manager, this.getTokenType())
				|| Objects.equals(TokenType.systemManager, this.getTokenType())
				|| Objects.equals(TokenType.auditManager, this.getTokenType())
				|| Objects.equals(TokenType.securityManager, this.getTokenType())) && (null != names)) {
			if (names.contains(this.distinguishedName) || names.contains(this.unique)) {
				return true;
			}
			Optional<String> optional = names.stream().filter(StringUtils::isNotEmpty).filter(s -> {
				Matcher matcher = PERSON_DISTINGUISHEDNAME_PATTERN.matcher(s);
				return matcher.find() && StringUtils.equalsIgnoreCase(matcher.group(2), this.unique);
			}).findFirst();
			return optional.isPresent();
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

	void setToken(String token) {
		this.token = token;
	}

	private void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public String getUnique() {
		return unique;
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

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}
}
