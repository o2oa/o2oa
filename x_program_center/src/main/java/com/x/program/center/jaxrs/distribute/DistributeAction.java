package com.x.program.center.jaxrs.distribute;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;

@Path("distribute")
public class DistributeAction extends AbstractJaxrsAction {

	@GET
	@Path("assemble/source/{source}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "为用户分派应用主机。")
	public Response assemble(@Context HttpServletRequest request, @PathParam("source") String source) {
		ActionResult<Map<String, WrapOutAssemble>> result = new ActionResult<>();
		try {
			result = new ActionAssemble().execute(request, source);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("webserver/assemble/source/{source}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "为用户分派应用主机和Web主机。")
	public Response assembleWithWebServer(@Context HttpServletRequest request, @PathParam("source") String source) {
		ActionResult<Map<String, Object>> result = new ActionResult<>();
		try {
			result = new ActionAssembleWithWebServer().execute(request, source);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}