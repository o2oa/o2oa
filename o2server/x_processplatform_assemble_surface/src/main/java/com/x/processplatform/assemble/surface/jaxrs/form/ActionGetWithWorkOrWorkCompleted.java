package com.x.processplatform.assemble.surface.jaxrs.form;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Form;

class ActionGetWithWorkOrWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetWithWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			
			ActionResult<Wo> result = new ActionResult<>();

			Business business = new Business(emc);

			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			Wo wo = null;

			Work work = emc.find(workOrWorkCompleted, Work.class);

			if (null != work) {
				wo = this.work(business, work);
			} else {
				wo = this.workCompleted(business, emc.flag(workOrWorkCompleted, WorkCompleted.class));
			}
			result.setData(wo);
			return result;
		}
	}

	private Wo work(Business business, Work work) throws Exception {
		Wo wo = new Wo();
		String id = work.getForm();
		if (StringUtils.isEmpty(id)) {
			Activity activity = business.getActivity(work);
			id = PropertyTools.getOrElse(activity, Activity.form_FIELDNAME, String.class, "");
		}
		if (StringUtils.isEmpty(id)) {
			Form form = business.form().pick(id);
			if (null != form) {
				wo.setData(form.getDataOrMobileData());
			}
		}
		return wo;
	}

	private Wo workCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		Wo wo = new Wo();
		if (StringUtils.isNotEmpty(workCompleted.getFormData())) {
			wo.setData(workCompleted.getFormData());
		} else {
			wo.setData(workCompleted.getFormMobileData());
		}
		return wo;
	}

	public static class Wo extends GsonPropertyObject {

		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

}