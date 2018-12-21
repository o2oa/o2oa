package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrConfigSystem;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrWorkBaseInfoQueryService  okrWorkBaseInfoQueryService  = new OkrWorkBaseInfoQueryService();
	protected OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected Ehcache cache = ApplicationCache.instance().getCache( OkrConfigSystem.class );
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected String catchNamePrefix = this.getClass().getName();
	
}
