package com.x.processplatform.assemble.surface.jaxrs.draft;

import javax.ws.rs.Path;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("draft")
@JaxrsDescribe("草稿")
public class DraftAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(DraftAction.class);

	// @JaxrsMethodDescribe(value = "列示指定工作或已完成工作的版式文件历史版本.", action = ActionListWithWorkOrWorkCompleted.class)
	// @GET
	// @Path("list/workorworkcompleted/{workOrWorkCompleted}")
	// @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	// @Consumes(MediaType.APPLICATION_JSON)
	// public void listWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
	// 		@Context HttpServletRequest request,
	// 		@JaxrsParameterDescribe("标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
	// 	ActionResult<List<ActionListWithWorkOrWorkCompleted.Wo>> result = new ActionResult<>();
	// 	EffectivePerson effectivePerson = this.effectivePerson(request);
	// 	try {
	// 		result = new ActionListWithWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted);
	// 	} catch (Exception e) {
	// 		logger.error(e, effectivePerson, request, null);
	// 		result.error(e);
	// 	}
	// 	asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	// }

}