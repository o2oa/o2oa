package com.x.processplatform.assemble.surface.jaxrs.sign;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DocSign;

class ActionGetByTask extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionGetByTask.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String taskId) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			DocSign docSign = emc.firstEqual(DocSign.class, DocSign.taskId_FIELDNAME, taskId);
			if (null == docSign) {
				throw new ExceptionEntityNotExist(taskId, DocSign.class);
			}
			if(!business.readableWithJob(effectivePerson, docSign.getJob())){
				throw new ExceptionAccessDenied(effectivePerson, taskId);
			}
			Wo wo = Wo.copier.copy(docSign);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends DocSign {

		static WrapCopier<DocSign, Wo> copier = WrapCopierFactory.wo(DocSign.class, Wo.class,
				null, JpaObject.FieldsInvisible);

	}

}
