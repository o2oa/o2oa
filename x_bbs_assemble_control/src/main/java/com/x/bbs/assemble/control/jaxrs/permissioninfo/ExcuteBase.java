package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoService;
import com.x.bbs.assemble.control.service.BBSSubjectInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( ExcuteBase.class);
	protected BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	protected BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	protected BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	protected UserManagerService userManagerService = new UserManagerService();
	
	protected Boolean checkUserPermission( String checkPermissionCode, List<String> permissionInfoList ) {
		if( permissionInfoList != null && !permissionInfoList.isEmpty() ){
			for( String permissionCode : permissionInfoList ){
				if( checkPermissionCode.equalsIgnoreCase( permissionCode )){
					return true;
				}
			}
		}
		return false;
	}
}
