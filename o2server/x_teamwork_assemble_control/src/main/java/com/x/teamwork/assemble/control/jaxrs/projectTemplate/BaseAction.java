package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectTemplatePersistService;
import com.x.teamwork.assemble.control.service.ProjectTemplateQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.core.entity.ProjectTemplate;

public class BaseAction extends StandardJaxrsAction {

	protected  Cache.CacheCategory projectTemplateCache = new Cache.CacheCategory( ProjectTemplate.class );
	
	protected 	ProjectTemplateQueryService projectTemplateQueryService = new ProjectTemplateQueryService();
	
	protected 	ProjectTemplatePersistService projectTemplatePersistService = new ProjectTemplatePersistService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
