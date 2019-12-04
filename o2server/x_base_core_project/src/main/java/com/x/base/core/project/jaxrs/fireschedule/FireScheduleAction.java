package com.x.base.core.project.jaxrs.fireschedule;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapBoolean;

@Path("fireschedule")
@JaxrsDescribe("触发任务")
public class FireScheduleAction extends StandardJaxrsAction {

	@JaxrsMethodDescribe(value = "接受x_program_center发送过来的运行schedule.", action = ActionExecute.class)
	@GET
	@Path("classname/{className}")
	public void execute(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("运行类") @PathParam("className") String className) throws Exception {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionExecute.Wo> result = new ActionExecute().execute(effectivePerson, servletContext, className);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	public static class Wo extends WrapBoolean {
		public Wo(Boolean value) {
			super(value);
		}
	}
}