package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.UserPermissionService;
import com.x.bbs.entity.BBSForumInfo;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction{
	
	protected Ehcache cache = ApplicationCache.instance().getCache( BBSForumInfo.class);
	protected UserPermissionService UserPermissionService = new UserPermissionService();
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	protected BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
}
