package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrConfigWorkLevelService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrConfigWorkLevelService okrConfigWorkLevelService = new OkrConfigWorkLevelService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
}
