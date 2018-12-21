package com.x.report.assemble.control.jaxrs.workplan;

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
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionDeleteWorkPlan;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionQueryWorkPlanNextWithReportId;

@Path("workplannext")
@JaxrsDescribe("下周期工作信息管理服务")
public class ReportWorkPlanNextAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( ReportWorkPlanNextAction.class );

	@JaxrsMethodDescribe(value = "根据ID查询指定的下周期工作计划信息列表", action = ActionGetWorkPlanNextWithId.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithId( @Context HttpServletRequest request, @JaxrsParameterDescribe("ID") @PathParam("id") String id ) {
		ActionResult<ActionGetWorkPlanNextWithId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGetWorkPlanNextWithId().execute(request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryWorkPlanNextWithReportId( e, id );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据汇报ID查询所有的下周期工作计划信息列表", action = ActionListWorkPlanNextWithReport.class)
	@GET
	@Path("listWithReport/{reportId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithReport( @Context HttpServletRequest request, @JaxrsParameterDescribe("工作汇报ID") @PathParam("reportId") String reportId ) {
		ActionResult<List<ActionListWorkPlanNextWithReport.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListWorkPlanNextWithReport().execute(request, effectivePerson, reportId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryWorkPlanNextWithReportId( e, reportId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "新建或者更新下周期工作计划信息", action = ActionSaveWorkPlanNext.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, @JaxrsParameterDescribe("下周期工作计划信息") JsonElement jsonElement) {
		ActionResult<ActionSaveWorkPlanNext.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSaveWorkPlanNext().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSettingInfoProcess( e, "新建或者更新下周期工作计划信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "更新排序号", action = ActionUpdatePlanNextOrderNumber.class )
	@PUT
	@Path("order/update")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOrder( @Context HttpServletRequest request, @JaxrsParameterDescribe("下周期工作计划顺序对象列表") JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionUpdatePlanNextOrderNumber().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSettingInfoProcess( e, "更新下周期工作计划信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除下周期工作计划信息对象.", action = ActionDeleteWorkPlanNext.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作计划ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDeleteWorkPlanNext.Wo> result = new ActionResult<>();
		try {
			result = new ActionDeleteWorkPlanNext().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionDeleteWorkPlan(e, id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}