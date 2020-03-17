package com.x.component.assemble.control.jaxrs.component;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.component.assemble.control.Business;
import com.x.component.core.entity.Component;

class ActionCreate extends ActionBase {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.editable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Component component = Wi.copier.copy(wi);
			emc.beginTransaction(Component.class);
			emc.persist(component, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			ApplicationCache.notify(Component.class);
			return result;
		}
	}

	public static class Wi extends Component {

		private static final long serialVersionUID = 8867806242224800105L;
		static WrapCopier<Wi, Component> copier = WrapCopierFactory.wi(Wi.class, Component.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WrapBoolean {

	}
}
