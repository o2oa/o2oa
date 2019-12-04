package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.core.entity.accredit.Empower;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Empower empower = emc.find(id, Empower.class);
			if (null == empower) {
				throw new ExceptionEntityNotExist(id, Empower.class);
			}
			Wo wo = Wo.copier.copy(empower);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Empower {

		private static final long serialVersionUID = -7495725325510376323L;

		public static WrapCopier<Empower, Wo> copier = WrapCopierFactory.wo(Empower.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
