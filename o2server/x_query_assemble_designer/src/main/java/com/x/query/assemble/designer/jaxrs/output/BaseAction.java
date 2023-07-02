package com.x.query.assemble.designer.jaxrs.output;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.core.entity.wrap.WrapQuery;

abstract class BaseAction extends StandardJaxrsAction {

	protected CacheCategory cache = new CacheCategory(CacheObject.class);

	public static class CacheObject extends GsonPropertyObject {

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
