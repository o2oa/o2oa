package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.CompanyStubs;

@Path("period")
public class PeriodAction {

	/*****************************************************************************************************************************/
	/* 每月产生的待办任务量 start */
	/*****************************************************************************************************************************/

	@HttpMethodDescribe(value = "获取每月产生的待办任务,所对应的应用流程以及活动,根据Task和TaskCompleted统计得到.", response = ApplicationStubs.class)
	@GET
	@Path("list/start/task/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStartTaskApplicationStubs(@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListStartTaskApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取每月产生的待办任务,所对应的公司以及部门,根据Task和TaskCompleted统计得到.", response = CompanyStubs.class)
	@GET
	@Path("list/start/task/companystubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStartTaskCompanyStubs(@Context HttpServletRequest request) {
		ActionResult<CompanyStubs> result = new ActionResult<>();
		try {
			result = new ActionListStartTaskCompanyStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,(0)表示占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/process/{processId}/activity/{activityId}/company/{companyName}/department/{departmentName}/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartTask(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTask().execute(applicationId, processId, activityId, companyName,
					departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据company进行分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/process/{processId}/activity/{activityId}/by/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartTaskByCompany(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByCompany().execute(applicationId, processId, activityId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据department进行分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/process/{processId}/activity/{activityId}/company/{companyName}/by/department")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartTaskByDepartment(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId, @PathParam("companyName") String companyName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByDepartment().execute(applicationId, processId, activityId,
					companyName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据application进行分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/start/task/company/{companyName}/department/{departmentName}/person/{personName}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartTaskByApplication(@Context HttpServletRequest request,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByApplication().execute(companyName, departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据process进行分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/company/{companyName}/department/{departmentName}/person/{personName}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartTaskByProcess(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByProcess().execute(applicationId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月产生的待办数量,现有待办+已经办理完成的已办,根据activity进行分项统计,(0)表示占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/start/task/application/{applicationId}/process/{processId}/company/{companyName}/department/{departmentName}/person/{personName}/by/activity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartTaskByActivity(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartTaskByActivity().execute(applicationId, processId, companyName,
					departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/*****************************************************************************************************************************/
	/* 每月产生的待办任务量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月完成的待办任务量 start */
	/*****************************************************************************************************************************/

	@HttpMethodDescribe(value = "获取每月完成的待办任务,所对应的应用流程以及活动,根据TaskCompleted统计得到.", response = ApplicationStubs.class)
	@GET
	@Path("list/completed/task/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCompletedTaskApplicationStubs(@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListCompletedTaskApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取每月完成的待办任务,所对应的公司以及部门,根据TaskCompleted统计得到.", response = CompanyStubs.class)
	@GET
	@Path("list/completed/task/companystubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCompletedTaskCompanyStubs(@Context HttpServletRequest request) {
		ActionResult<CompanyStubs> result = new ActionResult<>();
		try {
			result = new ActionListCompletedTaskCompanyStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.(0)表示占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/process/{processId}/activity/{activityId}/company/{companyName}/department/{departmentName}/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedTask(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<List<WrapOutMap>> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTask().execute(applicationId, processId, activityId, companyName,
					departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按company分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/process/{processId}/activity/{activityId}/by/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedTaskByCompany(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByCompany().execute(applicationId, processId, activityId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按department分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/process/{processId}/activity/{activityId}/company/{companyName}/by/department")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedTaskByDepartment(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId, @PathParam("companyName") String companyName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByDepartment().execute(applicationId, processId, activityId,
					companyName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按application分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/completed/task/company/{companyName}/department/{departmentName}/person/{personName}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedTaskByApplication(@Context HttpServletRequest request,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByApplication().execute(companyName, departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按process分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/company/{companyName}/department/{departmentName}/person/{personName}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedTaskByProcess(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByProcess().execute(applicationId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的办理量,,根据TaskCompleted统计得到.按process分项统计,(0)表示占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/completed/task/application/{applicationId}/process/{processId}/company/{companyName}/department/{departmentName}/person/{personName}/by/activity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedTaskByActivity(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedTaskByActivity().execute(applicationId, processId, companyName,
					departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	/*****************************************************************************************************************************/
	/* 每月完成的待办任务量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月超时的待办任务量 start */
	/*****************************************************************************************************************************/

	@HttpMethodDescribe(value = "获取每月产生的超时待办任务,所对应的应用流程以及活动,根据Task和TaskCompleted统计得到.", response = ApplicationStubs.class)
	@GET
	@Path("list/expired/task/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listExpiredTaskApplicationStubs(@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListExpiredTaskApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取每月产生的超时待办任务,所对应的公司以及部门,根据Task和TaskCompleted统计得到.", response = CompanyStubs.class)
	@GET
	@Path("list/expired/task/companystubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listExpiredTaskCompanyStubs(@Context HttpServletRequest request) {
		ActionResult<CompanyStubs> result = new ActionResult<>();
		try {
			result = new ActionListExpiredTaskCompanyStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份.(0)表示占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/process/{processId}/activity/{activityId}/company/{companyName}/department/{departmentName}/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredTask(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTask().execute(applicationId, processId, activityId, companyName,
					departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按company分项统计,(0)表示占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/process/{processId}/activity/{activityId}/by/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredTaskByCompany(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByCompany().execute(applicationId, processId, activityId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按department分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/process/{processId}/activity/{activityId}/company/{companyName}/by/department")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredTaskByDepartment(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("activityId") String activityId, @PathParam("companyName") String companyName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByDepartment().execute(applicationId, processId, activityId,
					companyName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按application分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/expired/task/company/{companyName}/department/{departmentName}/person/{personName}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredTaskByApplication(@Context HttpServletRequest request,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByApplication().execute(companyName, departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按process分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/company/{companyName}/department/{departmentName}/person/{personName}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredTaskByProcess(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByProcess().execute(applicationId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的超时办理量,根据task和taskCompleted的截至时间来计算,计入到待办截至时间的所在月份,按activity分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/expired/task/application/{applicationId}/process/{processId}/company/{companyName}/department/{departmentName}/person/{personName}/by/activity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredTaskByActivity(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredTaskByActivity().execute(applicationId, processId, companyName,
					departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/*****************************************************************************************************************************/
	/* 每月超时的待办任务量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月产生Work数量 start */
	/*****************************************************************************************************************************/

	@HttpMethodDescribe(value = "获取每月产生的Work,所对应的应用流程以及活动,根据Work和WorkCompleted统计得到.", response = ApplicationStubs.class)
	@GET
	@Path("list/start/work/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStartWorkApplicationStubs(@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListStartWorkApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取每月产生的Work,所对应的公司以及部门,根据Work和WorkCompleted统计得到.", response = CompanyStubs.class)
	@GET
	@Path("list/start/work/companystubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStartWorkCompanyStubs(@Context HttpServletRequest request) {
		ActionResult<CompanyStubs> result = new ActionResult<>();
		try {
			result = new ActionListStartWorkCompanyStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,(0)作为占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/start/work/application/{applicationId}/process/{processId}/company/{companyName}/department/{departmentName}/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartWork(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWork().execute(applicationId, processId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,按company分项统计,(0)作为占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/start/work/application/{applicationId}/process/{processId}/by/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartWorkByCompany(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWorkByCompany().execute(applicationId, processId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,按department分项统计,(0)作为占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/start/work/application/{applicationId}/process/{processId}/company/{companyName}/by/department")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartWorkByDepartment(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWorkByDepartment().execute(applicationId, processId, companyName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,按application分项统计,(0)作为占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/start/work/company/{companyName}/department/{departmentName}/person/{personName}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartWorkByApplication(@Context HttpServletRequest request,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWorkByApplication().execute(companyName, departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月的Work创建量,统计work和workCompleted,按process分项统计,(0)作为占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/start/work/application/{applicationId}/company/{companyName}/department/{departmentName}/person/{personName}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountStartWorkByProcess(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountStartWorkByProcess().execute(applicationId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/*****************************************************************************************************************************/
	/* 每月产生Work数量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月完成Work数量 start */
	/*****************************************************************************************************************************/

	@HttpMethodDescribe(value = "获取每月完成的Work,所对应的应用流程以及活动,根据WorkCompleted统计得到.", response = ApplicationStubs.class)
	@GET
	@Path("list/completed/work/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCompletedWorkApplicationStubs(@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListCompletedWorkApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取每月完成的Work,所对应的公司以及部门,根据WorkCompleted统计得到.", response = CompanyStubs.class)
	@GET
	@Path("list/completed/work/companystubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCompletedWorkCompanyStubs(@Context HttpServletRequest request) {
		ActionResult<CompanyStubs> result = new ActionResult<>();
		try {
			result = new ActionListCompletedWorkCompanyStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计,(0)表示占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/completed/work/application/{applicationId}/process/{processId}/company/{companyName}/department/{departmentName}/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedWork(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<List<WrapOutMap>> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWork().execute(applicationId, processId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计得到,根据company分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/completed/work/application/{applicationId}/process/{processId}/by/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedWorkByCompany(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWorkByCompany().execute(applicationId, processId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计得到,根据department分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/completed/work/application/{applicationId}/process/{processId}/company/{companyName}/by/department")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedWorkByDepartment(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWorkByDepartment().execute(applicationId, processId, companyName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计得到,根据application分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/completed/work/company/{companyName}/department/{departmentName}/person/{personName}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedWorkByApplication(@Context HttpServletRequest request,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWorkByApplication().execute(companyName, departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月完成的Work数量,时长,步数.根据workcompleted统计得到,根据process分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/completed/work/application/{applicationId}/company/{companyName}/department/{departmentName}/person/{personName}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountCompletedWorkByProcess(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountCompletedWorkByProcess().execute(applicationId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	/*****************************************************************************************************************************/
	/* 每月完成Work数量 end */
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	/* 每月超时Work数量 start */
	/*****************************************************************************************************************************/

	@HttpMethodDescribe(value = "获取每月超时的Work,所对应的应用流程以及活动,根据Work和WorkCompleted统计得到.", response = ApplicationStubs.class)
	@GET
	@Path("list/expired/work/applicationstubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listExpiredWorkApplicationStubs(@Context HttpServletRequest request) {
		ActionResult<ApplicationStubs> result = new ActionResult<>();
		try {
			result = new ActionListExpiredWorkApplicationStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取每月超时的Work,所对应的公司以及部门,根据Work和WorkCompleted统计得到.", response = CompanyStubs.class)
	@GET
	@Path("list/expired/work/companystubs")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listExpiredWorkCompanyStubs(@Context HttpServletRequest request) {
		ActionResult<CompanyStubs> result = new ActionResult<>();
		try {
			result = new ActionListExpiredWorkCompanyStubs().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,(0)表示占位符.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/expired/work/application/{applicationId}/process/{processId}/company/{companyName}/department/{departmentName}/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredWork(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWork().execute(applicationId, processId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,按company分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/expired/work/application/{applicationId}/process/{processId}/by/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredWorkByCompany(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWorkByCompany().execute(applicationId, processId);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,按department分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/expired/work/application/{applicationId}/process/{processId}/company/{companyName}/by/department")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredWorkByDepartment(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("processId") String processId,
			@PathParam("companyName") String companyName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWorkByDepartment().execute(applicationId, processId, companyName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,按department分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/expired/work/company/{companyName}/department/{departmentName}/person/{personName}/by/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredWorkByApplication(@Context HttpServletRequest request,
			@PathParam("companyName") String companyName, @PathParam("departmentName") String departmentName,
			@PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWorkByApplication().execute(companyName, departmentName, personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取过去12个月中每月超时的Work数量.根据work和workCompleted统计得到,按process分项统计,(0)表示占位符.", response = WrapOutMap.class)
	@GET
	@Path("list/count/expired/work/application/{applicationId}/company/{companyName}/department/{departmentName}/person/{personName}/by/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountExpiredWorkByProcess(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("companyName") String companyName,
			@PathParam("departmentName") String departmentName, @PathParam("personName") String personName) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionListCountExpiredWorkByProcess().execute(applicationId, companyName, departmentName,
					personName);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/*****************************************************************************************************************************/
	/* 每月超时Work数量 end */
	/*****************************************************************************************************************************/
}