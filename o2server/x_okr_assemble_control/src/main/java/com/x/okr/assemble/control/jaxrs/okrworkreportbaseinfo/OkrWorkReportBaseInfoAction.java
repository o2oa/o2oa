package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

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
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.BaseAction.WiOkrWorkReportBaseInfo;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.BaseAction.Wo;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.BaseAction.WoOkrWorkReportBaseInfo;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWrapInConvert;

@Path("okrworkreportbaseinfo")
@JaxrsDescribe("工作汇报信息管理服务")
public class OkrWorkReportBaseInfoAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkReportBaseInfoAction.class);

	@JaxrsMethodDescribe(value = "根据ID获取工作汇报信息", action = ActionDraftReport.class)
	@GET
	@Path("draft/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void draft(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("workId") String workId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDraftReport.Wo> result = new ActionResult<>();
		try {
			result = new ActionDraftReport().execute(request, effectivePerson, workId);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteDraftReport got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "新建或者更新工作汇报信息", action = ActionDraftReport.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		WiOkrWorkReportBaseInfo wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn(jsonElement, WiOkrWorkReportBaseInfo.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			try {
				result = new ActionSave().execute(request, effectivePerson, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error(e);
				logger.warn("system excute ExcuteSave got an exception. ");
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/**
	 * 确定汇报工作流 1、先确定工作汇报工作流执行方式：1）工作管理员督办 - 中心工作阅知领导审阅； 2）工作部署者审阅 2、如果是方式1）
	 * a.判断系统设置中是否有设置工作管理员 b.如果有设置工作管理员，那么下一步处理者为工作管理员，如果没有设置工作管理员，那么判断中心工作是否有设置阅知领导
	 * c.如果中心工作没有设置阅知领导，那么下一步处理者为工作部署者审阅，并且在汇报的descript中说明原因 3、汇报工作流执行方式生效工作层级
	 * 
	 * @param request
	 * @param wrapIn
	 * @return
	 */
	@JaxrsMethodDescribe(value = "提交工作汇报信息", action = ActionSubmit.class)
	@Path("submit")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void submit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubmit().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error(e);
				logger.warn("system excute ExcuteSubmit got an exception. ");
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID删除工作汇报信息", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作汇报信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteDelete got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID将工作汇报信息调度到结束", action = ActionDispatchToOver.class)
	@GET
	@Path("dispatch/{id}/over")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void dispatchOver(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作汇报信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<Wo> result = new ActionResult<>();
		try {
			result = new ActionDispatchToOver().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteDispatchToOver got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取工作汇报信息", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作汇报信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据工作ID获取工作汇报信息", action = ActionListByWork.class)
	@GET
	@Path("list/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("workId") String workId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkReportBaseInfo>> result = new ActionResult<>();
		try {
			result = new ActionListByWork().execute(request, effectivePerson, workId);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件查询的工作汇报信息，[草稿],下一页", action = ActionListDraftNextWithFilter.class)
	@PUT
	@Path("draft/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDraftNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkReportBaseInfo>> result = new ActionResult<>();
		try {
			result = new ActionListDraftNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件查询的工作汇报信息，[草稿],上一页", action = ActionListDraftPrevWithFilter.class)
	@PUT
	@Path("draft/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDraftPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkReportBaseInfo>> result = new ActionResult<>();
		try {
			result = new ActionListDraftPrevWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件查询的工作汇报信息，[处理中（待办）],下一页", action = ActionListMyTaskNextWithFilter.class)
	@PUT
	@Path("task/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyTaskNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListMyTaskNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListMyTaskNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件查询的工作汇报信息，[处理中（待办）],上一页", action = ActionListMyTaskPrevWithFilter.class)
	@PUT
	@Path("task/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyTaskPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListMyTaskPrevWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListMyTaskPrevWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件查询的工作汇报信息，[已处理（已办）],下一页", action = ActionListMyProcessNextWithFilter.class)
	@PUT
	@Path("process/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyProcessNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListMyProcessNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListMyProcessNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件查询的工作汇报信息，[已处理（已办）],上一页", action = ActionListMyProcessPrevWithFilter.class)
	@PUT
	@Path("process/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyProcessPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListMyProcessPrevWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListMyProcessPrevWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件查询的工作汇报信息，[已归档],下一页", action = ActionListMyArchiveNextWithFilter.class)
	@PUT
	@Path("archive/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyArchiveNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListMyArchiveNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListMyArchiveNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件查询的工作汇报信息，[已归档],上一页", action = ActionListMyArchivePrevWithFilter.class)
	@PUT
	@Path("archive/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyArchivePrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListMyArchivePrevWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListMyArchivePrevWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteGet got an exception. ");
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
