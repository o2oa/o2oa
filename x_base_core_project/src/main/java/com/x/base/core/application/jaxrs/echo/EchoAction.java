package com.x.base.core.application.jaxrs.echo;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;

@Path("echo")
public class EchoAction {
	@HttpMethodDescribe(value = "响应.", response = WrapOutEcho.class)
	@GET
	public Response get(@Context ServletContext servletContext) {
		ActionResult<WrapOutEcho> result = new ActionResult<>();
		WrapOutEcho wrap = new WrapOutEcho();
		wrap.setServletContextName(servletContext.getServletContextName());
		wrap.setServerTime(new Date());
		result.setData(wrap);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}