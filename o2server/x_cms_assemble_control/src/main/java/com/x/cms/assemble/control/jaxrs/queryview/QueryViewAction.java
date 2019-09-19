package com.x.cms.assemble.control.jaxrs.queryview;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.x.cms.core.entity.query.Query;


@Path("queryview")
@JaxrsDescribe("数据视图信息管理")
public class QueryViewAction extends BaseAction {

	@JaxrsMethodDescribe(value = "列示所有栏目中的数据视图信息.", action = ActionListAll.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAll( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		ActionResult<List<ActionListAll.Wo>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson( request );
			result = new ActionListAll().execute( request, effectivePerson);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "列示指定栏目中所有当前用户可见的数据视图信息.", action = ActionList.class)
	@GET
	@Path("list/application/flag/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void list( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		ActionResult<List<ActionList.Wo>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson( request );
			result = new ActionList().execute( request, effectivePerson);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示指定栏目中所有当前用户可见的数据视图信息.", action = ActionFlag.class)
	@GET
	@Path("flag/{flag}/application/flag/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void flag( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("数据视图信息标识") @PathParam("flag") String flag ) {
		ActionResult<ActionFlag.Wo> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionFlag().execute( request, effectivePerson, flag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "执行指定栏目中指定数据视图查询.", action = ActionExecute.class)
	@PUT
	@Path("flag/{flag}/application/flag/{appId}/execute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void execute( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("数据视图信息标识") @PathParam("flag") String flag, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("appId") String appId, 
			JsonElement jsonElement) {
		ActionResult<Query> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			try {
				result = new ActionExecute().execute( request, effectivePerson, flag, appId, jsonElement);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}