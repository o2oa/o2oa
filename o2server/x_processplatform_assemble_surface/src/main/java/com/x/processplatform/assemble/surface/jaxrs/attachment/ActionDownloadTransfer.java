package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionDownloadTransfer extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDownloadTransfer.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			CacheCategory cacheCategory = new CacheCategory(CacheResultObject.class);
			CacheKey cacheKey = new CacheKey(flag);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				CacheResultObject ro = (CacheResultObject) optional.get();
				wo = new Wo(ro.getBytes(), this.contentType(false, ro.getName()),
						this.contentDisposition(false, ro.getName()));
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}
