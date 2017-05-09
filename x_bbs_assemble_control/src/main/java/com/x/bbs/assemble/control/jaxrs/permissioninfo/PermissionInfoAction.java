package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingProcessException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SubjectIdEmptyException;

@Path("permission")
public class PermissionInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( PermissionInfoAction.class );

	@HttpMethodDescribe(value = "查询用户在指定板块中的所有操作权限.", response = WrapOutSectionPermission.class)
	@GET
	@Path("section/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSectionOperationPermissoin( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<WrapOutSectionPermission> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteGetSectionOperationPermissoin().execute( request, effectivePerson, sectionId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ConfigSettingProcessException( e, "查询用户在指定板块中的所有操作权限时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "查询用户对指定主题的所有操作权限.", response = WrapOutSubjectPermission.class)
	@GET
	@Path("subject/{subjectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSubjectOperationPermissoin( @Context HttpServletRequest request, @PathParam("subjectId") String subjectId ) {
		ActionResult<WrapOutSubjectPermission> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;		
		if( check ){
			if( subjectId == null || subjectId.isEmpty() ){
				check = false;
				Exception exception = new SubjectIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteGetSubjectOperationPermissoin().execute( request, effectivePerson, subjectId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ConfigSettingProcessException( e, "查询用户对指定主题的所有操作权限时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
	@HttpMethodDescribe(value = "查询用户中否可以在指定版块中发布主题.", response = WrapOutPermissionAble.class)
	@GET
	@Path("subjectPublishable/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response subjectPublishable( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<WrapOutPermissionAble> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}		
		if(check){
			try {
				result = new ExcuteCheckSubjectPublishable().execute( request, effectivePerson, sectionId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ConfigSettingProcessException( e, "查询用户中否可以在指定版块中发布主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "查询用户是否可以对指定主题进行回复.", response = WrapOutPermissionAble.class)
	@GET
	@Path("replyPublishable/{subjectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response replyPublishable( @Context HttpServletRequest request, @PathParam("subjectId") String subjectId ) {
		ActionResult<WrapOutPermissionAble> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( subjectId == null || subjectId.isEmpty() ){
				check = false;
				Exception exception = new SubjectIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteCheckReplyPublishable().execute( request, effectivePerson, subjectId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ConfigSettingProcessException( e, "查询用户是否可以对指定主题进行回复时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	

}