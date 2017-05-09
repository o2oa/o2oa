package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Menu;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Source;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Portal portal = emc.find(id, Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(id);
			}
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new PortalInsufficientPermissionException(effectivePerson.getName(), portal.getName(),
						portal.getId());
			}
			emc.beginTransaction(Portal.class);
			emc.beginTransaction(Menu.class);
			emc.beginTransaction(Page.class);
			emc.beginTransaction(Source.class);
			emc.beginTransaction(Script.class);
			this.removeMenu(business, portal.getId());
			this.removePage(business, portal.getId());
			this.removeSource(business, portal.getId());
			this.removeScript(business, portal.getId());
			emc.remove(portal, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Portal.class);
			ApplicationCache.notify(Menu.class);
			ApplicationCache.notify(Page.class);
			ApplicationCache.notify(Source.class);
			ApplicationCache.notify(Script.class);
			result.setData(WrapOutBoolean.trueInstance());
			return result;
		}
	}

	private void removeMenu(Business business, String portalId) throws Exception {
		List<String> ids = business.menu().listWithPortal(portalId);
		for (Menu o : business.entityManagerContainer().list(Menu.class, ids)) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void removePage(Business business, String portalId) throws Exception {
		List<String> ids = business.page().listWithPortal(portalId);
		for (Page o : business.entityManagerContainer().list(Page.class, ids)) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void removeSource(Business business, String portalId) throws Exception {
		List<String> ids = business.source().listWithPortal(portalId);
		for (Source o : business.entityManagerContainer().list(Source.class, ids)) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void removeScript(Business business, String portalId) throws Exception {
		List<String> ids = business.script().listWithPortal(portalId);
		for (Script o : business.entityManagerContainer().list(Script.class, ids)) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}
}