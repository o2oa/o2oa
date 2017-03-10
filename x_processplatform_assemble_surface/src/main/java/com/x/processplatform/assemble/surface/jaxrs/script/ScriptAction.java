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

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutScript;

@Path("script")
public class ScriptAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ScriptAction.class);

	@HttpMethodDescribe(value = "获取Script以及依赖脚本内容。", response = WrapOutScript.class)
	@POST
	@Path("{flag}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response load(@Context HttpServletRequest request, @PathParam("flag") String flag,
			@PathParam("applicationFlag") String applicationFlag, JsonElement jsonElement) {
		ActionResult<WrapOutScript> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLoad().execute(flag, applicationFlag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}