package com.x.base.core.project.jaxrs.sysresource;

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

import com.x.base.core.project.annotation.DescribeScope;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.openapi.ActionGet;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "SysResourceAction", description = "系统资源接口.")
@Path("sysresource")
@JaxrsDescribe(value = "系统资源接口.", scope = DescribeScope.system)
public class SysResourceAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(SysResourceAction.class);

	private static final String OPERATIONID_PREFIX = "SysResourceAction::";

	@Operation(summary = "获取静态资源信息.", operationId = OPERATIONID_PREFIX + "listResource", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionListResource.Wo.class))) })
	@JaxrsMethodDescribe(value = "获取静态资源信息.", action = ActionListResource.class)
	@GET
	@Path("filePath/{filePath}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listResource(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("查找路径(根路径:(0))") @PathParam("filePath") String filePath) {
		ActionResult<ActionListResource.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListResource().execute(effectivePerson, filePath);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
