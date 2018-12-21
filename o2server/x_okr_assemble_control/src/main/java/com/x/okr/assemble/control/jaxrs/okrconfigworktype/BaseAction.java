package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrConfigWorkTypeService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkPersonSearchService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.entity.OkrConfigWorkType;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected OkrConfigWorkTypeService okrConfigWorkTypeService = new OkrConfigWorkTypeService();
	protected OkrWorkPersonSearchService okrWorkPersonSearchService = new OkrWorkPersonSearchService();
	protected OkrCenterWorkQueryService okrCenterWorkQueryService = new OkrCenterWorkQueryService();
	protected OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected Ehcache cache = ApplicationCache.instance().getCache( OkrConfigWorkType.class );
	protected String catchNamePrefix = this.getClass().getName();
	
}
