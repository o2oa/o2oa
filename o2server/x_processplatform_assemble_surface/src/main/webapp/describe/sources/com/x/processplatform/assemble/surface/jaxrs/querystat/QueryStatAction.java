package com.x.processplatform.assemble.surface.jaxrs.querystat;

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
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryStat;
import com.x.processplatform.core.entity.query.Query;

@Path("querystat")
@JaxrsDescribe("统计操作")
public class QueryStatAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(QueryStatAction.class);

	//@HttpMethodDescribe(value = "列示所有当前用户可见的QueryStat.", response = WrapOutQueryStat.class)
	@GET
	@Path("list/application/flag/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void list(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutQueryStat>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionList().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取指定的QueryStat.", action = ActionFlag.class)
	@GET
	@Path("flag/{flag}/application/flag/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void flag(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @PathParam("flag") String flag,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<ActionFlag.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFlag().execute(effectivePerson, flag, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//@HttpMethodDescribe(value = "执行QueryView查询.", response = Query.class)
	@PUT
	@Path("flag/{flag}/application/flag/{applicationFlag}/execute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void execute(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @PathParam("flag") String flag,
			@PathParam("applicationFlag") String applicationFlag, JsonElement jsonElement) {
		ActionResult<Query> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExecute().execute(effectivePerson, flag, applicationFlag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}