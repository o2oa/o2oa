package com.x.ai.assemble.control.jaxrs.config;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionDeleteMcp extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionDeleteMcp.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		if (effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}
		AiConfig aiConfig = Business.getConfig();
		if (BooleanUtils.isNotTrue(aiConfig.getO2AiEnable())
				|| StringUtils.isBlank(aiConfig.getO2AiToken()) && StringUtils.isBlank(
				aiConfig.getO2AiBaseUrl())) {
			throw new ExceptionCustom("请启用o2 AI智能体，并设置相关参数.");
		}
		ActionResult<Wo> result = new ActionResult<>();
		List<NameValuePair> heads = List.of(
				new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
		String url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-mcp/delete";
		ConnectionAction.post(url, heads, gson.toJson(Map.of("idList",  List.of(id))));
		Wo wo = new Wo();
		wo.setId(id);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {
	}

}
