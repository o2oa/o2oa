package com.x.portal.assemble.designer.jaxrs.page;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
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
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Page.class);
			emc.remove(page, CheckRemoveType.all);
			/** 删除需要更新首页 */
			if (StringUtils.equals(page.getId(), portal.getFirstPage())) {
				emc.beginTransaction(Portal.class);
				portal.setFirstPage("");
				for (Page o : emc.list(Page.class, business.page().listWithPortal(portal.getId()))) {
					if (business.page().isFirstPage(o)) {
						portal.setFirstPage(o.getId());
						break;
					}
				}
			}
			emc.commit();
			ApplicationCache.notify(Page.class);
			ApplicationCache.notify(Portal.class);
			result.setData(WrapOutBoolean.trueInstance());
			return result;
		}
	}
}