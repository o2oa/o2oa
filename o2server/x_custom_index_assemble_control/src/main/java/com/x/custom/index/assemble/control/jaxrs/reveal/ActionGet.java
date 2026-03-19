package com.x.custom.index.assemble.control.jaxrs.reveal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.PersonDetail;
import com.x.base.core.project.tools.ListTools;
import com.x.custom.index.assemble.control.Business;
import com.x.custom.index.core.entity.Reveal;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Reveal reveal = emc.flag(flag, Reveal.class);
			PersonDetail personDetail = business.organization().person().detail(effectivePerson.getDistinguishedName(),
					true, true, true, true, true, false);
			if (null == reveal) {
				throw new ExceptionEntityExist(flag, Reveal.class);
			}
			if ((!effectivePerson.isManager())
					&& (!personDetail.containsAny(reveal.getCreatorPerson(), reveal.getAvailablePersonList(),
							reveal.getAvailableUnitList(), reveal.getAvailableGroupList()))
					&& (!(ListTools.isEmpty(reveal.getAvailablePersonList())
							&& ListTools.isEmpty(reveal.getAvailableUnitList())
							&& ListTools.isEmpty(reveal.getAvailableGroupList())))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(reveal);
			result.setData(wo);
		}
		return result;
	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionGet$Wo")
	public static class Wo extends Reveal {

		private static final long serialVersionUID = -2210112450849696028L;

		static WrapCopier<Reveal, Wo> copier = WrapCopierFactory.wo(Reveal.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
