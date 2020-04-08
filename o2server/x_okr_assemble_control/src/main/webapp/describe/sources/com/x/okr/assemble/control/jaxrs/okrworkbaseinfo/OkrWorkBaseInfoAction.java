package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

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
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.WorkCommonSearchFilter;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.BaseAction.WoOkrCenterWorkInfo;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.BaseAction.WoOkrWorkBaseInfo;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.BaseAction.WoOkrWorkBaseSimpleInfo;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;

/**
 * 具体工作项有短期工作还长期工作，短期工作不需要自动启动定期汇报，由人工撰稿汇报即可
 */

@Path("okrworkbaseinfo")
@JaxrsDescribe("具体工作任务信息管理服务")
public class OkrWorkBaseInfoAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkBaseInfoAction.class);

	@JaxrsMethodDescribe(value = "新建或者更新具体工作任务信息", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSave().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteSave got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "正式部署具体工作任务事项", action = ActionDeploy.class)
	@PUT
	@Path("deploy")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deploy(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WoOkrWorkBaseInfo> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			try {
				result = new ActionDeploy().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteDeploy got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID归档具体工作任务事项", action = ActionArchive.class)
	@GET
	@Path("archive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void archive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionArchive.Wo> result = new ActionResult<>();
		try {
			result = new ActionArchive().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID对具体工作任务事项进行工作进度调整", action = ActionProgressAdjust.class)
	@GET
	@Path("progress/{id}/{percent}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void progressAdjust(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("完成进度百分比") @PathParam("percent") Integer percent) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionProgressAdjust.Wo> result = new ActionResult<>();
		try {
			result = new ActionProgressAdjust().execute(request, effectivePerson, id, percent);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "收回已经部署具体工作任务事项", action = ActionRecycle.class)
	@GET
	@Path("recycle/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void recycle(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WoOkrWorkBaseInfo> result = new ActionResult<>();
		try {
			result = new ActionRecycle().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteRecycle got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID删除具体工作任务事项", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteDelete got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取具体工作任务事项", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WoOkrWorkBaseInfo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteGet got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取具体工作任务事项详细信息，展示用", action = ActionViewWork.class)
	@GET
	@Path("view/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void view(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionViewWork.Wo> result = new ActionResult<>();
		try {
			result = new ActionViewWork().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteViewWork got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取工作的操作内容和用户在工作中的身份", action = ActionGetWorkOperationWithId.class)
	@GET
	@Path("getOperation/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getOperation(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGetWorkOperationWithId.Wo> result = new ActionResult<>();
		try {
			result = new ActionGetWorkOperationWithId().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteViewWork got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "为工作信息绑定考核流程的信息", action = ActionAppraise.class)
	@GET
	@Path("appraise/{id}/wf_workid/{wid}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void appraise(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("流程WORKID") @PathParam("wid") String wid) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionAppraise().execute(request, effectivePerson, id, wid);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteViewWork got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据上级工作ID获取具体工作任务事项", action = ActionListSubWork.class)
	@GET
	@Path("list/sub/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseInfo>> result = new ActionResult<>();
		try {
			result = new ActionListSubWork().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteListSubWork got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "判断当前用户是否有权限拆解指定工作.", action = ActionWorkCanDismantling.class)
	@GET
	@Path("canDismantlingWork/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void canDismantlingWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionWorkCanDismantling().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteWorkCanDismantling got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据中心工作ID获取我可以看到的所有具体工作任务事项.", action = ActionListUsersWorkByCenterId.class)
	@GET
	@Path("center/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWorkByCenterId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("中心工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WoOkrCenterWorkInfo> result = new ActionResult<>();
		try {
			result = new ActionListUsersWorkByCenterId().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteListUsersWorkByCenterId got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[草稿],下一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("draft/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDraftNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryInfoStatus("正常");
				wrapIn.addQueryWorkProcessStatus("草稿");
				wrapIn.addQueryProcessIdentity("部署者");
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListDraftNextWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[草稿],上一页.", action = ActionListMyWorkByProcessIdentityPrevWithFilter.class)
	@PUT
	@Path("draft/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDraftPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryInfoStatus("正常");
				wrapIn.addQueryWorkProcessStatus("草稿");
				wrapIn.addQueryProcessIdentity("部署者");
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListDraftPrevWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[部署的],下一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("deployed/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDeployedNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("部署者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListDeployNextWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[部署的],是一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("deployed/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDeployedPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("部署者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListDeployPrevWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[阅知者],下一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("read/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyReadNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("阅知者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListReadNextWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[阅知者],上一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("read/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyReadPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("阅知者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListReadPrevWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[负责的],下一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("responsibility/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyResponsibilityNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("责任者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListResponsibilityNextWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[负责的],上一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("responsibility/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyResponsibilityPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("责任者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListResponsibilityPrevWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[授权的],下一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("delegate/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDelegateNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("授权者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListDelegatedNextWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[授权的],上一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("delegate/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDelegatePrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("授权者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListDelegatedPrevWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[协助的],下一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("cooperate/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyCooperateNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("协助者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListCooperateNextWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[协助的],上一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("cooperate/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyCooperatePrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("协助者");
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				result = new ActionListMyWorkByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListCooperatePrevWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[已归档],下一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("archive/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyArchiveNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryInfoStatus("已归档");
				wrapIn.addQueryProcessIdentity("观察者");
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListArchiveNextWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[已归档],上一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("archive/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyArchivePrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryInfoStatus("已归档");
				wrapIn.addQueryProcessIdentity("观察者");
				result = new ActionListMyWorkByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListArchivePrevWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[所有工作],下一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyWorkNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("观察者");
				if (wrapIn.getSequenceField() == null || wrapIn.getSequenceField().isEmpty()) {
					wrapIn.setSequenceField("completeDateLimitStr");
				}
				result = new ActionListMyWorkByProcessIdentityNextWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListWorkSimpleInfoNextWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的具体工作任务事项[所有工作],上一页.", action = ActionListMyWorkByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyWorkPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.setProcessIdentities(null);
				wrapIn.addQueryProcessIdentity("观察者");
				if (wrapIn.getSequenceField() == null || wrapIn.getSequenceField().isEmpty()) {
					wrapIn.setSequenceField("completeDateLimitStr");
				}
				result = new ActionListMyWorkByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id,
						count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ExcuteListWorkSimpleInfoPrevWithFilter got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据中心工作ID获取我部署的所有具体工作任务事项，并且以上级工作进行归类.", action = ActionListDeployWorkInCenterForForm.class)
	@GET
	@Path("deploy/form/center/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDeployWorkInCenterForForm(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("中心工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		try {
			result = new ActionListDeployWorkInCenterForForm().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteListDeployWorkInCenterForForm got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据中心工作ID获取我需要参与[负责，协助，阅知]的所有具体工作任务事项，并且以上级工作进行归类.", action = ActionListProcessWorkInCenterForForm.class)
	@GET
	@Path("process/form/center/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyProcessWorkInCenterForForm(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("中心工作信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		try {
			result = new ActionListProcessWorkInCenterForForm().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteListProcessWorkInCenterForForm got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "统计登录者所有的工作数量.", action = ActionGetMyWorkStatistic.class)
	@GET
	@Path("statistic/my")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getMyStatistic(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGetMyWorkStatistic.Wo> result = new ActionResult<>();
		try {
			result = new ActionGetMyWorkStatistic().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteGetMyWorkStatistic got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "绑定和同步流程信息", action = ActionRecycle.class)
	@GET
	@Path("wfsync/{workInfoId}/{wf_workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void wfSync(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作信息ID") @PathParam("workInfoId") String workInfoId,
			@JaxrsParameterDescribe("考核流程的workId") @PathParam("wf_workId") String wf_workId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WoOkrWorkBaseInfo> result = new ActionResult<>();
		try {
			result = new ActionWfSync().execute(request, effectivePerson, workInfoId, wf_workId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteRecycle got an exception. ");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}