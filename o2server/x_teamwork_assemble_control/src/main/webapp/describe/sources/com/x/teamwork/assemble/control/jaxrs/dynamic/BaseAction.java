package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicQueryService;
import com.x.teamwork.assemble.control.service.ChatQueryService;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.core.entity.Dynamic;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache dynamicCache = ApplicationCache.instance().getCache( Dynamic.class );
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();

	protected 	DynamicQueryService dynamicQueryService = new DynamicQueryService();
	
	protected 	ChatQueryService chatQueryService = new ChatQueryService();
}
