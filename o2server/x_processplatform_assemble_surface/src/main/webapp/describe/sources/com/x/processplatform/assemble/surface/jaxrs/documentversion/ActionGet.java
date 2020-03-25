package com.x.processplatform.assemble.surface.jaxrs.documentversion;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.DocumentVersion;

class ActionGet extends StandardJaxrsAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			DocumentVersion documentVersion = emc.find(id, DocumentVersion.class);
			if (null == documentVersion) {
				throw new ExceptionEntityNotExist(id, DocumentVersion.class);
			}
			if (!business.readableWithJob(effectivePerson, documentVersion.getJob())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(documentVersion);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends DocumentVersion {

		private static final long serialVersionUID = -2308839871835887434L;

		static WrapCopier<DocumentVersion, Wo> copier = WrapCopierFactory.wo(DocumentVersion.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}