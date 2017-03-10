package com.x.processplatform.assemble.designer.jaxrs.queryview;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInQueryView;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;

class ActionUpdate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInQueryView wrapIn = this.convertToWrapIn(jsonElement, WrapInQueryView.class);
			Business business = new Business(emc);
			QueryView queryView = emc.find(id, QueryView.class);
			if (null == queryView) {
				throw new QueryViewNotExistedException(id);
			}
			Application application = emc.find(queryView.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(queryView.getId());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			emc.beginTransaction(QueryView.class);
			updateCopier.copy(wrapIn, queryView);
			queryView.setLastUpdatePerson(effectivePerson.getName());
			queryView.setLastUpdateTime(new Date());
			transQuery(queryView);
			emc.check(queryView, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(QueryView.class);
			WrapOutId wrap = new WrapOutId(queryView.getId());
			result.setData(wrap);
			return result;
		}
	}
}
