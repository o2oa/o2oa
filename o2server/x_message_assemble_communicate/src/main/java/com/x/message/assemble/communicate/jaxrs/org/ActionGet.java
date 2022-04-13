package com.x.message.assemble.communicate.jaxrs.org;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.Org;

class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Org org = emc.find(id, Org.class);
			if (null == org) {
				throw new ExceptionEntityNotExist(id, Org.class);
			}
			Wo wo = Wo.copier.copy(org);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Org {

		private static final long serialVersionUID = -1681442801874856071L;

		public static final WrapCopier<Org, Wo> copier = WrapCopierFactory.wo(Org.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
