package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Person;

class ActionOauthBind extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOauthBind.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String name, String code, String redirectUri) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isAnonymous()) {
				throw new ExceptionPersonNotLogin();
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(false);
			OauthClient oauthClient = this.getOauthClient(name);
			if (null == oauthClient) {
				throw new ExceptionOauthNotExist(name);
			}
			if ((!oauthClient.getBindingEnable()) || StringUtils.isEmpty(oauthClient.getBindingField())) {
				throw new ExceptionOauthBindDisable();
			}
			logger.debug("oauthClient:{}", oauthClient);
			String tokenBody = "";
			if (StringUtils.equalsIgnoreCase("post", oauthClient.getTokenMethod())) {
				tokenBody = this.oauthClientTokenPost(oauthClient, redirectUri, code);
			} else {
				tokenBody = this.oauthClientTokenGet(oauthClient, redirectUri, code);
			}
			logger.debug("tokenBody:{}", tokenBody);
			if (StringUtils.isEmpty(tokenBody)) {
				throw new ExceptionOauthEmptyToken();
			}
			String accessToken = "";
			String refreshToken = "";
			if (StringUtils.equalsIgnoreCase(oauthClient.getTokenType(), "json")) {
				WiToken wiToken = gson.fromJson(tokenBody, WiToken.class);
				accessToken = wiToken.getAccess_token();
				refreshToken = wiToken.getRefresh_token();
			} else {
				accessToken = StringUtils.substringAfter(tokenBody, "access_token=");
				if (StringUtils.contains(accessToken, "&")) {
					accessToken = StringUtils.substringBefore(accessToken, "&");
				}
				refreshToken = StringUtils.substringAfter(tokenBody, "refresh_token=");
				if (StringUtils.contains(refreshToken, "&")) {
					accessToken = StringUtils.substringBefore(refreshToken, "&");
				}
			}
			logger.debug("accessToken:{}", accessToken);
			if (StringUtils.isEmpty(accessToken)) {
				throw new ExceptionOauthEmptyAccessToken();
			}
			String infoBody = "";
			if (StringUtils.equalsIgnoreCase("post", oauthClient.getInfoMethod())) {
				infoBody = this.oauthClientInfoPost(oauthClient, redirectUri, accessToken, refreshToken);
			} else {
				infoBody = this.oauthClientInfoGet(oauthClient, redirectUri, accessToken, refreshToken);
			}
			logger.debug("infoBody:{}", infoBody);
			if (StringUtils.isEmpty(infoBody)) {
				throw new ExceptionOauthEmptyInfo();
			}
			String credential = "";
			if (StringUtils.equalsIgnoreCase(oauthClient.getInfoType(), "json")) {
				JsonElement jsonElement = gson.fromJson(infoBody, JsonElement.class);
				credential = jsonElement.getAsJsonObject().get(oauthClient.getInfoCredentialField()).getAsString();
			} else if (StringUtils.equalsIgnoreCase(oauthClient.getInfoType(), "form")) {
				credential = StringUtils.substringAfter(infoBody, oauthClient.getInfoCredentialField() + "=");
				if (StringUtils.contains(credential, "&")) {
					credential = StringUtils.substringBefore(credential, "&");
				}
			} else {
				logger.debug("info script:{}.", oauthClient.getInfoScriptText());
				ScriptEngineManager factory = new ScriptEngineManager();
				ScriptEngine engine = factory.getEngineByName("nashorn");
				engine.put("text", infoBody);
				credential = engine.eval(oauthClient.getInfoScriptText()).toString();
			}
			logger.debug("credential:{}", credential);
			if (!Config.token().isInitialManager(credential)) {
				Person o = emc.flag(effectivePerson.getDistinguishedName(), Person.class);
				if (null != o) {
					emc.beginTransaction(Person.class);
					PropertyUtils.setProperty(o, oauthClient.getBindingField(), credential);
					wo.setValue(true);
					emc.commit();
				}
			}
			result.setData(wo);
			return result;
		}

	}

	public static class Wo extends WrapBoolean {

	}

	public static class WiToken {
		private String access_token;

		private String refresh_token;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getRefresh_token() {
			return refresh_token;
		}

		public void setRefresh_token(String refresh_token) {
			this.refresh_token = refresh_token;
		}
	}

}