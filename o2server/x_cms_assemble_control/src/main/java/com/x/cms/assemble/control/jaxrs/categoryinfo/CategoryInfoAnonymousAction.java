package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;


@Path("anonymous/categoryinfo")
@JaxrsDescribe("可匿名访问的信息发布内容分类管理服务")
public class CategoryInfoAnonymousAction extends StandardJaxrsAction{

	private static  Logger logger = LoggerFactory.getLogger( CategoryInfoAnonymousAction.class );

	@JaxrsMethodDescribe(value = "根据Flag获取分类信息对象.", action = ActionGetAnonymous.class)
	@GET
	@Path("{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("栏目标识") @PathParam("flag") String flag) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionGetAnonymous.Wo> result = null;
		try {
			result = new ActionGetAnonymous().execute( request, flag, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionCategoryInfoProcess( e, "根据ID查询分类信息对象时发生异常。flag:" + flag );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的信息分类,下一页.", action = ActionListNextWithFilterAnonymous.class)
	@PUT
	@Path("filter/list/{id}/next/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count,
			@JaxrsParameterDescribe("栏目ID")  @PathParam("appId") String appId,
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListNextWithFilterAnonymous.Wo>> result = null;
		try {
			result = new ActionListNextWithFilterAnonymous().execute( request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionCategoryInfoProcess( e, "列示根据过滤条件的信息分类时发生异常。" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的信息分类,下一页.", action = ActionListNextWithFilterAnonymous.class)
	@POST
	@Path("filter/list/{id}/next/{count}/app/{appId}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilterMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
									@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count,
									@JaxrsParameterDescribe("栏目ID")  @PathParam("appId") String appId,
									JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListNextWithFilterAnonymous.Wo>> result = null;
		try {
			result = new ActionListNextWithFilterAnonymous().execute( request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionCategoryInfoProcess( e, "列示根据过滤条件的信息分类时发生异常。" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取用户有查看访问文章信息的所有分类列表.", action = ActionListWhatICanView_Article.class)
	@GET
	@Path("list/view/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listViewableCategoryInfo_Article( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("栏目ID") @PathParam("appId")String appId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListWhatICanView_Article.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanView_Article().execute( request, appId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionCategoryInfoProcess( e, "根据指定应用栏目ID查询分类信息列表时发生异常。ID:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取用户有查看访问数据信息的所有分类列表.", action = ActionListWhatICanView_Data.class)
	@GET
	@Path("list/view/app/{appId}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listViewableCategoryInfo_Data( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("栏目ID") @PathParam("appId")String appId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListWhatICanView_Data.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanView_Data().execute( request, appId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionCategoryInfoProcess( e, "根据指定应用栏目ID查询分类信息列表时发生异常。ID:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取用户有查看访问信息的所有分类列表.", action = ActionListWhatICanView_AllType.class)
	@GET
	@Path("list/view/app/{appId}/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listViewableCategoryInfo_AllType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("栏目ID") @PathParam("appId")String appId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListWhatICanView_AllType.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanView_AllType().execute( request, appId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionCategoryInfoProcess( e, "根据指定应用栏目ID查询分类信息列表时发生异常。ID:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取用户有权限发布信息的所有分类列表.", action = ActionListWhatICanPublish.class)
	@GET
	@Path("list/publish/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPublishableCategoryInfo( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("栏目ID") @PathParam("appId")String appId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListWhatICanPublish.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanPublish().execute( request, appId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionCategoryInfoProcess( e, "根据应用栏目ID查询分类信息对象时发生异常。AppId:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
