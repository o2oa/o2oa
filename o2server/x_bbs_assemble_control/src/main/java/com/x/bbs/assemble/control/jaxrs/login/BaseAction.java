package com.x.bbs.assemble.control.jaxrs.login;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.service.*;

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
	protected BBSConfigSettingService configSettingService = new BBSConfigSettingService();
}
