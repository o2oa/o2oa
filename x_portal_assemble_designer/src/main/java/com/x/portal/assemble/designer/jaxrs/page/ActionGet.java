package com.x.portal.assemble.designer.jaxrs.page;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutPage;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Element;

class ActionGet extends ActionBase {
	ActionResult<WrapOutPage> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutPage> result = new ActionResult<>();
			WrapOutPage wrap = null;
			String cacheKey = ApplicationCache.concreteCacheKey(id);
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wrap = (WrapOutPage) element.getObjectValue();
			} else {
				Page page = emc.find(id, Page.class);
				if (null == page) {
					throw new PageNotExistedException(id);
				}
				wrap = outCopier.copy(page);
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