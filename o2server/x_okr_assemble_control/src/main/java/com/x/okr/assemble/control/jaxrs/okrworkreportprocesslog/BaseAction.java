package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	
}
