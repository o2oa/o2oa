package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionProcessException;
import com.x.cms.assemble.control.jaxrs.documentpermission.exception.WrapInConvertException;


@Path("appcategorypermission")
public class AppCategoryPermissionAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AppCategoryPermissionAction.class );
	
	@HttpMethodDescribe(value = "创建AppCategoryPermission权限配置信息对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInAppCategoryPermission wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAppCategoryPermission.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new AppCategoryPermissionProcessException( e, "应用栏目分类权限配置信息保存时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
			
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除AppCategoryPermission权限配置信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryPermissionProcessException( e, "根据ID删除应用栏目分类权限配置信息时发生异常。ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取appCategoryPermission对象.", response = WrapOutAppCategoryPermission.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutAppCategoryPermission> result = null;
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryPermissionProcessException( e, "根据ID查询应用栏目分类权限配置信息时发生异常。ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据栏目ID获取该栏目所有访问权限配置信息列表", response = WrapOutAppCategoryPermission.class)
	@GET
	@Path("list/app/{appId}/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listViewPermissionByApp(@Context HttpServletRequest request, @PathParam("appId")String appId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryPermission>> result = new ActionResult<>();
		try {
			result = new ExcuteListByAppInfo().execute( request, effectivePerson, appId, "VIEW" );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryPermissionProcessException( e, "根据应用栏目ID查询所有对应的应用栏目分类权限配置信息列表时发生异常。AppId:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据栏目ID获取该栏目所有发布权限配置信息列表", response = WrapOutAppCategoryPermission.class)
	@GET
	@Path("list/app/{appId}/publish")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPublishPermissionByApp(@Context HttpServletRequest request, @PathParam("appId")String appId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryPermission>> result = new ActionResult<>();
		try {
			result = new ExcuteListByAppInfo().execute( request, effectivePerson, appId, "PUBLISH" );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryPermissionProcessException( e, "根据应用栏目ID查询所有对应的应用栏目分类权限配置信息列表时发生异常。AppId:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据分类ID获取该栏目所有访问权限配置信息列表", response = WrapOutAppCategoryPermission.class)
	@GET
	@Path("list/category/{categoryId}/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listViewPermissionByCategory(@Context HttpServletRequest request, @PathParam("categoryId")String categoryId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryPermission>> result = new ActionResult<>();
		try {
			result = new ExcuteListByCategory().execute( request, effectivePerson, categoryId, "VIEW" );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryPermissionProcessException( e, "根据分类ID查询所有对应的应用栏目分类权限配置信息列表时发生异常。categoryId:" + categoryId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据分类ID获取该栏目所有发布权限配置信息列表", response = WrapOutAppCategoryPermission.class)
	@GET
	@Path("list/category/{categoryId}/publish")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPublishPermissionByCategory(@Context HttpServletRequest request, @PathParam("categoryId")String categoryId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryPermission>> result = new ActionResult<>();
		try {
			result = new ExcuteListByCategory().execute( request, effectivePerson, categoryId, "PUBLISH" );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryPermissionProcessException( e, "根据分类ID查询所有对应的应用栏目分类权限配置信息列表时发生异常。categoryId:" + categoryId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
}