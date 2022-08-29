package com.x.processplatform.assemble.surface.jaxrs.documentversion;

import java.util.List;

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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "DocumentVersionAction", description = "版本历史接口.")
@Path("documentversion")
@JaxrsDescribe("版本历史接口.")
public class DocumentVersionAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentVersionAction.class);
	private static final String OPERATIONID_PREFIX = "DocumentVersionAction::";

	@Operation(summary = "列示指定工作或已完成工作的版式文件历史版本.", operationId = OPERATIONID_PREFIX
			+ "listWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkOrWorkCompleted.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示指定工作或已完成工作的版式文件历史版本.", action = ActionListWithWorkOrWorkCompleted.class)
	@GET
	@Path("list/workorworkcompleted/{workOrWorkCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
		ActionResult<List<ActionListWithWorkOrWorkCompleted.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示指定工作或已完成工作以及类型的版式文件历史版本.", operationId = OPERATIONID_PREFIX
			+ "listWithWorkOrWorkCompletedCategory", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkOrWorkCompletedCategory.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示指定工作或已完成工作以及类型的版式文件历史版本.", action = ActionListWithWorkOrWorkCompletedCategory.class)
	@GET
	@Path("list/workorworkcompleted/{workOrWorkCompleted}/category/{category}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkOrWorkCompletedCategory(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
			@JaxrsParameterDescribe("类型") @PathParam("category") String category) {
		ActionResult<List<ActionListWithWorkOrWorkCompletedCategory.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWorkOrWorkCompletedCategory().execute(effectivePerson, workOrWorkCompleted,
					category);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示指定任务标识的版式文件历史版本.", operationId = OPERATIONID_PREFIX + "listWithJob", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJob.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示指定任务标识的版式文件历史版本.", action = ActionListWithJob.class)
	@GET
	@Path("list/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job) {
		ActionResult<List<ActionListWithJob.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithJob().execute(effectivePerson, job);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示指定任务标识以及类型的版式文件历史版本.", operationId = OPERATIONID_PREFIX
			+ "listWithJobCategory", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJobCategory.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示指定任务标识以及类型的版式文件历史版本.", action = ActionListWithJobCategory.class)
	@GET
	@Path("list/job/{job}/category/{category}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithJobCategory(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("类型") @PathParam("category") String category) {
		ActionResult<List<ActionListWithJobCategory.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithJobCategory().execute(effectivePerson, job, category);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "指定工作标识创建版式文件版本.", operationId = OPERATIONID_PREFIX + "create", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionCreate.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionCreate.Wi.class)) }))
	@JaxrsMethodDescribe(value = "指定工作标识创建版式文件版本.", action = ActionCreate.class)
	@POST
	@Path("work/{work}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("work") String work, JsonElement jsonElement) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreate().execute(effectivePerson, work, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据版式文件历史版本标识获取内容.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据版式文件历史版本标识获取内容.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}