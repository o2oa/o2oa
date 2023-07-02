package com.x.program.center.jaxrs.module;

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
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.program.center.core.entity.Structure;

public class ActionOutputFile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOutputFile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			CacheCategory cacheCategory = new CacheCategory(CacheObject.class);
			CacheKey cacheKey = new CacheKey(flag);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				CacheObject cacheObject = (CacheObject) optional.get();
				WrapModule module = cacheObject.getModule();
				wo = new Wo(gson.toJson(module).getBytes(DefaultCharset.name),
						this.contentType(true, module.getName() +"."+ Structure.default_extension),
						this.contentDisposition(true, module.getName() +"."+ Structure.default_extension));
			} else {
				Structure structure = emc.find(flag, Structure.class);
				if(structure != null){
					StorageMapping mapping = ThisApplication.context().storageMappings().get(Structure.class,
							structure.getStorage());
					if (null == mapping) {
						throw new ExceptionStorageNotExist(structure.getStorage());
					}
					try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
						structure.readContent(mapping, os);
						byte[] bs = os.toByteArray();
						wo = new Wo(bs, this.contentType(true, structure.getName() +"."+ Structure.default_extension),
								this.contentDisposition(true, structure.getName() +"."+ Structure.default_extension));
					}
				}
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