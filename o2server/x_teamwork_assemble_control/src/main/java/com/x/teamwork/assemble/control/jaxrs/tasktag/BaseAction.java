package com.x.teamwork.assemble.control.jaxrs.tasktag;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.assemble.control.service.TaskTagPersistService;
import com.x.teamwork.assemble.control.service.TaskTagQueryService;
import com.x.teamwork.core.entity.TaskTag;


public class BaseAction extends StandardJaxrsAction {
	protected Cache.CacheCategory taskTagCache = new Cache.CacheCategory(TaskTag.class);
	
	protected 	TaskTagQueryService taskTagQueryService = new TaskTagQueryService();
	
	protected 	TaskTagPersistService taskTagPersistService = new TaskTagPersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
}
