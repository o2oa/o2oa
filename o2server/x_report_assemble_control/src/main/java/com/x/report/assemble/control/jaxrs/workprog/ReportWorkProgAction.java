package com.x.report.assemble.control.jaxrs.workprog;

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
import com.x.report.assemble.control.jaxrs.workprog.exception.ExceptionQueryWorkProgWithReportId;

@Path("workprog")
@JaxrsDescribe("汇报工作完成情况信息管理服务")
public class ReportWorkProgAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(ReportWorkProgAction.class);

	@JaxrsMethodDescribe(value = "根据ID查询指定的工作完成情况信息列表", action = ActionGetWorkProgWithId.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithId( @Context HttpServletRequest request, @JaxrsParameterDescribe("ID") @PathParam("id") String id ) {
		ActionResult<ActionGetWorkProgWithId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGetWorkProgWithId().execute(request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryWorkProgWithReportId( e, id );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据汇报ID查询所有的工作完成情况信息列表", action = ActionListWorkProgWithReport.class)
	@GET
	@Path("listWithReport/{reportId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithReport( @Context HttpServletRequest request, @JaxrsParameterDescribe("工作汇报ID") @PathParam("reportId") String reportId ) {
		ActionResult<List<ActionListWorkProgWithReport.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListWorkProgWithReport().execute(request, effectivePerson, reportId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryWorkProgWithReportId( e, reportId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据工作ID列表查询所有的工作完成情况信息列表", action = ActionListWorkProgWithFilter.class)
	@PUT
	@Path("listWithWorks/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithWorks( @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year,
			JsonElement jsonElement ) {
		ActionResult<List<ActionListWorkProgWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListWorkProgWithFilter().execute(request, effectivePerson, year, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryWorkProgWithReportId( e, year );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "新建或者更新工作完成情况信息", action = ActionSaveWorkProg.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, @JaxrsParameterDescribe("工作完成情况信息") JsonElement jsonElement) {
		ActionResult<ActionSaveWorkProg.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSaveWorkProg().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSettingInfoProcess( e, "新建或者更新工作完成情况信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
	@JaxrsMethodDescribe( value = "更新排序号", action = ActionUpdateOrderNumber.class )
	@PUT
	@Path("order/update")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOrder( @Context HttpServletRequest request, @JaxrsParameterDescribe("完成情况信息顺序对象列表") JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionUpdateOrderNumber().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSettingInfoProcess( e, "新建或者更新工作完成情况信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除工作完成情况信息对象.", action = ActionDeleteWorkProg.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作完成情况信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDeleteWorkProg.Wo> result = new ActionResult<>();
		try {
			result = new ActionDeleteWorkProg().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionDeleteWorkPlan(e, id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}