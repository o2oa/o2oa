package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.Date;

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
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;

class ActionEditPermission extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Portal o = emc.find(id, Portal.class);
			if (null == o) {
				throw new PortalNotExistedException(id);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, o)) {
				throw new PortalInsufficientPermissionException(effectivePerson.getDistinguishedName(), o.getName(),
						o.getId());
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			emc.beginTransaction(Portal.class);
			Wi.copier.copy(wi, o);
			o.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			o.setLastUpdateTime(new Date());
			emc.check(o, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Portal.class);
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Portal {

		private static final long serialVersionUID = -5769672984923091869L;
		static WrapCopier<Wi, Portal> copier = WrapCopierFactory.wi(Wi.class, Portal.class,
				ListTools.toList(Portal.controllerList_FIELDNAME,Portal.availableIdentityList_FIELDNAME,
						Portal.availableUnitList_FIELDNAME, Portal.availableGroupList_FIELDNAME),
				null);

	}

	public static class Wo extends WoId {
	}
}
