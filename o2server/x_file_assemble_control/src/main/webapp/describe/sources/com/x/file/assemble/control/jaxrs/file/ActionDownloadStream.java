package com.x.file.assemble.control.jaxrs.file;

import java.io.ByteArrayOutputStream;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

class ActionDownloadStream extends StandardJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(File.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), id);
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wo = (Wo) element.getObjectValue();
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
					wo = new Wo(bs, this.contentType(true, file.getName()),
							this.contentDisposition(true, file.getName()));
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

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

	// @HttpMethodDescribe(value =
	// "获取File对象,url格式为:/servlet/file/download/{id}/stream.")
	// protected void doGet(HttpServletRequest request, HttpServletResponse
	// response)
	// throws ServletException, IOException {
	// try (EntityManagerContainer emc =
	// EntityManagerContainerFactory.instance().create()) {
	// request.setCharacterEncoding(DefaultCharset.name);
	// String part = this.getURIPart(request.getRequestURI(), PART_DOWNLOAD);
	// String id = StringUtils.substringBefore(part, "/");
	// /** 确定是否要用application/octet-stream输出 */
	// boolean streamContentType = StringUtils.endsWith(part, "/stream");
	// if (StringUtils.isEmpty(id)) {
	// throw new IdEmptyException();
	// }
	// File file = emc.find(id, File.class);
	// if (null == file) {
	// throw new FileNotExistedException(id);
	// }
	// this.setResponseHeader(response, file, streamContentType);
	// String cacheKey = ApplicationCache.concreteCacheKey(id);
	// Element element = cache.get(cacheKey);
	// byte[] bs = null;
	// if ((null != element) && (null != element.getObjectValue())) {
	// bs = (byte[]) element.getObjectValue();
	// } else {
	// StorageMapping mapping =
	// ThisApplication.context().storageMappings().get(File.class,
	// file.getStorage());
	// if (null == mapping) {
	// throw new StorageMappingNotExistedException(file.getStorage());
	// }
	// try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
	// file.readContent(mapping, os);
	// bs = os.toByteArray();
	// /**
	// * 对2M以下的文件进行缓存
	// */
	// if (bs.length < (1024 * 1024 * 2)) {
	// cache.put(new Element(cacheKey, bs));
	// }
	// }
	// }
	// response.getOutputStream().write(bs);
	// } catch (Exception e) {
	// e.printStackTrace();
	// ActionResult<Object> result = new ActionResult<>();
	// result.error(e);
	// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	// response.getWriter().print(result.toJson());
	// }
	// }

}