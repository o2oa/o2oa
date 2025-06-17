package com.x.ai.assemble.control.jaxrs.config;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.jaxrs.config.ActionUpdateModel.Wi;
import com.x.ai.core.entity.AiModel;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;

class ActionDeleteModel extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionDeleteModel.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			ActionResult<Wo> result = new ActionResult<>();
			AiModel aiModel = emc.find(id, AiModel.class);
			if(aiModel == null){
				throw new ExceptionEntityNotExist(id, AiModel.class);
			}
			if(BooleanUtils.isTrue(aiModel.getAsDefault())){
				throw new ExceptionCustom("不能删除启用的模型.");
			}
			emc.beginTransaction(AiModel.class);
			emc.remove(aiModel);
			emc.commit();
			this.deleteToO2Ai(aiModel.getName());
			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	private void deleteToO2Ai(String name) {
		try {
			AiConfig aiConfig = Business.getConfig();
			if(StringUtils.isNotBlank(aiConfig.getO2AiToken()) && StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())) {
				List<NameValuePair> heads = List.of(
						new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
				String url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-endpoint/get/"
								+ UrlEncoded.encodeString(name);
				ActionResponse resp = ConnectionAction.get(url, heads);
				if (Type.success.equals(resp.getType())) {
					Wi wi = resp.getData(Wi.class);
					url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-endpoint/delete";
					ConnectionAction.post(url, heads, gson.toJson(Map.of("idList",  List.of(wi.getId()))));
				}
			}
		} catch (Exception e) {
			logger.warn("deleteToO2Ai error:"+e.getMessage());
			logger.error(e);
		}
	}

	public static class Wo extends WoId {
	}

}
