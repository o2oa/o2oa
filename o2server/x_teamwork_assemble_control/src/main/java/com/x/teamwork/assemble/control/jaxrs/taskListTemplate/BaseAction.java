package com.x.teamwork.assemble.control.jaxrs.taskListTemplate;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.TaskListTemplatePersistService;
import com.x.teamwork.assemble.control.service.TaskListTemplateQueryService;
import com.x.teamwork.core.entity.TaskListTemplate;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache taskListTemplateCache = ApplicationCache.instance().getCache( TaskListTemplate.class );
	
	protected 	TaskListTemplateQueryService taskListTemplateQueryService = new TaskListTemplateQueryService();
	
	protected 	TaskListTemplatePersistService taskListTemplatePersistService = new TaskListTemplatePersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
