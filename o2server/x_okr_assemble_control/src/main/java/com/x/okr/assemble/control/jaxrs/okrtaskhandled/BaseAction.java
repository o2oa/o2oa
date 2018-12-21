package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrTaskHandledService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;

public class BaseAction extends StandardJaxrsAction {

	protected OkrUserManagerService userManagerService = new OkrUserManagerService();
	protected OkrTaskHandledService okrTaskHandledService = new OkrTaskHandledService();
	protected OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
}
