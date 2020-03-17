package com.x.portal.assemble.designer.jaxrs.file;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.portal.core.entity.File;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

class ActionDownload extends StandardJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(File.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag);
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wo = (Wo) element.getObjectValue();
			} else {
				File file = emc.flag(flag, File.class);
				if (null == file) {
					throw new ExceptionEntityNotExist(flag, File.class);
				}
				Portal portal = emc.find(file.getPortal(), Portal.class);
				if (null == portal) {
					throw new ExceptionEntityNotExist(file.getPortal(), Portal.class);
				}
				byte[] bs = new byte[] {};
				if (StringUtils.isNotEmpty(file.getData())) {
					bs = Base64.decodeBase64(file.getData());
				}
				wo = new Wo(bs, this.contentType(false, file.getFileName()),
						this.contentDisposition(false, file.getFileName()));
				/**
				 * 对10M以下的文件进行缓存
				 */
				if (bs.length < (1024 * 1024 * 10)) {
					cache.put(new Element(cacheKey, wo));
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