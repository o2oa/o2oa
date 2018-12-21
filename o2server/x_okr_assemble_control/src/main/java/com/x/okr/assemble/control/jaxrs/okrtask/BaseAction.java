package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrUserManagerService userManagerService = new OkrUserManagerService();
	protected OkrTaskService okrTaskService = new OkrTaskService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	protected OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	
}
