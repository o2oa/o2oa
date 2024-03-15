package com.x.processplatform.assemble.surface.jaxrs.snap;

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
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionTypeSnapWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTypeSnapWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workCompletedId) throws Exception {

		LOGGER.debug("execute:{}, workCompletedId:{}.", effectivePerson::getDistinguishedName, () -> workCompletedId);

		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(workCompletedId, WorkCompleted.class);
			}
			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowManage().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage())) {
				throw new ExceptionAccessDenied(effectivePerson, workCompleted);
			}
			job = workCompleted.getJob();
		}

		Wo wo = ThisApplication.context().applications()
				.getQuery(
						effectivePerson.getDebugger(), x_processplatform_service_processing.class, Applications
								.joinQueryUri("snap", "workcompleted", workCompletedId, "type", "snapworkcompleted"),
						job)
				.getData(Wo.class);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}

}
