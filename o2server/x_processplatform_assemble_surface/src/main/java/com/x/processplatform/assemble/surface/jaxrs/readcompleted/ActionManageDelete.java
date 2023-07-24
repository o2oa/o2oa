package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		ReadCompleted readCompleted = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			readCompleted = emc.find(id, ReadCompleted.class);
			if (null == readCompleted) {
				throw new ExceptionEntityNotExist(id, ReadCompleted.class);
			}
			Process process = business.process().pick(readCompleted.getProcess());
			Application application = business.application().pick(readCompleted.getApplication());
			// 需要对这个应用的管理权限
			if (BooleanUtils
					.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("readcompleted", readCompleted.getId()), readCompleted.getJob());
		Wo wo = new Wo();
		wo.setId(readCompleted.getId());
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.ActionManageDelete$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -7432653967330826139L;

	}

}