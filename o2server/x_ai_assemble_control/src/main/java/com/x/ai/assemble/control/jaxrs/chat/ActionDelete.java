package com.x.ai.assemble.control.jaxrs.chat;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ActionDelete extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String clueId)
			throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		AiConfig aiConfig = Business.getConfig();
		String url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-clue/delete";
		List<NameValuePair> heads = List.of(new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
		Map<String, Object> map = new HashMap<>();
		map.put("idList", List.of(clueId));
		ConnectionAction.post(url, heads, map);
		Wo wo = new Wo();
        wo.setValue(true);
		result.setData(wo);
		return result;
	}


	public static class Wo extends WrapBoolean {

	}

}
