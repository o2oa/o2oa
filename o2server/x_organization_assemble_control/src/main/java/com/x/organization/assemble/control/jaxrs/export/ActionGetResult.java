package com.x.organization.assemble.control.jaxrs.export;

import java.util.Optional;

import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionGetResult extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetResult.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		logger.debug(effectivePerson, "flag:{}.", flag);
		ActionResult<Wo> result = new ActionResult<>();
		CacheKey cacheKey = new CacheKey(flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (!optional.isPresent()) {
			throw new ExceptionResultNotFound(flag);
		}
		CacheFileResult o = (CacheFileResult) optional.get();
		Wo wo = new Wo(o.getBytes(), this.contentType(true, o.getName()), this.contentDisposition(true, o.getName()));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}