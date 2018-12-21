package com.x.okr.assemble.control.jaxrs.identity;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrSystemIdentityOperatorService;
import com.x.okr.assemble.control.service.OkrSystemIdentityQueryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrSystemIdentityOperatorService okrSystemIdentityOperatorService = new OkrSystemIdentityOperatorService();
	protected OkrSystemIdentityQueryService okrSystemIdentityQueryService = new OkrSystemIdentityQueryService();
	protected OkrUserManagerService userManagerService = new OkrUserManagerService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
}
