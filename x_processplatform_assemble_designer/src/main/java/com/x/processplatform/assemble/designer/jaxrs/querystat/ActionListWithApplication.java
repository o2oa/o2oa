package com.x.processplatform.assemble.designer.jaxrs.querystat;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;

class ActionListWithApplication extends ActionBase {
	ActionResult<List<WrapOutQueryStat>> execute(EffectivePerson effectivePerson, String applicationId)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutQueryStat>> result = new ActionResult<>();
			List<WrapOutQueryStat> wraps = new ArrayList<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			List<String> ids = business.queryStat().listWithApplication(applicationId);
			List<QueryStat> os = emc.list(QueryStat.class, ids);
			wraps = outCopier.copy(os);
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}
