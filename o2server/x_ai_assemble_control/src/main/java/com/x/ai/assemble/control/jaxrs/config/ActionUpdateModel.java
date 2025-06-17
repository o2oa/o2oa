package com.x.ai.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.core.entity.AiModel;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;

class ActionUpdateModel extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

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
			AiModel aiModel = emc.find(id, AiModel.class);
			if(aiModel == null){
				throw new ExceptionEntityNotExist(id, AiModel.class);
			}
			String name = aiModel.getName();
			if(BooleanUtils.isTrue(aiModel.getAsDefault()) && BooleanUtils.isNotTrue(wi.getAsDefault())){
				throw new ExceptionCustom("请设置一个启用的模型.");
			}
			List<AiModel> list = new ArrayList<>(emc.listAll(AiModel.class));
			list.remove(aiModel);

			emc.beginTransaction(AiModel.class);
			Wi.copier.copy(wi, aiModel);
			if(BooleanUtils.isTrue(aiModel.getAsDefault()) && !list.isEmpty()){
				list.forEach(o -> o.setAsDefault(false));
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(aiModel.getId());
			result.setData(wo);

			this.saveToO2Ai(name, aiModel);
			return result;
		}
	}

	private void saveToO2Ai(String name, AiModel aiModel) throws Exception{
		AiConfig aiConfig = Business.getConfig();
		if(StringUtils.isNotBlank(aiConfig.getO2AiToken()) && StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())) {
			List<NameValuePair> heads = List.of(
					new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
			String url =
					aiConfig.getO2AiBaseUrl() + "/ai-gateway-endpoint/get/"+ UrlEncoded.encodeString(name);
			ActionResponse resp = ConnectionAction.get(url, heads);
			if(Type.success.equals(resp.getType())){
				Wi wi = resp.getData(Wi.class);
				aiModel.copyTo(wi, ListTools.toList(JpaObject.FieldsUnmodify));
				url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-endpoint/update";
				resp = ConnectionAction.post(url, heads, gson.toJson(wi));
				if(!Type.success.equals(resp.getType())){
					throw new ExceptionCustom(resp.getMessage());
				}
			}else{
				url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-endpoint/create";
				resp = ConnectionAction.post(url, heads, gson.toJson(aiModel));
				if(!Type.success.equals(resp.getType())){
					throw new ExceptionCustom(resp.getMessage());
				}
			}
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
