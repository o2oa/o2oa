package com.x.teamwork.assemble.control.jaxrs.chat;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.ChatPersistService;
import com.x.teamwork.assemble.control.service.ChatQueryService;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.core.entity.Chat;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache chatCache = ApplicationCache.instance().getCache( Chat.class );
	
	protected 	ChatQueryService chatQueryService = new ChatQueryService();
	
	protected 	ChatPersistService chatPersistService = new ChatPersistService();
	
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
