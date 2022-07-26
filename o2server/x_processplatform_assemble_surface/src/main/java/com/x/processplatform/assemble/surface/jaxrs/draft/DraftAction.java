package com.x.processplatform.assemble.surface.jaxrs.draft;

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

@Tag(name = "DraftAction", description = "草稿接口.")
@Path("draft")
@JaxrsDescribe("草稿接口.")
public class DraftAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(DraftAction.class);
	private static final String OPERATIONID_PREFIX = "DraftAction::";

	@Operation(summary = "指定流程标识,创建草稿.", operationId = OPERATIONID_PREFIX + "draw", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDraw.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionDraw.Wi.class)) }))
	@JaxrsMethodDescribe(value = "指定流程标识,创建草稿.", action = ActionDraw.class)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("process/{processFlag}")
	@POST
	public void draw(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程标识") @PathParam("processFlag") String processFlag, JsonElement jsonElement) {
		ActionResult<ActionDraw.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDraw().execute(effectivePerson, processFlag, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "指定草稿标识,获取草稿内容.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "指定草稿标识,获取草稿内容.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("草稿标识") @PathParam("id") String id) {
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

	@Operation(summary = "指定草稿标识,删除草稿.", operationId = OPERATIONID_PREFIX + "delete", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDelete.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "指定草稿标识,删除草稿.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("草稿标识") @PathParam("id") String id) {
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

	@Operation(summary = "指定草稿标识,删除草稿(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteMockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDelete.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "指定草稿标识,删除草稿(mock delete to get).", action = ActionDelete.class)
	@GET
	@Path("{id}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("草稿标识") @PathParam("id") String id) {
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

	@Operation(summary = "保存草稿内容.", operationId = OPERATIONID_PREFIX + "save", responses = { @ApiResponse(content = {
			@Content(schema = @Schema(implementation = ActionSave.Wo.class)) }) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionSave.Wi.class)) }))
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@JaxrsMethodDescribe(value = "保存草稿内容.", action = ActionSave.class)
	@PUT
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSave().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "保存草稿内容(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "saveMockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionSave.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionSave.Wi.class)) }))
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@JaxrsMethodDescribe(value = "M保存草稿内容(mock put to post).", action = ActionSave.class)
	@POST
	@Path("mockputtopost")
	public void saveMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSave().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "分页列示当前用户创建的草稿.", operationId = OPERATIONID_PREFIX + "listMyPaging", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListMyPaging.Wo.class))) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionListMyPaging.Wi.class)) }))
	@JaxrsMethodDescribe(value = "分页列示当前用户创建的草稿.", action = ActionListMyPaging.class)
	@POST
	@Path("list/my/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<ActionListMyPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListMyPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "翻页列示当前用户创建的草稿,下一页.", operationId = OPERATIONID_PREFIX + "listNext", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListNext.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户创建的草稿,下一页.", action = ActionListNext.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("草稿标识") @PathParam("id") String id,
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

	@Operation(summary = "翻页列示当前用户创建的草稿,上一页.", operationId = OPERATIONID_PREFIX + "listNext", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPrev.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "翻页列示当前用户创建的草稿,上一页.", action = ActionListPrev.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("草稿标识") @PathParam("id") String id,
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

	@Operation(summary = "指定草稿标识,将草稿启动成为工作.", operationId = OPERATIONID_PREFIX + "start", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "指定草稿标识,将草稿启动成为工作.", action = ActionStart.class)
	@GET
	@Path("{id}/start")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void start(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("草稿标识") @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionStart().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
