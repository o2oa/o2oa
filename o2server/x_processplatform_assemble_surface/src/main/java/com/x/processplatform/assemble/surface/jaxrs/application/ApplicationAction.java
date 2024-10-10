package com.x.processplatform.assemble.surface.jaxrs.application;

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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AnonymousAction", description = "应用接口.")
@JaxrsDescribe("应用接口.")
@Path("application")
public class ApplicationAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationAction.class);
	private static final String OPERATIONID_PREFIX = "ApplicationAction::";

	@Operation(summary = "获取指定的应用信息,并附带其操作权限.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "获取指定的应用信息,并附带其操作权限.", action = ActionGet.class)
	@GET
	@Path("{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("flag") String flag) {
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

	@Operation(summary = "获取可见的应用,同时判断应用下有启动的流程.", operationId = OPERATIONID_PREFIX + "listWithPerson", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPerson.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "获取可见的应用,同时判断应用下有启动的流程.", action = ActionListWithPerson.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionListWithPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPerson().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取指定范围内的可见的应用,如果不指定则所有,同时判断应用下有启动的流程.", action = ActionListRangeWithPerson.class)
	@POST
	@Path("list/range")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listRangeWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<List<ActionListRangeWithPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListRangeWithPerson().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取可见的应用,同时判断应用下有启动的流程,并进行流程的名称匹配.", operationId = OPERATIONID_PREFIX
			+ "listWithPersonLike", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPersonLike.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "获取可见的应用,同时判断应用下有启动的流程,并进行流程的名称匹配.", action = ActionListWithPersonLike.class)
	@GET
	@Path("list/key/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPersonLike(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("关键字") @PathParam("key") String key) {
		ActionResult<List<ActionListWithPersonLike.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonLike().execute(effectivePerson, key);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据当前用户所有可见的Application,并绑定其启动的Process.", operationId = OPERATIONID_PREFIX
			+ "listWithPersonComplex", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPersonComplex.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据当前用户所有可见的Application,并绑定其启动的Process.", action = ActionListWithPersonComplex.class)
	@GET
	@Path("list/complex")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPersonComplex(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<List<ActionListWithPersonComplex.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonComplex().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据当前用户所有可见的Application,并绑定其可启动终端的Process.", operationId = OPERATIONID_PREFIX
			+ "listWithPersonAndTerminal", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPersonAndTerminal.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据当前用户所有可见的Application,并绑定其可启动终端的Process.", action = ActionListWithPersonAndTerminal.class)
	@GET
	@Path("list/terminal/{terminal}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPersonAndTerminal(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程启动终端：client(pc端可启动)、mobile(手机端可启动)") @PathParam("terminal") String terminal) {
		ActionResult<List<ActionListWithPersonAndTerminal.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonAndTerminal().execute(effectivePerson, terminal);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据传入用户，获取可见的Application,并绑定其启动的Process.", operationId = OPERATIONID_PREFIX
			+ "manageListWithPersonComplex", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListWithPersonComplex.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "根据传入用户，获取可见的Application,并绑定其启动的Process.", action = ActionManageListWithPersonComplex.class)
	@GET
	@Path("list/complex/manage/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListWithPersonComplex(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("用户") @PathParam("person") String person) {
		ActionResult<List<ActionManageListWithPersonComplex.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListWithPersonComplex().execute(effectivePerson, person);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "仅获取指定Application的Icon,没有权限限制.", operationId = OPERATIONID_PREFIX + "getIcon", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManageListWithPersonComplex.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "仅获取指定Application的Icon,没有权限限制.", action = ActionGetIcon.class)
	@GET
	@Path("{flag}/icon")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getIcon(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("flag") String flag) {
		ActionResult<ActionGetIcon.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetIcon().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "校验当前用户是否是指定应用的管理员.", operationId = OPERATIONID_PREFIX + "isManager", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionIsManager.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "校验当前用户是否是指定应用的管理员.", action = ActionIsManager.class)
	@GET
	@Path("{flag}/is/manager")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isManager(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("flag") String flag) {
		ActionResult<ActionIsManager.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionIsManager().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据应用标识删除流程实例数据.", operationId = OPERATIONID_PREFIX + "deleteWorkOrWorkCompleted", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteWorkOrWorkCompleted.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据应用标识删除流程实例数据", action = ActionDeleteWorkOrWorkCompleted.class)
	@DELETE
	@Path("{flag}/{onlyRemoveNotCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
										  @JaxrsParameterDescribe("应用标识") @PathParam("flag") String flag,
										  @JaxrsParameterDescribe("仅删除流转中Work") @PathParam("onlyRemoveNotCompleted") boolean onlyRemoveNotCompleted) {
		ActionResult<ActionDeleteWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWorkOrWorkCompleted().execute(effectivePerson, flag, onlyRemoveNotCompleted);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
