package com.x.processplatform.assemble.surface.jaxrs.warp;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("wrap")
@JaxrsDescribe("包装接口")
public class WrapAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(WrapAction.class);

	@JaxrsMethodDescribe(value = "列示指定流程当前用户的Task对象,上一页.", action = ActionGetWorkOrWorkCompleted.class)
	@GET
	@Path("work/or/workcompleted/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
		ActionResult<ActionGetWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWorkOrWorkCompleted().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}