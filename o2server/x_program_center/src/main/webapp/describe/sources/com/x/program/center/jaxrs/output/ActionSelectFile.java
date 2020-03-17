package com.x.program.center.jaxrs.output;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.tools.DefaultCharset;
import net.sf.ehcache.Element;

class ActionSelectFile extends BaseAction {

	private static String extension = ".xapp";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Element element = cache.get(flag);
			if (null == element || null == element.getObjectValue()) {
				throw new ExceptionFlagNotExist(flag);
			}
			CacheObject cacheObject = (CacheObject) element.getObjectValue();
			Wo wo = new Wo( gson.toJson(cacheObject.getModule()).getBytes(DefaultCharset.name ),
					this.contentType(true, cacheObject.getName() + extension ),
					this.contentDisposition(true, cacheObject.getName() + extension ));
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