package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Read read;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);
			read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionEntityNotExist(id, Read.class);
			}
			Process process = business.process().pick(read.getProcess());
			Application application = business.application().pick(read.getApplication());
			// 需要对这个应用的管理权限
			if (!business.canManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("read", read.getId()), read.getJob());
		wo.setId(read.getId());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {
	}

}