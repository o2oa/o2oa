package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutScript;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

import net.sf.ehcache.Element;

class ActionGet extends ActionBase {
	ActionResult<WrapOutScript> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutScript> result = new ActionResult<>();
			WrapOutScript wrap = null;
			String cacheKey = ApplicationCache.concreteCacheKey(id);
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wrap = (WrapOutScript) element.getObjectValue();
			} else {
				Script script = emc.find(id, Script.class);
				if (null == script) {
					throw new ScriptNotExistedException(id);
				}
				wrap = outCopier.copy(script);
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