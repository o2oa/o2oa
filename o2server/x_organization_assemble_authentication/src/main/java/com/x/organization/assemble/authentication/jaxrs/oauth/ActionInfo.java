package com.x.organization.assemble.authentication.jaxrs.oauth;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.InitialManager;
import com.x.base.core.project.config.Token.Oauth;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.Scripting;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.OauthCode;
import com.x.organization.core.entity.Person;

class ActionInfo extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionInfo.class);

	public static final Pattern SCRIPT_PATTERN = Pattern.compile("^\\((.+?)\\)$");

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String access_token) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (StringUtils.isEmpty(access_token)) {
				throw new ExceptionAccessTokenEmpty();
			}
			OauthCode oauthCode = emc.find(access_token, OauthCode.class);
			if (null == oauthCode) {
				throw new ExceptionOauthCodeNotExist(access_token);
			}
			Oauth oauth = Config.token().findOauth(oauthCode.getClientId());
			if (null == oauth) {
				throw new ExceptionOauthNotExist(oauthCode.getClientId());
			}
			WoInfo woInfo = this.info(business, oauthCode, oauth);
			Wo wo = new Wo();
			wo.setText(gson.toJson(woInfo));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoText {

	}

	public static class WoInfo extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = 6301619145098242551L;

	}

	private WoInfo info(Business business, OauthCode oauthCode, Oauth oauth) throws Exception {
		WoInfo woInfo = new WoInfo();
		if (Config.token().isInitialManager(oauthCode.getPerson())) {
			InitialManager initialManager = Config.token().initialManagerInstance();
			ScriptingEngine engine = Scripting.getEngine();
			engine.binding("person", initialManager);
			for (String str : StringUtils.split(oauthCode.getScope(), ",")) {
				String property = oauth.getMapping().get(str);
				String value = "";
				if (SCRIPT_PATTERN.matcher(property).find()) {
					value = Objects.toString(engine.eval(property));
				} else {
					value = Objects.toString(PropertyUtils.getProperty(initialManager, property));
				}
				// value = new String(value.getBytes(), "GB2312");
				woInfo.put(str, value);
			}
		} else {
			Person person = business.entityManagerContainer().find(oauthCode.getPerson(), Person.class);
			ScriptingEngine engine = Scripting.getEngine();
			engine.binding("person", person);
			for (String str : StringUtils.split(oauthCode.getScope(), ",")) {
				String property = oauth.getMapping().get(str);
				String value = "";
				if (SCRIPT_PATTERN.matcher(property).find()) {
					value = Objects.toString(engine.eval(property));
				} else {
					value = Objects.toString(PropertyUtils.getProperty(person, property));
				}
				// value = new String(value.getBytes(), "GB2312");
				woInfo.put(str, value);
			}
		}
		return woInfo;
	}

}