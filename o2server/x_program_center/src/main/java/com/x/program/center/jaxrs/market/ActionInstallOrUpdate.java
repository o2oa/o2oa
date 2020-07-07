package com.x.program.center.jaxrs.market;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.core.entity.Application;
import com.x.program.center.core.entity.Attachment;

import java.util.ArrayList;
import java.util.List;

class ActionInstallOrUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isAnonymous()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Application app = emc.find(id, Application.class);
			if (null == app) {
				throw new ExceptionEntityNotExist(id, Application.class);
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