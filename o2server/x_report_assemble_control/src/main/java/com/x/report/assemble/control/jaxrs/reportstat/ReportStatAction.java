package com.x.report.assemble.control.jaxrs.reportstat;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

@Path("stat")
@JaxrsDescribe("汇报统计服务")
public class ReportStatAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ReportStatAction.class);

	@JaxrsMethodDescribe(value = "部五项重点工作完成情况统计", action = ActionStatUnitWorkReport.class)
	@GET
	@Path("stat/list/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByYear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("年份") @PathParam("year") String year) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionStatUnitWorkReport.Wo>> result = new ActionResult<>();
		
		try {
			result = new ActionStatUnitWorkReport().execute( request, effectivePerson, year );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "统计每度重点工作内容", action = ActionStatUnitWorkInYear.class)
	@PUT
	@Path("stat/work/year/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWorkInfoByYear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("查询条件") JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionStatUnitWorkInYear.Wo>> result = new ActionResult<>();
		
		try {
			result = new ActionStatUnitWorkInYear().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}