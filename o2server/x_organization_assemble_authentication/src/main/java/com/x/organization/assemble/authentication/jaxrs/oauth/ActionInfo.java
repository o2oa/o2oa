package com.x.organization.assemble.authentication.jaxrs.oauth;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.InitialManager;
import com.x.base.core.project.config.Token.Oauth;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.OauthCode;
import com.x.organization.core.entity.Person;

class ActionInfo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionInfo.class);

	public static final Pattern SCRIPT_PATTERN = Pattern.compile("^\\((.+?)\\)$");

	private static CacheCategory cache = new CacheCategory(Person.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String accessToken,
			String contentType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (StringUtils.isEmpty(accessToken)) {
				String bearer = request.getHeader(HttpToken.X_AUTHORIZATION);
				if (StringUtils.isNotEmpty(bearer)) {
					accessToken = StringUtils.substringAfter(bearer, " ");
				}
			}
			if (StringUtils.isEmpty(accessToken)) {
				throw new ExceptionAccessTokenEmpty();
			}
			OauthCode oauthCode = emc.firstEqual(OauthCode.class, OauthCode.accessToken_FIELDNAME, accessToken);
			if (null == oauthCode) {
				throw new ExceptionOauthCodeNotExist(accessToken);
			}
			Oauth oauth = Config.token().findOauth(oauthCode.getClientId());
			if (null == oauth) {
				throw new ExceptionOauthNotExist(oauthCode.getClientId());
			}
			emc.beginTransaction(OauthCode.class);
			emc.remove(oauthCode, CheckRemoveType.all);
			emc.commit();
			Info info = this.info(business, oauthCode, oauth);
			Wo wo = new Wo();
			wo.setText(gson.toJson(info));
			wo.setContentType(contentType);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoText {

	}

	public static class Info extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = 6301619145098242551L;

	}

	private Source compliedScript(String clientId, String scope, String text) {

		CacheKey cacheKey = new CacheKey(this.getClass(), clientId, scope);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			return (Source) optional.get();
		} else {
			Source source = GraalvmScriptingFactory.functionalization(text);
			CacheManager.put(cache, cacheKey, source);
			return source;
		}
	}

	private Info info(Business business, OauthCode oauthCode, Oauth oauth) throws Exception {
		Info info = new Info();
		if (Config.token().isInitialManager(oauthCode.getPerson())) {
			InitialManager initialManager = Config.token().initialManagerInstance();
			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_PERSON, initialManager);
			for (String str : StringUtils.split(oauthCode.getScope(), ",")) {
				String property = StringEscapeUtils.unescapeJson(oauth.getMapping().get(str));
				Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.REGULAREXPRESSION_SCRIPT);
				Matcher matcher = pattern.matcher(property);
				String value = "";
				if (matcher.matches()) {
					Source source = this.compliedScript(oauthCode.getClientId(), str, matcher.group(1));
					Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
					if (opt.isPresent()) {
						value = opt.get();
					}
				} else {
					value = Objects.toString(PropertyUtils.getProperty(initialManager, property));
				}
				info.put(str, value);
			}
		} else {
			Person person = business.entityManagerContainer().find(oauthCode.getPerson(), Person.class);
			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_PERSON, person);
			for (String str : StringUtils.split(oauthCode.getScope(), ",")) {
				String property = oauth.getMapping().get(str);
				Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.REGULAREXPRESSION_SCRIPT);
				Matcher matcher = pattern.matcher(property);
				String value = "";
				if (matcher.matches()) {
					Source source = this.compliedScript(oauthCode.getClientId(), str,
							StringEscapeUtils.unescapeJson(matcher.group(1)));
					Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
					if (opt.isPresent()) {
						value = opt.get();
					}
				} else {
					value = Objects.toString(PropertyUtils.getProperty(person, property));
				}
				info.put(str, value);
			}
		}
		return info;
	}

}