package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrWorkReportPersonLinkService;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
	
}
