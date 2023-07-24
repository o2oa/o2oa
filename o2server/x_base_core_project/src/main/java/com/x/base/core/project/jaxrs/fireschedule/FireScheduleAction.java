package com.x.base.core.project.jaxrs.fireschedule;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

import com.x.base.core.project.annotation.DescribeScope;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FireScheduleAction", description = "触发定时任务接口.")
@Path("fireschedule")
@JaxrsDescribe(value = "触发定时任务接口.", scope = DescribeScope.system)
public class FireScheduleAction extends StandardJaxrsAction {

	private static final String OPERATIONID_PREFIX = "FireScheduleAction::";

	private static final Logger LOGGER = LoggerFactory.getLogger(FireScheduleAction.class);

	@Operation(summary = "接受x_program_center发送过来的运行定时任务指令.", operationId = OPERATIONID_PREFIX + "execute", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionExecute.Wo.class))) })
	@JaxrsMethodDescribe(value = "接受x_program_center发送过来的运行定时任务指令.", action = ActionExecute.class)
	@GET
	@Path("classname/{className}")
	public void execute(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("运行类") @PathParam("className") String className) throws Exception {
		ActionResult<ActionExecute.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExecute().execute(effectivePerson, servletContext, className);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}