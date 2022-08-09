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

@Path("anonymous/document")
@JaxrsDescribe("信息发布信息文档管理")
public class DocumentAnonymousAction extends StandardJaxrsAction{

	private static  Logger logger = LoggerFactory.getLogger( DocumentAnonymousAction.class );

	@JaxrsMethodDescribe(value = "根据ID访问信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionQueryViewDocument.class)
	@GET
	@Path("{id}/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void view( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryViewDocument.Wo> result = new ActionResult<>();
		try {
			result = new ActionQueryViewDocument().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 下一页.", action = ActionQueryListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count,
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryListNextWithFilter().execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 下一页.", action = ActionQueryListNextWithFilter.class)
	@POST
	@Path("filter/list/{id}/next/{count}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilterMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
									@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count,
									JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryListNextWithFilter().execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页查询符合过滤条件的已发布的信息内容.", action = ActionQueryListWithFilterPaging.class)
	@PUT
	@Path("filter/list/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listWithFilterPaging( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
											@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListWithFilterPaging.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryListWithFilterPaging().execute( request, page, size, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页查询符合过滤条件的已发布的信息内容.", action = ActionQueryListWithFilterPaging.class)
	@POST
	@Path("filter/list/{page}/size/{size}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listWithFilterPagingMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
											@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListWithFilterPaging.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryListWithFilterPaging().execute( request, page, size, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
