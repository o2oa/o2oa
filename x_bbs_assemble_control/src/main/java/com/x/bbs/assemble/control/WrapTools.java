package com.x.bbs.assemble.control;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.bbs.assemble.control.jaxrs.configsetting.WrapOutBBSConfigSetting;
import com.x.bbs.assemble.control.jaxrs.foruminfo.WrapInForumInfo;
import com.x.bbs.assemble.control.jaxrs.foruminfo.WrapOutForumInfo;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.WrapOutPermissionInfo;
import com.x.bbs.assemble.control.jaxrs.replyinfo.WrapInReplyInfo;
import com.x.bbs.assemble.control.jaxrs.replyinfo.WrapOutReplyInfo;
import com.x.bbs.assemble.control.jaxrs.roleinfo.WrapInRoleInfo;
import com.x.bbs.assemble.control.jaxrs.roleinfo.WrapOutRoleInfo;
import com.x.bbs.assemble.control.jaxrs.roleinfo.WrapOutUserRole;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.WrapInSectionInfo;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.WrapOutSectionInfo;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.WrapInSubjectInfo;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.WrapOutBBSVoteOption;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.WrapOutSubjectAttachment;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.WrapOutSubjectInfo;
import com.x.bbs.assemble.control.jaxrs.userinfo.WrapOutUserInfo;
import com.x.bbs.entity.BBSConfigSetting;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSUserInfo;
import com.x.bbs.entity.BBSUserRole;
import com.x.bbs.entity.BBSVoteOption;

public class WrapTools {
	
	public static BeanCopyTools<BBSConfigSetting, WrapOutBBSConfigSetting> configSetting_wrapout_copier = BeanCopyToolsBuilder.create( BBSConfigSetting.class, WrapOutBBSConfigSetting.class, null, WrapOutBBSConfigSetting.Excludes);
	
	public static BeanCopyTools< BBSForumInfo, WrapOutForumInfo > forumInfo_wrapout_copier = BeanCopyToolsBuilder.create( BBSForumInfo.class, WrapOutForumInfo.class, null, WrapOutForumInfo.Excludes);
	public static BeanCopyTools< WrapInForumInfo, BBSForumInfo > forumInfo_wrapin_copier = BeanCopyToolsBuilder.create( WrapInForumInfo.class, BBSForumInfo.class, null, WrapInForumInfo.Excludes );
	
	public static BeanCopyTools< BBSPermissionInfo, WrapOutPermissionInfo > permissionInfo_wrapout_copier = BeanCopyToolsBuilder.create( BBSPermissionInfo.class, WrapOutPermissionInfo.class, null, WrapOutPermissionInfo.Excludes);
	
	public static BeanCopyTools< BBSReplyInfo, WrapOutReplyInfo > replyInfo_wrapout_copier = BeanCopyToolsBuilder.create( BBSReplyInfo.class, WrapOutReplyInfo.class, null, WrapOutReplyInfo.Excludes);
	public static BeanCopyTools< WrapInReplyInfo, BBSReplyInfo > replyInfo_wrapin_copier = BeanCopyToolsBuilder.create( WrapInReplyInfo.class, BBSReplyInfo.class, null, WrapInReplyInfo.Excludes );
	
	public static BeanCopyTools< BBSRoleInfo, WrapOutRoleInfo > roleInfo_wrapout_copier = BeanCopyToolsBuilder.create(BBSRoleInfo.class, WrapOutRoleInfo.class, null, WrapOutRoleInfo.Excludes);
	public static BeanCopyTools< WrapInRoleInfo, BBSRoleInfo > roleInfo_wrapin_copier = BeanCopyToolsBuilder.create(WrapInRoleInfo.class, BBSRoleInfo.class, null, WrapInRoleInfo.Excludes);
	
	public static BeanCopyTools< BBSUserRole, WrapOutUserRole > userRole_wrapout_copier = BeanCopyToolsBuilder.create(BBSUserRole.class, WrapOutUserRole.class, null, WrapOutUserRole.Excludes);
	
	public static BeanCopyTools< BBSSectionInfo, WrapOutSectionInfo > sectionInfo_wrapout_copier = BeanCopyToolsBuilder.create( BBSSectionInfo.class, WrapOutSectionInfo.class, null, WrapOutSectionInfo.Excludes);
	public static BeanCopyTools< WrapInSectionInfo, BBSSectionInfo > sectionInfo_wrapin_copier = BeanCopyToolsBuilder.create( WrapInSectionInfo.class, BBSSectionInfo.class, null, WrapInSectionInfo.Excludes );
	
	public static BeanCopyTools< BBSSubjectAttachment, WrapOutSubjectAttachment > subjectAttachment_wrapout_copier = BeanCopyToolsBuilder.create( BBSSubjectAttachment.class, WrapOutSubjectAttachment.class, null, WrapOutSubjectAttachment.Excludes);
	
	public static BeanCopyTools< BBSSubjectInfo, WrapOutSubjectInfo > subjectInfo_wrapout_copier = BeanCopyToolsBuilder.create( BBSSubjectInfo.class, WrapOutSubjectInfo.class, null, WrapOutSubjectInfo.Excludes);
	public static BeanCopyTools< WrapInSubjectInfo, BBSSubjectInfo > subjectInfo_wrapin_copier = BeanCopyToolsBuilder.create( WrapInSubjectInfo.class, BBSSubjectInfo.class, null, WrapInSubjectInfo.Excludes );
	
	public static BeanCopyTools< BBSVoteOption, WrapOutBBSVoteOption > voteOption_wrapout_copier = BeanCopyToolsBuilder.create( BBSVoteOption.class, WrapOutBBSVoteOption.class, null, WrapOutBBSVoteOption.Excludes);

	public static BeanCopyTools< BBSUserInfo, WrapOutUserInfo > userInfo_wrapout_copier = BeanCopyToolsBuilder.create( BBSUserInfo.class, WrapOutUserInfo.class, null, WrapOutUserInfo.Excludes);
	
	
	
	
	
	
}