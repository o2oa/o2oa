package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.List;

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

@Path("correlation")
@JaxrsDescribe("关联内容.")
public class CorrelationAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationAction.class);

	@JaxrsMethodDescribe(value = "指定流程平台工作或已完成工作创建关联内容.", action = ActionCreateTypeProcessPlatform.class)
	@POST
	@Path("type/processplatform/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("流程平台任务标识") @PathParam("job") String job,
			JsonElement jsonElement) {
		ActionResult<ActionCreateTypeProcessPlatform.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateTypeProcessPlatform().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "指定内容管理文档创建关联内容.", action = ActionCreateTypeCms.class)
	@POST
	@Path("type/cms/document/{document}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("内容管理文档标识") @PathParam("document") String document, JsonElement jsonElement) {
		ActionResult<ActionCreateTypeCms.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateTypeCms().execute(effectivePerson, document, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据id删除多个流程平台关联内容.", action = ActionDeleteTypeProcessPlatform.class)
	@POST
	@Path("delete/type/processplatform/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("流程平台任务标识") @PathParam("job") String job,
			JsonElement jsonElement) {
		ActionResult<ActionDeleteTypeProcessPlatform.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteTypeProcessPlatform().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据id删除多个内容管理关联内容.", action = ActionDeleteTypeCms.class)
	@POST
	@Path("delete/type/cms/document/{document}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程平台任务标识") @PathParam("document") String document, JsonElement jsonElement) {
		ActionResult<ActionDeleteTypeCms.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteTypeCms().execute(effectivePerson, document, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "判断流程平台指定job的任务是否通过被关联具有阅读权限.", action = ActionReadableTypeProcessPlatform.class)
	@POST
	@Path("readable/type/processplatform")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void readableTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionReadableTypeProcessPlatform.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReadableTypeProcessPlatform().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "判断内容平台指定document的任务是否通过被关联具有阅读权限.", action = ActionReadableTypeCms.class)
	@POST
	@Path("readable/type/cms")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void readableTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionReadableTypeCms.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReadableTypeCms().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示流程平台指定job标识的关联内容.", action = ActionListTypeProcessPlatform.class)
	@GET
	@Path("list/type/processplatform/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("任务标识") @PathParam("job") String job) {
		ActionResult<List<ActionListTypeProcessPlatform.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTypeProcessPlatform().execute(effectivePerson, job);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示流程平台指定job标识指定关联内容框标识的关联内容.", action = ActionListTypeProcessPlatformWithSite.class)
	@GET
	@Path("list/type/processplatform/job/{job}/site/{site}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTypeProcessPlatformWithSite(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("任务标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("关联内容框标识") @PathParam("site") String site) {
		ActionResult<List<ActionListTypeProcessPlatformWithSite.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTypeProcessPlatformWithSite().execute(effectivePerson, job, site);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示内容管理指定document标识的关联内容.", action = ActionListTypeCms.class)
	@GET
	@Path("list/type/cms/document/{document}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("内容管理文档标识") @PathParam("document") String document) {
		ActionResult<List<ActionListTypeCms.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTypeCms().execute(effectivePerson, document);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示内容管理指定document标识指定关联内容框标识的关联内容.", action = ActionListTypeCmsWithSite.class)
	@GET
	@Path("list/type/cms/document/{document}/site/{site}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTypeCmsWithSite(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("内容管理文档标识") @PathParam("document") String document,
			@JaxrsParameterDescribe("关联内容框标识") @PathParam("site") String site) {
		ActionResult<List<ActionListTypeCmsWithSite.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTypeCmsWithSite().execute(effectivePerson, document, site);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}