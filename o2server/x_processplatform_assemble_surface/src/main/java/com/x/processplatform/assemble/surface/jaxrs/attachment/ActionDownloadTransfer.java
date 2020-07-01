package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import net.sf.ehcache.Element;

class ActionDownloadTransfer extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDownloadTransfer.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			Element element = cache.get(flag);
			if ((null != element) && (null != element.getObjectValue())) {
				CacheResultObject ro = (CacheResultObject) element.getObjectValue();
				wo = new Wo(ro.getBytes(), this.contentType(false, ro.getName()),
						this.contentDisposition(false, ro.getName()));
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
