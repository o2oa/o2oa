package com.x.query.assemble.surface.jaxrs.index;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "IndexAction", description = "检索接口.")
@Path("index")
@JaxrsDescribe("检索接口.")
public class IndexAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexAction.class);
	private static final String OPERATIONID_PREFIX = "IndexAction::";

	@Operation(summary = "执行检索.", operationId = OPERATIONID_PREFIX + "post", responses = { @ApiResponse(content = {
			@Content(schema = @Schema(implementation = ActionPost.Wo.class)) }) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionPost.Wi.class)) }))
	@JaxrsMethodDescribe(value = "执行检索.", action = ActionPost.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void post(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionPost.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPost().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "执行导出.", operationId = OPERATIONID_PREFIX + "export", responses = { @ApiResponse(content = {
			@Content(schema = @Schema(implementation = ActionExport.Wo.class)) }) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionExport.Wi.class)) }))
	@JaxrsMethodDescribe(value = "执行导出.", action = ActionExport.class)
	@POST
	@Path("export")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void export(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionExport.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExport().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取指定标识返回导出结果文件.", operationId = OPERATIONID_PREFIX + "exportResult", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionExportResult.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "获取指定标识返回导出结果文件.", action = ActionExportResult.class)
	@GET
	@Path("export/{flag}/result")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void exportResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
		ActionResult<ActionExportResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExportResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}