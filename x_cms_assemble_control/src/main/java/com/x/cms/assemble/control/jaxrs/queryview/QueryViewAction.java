package com.x.cms.assemble.control.jaxrs.queryview;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.core.entity.query.Query;


@Path("queryview")
public class QueryViewAction extends ActionBase {

	@HttpMethodDescribe(value = "列示所有当前用户可见的QueryView.", response = WrapOutQueryView.class)
	@GET
	@Path("list/application/flag/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionList().execute( request, effectivePerson);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示所有当前用户可见的QueryView.", response = WrapOutQueryView.class)
	@GET
	@Path("flag/{flag}/application/flag/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response flag(@Context HttpServletRequest request, @PathParam("flag") String flag) {
		ActionResult<WrapOutQueryView> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionFlag().execute( request, effectivePerson, flag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "执行QueryView查询.", response = Query.class)
	@PUT
	@Path("flag/{flag}/application/flag/{appId}/execute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response execute(@Context HttpServletRequest request, @PathParam("flag") String flag, @PathParam("appId") String appId, JsonElement jsonElement) {
		ActionResult<Query> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInQueryExecute wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInQueryExecute.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			e.printStackTrace();
		}

		if( check ){
			try {
				result = new ActionExecute().execute( request, effectivePerson, flag, appId, wrapIn);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}