package com.x.cms.assemble.control.jaxrs.permission;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.proxy.StandardJaxrsActionProxy;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("permission")
@JaxrsDescribe("栏目分类权限配置操作")
public class PermissionAction extends StandardJaxrsAction {

	private StandardJaxrsActionProxy proxy = new StandardJaxrsActionProxy(ThisApplication.context());
	private static  Logger logger = LoggerFactory.getLogger( PermissionAction.class );

	@JaxrsMethodDescribe(value = "查询登录用户是否指定栏目的管理员.", action = ActionIsAppInfoManager.class)
	@GET
	@Path("appInfo/{id}/manageable")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isAppInfoManager( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionIsAppInfoManager.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionIsAppInfoManager)proxy.getProxy(ActionIsAppInfoManager.class)).execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "查询登录用户是否指定栏目的管理员时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询登录用户是否指定分类的管理员.", action = ActionIsCategoryInfoManager.class)
	@GET
	@Path("categoryInfo/{id}/manageable")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isCategoryInfoManagers( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("分类ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionIsCategoryInfoManager.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionIsCategoryInfoManager)proxy.getProxy(ActionIsCategoryInfoManager.class)).execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "查询登录用户是否指定分类的管理员时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询栏目管理员信息列表.", action = ActionListAppInfoManagers.class)
	@GET
	@Path("appInfo/{id}/managers")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAppInfoManagers( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionListAppInfoManagers.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionListAppInfoManagers)proxy.getProxy(ActionListAppInfoManagers.class)).execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "查询栏目管理员信息列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询栏目发布者信息列表.", action = ActionListAppInfoPublishers.class)
	@GET
	@Path("appInfo/{id}/publishers")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAppInfoPublishers( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionListAppInfoPublishers.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionListAppInfoPublishers)proxy.getProxy(ActionListAppInfoPublishers.class)).execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "查询栏目发布者信息列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询栏目可见范围信息.", action = ActionListAppInfoViewers.class)
	@GET
	@Path("appInfo/{id}/viewers")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAppInfoViewers( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionListAppInfoViewers.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionListAppInfoViewers)proxy.getProxy(ActionListAppInfoViewers.class)).execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "查询栏目可见范围信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询分类管理员信息列表.", action = ActionListCategoryInfoManagers.class)
	@GET
	@Path("category/{id}/managers")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCategoryInfoManagers( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("分类ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionListCategoryInfoManagers.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionListCategoryInfoManagers)proxy.getProxy(ActionListCategoryInfoManagers.class)).execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "查询分类管理员信息列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询分类发布者信息列表.", action = ActionListCategoryInfoPublishers.class)
	@GET
	@Path("category/{id}/publishers")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCategoryInfoPublishers( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("分类ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionListCategoryInfoPublishers.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionListCategoryInfoPublishers)proxy.getProxy(ActionListCategoryInfoPublishers.class)).execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "查询分类发布者信息列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询分类发布范围信息.", action = ActionListCategoryInfoViewers.class)
	@GET
	@Path("category/{id}/viewers")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCategoryInfoViewers( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("分类ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionListCategoryInfoViewers.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionListCategoryInfoViewers)proxy.getProxy(ActionListCategoryInfoViewers.class)).execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "查询分类发布范围信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "保存或者更新栏目管理员信息.", action = ActionAppInfoManagerSave.class)
	@POST
	@Path("manager/appInfo/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveAppInfoManager( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionAppInfoManagerSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionAppInfoManagerSave)proxy.getProxy(ActionAppInfoManagerSave.class)).execute( request, effectivePerson, id, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "栏目管理员权限更新时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "保存或者更新栏目发布者信息.", action = ActionAppInfoPublisherSave.class)
	@POST
	@Path("publisher/appInfo/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveAppInfoPublisher( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionAppInfoPublisherSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionAppInfoPublisherSave)proxy.getProxy(ActionAppInfoPublisherSave.class)).execute( request, effectivePerson, id, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "栏目发布者权限更新时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "保存或者更新栏目可见范围信息.", action = ActionAppInfoViewerSave.class)
	@POST
	@Path("viewer/appInfo/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveAppInfoViewer( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionAppInfoViewerSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionAppInfoViewerSave)proxy.getProxy(ActionAppInfoViewerSave.class)).execute( request, effectivePerson, id, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "栏目可见范围权限更新时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "保存或者更新分类管理员信息.", action = ActionCategoryInfoManagerSave.class)
	@POST
	@Path("manager/categoryInfo/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCategoryInfoManager( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement, 
			@JaxrsParameterDescribe("分类ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionCategoryInfoManagerSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionCategoryInfoManagerSave)proxy.getProxy(ActionCategoryInfoManagerSave.class)).execute( request, effectivePerson, id, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "分类管理员权限更新时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "保存或者更新分类发布者信息.", action = ActionCategoryInfoPublisherSave.class)
	@POST
	@Path("publisher/categoryInfo/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCategoryInfoPublisher( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement, 
			@JaxrsParameterDescribe("分类ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionCategoryInfoPublisherSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionCategoryInfoPublisherSave)proxy.getProxy(ActionCategoryInfoPublisherSave.class)).execute( request, effectivePerson, id, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "分类发布者权限更新时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "保存或者更新分类可见范围信息.", action = ActionCategoryInfoViewerSave.class)
	@POST
	@Path("viewer/categoryInfo/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCategoryInfoViewer( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement, 
			@JaxrsParameterDescribe("分类ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionCategoryInfoViewerSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionCategoryInfoViewerSave)proxy.getProxy(ActionCategoryInfoViewerSave.class)).execute( request, effectivePerson, id, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "分类可见范围权限更新时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}	
}