package com.x.okr.assemble.control.jaxrs.statistic;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.ActionSave;
import com.x.okr.assemble.control.jaxrs.statistic.BaseAction.WoOkrReportSubmitStatusDate;
import com.x.okr.assemble.control.jaxrs.statistic.BaseAction.WoOkrStatisticReportContentCenter;

@Path("streportcontent")
@JaxrsDescribe("工作汇报内容统计信息管理服务")
public class OkrStatisticReportContentAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrStatisticReportContentAction.class);

	@JaxrsMethodDescribe(value = "测试定时代理，对工作的汇报情况进行统计分析", action = ActionSave.class)
	@GET
	@Path("excute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void excute(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ActionStReportContentCaculate().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteReportStatusCaculate got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "测试定时代理，对工作的汇报提交情况进行统计分析.", action = ActionStReportContentCaculateAll.class)
	@GET
	@Path("excute/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void excuteAll(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ActionStReportContentCaculateAll().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteReportStatusCaculate got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Path("filter/list")
	@JaxrsMethodDescribe(value = "根据条件获取OkrStatisticReportContent部分信息对象.", action = ActionStReportContentListByFilter.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByCondition(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrStatisticReportContentCenter>> result = new ActionResult<>();
		try {
			result = new ActionStReportContentListByFilter().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteReportStatusCaculate got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Path("filter/tree")
	@JaxrsMethodDescribe(value = "根据条件获取OkrStatisticReportContent部分信息对象.", action = ActionStTreeByFilter.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void treeByCondition(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrStatisticReportContentCenter>> result = new ActionResult<>();
		try {
			result = new ActionStTreeByFilter().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteReportStatusCaculate got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Path("filter/sub/{parentId}")
	@JaxrsMethodDescribe(value = "根据条件获取OkrStatisticReportContent部分信息对象.", action = ActionStReportContentListSubByFilter.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listReportContentByCondition(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("上级工作信息ID") @PathParam("parentId") String parentId, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrStatisticReportContentCenter>> result = new ActionResult<>();
		try {
			result = new ActionStReportContentListSubByFilter().execute(request, effectivePerson, parentId,
					jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteReportStatusCaculate got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Path("date/list")
	@JaxrsMethodDescribe(value = "根据条件获取统计的日期列表.", action = ActionStReportContentListDateByFilter.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDateByCondition(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrReportSubmitStatusDate>> result = new ActionResult<>();
		try {
			result = new ActionStReportContentListDateByFilter().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteReportStatusCaculate got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Path("export")
	@JaxrsMethodDescribe(value = "根据条件获取OkrStatisticReportContent部分信息对象.", action = ActionStReportContentExport.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void export(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionStReportContentExport.Wo> result = new ActionResult<>();
		try {
			result = new ActionStReportContentExport().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteReportStatusCaculate got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
