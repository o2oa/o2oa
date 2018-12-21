package com.x.processplatform.assemble.designer.jaxrs.querystat;

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
import com.x.processplatform.core.entity.element.QueryStat;
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
			emc.beginTransaction(QueryStat.class);
			QueryStat queryStat = new QueryStat();
			Wi.copier.copy(wi, queryStat);
			QueryView queryView = emc.find(queryStat.getQueryView(), QueryView.class);
			if (null == queryView) {
				throw new ExceptionQueryViewNotExist(queryStat.getQueryView());
			}
			queryStat.setQueryView(queryView.getId());
			queryStat.setQueryViewName(queryView.getName());
			queryStat.setQueryViewAlias(queryView.getAlias());
			queryStat.setApplication(application.getId());
			queryStat.setCreatorPerson(effectivePerson.getDistinguishedName());
			queryStat.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			queryStat.setLastUpdateTime(new Date());
			emc.persist(queryStat, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(QueryStat.class);
			Wo wo = new Wo();
			wo.setId(queryStat.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends QueryStat {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, QueryStat> copier = WrapCopierFactory.wi(Wi.class, QueryStat.class, null,
				Arrays.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));
	}

}
