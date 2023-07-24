package com.x.general.assemble.control.jaxrs.ecnet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.assemble.control.jaxrs.qrcode.ActionPostCreate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "EcnetAction", description = "文本纠错.")
@Path("ecnet")
@JaxrsDescribe("文本纠错.")
public class EcnetAction extends StandardJaxrsAction {

	private static final Logger logger = LoggerFactory.getLogger(EcnetAction.class);
	private static final String OPERATIONID_PREFIX = "EcnetAction::";

	@Operation(summary = "文本纠错检查.", operationId = OPERATIONID_PREFIX + "check", responses = { @ApiResponse(content = {
			@Content(schema = @Schema(implementation = ActionCheck.Wo.class)) }) }, requestBody = @RequestBody(content = {
					@Content(schema = @Schema(implementation = ActionCheck.Wi.class)) }))
	@JaxrsMethodDescribe(value = "文本纠错检查.", action = ActionCheck.class)
	@POST
	@Path("check")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void check(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionCheck.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheck().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}