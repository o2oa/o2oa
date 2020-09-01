package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.File;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Portal portal = emc.find(id, Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(id);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new PortalInsufficientPermissionException(effectivePerson.getDistinguishedName(),
						portal.getName(), portal.getId());
			}
			emc.beginTransaction(Portal.class);
			emc.beginTransaction(Widget.class);
			emc.beginTransaction(Page.class);
			emc.beginTransaction(Script.class);
			emc.beginTransaction(File.class);
			this.removeWidget(business, portal.getId());
			this.removePage(business, portal.getId());
			this.removeScript(business, portal.getId());
			this.removeFile(business, portal.getId());
			emc.remove(portal, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(Portal.class);
			CacheManager.notify(Widget.class);
			CacheManager.notify(Page.class);
			CacheManager.notify(Script.class);
			CacheManager.notify(File.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void removeWidget(Business business, String portalId) throws Exception {
		List<String> ids = business.widget().listWithPortal(portalId);
		for (Widget o : business.entityManagerContainer().list(Widget.class, ids)) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void removePage(Business business, String portalId) throws Exception {
		List<String> ids = business.page().listWithPortal(portalId);
		for (Page o : business.entityManagerContainer().list(Page.class, ids)) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void removeScript(Business business, String portalId) throws Exception {
		List<String> ids = business.script().listWithPortal(portalId);
		for (Script o : business.entityManagerContainer().list(Script.class, ids)) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void removeFile(Business business, String portalId) throws Exception {
		for (File o : business.entityManagerContainer().listEqual(File.class, File.portal_FIELDNAME, portalId)) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	public static class Wo extends WrapBoolean {
	}
}