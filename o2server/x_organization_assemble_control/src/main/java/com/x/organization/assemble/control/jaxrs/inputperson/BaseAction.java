package com.x.organization.assemble.control.jaxrs.inputperson;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.Business;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected static List<String> genderTypeFemaleItems = Arrays.asList(new String[] { "f","F" ,"女", "female" });
	protected static List<String> genderTypeMaleItems = Arrays.asList(new String[] { "m", "M", "男", "male" });

	protected Ehcache cache = ApplicationCache.instance().getCache(CacheInputResult.class);
	
	protected boolean checkMobile(Business business, String mobile) throws Exception {
		if (!Config.person().isMobile(mobile)) {
			return false;
		}else{
			return true;
		}
	}

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