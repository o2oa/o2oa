package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutSerialNumber;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.element.Application;

 class ActionGet extends ActionBase {
	ActionResult<WrapOutSerialNumber> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutSerialNumber> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			SerialNumber o = emc.find(id, SerialNumber.class);
			if (null == o) {
				throw new SerialNumberNotExistedException(id);
			}
			Application application = business.application().pick(o.getApplication());
			if (null == application) {
				throw new ApplicationNotExistedException(o.getApplication());
			}
			if (!business.application().allowControl(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getId());
			}
			WrapOutSerialNumber wrap = outCopier.copy(o);
			this.fillProcessName(business, wrap);
			result.setData(wrap);
		}
		return result;
	}
}
