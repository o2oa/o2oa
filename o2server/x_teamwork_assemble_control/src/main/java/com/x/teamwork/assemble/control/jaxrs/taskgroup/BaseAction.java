package com.x.teamwork.assemble.control.jaxrs.taskgroup;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.TaskGroupPersistService;
import com.x.teamwork.assemble.control.service.TaskGroupQueryService;

public class BaseAction extends StandardJaxrsAction {
	
	protected 	TaskGroupQueryService taskGroupQueryService = new TaskGroupQueryService();
	
	protected 	TaskGroupPersistService taskGroupPersistService = new TaskGroupPersistService();
	
	protected ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
