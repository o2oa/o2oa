package com.x.okr.assemble.control.jaxrs.statistic;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.statistic.BaseAction.WoOkrStatisticReportStatusTable;

@Path("streportstatus")
@JaxrsDescribe("工作汇报状态统计信息管理服务")
public class OkrStatisticReportStatusAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrStatisticReportStatusAction.class);

	@JaxrsMethodDescribe(value = "测试定时代理，对工作的汇报提交情况进行统计分析", action = ActionStReportStatusCaculate.class)
	@GET
	@Path("excute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void excute(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ActionStReportStatusCaculate().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteStReportStatusCaculate got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "测试定时代理，对工作的汇报提交情况进行统计分析", action = ActionStReportStatusCaculateAll.class)
	@GET
	@Path("excute/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void excuteAll(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ActionStReportStatusCaculateAll().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteStReportStatusCaculateAll got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "导出工作的汇报状态统计信息", action = ActionStReportStatusExport.class)
	@Path("export")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void export(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionStReportStatusExport.Wo> result = new ActionResult<>();
		try {
			result = new ActionStReportStatusExport().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteStReportStatusCaculateAll got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据条件查询工作的汇报状态统计信息", action = ActionStReportStatusListByFilter.class)
	@Path("filter/list")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listReportStatusByCondition(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WoOkrStatisticReportStatusTable> result = new ActionResult<>();
		try {
			result = new ActionStReportStatusListByFilter().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
			logger.warn("system excute ExcuteStReportStatusCaculateAll got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
