package com.x.portal.assemble.designer.jaxrs.page;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Page page = emc.find(id, Page.class);
			if (null == page) {
				throw new PageNotExistedException(id);
			}
			Portal portal = emc.find(page.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(page.getPortal());
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Page.class);
			emc.remove(page, CheckRemoveType.all);
			/** 删除需要更新首页 */
			if (StringUtils.equals(page.getId(), portal.getFirstPage())) {
				emc.beginTransaction(Portal.class);
				portal.setFirstPage("");
				for (Page o : emc.list(Page.class, business.page().listWithPortal(portal.getId()))) {
					portal.setFirstPage(o.getId());
					if (business.page().isFirstPage(o)) {
						break;
					}
				}
			}
			emc.commit();
			CacheManager.notify(Page.class);
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