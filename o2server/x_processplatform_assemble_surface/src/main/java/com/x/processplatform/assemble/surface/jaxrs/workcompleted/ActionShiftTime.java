package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionShiftTimeWi;
import com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionShiftTimeWo;

class ActionShiftTime extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionShiftTime.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}", effectivePerson::getDistinguishedName, () -> jsonElement);

		Wi wi = this.init(effectivePerson, jsonElement);

		Wo wo = this.shiftTime(wi);

		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;

	}

	private Wi init(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.flag(wi.getId(), WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(wi.getId(), WorkCompleted.class);
			}
			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowManage().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			return wi;
		}
	}

	private Wo shiftTime(Wi wi) throws Exception {
		return ThisApplication.context().applications().postQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("workcompleted", "shift", "time"), wi).getData(Wo.class);
	}

	public static class Wi extends ActionShiftTimeWi {

		private static final long serialVersionUID = 1966814422721596072L;

	}

	public static class Wo extends ActionShiftTimeWo {

		private static final long serialVersionUID = -6048816634681644627L;

	}

}