package com.x.query.assemble.designer.jaxrs.query;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;

class ActionIcon extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionIcon.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		logger.debug(effectivePerson, "flag:{}.", flag);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Query query = emc.flag(flag, Query.class );
			if (null == query) {
				throw new ExceptionQueryNotExist(flag);
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName(),
						query.getId());
			}
			Wo wo = Wo.copier.copy(query);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Query {

		private static final long serialVersionUID = 2886873983211744188L;

		static WrapCopier<Query, Wo> copier = WrapCopierFactory.wo(Query.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
