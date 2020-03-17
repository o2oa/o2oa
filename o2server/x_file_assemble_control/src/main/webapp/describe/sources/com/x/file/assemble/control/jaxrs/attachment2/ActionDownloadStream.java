package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;

class ActionDownloadStream extends StandardJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Attachment2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */
			Attachment2 attachment = emc.find(id, Attachment2.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson())) {
				throw new ExceptionAttachmentAccessDenied(effectivePerson, attachment);
			}
			OriginFile originFile = emc.find(attachment.getOriginFile(),OriginFile.class);
			if (null == originFile) {
				throw new ExceptionAttachmentNotExist(id,attachment.getOriginFile());
			}
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), id);
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
					wo = new Wo(bs, this.contentType(true, attachment.getName()),
							this.contentDisposition(true, attachment.getName()));
					/**
					 * 对10M以下的文件进行缓存
					 */
					if (bs.length < (1024 * 1024 * 10)) {
						cache.put(new Element(cacheKey, wo));
					}
				}
			}
			result.setData(wo);
			return result;
		}
	}

	// @HttpMethodDescribe(value =
	// "创建Attachment对象./servlet/attachment/download/{id}")
	// protected void doGet(HttpServletRequest request, HttpServletResponse
	// response)
	// throws ServletException, IOException {
	// try (EntityManagerContainer emc =
	// EntityManagerContainerFactory.instance().create()) {
	// request.setCharacterEncoding(DefaultCharset.name);
	// EffectivePerson effectivePerson = this.effectivePerson(request);
	// String id = this.getURIPart(request.getRequestURI(), "download");
	// /* 确定是否要用application/octet-stream输出 */
	// boolean streamContentType = StringUtils.endsWith(request.getRequestURI(),
	// "/stream");
	// Attachment attachment = emc.find(id, Attachment.class,
	// ExceptionWhen.not_found);
	// if (!StringUtils.equals(effectivePerson.getDistinguishedName(),
	// attachment.getPerson())
	// &&
	// (!attachment.getShareList().contains(effectivePerson.getDistinguishedName()))
	// &&
	// (!attachment.getEditorList().contains(effectivePerson.getDistinguishedName())))
	// {
	// throw new Exception("person{name:" +
	// effectivePerson.getDistinguishedName() + "} access attachment{id:"
	// + id + "} access denied.");
	// }
	// this.setResponseHeader(response, attachment, streamContentType);
	// StorageMapping mapping =
	// ThisApplication.context().storageMappings().get(Attachment.class,
	// attachment.getStorage());
	// if (null == mapping) {
	// throw new ExceptionStorageMappingNotExist(attachment.getStorage());
	// }
	// OutputStream output = response.getOutputStream();
	// attachment.readContent(mapping, output);
	// } catch (Exception e) {
	// e.printStackTrace();
	// ActionResult<Object> result = new ActionResult<>();
	// result.error(e);
	// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	// response.getWriter().print(result.toJson());
	// }
	//
	// }

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}