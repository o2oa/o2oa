package com.x.report.assemble.control.jaxrs.workinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryReportWithReportId;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryWorkInfoWithId;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionSaveWorkInfo;

@Path("reportworkinfo")
@JaxrsDescribe("汇报重点工作信息管理服务")
public class ReportWorkInfoAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(ReportWorkInfoAction.class);

	@JaxrsMethodDescribe( value = "新建或者更新工作信息项", action = ActionSaveWorkInfo.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveWorkInfo( @Context HttpServletRequest request, @JaxrsParameterDescribe("工作信息数据") JsonElement jsonElement) {
		ActionResult<ActionSaveWorkInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSaveWorkInfo().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSaveWorkInfo( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "更新工作计划汇总信息", action = ActionSaveWorkPlanSummary.class )
	@POST
	@Path("planSummaruy")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveWorkPlanSummary( @Context HttpServletRequest request, @JaxrsParameterDescribe("工作计划汇总信息数据") JsonElement jsonElement) {
		ActionResult<ActionSaveWorkPlanSummary.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSaveWorkPlanSummary().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSaveWorkInfo( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "更新工作完成情况汇总信息", action = ActionSaveWorkProgSummary.class )
	@POST
	@Path("progSummaruy")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveWorkProgSummary( @Context HttpServletRequest request, @JaxrsParameterDescribe("工作完成情况汇总信息数据") JsonElement jsonElement) {
		ActionResult<ActionSaveWorkProgSummary.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSaveWorkProgSummary().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSaveWorkInfo( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "更新工作计划汇总信息(批量)", action = ActionSaveWorkPlanSummaries.class )
	@POST
	@Path("planSummaries")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveWorkPlanSummaries( @Context HttpServletRequest request, @JaxrsParameterDescribe("工作计划汇总信息数据列表") JsonElement jsonElement) {
		ActionResult<List<ActionSaveWorkPlanSummaries.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSaveWorkPlanSummaries().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSaveWorkInfo( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "更新工作完成情况汇总信息(批量)", action = ActionSaveWorkProgSummaries.class )
	@POST
	@Path("progSummaries")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveWorkProgSummaries( @Context HttpServletRequest request, @JaxrsParameterDescribe("工作完成情况汇总信息数据列表") JsonElement jsonElement) {
		ActionResult<List<ActionSaveWorkProgSummaries.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSaveWorkProgSummaries().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSaveWorkInfo( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID查询工作信息", action = ActionGetWorkInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request,
			@JaxrsParameterDescribe("ID") @PathParam("id") String id ) {
		ActionResult<ActionGetWorkInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionGetWorkInfo().execute(request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryWorkInfoWithId( e, id );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据信息查询汇报涉及的重点工作ID查询部门重点工作信息列表", action = ActionListWorkInfoWithReport.class)
	@GET
	@Path("list/report/{reportId}/unitwork/{workMonthFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUnitWorkWithReport( @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作汇报信息ID") @PathParam("reportId") String reportId,
			@JaxrsParameterDescribe("工作月份类别：THISMONTH|NEXTMONTH") @PathParam("workMonthFlag") String workMonthFlag) {
		ActionResult<List<ActionListWorkInfoWithReport.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionListWorkInfoWithReport().execute(request, effectivePerson, reportId, workMonthFlag );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryReportWithReportId( e, reportId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "根据组织名称查询可使用的工作标签", action = ActionListWorkTagWithUnit.class )
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWorkTagWithUnit( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("查询条件：组织名称") JsonElement jsonElement ) {
		ActionResult<List<ActionListWorkTagWithUnit.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionListWorkTagWithUnit().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSaveWorkInfo( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}