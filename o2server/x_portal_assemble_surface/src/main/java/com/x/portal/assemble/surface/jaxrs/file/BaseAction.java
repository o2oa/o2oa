package com.x.portal.assemble.surface.jaxrs.file;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.core.entity.File;

abstract class BaseAction extends StandardJaxrsAction {

    protected Cache.CacheCategory cache = new Cache.CacheCategory(File.class);
}
