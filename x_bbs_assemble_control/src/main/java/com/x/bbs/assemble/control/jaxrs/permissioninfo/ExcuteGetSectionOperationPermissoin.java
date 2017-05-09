package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.PermissionInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SectionNotExistsException;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;

public class ExcuteGetSectionOperationPermissoin extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGetSectionOperationPermissoin.class );
	
	protected ActionResult<WrapOutSectionPermission> execute( HttpServletRequest request, EffectivePerson effectivePerson, String sectionId ) throws Exception {
		ActionResult<WrapOutSectionPermission> result = new ActionResult<>();
		WrapOutSectionPermission wrap = new WrapOutSectionPermission();
		RoleAndPermission roleAndPermission = null;
		BBSSectionInfo sectionInfo = null;
		Boolean hasPermission = false;
		Boolean check = true;
		String checkUserPermission = null;
		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			try {
				sectionInfo = sectionInfoService.get( sectionId );
			} catch (Exception e) {
				check = false;
				Exception exception = new PermissionInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + sectionId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new SectionNotExistsException( sectionId );
				result.error( exception );
			}
		}
		if( check ){
			if( !"anonymous".equalsIgnoreCase( effectivePerson.getTokenType().name() ) ){
				try {
					roleAndPermission = userManagerService.getUserRoleAndPermission( effectivePerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new PermissionInfoProcessException( e, "获取用户的论坛访问权限列表时发生异常.Person:" + effectivePerson.getName());
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( roleAndPermission == null ){
				roleAndPermission = new RoleAndPermission();
			}
		}
		//判断主题发布权限
		if( check ){
			if( "所有人".equals( sectionInfo.getSubjectPublishAble() ) ){
				wrap.setSubjectPublishAble( true );
			}else{//判断权限
				checkUserPermission = "SECTION_SUBJECT_PUBLISH_"+ sectionInfo.getId();
				hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
				if( hasPermission ){
					wrap.setSubjectPublishAble( true );
				}
			}
		}
		//判断版块主题回复权限
		if( check ){
			if( "所有人".equals( sectionInfo.getReplyPublishAble() ) ){
				wrap.setReplyPublishAble( true );
			}else{//判断权限
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
				wrap.setSubjectAuditAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_MANAGEMENT_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setSubjectManageAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_RECOMMEND_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setSubjectRecommendAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_STICK_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setSubjectStickAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_SUBJECT_CREAM_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setSubjectCreamAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_REPLY_AUDIT_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setReplyAuditAble(true);
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
			checkUserPermission = "SECTION_SECTION_MANAGER_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setSectionManageAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_PERMISSION_MANAGEMENT_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setSectionPermissoinManageAble(true);
			}
		}
		if( check ){
			checkUserPermission = "SECTION_CONFIG_MANAGEMENT_"+ sectionInfo.getId();
			hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
			if( hasPermission ){
				wrap.setSectionConfigManageAble(true);
			}
		}
		if( check ){
			if( effectivePerson.getName()!= null && sectionInfo.getCreatorName().equalsIgnoreCase( effectivePerson.getName() )){
				wrap.setSectionManageAble( true );
				wrap.setSectionPermissoinManageAble( true );
				wrap.setSectionConfigManageAble( true );
			}
		}
		result.setData( wrap );
		return result;
	}

}