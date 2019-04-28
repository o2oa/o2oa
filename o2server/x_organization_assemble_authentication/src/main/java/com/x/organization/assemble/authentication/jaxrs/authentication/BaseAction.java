package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrapout.WrapOutAuthentication;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	static WrapCopier<Person, WrapOutAuthentication> authenticationOutCopier = WrapCopierFactory.wo(Person.class,
			WrapOutAuthentication.class, null, JpaObject.FieldsInvisible);

	/* 管理员通过密码登录 */
	<T extends AbstractWoAuthentication> T manager(HttpServletRequest request, HttpServletResponse response,
			Business business, Class<T> cls) throws Exception {
		HttpToken httpToken = new HttpToken();
		EffectivePerson effectivePerson = new EffectivePerson(Config.token().getInitialManager(), TokenType.manager,
				Config.token().getCipher());
		if ((null != request) && (null != response)) {
			httpToken.setToken(request, response, effectivePerson);
		}
		T t = cls.newInstance();
		Config.token().initialManagerInstance().copyTo(t);
		t.setTokenType(TokenType.manager);
		t.setToken(effectivePerson.getToken());
		return t;
	}

	/** 创建普通用户返回信息 */
	<T extends AbstractWoAuthentication> T user(HttpServletRequest request, HttpServletResponse response,
			Business business, Person person, Class<T> cls) throws Exception {
		T t = cls.newInstance();
		person.copyTo(t, Person.password_FIELDNAME, Person.pinyin_FIELDNAME, Person.pinyinInitial_FIELDNAME);
		HttpToken httpToken = new HttpToken();
		TokenType tokenType = TokenType.user;
		boolean isManager = business.organization().person().hasRole(person.getDistinguishedName(),
				OrganizationDefinition.Manager);
		if (isManager) {
			tokenType = TokenType.manager;
		}
		EffectivePerson effectivePerson = new EffectivePerson(person.getDistinguishedName(), tokenType,
				Config.token().getCipher());
		if ((null != request) && (null != response)) {
			httpToken.setToken(request, response, effectivePerson);
		}
		t.setToken(effectivePerson.getToken());
		t.setTokenType(tokenType);
		/** 添加角色 */
		t.setRoleList(listRole(business, person.getId()));
		/** 添加身份 */
		t.setIdentityList(listIdentity(business, person.getId()));
		/** 判断密码是否过期需要修改密码 */
		this.passwordExpired(t);
		return t;
	}

	public static abstract class AbstractWoAuthentication extends Person {

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
		if ((null != wo.getPasswordExpiredTime()) && (wo.getPasswordExpiredTime().getTime() < (new Date()).getTime())) {
			wo.setPasswordExpired(true);
		} else {
			wo.setPasswordExpired(false);
		}
	}

	private List<String> listRole(Business business, String personId) throws Exception {
		List<String> roles = new ArrayList<>();
		for (Role o : business.entityManagerContainer().fetch(business.role().listWithPerson(personId), Role.class,
				ListTools.toList(Role.DISTINGUISHEDNAME))) {
			roles.add(o.getDistinguishedName());
		}
		return roles;
	}

	private List<WoIdentity> listIdentity(Business business, String personId) throws Exception {
		List<String> ids = business.identity().listWithPerson(personId);
		List<WoIdentity> list = business.entityManagerContainer().fetch(ids, WoIdentity.copier);
		list = list.stream().sorted(Comparator.comparing(WoIdentity::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	protected static String[] AUTH_PARAMETER_TAGS = new String[] { "{$client_id}", "{$client_secret}" };

	protected static String[] TOKEN_PARAMETER_TAGS = new String[] { "{$client_id}", "{$client_secret}",
			"{$redirect_uri}", "{$code}" };

	protected static String[] INFO_PARAMETER_TAGS = new String[] { "{$client_id}", "{$client_secret}",
			"{$redirect_uri}", "{$access_token}", "{$refresh_token}" };

	protected String oauthClientTokenGet(OauthClient oauthClient, String redirectUri, String code) throws Exception {
		String address = oauthClient.getTokenAddress();
		String parameter = fillTokenParameter(oauthClient.getTokenParameter(), oauthClient, redirectUri, code);
		if (StringUtils.contains(address, "?")) {
			address = address + "&" + parameter;
		} else {
			address = address + "?" + parameter;
		}
		logger.debug("token get address:{}.", address);
		String str = HttpConnection.getAsString(address, null);
		return str;
	}

	protected String oauthClientTokenPost(OauthClient oauthClient, String redirectUri, String code) throws Exception {
		String address = oauthClient.getTokenAddress();
		String parameter = fillTokenParameter(oauthClient.getTokenParameter(), oauthClient, redirectUri, code);
		logger.debug("token post address:{}.", address);
		logger.debug("token post parameter:{}.", parameter);
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"));
		String str = HttpConnection.postAsString(address, null, parameter);
		return str;
	}

	protected String oauthClientInfoGet(OauthClient oauthClient, String redirectUri, String accessToken,
			String refreshToken) throws Exception {
		String address = oauthClient.getInfoAddress();
		String parameter = fillInfoParameter(oauthClient.getInfoParameter(), oauthClient, redirectUri, accessToken,
				refreshToken);
		if (StringUtils.contains(address, "?")) {
			address = address + "&" + parameter;
		} else {
			address = address + "?" + parameter;
		}
		String str = HttpConnection.getAsString(address, null);
		return str;
	}

	protected String oauthClientInfoPost(OauthClient oauthClient, String redirectUri, String accessToken,
			String refreshToken) throws Exception {
		String address = oauthClient.getInfoAddress();
		String parameter = fillInfoParameter(oauthClient.getInfoParameter(), oauthClient, redirectUri, accessToken,
				refreshToken);
		logger.debug("info post address:{}.", address);
		logger.debug("info post parameter:{}.", parameter);
		String str = HttpConnection.postAsString(address, null, parameter);
		return str;
	}

	protected OauthClient getOauthClient(String name) throws Exception {
		if (ListTools.isNotEmpty(Config.token().getOauthClients())) {
			for (OauthClient o : Config.token().getOauthClients()) {
				if (BooleanUtils.isTrue(o.getEnable()) && StringUtils.equals(o.getName(), name)) {
					return o;
				}
			}
		}
		return null;
	}

	protected String fillAuthParameter(String url, OauthClient oauthClient) throws Exception {
		return StringUtils.replaceEach(url, AUTH_PARAMETER_TAGS,
				new String[] { oauthClient.getClientId(), oauthClient.getClientSecret() });
	}

	protected String fillTokenParameter(String url, OauthClient oauthClient, String redirectUri, String code)
			throws Exception {
		return StringUtils.replaceEach(url, TOKEN_PARAMETER_TAGS,
				new String[] { oauthClient.getClientId(), oauthClient.getClientSecret(), redirectUri, code });
	}

	protected String fillInfoParameter(String url, OauthClient oauthClient, String redirectUri, String accessToken,
			String refreshToken) throws Exception {
		return StringUtils.replaceEach(url, INFO_PARAMETER_TAGS, new String[] { oauthClient.getClientId(),
				oauthClient.getClientSecret(), redirectUri, accessToken, refreshToken });
	}

	protected boolean failureLocked(Person person) throws Exception {
		if ((person.getFailureCount() != null) && (person.getFailureCount() >= Config.person().getFailureCount())) {
			if (!DateTools.beforeNowMinutesNullIsTrue(person.getFailureTime(), Config.person().getFailureInterval())) {
				return true;
			}
		}
		return false;
	}

	protected void failure(Person person) throws Exception {
		if (!DateTools.beforeNowMinutesNullIsTrue(person.getFailureTime(), Config.person().getFailureInterval())) {
			person.setFailureCount(person.getFailureCount() + 1);
		} else {
			person.setFailureCount(1);
			person.setFailureTime(new Date());
		}
	}

}