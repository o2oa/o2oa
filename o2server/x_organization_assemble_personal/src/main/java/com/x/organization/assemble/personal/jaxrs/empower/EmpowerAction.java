package com.x.organization.assemble.personal.jaxrs.empower;

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

@Tag(name = "EmpowerAction", description = "授权操作接口.")
@Path("empower")
@JaxrsDescribe("授权操作接口.")
public class EmpowerAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmpowerAction.class);
	private static final String OPERATIONID_PREFIX = "EmpowerAction::";

	@Operation(summary = "获取用户授权对象.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "获取用户授权对象.", action = ActionGet.class)
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

	@Operation(summary = "管理员列示授权对象,下一页.", operationId = OPERATIONID_PREFIX + "listNext", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListNext.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "管理员列示授权对象,下一页.", action = ActionListNext.class)
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

	@Operation(summary = "管理员列示授权对象,上一页.", operationId = OPERATIONID_PREFIX + "listPrev", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPrev.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "管理员列示授权对象,上一页.", action = ActionListPrev.class)
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

	@Operation(summary = "获取当前人员的授权.", operationId = OPERATIONID_PREFIX + "listWithCurrentPerson", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithCurrentPerson.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "获取当前人员的授权.", action = ActionListWithCurrentPerson.class)
	@GET
	@Path("list/currentperson")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentPerson(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<List<ActionListWithCurrentPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPerson().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取当前人员生效的授权.", operationId = OPERATIONID_PREFIX + "listWithCurrentPersonEnable", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithCurrentPersonEnable.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "获取当前人员生效的授权.", action = ActionListWithCurrentPersonEnable.class)
	@GET
	@Path("list/currentperson/enable")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentPersonEnable(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<List<ActionListWithCurrentPersonEnable.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPersonEnable().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "查询指定人员的授权.", operationId = OPERATIONID_PREFIX + "listWithPerson", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPerson.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "查询指定人员的授权.", action = ActionListWithPerson.class)
	@GET
	@Path("list/person/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("人员") @PathParam("flag") String flag) {
		ActionResult<List<ActionListWithPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPerson().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取当前人员的被授权.", operationId = OPERATIONID_PREFIX + "listTo", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListTo.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "获取当前人员的被授权.", action = ActionListTo.class)
	@GET
	@Path("list/to")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionListTo.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTo().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取当前人员的生效的被授权.", operationId = OPERATIONID_PREFIX + "listToEnable", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListToEnable.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "获取当前人员的生效的被授权.", action = ActionListToEnable.class)
	@GET
	@Path("list/to/enable")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listToEnable(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionListToEnable.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListToEnable().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "创建授权.", operationId = OPERATIONID_PREFIX + "create", responses = { @ApiResponse(content = {
			@Content(array = @ArraySchema(schema = @Schema(implementation = ActionCreate.Wo.class))) }) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionCreate.Wi.class)) }))
	@JaxrsMethodDescribe(value = "创建授权.", action = ActionCreate.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreate().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理员创建授权.", operationId = OPERATIONID_PREFIX + "managerCreate", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionManagerCreate.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManagerCreate.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理员创建授权.", action = ActionManagerCreate.class)
	@Path("manager")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionManagerCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerCreate().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理员更新授权.", operationId = OPERATIONID_PREFIX + "managerEdit", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionManagerEdit.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManagerEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理员更新授权.", action = ActionManagerEdit.class)
	@PUT
	@Path("manager/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerEdit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionManagerEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerEdit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "管理员更新授权(Mock put to post).", operationId = OPERATIONID_PREFIX
			+ "managerEditMockPutToPost", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionManagerEdit.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionManagerEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "管理员更新授权(Mock put to post).", action = ActionManagerEdit.class)
	@POST
	@Path("manager/{id}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerEditMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionManagerEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerEdit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "更新授权.", operationId = OPERATIONID_PREFIX + "edit", responses = { @ApiResponse(content = {
			@Content(array = @ArraySchema(schema = @Schema(implementation = ActionEdit.Wo.class))) }) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "更新授权.", action = ActionEdit.class)
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

	@Operation(summary = "更新授权(Mock put to post).", operationId = OPERATIONID_PREFIX
			+ "editMockPutToPost", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionEdit.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "更新授权(Mock put to post).", action = ActionEdit.class)
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

	@Operation(summary = "更新授权(Mock put to post).", operationId = OPERATIONID_PREFIX + "delete", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionEdit.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionEdit.Wi.class)) }))
	@JaxrsMethodDescribe(value = "删除授权", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除授权 MockDeleteToGet", action = ActionDelete.class)
	@GET
	@Path("{id}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员删除授权", action = ActionManagerDelete.class)
	@DELETE
	@Path("manager/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionManagerDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员删除授权 MockDeleteToGet", action = ActionManagerDelete.class)
	@GET
	@Path("manager/{id}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerDeleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionManagerDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "启用授权", action = ActionEnable.class)
	@GET
	@Path("{id}/enable")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void enable(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionEnable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEnable().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "禁用授权", action = ActionDisable.class)
	@GET
	@Path("{id}/disable")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void disable(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionDisable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDisable().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员根据条件查询授权记录.", action = ActionManagerListPaging.class)
	@POST
	@Path("manager/list/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerlistPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<ActionManagerListPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerListPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

}
