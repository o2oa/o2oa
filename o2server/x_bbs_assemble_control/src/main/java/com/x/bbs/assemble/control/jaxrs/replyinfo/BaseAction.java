package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.List;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSReplyInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.UserPermissionService;
import com.x.bbs.entity.BBSSubjectInfo;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction{
	protected Ehcache cache = ApplicationCache.instance().getCache( BBSSubjectInfo.class);
	protected UserPermissionService UserPermissionService = new UserPermissionService();
	protected BBSReplyInfoService replyInfoService = new BBSReplyInfoService();
	protected BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
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
