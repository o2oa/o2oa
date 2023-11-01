package com.x.processplatform.assemble.surface.jaxrs.task;

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
import com.x.base.core.project.annotation.DescribeScope;
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

@Tag(name = "TaskAction", description = "待办接口.")
@Path("task")
@JaxrsDescribe(value = "待办接口.", scope = DescribeScope.commonly)
public class TaskAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskAction.class);

	private static final String OPERATIONID_PREFIX = "TaskAction::";

	@Operation(summary = "根据指定的job列示待办.", operationId = OPERATIONID_PREFIX + "listWithJob", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJob.Wo.class)))) })
	@JaxrsMethodDescribe(value = "根据指定的job列示待办.", action = ActionListWithJob.class, scope = DescribeScope.commonly)
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

	@Operation(summary = "根据工作获取待办.", operationId = OPERATIONID_PREFIX + "listWithWork", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWork.Wo.class)))) })
	@JaxrsMethodDescribe(value = "根据工作获取待办.", action = ActionListWithWork.class, scope = DescribeScope.commonly)
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

	@Operation(summary = "翻页列示当前用户的待办对象,下一页.", operationId = OPERATIONID_PREFIX + "listNext", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListNext.Wo.class)))) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户的待办对象,下一页.", action = ActionListNext.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
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

	@Operation(summary = "翻页列示当前用户的待办对象,上一页.", operationId = OPERATIONID_PREFIX + "listPrev", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPrev.Wo.class)))) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户的待办对象,上一页.", action = ActionListPrev.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
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

	@Operation(summary = "翻页显示当前用户指定应用的待办,下一页.", operationId = OPERATIONID_PREFIX
			+ "listNextWithApplication", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionListNextWithApplication.Wo.class))) })
	@JaxrsMethodDescribe(value = "翻页显示当前用户指定应用的待办,下一页.", action = ActionListNextWithApplication.class)
	@GET
	@Path("list/{id}/next/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id,
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

	@Operation(summary = "翻页显示当前用户指定应用的待办,上一页.", operationId = OPERATIONID_PREFIX
			+ "listPrevWithApplication", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionListPrevWithApplication.Wo.class))) })
	@JaxrsMethodDescribe(value = "翻页显示当前用户指定应用的待办,上一页.", action = ActionListPrevWithApplication.class)
	@GET
	@Path("list/{id}/prev/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id,
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

	@Operation(summary = "翻页显示当前用户指定流程的待办,下一页.", operationId = OPERATIONID_PREFIX + "listNextWithProcess", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionListNextWithProcess.Wo.class))) })
	@JaxrsMethodDescribe(value = "翻页显示当前用户指定流程的待办,下一页.", action = ActionListNextWithProcess.class)
	@GET
	@Path("list/{id}/next/{count}/process/{processFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
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

	@Operation(summary = "翻页显示当前用户指定流程的待办,下一页.", operationId = OPERATIONID_PREFIX + "listPrevWithProcess", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionListPrevWithProcess.Wo.class))) })
	@JaxrsMethodDescribe(value = "列示指定流程当前用户的待办对象,上一页.", action = ActionListPrevWithProcess.class)
	@GET
	@Path("list/{id}/prev/{count}/process/{processFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
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

	@Operation(summary = "根据标识获取待办内容.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionGet.Wo.class))) })
	@JaxrsMethodDescribe(value = "根据标识获取待办内容.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办标识") @PathParam("id") String id) {
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

	@Operation(summary = "统计当前用户按应用分类待办数量.", operationId = OPERATIONID_PREFIX
			+ "listCountWithApplication", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionListCountWithApplication.Wo.class))) })
	@JaxrsMethodDescribe(value = "统计当前用户按应用分类待办数量.", action = ActionListCountWithApplication.class)
	@GET
	@Path("list/count/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountWithApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<List<ActionListCountWithApplication.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListCountWithApplication().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "统计当前用户指定应用中按流程分类待办数量.", operationId = OPERATIONID_PREFIX
			+ "listCountWithProcess", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionListCountWithProcess.Wo.class))) })
	@JaxrsMethodDescribe(value = "统计当前用户指定应用中按流程分类待办数量.", action = ActionListCountWithProcess.class)
	@GET
	@Path("list/count/application/{applicationFlag}/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountWithProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<ActionListCountWithProcess.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListCountWithProcess().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取可用与过滤的的待办分类值.", operationId = OPERATIONID_PREFIX + "filterAttribute", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionFilterAttribute.Wo.class))) })
	@JaxrsMethodDescribe(value = "获取可用与过滤的的待办分类值.", action = ActionFilterAttribute.class)
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

	@Operation(summary = "根据输入的分类过滤值,获取可用与过滤的的待办分类值.", operationId = OPERATIONID_PREFIX
			+ "filterAttributeFilter", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionFilterAttributeFilter.Wo.class))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionFilterAttributeFilter.Wi.class)) }))
	@JaxrsMethodDescribe(value = "根据输入的分类过滤值,获取可用与过滤的的待办分类值.", action = ActionFilterAttributeFilter.class)
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

	@Operation(summary = "翻页显示当前用户待办,通过输入过滤值进行范围限制,下一页.", operationId = OPERATIONID_PREFIX
			+ "listNextWithFilter", responses = {
					@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListNextFilter.Wo.class)))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionListNextFilter.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页显示当前用户待办,通过输入过滤值进行范围限制,下一页.", action = ActionListNextFilter.class)
	@POST
	@Path("list/{id}/next/{count}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
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

	@Operation(summary = "翻页显示当前用户待办,通过输入过滤值进行范围限制,上一页.", operationId = OPERATIONID_PREFIX
			+ "listNextWithFilter", responses = {
					@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPrevFilter.Wo.class)))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionListPrevFilter.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页显示当前用户待办,通过输入过滤值进行范围限制,上一页.", action = ActionListPrevFilter.class)
	@POST
	@Path("list/{id}/prev/{count}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
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

	@Operation(summary = "分页显示当前用户的待办.", operationId = OPERATIONID_PREFIX + "listMyPaging", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListMyPaging.Wo.class)))) })
	@JaxrsMethodDescribe(value = "分页显示当前用户的待办.", action = ActionListMyPaging.class)
	@GET
	@Path("list/my/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("数量") @PathParam("size") Integer size) {
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

	@Operation(summary = "分页显示根据条件过滤当前用户待办,可选条件:应用,流程,开始时间,结束时间,创建组织,创建年月,活动名称,过期时间,催办时间,是否包含草稿,关键字.", operationId = OPERATIONID_PREFIX
			+ "listMyFilterPaging", responses = {
					@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListMyFilterPaging.Wo.class)))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionListMyFilterPaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "分页显示根据条件过滤当前用户待办,可选条件:应用,流程,开始时间,结束时间,创建组织,创建年月,活动名称,过期时间,催办时间,是否包含草稿,关键字.", action = ActionListMyFilterPaging.class)
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

	@Operation(summary = "保存待办意见并继续流转.返回流转记录..", operationId = OPERATIONID_PREFIX + "processing", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionProcessing.Wo.class))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionProcessing.Wi.class)) }))
	@JaxrsMethodDescribe(value = "保存待办意见并继续流转.返回流转记录.", action = ActionProcessing.class)
	@POST
	@Path("{id}/processing")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void processing(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionProcessing.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionProcessing().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Deprecated(forRemoval = true)
	@JaxrsMethodDescribe(value = "使用神经网络自动进行处理", action = ActionProcessingNeural.class)
	@POST
	@Path("{id}/processing/neural")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void processingNeural(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionProcessingNeural.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionProcessingNeural().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "更新指定待办的选择的路由及意见.", operationId = OPERATIONID_PREFIX + "edit", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionEdit.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "更新指定待办的选择的路由及意见.", action = ActionEdit.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "更新指定待办的选择的路由及意见(MockPutToPost).", operationId = OPERATIONID_PREFIX + "edit", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionEdit.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "更新指定待办的选择的路由及意见(MockPutToPost).", action = ActionEdit.class)
	@POST
	@Path("{id}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void editMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Deprecated(forRemoval = true)
	@JaxrsMethodDescribe(value = "预计下一活动处理状态及处理人.", action = ActionWill.class)
	@GET
	@Path("{id}/will")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void will(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionWill.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionWill().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "取得复合的待办信息,包括:待办对象,工作对象,附件对象,已完成工作对象和工作日志对象.", operationId = OPERATIONID_PREFIX
			+ "getReference", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionReference.Wo.class))) })
	@JaxrsMethodDescribe(value = "取得复合的待办信息,包括:待办对象,工作对象,附件对象,已完成工作对象和工作日志对象.", action = ActionReference.class)
	@GET
	@Path("{id}/reference")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getReference(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
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

	@Operation(summary = "获取指定人员的待办数量,没有权限限制.", operationId = OPERATIONID_PREFIX + "countWithPerson", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionCountWithPerson.Wo.class))) })
	@JaxrsMethodDescribe(value = "获取指定人员的待办数量,没有权限限制.", action = ActionCountWithPerson.class)
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

	@Operation(summary = "获取指定人员,应用,流程在指定范围内的待办数量,没有权限限制.", operationId = OPERATIONID_PREFIX
			+ "countWithFilter", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionCountWithFilter.Wo.class))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionCountWithFilter.Wi.class)) }))
	@JaxrsMethodDescribe(value = "获取指定人员,应用,流程在指定范围内的待办数量,没有权限限制.", action = ActionCountWithFilter.class)
	@POST
	@Path("count/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void countWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionCountWithFilter.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCountWithFilter().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理维护接口:删除待办,需要管理权限.", operationId = OPERATIONID_PREFIX + "manageDelete", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManageDelete.Wo.class))) })
	@JaxrsMethodDescribe(value = "管理维护接口:删除待办,需要管理权限.", action = ActionManageDelete.class)
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

	@Operation(summary = "管理维护接口:删除待办,需要管理权限(MockGetToDelete).", operationId = OPERATIONID_PREFIX
			+ "manageDeleteMockDeleteToGet", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManageDelete.Wo.class))) })
	@JaxrsMethodDescribe(value = "管理维护接口:删除待办,需要管理权限.MockGetToDelete.", action = ActionManageDelete.class)
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

	@Operation(summary = "管理维护接口:待办转已办,需要管理权限.", operationId = OPERATIONID_PREFIX + "manageProcessing", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManageProcessing.Wo.class))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionManageProcessing.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理维护接口:待办转已办,需要管理权限.", action = ActionManageProcessing.class)
	@PUT
	@Path("{id}/processing/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageProcessing(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionManageProcessing.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageProcessing().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理维护接口:待办转已办,需要管理权限(MockPostToPut).", operationId = OPERATIONID_PREFIX
			+ "manageProcessingMockPutToPost", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManageProcessing.Wo.class))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageProcessing.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理维护接口:待办转已办,需要管理权限(MockPostToPut).", action = ActionManageProcessing.class)
	@POST
	@Path("{id}/processing/manage/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageProcessingMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("待办标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionManageProcessing.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageProcessing().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理维护接口:管理修改意见,需要管理权限.", operationId = OPERATIONID_PREFIX + "manageOpinion", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManageOpinion.Wo.class))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionManageOpinion.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理维护接口:管理修改意见,需要管理权限.", action = ActionManageOpinion.class)
	@PUT
	@Path("{id}/opinion/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageOpinion(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionManageOpinion.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageOpinion().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理维护接口:管理修改意见,需要管理权限(MockPostToPut).", operationId = OPERATIONID_PREFIX
			+ "manageOpinion", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManageOpinion.Wo.class))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageOpinion.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理维护接口:管理修改意见,需要管理权限(MockPostToPut).", action = ActionManageOpinion.class)
	@POST
	@Path("{id}/opinion/manage/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageOpinionMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("待办标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionManageOpinion.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageOpinion().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Deprecated(forRemoval = true)
	@JaxrsMethodDescribe(value = "重置待办,将之前的待办转为已办,opinion:办理意见,routeName:选择路由,identityList:新的办理人.", action = ActionManageReset.class)
	@PUT
	@Path("{id}/reset/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageReset(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionManageReset.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageReset().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Deprecated(forRemoval = true)
	@JaxrsMethodDescribe(value = "重置待办,将之前的待办转为已办,opinion:办理意见,routeName:选择路由,identityList:新的办理人.", action = ActionManageReset.class)
	@POST
	@Path("{id}/reset/manage/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageResetMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionManageReset.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageReset().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理维护接口:翻页显示所有待办,下一页.", operationId = OPERATIONID_PREFIX + "manageListNext", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListNext.Wo.class)))) })
	@JaxrsMethodDescribe(value = "管理维护接口:翻页显示所有待办,下一页.", action = ActionManageListNext.class)
	@GET
	@Path("list/{id}/next/{count}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionManageListNext.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListNext().execute(effectivePerson, id, count);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "管理维护接口:翻页显示所有待办,上一页.", operationId = OPERATIONID_PREFIX + "manageListPrev", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListPrev.Wo.class)))) })
	@JaxrsMethodDescribe(value = "管理维护接口:翻页显示所有待办,上一页.", action = ActionManageListPrev.class)
	@GET
	@Path("list/{id}/prev/{count}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionManageListPrev.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListPrev().execute(effectivePerson, id, count);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "管理维护接口:获取指定过滤条件过滤的待办,下一页.", operationId = OPERATIONID_PREFIX
			+ "manageListNextWithFilter", responses = {
					@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListNextFilter.Wo.class)))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageListNextFilter.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理维护接口:获取指定过滤条件过滤的待办,下一页.", action = ActionManageListNextFilter.class)
	@POST
	@Path("list/{id}/next/{count}/filter/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionManageListNextFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListNextFilter().execute(effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理维护接口:获取指定过滤条件过滤的待办,上一页.", operationId = OPERATIONID_PREFIX
			+ "manageListPrevWithFilter", responses = {
					@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListPrevFilter.Wo.class)))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageListPrevFilter.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理维护接口:获取指定过滤条件过滤的待办,上一页.", action = ActionManageListPrevFilter.class)
	@POST
	@Path("list/{id}/prev/{count}/filter/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionManageListPrevFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListPrevFilter().execute(effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理维护接口:翻页显示按条件过滤待办.", operationId = OPERATIONID_PREFIX
			+ "manageListFilterPaging", responses = {
					@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListFilterPaging.Wo.class)))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManageListFilterPaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理维护接口:翻页显示按条件过滤待办.", action = ActionManageListFilterPaging.class)
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

	@Operation(summary = "管理维护接口:待办提醒.", operationId = OPERATIONID_PREFIX + "managePress", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManagePress.Wo.class))) })
	@JaxrsMethodDescribe(value = "管理维护接口:待办提醒.", action = ActionManagePress.class)
	@GET
	@Path("{id}/press/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managePress(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionManagePress.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagePress().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "管理维护接口:列示指定人员的待办.", operationId = OPERATIONID_PREFIX + "manageListWithPerson", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManageListWithPerson.Wo.class))) })
	@JaxrsMethodDescribe(value = "管理维护接口:列示指定人员的待办.", action = ActionManageListWithPerson.class)
	@GET
	@Path("list/person/{person}/exclude/draft/{isExcludeDraft}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("用户") @PathParam("person") String person,
			@JaxrsParameterDescribe("是否排除草稿待办：false(不排除)|true") @PathParam("isExcludeDraft") Boolean isExcludeDraft) {
		ActionResult<List<ActionManageListWithPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListWithPerson().execute(effectivePerson, person, isExcludeDraft);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "管理维护接口:按创建时间查询指定时间段内当前所有待办.", operationId = OPERATIONID_PREFIX
			+ "manageListWithDateHour", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = ActionManageListWithDateHour.Wo.class))) })
	@JaxrsMethodDescribe(value = "管理维护接口:按创建时间查询指定时间段内当前所有待办.", action = ActionManageListWithDateHour.class)
	@GET
	@Path("list/date/{date}/hour/{hour}/exclude/draft/{isExcludeDraft}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListWithDateHour(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("日期（如:2020-09-11）") @PathParam("date") String date,
			@JaxrsParameterDescribe("小时（0-23）") @PathParam("hour") Integer hour,
			@JaxrsParameterDescribe("是否排除草稿待办：false(不排除)|true") @PathParam("isExcludeDraft") Boolean isExcludeDraft) {
		ActionResult<List<ActionManageListWithDateHour.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListWithDateHour().execute(effectivePerson, date, hour, isExcludeDraft);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

//	@JaxrsMethodDescribe(value = "加签.", action = ActionAdd.class)
//	@POST
//	@Path("{id}/add")
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void add(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
//			@JaxrsParameterDescribe("待办标识") @PathParam("id") String id, JsonElement jsonElement) {
//		ActionResult<ActionAdd.Wo> result = new ActionResult<>();
//		EffectivePerson effectivePerson = this.effectivePerson(request);
//		try {
//			result = new ActionAdd().execute(effectivePerson, id, jsonElement);
//		} catch (Exception e) {
//			LOGGER.error(e, effectivePerson, request, jsonElement);
//			result.error(e);
//		}
//		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
//	}

	@Operation(summary = "分页显示当前用户创建工作的待办.", operationId = OPERATIONID_PREFIX + "V2ListCreatePaging", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = V2ListCreatePaging.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2ListCreatePaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "分页显示当前用户创建工作的待办.", action = V2ListCreatePaging.class)
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

	@Operation(summary = "翻页显示当前用户创建工作的待办,下一页.", operationId = OPERATIONID_PREFIX + "V2ListCreateNext", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = V2ListCreateNext.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2ListCreateNext.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页显示当前用户创建工作的待办,下一页.", action = V2ListCreateNext.class)
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

	@Operation(summary = "翻页显示当前用户创建工作的待办,上一页.", operationId = OPERATIONID_PREFIX + "V2ListCreatePrev", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = V2ListCreatePrev.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2ListCreatePrev.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页显示当前用户创建工作的待办,上一页.", action = V2ListCreatePrev.class)
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

	@Operation(summary = "分页显示按条件过滤的当前用户待办.", operationId = OPERATIONID_PREFIX + "V2ListPaging", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = V2ListPaging.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2ListPaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "分页显示按条件过滤的当前用户待办.", action = V2ListPaging.class)
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

	@Operation(summary = "翻页显示按条件过滤的当前用户待办,下一页.", operationId = OPERATIONID_PREFIX + "V2ListNext", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = V2ListNext.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2ListNext.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页显示按条件过滤的当前用户待办,下一页.", action = V2ListNext.class)
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

	@Operation(summary = "翻页显示按条件过滤的当前用户待办,上一页.", operationId = OPERATIONID_PREFIX + "V2ListPrev", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = V2ListPrev.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2ListPrev.Wi.class)) }))
	@JaxrsMethodDescribe(value = "翻页显示按条件过滤的当前用户待办,上一页.", action = V2ListPrev.class)
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

	@Operation(summary = "按条件过滤的当前用户待办.", operationId = OPERATIONID_PREFIX + "V2List", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = V2List.Wo.class)))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2List.Wi.class)) }))
	@JaxrsMethodDescribe(value = "按条件过滤的当前用户待办.", action = V2List.class)
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

	@Operation(summary = "按条件过滤统计待办数量.", operationId = OPERATIONID_PREFIX + "V2Count", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = V2Count.Wo.class))) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = V2Count.Wi.class)) }))
	@JaxrsMethodDescribe(value = "按条件过滤统计待办数量.", action = V2Count.class)
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

	@Operation(summary = "重置待办,将之前的待办转为已办,opinion:办理意见,routeName:选择路由,identityList:新的办理人.", operationId = OPERATIONID_PREFIX
			+ "V2Reset", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = V2Reset.Wo.class))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = V2Reset.Wi.class)) }))
	@JaxrsMethodDescribe(value = "重置待办,将之前的待办转为已办,opinion:办理意见,routeName:选择路由,identityList:新的办理人.", action = V2Reset.class)
	@PUT
	@Path("v2/{id}/reset")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2Reset(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<V2Reset.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Reset().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "重置待办,将之前的待办转为已办,opinion:办理意见,routeName:选择路由,identityList:新的办理人(MockPutToPost).", operationId = OPERATIONID_PREFIX
			+ "V2ResetMockPutToPost", responses = {
					@ApiResponse(content = @Content(schema = @Schema(implementation = V2Reset.Wo.class))) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = V2Reset.Wi.class)) }))
	@JaxrsMethodDescribe(value = "重置待办,将之前的待办转为已办,opinion:办理意见,routeName:选择路由,identityList:新的办理人(MockPutToPost)", action = V2Reset.class)
	@POST
	@Path("v2/{id}/reset/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void V2ResetMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<V2Reset.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Reset().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

//	@Operation(summary = "在指定待办位置新增处理人.", operationId = OPERATIONID_PREFIX + "V2Add", responses = {
//			@ApiResponse(content = @Content(schema = @Schema(implementation = V2Add.Wo.class))) })
//	@JaxrsMethodDescribe(value = "在指定待办位置新增处理人.", action = V2Add.class)
//	@POST
//	@Path("v2/{id}/add")
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void V2Add(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
//			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
//		ActionResult<V2Add.Wo> result = new ActionResult<>();
//		EffectivePerson effectivePerson = this.effectivePerson(request);
//		try {
//			result = new V2Add().execute(effectivePerson, id, jsonElement);
//		} catch (Exception e) {
//			LOGGER.error(e, effectivePerson, request, jsonElement);
//			result.error(e);
//		}
//		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
//	}

	@Operation(summary = "挂起待办,暂停待办处理计时.", operationId = OPERATIONID_PREFIX + "V2Pause", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = V2Pause.Wo.class))) })
	@JaxrsMethodDescribe(value = "挂起待办,暂停待办处理计时.", action = V2Pause.class)
	@GET
	@Path("v2/{id}/pause")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2Pause(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<V2Pause.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Pause().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "取消挂起待办,恢复待办处理计时.", operationId = OPERATIONID_PREFIX + "V2Resume", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = V2Resume.Wo.class))) })
	@JaxrsMethodDescribe(value = "取消挂起待办,恢复待办处理计时.", action = V2Resume.class)
	@GET
	@Path("v2/{id}/resume")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2Resume(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<V2Resume.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Resume().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "处理待办并流转.", action = V2TriggerProcessing.class)
	@GET
	@Path("v2/{id}/trigger/processing")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2TriggerProcessing(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<V2TriggerProcessing.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2TriggerProcessing().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "加签.", action = V3Add.class)
	@POST
	@Path("v3/{id}/add")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v3Add(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<V3Add.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V3Add().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
