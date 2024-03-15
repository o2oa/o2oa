package com.x.base.core.project.jaxrs.cache;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.DescribeScope;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
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

@Tag(name = "CacheAction", description = "缓存操作接口.")
@Path("cache")
@JaxrsDescribe(value = "缓存操作接口", scope = DescribeScope.system)
public class CacheAction extends StandardJaxrsAction {

	private static final Logger logger = LoggerFactory.getLogger(CacheAction.class);

	private static final String OPERATIONID_PREFIX = "CacheAction::";

	@Operation(summary = "接收缓存刷新指令.", operationId = OPERATIONID_PREFIX + "receive", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionReceive.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = ActionReceive.Wi.class)) }))
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@JaxrsMethodDescribe(value = "接收缓存刷新指令.", action = ActionReceive.class)
	public void receive(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionReceive.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReceive().execute(effectivePerson, servletContext, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "接收刷新Config配置文件指令.", operationId = OPERATIONID_PREFIX + "configFlush", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionConfigFlush.Wo.class)) }) })
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("config/flush")
	@JaxrsMethodDescribe(value = "接收刷新Config配置文件指令.", action = ActionConfigFlush.class)
	public void configFlush(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request) {
		ActionResult<ActionConfigFlush.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionConfigFlush().execute(effectivePerson, servletContext);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "接收刷新CommonScript指令.", operationId = OPERATIONID_PREFIX + "commonScriptFlush", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCommonScriptFlush.Wo.class)) }) })
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("commonscript/flush")
	@JaxrsMethodDescribe(value = "接收刷新CommonScript指令.", action = ActionCommonScriptFlush.class)
	public void commonScriptFlush(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request) {
		ActionResult<ActionCommonScriptFlush.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCommonScriptFlush().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "显示缓存状态.", operationId = OPERATIONID_PREFIX + "detail", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDetail.Wo.class)) }) })
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("detail")
	@JaxrsMethodDescribe(value = "显示缓存状态.", action = ActionDetail.class)
	public void detail(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request) {
		ActionResult<ActionDetail.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDetail().execute(effectivePerson, servletContext);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}