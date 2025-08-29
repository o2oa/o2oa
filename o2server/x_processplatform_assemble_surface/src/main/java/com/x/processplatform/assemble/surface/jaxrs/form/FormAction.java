package com.x.processplatform.assemble.surface.jaxrs.form;

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
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Tag(name = "FormAction", description = "表单接口.")
@Path("form")
@JaxrsDescribe("表单接口.")
public class FormAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormAction.class);
	private static final String OPERATIONID_PREFIX = "FormAction::";

	@Operation(summary = "获取表单内容.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "获取表单内容.", action = ActionGet.class)
	@GET
	@Path("{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("表单标识") @PathParam("flag") String flag) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取移动端表单内容.", operationId = OPERATIONID_PREFIX + "getMobile", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetMobile.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "获取移动端表单内容.", action = ActionGetMobile.class)
	@GET
	@Path("{flag}/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getMobile(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("表单标识") @PathParam("flag") String flag) {
		ActionResult<ActionGetMobile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetMobile().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据标识和应用标识获取表单.", operationId = OPERATIONID_PREFIX + "getMobile", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionGetWithApplication.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据标识和应用标识获取表单.", action = ActionGetWithApplication.class)
	@GET
	@Path("{flag}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithApplication(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("表单标识") @PathParam("flag") String flag,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<ActionGetWithApplication.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithApplication().execute(effectivePerson, applicationFlag, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据标识和应用标识获取移动端表单.", operationId = OPERATIONID_PREFIX
			+ "getWithApplicationMobile", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionGetWithApplicationMobile.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据标识和应用标识获取移动端表单.", action = ActionGetWithApplicationMobile.class)
	@GET
	@Path("{flag}/application/{applicationFlag}/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithApplicationMobile(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("表单标识") @PathParam("flag") String flag,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<ActionGetWithApplicationMobile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithApplicationMobile().execute(effectivePerson, applicationFlag, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "查询表单,如果有表单那么返回表单标识,如果表单不存在且是已完成工作,那么返回储存表单.", operationId = OPERATIONID_PREFIX
			+ "V2LookupWorkOrWorkCompleted", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionGetWithApplicationMobile.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "查询表单,如果有表单那么返回表单标识,如果表单不存在且是已完成工作,那么返回储存表单.", action = V2LookupWorkOrWorkCompleted.class)
	@GET
	@Path("v2/lookup/workorworkcompleted/{workOrWorkCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2LookupWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
		ActionResult<V2LookupWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2LookupWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询表单,如果有表单那么返回表单id,如果表单不存在且是已完成工作,那么返回storeFormMobile.", action = V2LookupWorkOrWorkCompletedMobile.class)
	@GET
	@Path("v2/lookup/workorworkcompleted/{workOrWorkCompleted}/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2LookupWorkOrWorkCompletedMobile(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
		ActionResult<V2LookupWorkOrWorkCompletedMobile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2LookupWorkOrWorkCompletedMobile().execute(effectivePerson, workOrWorkCompleted);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询表单,如果有表单那么返回表单标识,如果表单不存在且是已完成工作,那么返回储存表单.", action = V2LookupTaskCompleted.class)
	@GET
	@Path("v2/lookup/taskcompleted/{taskcompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2LookupTaskCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("已办标识") @PathParam("taskcompleted") String workOrWorkCompleted) {
		ActionResult<V2LookupTaskCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2LookupTaskCompleted().execute(effectivePerson, workOrWorkCompleted);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询表单,如果有表单那么返回表单id,如果表单不存在且是已完成工作,那么返回storeFormMobile.", action = V2LookupTaskCompletedMobile.class)
	@GET
	@Path("v2/lookup/taskcompleted/{taskcompleted}/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2LookupTaskCompletedMobile(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("已办标识") @PathParam("taskcompleted") String workOrWorkCompleted) {
		ActionResult<V2LookupTaskCompletedMobile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2LookupTaskCompletedMobile().execute(effectivePerson, workOrWorkCompleted);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据表单标识获取表单.", operationId = OPERATIONID_PREFIX + "V2Get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = V2Get.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据表单标识获取表单.", action = V2Get.class)
	@GET
	@Path("v2/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2Get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("缓存标志") @QueryParam("t") String t) {
		ActionResult<V2Get.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Get().execute(effectivePerson, id, t);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据表单标识获取移动表单.", operationId = OPERATIONID_PREFIX + "V2GetMobile", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = V2GetMobile.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据表单标识获取移动表单.", action = V2GetMobile.class)
	@GET
	@Path("v2/{id}/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2GetMobile(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("缓存标志") @QueryParam("t") String t) {
		ActionResult<V2GetMobile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2GetMobile().execute(effectivePerson, id, t);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
