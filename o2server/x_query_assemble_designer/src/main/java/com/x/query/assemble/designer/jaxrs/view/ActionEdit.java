package com.x.query.assemble.designer.jaxrs.view;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;

class ActionEdit extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			View view = emc.find(id, View.class);
			if (null == view) {
				throw new ExceptionViewNotExist(id);
			}
			Query query = emc.find(view.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionQueryNotExist(view.getQuery());
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName());
			}
			switch (StringUtils.trimToEmpty(wi.getType())) {
			case View.TYPE_CMS:
				//view.setData(gson.toJson(gson.fromJson(view.getData(), CmsPlan.class)));
				break;
			case View.TYPE_PROCESSPLATFORM:
				//view.setData(gson.toJson(gson.fromJson(view.getData(), ProcessPlatformPlan.class)));
				break;
			default:
				throw new ExceptionTypeValue(wi.getType());
			}
			emc.beginTransaction(View.class);
			Wi.copier.copy(wi, view);
			view.setQuery(query.getId());
			if (StringUtils.isNotEmpty(view.getName()) && (!this.idleName(business, view))) {
				throw new ExceptionNameExist(view.getName());
			}
			if (StringUtils.isNotEmpty(view.getAlias()) && (!this.idleAlias(business, view))) {
				throw new ExceptionAliasExist(view.getName());
			}
			emc.check(view, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(View.class);
			Wo wo = new Wo();
			wo.setId(view.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends View {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, View> copier = WrapCopierFactory.wi(Wi.class, View.class, null, JpaObject.FieldsUnmodify);
	}
}
