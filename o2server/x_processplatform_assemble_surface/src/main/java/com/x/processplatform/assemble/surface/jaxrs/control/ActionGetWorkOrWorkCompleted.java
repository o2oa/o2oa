package com.x.processplatform.assemble.surface.jaxrs.control;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionGetWorkOrWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {

		LOGGER.debug("execute:{}, workOrWorkCompleted:{}.", effectivePerson::getDistinguishedName,
				() -> workOrWorkCompleted);

		ActionResult<Wo> result = new ActionResult<>();
		Control ctrl = new Control();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(workOrWorkCompleted, Work.class);
			if (null != work) {
				ctrl = new WorkControlBuilder(effectivePerson, business, work).enableAll().build();
			} else {
				WorkCompleted workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
				if (null != workCompleted) {
					ctrl = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted).enableAll()
							.build();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		if (BooleanUtils.isFalse(ctrl.getAllowVisit())) {
			throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
		}
		result.setData(Wo.copier.copy(ctrl));
		return result;
	}

	public static class Wo extends Control {

		private static final long serialVersionUID = 6785048441528902022L;

		static WrapCopier<Control, Wo> copier = WrapCopierFactory.wo(Control.class, Wo.class, null, null);

	}

}