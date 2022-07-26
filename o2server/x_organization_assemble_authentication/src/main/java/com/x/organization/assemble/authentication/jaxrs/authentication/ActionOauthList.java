package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.config.Qiyeweixin;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionOauthListWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionOauthList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOauthList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		if (ListTools.isNotEmpty(Config.token().getOauthClients())) {
			for (OauthClient o : Config.token().getOauthClients()) {
				if (BooleanUtils.isTrue(o.getEnable())) {
					Wo wo = new Wo();
					wo.setName(o.getName());
					wo.setIcon(o.getIcon());
					wo.setAuthAddress(o.getAuthAddress());
					wo.setAuthMethod(o.getAuthMethod());
					wo.setDisplayName(o.getDisplayName());
					wo.setBindingEnable(o.getBindingEnable());
					Map<String, Object> param = oauthCreateParam(o, "", "");
					String authParameter = this.fillParameter(o.getAuthParameter(), param);
					LOGGER.debug("auth parameter:{}.", authParameter);
					wo.setAuthParameter(authParameter);
					wos.add(wo);
				}
			}
		}
		// 企业微信扫码登录
		if (BooleanUtils.isTrue(Config.qiyeweixin().getScanLoginEnable())) {
			Wo wo = new Wo();
			wo.setName("企业微信");
			wo.setDisplayName("@O2企业微信");
			wo.setIcon(Qiyeweixin.qywxLogo);
			wos.add(wo);
		}
		// 钉钉扫码登录
		if (BooleanUtils.isTrue(Config.dingding().getScanLoginEnable())) {
			Wo wo = new Wo();
			wo.setName("钉钉");
			wo.setIcon(Dingding.dingdingLogo);
			wo.setDisplayName("@O2钉钉");
			wos.add(wo);
		}
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionOauthList$Wo")
	public static class Wo extends ActionOauthListWo {

		private static final long serialVersionUID = 1488365958451799857L;

	}

}