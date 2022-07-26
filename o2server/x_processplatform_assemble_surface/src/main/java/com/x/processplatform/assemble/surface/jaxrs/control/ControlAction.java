package com.x.processplatform.assemble.surface.jaxrs.control;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ControlAction", description = "权限接口.")
@Path("control")
@JaxrsDescribe("权限接口.")
public class ControlAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControlAction.class);
	private static final String OPERATIONID_PREFIX = "ControlAction::";

	@Operation(summary = "根据工作标识或完成工作标识获取权限.", operationId = OPERATIONID_PREFIX
			+ "getWorkOrWorkCompleted", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionGetWorkOrWorkCompleted.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识或完成工作标识获取权限.", action = ActionGetWorkOrWorkCompleted.class)
	@GET
	@Path("workorworkcompleted/{workOrWorkCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
		ActionResult<ActionGetWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}