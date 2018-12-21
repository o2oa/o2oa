package com.x.query.assemble.designer.jaxrs.output;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.core.entity.wrap.WrapQuery;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected Ehcache cache = ApplicationCache.instance().getCache(CacheObject.class);

	public static class CacheObject {

		private String name;

		private WrapQuery query;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public WrapQuery getQuery() {
			return query;
		}

		public void setQuery(WrapQuery query) {
			this.query = query;
		}

	}

}
