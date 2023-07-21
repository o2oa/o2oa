package com.x.processplatform.assemble.surface.jaxrs.data;

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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "DataAction", description = "业务数据接口.")
@Path("data")
@JaxrsDescribe("业务数据接口.")
public class DataAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataAction.class);
	private static final String OPERATIONID_PREFIX = "DataAction::";

	@Operation(summary = "根据任务标识获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJob", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识获取业务数据.", action = ActionGetWithJob.class)
	@GET
	@Path("job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJob().execute(effectivePerson, job);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识和一级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJobPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和一级路径获取业务数据.", action = ActionGetWithJobPath0.class)
	@GET
	@Path("job/{job}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath0().execute(effectivePerson, job, path0);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识和二级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJobPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和二级路径获取业务数据.", action = ActionGetWithWorkPath1.class)
	@GET
	@Path("job/{job}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath1().execute(effectivePerson, job, path0, path1);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识和三级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJobPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和三级路径获取业务数据.", action = ActionGetWithJobPath2.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath2().execute(effectivePerson, job, path0, path1, path2);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识和四级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJobPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和四级路径获取业务数据.", action = ActionGetWithJobPath3.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath3().execute(effectivePerson, job, path0, path1, path2, path3);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识和五级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJobPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和五级路径获取业务数据.", action = ActionGetWithJobPath4.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath4().execute(effectivePerson, job, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识和六级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJobPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和六级路径获取业务数据.", action = ActionGetWithJobPath5.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath5().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识和七级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJobPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和七级路径获取业务数据.", action = ActionGetWithJobPath6.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath6().execute(effectivePerson, job, path0, path1, path2, path3, path4, path5,
					path6);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识和八级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithJobPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和八级路径获取业务数据.", action = ActionGetWithJobPath7.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath7().execute(effectivePerson, job, path0, path1, path2, path3, path4, path5,
					path6, path7);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWork", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识获取业务数据.", action = ActionGetWithWork.class)
	@GET
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWork().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和一级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和一级路径获取业务数据.", action = ActionGetWithWorkPath0.class)
	@GET
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和二级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和二级路径获取业务数据.", action = ActionGetWithWorkPath1.class)
	@GET
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和三级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath2.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和四级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和四级路径获取业务数据.", action = ActionGetWithWorkPath3.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和五级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和五级路径获取业务数据.", action = ActionGetWithWorkPath4.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和六级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和六级路径获取业务数据.", action = ActionGetWithWorkPath5.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和七级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和七级路径获取业务数据.", action = ActionGetWithWorkPath6.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4, path5,
					path6);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和八级路径获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和八级路径获取业务数据.", action = ActionGetWithWorkPath7.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4, path5,
					path6, path7);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "从item中获取已完成工作的业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedFromItem", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "从item中获取已完成工作的业务数据.", action = ActionGetWithWorkCompletedFromItem.class)
	@GET
	@Path("workcompleted/{id}/from/item")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedFromItem(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedFromItem().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "从data中获取已完成工作的业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedFromData", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "从data中获取workCompleted的data数据.", action = ActionGetWithWorkCompletedFromData.class)
	@GET
	@Path("workcompleted/{id}/from/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedFromData(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedFromData().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识获取业务数据.", operationId = OPERATIONID_PREFIX + "getWithWorkCompleted", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识获取业务数据.", action = ActionGetWithWorkCompleted.class)
	@GET
	@Path("workcompleted/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompleted().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识和一级路径获取业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedPath0", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和一级路径获取业务数据.", action = ActionGetWithWorkCompletedPath0.class)
	@GET
	@Path("workcompleted/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识和二级路径获取业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedPath1", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和二级路径获取业务数据.", action = ActionGetWithWorkCompletedPath1.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识和三级路径获取业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedPath2", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和三级路径获取业务数据.", action = ActionGetWithWorkCompletedPath2.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识和四级路径获取业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedPath3", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和四级路径获取业务数据.", action = ActionGetWithWorkCompletedPath3.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识和五级路径获取业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedPath4", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和五级路径获取业务数据.", action = ActionGetWithWorkCompletedPath4.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath4().execute(effectivePerson, id, path0, path1, path2, path3,
					path4);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识和六级路径获取业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedPath5", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath5.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath5().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识和七级路径获取业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedPath6", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath6.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath6().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据已完成工作标识和八级路径获取业务数据.", operationId = OPERATIONID_PREFIX
			+ "getWithWorkCompletedPath7", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath7.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath7().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, path7);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJob", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识更新业务数据.", action = ActionUpdateWithJob.class)
	@PUT
	@Path("job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJob.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJob().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobMockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识更新业务数据(mock put to post).", action = ActionUpdateWithJob.class)
	@POST
	@Path("job/{job}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJob.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJob().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和一级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJobPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和一级路径更新业务数据.", action = ActionUpdateWithJobPath0.class)
	@PUT
	@Path("job/{job}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath0().execute(effectivePerson, job, path0, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和一级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobPath0MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和一级路径更新业务数据(mock put to post).", action = ActionUpdateWithJobPath0.class)
	@POST
	@Path("job/{job}/{path0}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath0MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath0().execute(effectivePerson, job, path0, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和二级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJobPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和二级路径更新业务数据.", action = ActionUpdateWithJobPath1.class)
	@PUT
	@Path("job/{job}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath1().execute(effectivePerson, job, path0, path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和二级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobPath1MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和二级路径更新业务数据(mock put to post).", action = ActionUpdateWithJobPath1.class)
	@POST
	@Path("job/{job}/{path0}/{path1}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath1MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath1().execute(effectivePerson, job, path0, path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和三级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJobPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和三级路径更新业务数据.", action = ActionUpdateWithJobPath2.class)
	@PUT
	@Path("job/{job}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath2().execute(effectivePerson, job, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和三级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobPath2MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和三级路径更新业务数据(mock put to post).", action = ActionUpdateWithJobPath2.class)
	@POST
	@Path("job/{job}/{path0}/{path1}/{path2}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath2MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath2().execute(effectivePerson, job, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和四级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJobPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和四级路径更新业务数据.", action = ActionUpdateWithJobPath3.class)
	@PUT
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath3().execute(effectivePerson, job, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和四级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobPath3MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和四级路径更新业务数据(mock put to post).", action = ActionUpdateWithJobPath3.class)
	@POST
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath3MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath3().execute(effectivePerson, job, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和五级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJobPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和五级路径更新业务数据.", action = ActionUpdateWithJobPath4.class)
	@PUT
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath4().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和五级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobPath4MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和五级路径更新业务数据(mock put to post).", action = ActionUpdateWithJobPath4.class)
	@POST
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath4MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath4().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和六级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJobPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和六级路径更新业务数据.", action = ActionUpdateWithJobPath5.class)
	@PUT
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath5().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和六级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobPath5MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和六级路径更新业务数据(mock put to post).", action = ActionUpdateWithJobPath5.class)
	@POST
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath5MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath5().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和七级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJobPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和七级路径更新业务数据.", action = ActionUpdateWithJobPath6.class)
	@PUT
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath6().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和七级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobPath6MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和七级路径更新业务数据(mock put to post).", action = ActionUpdateWithJobPath6.class)
	@POST
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath6MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath6().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和八级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithJobPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和八级路径更新业务数据.", action = ActionUpdateWithJobPath7.class)
	@PUT
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath7().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据任务标识和八级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithJobPath7MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识和八级路径更新业务数据(mock put to post).", action = ActionUpdateWithJobPath7.class)
	@POST
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithJobPath7MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("job标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithJobPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithJobPath7().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWork", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识更新业务数据.", action = ActionUpdateWithWork.class)
	@PUT
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWork().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkMockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识更新业务数据(mock put to post).", action = ActionUpdateWithWork.class)
	@POST
	@Path("work/{id}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWork().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和一级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和一级路径更新业务数据.", action = ActionUpdateWithWorkPath0.class)
	@PUT
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和一级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkPath0MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和一级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkPath0.class)
	@POST
	@Path("work/{id}/{path0}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath0MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和二级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和二级路径更新业务数据.", action = ActionUpdateWithWorkPath1.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和二级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkPath1MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和二级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkPath1.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath1MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和三级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和三级路径更新业务数据.", action = ActionUpdateWithWorkPath2.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和三级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkPath2MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和三级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkPath2.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath2MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和四级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和四级路径更新业务数据.", action = ActionUpdateWithWorkPath3.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和四级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkPath3MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和四级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkPath3.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath3MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和五级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和五级路径更新业务数据.", action = ActionUpdateWithWorkPath4.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和五级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkPath4MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和五级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkPath4.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath4MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和六级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和六级路径更新业务数据.", action = ActionUpdateWithWorkPath5.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和六级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkPath5MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和六级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkPath5.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath5MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和七级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和七级路径更新业务数据.", action = ActionUpdateWithWorkPath6.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和七级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkPath6MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和七级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkPath6.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath6MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和八级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和八级路径更新业务数据.", action = ActionUpdateWithWorkPath7.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和八级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkPath7MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和八级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkPath7.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath7MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识更新业务数据.", operationId = OPERATIONID_PREFIX + "updateWithWorkCompleted", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识更新业务数据.", action = ActionUpdateWithWorkCompleted.class)
	@PUT
	@Path("workcompleted/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompleted().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedMockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识更新业务数据(mock put to post).", action = ActionUpdateWithWorkCompleted.class)
	@POST
	@Path("workcompleted/{id}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompleted().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和一级路径更新业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath0", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和一级路径更新业务数据.", action = ActionUpdateWithWorkCompletedPath0.class)
	@PUT
	@Path("workcompleted/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和一级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath0MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和一级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkCompletedPath0.class)
	@POST
	@Path("workcompleted/{id}/{path0}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath0MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和二级路径更新业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath1", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和二级路径更新业务数据.", action = ActionUpdateWithWorkCompletedPath1.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和二级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath1MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和二级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkCompletedPath1.class)
	@POST
	@Path("workcompleted/{id}/{path0}/{path1}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath1MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和三级路径更新业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath1MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和三级路径更新业务数据.", action = ActionUpdateWithWorkCompletedPath2.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath2().execute(effectivePerson, id, path0, path1, path2,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和三级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath2MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和三级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkCompletedPath2.class)
	@POST
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath2MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath2().execute(effectivePerson, id, path0, path1, path2,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和四级路径更新业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath3", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和四级路径更新业务数据.", action = ActionUpdateWithWorkCompletedPath3.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和四级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath3MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和四级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkCompletedPath3.class)
	@POST
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath3MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和五级路径更新业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath4", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和五级路径更新业务数据.", action = ActionUpdateWithWorkCompletedPath4.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath4().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和五级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath4MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和五级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkCompletedPath4.class)
	@POST
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath4MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath4().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和六级路径更新业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath5", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和六级路径更新业务数据.", action = ActionUpdateWithWorkCompletedPath5.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath5().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和六级路径更新业务数据mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath5MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和六级路径更新业务数据mock put to post).", action = ActionUpdateWithWorkCompletedPath5.class)
	@POST
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath5MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath5().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和七级路径更新业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath1MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和七级路径更新业务数据.", action = ActionUpdateWithWorkCompletedPath6.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath6().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和七级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath6MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和七级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkCompletedPath6.class)
	@POST
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath6MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath6().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和八级路径更新业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath7", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和八级路径更新业务数据.", action = ActionUpdateWithWorkCompletedPath7.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath7().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据已完成工作标识和八级路径更新业务数据(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath7MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据已完成工作标识和八级路径更新业务数据(mock put to post).", action = ActionUpdateWithWorkCompletedPath7.class)
	@POST
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath7MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath7().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWork", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识更新业务数据.", action = ActionCreateWithWork.class)
	@POST
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWork().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和一级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWorkPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath0.class)
	@POST
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和二级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWorkPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath1.class)
	@POST
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和三级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWorkPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath2.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和四级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWorkPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath3.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和五级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWorkPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath4.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和六级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWorkPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath5.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和七级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWorkPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath6.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识和八级路径更新业务数据.", operationId = OPERATIONID_PREFIX + "createWithWorkPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath7.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "根据工作标识删除业务数据.", operationId = OPERATIONID_PREFIX + "deleteWithWork", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识删除业务数据.", action = ActionDeleteWithWork.class)
	@DELETE
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
		ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWork().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkMockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识删除业务数据(mock delete to get).", action = ActionDeleteWithWork.class)
	@GET
	@Path("work/{id}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
		ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWork().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和一级路径删除业务数据.", operationId = OPERATIONID_PREFIX + "deleteWithWorkPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和一级路径删除业务数据.", action = ActionDeleteWithWorkPath0.class)
	@DELETE
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<ActionDeleteWithWorkPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和一级路径删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkPath0MockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和一级路径删除业务数据(mock delete to get).", action = ActionDeleteWithWorkPath0.class)
	@GET
	@Path("work/{id}/{path0}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath0MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<ActionDeleteWithWorkPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和二级路径删除业务数据.", operationId = OPERATIONID_PREFIX + "deleteWithWorkPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和二级路径删除业务数据.", action = ActionDeleteWithWorkPath1.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<ActionDeleteWithWorkPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和二级路径删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkPath1MockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和二级路径删除业务数据(mock put to post).", action = ActionDeleteWithWorkPath1.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath1MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<ActionDeleteWithWorkPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和三级路径删除业务数据.", operationId = OPERATIONID_PREFIX + "deleteWithWorkPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和三级路径删除业务数据.", action = ActionDeleteWithWorkPath2.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<ActionDeleteWithWorkPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和三级路径删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkPath2MockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和三级路径删除业务数据(mock put to post).", action = ActionDeleteWithWorkPath2.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath2MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<ActionDeleteWithWorkPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和四级路径删除业务数据.", operationId = OPERATIONID_PREFIX + "deleteWithWorkPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和四级路径删除业务数据.", action = ActionDeleteWithWorkPath3.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<ActionDeleteWithWorkPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和四级路径删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkPath3MockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和四级路径删除业务数据(mock put to post).", action = ActionDeleteWithWorkPath3.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath3MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<ActionDeleteWithWorkPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和五级路径删除业务数据.", operationId = OPERATIONID_PREFIX + "deleteWithWorkPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和五级路径删除业务数据.", action = ActionDeleteWithWorkPath4.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<ActionDeleteWithWorkPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和五级路径删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkPath4MockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和五级路径删除业务数据(mock delete to get).", action = ActionDeleteWithWorkPath4.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath4MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<ActionDeleteWithWorkPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和六级路径删除业务数据.", operationId = OPERATIONID_PREFIX
			+ "updateWithWorkCompletedPath1MockPutToPost", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和六级路径删除业务数据.", action = ActionDeleteWithWorkPath5.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<ActionDeleteWithWorkPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和六级路径删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkPath5MockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和六级路径删除业务数据(mock delete to get).", action = ActionDeleteWithWorkPath5.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath5MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<ActionDeleteWithWorkPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和七级路径删除业务数据.", operationId = OPERATIONID_PREFIX + "deleteWithWorkPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和七级路径删除业务数据.", action = ActionDeleteWithWorkPath6.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<ActionDeleteWithWorkPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和七级路径删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkPath6MockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和七级路径删除业务数据(mock delete to get).", action = ActionDeleteWithWorkPath6.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath6MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<ActionDeleteWithWorkPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和八级路径删除业务数据.", operationId = OPERATIONID_PREFIX + "deleteWithWorkPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和八级路径删除业务数据.", action = ActionDeleteWithWorkPath7.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<ActionDeleteWithWorkPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据工作标识和八级路径删除业务数据(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteWithWorkPath7MockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据工作标识和八级路径删除业务数据(mock delete to get).", action = ActionDeleteWithWorkPath7.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath7MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<ActionDeleteWithWorkPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据任务标识获取指定路径的部分业务数据.", operationId = OPERATIONID_PREFIX + "fetchWithJob", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "根据任务标识获取指定路径的部分业务数据.", action = ActionFetchWithJob.class)
	@POST
	@Path("fetch/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void fetchWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job, JsonElement jsonElement) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFetchWithJob().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
