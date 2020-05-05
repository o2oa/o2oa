package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectTemplatePersistService;
import com.x.teamwork.assemble.control.service.ProjectTemplateQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.core.entity.ProjectTemplate;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache projectTemplateCache = ApplicationCache.instance().getCache( ProjectTemplate.class );
	
	protected 	ProjectTemplateQueryService projectTemplateQueryService = new ProjectTemplateQueryService();
	
	protected 	ProjectTemplatePersistService projectTemplatePersistService = new ProjectTemplatePersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
