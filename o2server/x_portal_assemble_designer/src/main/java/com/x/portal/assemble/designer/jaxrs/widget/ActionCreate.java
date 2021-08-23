package com.x.portal.assemble.designer.jaxrs.widget;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Widget;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Portal portal = emc.find(wi.getPortal(), Portal.class);
			if (!business.editable(effectivePerson, portal)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Widget.class);
			Widget widget = Wi.copier.copy(wi);
			this.checkName(business, widget);
			this.checkAlias(business, widget);
			emc.persist(widget, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Widget.class);
			CacheManager.notify(Portal.class);
			Wo wo = new Wo();
			wo.setId(widget.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Widget {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Widget> copier = WrapCopierFactory.wi(Wi.class, Widget.class, null,
				JpaObject.FieldsUnmodifyExcludeId);
	}

	public static class Wo extends WoId {

	}

}