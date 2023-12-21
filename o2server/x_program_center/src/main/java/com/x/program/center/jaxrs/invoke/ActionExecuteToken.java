package com.x.program.center.jaxrs.invoke;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.Sso;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.Crypto;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Invoke;

class ActionExecuteToken extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecuteToken.class);

	// 时间限制
	private static final int THRESHOLD = 60000 * 30;

	ActionResult<Object> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag,
			String client, String token, JsonElement jsonElement) throws Exception {

		LOGGER.debug("{} invoke :{}.", effectivePerson.getDistinguishedName(), flag);

		CacheCategory cacheCategory = new CacheCategory(Invoke.class);

		Invoke invoke = this.get(cacheCategory, flag);

		if (null == invoke) {
			throw new ExceptionEntityNotExist(flag, Invoke.class);
		}

		checkEnable(invoke);

		checkRemoteAddrRegex(request, invoke);

		checkClient(client);

		checkToken(token);

		Sso sso = Config.token().findSso(client);
		if (null == sso) {
			throw new ExceptionClientNotExist(client);
		}
		String content = decrypt(client, token, sso);
		checkTimeThreshold(StringUtils.substringAfter(content, SPLIT));
		String credential = URLDecoder.decode(StringUtils.substringBefore(content, SPLIT),
				StandardCharsets.UTF_8.name());
		if (StringUtils.isEmpty(credential)) {
			throw new ExceptionEmptyCredential();
		}
		if ((!StringUtils.equals(EffectivePerson.CIPHER, credential))
				&& (!Config.token().isInitialManager(credential))) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String person = business.organization().person().get(credential);
				if (StringUtils.isEmpty(person)) {
					throw new ExceptionPersonNotExist(credential);
				}
				List<String> roles = business.organization().role().listWithPerson(person);
				TokenType tokenType = TokenType.user;
				if (roles.contains(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.Manager))) {
					tokenType = TokenType.manager;
				} else if (roles
						.contains(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.SystemManager))) {
					tokenType = TokenType.systemManager;
				} else if (roles
						.contains(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.SecurityManager))) {
					tokenType = TokenType.securityManager;
				} else if (roles
						.contains(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.AuditManager))) {
					tokenType = TokenType.auditManager;
				}
				effectivePerson = new EffectivePerson(person, tokenType, Config.token().getCipher(),
						Config.person().getEncryptType());
			}
		}
		return execute(request, effectivePerson, jsonElement, cacheCategory, invoke);
	}

	private void checkTimeThreshold(String time) throws ExceptionTokenExpired {
		Date date = new Date(Long.parseLong(time));
		Date now = new Date();
		if (Math.abs((now.getTime() - date.getTime())) >= THRESHOLD) {
			throw new ExceptionTokenExpired();
		}
	}

	private String decrypt(String client, String token, Sso sso) throws ExceptionReadToken {
		String value = "";
		try {
			value = Crypto.decrypt(token, sso.getKey(), Config.person().getEncryptType());
			LOGGER.debug("decrypt sso client:{}, token:{}, key:{}, content:{}.", client::toString, token::toString,
					sso::getKey, value::toString);
		} catch (Exception e) {
			throw new ExceptionReadToken(client, token);
		}
		return value;
	}

}
