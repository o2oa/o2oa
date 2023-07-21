package com.x.file.assemble.control.jaxrs.attachment;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

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
import com.x.file.core.entity.personal.Attachment;

class ActionDownloadStream extends StandardJaxrsAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson())
					&& (!attachment.getShareList().contains(effectivePerson.getDistinguishedName()))
					&& (!attachment.getEditorList().contains(effectivePerson.getDistinguishedName()))) {
				throw new ExceptionAttachmentAccessDenied(effectivePerson, attachment);
			}
			CacheCategory cacheCategory = new CacheCategory(Attachment.class);
			CacheKey cacheKey = new CacheKey(this.getClass(), id);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						attachment.getStorage());
				if (null == mapping) {
					throw new ExceptionStorageNotExist(attachment.getStorage());
				}
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					attachment.readContent(mapping, os);
					byte[] bs = os.toByteArray();
					wo = new Wo(bs, this.contentType(true, attachment.getName()),
							this.contentDisposition(true, attachment.getName()));
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

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}