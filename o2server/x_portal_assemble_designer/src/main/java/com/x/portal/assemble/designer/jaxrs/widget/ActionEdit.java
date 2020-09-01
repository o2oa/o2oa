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
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Widget;

class ActionEdit extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Widget widget = emc.find(id, Widget.class);
			if (null == widget) {
				throw new ExceptionEntityNotExist(id, Widget.class);
			}
			Portal portal = emc.find(widget.getPortal(), Portal.class);
			if (null == portal) {
				throw new ExceptionEntityNotExist(widget.getPortal(), Portal.class);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Widget.class);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wi.copier.copy(wi, widget);
			this.checkName(business, widget);
			this.checkAlias(business, widget);
			emc.check(widget, CheckPersistType.all);
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
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}