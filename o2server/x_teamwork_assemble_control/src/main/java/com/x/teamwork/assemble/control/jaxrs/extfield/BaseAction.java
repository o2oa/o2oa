package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.CustomExtFieldRelePersistService;
import com.x.teamwork.assemble.control.service.CustomExtFieldReleQueryService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.core.entity.CustomExtFieldRele;

public class BaseAction extends StandardJaxrsAction {

	protected Cache.CacheCategory customExtFieldReleCache = new Cache.CacheCategory( CustomExtFieldRele.class );
	
	protected 	CustomExtFieldReleQueryService customExtFieldReleQueryService = new CustomExtFieldReleQueryService();
	
	protected 	CustomExtFieldRelePersistService customExtFieldRelePersistService = new CustomExtFieldRelePersistService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
