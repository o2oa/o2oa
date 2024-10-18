package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
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

@Tag(name = "ProcessAction", description = "流程接口.")
@Path("process")
@JaxrsDescribe("流程接口.")
public class ProcessAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ProcessAction.class);
	private static final String OPERATIONID_PREFIX = "ProcessAction::";

	@Operation(summary = "根据流程标识获取流程.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据流程标识获取流程.", action = ActionGet.class)
	@GET
	@Path("{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程标识") @PathParam("flag") String flag) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取流程内容,附带所有的活动节点信息.", operationId = OPERATIONID_PREFIX + "getComplex", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetComplex.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "获取流程内容,附带所有的活动节点信息.", action = ActionGetComplex.class)
	@GET
	@Path("{flag}/complex")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getComplex(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程标识") @PathParam("flag") String flag) {
		ActionResult<ActionGetComplex.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetComplex().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据流程标识获取指定流程可调度到的节点.", operationId = OPERATIONID_PREFIX + "getAllowRerouteTo", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetAllowRerouteTo.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据流程标识获取指定流程可调度到的节点.", action = ActionGetAllowRerouteTo.class)
	@GET
	@Path("{flag}/allowrerouteto")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getAllowRerouteTo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程标识") @PathParam("flag") String flag) {
		ActionResult<ActionGetAllowRerouteTo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetAllowRerouteTo().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据指定应用标识获取可启动的流程.", operationId = OPERATIONID_PREFIX
			+ "listWithPersonWithApplication", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPersonWithApplication.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据指定应用标识获取可启动的流程.", action = ActionListWithPersonWithApplication.class)
	@GET
	@Path("list/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPersonWithApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<ActionListWithPersonWithApplication.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonWithApplication().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据指定应用标识和指定条件获取可启动的流程.", operationId = OPERATIONID_PREFIX
			+ "listWithPersonWithApplicationFilter", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPersonWithApplicationFilter.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionListWithPersonWithApplicationFilter.Wi.class)) }))
	@JaxrsMethodDescribe(value = "根据指定应用标识和指定条件过滤可启动的流程.", action = ActionListWithPersonWithApplicationFilter.class)
	@POST
	@Path("list/application/{applicationFlag}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPersonWithApplicationFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag,
			JsonElement jsonElement) {
		ActionResult<List<ActionListWithPersonWithApplicationFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonWithApplicationFilter().execute(effectivePerson, applicationFlag,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取当前用户在指定流程中可启动流程的身份.", operationId = OPERATIONID_PREFIX
			+ "listAvailableIdentityWithProcess", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListAvailableIdentityWithProcess.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "获取当前用户在指定流程中可启动流程的身份.", action = ActionListAvailableIdentityWithProcess.class)
	@GET
	@Path("list/available/identity/process/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAvailableIdentityWithProcess(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("流程标识") @PathParam("flag") String flag) {
		ActionResult<List<ActionListAvailableIdentityWithProcess.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListAvailableIdentityWithProcess().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据应用标识和流程标识获取流程.", operationId = OPERATIONID_PREFIX
			+ "getWithProcessWithApplication", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionGetWithProcessWithApplication.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据应用标识和流程标识获取流程.", action = ActionGetWithProcessWithApplication.class)
	@GET
	@Path("{flag}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithProcessWithApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("流程标识") @PathParam("flag") String flag,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<ActionGetWithProcessWithApplication.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithProcessWithApplication().execute(effectivePerson, flag, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据流程标识查询流程简要信息.", operationId = OPERATIONID_PREFIX + "ListWithIds", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionListWithProcess.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionListWithProcess.Wi.class)) }))
	@JaxrsMethodDescribe(value = "根据流程标识查询流程简要信息.", action = ActionListWithProcess.class)
	@POST
	@Path("list/ids")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListWithIds(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<List<ActionListWithProcess.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithProcess().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据活动标识和活动类型获取流程节点信息.", operationId = OPERATIONID_PREFIX + "getActivity", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetActivity.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据活动标识和活动类型获取流程节点信息.", action = ActionGetActivity.class)
	@GET
	@Path("activity/{activity}/activityType/{activityType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getActivity(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程节点标志") @PathParam("activity") String activity,
			@JaxrsParameterDescribe("流程节点类型") @PathParam("activityType") String activityType) {
		ActionResult<ActionGetActivity.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetActivity().execute(effectivePerson, activity, activityType);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据指定应用标识获取可管理的流程.", operationId = OPERATIONID_PREFIX
			+ "listControllableWithApplication", responses = { @ApiResponse(content = {
			@Content(schema = @Schema(implementation = ActionListControllableWithApplication.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据指定应用标识获取可管理的流程.", action = ActionListControllableWithApplication.class)
	@GET
	@Path("list/controllable/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listControllableWithApplication(@Suspended final AsyncResponse asyncResponse,
											  @Context HttpServletRequest request,
											  @JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<ActionListControllableWithApplication.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListControllableWithApplication().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据流程标识删除流程实例数据.", operationId = OPERATIONID_PREFIX + "deleteWorkOrWorkCompleted", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteWorkOrWorkCompleted.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "据流程标识删除流程实例数据", action = ActionDeleteWorkOrWorkCompleted.class)
	@DELETE
	@Path("{flag}/{onlyRemoveNotCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					@JaxrsParameterDescribe("流程标识") @PathParam("flag") String flag,
					@JaxrsParameterDescribe("仅删除流转中Work") @PathParam("onlyRemoveNotCompleted") boolean onlyRemoveNotCompleted) {
		ActionResult<ActionDeleteWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWorkOrWorkCompleted().execute(effectivePerson, flag, onlyRemoveNotCompleted);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
