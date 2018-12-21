package com.x.cms.assemble.control.jaxrs.queryview;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;


public abstract class BaseAction extends StandardJaxrsAction {
	
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	
}