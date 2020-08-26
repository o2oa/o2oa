package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.core.entity.Script;
import com.x.base.core.project.cache.Cache.CacheCategory;

abstract class BaseAction extends StandardJaxrsAction {

	protected CacheCategory cache = new CacheCategory(Script.class);

}
