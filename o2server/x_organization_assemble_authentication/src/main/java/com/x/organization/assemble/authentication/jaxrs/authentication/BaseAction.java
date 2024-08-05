package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.cache.CacheManager;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.organization.assemble.authentication.ThisApplication;
import com.x.organization.core.entity.enums.PersonStatusEnum;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.LdapTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.MD5Tool;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

	protected static final String OAUTH_ACCESSTOKEN = "access_token";
	protected static final String OAUTH_CLIENTID = "clientId";
	protected static final String OAUTH_CLIENTSECRET = "clientSecret";
	protected static final String OAUTH_CODE = "code";
	protected static final String OAUTH_REDIRECTURI = "redirectUri";
	protected static final String CUSTOM_SMS_APPLICATION = "x_sms_assemble_control";
	protected static final String CUSTOM_SMS_CONFIG_NAME = "custom_sms";

	private static final Type OAUTH_PARAMTYPE = new TypeToken<Map<String, Object>>() {
	}.getType();

	/** 管理员通过密码登录 */
	<T extends AbstractWoAuthentication> T manager(HttpServletRequest request, HttpServletResponse response,
			String credential, Class<T> cls) throws Exception {
		HttpToken httpToken = new HttpToken();
		TokenType tokenType = TokenType.manager;
		if (BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			tokenType = Config.ternaryManagement().getTokenType(credential);
		}
		EffectivePerson effectivePerson = new EffectivePerson(credential, tokenType, HttpToken.getClient(request),
				Config.token().getCipher(), Config.person().getEncryptType());
		if ((null != request) && (null != response)) {
			httpToken.setToken(request, response, effectivePerson);
		}
		T t = cls.getDeclaredConstructor().newInstance();
		if (BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			Config.ternaryManagement().initialManagerInstance(credential).copyTo(t);
		} else {
			Config.token().initialManagerInstance().copyTo(t);
		}
		t.setTokenType(tokenType);
		t.setToken(effectivePerson.getToken());
		return t;
	}

	/** 创建普通用户返回信息 */
	<T extends AbstractWoAuthentication> T user(HttpServletRequest request, HttpServletResponse response,
			Business business, Person person, Class<T> cls) throws Exception {
		T t = cls.getDeclaredConstructor().newInstance();
		if (this.failureLocked(person)) {
			throw new ExceptionFailureLocked(DateTools.format(person.getLockExpireTime()));
		}
		if (PersonStatusEnum.BAN.getValue().equals(person.getStatus())) {
			throw new ExceptionFailureBanned();
		}
		person.copyTo(t, Person.password_FIELDNAME, Person.pinyin_FIELDNAME, Person.pinyinInitial_FIELDNAME,
				Person.icon_FIELDNAME, Person.iconLdpi_FIELDNAME, Person.iconMdpi_FIELDNAME);
		HttpToken httpToken = new HttpToken();
		TokenType tokenType = TokenType.user;
		List<String> roles = business.organization().role().listWithPerson(person.getDistinguishedName());
		if (roles.contains(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.Manager))) {
			tokenType = TokenType.manager;
		} else if (roles.contains(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.SystemManager))) {
			tokenType = TokenType.systemManager;
		} else if (roles.contains(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.SecurityManager))) {
			tokenType = TokenType.securityManager;
		} else if (roles.contains(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.AuditManager))) {
			tokenType = TokenType.auditManager;
		}
		EffectivePerson effectivePerson = new EffectivePerson(person.getDistinguishedName(), tokenType,
				HttpToken.getClient(request), Config.token().getCipher(), Config.person().getEncryptType());
		if ((null != request) && (null != response)) {
			if (!isMoaTerminal(request)) {
				String clientIp = HttpToken.remoteAddress(request);
				LOGGER.debug("{} client ip is : {}", person.getDistinguishedName(), clientIp);
				if (!this.checkIp(clientIp, person.getIpAddress())) {
					throw new ExceptionInvalidIpAddress(clientIp);
				}
			}
			httpToken.setToken(request, response, effectivePerson);
		}
		t.setToken(effectivePerson.getToken());
		t.setTokenType(tokenType);
		/** 添加角色 */
		t.setRoleList(roles);
		/** 添加身份 */
		t.setIdentityList(listIdentity(business, person.getId()));
		/** 判断密码是否过期需要修改密码 */
		this.passwordExpired(t);
		this.recordLogin(person.getDistinguishedName(), HttpToken.remoteAddress(request),
				request.getHeader(HttpToken.X_CLIENT));
		return t;
	}

	protected void recordLogin(String name, String address, String client) throws Exception {
		QueueLoginRecord.LoginRecord o = new QueueLoginRecord.LoginRecord();
		o.setAddress(Objects.toString(address, ""));
		o.setClient(Objects.toString(client, ""));
		o.setName(Objects.toString(name, ""));
		o.setDate(new Date());
		ThisApplication.queueLoginRecord.send(o);
	}

	protected List<String> listWithCredential(Business business, String credential) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.name), credential);
		p = cb.or(p, cb.equal(root.get(Person_.distinguishedName), credential));
		p = cb.or(p, cb.equal(root.get(Person_.unique), credential));
		p = cb.or(p, cb.equal(root.get(Person_.id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.mail), credential));
		p = cb.or(p, cb.equal(root.get(Person_.qq), credential));
		p = cb.or(p, cb.equal(root.get(Person_.weixin), credential));
		p = cb.or(p, cb.equal(root.get(Person_.mobile), credential));
		p = cb.or(p, cb.equal(root.get(Person_.employee), credential));
		p = cb.or(p, cb.equal(root.get(Person_.mpwxopenId), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open1Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open2Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open3Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open4Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open5Id), credential));
		cq.select(root.get(Person_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	protected String password(String password) throws Exception {
		return BooleanUtils.isTrue(Config.token().getRsaEnable()) ? Crypto.rsaDecrypt(password, Config.privateKey())
				: password;
	}

	protected Person personLogin(Business business, String id, String password, String credential) throws Exception {
		Person person = business.entityManagerContainer().find(id, Person.class);
		if (null == person) {
			return null;
		}
		if (PersonStatusEnum.BAN.getValue().equals(person.getStatus())) {
			throw new ExceptionFailureBanned();
		}
		if (BooleanUtils.isTrue(Config.person().getSuperPermission())
				&& StringUtils.equals(Config.token().getPassword(), password)) {
			LOGGER.warn("user: {} use superPermission.", person.getName());
			return person;
		}
		if (this.failureLocked(person)) {
			throw new ExceptionFailureLocked(DateTools.format(person.getLockExpireTime()));
		}

		if (validatePassword(person, password, credential)) {
			return person;
		} else {
			business.entityManagerContainer().beginTransaction(Person.class);
			this.failure(person);
			business.entityManagerContainer().commit();
			CacheManager.notify(Person.class);
			return null;
		}
	}

	Person peopleLogin(Business business, List<String> people, String password, String credential) throws Exception {
		for (String id : people) {
			Person person = business.entityManagerContainer().find(id, Person.class);
			if (PersonStatusEnum.isNormal(person.getStatus()) && validatePassword(person, password, credential)) {
				return person;
			}
		}
		return null;
	}

	protected boolean validatePassword(Person person, String password, String credential) throws Exception {
		if (BooleanUtils.isTrue(Config.token().getLdapAuth().getEnable())) {
			return LdapTools.auth(credential, password);
		}
		return (StringUtils.equals(Crypto.encrypt(password, Config.token().getKey(), Config.person().getEncryptType()),
				person.getPassword()) || StringUtils.equals(MD5Tool.getMD5Str(password), person.getPassword()));
	}

	public abstract static class AbstractWoAuthentication extends Person {

		private static final long serialVersionUID = 6043043594889691395L;
		@FieldDescribe("令牌类型")
		private TokenType tokenType;
		@FieldDescribe("令牌")
		private String token;
		@FieldDescribe("角色")
		private List<String> roleList;
		@FieldDescribe("口令是否过期")
		private Boolean passwordExpired;
		@FieldDescribe("身份")
		private List<WoIdentity> identityList;

		public TokenType getTokenType() {
			return tokenType;
		}

		public void setTokenType(TokenType tokenType) {
			this.tokenType = tokenType;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

		public Boolean getPasswordExpired() {
			return passwordExpired;
		}

		public void setPasswordExpired(Boolean passwordExpired) {
			this.passwordExpired = passwordExpired;
		}

		public List<WoIdentity> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<WoIdentity> identityList) {
			this.identityList = identityList;
		}

	}

	public static class WoIdentity extends Identity {

		private static final long serialVersionUID = 1844319285073456448L;

		static WrapCopier<Identity, WoIdentity> copier = WrapCopierFactory.wo(Identity.class, WoIdentity.class, null,
				JpaObject.FieldsInvisible);

	}

	private void passwordExpired(AbstractWoAuthentication wo) throws Exception {
		wo.setPasswordExpired(false);
		if (Config.person().getFirstLoginModifyPwd()) {
			if (wo.getChangePasswordTime() == null) {
				wo.setPasswordExpired(true);
				return;
			}
		}
		Integer passwordPeriod = Config.person().getPasswordPeriod();
		if (passwordPeriod.intValue() == 0) {
			return;
		}
		if (null != wo.getPasswordExpiredTime()) {
			if (wo.getPasswordExpiredTime().getTime() < (new Date()).getTime()) {
				wo.setPasswordExpired(true);
			}
		} else if (wo.getChangePasswordTime() != null) {
			Date date = DateTools.addDay(wo.getChangePasswordTime(), passwordPeriod);
			if (date.getTime() < (new Date()).getTime()) {
				wo.setPasswordExpired(true);
			}
		}
	}

	private List<WoIdentity> listIdentity(Business business, String personId) throws Exception {
		List<String> ids = business.identity().listWithPerson(personId);
		List<WoIdentity> list = business.entityManagerContainer().fetch(ids, WoIdentity.copier);
		list = list.stream().sorted(Comparator.comparing(WoIdentity::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	protected OauthClient oauthClient(String name) throws Exception {
		if (ListTools.isNotEmpty(Config.token().getOauthClients())) {
			for (OauthClient o : Config.token().getOauthClients()) {
				if (BooleanUtils.isTrue(o.getEnable()) && StringUtils.equals(o.getName(), name)) {
					return o;
				}
			}
		}
		throw new ExceptionOauthNotExist(name);
	}

	protected Map<String, Object> oauthCreateParam(OauthClient oauthClient, String code, String redirectUri) {
		Map<String, Object> param = new HashMap<>();
		param.put(OAUTH_CLIENTID, oauthClient.getClientId());
		param.put(OAUTH_CODE, code);
		param.put(OAUTH_REDIRECTURI, redirectUri);
		param.put(OAUTH_CLIENTSECRET, oauthClient.getClientSecret());
		return param;
	}

	protected String oauthClientTokenGet(OauthClient oauthClient, Map<String, Object> param) throws Exception {
		String address = oauthClient.getTokenAddress();
		String parameter = fillParameter(oauthClient.getTokenParameter(), param);
		if (StringUtils.contains(address, "?")) {
			address = address + "&" + parameter;
		} else {
			address = address + "?" + parameter;
		}
		LOGGER.debug("token get address:{}.", address);
		return HttpConnection.getAsString(address, null);
	}

	protected String oauthClientTokenPost(OauthClient oauthClient, Map<String, Object> param) throws Exception {
		String address = oauthClient.getTokenAddress();
		String parameter = fillParameter(oauthClient.getTokenParameter(), param);
		LOGGER.debug("token post address:{}.", address);
		LOGGER.debug("token post parameter:{}.", parameter);
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"));
		return HttpConnection.postAsString(address, heads, parameter);
	}

	protected String oauthClientInfoGet(OauthClient oauthClient, Map<String, Object> param) throws Exception {
		String address = oauthClient.getInfoAddress();
		String parameter = fillParameter(oauthClient.getInfoParameter(), param);
		if (StringUtils.contains(address, "?")) {
			address = address + "&" + parameter;
		} else {
			address = address + "?" + parameter;
		}
		return HttpConnection.getAsString(address, null);
	}

	protected String oauthClientInfoPost(OauthClient oauthClient, Map<String, Object> param) throws Exception {
		String address = oauthClient.getInfoAddress();
		String parameter = fillParameter(oauthClient.getInfoParameter(), param);
		LOGGER.debug("info post address:{}.", address);
		LOGGER.debug("info post parameter:{}.", parameter);
		return HttpConnection.postAsString(address, null, parameter);
	}

	protected void oauthToken(OauthClient oauthClient, Map<String, Object> param) throws Exception {
		String body = null;
		if (StringUtils.equalsIgnoreCase("post", oauthClient.getTokenMethod())) {
			body = this.oauthClientTokenPost(oauthClient, param);
		} else {
			body = this.oauthClientTokenGet(oauthClient, param);
		}
		LOGGER.debug("body:{}", body);
		if (StringUtils.equalsIgnoreCase(oauthClient.getTokenType(), "json")) {
			param.putAll(gson.fromJson(body, OAUTH_PARAMTYPE));
		} else {
			Arrays.stream(StringUtils.split(body, "&")).forEach(o -> {
				String[] values = StringUtils.split(o, "=");
				param.put(values[0], values[1]);
			});
		}
	}

	protected void oauthCheckAccessToken(Map<String, Object> param) throws ExceptionOauthEmptyAccessToken {
		if (StringUtils.isEmpty(Objects.toString(param.get(OAUTH_ACCESSTOKEN)))) {
			throw new ExceptionOauthEmptyAccessToken();
		}
	}

	protected void oauthCheckCredential(String credential) throws ExceptionOauthEmptyCredential {
		if (StringUtils.isEmpty(credential)) {
			throw new ExceptionOauthEmptyCredential();
		}
	}

	protected void oauthInfo(OauthClient oauthClient, Map<String, Object> param) throws Exception {
		if (StringUtils.isBlank(oauthClient.getInfoAddress())) {
			return;
		}
		String body = null;
		if (StringUtils.equalsIgnoreCase("post", oauthClient.getInfoMethod())) {
			body = this.oauthClientInfoPost(oauthClient, param);
		} else {
			body = this.oauthClientInfoGet(oauthClient, param);
		}
		LOGGER.debug("infoBody:{}", body);
		if (StringUtils.isEmpty(body)) {
			throw new ExceptionOauthEmptyInfo();
		}
		if (StringUtils.equalsIgnoreCase(oauthClient.getInfoType(), "json")) {
			param.putAll(gson.fromJson(body, OAUTH_PARAMTYPE));
		} else if (StringUtils.equalsIgnoreCase(oauthClient.getInfoType(), "form")) {
			Arrays.stream(StringUtils.split(body, "&")).forEach(o -> {
				String[] values = StringUtils.split(o, "=");
				param.put(values[0], values[1]);
			});
		} else {
			LOGGER.debug("info script:{}.", oauthClient.getInfoScriptText());
			Source source = GraalvmScriptingFactory.functionalization(oauthClient.getInfoScriptText());
			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_TEXT, body);
			GraalvmScriptingFactory.eval(source, bindings, jsonElement -> {
				Map<String, Object> info = XGsonBuilder.instance().fromJson(jsonElement, OAUTH_PARAMTYPE);
				param.putAll(info);
			});
		}
	}

	protected String fillParameter(String txt, Map<String, Object> param) {
		Pattern pattern = Pattern.compile("\\{\\$(.+?)\\}");
		Matcher matcher = pattern.matcher(txt);
		while (matcher.find()) {
			Object value = param.get(matcher.group(1));
			if (null != value) {
				txt = StringUtils.replace(txt, matcher.group(), Objects.toString(value));
			}
		}
		return txt;
	}

	protected boolean failureLocked(Person person) {
		return PersonStatusEnum.LOCK.getValue().equals(person.getStatus()) && person.getLockExpireTime() != null
				&& person.getLockExpireTime().getTime() > System.currentTimeMillis();
	}

	protected void failure(Person person) throws Exception {
		Integer failureInterval = Config.person().getFailureInterval();
		if (!DateTools.beforeNowMinutesNullIsTrue(person.getFailureTime(), failureInterval)) {
			person.setFailureCount(person.getFailureCount() + 1);
			if (person.getFailureCount() >= Config.person().getFailureCount()) {
				person.setStatus(PersonStatusEnum.LOCK.getValue());
				person.setLockExpireTime(DateTools.addMinutes(new Date(), failureInterval));
				person.setStatusDes("登录失败超限次");
			}
		} else {
			person.setFailureCount(1);
			person.setFailureTime(new Date());
		}
	}

	protected boolean checkIp(String clientIp, String ipAddress) {
		boolean returnValue = true;
		if (StringUtils.isNotEmpty(clientIp) && StringUtils.isNotEmpty(ipAddress)) {
			try {
				String[] ipAddressArr = StringUtils.split(ipAddress, ",");
				for (String regIp : ipAddressArr) {
					if (StringUtils.isNotEmpty(regIp)) {
						Pattern pattern = Pattern.compile(regIp.trim());
						Matcher matcher = pattern.matcher(clientIp);
						returnValue = matcher.find();
						if (returnValue) {
							break;
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		return returnValue;
	}

	protected boolean isMoaTerminal(HttpServletRequest request) {
		String xClient = request.getHeader(HttpToken.X_CLIENT);
		if (StringUtils.isNotBlank(xClient)) {
			xClient = xClient.toLowerCase();
			if (xClient.indexOf("android") != -1) {
				// 安卓
				return true;
			}
			if (xClient.indexOf("ios") != -1) {
				// 安卓
				return true;
			}
		}
		String userAgent = request.getHeader("User-Agent");
		if (StringUtils.isNotBlank(userAgent)) {
			userAgent = userAgent.toLowerCase();
			if (userAgent.indexOf("micromessenger") != -1) {
				// 微信
				return true;
			}
			if (userAgent.indexOf("dingtalk") != -1) {
				// 钉钉
				return true;
			}
			if (userAgent.indexOf("android") != -1) {
				// 安卓
				return true;
			}
			if (userAgent.indexOf("iphone") != -1 || userAgent.indexOf("ipad") != -1
					|| userAgent.indexOf("ipod") != -1) {
				// 苹果
				return true;
			}
		}
		return false;
	}

}
