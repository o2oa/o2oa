package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.cache.ApplicationCache;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( ExcuteBase.class);
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	protected BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
}
