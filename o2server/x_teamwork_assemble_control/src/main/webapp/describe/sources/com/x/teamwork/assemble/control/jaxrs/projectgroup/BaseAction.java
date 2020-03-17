package com.x.teamwork.assemble.control.jaxrs.projectgroup;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectGroupPersistService;
import com.x.teamwork.assemble.control.service.ProjectGroupQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.core.entity.ProjectGroup;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache projectGroupCache = ApplicationCache.instance().getCache( ProjectGroup.class );
	
	protected 	ProjectGroupQueryService projectGroupQueryService = new ProjectGroupQueryService();
	
	protected 	ProjectGroupPersistService projectGroupPersistService = new ProjectGroupPersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
