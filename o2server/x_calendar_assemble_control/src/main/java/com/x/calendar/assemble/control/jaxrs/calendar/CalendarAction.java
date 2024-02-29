package com.x.calendar.assemble.control.jaxrs.calendar;

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
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("calendar")
@JaxrsDescribe("日历信息管理服务")
public class CalendarAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(CalendarAction.class);

	@JaxrsMethodDescribe(value = "获取我能访问到的所有日历信息列表", action = ActionListMyCalendar.class)
	@GET
	@Path("list/my")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyCalendar(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionListMyCalendar.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionListMyCalendar().execute(request, effectivePerson);
//				result = ((ActionListWhatICanView) proxy.getProxy(ActionListWhatICanView.class)).execute(request,
//						effectivePerson);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "获取我能访问到的所有日历信息列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取所有公开的日历信息列表", action = ActionListPublicCalendar.class)
	@GET
	@Path("list/public")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPublicCalendar(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionListPublicCalendar.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListPublicCalendar().execute(request, effectivePerson);
//				result = ((ActionListPublicCalendar) proxy.getProxy(ActionListPublicCalendar.class)).execute(request,
//						effectivePerson);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "获取所有公开的日历信息列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取日历信息", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历信息ID") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGet().execute(request, effectivePerson, id);
//				result = ((ActionGet) proxy.getProxy(ActionGet.class)).execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "根据ID获取日历信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据条件查询日历信息列表", action = ActionListWithCondition.class)
	@PUT
	@Path("list/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("查询条件") JsonElement jsonElement) {
		ActionResult<List<ActionListWithCondition.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListWithCondition().execute(request, effectivePerson, jsonElement);
//				result = ((ActionListWithCondition) proxy.getProxy(ActionListWithCondition.class)).execute(request,
//						effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "根据条件查询日历信息列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "判断当前用户是否有管理员权限", action = ActionIsManager.class)
	@GET
	@Path("ismanager")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isManager(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionIsManager().execute(request, effectivePerson);
//				result = ((ActionIsManager) proxy.getProxy(ActionIsManager.class)).execute(request, effectivePerson);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "判断当前用户是否有管理员权限时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "关注一个公开的日历信息", action = ActionFollowCalendar.class)
	@GET
	@Path("follow/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void followCalendar(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历信息ID") @PathParam("id") String id) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionFollowCalendar().execute(request, effectivePerson, id);
//				result = ((ActionFollowCalendar) proxy.getProxy(ActionFollowCalendar.class)).execute(request,
//						effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "关注一个公开的日历信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "取消关注一个公开的日历信息", action = ActionFollowCalendarCancel.class)
	@GET
	@Path("follow/{id}/cancel")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void followCalendarCancel(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历信息ID") @PathParam("id") String id) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionFollowCalendarCancel().execute(request, effectivePerson, id);
//				result = ((ActionFollowCalendarCancel) proxy.getProxy(ActionFollowCalendarCancel.class))
//						.execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "取消关注一个公开的日历信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "判断当前用户是否对指定的日历有管理员权限", action = ActionIsCalendarManager.class)
	@GET
	@Path("ismanager/calendar/{accountId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isCalendarManager(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("指定的日历ID") String accountId) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionIsCalendarManager().execute(request, effectivePerson, accountId);
//				result = ((ActionIsCalendarManager) proxy.getProxy(ActionIsCalendarManager.class)).execute(request,
//						effectivePerson, accountId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "判断当前用户是否对指定的日历有管理员权限时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "新建或者更新日历信息", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历信息") JsonElement jsonElement) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSave().execute(request, effectivePerson, jsonElement);
//				result = ((ActionSave) proxy.getProxy(ActionSave.class)).execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "新建或者更新日历信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID删除日历信息及所有事件", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destory(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("日历信息ID") @PathParam("id") String id) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionDelete().execute(request, effectivePerson, id);
//				result = ((ActionDelete) proxy.getProxy(ActionDelete.class)).execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionCalendarInfoProcess(e, "根据ID删除日历信息及所有事件时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}