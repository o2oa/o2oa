package com.x.program.center.jaxrs.module;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.WrapModule;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected Ehcache cache = ApplicationCache.instance().getCache(CacheObject.class);

	public static class CacheObject {

		private WrapModule module;

		public WrapModule getModule() {
			return module;
		}

		public void setModule(WrapModule module) {
			this.module = module;
		}

	}

}
