package com.x.cms.assemble.control.jaxrs.data;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;

class ActionUpdateWithDocument extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/** 防止提交空数据清空data */
			if (jsonElement.isJsonNull()) {
				throw new ExceptionNullData();
			}
			if (jsonElement.isJsonArray()) {
				throw new ExceptionArrayData();
			}
			if (jsonElement.isJsonPrimitive()) {
				throw new ExceptionPrimitiveData();
			}
			if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().entrySet().isEmpty()) {
				throw new ExceptionEmptyData();
			}
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Document document = emc.find(id, Document.class);
			if (null == document) {
				throw new ExceptionDocumentNotExists(id);
			}
			JsonElement source = getData(business, id);
			JsonElement merge = XGsonBuilder.merge(jsonElement, source);
			/** 先更新title和serial,再更新DataItem,因为旧的DataItem中也有title和serial数据. */
			this.updateTitleSerialObjectSecurityClearance(business, document, merge);
			this.updateData(business, document, merge);
			/** 在方法内进行了commit不需要再次进行commit */
			// emc.commit();
			Wo wo = new Wo();
			wo.setId(document.getId());
			result.setData(wo);

			CacheManager.notify( Document.class );

			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
