package com.x.report.assemble.control.jaxrs.workinfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoCompanyStrategyWorks;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryMeasureWithId;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionQueryWorkPlanWithReportId;

@Path("strategyworksnap")
@JaxrsDescribe("重点工作信息快照查询管理服务")
public class StrategyWorkSnapAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(StrategyWorkSnapAction.class);

	@JaxrsMethodDescribe(value = "根据汇报ID以及举措ID查询指定的举措信息对象", action = ActionGetMeasureSnapWithMeasureId.class)
	@GET
	@Path("measure/{reportId}/{measureId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMeasureSnapWithReportWithId( @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作汇报信息ID") @PathParam("reportId") String reportId, 
			@JaxrsParameterDescribe("举措信息ID") @PathParam("measureId") String measureId ) {
		ActionResult<CompanyStrategyMeasure.WoMeasuresInfo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGetMeasureSnapWithMeasureId().execute(request, effectivePerson, reportId, measureId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryMeasureWithId( e, measureId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "根据汇报ID以及涉及的重点工作ID查询指定的重点工作信息", action = ActionGetWorkSnapWithWorkId.class)
	@GET
	@Path("work/{reportId}/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWorkSnapWithReportWithId( @Context HttpServletRequest request,
										 @JaxrsParameterDescribe("工作汇报信息ID") @PathParam("reportId") String reportId,
										 @JaxrsParameterDescribe("工作信息ID") @PathParam("workId") String workId ) {
		ActionResult<WoCompanyStrategyWorks> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGetWorkSnapWithWorkId().execute(request, effectivePerson, reportId, workId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryWorkPlanWithReportId( e, workId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}