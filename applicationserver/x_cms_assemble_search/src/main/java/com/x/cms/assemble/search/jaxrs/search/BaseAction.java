package com.x.cms.assemble.search.jaxrs.search;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.search.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction {
	
	protected UserManagerService userManagerService = new UserManagerService();
	
}
