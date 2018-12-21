package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	
	
}
