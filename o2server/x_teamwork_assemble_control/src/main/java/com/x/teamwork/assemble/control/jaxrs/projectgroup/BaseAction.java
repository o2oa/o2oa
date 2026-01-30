package com.x.teamwork.assemble.control.jaxrs.projectgroup;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectGroupPersistService;
import com.x.teamwork.assemble.control.service.ProjectGroupQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.core.entity.ProjectGroup;

public class BaseAction extends StandardJaxrsAction {

	protected Cache.CacheCategory projectGroupCache = new Cache.CacheCategory( ProjectGroup.class );
	
	protected 	ProjectGroupQueryService projectGroupQueryService = new ProjectGroupQueryService();
	
	protected 	ProjectGroupPersistService projectGroupPersistService = new ProjectGroupPersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
