package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Draft;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Draft draft = emc.find(id, Draft.class);
			if (null == draft) {
				throw new ExceptionEntityNotExist(id, Draft.class);
			}
			if ((!effectivePerson.isPerson(draft.getPerson())) && (!business
					.ifPersonCanManageApplicationOrProcess(effectivePerson, draft.getApplication(), draft.getProcess()))) {
				throw new ExceptionAccessDenied(effectivePerson, draft);
			}
			emc.beginTransaction(Draft.class);
			emc.remove(draft, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(draft.getId());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionDelete$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 1488438332387072858L;

	}

}
