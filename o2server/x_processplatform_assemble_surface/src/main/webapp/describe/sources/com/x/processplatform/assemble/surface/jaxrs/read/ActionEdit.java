package com.x.processplatform.assemble.surface.jaxrs.read;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionEntityNotExist(id, Read.class);
			}
			if (business.read().allowProcessing(effectivePerson, read)) {
				throw new ExceptionAccessDenied(effectivePerson, read);
			}
			emc.beginTransaction(Read.class);
			read.setOpinion(wi.getOpinion());
			emc.check(read, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("意见")
		private String opinion;

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

	}

	public static class Wo extends WoId {
	}

}
