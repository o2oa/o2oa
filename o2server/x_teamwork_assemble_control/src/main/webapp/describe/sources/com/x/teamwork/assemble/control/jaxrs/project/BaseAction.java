package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectGroupPersistService;
import com.x.teamwork.assemble.control.service.ProjectGroupQueryService;
import com.x.teamwork.assemble.control.service.ProjectPersistService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.core.entity.Project;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache projectCache = ApplicationCache.instance().getCache( Project.class );
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected 	ProjectPersistService projectPersistService = new ProjectPersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected ProjectGroupQueryService projectGroupQueryService = new ProjectGroupQueryService();
	
	protected ProjectGroupPersistService projectGroupPersistService = new ProjectGroupPersistService();	
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
