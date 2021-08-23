package com.x.portal.assemble.designer.jaxrs.page;

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
import com.x.portal.core.entity.Page;
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
				Page page = emc.find(id, Page.class);
				if (null == page) {
					throw new PageNotExistedException(id);
				}
				wo = Wo.copier.copy(page);
				CacheManager.put(cache, cacheKey, wo);
			}
			Portal portal = emc.find(wo.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(id);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new PortalInvisibleException(effectivePerson.getDistinguishedName(), portal.getName(),
						portal.getId());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Page {

		private static final long serialVersionUID = 6147694053942736622L;
		
		static WrapCopier<Page, Wo> copier = WrapCopierFactory.wo(Page.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}