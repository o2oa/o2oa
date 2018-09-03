package com.x.cms.assemble.search.jaxrs.spider;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.search.service.AppInfoServiceAdv;
import com.x.cms.assemble.search.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.search.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.search.service.FileInfoServiceAdv;
import com.x.cms.assemble.search.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction {
	
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	
	protected DocumentInfoServiceAdv documentInfoServiceAdv = new DocumentInfoServiceAdv();
	
	protected FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	
	protected UserManagerService userManagerService = new UserManagerService();
	
}
