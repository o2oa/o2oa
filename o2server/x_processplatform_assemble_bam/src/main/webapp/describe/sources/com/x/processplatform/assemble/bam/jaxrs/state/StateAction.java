package com.x.processplatform.assemble.bam.jaxrs.state;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("state")
@JaxrsDescribe("运行统计")
public class StateAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(StateAction.class);

	@JaxrsMethodDescribe(value = "获取全局统计.", action = ActionSummary.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("summary")
	public void summary(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSummary.Wo> result = new ActionResult<>();
		try {
			result = new ActionSummary().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取运行情况.", action = ActionRunning.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("running")
	public void running(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionRunning.Wo> result = new ActionResult<>();
		try {
			result = new ActionRunning().execute();
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取根据组织,个人的统计.", action = ActionOrganization.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("organization")
	public void organization(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionOrganization.Wo> result = new ActionResult<>();
		try {
			result = new ActionOrganization().execute();
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取根据应用的统计.", action = ActionCategory.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("category")
	public void category(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionCategory.Wo> result = new ActionResult<>();
		try {
			result = new ActionCategory().execute();
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "触发根据应用的统计.", action = ActionCategoryTrigger.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("category/trigger")
	public void categoryTrigger(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionCategoryTrigger.Wo> result = new ActionResult<>();
		try {
			result = new ActionCategoryTrigger().execute();
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "触发获取应用.", action = ActionApplicationStubsTrigger.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("applicationtstubs/trigger")
	public void applicationStubsTrigger(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionApplicationStubsTrigger.Wo> result = new ActionResult<>();
		try {
			result = new ActionApplicationStubsTrigger().execute();
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}