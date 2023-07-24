package com.x.cms.assemble.control.jaxrs.document;

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

@Path("viewrecord")
@JaxrsDescribe("信息访问日志管理")
public class DocumentViewRecordAction extends StandardJaxrsAction{

	private static  Logger logger = LoggerFactory.getLogger( DocumentViewRecordAction.class );

	@JaxrsMethodDescribe(value = "根据文档ID获取该文档的访问用户记录信息，按时间倒序，前50条.", action = ActionQueryListViewRecordByFilterNext.class)
	@GET
	@Path("document/{docId}/filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByDocumentFilterNext( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("信息文档ID") @PathParam("docId") String docId,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count,
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListViewRecordByFilterNext.Wo>> result = null;
		try {
			result = new ActionQueryListViewRecordByFilterNext().execute( request, effectivePerson, docId, id, count );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionServiceLogic( e,"系统查询文档访问信息时发生未知异常。" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据人员姓名，获取该用户访问的文档记录，按时间倒序，前50条.", action = ActionQueryListViewRecordByPerson.class)
	@GET
	@Path( "person/{name}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByPerson( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("用户姓名") @PathParam("name") String name ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListViewRecordByPerson.Wo>> result = null;
		try {
			result = new ActionQueryListViewRecordByPerson().execute( request, effectivePerson, name );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionServiceLogic( e,"系统查询文档访问信息时发生未知异常。" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "从指定的文档ID列表中判断未读过的文档ID列表.", action = ActionQueryListUnReadDocIds.class)
	@PUT
	@Path( "unread" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listUnReadIds( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< ActionQueryListUnReadDocIds.Wo> result = null;
		try {
			result = new ActionQueryListUnReadDocIds().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionServiceLogic( e,"系统从指定的文档ID列表中判断未读过的文档ID列表时发生未知异常。" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "从指定的文档ID列表中判断未读过的文档ID列表.", action = ActionQueryListUnReadDocIds.class)
	@POST
	@Path( "unread/mockputtopost" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listUnReadIdsMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< ActionQueryListUnReadDocIds.Wo> result = null;
		try {
			result = new ActionQueryListUnReadDocIds().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionServiceLogic( e,"系统从指定的文档ID列表中判断未读过的文档ID列表时发生未知异常。" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据文档ID查询当前用户是否有阅读记录.", action = ActionQueryHasViewDocument.class)
	@GET
	@Path("document/{docId}/has/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void hasViewDocument( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
										  @JaxrsParameterDescribe("信息文档ID") @PathParam("docId") String docId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryHasViewDocument.Wo> result = null;
		try {
			result = new ActionQueryHasViewDocument().execute(effectivePerson, docId);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页查询阅读记录.", action = ActionQueryListViewRecordPaging.class)
	@POST
	@Path("list/install/log/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									 @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
									 @JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<ActionQueryListViewRecordPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionQueryListViewRecordPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

}
