package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;

class ActionGet extends ActionBase {
	ActionResult<WrapOutQueryStat> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutQueryStat> result = new ActionResult<>();
			Business business = new Business(emc);
			QueryStat queryStat = emc.find(id, QueryStat.class, ExceptionWhen.not_found);
			Application application = emc.find(queryStat.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			WrapOutQueryStat wrap = outCopier.copy(queryStat);
			result.setData(wrap);
			return result;
		}
	}
}
