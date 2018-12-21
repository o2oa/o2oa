package com.x.bbs.assemble.control.jaxrs.login;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.UserPermissionService;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserPermissionService userPermissionService = new UserPermissionService();
	protected Ehcache cache = ApplicationCache.instance().getCache( BaseAction.class);
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	protected BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
}
