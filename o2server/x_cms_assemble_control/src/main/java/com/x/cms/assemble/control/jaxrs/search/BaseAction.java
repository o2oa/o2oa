package com.x.cms.assemble.control.jaxrs.search;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.PermissionQueryService;


class BaseAction extends StandardJaxrsAction {
	protected PermissionQueryService permissionQueryService = new PermissionQueryService();
}
