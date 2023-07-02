package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionOauthGetWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionOauthGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOauthGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String name) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		OauthClient oauthClient = null;
		if (ListTools.isNotEmpty(Config.token().getOauthClients())) {
			for (OauthClient o : Config.token().getOauthClients()) {
				if (BooleanUtils.isTrue(o.getEnable()) && StringUtils.equals(o.getName(), name)) {
					oauthClient = o;
				}
			}
		}
		if (null == oauthClient) {
			throw new ExceptionOauthNotExist(name);
		}
		Wo wo = new Wo();
		wo.setName(oauthClient.getName());
		wo.setRedirectUri(oauthClient.getAuthAddress());
		wo.setAuthAddress(oauthClient.getAuthAddress());
		wo.setAuthMethod(oauthClient.getAuthMethod());
		wo.setIcon(oauthClient.getIcon());
		Map<String, Object> param = oauthCreateParam(oauthClient, "", "");
		String authParameter = fillParameter(oauthClient.getAuthParameter(), param);
		LOGGER.debug("auth parameter:{}.", authParameter);
		wo.setAuthParameter(authParameter);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionOauthGet$Wo")
	public static class Wo extends ActionOauthGetWo {

		private static final long serialVersionUID = 4572263092554577141L;

	}

}