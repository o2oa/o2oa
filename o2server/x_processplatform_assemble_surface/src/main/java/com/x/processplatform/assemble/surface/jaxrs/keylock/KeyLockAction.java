package com.x.processplatform.assemble.surface.jaxrs.keylock;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "KeyLockAction", description = "工作锁接口.")
@Path("keylock")
@JaxrsDescribe("工作锁接口.")
public class KeyLockAction extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeyLockAction.class);
	private static final String OPERATIONID_PREFIX = "KeyLockAction::";

	@Operation(summary = "使用当前用户身份锁定工作.", operationId = OPERATIONID_PREFIX + "lock", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionLock.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "使用当前用户身份锁定工作.", action = ActionLock.class)
	@PUT
	@Path("lock")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void lock(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionLock.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLock().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "使用当前用户身份锁定工作.", operationId = OPERATIONID_PREFIX + "lock", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionLock.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "Mock Post To Put.", action = ActionLock.class)
	@POST
	@Path("lock/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void lockMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionLock.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLock().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}