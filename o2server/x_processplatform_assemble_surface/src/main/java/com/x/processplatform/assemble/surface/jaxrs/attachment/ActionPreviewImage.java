package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.io.FilenameUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DocumentTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;

class ActionPreviewImage extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionPreviewImage.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Integer page) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}

			if (!business.readableWithJob(effectivePerson, attachment.getJob())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}

		byte[] bytes = DocumentTools.toImage(attachment.getName(), attachment.getBytes(), "", page);
		PreviewImageResultObject obj = new PreviewImageResultObject();
		obj.setPerson(effectivePerson.getDistinguishedName());
		obj.setBytes(bytes);
		obj.setName(FilenameUtils.getBaseName(attachment.getName()) + ".png");

		String key = StringTools.uniqueToken();
		CacheCategory cacheCategory = new CacheCategory(PreviewImageResultObject.class);
		CacheKey cacheKey = new CacheKey(key);
		CacheManager.put(cacheCategory, cacheKey, obj);
		Wo wo = new Wo();
		wo.setId(key);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

}