package com.x.strategydeploy.assemble.control.inputmeasures;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	protected Ehcache cache = ApplicationCache.instance().getCache(CacheInputResult.class);

	public static class CacheInputResult {

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
