package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	
}
