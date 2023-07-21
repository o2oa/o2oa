package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.Date;

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
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;

class ActionEdit extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Portal o = emc.find(id, Portal.class);
			if (null == o) {
				throw new PortalNotExistedException(id);
			}
			if (!business.editable(effectivePerson, o)) {
				throw new PortalInsufficientPermissionException(effectivePerson.getDistinguishedName(), o.getName(),
						o.getId());
			}
			emc.beginTransaction(Portal.class);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wi.copier.copy(wi, o);
			o.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			o.setLastUpdateTime(new Date());
			this.checkName(business, o);
			this.checkAlias(business, o);
			emc.check(o, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Portal.class);
			CacheManager.notify(Widget.class);
			CacheManager.notify(Page.class);
			CacheManager.notify(Script.class);
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Portal {

		private static final long serialVersionUID = 8552082924745177797L;
		static WrapCopier<Wi, Portal> copier = WrapCopierFactory.wi(Wi.class, Portal.class, null,
				JpaObject.FieldsUnmodifyIncludePorperties);

	}

	public static class Wo extends WoId {
	}
}
