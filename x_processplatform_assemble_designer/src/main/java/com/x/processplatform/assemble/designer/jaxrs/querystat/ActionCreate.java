package com.x.processplatform.assemble.designer.jaxrs.querystat;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInQueryStat;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryView;

class ActionCreate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInQueryStat wrapIn = this.convertToWrapIn(jsonElement, WrapInQueryStat.class);
			Business business = new Business(emc);
			Application application = emc.find(wrapIn.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.beginTransaction(QueryStat.class);
			QueryStat queryStat = new QueryStat();
			inCopier.copy(wrapIn, queryStat);
			QueryView queryView = emc.find(queryStat.getQueryView(), QueryView.class, ExceptionWhen.not_found);
			queryStat.setQueryView(queryView.getId());
			queryStat.setQueryViewName(queryView.getName());
			queryStat.setQueryViewAlias(queryView.getAlias());
			queryStat.setApplication(application.getId());
			queryStat.setCreatorPerson(effectivePerson.getName());
			queryStat.setLastUpdatePerson(effectivePerson.getName());
			queryStat.setLastUpdateTime(new Date());
			emc.persist(queryStat, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(QueryStat.class);
			WrapOutId wrap = new WrapOutId(queryStat.getId());
			result.setData(wrap);
			return result;
		}
	}
}
