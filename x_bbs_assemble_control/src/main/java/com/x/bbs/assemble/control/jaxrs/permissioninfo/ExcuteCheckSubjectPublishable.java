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

public class ExcuteCheckSubjectPublishable extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteCheckSubjectPublishable.class );
	
	protected ActionResult<WrapOutPermissionAble> execute( HttpServletRequest request, EffectivePerson effectivePerson, String sectionId ) throws Exception {
		ActionResult<WrapOutPermissionAble> result = new ActionResult<>();
		WrapOutPermissionAble wrap = new WrapOutPermissionAble();
		RoleAndPermission roleAndPermission = null;
		BBSSectionInfo sectionInfo = null;
		Boolean hasPermission = false;
		Boolean publishAble = true;
		Boolean check = true;
		String checkUserPermission = null;
		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				publishAble = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}		
		if( check ){//获取用户的权限列表
			if( "anonymous".equalsIgnoreCase( effectivePerson.getTokenType().name() )){
				roleAndPermission = new RoleAndPermission();
			}else{
				try {
					roleAndPermission = userManagerService.getUserRoleAndPermission( effectivePerson.getName() );
				} catch (Exception e) {
					check = false;
					publishAble = false;
					Exception exception = new PermissionInfoProcessException( e, "获取用户的论坛访问权限列表时发生异常.Person:" + effectivePerson.getName());
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}		
		if( check ){
			try {
				sectionInfo = sectionInfoService.get( sectionId );
			} catch (Exception e) {
				check = false;
				publishAble = false;
				Exception exception = new PermissionInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + sectionId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				publishAble = false;
				Exception exception = new SectionNotExistsException( sectionId );
				result.error( exception );
			}else{
				if( "所有人".equals( sectionInfo.getSubjectPublishAble() ) ){
					//result.setUserMessage( "所有用户都可以在版块["+ sectionInfo.getSectionName() +"]中进行主题发布。" );
				}else{//判断权限
					checkUserPermission = "SECTION_SUBJECT_PUBLISH_"+ sectionInfo.getId();
					hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
					if( !hasPermission ){
						publishAble = false;
						//result.setUserMessage( "用户没有版块["+ sectionInfo.getSectionName() +"]的主题发布权限" );
					}
				}
			}
		}		
		if( check ){
			wrap.setCheckResult( publishAble );
		}		
		result.setData(wrap);
		return result;
	}

}