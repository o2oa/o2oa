package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.component.core.entity.Component;

class ActionGet extends ActionBase {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Component component = emc.flag(flag, Component.class);
			if (null == component) {
				throw new ExceptionEntityNotExist(flag, Component.class);
			}
			Wo wo = Wo.copier.copy(component);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Component {

		private static final long serialVersionUID = 8867806242224800105L;
		static WrapCopier<Component, Wo> copier = WrapCopierFactory.wo(Component.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
