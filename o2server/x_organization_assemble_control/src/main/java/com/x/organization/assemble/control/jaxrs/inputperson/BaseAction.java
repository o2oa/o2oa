package com.x.organization.assemble.control.jaxrs.inputperson;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected static List<String> genderTypeFemaleItems = Arrays.asList(new String[] { "f", "女", "female" });
	protected static List<String> genderTypeMaleItems = Arrays.asList(new String[] { "m", "男", "male" });

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