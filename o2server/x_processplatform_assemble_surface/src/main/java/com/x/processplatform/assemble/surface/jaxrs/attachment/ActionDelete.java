package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import org.apache.commons.lang3.BooleanUtils;

class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			Long taskCount = business.task().countWithPersonWithJob(effectivePerson.getDistinguishedName(), attachment.getJob());
			if (taskCount<0 && !business.canManageApplicationOrProcess(effectivePerson, attachment.getApplication(),
					attachment.getProcess())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		Wo wo = ThisApplication.context().applications()
				.deleteQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("attachment", attachment.getId()))
				.getData(Wo.class);
		wo.setId(attachment.getId());
		logger.info("{}操作删除附件：{}{}",effectivePerson.getDistinguishedName(),id,attachment.getName());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

	public static class WoControl extends WorkControl {
	}

}
