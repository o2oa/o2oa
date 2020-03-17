package com.x.processplatform.assemble.surface.jaxrs.documentversion;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Work;

class ActionCreate extends StandardJaxrsAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement) throws Exception {
		Work work = null;
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			if (!business.readableWithJob(effectivePerson, work.getJob())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
		}
		wi.setPerson(effectivePerson.getDistinguishedName());
		Wo wo = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("documentversion", "work", work.getId()), wi, work.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	public static class Wi extends DocumentVersion {

		private static final long serialVersionUID = 6403329784150966767L;
		static WrapCopier<Wi, DocumentVersion> copier = WrapCopierFactory.wi(Wi.class, DocumentVersion.class,
				ListTools.toList(DocumentVersion.data_FIELDNAME, DocumentVersion.category_FIELDNAME), null);
	}

	public static class Wo extends WoId {

	}

}