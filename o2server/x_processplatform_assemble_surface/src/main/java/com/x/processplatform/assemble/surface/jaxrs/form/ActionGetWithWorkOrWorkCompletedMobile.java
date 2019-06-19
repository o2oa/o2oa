package com.x.processplatform.assemble.surface.jaxrs.form;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.jaxrs.form.ActionGetWithWorkOrWorkCompleted.WoWorkCompletedForm;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Form;

class ActionGetWithWorkOrWorkCompletedMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetWithWorkOrWorkCompletedMobile.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<JsonElement> result = new ActionResult<>();

			Business business = new Business(emc);

			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			JsonElement wo = null;

			Work work = emc.find(workOrWorkCompleted, Work.class);

			if (null != work) {
				wo = gson.toJsonTree(this.work(business, work));
			} else {
				wo = gson.toJsonTree(this.workCompleted(business, emc.flag(workOrWorkCompleted, WorkCompleted.class)));
			}
			result.setData(wo);
			return result;
		}
	}

	private WoWorkForm work(Business business, Work work) throws Exception {
		WoWorkForm wo = new WoWorkForm();
		String id = work.getForm();
		if (StringUtils.isEmpty(id)) {
			Activity activity = business.getActivity(work);
			id = PropertyTools.getOrElse(activity, Activity.form_FIELDNAME, String.class, "");
		}
		if (StringUtils.isNotEmpty(id)) {
			Form form = business.form().pick(id);
			wo = WoWorkForm.copier.copy(form);
			if (StringUtils.isNotEmpty(wo.getMobileData())) {
				wo.setData(wo.getMobileData());
			}
			/* 清空移动端表单,减少传输量 */
			wo.setMobileData("");
		}
		return wo;
	}

	private WoWorkCompletedForm workCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		WoWorkCompletedForm wo = new WoWorkCompletedForm();
		if (StringUtils.isNotEmpty(workCompleted.getFormMobileData())) {
			wo.setData(workCompleted.getFormMobileData());
		} else if (StringUtils.isNotEmpty(workCompleted.getFormData())) {
			wo.setData(workCompleted.getFormData());
		} else if (StringUtils.isNotEmpty(workCompleted.getForm())) {
			Form form = business.form().pick(workCompleted.getForm());
			if (null != form) {
				if (StringUtils.isNotEmpty(form.getMobileData())) {
					wo.setData(form.getMobileData());
				} else if (StringUtils.isNotEmpty(form.getData())) {
					wo.setData(workCompleted.getFormData());
				}
			}
		}
		return wo;
	}

	public static class WoWorkForm extends Form {

		private static final long serialVersionUID = 1303951663975390089L;

		static WrapCopier<Form, WoWorkForm> copier = WrapCopierFactory.wo(Form.class, WoWorkForm.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

	public static class WoWorkCompletedForm extends GsonPropertyObject {

		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

}