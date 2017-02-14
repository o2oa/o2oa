package com.x.processplatform.assemble.designer.jaxrs.queryview;

import java.util.Date;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInQueryView;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;

class ActionCreate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, WrapInQueryView wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = emc.find(wrapIn.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.beginTransaction(QueryView.class);
			QueryView queryView = new QueryView();
			createCopier.copy(wrapIn, queryView);
			queryView.setApplication(application.getId());
			queryView.setCreatorPerson(effectivePerson.getName());
			queryView.setLastUpdatePerson(effectivePerson.getName());
			queryView.setLastUpdateTime(new Date());
			this.transQuery(queryView);
			emc.persist(queryView, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(QueryView.class);
			WrapOutId wrap = new WrapOutId(queryView.getId());
			result.setData(wrap);
			return result;
		}
	}
}
