package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	
}
