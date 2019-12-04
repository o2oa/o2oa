package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

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
import com.x.okr.assemble.control.jaxrs.WorkCommonSearchFilter;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionWrapInConvert;

@Path("okrcenterworkinfo")
@JaxrsDescribe("中心工作管理服务")
public class OkrCenterWorkInfoAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrCenterWorkInfoAction.class);

	@JaxrsMethodDescribe(value = "新建或者更新中心工作信息对象", action = ActionSave.class)
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
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取中心工作信息对象", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("中心工作ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建中心工作草稿, 未保存", action = ActionDraftNewCenter.class)
	@GET
	@Path("draft")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void draftNew(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDraftNewCenter.Wo> result = new ActionResult<>();
		try {
			result = new ActionDraftNewCenter().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID删除中心工作数据对象", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("中心工作ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID归档中心工作数据对象", action = ActionArchive.class)
	@GET
	@Path("archive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void archive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("中心工作ID") @PathParam("id") String id) {
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

	@JaxrsMethodDescribe(value = "中心工作正式部署服务", action = ActionDeploy.class)
	@GET
	@Path("deploy/{centerId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deploy(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("中心工作ID") @PathParam("centerId") String centerId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDeploy.Wo> result = new ActionResult<>();
		try {
			result = new ActionDeploy().execute(request, effectivePerson, centerId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的中心工作[草稿],下一页", action = ActionListByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("draft/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDraftNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByProcessIdentityNextWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			try {
				wrapIn.setProcessIdentities(null);
				wrapIn.setWorkProcessStatuses(null);
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.addQueryInfoStatus("正常");
				wrapIn.addQueryWorkProcessStatus("草稿");
				wrapIn.addQueryProcessIdentity("部署者");
				result = new ActionListByProcessIdentityNextWithFilter().execute(request, effectivePerson, id, count,
						wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的中心工作[草稿],上一页", action = ActionListByProcessIdentityPrevWithFilter.class)
	@PUT
	@Path("draft/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDraftPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByProcessIdentityPrevWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}

		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setProcessIdentities(null);
				wrapIn.setWorkProcessStatuses(null);
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.addQueryInfoStatus("正常");
				wrapIn.addQueryWorkProcessStatus("草稿");
				wrapIn.addQueryProcessIdentity("部署者");
				result = new ActionListByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id, count,
						wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的中心工作[部署的],下一页", action = ActionListByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("deployed/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDeployedNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByProcessIdentityNextWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}

		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setProcessIdentities(null);
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				wrapIn.addQueryProcessIdentity("观察者");
				result = new ActionListByProcessIdentityNextWithFilter().execute(request, effectivePerson, id, count,
						wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的中心工作[部署的],上一页", action = ActionListByProcessIdentityPrevWithFilter.class)
	@PUT
	@Path("deployed/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyDeployedPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByProcessIdentityPrevWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}

		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setProcessIdentities(null);
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				wrapIn.addQueryProcessIdentity("观察者");
				result = new ActionListByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id, count,
						wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的中心工作[我可以阅知的],下一页", action = ActionListByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("read/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyReadNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByProcessIdentityNextWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}

		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setProcessIdentities(null);
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				wrapIn.addQueryProcessIdentity("观察者");
				result = new ActionListByProcessIdentityNextWithFilter().execute(request, effectivePerson, id, count,
						wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的中心工作[我可以阅知的],上一页", action = ActionListByProcessIdentityPrevWithFilter.class)
	@PUT
	@Path("read/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyReadPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByProcessIdentityPrevWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}

		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setProcessIdentities(null);
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.addQueryInfoStatus("正常");
				if (wrapIn.getWorkProcessStatuses() == null) {
					wrapIn.addQueryWorkProcessStatus("待审核");
					wrapIn.addQueryWorkProcessStatus("待确认");
					wrapIn.addQueryWorkProcessStatus("执行中");
					wrapIn.addQueryWorkProcessStatus("已完成");
					wrapIn.addQueryWorkProcessStatus("已撤消");
				}
				wrapIn.addQueryProcessIdentity("观察者");
				result = new ActionListByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id, count,
						wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的中心工作[已归档],下一页", action = ActionListByProcessIdentityNextWithFilter.class)
	@PUT
	@Path("archive/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyArchiveNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByProcessIdentityNextWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}

		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setProcessIdentities(null);
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.addQueryInfoStatus("已归档");
				wrapIn.addQueryProcessIdentity("观察者");
				result = new ActionListByProcessIdentityNextWithFilter().execute(request, effectivePerson, id, count,
						wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件查询的中心工作[已归档],上一页", action = ActionListByProcessIdentityPrevWithFilter.class)
	@PUT
	@Path("archive/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyArchivePrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByProcessIdentityPrevWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
			if (wrapIn == null) {
				wrapIn = new WorkCommonSearchFilter();
			}

		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wrapIn.setProcessIdentities(null);
				wrapIn.setEmployeeNames(null);
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setTopUnitNames(null);
				wrapIn.setUnitNames(null);
				wrapIn.setInfoStatuses(null);
				wrapIn.addQueryInfoStatus("已归档");
				wrapIn.addQueryProcessIdentity("观察者");
				result = new ActionListByProcessIdentityPrevWithFilter().execute(request, effectivePerson, id, count,
						wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的中心工作,下一页", action = ActionListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListNextWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				result = new ActionListNextWithFilter().execute(request, effectivePerson, id, count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的中心工作,上一页", action = ActionListPrevWithFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListPrevWithFilter.Wo>> result = new ActionResult<>();
		WorkCommonSearchFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WorkCommonSearchFilter.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				result = new ActionListPrevWithFilter().execute(request, effectivePerson, id, count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}