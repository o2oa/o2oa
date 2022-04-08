package com.x.message.assemble.communicate.jaxrs.connector;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Message;

abstract class BaseAction extends StandardJaxrsAction {
    protected CacheCategory cacheCategory = new CacheCategory(Instant.class, Message.class);
}