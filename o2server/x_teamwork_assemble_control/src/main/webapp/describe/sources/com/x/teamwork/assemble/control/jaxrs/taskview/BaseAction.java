package com.x.teamwork.assemble.control.jaxrs.taskview;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.assemble.control.service.TaskTagQueryService;
import com.x.teamwork.assemble.control.service.TaskViewPersistService;
import com.x.teamwork.assemble.control.service.TaskViewQueryService;
import com.x.teamwork.core.entity.TaskTag;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected Ehcache taskViewCache = ApplicationCache.instance().getCache( TaskTag.class );
	
	protected 	TaskViewQueryService taskViewQueryService = new TaskViewQueryService();
	
	protected 	TaskViewPersistService taskViewPersistService = new TaskViewPersistService();
	
	protected 	TaskTagQueryService taskTagQueryService = new TaskTagQueryService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
}
