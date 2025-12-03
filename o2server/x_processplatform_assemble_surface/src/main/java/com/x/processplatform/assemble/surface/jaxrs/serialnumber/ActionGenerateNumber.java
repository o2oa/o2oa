package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import java.util.HashMap;

class ActionGenerateNumber extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGenerateNumber.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String processId, String name)
			throws Exception {
		LOGGER.info("execute generate number:{}, processId:{}, name:{}.", effectivePerson::getDistinguishedName, () -> processId, () -> name);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = business.process().pick(processId);
			if (null == process) {
				throw new ExceptionEntityNotExist(processId);
			}
			Application application = business.application().pick(process.getApplication());
			if(application == null){
				throw new ExceptionEntityNotExist(process.getApplication());
			}
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")
					&& !(effectivePerson.isPerson(application.getControllerList()))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		Wo wo = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "process", processId, "name", name, "serial"), new HashMap<>(), processId)
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapInteger {

		private static final long serialVersionUID = 8667007945527601792L;

	}

}
