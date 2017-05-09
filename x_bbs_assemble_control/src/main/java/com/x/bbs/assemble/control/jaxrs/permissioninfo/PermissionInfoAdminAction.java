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

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ForumIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.PermissionInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.RoleCodeEmptyException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SectionIdEmptyException;



@Path("user/permission")
public class PermissionInfoAdminAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( PermissionInfoAdminAction.class );
	
	@HttpMethodDescribe(value = "获取指定的角色Code绑定的所有PermissionInfo的信息列表.", response = WrapOutPermissionInfo.class)
	@GET
	@Path("role/{roleCode}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPermissionByRoleCode( @Context HttpServletRequest request, @PathParam("roleCode") String roleCode ) {
		ActionResult<List<WrapOutPermissionInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( roleCode == null || roleCode.isEmpty() ){
				check = false;
				Exception exception = new RoleCodeEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteListPermissionByRoleCode().execute( request, effectivePerson, roleCode );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new PermissionInfoProcessException( e, "获取指定的角色Code绑定的所有PermissionInfo的信息列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定的论坛绑定的所有PermissionInfo的信息列表.", response = WrapOutPermissionInfo.class)
	@GET
	@Path("forum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPermissionByForumId( @Context HttpServletRequest request, @PathParam("forumId") String forumId ) {
		ActionResult<List<WrapOutPermissionInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
			}
		}		
		if(check){
			try {
				result = new ExcuteListPermissionByRoleCode().execute( request, effectivePerson, forumId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new PermissionInfoProcessException( e, "获取指定的论坛绑定的所有PermissionInfo的信息列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定的版块绑定的所有PermissionInfo的信息列表.", response = WrapOutPermissionInfo.class)
	@GET
	@Path("section/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPermissionBySection( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<List<WrapOutPermissionInfo>> result = new ActionResult<>();
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
				result = new ExcuteListPermissionBySection().execute( request, effectivePerson, sectionId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new PermissionInfoProcessException( e, "获取指定的版块绑定的所有PermissionInfo的信息列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}