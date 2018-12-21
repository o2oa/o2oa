package com.x.bbs.assemble.control.jaxrs.userinfo;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.service.UserPermissionService;

public class BaseAction extends StandardJaxrsAction{
	protected UserPermissionService UserPermissionService = new UserPermissionService();
}
