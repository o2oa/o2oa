package com.x.teamwork.assemble.control.jaxrs.list;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.TaskGroupQueryService;
import com.x.teamwork.assemble.control.service.TaskListPersistService;
import com.x.teamwork.assemble.control.service.TaskListQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.assemble.control.service.TaskTagQueryService;
import com.x.teamwork.core.entity.TaskList;

public class BaseAction extends StandardJaxrsAction {

	protected Cache.CacheCategory taskListCache = new Cache.CacheCategory( TaskList.class );
	
	protected 	TaskGroupQueryService taskGroupQueryService = new TaskGroupQueryService();
	
	protected 	TaskListQueryService taskListQueryService = new TaskListQueryService();
	
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
	
	protected 	TaskListPersistService taskListPersistService = new TaskListPersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected TaskTagQueryService taskTagQueryService = new TaskTagQueryService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	
}
