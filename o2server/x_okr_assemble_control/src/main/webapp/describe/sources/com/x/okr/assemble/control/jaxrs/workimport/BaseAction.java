package com.x.okr.assemble.control.jaxrs.workimport;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;

public class BaseAction extends StandardJaxrsAction {
	protected OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();
	protected OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoQueryService = new OkrWorkBaseInfoQueryService();
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected DateOperation dateOperation = new DateOperation();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
}
