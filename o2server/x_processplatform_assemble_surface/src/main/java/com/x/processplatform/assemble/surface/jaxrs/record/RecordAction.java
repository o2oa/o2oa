package com.x.processplatform.assemble.surface.jaxrs.record;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

@Tag(name = "RecordAction", description = "记录接口.")
@Path("record")
@JaxrsDescribe("记录接口.")
public class RecordAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecordAction.class);
	private static final String OPERATIONID_PREFIX = "RecordAction::";

	@Operation(summary = "根据工作或完成工作标识获取记录.", operationId = OPERATIONID_PREFIX
			+ "listWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkOrWorkCompleted.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据工作或完成工作标识获取记录.", action = ActionListWithWorkOrWorkCompleted.class)
	@GET
	@Path("list/workorworkcompleted/{workOrWorkCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作或完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
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

	@Operation(summary = "分页列示根据工作或完成工作标识获取的记录.", operationId = OPERATIONID_PREFIX
			+ "listWithWorkOrWorkCompletedPaging", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkOrWorkCompletedPaging.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "分页列示根据工作或完成工作标识获取的记录.", action = ActionListWithWorkOrWorkCompletedPaging.class)
	@GET
	@Path("list/workorworkcompleted/{workOrWorkCompleted}/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkOrWorkCompletedPaging(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作或完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("数量") @PathParam("size") Integer size) {
		ActionResult<List<ActionListWithWorkOrWorkCompletedPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWorkOrWorkCompletedPaging().execute(effectivePerson, workOrWorkCompleted, page,
					size);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识获取记录.", operationId = OPERATIONID_PREFIX + "listWithJob", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJob.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据任务标识获取记录.", action = ActionListWithJob.class)
	@GET
	@Path("list/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作的job") @PathParam("job") String job) {
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

	@Operation(summary = "分页列示根据任务标识获取的记录.", operationId = OPERATIONID_PREFIX + "listWithJobPaging", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJobPaging.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "分页列示根据任务标识获取的记录.", action = ActionListWithJobPaging.class)
	@GET
	@Path("list/job/{job}/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithJobPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作的job") @PathParam("job") String job,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("数量") @PathParam("size") Integer size) {
		ActionResult<List<ActionListWithJobPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithJobPaging().execute(effectivePerson, job, page, size);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据记录标识删除记录,需要管理权限.", operationId = OPERATIONID_PREFIX + "manageDelete", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageDelete.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据记录标识删除记录,需要管理权限.", action = ActionManageDelete.class)
	@DELETE
	@Path("{id}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionManageDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据记录标识删除记录,需要管理权限(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "manageDeleteMockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManageDelete.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据记录标识删除记录,需要管理权限(mock delete to get).", action = ActionManageDelete.class)
	@GET
	@Path("{id}/manage/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageDeleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionManageDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识创建记录,需要管理权限.", operationId = OPERATIONID_PREFIX + "manageCreateWithJob", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManageCreateWithJob.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageCreateWithJob.Wi.class)) }))
	@JaxrsMethodDescribe(value = "根据任务标识创建记录,需要管理权限.", action = ActionManageCreateWithJob.class)
	@POST
	@Path("job/{job}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageCreateWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job, JsonElement jsonElement) {
		ActionResult<ActionManageCreateWithJob.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageCreateWithJob().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据记录标识修改记录,需要管理权限.", operationId = OPERATIONID_PREFIX + "manageEdit", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManageEdit.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "根据记录标识修改记录,需要管理权限.", action = ActionManageEdit.class)
	@PUT
	@Path("{id}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageEdit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionManageEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageEdit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据记录标识修改记录,需要管理权限(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "manageEdit", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManageEdit.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "根据记录标识修改记录,需要管理权限(mock put to post).", action = ActionManageEdit.class)
	@POST
	@Path("{id}/manage/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageEditMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionManageEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageEdit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}
}
