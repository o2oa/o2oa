package com.x.processplatform.assemble.surface.jaxrs.script;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInScript;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutScript;

@Path("script")
public class ScriptAction extends StandardJaxrsAction {
	@HttpMethodDescribe(value = "获取Script以及依赖脚本内容。", response = WrapOutScript.class)
	@POST
	@Path("{flag}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response load(@Context HttpServletRequest request, @PathParam("flag") String flag,
			@PathParam("applicationFlag") String applicationFlag, WrapInScript wrapIn) {
		ActionResult<WrapOutScript> result = new ActionResult<>();
		try {
			result = new ActionLoad().execute(flag, applicationFlag, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}