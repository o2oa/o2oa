package com.x.query.assemble.designer.jaxrs.reveal;

import org.apache.commons.lang3.StringUtils;

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
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;

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
			emc.beginTransaction(Reveal.class);
			Reveal reveal = new Reveal();
			Wi.copier.copy(wi, reveal);
			if (StringUtils.isNotEmpty(reveal.getName()) && (!this.idleName(business, reveal))) {
				throw new ExceptionNameExist(reveal.getName());
			}
			if (StringUtils.isNotEmpty(reveal.getAlias()) && (!this.idleAlias(business, reveal))) {
				throw new ExceptionAliasExist(reveal.getAlias());
			}
			reveal.setQuery(query.getId());
			emc.persist(reveal, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Reveal.class);
			Wo wo = new Wo();
			wo.setId(reveal.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Reveal {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Reveal> copier = WrapCopierFactory.wi(Wi.class, Reveal.class, null,
				JpaObject.FieldsUnmodifyExcludeId);
	}

}