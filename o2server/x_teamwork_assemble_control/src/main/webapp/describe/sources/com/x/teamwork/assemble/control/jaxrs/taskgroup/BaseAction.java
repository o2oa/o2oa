package com.x.teamwork.assemble.control.jaxrs.taskgroup;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.TaskGroupPersistService;
import com.x.teamwork.assemble.control.service.TaskGroupQueryService;
import com.x.teamwork.core.entity.TaskGroup;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache taskGroupCache = ApplicationCache.instance().getCache( TaskGroup.class );
	
	protected 	TaskGroupQueryService taskGroupQueryService = new TaskGroupQueryService();
	
	protected 	TaskGroupPersistService taskGroupPersistService = new TaskGroupPersistService();
	
	protected ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
