package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.tools.DocumentTools;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;

class ActionOfficePreview extends StandardJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Attachment2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String type) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */
			Attachment2 attachment = emc.find(id, Attachment2.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if (!effectivePerson.isManager() && !StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson())) {
				throw new ExceptionAttachmentAccessDenied(effectivePerson, attachment);
			}
			OriginFile originFile = emc.find(attachment.getOriginFile(),OriginFile.class);
			if (null == originFile) {
				throw new ExceptionAttachmentNotExist(id,attachment.getOriginFile());
			}
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), id, type);
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wo = (Wo) element.getObjectValue();
			} else {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
						originFile.getStorage());
				if (null == mapping) {
					throw new ExceptionStorageNotExist(originFile.getStorage());
				}
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					originFile.readContent(mapping, os);
					byte[] bs = os.toByteArray();
					byte[] cbs = null;
					if("html".equalsIgnoreCase(type)) {
						type = ".html";
						cbs = DocumentTools.toHtml(attachment.getName(), bs);
					}else{
						type = ".pdf";
						cbs = DocumentTools.toPdf2(attachment.getName(), bs);
					}
					if(cbs!=null){
						wo = new Wo(cbs, this.contentType(false, attachment.getName()+type),
								this.contentDisposition(false, attachment.getName()+type));
						/**
						 * 对10M以下的文件进行缓存
						 */
						if (bs.length < (1024 * 1024 * 10)) {
							cache.put(new Element(cacheKey, wo));
						}
					}else{
						wo = new Wo(bs, this.contentType(false, attachment.getName()),
								this.contentDisposition(false, attachment.getName()));
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