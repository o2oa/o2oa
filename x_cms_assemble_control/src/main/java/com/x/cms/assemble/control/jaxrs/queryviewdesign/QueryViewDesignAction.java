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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.core.entity.query.Query;

@Path("queryview/design")
public class QueryViewDesignAction extends ActionBase {

	@HttpMethodDescribe(value = "列示QueryView对象,下一页.", response = WrapOutQueryView.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
		try {
			result = new ActionListNext().execute(id, count);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示QueryView对象,上一页.", response = WrapOutQueryView.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
		try {
			result = new ActionListPrev().execute(id, count);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取QueryView内容.", response = WrapOutQueryView.class)
	@GET
	@Path("flag/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response flag(@Context HttpServletRequest request, @PathParam("flag") String flag) {
		ActionResult<WrapOutQueryView> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionFlag().execute(effectivePerson, flag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取QueryView内容.", response = WrapOutQueryView.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutQueryView> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建QueryView.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInQueryView wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInQueryView.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			e.printStackTrace();
		}
		if( check ){
			try {
				result = new ActionCreate().execute( request, effectivePerson, wrapIn);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新QueryView.", request = JsonElement.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInQueryView wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInQueryView.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			e.printStackTrace();
		}

		if( check ){
			try {
				result = new ActionUpdate().execute(effectivePerson, id, wrapIn);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除QueryView.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据应用列示视图.", response = WrapOutQueryView.class)
	@GET
	@Path("list/application/{applicationId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithApplication(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId) {
		ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListWithApplication().execute(effectivePerson, applicationId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "模拟执行queryView")
	@PUT
	@Path("flag/{flag}/simulate")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response simulate( @Context HttpServletRequest request, @PathParam("flag") String flag, JsonElement jsonElement ) {
		ActionResult<Query> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Query wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Query.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
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
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}