package com.x.organization.assemble.control.jaxrs.export;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected Ehcache cache = ApplicationCache.instance().getCache(CacheFileResult.class);

	public static class CacheFileResult {

		private String name;

		private byte[] bytes;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

	}
}
