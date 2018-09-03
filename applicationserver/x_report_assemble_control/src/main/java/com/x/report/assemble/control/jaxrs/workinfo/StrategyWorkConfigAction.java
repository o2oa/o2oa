package com.x.report.assemble.control.jaxrs.workinfo;

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
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoCompanyStrategy;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoCompanyStrategyWorks;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionQueryFullInfoWithReportId;

@Path("strategywork")
@JaxrsDescribe("重点工作信息快照查询管理服务")
public class StrategyWorkConfigAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(StrategyWorkConfigAction.class);
	
	@JaxrsMethodDescribe(value = "从战略系统获取战略举措配置", action = ActionListCompanyStrategyMeasure.class)
	@GET
	@Path("listCompanyStrategyMeasure/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response connectStrategyMeasureWithYear( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("战略举措配置年份") @PathParam("year") String year) {
		ActionResult<List<WoCompanyStrategy>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListCompanyStrategyMeasure().execute(request, effectivePerson, year );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryFullInfoWithReportId( e, year );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "从战略系统获取部门重点工作列表", action = ActionListCompanyStrategyWorks.class)
	@GET
	@Path("listCompanyStrategyWorks/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response connectWorksWithYear( @Context HttpServletRequest request
			, @JaxrsParameterDescribe("部门重点工作年份") @PathParam("year") String year
			, @JaxrsParameterDescribe("部门重点工作月份") @PathParam("month") String month
			) {
		ActionResult<List<WoCompanyStrategyWorks>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListCompanyStrategyWorks().execute(request, effectivePerson, year, month );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionQueryFullInfoWithReportId( e, year );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}