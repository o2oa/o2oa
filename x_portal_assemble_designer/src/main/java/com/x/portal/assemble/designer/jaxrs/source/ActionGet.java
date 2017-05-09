package com.x.portal.assemble.designer.jaxrs.source;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutSource;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Source;

import net.sf.ehcache.Element;

class ActionGet extends ActionBase {
	ActionResult<WrapOutSource> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutSource> result = new ActionResult<>();
			WrapOutSource wrap = null;
			String cacheKey = ApplicationCache.concreteCacheKey(id);
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wrap = (WrapOutSource) element.getObjectValue();
			} else {
				Source source = emc.find(id, Source.class);
				if (null == source) {
					throw new SourceNotExistedException(id);
				}
				wrap = outCopier.copy(source);
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