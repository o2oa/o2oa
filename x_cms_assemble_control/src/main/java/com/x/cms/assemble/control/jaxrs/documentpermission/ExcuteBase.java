package com.x.cms.assemble.control.jaxrs.documentpermission;

import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentPermissionServiceAdv;
import com.x.cms.assemble.control.service.LogService;

public class ExcuteBase {
	
	protected LogService logService = new LogService();
	protected DocumentInfoServiceAdv documentServiceAdv = new DocumentInfoServiceAdv();
	protected DocumentPermissionServiceAdv documentPermissionServiceAdv = new DocumentPermissionServiceAdv();
}
