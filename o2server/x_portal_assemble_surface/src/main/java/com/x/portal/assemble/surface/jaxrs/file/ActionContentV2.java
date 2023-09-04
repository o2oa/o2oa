package com.x.portal.assemble.surface.jaxrs.file;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.File;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

class ActionContentV2 extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionContentV2.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			CacheKey cacheKey = new CacheKey(this.getClass(), flag);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				wo = ((Wo) optional.get());
			} else {
				File file = emc.flag(flag, File.class);
				if(file == null){
					throw new ExceptionEntityNotExist(flag, File.class);
				}
				byte[] bs = new byte[] {};
				if (StringUtils.isNotEmpty(file.getData())) {
					bs = Base64.decodeBase64(file.getData());
				}
				wo = new Wo(bs, this.contentType(false, file.getFileName()),
						this.contentDisposition(false, file.getFileName()));
				wo.setFastETag(file.getId()+file.getUpdateTime().getTime());
				wo.setMaxAge(3600 * 24);
				wo.setLastModified(file.getUpdateTime());
				CacheManager.put(cache, cacheKey, wo);
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
