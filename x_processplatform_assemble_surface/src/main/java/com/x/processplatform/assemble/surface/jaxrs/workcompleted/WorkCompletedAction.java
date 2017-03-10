package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInFilter;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTaskCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;

@Path("workcompleted")
public class WorkCompletedAction extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(WorkCompletedAction.class);

	@HttpMethodDescribe(value = "列示当前用户在指定的application下创建的WorkCompleted对象,下一页.", response = WrapOutWorkCompleted.class)
	@GET
	@Path("list/{id}/next/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithApplication(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNextWithApplication().execute(effectivePerson, id, count, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户在指定的application下创建的WorkCompleted对象,上一页.", response = WrapOutWorkCompleted.class)
	@GET
	@Path("list/{id}/prev/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithApplication(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrevWithApplication().execute(effectivePerson, id, count, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取复合的WorkCompleted.", response = WrapOutMap.class)
	@GET
	@Path("{id}/complex")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getComplex(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionComplex().execute(id, effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取复合的WorkCompleted.", response = WrapOutMap.class)
	@GET
	@Path("{id}/complex/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getComplexMobile(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionComplexMobile().execute(id, effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取复合的WorkCompleted，使用最后记录的表单。", response = WrapOutMap.class)
	@GET
	@Path("{id}/complex/snap/form")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getComplexSnapForm(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionComplexSnapForm().execute(id, effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取复合的WorkCompleted，使用最后记录的Mobile表单。", response = WrapOutMap.class)
	@GET
	@Path("{id}/complex/snap/form/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getComplexMobileSnapForm(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionComplexSnapForm().execute(id, effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "统计当前用户创建的WorkCompleted，按应用分类.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countWithApplication(@Context HttpServletRequest request) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		List<NameValueCountPair> wraps = new ArrayList<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListCountWithApplication().execute(effectivePerson);
			result.setData(wraps);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "统计当前用户在指定应用下的WorkCompleted，按流程分类.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/application/{applicationFlag}/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countWithProcess(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		List<NameValueCountPair> wraps = new ArrayList<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListCountWithProcess().execute(effectivePerson, applicationFlag);
			result.setData(wraps);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取用于过滤的可选属性值", response = NameValueCountPair.class)
	@GET
	@Path("filter/attribute/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterAttribute(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<Map<String, List<NameValueCountPair>>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFilterAttribute().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据过滤属性列示WorkCompleted,下一页.", response = WrapOutWorkCompleted.class)
	@POST
	@Path("list/{id}/next/{count}/application/{applicationFlag}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNextWithFilter().execute(effectivePerson, id, count, applicationFlag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据过滤属性列示WorkCompleted,上一页.", response = WrapOutTaskCompleted.class)
	@POST
	@Path("list/{id}/prev/{count}/application/{applicationFlag}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrevWithFilter().execute(effectivePerson, id, count, applicationFlag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取工作内容.", response = WrapOutId.class)
	@GET
	@Path("{id}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageGet(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutWorkCompleted> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ManageGet().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "如果是xadmin或者是ProcessPlatformManager或Manager角色那么显示这个应用下的所有WorkCompleted,如果是Process的管理员,那么显示Process下的WorkCompleted.下一页.", response = WrapOutWorkCompleted.class)
	@GET
	@Path("list/{id}/next/{count}/application/{applicationFlag}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListNext(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ManageListNext().execute(effectivePerson, id, count, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户创建的WorkCompleted对象,上一页.", response = WrapOutWorkCompleted.class)
	@GET
	@Path("list/{id}/prev/{count}/application/{applicationFlag}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListPrev(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ManageListPrev().execute(effectivePerson, id, count, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定application下根据porcess分类的workCompleted数量.", response = NameValueCountPair.class)
	@GET
	@Path("list/count/application/{applicationFlag}/process/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListCountWithProcess(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ManageListCountWithProcess().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定Work下所有的待办已办,待阅已阅和参阅.", response = WrapOutMap.class)
	@GET
	@Path("{id}/assignment/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageGetAssignment(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ManageGetAssignment().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除所有相关数据.", response = WrapOutId.class)
	@DELETE
	@Path("{id}/delete/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageDelete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<List<WrapOutId>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ManageDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取用于过滤的可选属性值", response = WrapOutMap.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("filter/attribute/application/{applicationFlag}/manage")
	public Response manageFilterAttribute(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<Map<String, List<NameValueCountPair>>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ManageFilterAttribute().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的WorkCompleted,下一页.", response = WrapOutWorkCompleted.class, request = WrapInFilter.class)
	@POST
	@Path("list/{id}/next/{count}/application/{applicationFlag}/filter/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ManageListNextFilter().execute(effectivePerson, id, count, applicationFlag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Work,上一页.", response = WrapOutWorkCompleted.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/prev/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try  {
			result = new ManageListPrevFilter().execute(effectivePerson, id, count, applicationFlag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}