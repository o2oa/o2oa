package com.x.teamwork.assemble.control.jaxrs.tasktag;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.assemble.control.service.TaskTagPersistService;
import com.x.teamwork.assemble.control.service.TaskTagQueryService;
import com.x.teamwork.core.entity.TaskTag;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected Ehcache taskTagCache = ApplicationCache.instance().getCache( TaskTag.class );
	
	protected 	TaskTagQueryService taskTagQueryService = new TaskTagQueryService();
	
	protected 	TaskTagPersistService taskTagPersistService = new TaskTagPersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
}
