package com.x.general.assemble.control.jaxrs.qrcode;

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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Tag(name = "QrCodeAction", description = "二维码.")
@Path("qrcode")
@JaxrsDescribe("二维码")
public class QrCodeAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeAction.class);
	private static final String OPERATIONID_PREFIX = "QrCodeAction::";

	@Operation(summary = "GET方法生成二维码图像.", operationId = OPERATIONID_PREFIX + "getCreate", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetCreate.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "GET方法生成二维码图像.", action = ActionGetCreate.class)
	@GET
	@Path("width/{width}/height/{height}/text/{text}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("宽") @PathParam("width") Integer width,
			@JaxrsParameterDescribe("高") @PathParam("height") Integer height,
			@JaxrsParameterDescribe("文本") @PathParam("text") String text) {
		ActionResult<ActionGetCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetCreate().execute(effectivePerson, width, height, text);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "POST方法生成二维码图像.", operationId = OPERATIONID_PREFIX + "postCreate", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionPostCreate.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionPostCreate.Wi.class)) }))
	@JaxrsMethodDescribe(value = "POST方法生成二维码图像,返回base64.", action = ActionPostCreate.class)
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void postCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionPostCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPostCreate().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
