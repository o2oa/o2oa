package com.x.portal.assemble.designer.jaxrs.page;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

import java.util.Optional;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo;
			Portal portal;
			CacheKey cacheKey = new CacheKey(id);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
				portal = emc.find(wo.getPortal(), Portal.class);
				if (null == portal) {
					throw new PortalNotExistedException(wo.getPortal());
				}
			} else {
				Page page = emc.find(id, Page.class);
				if (null == page) {
					throw new PageNotExistedException(id);
				}
				wo = Wo.copier.copy(page);
				portal = emc.find(wo.getPortal(), Portal.class);
				if (null == portal) {
					throw new PortalNotExistedException(wo.getPortal());
				}
				wo.setCornerMarkScript(portal.getProperties().getCornerMarkScript());
				wo.setCornerMarkScriptText(portal.getProperties().getCornerMarkScriptText());
				CacheManager.put(cache, cacheKey, wo);
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

		@FieldDescribe("角标关联脚本.")
		private String cornerMarkScript;

		@FieldDescribe("角标脚本文本.")
		private String cornerMarkScriptText;

		public String getCornerMarkScript() {
			return cornerMarkScript;
		}

		public void setCornerMarkScript(String cornerMarkScript) {
			this.cornerMarkScript = cornerMarkScript;
		}

		public String getCornerMarkScriptText() {
			return cornerMarkScriptText;
		}

		public void setCornerMarkScriptText(String cornerMarkScriptText) {
			this.cornerMarkScriptText = cornerMarkScriptText;
		}
	}

}
