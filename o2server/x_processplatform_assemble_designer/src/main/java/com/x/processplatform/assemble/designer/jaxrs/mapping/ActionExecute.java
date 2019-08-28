package com.x.processplatform.assemble.designer.jaxrs.mapping;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Mapping;

class ActionExecute extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Mapping mapping = emc.flag(flag, Mapping.class);
			if (null == mapping) {
				throw new ExceptionEntityNotExist(flag, Mapping.class);
			}
			if (BooleanUtils.isNotTrue(mapping.getEnable())) {
				throw new ExceptionDisable(mapping.getName());
			}
			Application application = emc.flag(mapping.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(mapping.getApplication(), Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			if (!ThisApplication.mappingExecuteQueue.contains(mapping.getId())) {
				ThisApplication.mappingExecuteQueue.send(mapping.getId());
			} else {
				throw new ExceptionAlreadyAddQueue();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}
	

	public static class Wo extends WrapBoolean {

	}

}