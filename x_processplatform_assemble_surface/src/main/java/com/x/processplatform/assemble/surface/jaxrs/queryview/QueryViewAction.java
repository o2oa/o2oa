package com.x.processplatform.assemble.surface.jaxrs.queryview;

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
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryView;
import com.x.processplatform.core.entity.query.Query;

@Path("queryview")
public class QueryViewAction extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(QueryViewAction.class);
	
	@HttpMethodDescribe(value = "列示所有当前用户可见的QueryView而且display=true的.", response = WrapOutQueryView.class)
	@GET
	@Path("list/application/flag/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request, @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionList().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定的QueryView.", response = WrapOutQueryView.class)
	@GET
	@Path("flag/{flag}/application/flag/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response flag(@Context HttpServletRequest request, @PathParam("flag") String flag,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<WrapOutQueryView> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFlag().execute(effectivePerson, flag, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "执行QueryView查询.", response = Query.class)
	@PUT
	@Path("flag/{flag}/application/flag/{applicationFlag}/execute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response execute(@Context HttpServletRequest request, @PathParam("flag") String flag,
			@PathParam("applicationFlag") String applicationFlag, JsonElement jsonElement) {
		ActionResult<Query> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExecute().execute(effectivePerson, flag, applicationFlag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}