package com.x.calendar.assemble.control.jaxrs.event;

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

@Path("event")
@JaxrsDescribe("日历事件信息管理服务")
public class Calendar_EventAction extends StandardJaxrsAction {

	// private StandardJaxrsActionProxy proxy = new
	// StandardJaxrsActionProxy(ThisApplication.context());
	private static Logger logger = LoggerFactory.getLogger(Calendar_EventAction.class);

	@JaxrsMethodDescribe(value = "根据ID获取日历事件信息", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历事件信息ID") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGet().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "根据ID获取日历事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取日历事件信息的RFC内容", action = ActionGetRFCContent.class)
	@GET
	@Path("rfc/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getRFC(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历事件信息ID") @PathParam("id") String id) {
		ActionResult<ActionGetRFCContent.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGetRFCContent().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "根据ID获取日历事件信息的RFC内容时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据事件ID删除指定的日历事件信息", action = ActionDestroySingleEventWithId.class)
	@DELETE
	@Path("single/{eventId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destorySingleEvent(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历事件信息ID") @PathParam("eventId") String eventId) {
		ActionResult<ActionDestroySingleEventWithId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionDestroySingleEventWithId().execute(request, effectivePerson, eventId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "根据ID删除日历事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据指定事件ID删除所有的重复日历事件信息", action = ActionDestroyWithMaster.class)
	@DELETE
	@Path("all/{eventId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destoryAllEventsWithRepeatMaster(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历事件信息ID") @PathParam("eventId") String eventId) {
		ActionResult<ActionDestroyWithMaster.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionDestroyWithMaster().execute(request, effectivePerson, eventId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "根据指定事件ID删除所有的重复日历事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除重复日程事件中指定事件开始后续所有的日程事件信息", action = ActionDestroyAfterEventId.class)
	@DELETE
	@Path("after/{eventId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destoryAfterEvent(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历事件信息ID") @PathParam("eventId") String eventId) {
		ActionResult<ActionDestroyAfterEventId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionDestroyAfterEventId().execute(request, effectivePerson, eventId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "删除重复日程事件中指定事件开始后续所有的日程事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据条件查询日历事件信息列表", action = ActionListWithCondition.class)
	@PUT
	@Path("list/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("查询条件") JsonElement jsonElement) {
		ActionResult<ActionListWithCondition.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListWithCondition().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "根据条件查询日历事件信息列表！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据条件查询日历事件信息列表(返回简单列表)", action = ActionSimpleListWithCondition.class)
	@PUT
	@Path("list/filter/sample")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilterSample(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("查询条件") JsonElement jsonElement) {
		ActionResult<List<ActionSimpleListWithCondition.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSimpleListWithCondition().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "根据条件查询日历事件信息列表！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建一个新的日历事件信息", action = ActionCreate.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					   @JaxrsParameterDescribe("日历事件信息") JsonElement jsonElement) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionCreate().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "新建或者更新日历事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员创建一个新的日历事件信息", action = ActionManageCreate.class)
	@Path("manage")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					   @JaxrsParameterDescribe("日历事件信息") JsonElement jsonElement) {
		ActionResult<ActionManageCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionManageCreate().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "新建或者更新日历事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新事件所有的重复日程事件", action = ActionUpdateWithMaster.class)
	@Path("update/all/{eventId}")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateAllEventsWithRepeatMaster(
			@JaxrsParameterDescribe("日历事件信息ID") @PathParam("eventId") String eventId,
			@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历事件信息") JsonElement jsonElement) {
		ActionResult<ActionUpdateWithMaster.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionUpdateWithMaster().execute(request, effectivePerson, eventId, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "更新日历事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新重复日程事件中指定事件开始后续所有的日程事件信息", action = ActionUpdateAfterEventId.class)
	@Path("update/after/{eventId}")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateAfterEvent(@JaxrsParameterDescribe("日历事件信息ID") @PathParam("eventId") String eventId,
			@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历事件信息") JsonElement jsonElement) {
		ActionResult<ActionUpdateAfterEventId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionUpdateAfterEventId().execute(request, effectivePerson, eventId, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "更新日历事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据事件ID更新单个日程事件信息", action = ActionUpdateSingleEventWithId.class)
	@Path("update/single/{eventId}")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateSingleEvent(@JaxrsParameterDescribe("日历事件信息ID") @PathParam("eventId") String eventId,
			@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历事件信息") JsonElement jsonElement) {
		ActionResult<ActionUpdateSingleEventWithId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionUpdateSingleEventWithId().execute(request, effectivePerson, eventId, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "更新日历事件信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}