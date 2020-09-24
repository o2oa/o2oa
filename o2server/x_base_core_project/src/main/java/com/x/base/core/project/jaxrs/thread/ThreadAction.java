package com.x.base.core.project.jaxrs.thread;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("thread")
@JaxrsDescribe("线程接口")
public class ThreadAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ThreadAction.class);

	@JaxrsMethodDescribe(value = "是否运行中.", action = ActionAlive.class)
	@GET
	@Path("alive/{name}")
	public void alive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context ServletContext servletContext, @PathParam("name") String name) {
		ActionResult<ActionAlive.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAlive().execute(effectivePerson, servletContext, name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取参数.", action = ActionParameter.class)
	@GET
	@Path("parameter/{name}")
	public void parameter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context ServletContext servletContext, @PathParam("name") String name) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionParameter().execute(effectivePerson, servletContext, name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "停止运行.", action = ActionStop.class)
	@GET
	@Path("stop/{name}")
	public void stop(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context ServletContext servletContext, @PathParam("name") String name) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionStop().execute(effectivePerson, servletContext, name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}