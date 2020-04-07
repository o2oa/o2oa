package com.x.okr.assemble.control.jaxrs.okrworkchat;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkChatService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;

public class BaseAction extends StandardJaxrsAction {

	protected OkrWorkChatService okrWorkChatService = new OkrWorkChatService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	
}
