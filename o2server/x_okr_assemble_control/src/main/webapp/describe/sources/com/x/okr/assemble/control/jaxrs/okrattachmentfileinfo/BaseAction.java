package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrAttachmentFileInfoService;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportOperationService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;

public class BaseAction extends StandardJaxrsAction {		
	
	protected OkrAttachmentFileInfoService okrAttachmentFileInfoService = new OkrAttachmentFileInfoService();
	protected OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected OkrWorkReportOperationService okrWorkReportOperationService = new OkrWorkReportOperationService();
	
}
