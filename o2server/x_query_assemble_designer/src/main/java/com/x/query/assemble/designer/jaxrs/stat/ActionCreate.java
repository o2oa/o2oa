package com.x.query.assemble.designer.jaxrs.stat;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.express.plan.Calculate;

class ActionCreate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Query query = emc.find(wi.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionQueryNotExist(wi.getQuery());
			}
			Business business = new Business(emc);
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getName(), query.getName());
			}
			emc.beginTransaction(Stat.class);
			Stat stat = Wi.copier.copy(wi);
//			View view = emc.find(stat.getView(), View.class);
//			if (null == view) {
//				throw new ExceptionViewNotExist(stat.getView());
//			}
			if (StringUtils.isNotEmpty(stat.getName()) && (!this.idleName(business, stat))) {
				throw new ExceptionNameExist(stat.getName());
			}
			if (StringUtils.isNotEmpty(stat.getAlias()) && (!this.idleAlias(business, stat))) {
				throw new ExceptionAliasExist(stat.getAlias());
			}
			stat.setQuery(query.getId());
			WiData wiData = gson.fromJson(stat.getData(), WiData.class);
			stat.setData(gson.toJson(wiData));

			emc.persist(stat, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Stat.class);
			Wo wo = new Wo();
			wo.setId(stat.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class WiData extends GsonPropertyObject {
		@SuppressWarnings("unused")
		private Calculate calculate;
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Stat {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Stat> copier = WrapCopierFactory.wi(Wi.class, Stat.class, null,
				JpaObject.FieldsUnmodifyExcludeId);
	}

}