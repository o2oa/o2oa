package com.x.file.assemble.control.jaxrs.file;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;

class ActionDownload extends StandardJaxrsAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			CacheCategory cacheCategory = new CacheCategory(WoFile.class);
			CacheKey cacheKey = new CacheKey(this.getClass(), id);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				File file = emc.find(id, File.class);
				if (null == file) {
					throw new ExceptionFileNotExisted(id);
				}
				StorageMapping mapping = ThisApplication.context().storageMappings().get(File.class, file.getStorage());
				if (null == mapping) {
					throw new ExceptionStorageMappingNotExisted(file.getStorage());
				}
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					file.readContent(mapping, os);
					byte[] bs = os.toByteArray();
					String fastETag = file.getId()+file.getUpdateTime().getTime();
					wo = new Wo(bs, this.contentType(false, file.getName()),
							this.contentDisposition(false, file.getName()), fastETag);
					/**
					 * 对10M以下的文件进行缓存
					 */
					if (bs.length < (1024 * 1024 * 10)) {
						CacheManager.put(cacheCategory, cacheKey, wo);
					}
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition, String fastETag) {
			super(bytes, contentType, contentDisposition, fastETag);
		}

	}

}
