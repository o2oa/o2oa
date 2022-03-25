package com.x.processplatform.service.processing.jaxrs.form;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.service.processing.Business;

class ActionSuitableActivity extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSuitableActivity.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String activityId) throws Exception {

		LOGGER.debug("execute:{}, activityId:{}.", effectivePerson::getDistinguishedName, () -> activityId);
		
		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Activity activity = business.element().getActivity(activityId);
			if (null == activity) {
				throw new ExceptionEntityNotExist(activityId);
			}
			Wo wo = new Wo();
			wo.setValue(business.element().lookupSuitableForm(activity.getProcess(), activity.getId()));
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = 6457473592503074552L;

	}

}
