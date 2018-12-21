package com.x.processplatform.assemble.surface.jaxrs.read;

import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;

class ActionProcessing extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionEntityNotExist(id,Read.class);
			}
			if (!business.read().allowProcessing(effectivePerson, read)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Read.class);
			/* 如果有新的流程意见那么覆盖原有流程意见 */
			if (StringUtils.isNotEmpty(wi.getOpinion())) {
				read.setOpinion(wi.getOpinion());
			}
			emc.commit();
			/* processing read */
			Wo wo = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					"read/" + URLEncoder.encode(read.getId(), "UTF-8") + "/processing", null).getData(Wo.class);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

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
