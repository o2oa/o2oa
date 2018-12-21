package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.element.Application;

 class ActionRemove extends BaseAction {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			SerialNumber o = emc.find(id, SerialNumber.class);
			if (null == o) {
				throw new ExceptionSerialNumberNotExist(id);
			}
			Application application = business.application().pick(o.getApplication());
			if (null == application) {
				throw new ExceptionApplicationNotExist(o.getApplication());
			}
			if (!business.application().allowControl(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(), application.getId());
			}
			emc.beginTransaction(SerialNumber.class);
			emc.remove(o, CheckRemoveType.all);
			emc.commit();
			WrapOutId wrap = new WrapOutId(id);
			result.setData(wrap);
		}
		return result;
	}
}
