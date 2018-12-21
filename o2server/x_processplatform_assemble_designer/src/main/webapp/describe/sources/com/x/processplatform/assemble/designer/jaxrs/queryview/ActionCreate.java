package com.x.processplatform.assemble.designer.jaxrs.queryview;

import java.util.Arrays;
import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;

class ActionCreate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Application application = emc.find(wi.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(wi.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			emc.beginTransaction(QueryView.class);
			QueryView queryView = new QueryView();
			Wi.copier.copy(wi, queryView);
			queryView.setApplication(application.getId());
			queryView.setCreatorPerson(effectivePerson.getDistinguishedName());
			queryView.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			queryView.setLastUpdateTime(new Date());
			transQuery(queryView);
			emc.persist(queryView, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(QueryView.class);
			Wo wo = new Wo();
			wo.setId(queryView.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends QueryView {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, QueryView> copier = WrapCopierFactory.wi(Wi.class, QueryView.class, null, Arrays
				.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

	}
}
