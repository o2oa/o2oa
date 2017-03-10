package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;

class ActionFlag extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionFlag.class);

	ActionResult<WrapOutQueryStat> execute(EffectivePerson effectivePerson, String flag, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutQueryStat> result = new ActionResult<>();
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
				throw new QueryStatAccessDeniedException(effectivePerson.getName(), flag, applicationFlag);
			}
			WrapOutQueryStat wrap = outCopier.copy(o);
			result.setData(wrap);
			return result;
		}
	}

}