package com.x.processplatform.assemble.surface.jaxrs.snap;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Snap;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Snap snap = emc.find(id, Snap.class);
			if (null == snap) {
				throw new ExceptionEntityNotExist(id, Snap.class);
			}
			if (!allow(effectivePerson, business, snap)) {
				throw new ExceptionAccessDenied(effectivePerson, snap);
			}
			job = snap.getJob();
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = ThisApplication
				.context().applications().deleteQuery(effectivePerson.getDebugger(),
						x_processplatform_service_processing.class, Applications.joinQueryUri("snap", id), job)
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	private boolean allow(EffectivePerson effectivePerson, Business business, Snap snap) throws Exception {
		return (business.ifPersonCanManageApplicationOrProcess(effectivePerson, snap.getApplication(),
				snap.getProcess()) || effectivePerson.isNotPerson(snap.getPerson()));
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}

}
