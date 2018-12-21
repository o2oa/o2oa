package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Element;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			String cacheKey = ApplicationCache.concreteCacheKey(id);
			Element element = cache.get(cacheKey);
			if (null != element && null != element.getObjectValue()) {
				wo = (Wo) element.getObjectValue();
			} else {
				Portal o = emc.find(id, Portal.class);
				if (null == o) {
					throw new PortalNotExistedException(id);
				}
				if (!business.editable(effectivePerson, o)) {
					throw new InvisibleException(effectivePerson.getDistinguishedName(), o.getName(), o.getId());
				}
				wo = Wo.copier.copy(o);
				cache.put(new Element(cacheKey, wo));
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Portal {

		private static final long serialVersionUID = 6993675643268828658L;

		static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}