package com.x.bbs.assemble.control.jaxrs.shutup;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSShutup;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if(!business.isManager(effectivePerson)){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			BBSShutup shutup = emc.find(id, BBSShutup.class);
			if (null == shutup) {
				throw new ExceptionEntityNotExist(id);
			}
			emc.beginTransaction(BBSShutup.class);
			emc.remove(shutup, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
