package com.x.processplatform.assemble.surface.jaxrs.snap;

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
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Snap;

class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Snap snap = emc.find(id, Snap.class);
			if (null == snap) {
				throw new ExceptionEntityNotExist(id, Snap.class);
			}
			if (!allow(effectivePerson, business, snap)) {
				throw new ExceptionAccessDenied(effectivePerson, snap);
			}
			result.setData(Wo.copier.copy(snap));
		}
		return result;
	}

	private boolean allow(EffectivePerson effectivePerson, Business business, Snap snap) throws Exception {
		return (business.ifPersonCanManageApplicationOrProcess(effectivePerson, snap.getApplication(), snap.getProcess())
				|| effectivePerson.isNotPerson(snap.getPerson()));
	}

	public static class Wo extends Snap {

		private static final long serialVersionUID = -2577413577740827608L;

		static WrapCopier<Snap, Wo> copier = WrapCopierFactory.wo(Snap.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
