package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.PermissionInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SectionNotExistsException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SubjectIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SubjectNotExistsException;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteGetSubjectOperationPermissoin extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGetSubjectOperationPermissoin.class );
	
	protected ActionResult<WrapOutSubjectPermission> execute( HttpServletRequest request, EffectivePerson effectivePerson, String subjectId ) throws Exception {
		ActionResult<WrapOutSubjectPermission> result = new ActionResult<>();
		WrapOutSubjectPermission wrap = new WrapOutSubjectPermission();
		RoleAndPermission roleAndPermission = null;
		BBSSubjectInfo subjectInfo = null;
		BBSSectionInfo sectionInfo = null;
		Boolean hasPermission = false;
		Boolean publishAble = true;
		Boolean check = true;
		String checkUserPermission = null;
		
		if( check ){
			if( subjectId == null || subjectId.isEmpty() ){
				check = false;
				Exception exception = new SubjectIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			try {
				subjectInfo = subjectInfoService.get( subjectId );
			} catch (Exception e) {
				check = false;
				Exception exception = new PermissionInfoProcessException( e, "根据指定ID查询主题信息时发生异常.ID:" + subjectId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( subjectInfo == null ){
				check = false;
				Exception exception = new SubjectNotExistsException( subjectId );
				result.error( exception );
			}
		}
		if( check ){
			try {
				sectionInfo = sectionInfoService.get( subjectInfo.getSectionId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new PermissionInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + subjectInfo.getSectionId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && publishAble ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new SectionNotExistsException( subjectInfo.getSectionId() );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){//获取用户的权限列表
			if( !"anonymous".equalsIgnoreCase( effectivePerson.getTokenType().name() )){
				try {
					roleAndPermission = userManagerService.getUserRoleAndPermission( effectivePerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new PermissionInfoProcessException( e, "获取用户的论坛访问权限列表时发生异常.Person:" + effectivePerson.getName());
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( roleAndPermission == null ){
			roleAndPermission = new RoleAndPermission();
		}
		if( check ){
			if( "所有人".equals( sectionInfo.getReplyPublishAble() ) ){
				wrap.setReplyPublishAble( true );
			}else{
				checkUserPermission = "SECTION_REPLY_PUBLISH_"+ sectionInfo.getId();
				hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
				if( hasPermission ){
					wrap.setReplyPublishAble( true );
				}
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_AUDIT_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setAuditAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_MANAGEMENT_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setManageAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_RECOMMEND_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setRecommendAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_STICK_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setStickAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_CREAM_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setCreamAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_REPLY_AUDIT_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setReplyAuditAble( true );
			}
		}
		if( check ){
			checkUserPermission = "SECTION_REPLY_MANAGEMENT_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setReplyManageAble(true);
			}
		}
		if( check ){
			if( subjectInfo.getStopReply() ){
				wrap.setReplyPublishAble( false );
			}
			if( !"启用".equals( subjectInfo.getSubjectStatus() ) ){
				wrap.setReplyPublishAble( false );
			}
		}
		//自己发的贴子自己可以删除和编辑
		if( check ){
			if( effectivePerson.getName()!= null && subjectInfo.getCreatorName().equalsIgnoreCase( effectivePerson.getName() )){
				wrap.setManageAble( true );
			}
		}
		
		result.setData(wrap);
		return result;
	}

}