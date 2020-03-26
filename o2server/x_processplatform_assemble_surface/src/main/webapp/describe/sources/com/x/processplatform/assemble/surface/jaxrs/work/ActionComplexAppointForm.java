package com.x.processplatform.assemble.surface.jaxrs.work;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.jaxrs.work.BaseAction.AbstractWo;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Form;

class ActionComplexAppointForm extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionComplexAppointForm.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String formFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			Wo wo = this.get(business, effectivePerson, work, Wo.class);
			WoControl control = wo.getControl();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId());
			}
			wo.setForm(this.referenceForm(business, work, formFlag));
			result.setData(wo);
			return result;
		}
	}

	private WoForm referenceForm(Business business, Work work, String formFlag) throws Exception {
		Form form = business.form().pick(formFlag);
		if (null == form) {
			logger.info("work title:{}, id:{}, can not find form:{}.", work.getTitle(), work.getId(), work.getForm());
			return null;
		}
		WoForm woForm = new WoForm();
		form.copyTo(woForm, "data", "mobileData");
		woForm.setData(form.getDataOrMobileData());
		return woForm;
	}

	public static class Wo extends AbstractWo {
	}
}