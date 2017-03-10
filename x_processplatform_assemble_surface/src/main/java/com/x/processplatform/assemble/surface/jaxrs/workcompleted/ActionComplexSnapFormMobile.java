package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutMap;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutForm;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionComplexSnapFormMobile extends ActionBase {

	ActionResult<WrapOutMap> execute(String id, EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new WorkCompletedNotExistedException(id);
			}
			WrapOutMap wrap = this.complexWithoutForm(business, effectivePerson, workCompleted);
			Control control = (Control) wrap.get("control");
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new WorkCompletedAccessDeniedException(effectivePerson.getName(), id);
			}
			wrap.put("form", this.getWrapOutForm(business, workCompleted));
			result.setData(wrap);
			return result;
		}
	}

	private WrapOutForm getWrapOutForm(Business business, WorkCompleted workCompleted) throws Exception {
		WrapOutForm wrap = new WrapOutForm();
		wrap.setData(workCompleted.getFormMobileData());
		return wrap;
	}

}
