package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.x.base.core.project.bean.NameValueCountPair;
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

@Tag(name = "ReadCompletedAction", description = "已阅接口.")
@Path("readcompleted")
@JaxrsDescribe("已阅接口.")
public class ReadCompletedAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReadCompletedAction.class);
	private static final String OPERATIONID_PREFIX = "ReadCompletedAction::";

	@Operation(summary = "根据任务标识获取已阅.", operationId = OPERATIONID_PREFIX + "listWithJob", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJob.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据任务标识获取已阅.", action = ActionListWithJob.class)
	@GET
	@Path("list/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("任务标识") @PathParam("job") String job) {
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

	@Operation(summary = "根据工作标识获取已阅.", operationId = OPERATIONID_PREFIX + "listWithWork", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWork.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据工作标识获取已阅.", action = ActionListWithWork.class)
	@GET
	@Path("list/work/{work}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("work") String work) {
		ActionResult<List<ActionListWithWork.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWork().execute(effectivePerson, work);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据标识获取已阅内容.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据标识获取已阅内容.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id) {
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

	@Operation(summary = "根据工作标识或完成工作标识获取已阅.", operationId = OPERATIONID_PREFIX
			+ "listWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkOrWorkCompleted.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据工作标识或完成工作标识获取已阅.", action = ActionListWithWorkOrWorkCompleted.class)
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

	@Operation(summary = "翻页列示当前用户的已阅,下一页.", operationId = OPERATIONID_PREFIX + "listNext", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListNext.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户的已阅,下一页.", action = ActionListNext.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListNext.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNext().execute(effectivePerson, id, count);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "翻页列示当前用户的已阅,上一页.", operationId = OPERATIONID_PREFIX + "listPrev", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPrev.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户的已阅,上一页.", action = ActionListPrev.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListPrev.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrev().execute(effectivePerson, id, count);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "翻页列示当前用户指定应用标识的已阅,下一页.", operationId = OPERATIONID_PREFIX
			+ "listNextWithApplication", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListNextWithApplication.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户指定应用标识的已阅,下一页.", action = ActionListNextWithApplication.class)
	@GET
	@Path("list/{id}/next/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已阅标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<ActionListNextWithApplication.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNextWithApplication().execute(effectivePerson, id, count, applicationFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "翻页列示当前用户指定应用标识的已阅,上一页.", operationId = OPERATIONID_PREFIX
			+ "listPrevWithApplication", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPrevWithApplication.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户指定应用标识的已阅,上一页.", action = ActionListPrevWithApplication.class)
	@GET
	@Path("list/{id}/prev/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已阅标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<ActionListPrevWithApplication.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrevWithApplication().execute(effectivePerson, id, count, applicationFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "翻页列示当前用户指定流程标识的已阅,下一页.", operationId = OPERATIONID_PREFIX
			+ "listNextWithProcess", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListNextWithProcess.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户指定流程标识的已阅,下一页.", action = ActionListNextWithProcess.class)
	@GET
	@Path("list/{id}/next/{count}/process/{processFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count,
			@JaxrsParameterDescribe("流程标识") @PathParam("processFlag") String processFlag) {
		ActionResult<List<ActionListNextWithProcess.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNextWithProcess().execute(effectivePerson, id, count, processFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "翻页列示当前用户指定流程标识的已阅,上一页.", operationId = OPERATIONID_PREFIX
			+ "listPrevWithProcess", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPrevWithProcess.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户指定流程标识的已阅,上一页.", action = ActionListPrevWithProcess.class)
	@GET
	@Path("list/{id}/prev/{count}/process/{processFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count,
			@JaxrsParameterDescribe("流程标识") @PathParam("processFlag") String processFlag) {
		ActionResult<List<ActionListPrevWithProcess.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrevWithProcess().execute(effectivePerson, id, count, processFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "统计当前用户按应用分类的已阅.", operationId = OPERATIONID_PREFIX + "listCountWithApplication", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = NameValueCountPair.class))) }) })
	@JaxrsMethodDescribe(value = "统计当前用户按应用分类的已阅.", action = ActionListCountWithApplication.class)
	@GET
	@Path("list/count/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountWithApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListCountWithApplication().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "统计当前用户指定应用标识按流程分类的已阅.", operationId = OPERATIONID_PREFIX
			+ "listCountWithProcess", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = NameValueCountPair.class))) }) })
	@JaxrsMethodDescribe(value = "统计当前用户指定应用标识按流程分类的已阅.", action = ActionListCountWithProcess.class)
	@GET
	@Path("list/count/application/{applicationFlag}/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountWithProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListCountWithProcess().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取可用与过滤的分类值.", operationId = OPERATIONID_PREFIX + "filterAttribute", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionFilterAttribute.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "获取可用与过滤的分类值.", action = ActionFilterAttribute.class)
	@GET
	@Path("filter/attribute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void filterAttribute(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionFilterAttribute.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFilterAttribute().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取可用与过滤的分类值,通过输入值进行范围限制.", operationId = OPERATIONID_PREFIX
			+ "filterAttributeFilter", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionFilterAttributeFilter.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "获取可用与过滤的分类值,通过输入值进行范围限制.", action = ActionFilterAttributeFilter.class)
	@POST
	@Path("filter/attribute/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void filterAttributeFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionFilterAttributeFilter.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFilterAttributeFilter().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "翻页列示按过滤条件过滤的已阅,下一页.", operationId = OPERATIONID_PREFIX + "listNextWithFilter", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListNextFilter.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示按过滤条件过滤的已阅,下一页.", action = ActionListNextFilter.class)
	@POST
	@Path("list/{id}/next/{count}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListNextFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNextFilter().execute(effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "翻页列示按过滤条件过滤的已阅,上一页.", operationId = OPERATIONID_PREFIX + "listPrevWithFilter", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPrevFilter.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示按过滤条件过滤的已阅,上一页.", action = ActionListPrevFilter.class)
	@POST
	@Path("list/{id}/prev/{count}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListPrevFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrevFilter().execute(effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "列示指定已阅后续的所有工作和已完成工作.", operationId = OPERATIONID_PREFIX + "listPrevWithFilter", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionReference.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "列示指定已阅后续的所有工作和已完成工作.", action = ActionReference.class)
	@GET
	@Path("{id}/reference")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getReference(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id) {
		ActionResult<ActionReference.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReference().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取指定人员的已办数量,没有权限限制.", operationId = OPERATIONID_PREFIX + "countWithPerson", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCountWithPerson.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "获取指定人员的已办数量,没有权限限制.", action = ActionCountWithPerson.class)
	@GET
	@Path("count/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void countWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("个人标识") @PathParam("credential") String credential) {
		ActionResult<ActionCountWithPerson.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCountWithPerson().execute(effectivePerson, credential);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已阅标识删除已阅,需要管理权限.", operationId = OPERATIONID_PREFIX + "manageDelete", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageDelete.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据已阅标识删除已阅,需要管理权限.", action = ActionManageDelete.class)
	@DELETE
	@Path("{id}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id) {
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

	@Operation(summary = "根据已阅标识删除已阅,需要管理权限(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "manageDeleteMockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManageDelete.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据已阅标识删除已阅,需要管理权限(mock delete to get).", action = ActionManageDelete.class)
	@GET
	@Path("{id}/manage/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageDeleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已阅标识") @PathParam("id") String id) {
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

	@Operation(summary = "根据已阅标识修改意见,需要管理权限.", operationId = OPERATIONID_PREFIX + "manageUpdate", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManageOpinion.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageOpinion.Wi.class)) }))
	@JaxrsMethodDescribe(value = "根据已阅标识修改意见,需要管理权限.", action = ActionManageOpinion.class)
	@POST
	@Path("{id}/opinion/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已阅标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionManageOpinion.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageOpinion().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "分页列示当前用户的已阅.", operationId = OPERATIONID_PREFIX + "listMyPaging", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListMyPaging.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "分页列示当前用户的已阅.", action = ActionListMyPaging.class)
	@GET
	@Path("list/my/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size) {
		ActionResult<List<ActionListMyPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListMyPaging().execute(effectivePerson, page, size);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "分页列示按过滤条件过滤当前用户的已阅.", operationId = OPERATIONID_PREFIX + "listMyFilterPaging", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListMyFilterPaging.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionListMyFilterPaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "分页列示按过滤条件过滤当前用户的已阅.", action = ActionListMyFilterPaging.class)
	@POST
	@Path("list/my/filter/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyFilterPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<ActionListMyFilterPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListMyFilterPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "分页列示按过滤条件过滤的已阅,需要管理权限.", operationId = OPERATIONID_PREFIX
			+ "manageListFilterPaging", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListFilterPaging.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageListFilterPaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "分页列示按过滤条件过滤的已阅,需要管理权限.", action = ActionManageListFilterPaging.class)
	@POST
	@Path("list/filter/{page}/size/{size}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListFilterPaging(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<ActionManageListFilterPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListFilterPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "分页列示当前用户创建工作的已阅.", operationId = OPERATIONID_PREFIX + "V2ListCreatePaging", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = V2ListCreatePaging.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = V2ListCreatePaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "分页列示当前用户创建工作的已阅.", action = V2ListCreatePaging.class)
	@POST
	@Path("v2/list/create/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2ListCreatePaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<V2ListCreatePaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2ListCreatePaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "翻页列示当前用户创建的工作的已阅,下一页.", operationId = OPERATIONID_PREFIX + "V2ListCreateNext", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = V2ListCreateNext.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = V2ListCreateNext.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页列示当前用户创建的工作的已阅,下一页.", action = V2ListCreateNext.class)
	@POST
	@Path("v2/list/create/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2ListCreateNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<V2ListCreateNext.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2ListCreateNext().execute(effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "翻页列示当前用户创建的工作的已阅,上一页.", operationId = OPERATIONID_PREFIX + "V2ListCreatePrev", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = V2ListCreatePrev.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = V2ListCreatePrev.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页列示当前用户创建的工作的已阅,上一页.", action = V2ListCreatePrev.class)
	@POST
	@Path("v2/list/create/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2ListCreatePrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<V2ListCreatePrev.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2ListCreatePrev().execute(effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "分页列示当前用户的已阅.", operationId = OPERATIONID_PREFIX + "V2ListPaging", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = V2ListPaging.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = V2ListPaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页列示当前用户的已阅.", action = V2ListPaging.class)
	@POST
	@Path("v2/list/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2ListPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<V2ListPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2ListPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "翻页列示当前用户的已阅,下一页.", operationId = OPERATIONID_PREFIX + "V2ListNext", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = V2ListNext.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = V2ListNext.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页列示当前用户的已阅,下一页.", action = V2ListNext.class)
	@POST
	@Path("v2/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2ListNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<V2ListNext.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2ListNext().execute(effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "翻页列示当前用户的已阅,上一页.", operationId = OPERATIONID_PREFIX + "V2ListPrev", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = V2ListPrev.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = V2ListPrev.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页列示当前用户的已阅,上一页.", action = V2ListPrev.class)
	@POST
	@Path("v2/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2ListPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<V2ListPrev.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2ListPrev().execute(effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "列示已阅.", operationId = OPERATIONID_PREFIX + "V2List", responses = { @ApiResponse(content = {
			@Content(array = @ArraySchema(schema = @Schema(implementation = V2List.Wo.class))) }) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2List.Wi.class)) }))
	@JaxrsMethodDescribe(value = "列示已阅.", action = V2List.class)
	@POST
	@Path("v2/list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2List(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<List<V2List.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2List().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "统计已阅数量.", operationId = OPERATIONID_PREFIX + "V2Count", responses = { @ApiResponse(content = {
			@Content(array = @ArraySchema(schema = @Schema(implementation = V2Count.Wo.class))) }) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2Count.Wi.class)) }))
	@JaxrsMethodDescribe(value = "统计已阅数量.", action = V2Count.class)
	@POST
	@Path("v2/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2Count(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<V2Count.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Count().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "按创建时间查询指定时间段内当前所有已阅,需要管理权限.", operationId = OPERATIONID_PREFIX
			+ "manageListWithDate", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListWithDate.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "按创建时间查询指定时间段内当前所有已阅,需要管理权限.", action = ActionManageListWithDate.class)
	@GET
	@Path("list/date/{date}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListWithDate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日期（如:2020-09-11）") @PathParam("date") String date) {
		ActionResult<List<ActionManageListWithDate.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListWithDate().execute(effectivePerson, date);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
