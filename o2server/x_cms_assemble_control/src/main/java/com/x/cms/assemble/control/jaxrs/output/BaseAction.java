package com.x.cms.assemble.control.jaxrs.output;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.core.entity.element.wrap.WrapCms;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected Ehcache cache = ApplicationCache.instance().getCache(OutputCacheObject.class);

	public static class OutputCacheObject {

		private String name;

		private WrapCms cmsAppInfo;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public WrapCms getCmsAppInfo() {
			return cmsAppInfo;
		}

		public void setCmsAppInfo(WrapCms cmsAppInfo) {
			this.cmsAppInfo = cmsAppInfo;
		}		
	}
}
