package com.x.portal.assemble.designer.jaxrs.widget;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Widget;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
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
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Widget.class);
			emc.remove(widget, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(Widget.class);
			CacheManager.notify(Portal.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}