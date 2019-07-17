package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.List;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoService;
import com.x.bbs.assemble.control.service.BBSSubjectInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.UserPermissionService;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserPermissionService UserPermissionService = new UserPermissionService();
	protected Ehcache cache = ApplicationCache.instance().getCache( BaseAction.class);
	protected BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	protected BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	protected BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	protected UserManagerService userManagerService = new UserManagerService();
	
	protected Boolean checkUserPermission( String checkPermissionCode, List<String> permissionInfoList ) {
		if( ListTools.isNotEmpty( permissionInfoList ) ){
			for( String permissionCode : permissionInfoList ){
				if( checkPermissionCode.equalsIgnoreCase( permissionCode )){
					return true;
				}
			}
		}
		return false;
	}
}
