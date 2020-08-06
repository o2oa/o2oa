package com.x.processplatform.assemble.designer.jaxrs.output;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;

abstract class BaseAction extends StandardJaxrsAction {

//	protected Ehcache cache = ApplicationCache.instance().getCache(OutputCacheObject.class);
	protected CacheCategory cacheCategory = new CacheCategory(OutputCacheObject.class);

	public static class OutputCacheObject {

		private String name;

		private WrapProcessPlatform application;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public WrapProcessPlatform getApplication() {
			return application;
		}

		public void setApplication(WrapProcessPlatform application) {
			this.application = application;
		}

	}

}
