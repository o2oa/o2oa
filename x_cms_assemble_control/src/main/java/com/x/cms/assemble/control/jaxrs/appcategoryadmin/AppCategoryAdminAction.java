package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

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
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminProcessException;

@Path("appcategoryadmin")
public class AppCategoryAdminAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( AppCategoryAdminAction.class );

	@HttpMethodDescribe(value = "创建AppCategoryAdmin权限配置信息对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInAppCategoryAdmin wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAppCategoryAdmin.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new AppCategoryAdminProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new AppCategoryAdminProcessException( e, "应用栏目分类管理员配置信息保存时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AppCategoryAdmin权限配置信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = null;
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryAdminProcessException( e, "根据ID删除应用栏目分类管理员配置信息时发生异常。ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取appCategoryAdmin对象.", response = WrapOutAppCategoryAdmin.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutAppCategoryAdmin> result = null;
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryAdminProcessException( e, "根据ID查询应用栏目分类管理员配置信息时发生异常。ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有权限配置信息列表", response = WrapOutAppCategoryAdmin.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAppCategoryAdmin( @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryAdmin>> result = null;
		try {
			result = new ExcuteListAll().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryAdminProcessException( e, "查询所有应用栏目分类管理员配置信息时发生异常。" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "获取指定分类管理员配置列表", response = WrapOutAppCategoryAdmin.class)
	@GET
	@Path("list/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByCategoryId( @Context HttpServletRequest request, @PathParam("categoryId") String categoryId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryAdmin>> result = null;
		try {
			result = new ExcuteListByCategoryId().execute( request, effectivePerson, categoryId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryAdminProcessException( e, "根据分类ID查询所有对应的应用栏目分类管理员配置信息列表时发生异常。CategoryId:" + categoryId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "获取指定管理员栏目管理员配置列表", response = WrapOutAppCategoryAdmin.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAppInfoAdmin(@Context HttpServletRequest request, @PathParam( "appId") String appId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryAdmin>> result = null;
		try {
			result = new ExcuteListByAppId().execute( request, effectivePerson, appId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryAdminProcessException( e, "根据应用栏目ID查询所有对应的应用栏目分类管理员配置信息列表时发生异常。AppId:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "获取指定用户的管理员相关配置列表", response = WrapOutAppCategoryAdmin.class)
	@GET
	@Path("list/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByPersonName(@Context HttpServletRequest request, @PathParam("person") String person) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryAdmin>> result = null;
		try {
			result = new ExcuteListByPersonName().execute( request, effectivePerson, person );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryAdminProcessException( e, "根据管理员姓名查询所有对应的应用栏目分类管理员配置信息列表时发生异常。Person:" + person );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "获取用户有权限访问的权限配置信息列表", response = WrapOutAppCategoryAdmin.class)
	@GET
	@Path("list/{person}/type/{objectType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAppCategoryObjectIdByUser( @Context HttpServletRequest request,
			@PathParam("person") String person, @PathParam("objectType") String objectType ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppCategoryAdmin>> result = null;
		try {
			result = new ExcuteListByPersonNameAndType().execute( request, effectivePerson, person, objectType );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppCategoryAdminProcessException( e, "根据管理员姓名查询所有对应的应用栏目分类管理员配置信息列表时发生异常。Person:" + person + ", objectType:" + objectType );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}	
}