package com.x.portal.assemble.designer.jaxrs.menu;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutMenu;
import com.x.portal.core.entity.Menu;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Element;

class ActionGet extends ActionBase {
	ActionResult<WrapOutMenu> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutMenu> result = new ActionResult<>();
			WrapOutMenu wrap = null;
			String cacheKey = ApplicationCache.concreteCacheKey(id);
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wrap = (WrapOutMenu) element.getObjectValue();
			} else {
				Menu menu = emc.find(id, Menu.class);
				if (null == menu) {
					throw new MenuNotExistedException(id);
				}
				wrap = outCopier.copy(menu);
				cache.put(new Element(cacheKey, wrap));
			}
			Portal portal = emc.find(wrap.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(id);
			}
			if (!business.portal().editable(effectivePerson, portal)) {
				throw new PortalInvisibleException(effectivePerson.getName(), portal.getName(), portal.getId());
			}
			result.setData(wrap);
			return result;
		}
	}

}