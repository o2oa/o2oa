package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.query.Query;

@Path("queryview/design")
@JaxrsDescribe("数据视图设计信息管理")
public class QueryViewDesignAction extends BaseAction {

	@JaxrsMethodDescribe(value = "列示数据视图设计信息对象,下一页.", action = ActionListNext.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNext( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count
			) {
		ActionResult<List<ActionListNext.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListNext().execute(id, count);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示数据视图设计信息对象,上一页.", action = ActionListPrev.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void standardListPrev( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count
			) {
		ActionResult<List<ActionListPrev.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListPrev().execute(id, count);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据标识获取数据视图设计信息内容.", action = ActionFlag.class)
	@GET
	@Path("flag/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void flag( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("视图信息标识")  @PathParam("flag") String flag) {
		ActionResult<ActionFlag.Wo> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionFlag().execute(effectivePerson, flag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取数据视图设计信息内容.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("视图信息ID") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe( value = "创建数据视图设计信息.", action = ActionCreate.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			try {
				result = new ActionCreate().execute( request, effectivePerson, jsonElement );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe( value = "更新数据视图设计信息.", action = ActionUpdate.class )
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void update( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("视图信息ID") @PathParam("id") String id, 
			JsonElement jsonElement) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			try {
				result = new ActionUpdate().execute(effectivePerson, id, jsonElement );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe( value = "删除数据视图设计信息.", action = ActionDelete.class )
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("视图信息ID") @PathParam("id") String id) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDelete().execute( effectivePerson, id );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe( value = "根据应用列示视图设计信息.", action = ActionListWithApplication.class )
	@GET
	@Path("list/application/{applicationId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithApplication( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("栏目信息ID") @PathParam("applicationId") String applicationId ) {
		ActionResult<List<ActionListWithApplication.Wo>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListWithApplication().execute(effectivePerson, applicationId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe( value = "模拟执行视图设计信息.", action = ActionSimulate.class )
	@PUT
	@Path("flag/{flag}/simulate")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void simulate( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("视图信息标识")@PathParam("flag") String flag, JsonElement jsonElement ) {
		ActionResult<Query> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInQueryViewExecute wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInQueryViewExecute.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			e.printStackTrace();
		}
		if( check ){
			try {
				result = new ActionSimulate().execute(effectivePerson, flag, wrapIn);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}