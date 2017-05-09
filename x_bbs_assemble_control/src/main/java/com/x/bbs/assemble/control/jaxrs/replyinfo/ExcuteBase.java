package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSReplyInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( ExcuteBase.class);
	protected BBSReplyInfoService replyInfoService = new BBSReplyInfoService();
	protected BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
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
