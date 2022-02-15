package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			CacheKey cacheKey = new CacheKey(id);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				Portal o = emc.find(id, Portal.class);
				if (null == o) {
					throw new PortalNotExistedException(id);
				}
				if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, o)) {
					throw new InvisibleException(effectivePerson.getDistinguishedName(), o.getName(), o.getId());
				}
				wo = Wo.copier.copy(o);
				CacheManager.put(cache, cacheKey, wo);
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
