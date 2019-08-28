package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionOauthLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOauthLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String name, String code, String redirectUri) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Audit audit = logger.audit(effectivePerson);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			OauthClient oauthClient = this.getOauthClient(name);
			if (null == oauthClient) {
				throw new ExceptionOauthNotExist(name);
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
			logger.debug("accessToken:{}, refreshToken:{}.", accessToken, refreshToken);
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
			if (StringUtils.isEmpty(credential)) {
				throw new ExceptionOauthEmptyCredential();
			}
			Wo wo = new Wo();
			if (Config.token().isInitialManager(credential)) {
				wo = this.manager(request, response, business, Wo.class);
			} else {
				/* 普通用户登录,也有可能拥有管理员角色 */
				String personId = business.person().getWithCredential(credential);
				if (StringUtils.isEmpty(personId)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				Person o = emc.find(personId, Person.class);
				wo = this.user(request, response, business, o, Wo.class);
				audit.log(o.getDistinguishedName());
			}
			result.setData(wo);
			return result;
		}

	}

	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -1473824515272368422L;

		private String url;
		private String method;
		private String parameter;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getParameter() {
			return parameter;
		}

		public void setParameter(String parameter) {
			this.parameter = parameter;
		}
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