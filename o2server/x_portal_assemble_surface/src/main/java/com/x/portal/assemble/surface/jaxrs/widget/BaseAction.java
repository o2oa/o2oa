package com.x.portal.assemble.surface.jaxrs.widget;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.core.entity.Widget;

abstract class BaseAction extends StandardJaxrsAction {

	CacheCategory cache = new CacheCategory(Widget.class);

}
