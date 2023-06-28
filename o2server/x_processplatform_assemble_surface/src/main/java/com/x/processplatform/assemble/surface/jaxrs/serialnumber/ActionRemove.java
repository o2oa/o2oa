package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.element.Application;

class ActionRemove extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
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
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, null)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(SerialNumber.class);
			emc.remove(o, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -6587064524386680060L;

	}
}
