package com.x.teamwork.assemble.control.jaxrs.taskListTemplate;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.TaskListTemplatePersistService;
import com.x.teamwork.assemble.control.service.TaskListTemplateQueryService;

public class BaseAction extends StandardJaxrsAction {

	
	protected 	TaskListTemplateQueryService taskListTemplateQueryService = new TaskListTemplateQueryService();
	
	protected 	TaskListTemplatePersistService taskListTemplatePersistService = new TaskListTemplatePersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
