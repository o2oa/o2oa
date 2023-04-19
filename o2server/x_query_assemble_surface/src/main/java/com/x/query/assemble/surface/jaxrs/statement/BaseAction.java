package com.x.query.assemble.surface.jaxrs.statement;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.core.entity.schema.Statement;

abstract class BaseAction extends StandardJaxrsAction {

    protected CacheCategory cache = new CacheCategory(Statement.class);

}
