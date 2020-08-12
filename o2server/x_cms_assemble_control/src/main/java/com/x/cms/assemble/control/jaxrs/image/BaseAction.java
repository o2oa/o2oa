package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FileInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;

public class BaseAction extends StandardJaxrsAction {

	protected LogService logService = new LogService();
	
	protected FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	
	protected DocumentQueryService documentInfoServiceAdv = new DocumentQueryService();
	
}
