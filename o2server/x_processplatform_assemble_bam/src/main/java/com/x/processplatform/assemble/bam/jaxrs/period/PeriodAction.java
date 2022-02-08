package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.List;

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
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.UnitStubs;

@Path("period")
@JaxrsDescribe("阶段统计")
public class PeriodAction extends StandardJaxrsAction {

	/*****************************************************************************************************************************/
	/* 每月产生的待办任务量 start */
	/*****************************************************************************************************************************/

	@JaxrsMethodDescribe(value = "获取每月产生的待办任务,所对应的应用流程以及活动,根据Task和TaskCompleted统计得到.", action = ActionListStartTaskApplicationStubs.class)
	@GET
	@Path("list/start/task/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStartTaskApplicationStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListStartTaskApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取每月产生的待办任务,所对应的组织,根据Task和TaskCompleted统计得到.", action = ActionListStartTaskUnitStubs.class)
	@GET
	@Path("list/start/task/unitstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStartTaskCompanyStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<UnitStubs> result = new ActionResult<>();
		try {
			result = new ActionListStartTaskUnitStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,(0)表示占位符.", action = ActionListCountStartTask.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/process/{processId}/activity/{activityId}/unit/{unit}/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartTask(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("活动标识") @PathParam("activityId") String activityId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<List<ActionListCountStartTask.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTask().execute(applicationId, processId, activityId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据unit进行分项统计,(0)表示占位符.", action = ActionListCountStartTaskByUnit.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/process/{processId}/activity/{activityId}/by/unit")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartTaskByUnit(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("活动标识") @PathParam("activityId") String activityId) {
		ActionResult<ActionListCountStartTaskByUnit.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByUnit().execute(applicationId, processId, activityId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据application进行分项统计,(0)表示占位符.", action = ActionListCountStartTaskByApplication.class)
	@GET
	@Path("list/count/start/task/unit/{unit}/person/{person}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartTaskByApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountStartTaskByApplication.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByApplication().execute(unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据process进行分项统计,(0)表示占位符.", action = ActionListCountStartTaskByProcess.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/unit/{unit}/person/{person}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartTaskByProcess(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountStartTaskByProcess.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByProcess().execute(applicationId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据activity进行分项统计,(0)表示占位符.", action = ActionListCountStartTaskByActivity.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/process/{processId}/unit/{unit}/person/{person}/by/activity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartTaskByActivity(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountStartTaskByActivity.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByActivity().execute(applicationId, processId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/*****************************************************************************************************************************/
	/* 每月产生的待办任务量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月完成的待办任务量 start */
	/*****************************************************************************************************************************/

	@JaxrsMethodDescribe(value = "获取每月完成的待办任务,所对应的应用流程以及活动,根据TaskCompleted统计得到.", action = ActionListCompletedTaskApplicationStubs.class)
	@GET
	@Path("list/completed/task/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCompletedTaskApplicationStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListCompletedTaskApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取每月完成的待办任务,所对应的公司以及部门,根据TaskCompleted统计得到.", action = ActionListCompletedTaskUnitStubs.class)
	@GET
	@Path("list/completed/task/unitstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCompletedTaskCompanyStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<UnitStubs> result = new ActionResult<>();
		try {
			result = new ActionListCompletedTaskUnitStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.(0)表示占位符.", action = ActionListCountCompletedTask.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/process/{processId}/activity/{activityId}/unit/{unit}/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedTask(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("活动标识") @PathParam("activityId") String activityId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<List<ActionListCountCompletedTask.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTask().execute(applicationId, processId, activityId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按unit分项统计,(0)表示占位符.", action = ActionListCountCompletedTaskByUnit.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/process/{processId}/activity/{activityId}/by/unit")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedTaskByUnit(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("活动标识") @PathParam("activityId") String activityId) {
		ActionResult<ActionListCountCompletedTaskByUnit.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByUnit().execute(applicationId, processId, activityId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按application分项统计,(0)表示占位符.", action = ActionListCountCompletedTaskByApplication.class)
	@GET
	@Path("list/count/completed/task/unit/{unit}/person/{person}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedTaskByApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountCompletedTaskByApplication.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByApplication().execute(unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按process分项统计,(0)表示占位符.", action = ActionListCountCompletedTaskByProcess.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/unit/{unit}/person/{person}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedTaskByProcess(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountCompletedTaskByProcess.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByProcess().execute(applicationId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按process分项统计,(0)表示占位符.", action = ActionListCountCompletedTaskByActivity.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/process/{processId}/unit/{unit}/person/{person}/by/activity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedTaskByActivity(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("活动标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountCompletedTaskByActivity.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByActivity().execute(applicationId, processId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	/*****************************************************************************************************************************/
	/* 每月完成的待办任务量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月超时的待办任务量 start */
	/*****************************************************************************************************************************/

	@JaxrsMethodDescribe(value = "获取每月产生的超时待办任务,所对应的应用流程以及活动,根据Task和TaskCompleted统计得到.", action = ActionListExpiredTaskApplicationStubs.class)
	@GET
	@Path("list/expired/task/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listExpiredTaskApplicationStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListExpiredTaskApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取每月产生的超时待办任务,所对应的公司以及部门,根据Task和TaskCompleted统计得到.", action = ActionListExpiredTaskUnitStubs.class)
	@GET
	@Path("list/expired/task/unitstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listExpiredTaskCompanyStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<UnitStubs> result = new ActionResult<>();
		try {
			result = new ActionListExpiredTaskUnitStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份.(0)表示占位符.", action = ActionListCountExpiredTask.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/process/{processId}/activity/{activityId}/unit/{unit}/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredTask(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("活动标识") @PathParam("activityId") String activityId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<List<ActionListCountExpiredTask.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTask().execute(applicationId, processId, activityId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按unit分项统计,(0)表示占位符.", action = ActionListCountExpiredTaskByUnit.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/process/{processId}/activity/{activityId}/by/unit")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredTaskByUnit(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("活动标识") @PathParam("activityId") String activityId) {
		ActionResult<ActionListCountExpiredTaskByUnit.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByUnit().execute(applicationId, processId, activityId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按application分项统计,(0)表示占位符.", action = ActionListCountExpiredTaskByApplication.class)
	@GET
	@Path("list/count/expired/task/unit/{unit}/person/{person}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredTaskByApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountExpiredTaskByApplication.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByApplication().execute(unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按process分项统计,(0)表示占位符.", action = ActionListCountExpiredTaskByProcess.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/unit/{unit}/person/{person}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredTaskByProcess(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountExpiredTaskByProcess.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByProcess().execute(applicationId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按activity分项统计,(0)表示占位符.", action = ActionListCountExpiredTaskByActivity.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/process/{processId}/unit/{unit}/person/{person}/by/activity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredTaskByActivity(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountExpiredTaskByActivity.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByActivity().execute(applicationId, processId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/*****************************************************************************************************************************/
	/* 每月超时的待办任务量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月产生Work数量 start */
	/*****************************************************************************************************************************/

	@JaxrsMethodDescribe(value = "获取每月产生的Work,所对应的应用流程以及活动,根据Work和WorkCompleted统计得到.", action = ActionListStartWorkApplicationStubs.class)
	@GET
	@Path("list/start/work/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStartWorkApplicationStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListStartWorkApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取每月产生的Work,所对应的公司以及部门,根据Work和WorkCompleted统计得到.", action = ActionListStartWorkUnitStubs.class)
	@GET
	@Path("list/start/work/unitstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStartWorkUnitStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<UnitStubs> result = new ActionResult<>();
		try {
			result = new ActionListStartWorkUnitStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,(0)作为占位符.", action = ActionListCountStartWork.class)
	@GET
	@Path("list/count/start/work/application/{applicationId}/process/{processId}/unit/{unit}/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<List<ActionListCountStartWork.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWork().execute(applicationId, processId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,按unit分项统计,(0)作为占位符.", action = ActionListCountStartWorkByUnit.class)
	@GET
	@Path("list/count/start/work/application/{applicationId}/process/{processId}/by/unit")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartWorkByCompany(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId) {
		ActionResult<ActionListCountStartWorkByUnit.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWorkByUnit().execute(applicationId, processId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,按application分项统计,(0)作为占位符.", action = ActionListCountStartWorkByApplication.class)
	@GET
	@Path("list/count/start/work/unit/{unit}/person/{person}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartWorkByApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountStartWorkByApplication.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWorkByApplication().execute(unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,按process分项统计,(0)作为占位符.", action = ActionListCountStartWorkByProcess.class)
	@GET
	@Path("list/count/start/work/application/{applicationId}/unit/{unit}/person/{person}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountStartWorkByProcess(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountStartWorkByProcess.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWorkByProcess().execute(applicationId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/*****************************************************************************************************************************/
	/* 每月产生Work数量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月完成Work数量 start */
	/*****************************************************************************************************************************/

	@JaxrsMethodDescribe(value = "获取每月完成的Work,所对应的应用流程以及活动,根据WorkCompleted统计得到.", action = ActionListCompletedWorkApplicationStubs.class)
	@GET
	@Path("list/completed/work/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCompletedWorkApplicationStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListCompletedWorkApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取每月完成的Work,所对应的公司以及部门,根据WorkCompleted统计得到.", action = ActionListCompletedWorkUnitStubs.class)
	@GET
	@Path("list/completed/work/unitstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCompletedWorkCompanyStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<UnitStubs> result = new ActionResult<>();
		try {
			result = new ActionListCompletedWorkUnitStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计,(0)表示占位符.", action = ActionListCountCompletedWork.class)
	@GET
	@Path("list/count/completed/work/application/{applicationId}/process/{processId}/unit/{unit}/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedWork(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<List<ActionListCountCompletedWork.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWork().execute(applicationId, processId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计得到,根据unit分项统计,(0)表示占位符.", action = ActionListCountCompletedWorkByUnit.class)
	@GET
	@Path("list/count/completed/work/application/{applicationId}/process/{processId}/by/unit")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedWorkByCompany(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId) {
		ActionResult<ActionListCountCompletedWorkByUnit.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWorkByUnit().execute(applicationId, processId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计得到,根据application分项统计,(0)表示占位符.", action = ActionListCountCompletedWorkByApplication.class)
	@GET
	@Path("list/count/completed/work/unit/{unit}/person/{person}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedWorkByApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountCompletedWorkByApplication.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWorkByApplication().execute(unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计得到,根据process分项统计,(0)表示占位符.", action = ActionListCountCompletedWorkByProcess.class)
	@GET
	@Path("list/count/completed/work/application/{applicationId}/unit/{unit}/person/{person}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountCompletedWorkByProcess(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountCompletedWorkByProcess.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWorkByProcess().execute(applicationId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	/*****************************************************************************************************************************/
	/* 每月完成Work数量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月超时Work数量 start */
	/*****************************************************************************************************************************/

	@JaxrsMethodDescribe(value = "获取每月超时的Work,所对应的应用流程以及活动,根据Work和WorkCompleted统计得到.", action = ActionListExpiredWorkApplicationStubs.class)
	@GET
	@Path("list/expired/work/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listExpiredWorkApplicationStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListExpiredWorkApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取每月超时的Work,所对应的公司以及部门,根据Work和WorkCompleted统计得到.", action = ActionListExpiredWorkUnitStubs.class)
	@GET
	@Path("list/expired/work/unitstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listExpiredWorkCompanyStubs(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<UnitStubs> result = new ActionResult<>();
		try {
			result = new ActionListExpiredWorkUnitStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,(0)表示占位符.", action = ActionListCountExpiredWork.class)
	@GET
	@Path("list/count/expired/work/application/{applicationId}/process/{processId}/unit/{unit}/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<List<ActionListCountExpiredWork.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWork().execute(applicationId, processId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,按unit分项统计,(0)表示占位符.", action = ActionListCountExpiredWorkByUnit.class)
	@GET
	@Path("list/count/expired/work/application/{applicationId}/process/{processId}/by/unit")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredWorkByCompany(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("流程标识") @PathParam("processId") String processId) {
		ActionResult<ActionListCountExpiredWorkByUnit.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWorkByUnit().execute(applicationId, processId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,按department分项统计,(0)表示占位符.", action = ActionListCountExpiredWorkByApplication.class)
	@GET
	@Path("list/count/expired/work/unit/{unit}/person/{person}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredWorkByApplication(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("用户标识") @PathParam("person") String person) {
		ActionResult<ActionListCountExpiredWorkByApplication.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWorkByApplication().execute(unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,按process分项统计,(0)表示占位符.", action = ActionListCountExpiredWorkByProcess.class)
	@GET
	@Path("list/count/expired/work/application/{applicationId}/unit/{unit}/person/{person}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCountExpiredWorkByProcess(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("applicationId") String applicationId,
			@JaxrsParameterDescribe("组织标识") @PathParam("unit") String unit,
			@JaxrsParameterDescribe("个人标识") @PathParam("person") String person) {
		ActionResult<ActionListCountExpiredWorkByProcess.Wo> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWorkByProcess().execute(applicationId, unit, person);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/*****************************************************************************************************************************/
	/* 每月超时Work数量 end */
	/*****************************************************************************************************************************/
}