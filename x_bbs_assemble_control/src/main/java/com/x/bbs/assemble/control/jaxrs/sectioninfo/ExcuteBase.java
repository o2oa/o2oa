package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;

public class ExcuteBase {
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	protected BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	protected BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	
}
