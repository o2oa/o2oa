package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.List;

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
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

/**
 * 在一个应用的管理状态下删除一个workCompleted, 权限:需要至少process的管理权限.
 */
class ActionManageDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageDelete.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		WorkCompleted workCompleted;
		ActionResult<List<Wo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			Process process = business.process().pick(workCompleted.getProcess());
			Application application = business.application().pick(workCompleted.getApplication());
			// 需要对这个应用的管理权限
			if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		List<Wo> wos = ThisApplication.context().applications()
				.deleteQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("job", workCompleted.getJob()), workCompleted.getJob())
				.getDataAsList(Wo.class);
		result.setData(wos);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 593280558916677567L;

	}
}
