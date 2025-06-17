package com.x.ai.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.core.entity.AiModel;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCreateModel extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(StringUtils.isEmpty(wi.getType())){
				throw new ExceptionFieldEmpty(AiModel.type_FIELDNAME);
			}
			if(StringUtils.isEmpty(wi.getModel())){
				throw new ExceptionFieldEmpty(AiModel.model_FIELDNAME);
			}
			if(StringUtils.isEmpty(wi.getCompletionUrl())){
				throw new ExceptionFieldEmpty(AiModel.completionUrl_FIELDNAME);
			}
			List<AiModel> list = emc.listEqual(AiModel.class, AiModel.asDefault_FIELDNAME, true);
			if(list.isEmpty()){
				wi.setAsDefault(true);
			}
			emc.beginTransaction(AiModel.class);
			AiModel aiModel = Wi.copier.copy(wi);
			emc.persist(aiModel, CheckPersistType.all);
			if(BooleanUtils.isTrue(aiModel.getAsDefault()) && !list.isEmpty()){
				list.forEach(o -> o.setAsDefault(false));
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(aiModel.getId());
			result.setData(wo);

			this.saveToO2Ai(aiModel);
			return result;
		}
	}

	private void saveToO2Ai(AiModel aiModel) throws Exception{
		AiConfig aiConfig = Business.getConfig();
		if(StringUtils.isNotBlank(aiConfig.getO2AiToken()) && StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())) {
			String url =
					aiConfig.getO2AiBaseUrl() + "/ai-gateway-endpoint/create";
			List<NameValuePair> heads = List.of(
					new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
			ConnectionAction.post(url, heads, gson.toJson(aiModel));
		}
	}

	public static class Wi extends AiModel {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, AiModel> copier = WrapCopierFactory.wi(Wi.class, AiModel.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}
