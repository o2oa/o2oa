package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.accredit.Empower;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Empower empower = emc.find(id, Empower.class);
			if (null == empower) {
				throw new ExceptionEntityNotExist(id, Empower.class);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(empower.getFromPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, empower);
			}
			Wo wo = Wo.copier.copy(empower);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.empower.ActionGet$Wo")
	public static class Wo extends Empower {

		private static final long serialVersionUID = -7495725325510376323L;

		static WrapCopier<Empower, Wo> copier = WrapCopierFactory.wo(Empower.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
