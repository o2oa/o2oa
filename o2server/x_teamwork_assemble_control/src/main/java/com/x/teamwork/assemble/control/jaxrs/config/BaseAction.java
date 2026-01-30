package com.x.teamwork.assemble.control.jaxrs.config;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.SystemConfigPersistService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.UserManagerService;
import com.x.teamwork.core.entity.Project;

public class BaseAction extends StandardJaxrsAction{

	protected Cache.CacheCategory cache = new Cache.CacheCategory( Project.class );
	protected SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
	protected SystemConfigPersistService systemConfigPersistService = new SystemConfigPersistService();
	
	protected UserManagerService userManagerService = new UserManagerService();

}
