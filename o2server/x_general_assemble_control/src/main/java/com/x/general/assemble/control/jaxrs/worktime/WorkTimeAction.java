package com.x.general.assemble.control.jaxrs.worktime;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "WorkTimeAction", description = "工作时间.")
@Path("worktime")
@JaxrsDescribe("工作时间.")
public class WorkTimeAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkTimeAction.class);
	private static final String OPERATIONID_PREFIX = "WorkTimeAction::";

	@Operation(summary = "计算开始时间和结束时间之间的工作时间间隔(分钟).", operationId = OPERATIONID_PREFIX + "betweenMinutes", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionBetweenMinutes.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "计算开始时间和结束时间之间的工作时间间隔(分钟).", action = ActionBetweenMinutes.class)
	@GET
	@Path("betweenminutes/start/{start}/end/{end}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void betweenMinutes(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("开始时间") @PathParam("start") String start,
			@JaxrsParameterDescribe("结束时间") @PathParam("end") String end) {
		ActionResult<ActionBetweenMinutes.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBetweenMinutes().execute(effectivePerson, start, end);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "计算开始时间前进指定分钟数后的工作时间.", operationId = OPERATIONID_PREFIX + "forwardMinutes", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionForwardMinutes.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "计算开始时间前进指定分钟数后的工作时间.", action = ActionForwardMinutes.class)
	@GET
	@Path("forwardminutes/start/{start}/minutes/{minutes}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void forwardMinutes(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("开始时间") @PathParam("start") String start,
			@JaxrsParameterDescribe("前进分钟数") @PathParam("minutes") int minutes) {
		ActionResult<ActionForwardMinutes.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionForwardMinutes().execute(effectivePerson, start, minutes);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "计算开始时间前进指定工作天数后的工作时间.", operationId = OPERATIONID_PREFIX + "forwardDays", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionForwardMinutes.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "计算开始时间前进指定工作天数后的工作时间.", action = ActionForwardDays.class)
	@GET
	@Path("forwarddays/start/{start}/days/{days}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void forwardDays(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("开始时间") @PathParam("start") String start,
			@JaxrsParameterDescribe("前进工作天数") @PathParam("days") int days) {
		ActionResult<ActionForwardDays.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionForwardDays().execute(effectivePerson, start, days);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "返回一个工作日的工作分钟数.", operationId = OPERATIONID_PREFIX + "minutesOfWorkDay", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionMinutesOfWorkDay.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "返回一个工作日的工作分钟数.", action = ActionMinutesOfWorkDay.class)
	@GET
	@Path("minutesofworkday")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void minutesOfWorkDay(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionMinutesOfWorkDay.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionMinutesOfWorkDay().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "返回指定时间是否是工作时间.", operationId = OPERATIONID_PREFIX + "isWorkTime", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionIsWorkTime.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "返回指定时间是否是工作时间.", action = ActionIsWorkTime.class)
	@GET
	@Path("isworktime/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isWorkTime(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("指定时间") @PathParam("date") String date) {
		ActionResult<ActionIsWorkTime.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionIsWorkTime().execute(effectivePerson, date);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "返回指定时间是否是工作日.", operationId = OPERATIONID_PREFIX + "isWorkDay", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionIsWorkDay.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "返回指定时间是否是工作日.", action = ActionIsWorkDay.class)
	@GET
	@Path("isworkday/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isWorkDay(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("指定日期") @PathParam("date") String date) {
		ActionResult<ActionIsWorkDay.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionIsWorkDay().execute(effectivePerson, date);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "返回指定时间是否是节假日.", operationId = OPERATIONID_PREFIX + "isHoliday", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionIsHoliday.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "返回指定时间是否是节假日.", action = ActionIsHoliday.class)
	@GET
	@Path("isholiday/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isHoliday(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("指定日期") @PathParam("date") String date) {
		ActionResult<ActionIsHoliday.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionIsHoliday().execute(effectivePerson, date);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "返回指定时间是否定义为节假日.", operationId = OPERATIONID_PREFIX + "inDefinedHoliday", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionInDefinedHoliday.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "返回指定时间是否定义为节假日.", action = ActionInDefinedHoliday.class)
	@GET
	@Path("indefinedholiday/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void inDefinedHoliday(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("指定日期") @PathParam("date") String date) {
		ActionResult<ActionInDefinedHoliday.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionInDefinedHoliday().execute(effectivePerson, date);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "返回指定时间是否定义为工作日.", operationId = OPERATIONID_PREFIX + "inDefinedWorkDay", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionInDefinedWorkDay.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "返回指定时间是否定义为工作日.", action = ActionInDefinedWorkDay.class)
	@GET
	@Path("indefinedworkday/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void inDefinedWorkDay(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("指定日期") @PathParam("date") String date) {
		ActionResult<ActionInDefinedWorkDay.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionInDefinedWorkDay().execute(effectivePerson, date);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "根据2个日期计算出节假天数.", operationId = OPERATIONID_PREFIX + "betweenHolidayCount", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionHolidayCount.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "根据2个日期计算出节假天数.", action = ActionHolidayCount.class)
	@GET
	@Path("betweenholidaycount/start/{startDate}/end/{endDate}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void betweenHolidayCount(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("开始日期") @PathParam("startDate") String start,
			@JaxrsParameterDescribe("结束日期") @PathParam("endDate") String end) {
		ActionResult<ActionHolidayCount.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionHolidayCount().execute(effectivePerson, start, end);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}