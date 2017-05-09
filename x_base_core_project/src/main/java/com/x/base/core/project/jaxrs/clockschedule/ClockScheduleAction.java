package com.x.base.core.project.jaxrs.clockschedule;

import java.lang.reflect.Constructor;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.clock.ClockScheduleTask;
import com.x.base.core.project.jaxrs.BooleanWo;
import com.x.base.core.project.jaxrs.ResponseFactory;

@Path("clockschedule")
public class ClockScheduleAction {
	@HttpMethodDescribe(value = "接受x_program_center发送过来的运行schedule.", response = BooleanWo.class)
	@GET
	@Path("clocktaskclassname/{clockTaskClassName}")
	public void get(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@PathParam("clockTaskClassName") String clockTaskClassName) throws Exception {
		ActionResult<BooleanWo> result = new ActionResult<>();
		com.x.base.core.project.Context ctx = (com.x.base.core.project.Context) servletContext
				.getAttribute(com.x.base.core.project.Context.class.getName());
		Class<?> clz = Class.forName(clockTaskClassName);
		Constructor<?> constructor = clz.getConstructor(com.x.base.core.project.Context.class);
		Object o = constructor.newInstance(new Object[] { ctx });
		ctx.timer((ClockScheduleTask) o, 1);
		result.setData(BooleanWo.trueInstance());
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}