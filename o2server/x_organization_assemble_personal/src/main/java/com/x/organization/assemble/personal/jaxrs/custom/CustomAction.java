package com.x.organization.assemble.personal.jaxrs.custom;

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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CustomAction", description = "个性化数据接口.")
@Path("custom")
@JaxrsDescribe("个性化数据接口.")
public class CustomAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAction.class);

	private static final String OPERATIONID_PREFIX = "CustomAction::";

	@Operation(summary = "根据当前的访问用户获取Custom数据.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = String.class)) }) })
	@JaxrsMethodDescribe(value = "根据当前的访问用户获取Custom数据.", action = ActionGet.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("名称") @PathParam("name") String name) {
		ActionResult<String> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, name);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取指定用户的Custom数据.", operationId = OPERATIONID_PREFIX + "managerGet", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = String.class)) }) })
	@JaxrsMethodDescribe(value = "获取指定用户的Custom数据.", action = ActionManagerGet.class)
	@GET
	@Path("manager/person/{person}/name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("用户") @PathParam("person") String person,
			@JaxrsParameterDescribe("名称") @PathParam("name") String name) {
		ActionResult<String> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerGet().execute(effectivePerson, person, name);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "获取指定用户的Custom数据.", operationId = OPERATIONID_PREFIX + "update", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionEdit.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = String.class)) }))
	@JaxrsMethodDescribe(value = "更新指定名称的Custom.", action = ActionEdit.class)
	@PUT
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("名称") @PathParam("name") String name, String wi) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, name, wi);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "更新指定名称的Custom数据(Mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateMockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionEdit.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = String.class)) }))
	@JaxrsMethodDescribe(value = "更新指定名称的Custom数据(Mock put to post).", action = ActionEdit.class)
	@POST
	@Path("{name}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("名称") @PathParam("name") String name, String wi) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, name, wi);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "管理员更新指定用户指定名称的Custom数据.", operationId = OPERATIONID_PREFIX + "managerUpdate", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManagerEdit.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = String.class)) }))
	@JaxrsMethodDescribe(value = "管理员更新指定用户指定名称的Custom数据.", action = ActionManagerEdit.class)
	@PUT
	@Path("manager/person/{person}/name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("用户") @PathParam("person") String person,
			@JaxrsParameterDescribe("定义名称") @PathParam("name") String name, String wi) {
		ActionResult<ActionManagerEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerEdit().execute(effectivePerson, person, name, wi);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "管理员更新指定用户指定名称的Custom数据(Mock put to post).", operationId = OPERATIONID_PREFIX
			+ "managerUpdateMockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManagerEdit.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = String.class)) }))
	@JaxrsMethodDescribe(value = "管理员更新指定用户指定名称的Custom数据(Mock put to post).", action = ActionManagerEdit.class)
	@POST
	@Path("manager/person/{person}/name/{name}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerUpdateMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("用户") @PathParam("person") String person,
			@JaxrsParameterDescribe("定义名称") @PathParam("name") String name, String wi) {
		ActionResult<ActionManagerEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerEdit().execute(effectivePerson, person, name, wi);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "删除指定名称的Custom数据.", operationId = OPERATIONID_PREFIX + "delete", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDelete.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "删除指定名称的Custom数据.", action = ActionDelete.class)
	@DELETE
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("名称") @PathParam("name") String name) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, name);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "删除指定名称的Custom数据(Mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteMockDeleteToGet", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDelete.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "删除指定名称的Custom数据(Mock delete to get).", action = ActionDelete.class)
	@GET
	@Path("{name}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("名称") @PathParam("name") String name) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, name);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
