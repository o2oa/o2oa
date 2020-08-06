package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionPreviewPdfResult extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionPreviewPdfResult.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		CacheCategory cacheCategory = new CacheCategory(PreviewPdfResultObject.class);
		CacheKey cacheKey = new CacheKey(flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			PreviewPdfResultObject obj = (PreviewPdfResultObject) optional.get();
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), obj.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			wo = new Wo(obj.getBytes(), this.contentType(true, obj.getName()),
					this.contentDisposition(true, obj.getName()));
			result.setData(wo);
		} else {
			throw new ExceptionPreviewPdfResultObject(flag);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}