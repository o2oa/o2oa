package com.x.processplatform.service.processing.jaxrs.work;

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

@Path("work")
@JaxrsDescribe("工作")
public class WorkAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkAction.class);

	@JaxrsMethodDescribe(value = "创建一个处于start状态的工作.", action = ActionCreate.class)
	@POST
	@Path("process/{processId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId, JsonElement jsonElement) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreate().execute(effectivePerson, processId, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "赋值创建一个处于start状态的work.", action = ActionAssignCreate.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void assignCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionAssignCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAssignCreate().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "流转一个流程实例.", action = ActionProcessing.class)
	@PUT
	@Path("{id}/processing")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void processing(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionProcessing.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionProcessing().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "流程信号.", action = ActionProcessingSignal.class)
	@GET
	@Path("{id}/series/{series}/activitytoken/{activityToken}/processing/signal")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void processingSignal(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("串号") @PathParam("series") String series,
			@JaxrsParameterDescribe("活动Token") @PathParam("activityToken") String activityToken) {
		ActionResult<ActionProcessingSignal.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionProcessingSignal().execute(effectivePerson, id, series, activityToken);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除单个工作.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
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

//	@JaxrsMethodDescribe(value = "调度.", action = ActionReroute.class)
//	@PUT
//	@Path("{id}/reroute/activity/{activityId}/activitytype/{activityType}")
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void reroute(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
//			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
//			@JaxrsParameterDescribe("活动标识") @PathParam("activityId") String activityId, JsonElement jsonElement) {
//		ActionResult<ActionReroute.Wo> result = new ActionResult<>();
//		EffectivePerson effectivePerson = this.effectivePerson(request);
//		try {
//			result = new ActionReroute().execute(effectivePerson, id, activityId, jsonElement);
//		} catch (Exception e) {
//			LOGGER.error(e, effectivePerson, request, null);
//			result.error(e);
//		}
//		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
//	}

//	@JaxrsMethodDescribe(value = "指定文件增加一个副本.", action = ActionAddSplit.class)
//	@PUT
//	@Path("{id}/add/split")
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void addSplit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
//			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
//		ActionResult<List<ActionAddSplit.Wo>> result = new ActionResult<>();
//		EffectivePerson effectivePerson = this.effectivePerson(request);
//		try {
//			result = new ActionAddSplit().execute(effectivePerson, id, jsonElement);
//		} catch (Exception e) {
//			LOGGER.error(e, effectivePerson, request, null);
//			result.error(e);
//		}
//		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
//	}
//
//	@JaxrsMethodDescribe(value = "回滚指定的工作到指定的workLog.", action = ActionRollback.class)
//	@PUT
//	@Path("{id}/rollback")
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void rollback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
//			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
//		ActionResult<ActionRollback.Wo> result = new ActionResult<>();
//		EffectivePerson effectivePerson = this.effectivePerson(request);
//		try {
//			result = new ActionRollback().execute(effectivePerson, id, jsonElement);
//		} catch (Exception e) {
//			LOGGER.error(e, effectivePerson, request, null);
//			result.error(e);
//		}
//		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
//	}

	@JaxrsMethodDescribe(value = "添加人工环节处理人,不会自动产生,需要processing之后才会执行.", action = ActionManualAppendIdentity.class)
	@PUT
	@Path("{id}/manual/append/identity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manualAppendIdentity(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionManualAppendIdentity.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManualAppendIdentity().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除滞留的草稿工作.", action = ActionDeleteDraft.class)
	@DELETE
	@Path("{id}/draft")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDraft(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
		ActionResult<ActionDeleteDraft.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDraft().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新工作实例.", action = ActionEdit.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取工作实例.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
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

	@JaxrsMethodDescribe(value = "指定Work运行映射.", action = ActionProjection.class)
	@GET
	@Path("{id}/projection")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void projection(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
		ActionResult<ActionProjection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionProjection().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "执行人工活动流转完成事件.", action = ActionManualAfterProcessing.class)
	@POST
	@Path("manual/after/processing")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manualAfterProcessing(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionManualAfterProcessing.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManualAfterProcessing().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建工作序列号.", action = ActionCreateSerial.class)
	@POST
	@Path("process/{processId}/name/{name}/serial")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWorkSerial(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("编号名称") @PathParam("name") String name) {
		ActionResult<ActionCreateSerial.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateSerial().execute(effectivePerson, processId, name);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "V2_调度.", action = V2Reroute.class)
	@PUT
	@Path("v2/{id}/reroute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2Reroute(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<V2Reroute.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Reroute().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "V2_撤回.", action = V2Retract.class)
	@PUT
	@Path("v2/{id}/retract")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2Retract(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<V2Retract.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Retract().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "V2_回滚.", action = V2Rollback.class)
	@PUT
	@Path("v2/{id}/rollback")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2Rollback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<V2Rollback.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Rollback().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "V2_回滚.", action = V2Rollback.class)
	@PUT
	@Path("v2/{id}/add/split")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2AddSplit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<V2AddSplit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2AddSplit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

//	@JaxrsMethodDescribe(value = "V2_添加待办身份矩阵,before(在指定位置前添加),after(在指定位置后添加),top(添加在最前),bottom(添加到最后),extend(与指定位置同一行进行扩展)", action = V2AddManualTaskIdentityMatrix.class)
//	@POST
//	@Path("v2/{id}/add/manual/task/identity/matrix")
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void v2AddManualTaskIdentityMatrix(@Suspended final AsyncResponse asyncResponse,
//			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
//			JsonElement jsonElement) {
//		ActionResult<V2AddManualTaskIdentityMatrix.Wo> result = new ActionResult<>();
//		EffectivePerson effectivePerson = this.effectivePerson(request);
//		try {
//			result = new V2AddManualTaskIdentityMatrix().execute(effectivePerson, id, jsonElement);
//		} catch (Exception e) {
//			LOGGER.error(e, effectivePerson, request, jsonElement);
//			result.error(e);
//		}
//		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
//	}

	@JaxrsMethodDescribe(value = "V2_回退", action = V2GoBack.class)
	@POST
	@Path("v2/{id}/goback")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2GoBack(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<V2GoBack.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2GoBack().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "V2_终止", action = V2Terminate.class)
	@GET
	@Path("v2/{id}/terminate")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2Terminate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
		ActionResult<V2Terminate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Terminate().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
