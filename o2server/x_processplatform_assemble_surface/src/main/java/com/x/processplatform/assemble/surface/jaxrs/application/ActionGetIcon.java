package com.x.processplatform.assemble.surface.jaxrs.application;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.express.assemble.surface.jaxrs.application.ActionGetIconWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGetIcon extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetIcon.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		CacheKey cacheKey = new CacheKey(this.getClass(), flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			wo = (Wo) optional.get();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Application application = business.application().pick(flag);
				if (null != application) {
					wo = new Wo();
					wo.setIcon(application.getIcon());
					wo.setIconHue(application.getIconHue());
					CacheManager.put(cacheCategory, cacheKey, wo);
				}
			}
		}
		result.setData(wo);
		return result;

	}

	
	@Schema(name="com.x.processplatform.assemble.surface.jaxrs.application.ActionGetIcon$Wo")
	public static class Wo extends ActionGetIconWo {

		private static final long serialVersionUID = 2713907116018975830L;

	}

}
