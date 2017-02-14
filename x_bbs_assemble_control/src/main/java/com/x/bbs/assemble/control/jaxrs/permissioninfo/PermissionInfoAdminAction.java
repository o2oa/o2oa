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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
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
		//从数据库查询论坛列表
		try {
			permissionInfoList = permissionInfoService.listAllPermissionInfo();
			if( permissionInfoList == null ){
				permissionInfoList = new ArrayList<BBSPermissionInfo>();
			}
		} catch (Exception e) {
			result.error( e );
			result.setUserMessage( "系统在查询所有论坛信息时发生异常" );
			logger.error( "system query all forum info got an exception!", e );
		}			
		try {
			wraps = wrapout_copier.copy( permissionInfoList );
		} catch (Exception e) {
			result.error( e );
			result.setUserMessage( "系统在将论坛信息列表转换为输出格式时发生异常" );
			logger.error( "system copy forum list to wraps got an exception!", e );
		}
		result.setData( wraps );
		
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
		Boolean check = true;
		
		if( check ){
			if( roleCode == null || roleCode.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数roleCode为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数roleCode为空，无法继续进行查询" );
			}
		}		
		try {
			permissionInfoList = permissionInfoService.listPermissionByRoleCode( roleCode );
			if( permissionInfoList == null ){
				permissionInfoList = new ArrayList<BBSPermissionInfo>();
			}
		} catch (Exception e) {
			result.error( e );
			result.setUserMessage( "系统在获取指定的角色Code绑定的所有PermissionInfo的信息列表时发生异常" );
			logger.error( "system query permission info by role code an exception!", e );
		}			
		try {
			wraps = wrapout_copier.copy( permissionInfoList );
		} catch (Exception e) {
			result.error( e );
			result.setUserMessage( "系统在将论坛信息列表转换为输出格式时发生异常" );
			logger.error( "system copy forum list to wraps got an exception!", e );
		}
		result.setData( wraps );
		
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
		Boolean check = true;
		
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数forumId为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数forumId为空，无法继续进行查询" );
			}
		}		
		try {
			permissionInfoList = permissionInfoService.listPermissionByForumId( forumId );
			if( permissionInfoList == null ){
				permissionInfoList = new ArrayList<BBSPermissionInfo>();
			}
		} catch (Exception e) {
			result.error( e );
			result.setUserMessage( "系统获取指定的论坛绑定的所有PermissionInfo的信息列表时发生异常" );
			logger.error( "system query permission info by forum id an exception!", e );
		}			
		try {
			wraps = wrapout_copier.copy( permissionInfoList );
		} catch (Exception e) {
			result.error( e );
			result.setUserMessage( "系统在将论坛信息列表转换为输出格式时发生异常" );
			logger.error( "system copy forum list to wraps got an exception!", e );
		}
		result.setData( wraps );
		
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
		Boolean check = true;
		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数sectionId为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数sectionId为空，无法继续进行查询" );
			}
		}		
		try {
			permissionInfoList = permissionInfoService.listPermissionBySection( sectionId );
			if( permissionInfoList == null ){
				permissionInfoList = new ArrayList<BBSPermissionInfo>();
			}
		} catch (Exception e) {
			result.error( e );
			result.setUserMessage( "系统获取指定的版块绑定的所有PermissionInfo的信息列表时发生异常" );
			logger.error( "system query permission info by section id an exception!", e );
		}			
		try {
			wraps = wrapout_copier.copy( permissionInfoList );
		} catch (Exception e) {
			result.error( e );
			result.setUserMessage( "系统在将论坛信息列表转换为输出格式时发生异常" );
			logger.error( "system copy forum list to wraps got an exception!", e );
		}
		result.setData( wraps );
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}