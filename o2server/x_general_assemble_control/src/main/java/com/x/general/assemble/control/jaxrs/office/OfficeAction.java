package com.x.general.assemble.control.jaxrs.office;

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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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

@Tag(name = "OfficeAction", description = "office文件转换.")
@Path("office")
@JaxrsDescribe("office文件转换.")
public class OfficeAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(OfficeAction.class);
	private static final String OPERATIONID_PREFIX = "OfficeAction::";

	@Operation(summary = "word转换html.", operationId = OPERATIONID_PREFIX + "input", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionToHtml.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "word转换html.", action = ActionToHtml.class)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void input(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("office文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionToHtml.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionToHtml().execute(effectivePerson, bytes, disposition);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "html转换word.", operationId = OPERATIONID_PREFIX + "htmlToWord", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionHtmlToWord.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "html转换word.", action = ActionHtmlToWord.class)
	@POST
	@Path("html/to/word")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void htmlToWord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionHtmlToWord.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionHtmlToWord().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "html转换成word结果流文件.", operationId = OPERATIONID_PREFIX + "htmlToWordResult", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionHtmlToWordResult.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "html转换成word结果流文件.", action = ActionHtmlToWordResult.class)
	@GET
	@Path("html/to/word/result/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void htmlToWordResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
		ActionResult<ActionHtmlToWordResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionHtmlToWordResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}