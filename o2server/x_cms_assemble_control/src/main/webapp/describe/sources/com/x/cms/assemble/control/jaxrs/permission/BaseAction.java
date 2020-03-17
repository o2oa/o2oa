package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentPersistService;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction {
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected DocumentQueryService documentQueryService = new DocumentQueryService();
	protected DocumentPersistService documentPersistService = new DocumentPersistService();

}
