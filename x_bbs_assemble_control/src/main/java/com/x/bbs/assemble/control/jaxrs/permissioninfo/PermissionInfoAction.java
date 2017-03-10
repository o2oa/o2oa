package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.service.BBSSectionInfoService;
import com.x.bbs.assemble.control.service.BBSSubjectInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

@Path("permission")
public class PermissionInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( PermissionInfoAction.class );
	private BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	private BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	private UserManagerService userManagerService = new UserManagerService();

	@HttpMethodDescribe(value = "查询用户在指定板块中的所有操作权限.", response = WrapOutSectionPermission.class)
	@GET
	@Path("section/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSectionOperationPermissoin( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<WrapOutSectionPermission> result = new ActionResult<>();
		WrapOutSectionPermission wrap = new WrapOutSectionPermission();
		RoleAndPermission roleAndPermission = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSectionInfo sectionInfo = null;
		Boolean hasPermission = false;
		Boolean check = true;
		String checkUserPermission = null;
		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				sectionInfo = sectionInfoService.get( sectionId );
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionQueryByIdException( e, sectionId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new SectionNotExistsException( sectionId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( !"anonymous".equalsIgnoreCase( currentPerson.getTokenType().name() ) ){
				try {
					roleAndPermission = userManagerService.getUserRoleAndPermission( currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new UserRoleAndPermissionGetException( e, currentPerson.getName());
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
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
			if( currentPerson.getName()!= null && sectionInfo.getCreatorName().equalsIgnoreCase( currentPerson.getName() )){
				wrap.setSectionManageAble( true );
				wrap.setSectionPermissoinManageAble( true );
				wrap.setSectionConfigManageAble( true );
			}
		}
		result.setData( wrap );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "查询用户对指定主题的所有操作权限.", response = WrapOutSubjectPermission.class)
	@GET
	@Path("subject/{subjectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSubjectOperationPermissoin( @Context HttpServletRequest request, @PathParam("subjectId") String subjectId ) {
		ActionResult<WrapOutSubjectPermission> result = new ActionResult<>();
		WrapOutSubjectPermission wrap = new WrapOutSubjectPermission();
		RoleAndPermission roleAndPermission = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
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
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				subjectInfo = subjectInfoService.get( subjectId );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectQueryByIdException( e, subjectId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( subjectInfo == null ){
				check = false;
				Exception exception = new SubjectNotExistsException( subjectId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				sectionInfo = sectionInfoService.get( subjectInfo.getSectionId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionQueryByIdException( e, subjectInfo.getSectionId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check && publishAble ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new SectionNotExistsException( subjectInfo.getSectionId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){//获取用户的权限列表
			if( !"anonymous".equalsIgnoreCase( currentPerson.getTokenType().name() )){
				try {
					roleAndPermission = userManagerService.getUserRoleAndPermission( currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new UserRoleAndPermissionGetException( e, currentPerson.getName());
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
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
			if( currentPerson.getName()!= null && subjectInfo.getCreatorName().equalsIgnoreCase( currentPerson.getName() )){
				wrap.setManageAble( true );
			}
		}
		
		result.setData(wrap);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
	@HttpMethodDescribe(value = "查询用户中否可以在指定版块中发布主题.", response = WrapOutPermissionAble.class)
	@GET
	@Path("subjectPublishable/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response subjectPublishable( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<WrapOutPermissionAble> result = new ActionResult<>();
		WrapOutPermissionAble wrap = new WrapOutPermissionAble();
		RoleAndPermission roleAndPermission = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
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
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){//获取用户的权限列表
			if( "anonymous".equalsIgnoreCase( currentPerson.getTokenType().name() )){
				roleAndPermission = new RoleAndPermission();
			}else{
				try {
					roleAndPermission = userManagerService.getUserRoleAndPermission( currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					publishAble = false;
					Exception exception = new UserRoleAndPermissionGetException( e, currentPerson.getName());
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}		
		if( check ){
			try {
				sectionInfo = sectionInfoService.get( sectionId );
			} catch (Exception e) {
				check = false;
				publishAble = false;
				Exception exception = new SectionQueryByIdException( e, sectionId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				publishAble = false;
				Exception exception = new SectionNotExistsException( sectionId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
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
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "查询用户是否可以对指定主题进行回复.", response = WrapOutPermissionAble.class)
	@GET
	@Path("replyPublishable/{subjectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response replyPublishable( @Context HttpServletRequest request, @PathParam("subjectId") String subjectId ) {
		ActionResult<WrapOutPermissionAble> result = new ActionResult<>();
		WrapOutPermissionAble wrap = new WrapOutPermissionAble();
		RoleAndPermission roleAndPermission = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		BBSSectionInfo sectionInfo = null;
		Boolean hasPermission = false;
		Boolean publishAble = true;
		Boolean check = true;
		String checkUserPermission = null;
		
		if( check ){
			if( subjectId == null || subjectId.isEmpty() ){
				check = false;
				publishAble = false;
				Exception exception = new SubjectIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){//获取用户的权限列表
			if( "anonymous".equalsIgnoreCase( currentPerson.getTokenType().name() )){
				roleAndPermission = new RoleAndPermission();
			}else{
				try {
					roleAndPermission = userManagerService.getUserRoleAndPermission( currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					publishAble = false;
					Exception exception = new UserRoleAndPermissionGetException( e, currentPerson.getName());
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			try {
				subjectInfo = subjectInfoService.get( subjectId );
			} catch (Exception e) {
				check = false;
				publishAble = false;
				Exception exception = new SubjectQueryByIdException( e, subjectId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			if( subjectInfo == null ){
				check = false;
				publishAble = false;
				Exception exception = new SubjectNotExistsException( subjectId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}else{
				if( subjectInfo.getStopReply() ){
					publishAble = false;
					//result.setUserMessage( "指定的主题已经置为禁止回复。" );
				}
				if( !"启用".equals( subjectInfo.getSubjectStatus() ) ){
					publishAble = false;
					//result.setUserMessage( "指定的主题状态为["+ subjectInfo.getSubjectStatus() +"]，禁止用户回复。" );
				}
			}
		}
		if( check && publishAble ){ //再判断用户是否可以在主题所在的版块进行回复
			try {
				sectionInfo = sectionInfoService.get( subjectInfo.getSectionId() );
			} catch (Exception e) {
				check = false;
				publishAble = false;
				Exception exception = new SectionQueryByIdException( e, subjectInfo.getSectionId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check && publishAble ){
			if( sectionInfo == null ){
				check = false;
				publishAble = false;
				Exception exception = new SectionNotExistsException( subjectInfo.getSectionId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}else{
				if( "所有人".equals( sectionInfo.getReplyPublishAble() ) ){
					//result.setUserMessage( "所有用户都可以在版块["+ sectionInfo.getSectionName() +"]中进行对主题发表回复。" );
				}else{//判断权限
					checkUserPermission = "SECTION_REPLY_PUBLISH_"+ sectionInfo.getId();
					hasPermission = checkUserPermission( checkUserPermission, roleAndPermission.getPermissionInfoList() );
					if( !hasPermission ){
						publishAble = false;
						//result.setUserMessage( "用户没有版块["+ sectionInfo.getSectionName() +"]的回复发表权限" );
					}
				}
			}
		}
		
		if( check ){
			wrap.setCheckResult( publishAble );
		}
		
		result.setData(wrap);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private Boolean checkUserPermission( String checkPermissionCode, List<String> permissionInfoList ) {
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