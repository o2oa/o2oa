package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.ArrayList;
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
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.entity.BBSPermissionInfo;



@Path("user/permission")
public class PermissionInfoAdminAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( PermissionInfoAdminAction.class );
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BeanCopyTools< BBSPermissionInfo, WrapOutPermissionInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSPermissionInfo.class, WrapOutPermissionInfo.class, null, WrapOutPermissionInfo.Excludes);
	
	@HttpMethodDescribe(value = "获取所有PermissionInfo的信息列表.", response = WrapOutPermissionInfo.class)
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutPermissionInfo>> result = new ActionResult<>();
		List<WrapOutPermissionInfo> wraps = new ArrayList<>();
		List<BBSPermissionInfo> permissionInfoList = null;	
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			//从数据库查询论坛列表
			try {
				permissionInfoList = permissionInfoService.listAllPermissionInfo();
				if( permissionInfoList == null ){
					permissionInfoList = new ArrayList<BBSPermissionInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new PermissionListAllException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				wraps = wrapout_copier.copy( permissionInfoList );
				result.setData( wraps );
			} catch (Exception e) {
				Exception exception = new PermissionWrapOutException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定的角色Code绑定的所有PermissionInfo的信息列表.", response = WrapOutPermissionInfo.class)
	@GET
	@Path("role/{roleCode}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPermissionByRoleCode( @Context HttpServletRequest request, @PathParam("roleCode") String roleCode ) {
		ActionResult<List<WrapOutPermissionInfo>> result = new ActionResult<>();
		List<WrapOutPermissionInfo> wraps = new ArrayList<>();
		List<BBSPermissionInfo> permissionInfoList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( roleCode == null || roleCode.isEmpty() ){
				check = false;
				Exception exception = new RoleCodeEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				permissionInfoList = permissionInfoService.listPermissionByRoleCode( roleCode );
				if( permissionInfoList == null ){
					permissionInfoList = new ArrayList<BBSPermissionInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new PermissionListByRoleCodeException( e, roleCode );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}		
		}
		if( check ){
			try {
				wraps = wrapout_copier.copy( permissionInfoList );
				result.setData( wraps );
			} catch (Exception e) {
				Exception exception = new PermissionWrapOutException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
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
		List<WrapOutPermissionInfo> wraps = new ArrayList<>();
		List<BBSPermissionInfo> permissionInfoList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			try {
				permissionInfoList = permissionInfoService.listPermissionByForumId( forumId );
				if( permissionInfoList == null ){
					permissionInfoList = new ArrayList<BBSPermissionInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new PermissionListByForumException( e, forumId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				wraps = wrapout_copier.copy( permissionInfoList );
				result.setData( wraps );
			} catch (Exception e) {
				Exception exception = new PermissionWrapOutException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
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
		List<WrapOutPermissionInfo> wraps = new ArrayList<>();
		List<BBSPermissionInfo> permissionInfoList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
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
				permissionInfoList = permissionInfoService.listPermissionBySection( sectionId );
				if( permissionInfoList == null ){
					permissionInfoList = new ArrayList<BBSPermissionInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new PermissionListBySectionException( e, sectionId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}	
		}
		if( check ){
			try {
				wraps = wrapout_copier.copy( permissionInfoList );
				result.setData( wraps );
			} catch (Exception e) {
				Exception exception = new PermissionWrapOutException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}