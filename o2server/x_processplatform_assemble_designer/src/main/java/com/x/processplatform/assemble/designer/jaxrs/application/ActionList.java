package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.application().listWithPerson(effectivePerson);
			/* 由于有多值字段所以需要全部取出 */
			List<Wo> wos = Wo.copier.copy(emc.list(Application.class, ids));
			wos = business.application().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = -7648824521711153693L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class,
				JpaObject.singularAttributeField(Application.class, true, true), JpaObject.FieldsInvisible);

	}

}