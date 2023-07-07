package com.x.processplatform.assemble.surface.jaxrs.work;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;

class ActionProjection extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionProjection.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Work work = null;
		Control control = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null != work) {
				control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
			}
		}
		if ((null != control) && BooleanUtils.isTrue(control.getAllowSave())) {
			wo = ThisApplication.context().applications()
					.getQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", work.getId(), "projection"), work.getJob())
					.getData(Wo.class);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 8530828613517157899L;

	}

}