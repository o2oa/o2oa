package com.x.base.core.project.jaxrs.openapi;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import com.x.base.core.project.annotation.DescribeScope;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
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

@Tag(name = "OpenApiAction", description = "OpenAPI接口规范.")
@Path("openapi")
@JaxrsDescribe(value = "OpenAPI接口规范.", scope = DescribeScope.system)
public class OpenApiAction extends StandardJaxrsAction {

	private static final String OPERATIONID_PREFIX = "OpenApiAction::";

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiAction.class);

	@Context
	ServletConfig servletConfig;

	@Context
	Application application;

	@Operation(summary = "获取openapi描述文件.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionGet.Wo.class))) })
	@JaxrsMethodDescribe(value = "获取openapi描述文件.", action = ActionGet.class)
	@GET
	public void get(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, servletContext, servletConfig, application);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}