package com.x.query.assemble.designer.jaxrs.view;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;

class ActionEditPermission extends BaseAction {
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
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName());
			}
			emc.beginTransaction(View.class);
			Wi.copier.copy(wi, view);
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
		private static final long serialVersionUID = 751419677780615614L;
		static WrapCopier<Wi, View> copier = WrapCopierFactory.wi(Wi.class, View.class,
				ListTools.toList(View.availableIdentityList_FIELDNAME, View.availableUnitList_FIELDNAME,
				View.availableGroupList_FIELDNAME), null);
	}
}
