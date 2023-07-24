package com.x.meeting.assemble.control.jaxrs.config;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.meeting.core.entity.MeetingConfig;

abstract class BaseAction extends StandardJaxrsAction {

    Cache.CacheCategory cache = new Cache.CacheCategory(MeetingConfig.class);

}
