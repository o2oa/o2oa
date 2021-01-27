package com.x.program.center.jaxrs.invoke;

import java.net.URLDecoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Invoke;

class ActionExecuteToken extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecuteToken.class);

	private static final String SPLIT = "#";

	ActionResult<Object> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag,
			String client, String token, JsonElement jsonElement) throws Exception {

		CacheCategory cacheCategory = new CacheCategory(Invoke.class);

		Invoke invoke = this.get(cacheCategory, flag);

		if (null == invoke) {
			throw new ExceptionEntityNotExist(flag, Invoke.class);
		}

		if (!BooleanUtils.isTrue(invoke.getEnable())) {
			throw new ExceptionNotEnable(invoke.getName());
		}

		if (StringUtils.isNotEmpty(invoke.getRemoteAddrRegex())) {
			Matcher matcher = Pattern.compile(invoke.getRemoteAddrRegex()).matcher(request.getRemoteAddr());
			if (!matcher.find()) {
				throw new ExceptionInvalidRemoteAddr(request.getRemoteAddr(), invoke.getName());
			}
		}

		if (StringUtils.isEmpty(client)) {
			throw new ExceptionClientEmpty();
		}
		if (StringUtils.isEmpty(token)) {
			throw new ExceptionTokenEmpty();
		}
		Sso sso = Config.token().findSso(client);
		if (null == sso) {
			throw new ExceptionClientNotExist(client);
		}
		String content = null;
		logger.debug("decrypt sso client:{}, token:{}, key:{}.", client, token, sso.getKey());
		try {
			content = Crypto.decrypt(token, sso.getKey());
			logger.debug("decrypt sso client:{}, token:{}, key:{}, content:{}.", client, token, sso.getKey(), content);
		} catch (Exception e) {
			throw new ExceptionReadToken(client, token);
		}
		String credential = URLDecoder.decode(StringUtils.substringBefore(content, SPLIT),
				DefaultCharset.name_iso_utf_8);
		String timeString = StringUtils.substringAfter(content, SPLIT);
		if (StringUtils.isEmpty(credential)) {
			throw new ExceptionEmptyCredential();
		}
		Date date = new Date(Long.parseLong(timeString));
		Date now = new Date();
		// 15分钟
		if (Math.abs((now.getTime() - date.getTime())) >= (60000 * 15)) {
			throw new ExceptionTokenExpired();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String person = business.organization().person().get(credential);
			if (StringUtils.isEmpty(person)) {
				throw new ExceptionPersonNotExist(credential);
			}
		}
		return executeInvoke(request, effectivePerson, jsonElement, cacheCategory, invoke);
	}

}