package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;

class ActionFlag extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionFlag.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ApplicationNotExistedException(applicationFlag);
			}
			QueryStat o = business.queryStat().pick(flag, application);
			if (null == o) {
				throw new QueryStatNotExistedException(flag, applicationFlag);
			}
			if (!business.queryStat().allowRead(effectivePerson, o, application)) {
				throw new QueryStatAccessDeniedException(effectivePerson.getDistinguishedName(), flag, applicationFlag);
			}
			Wo wo = Wo.copier.copy(o);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends QueryStat {

		private static final long serialVersionUID = 2886873983211744188L;

		static WrapCopier<QueryStat, Wo> copier = WrapCopierFactory.wo(QueryStat.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}