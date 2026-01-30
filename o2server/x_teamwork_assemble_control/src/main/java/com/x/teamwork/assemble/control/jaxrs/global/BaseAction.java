package com.x.teamwork.assemble.control.jaxrs.global;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.PriorityPersistService;
import com.x.teamwork.assemble.control.service.PriorityQueryService;
import com.x.teamwork.assemble.control.service.ProjectConfigPersistService;
import com.x.teamwork.assemble.control.service.ProjectConfigQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;


public class BaseAction extends StandardJaxrsAction {

	
	protected 	PriorityQueryService priorityQueryService = new PriorityQueryService();
	
	protected 	PriorityPersistService priorityPersistService = new PriorityPersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();

	
	protected 	ProjectConfigPersistService projectConfigPersistService = new ProjectConfigPersistService();
	
	protected 	ProjectConfigQueryService projectConfigQueryService = new ProjectConfigQueryService();
	
}
