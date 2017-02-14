package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;
import java.util.Map;

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

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInFilter;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.core.entity.element.ActivityType;

@Path("work")
public class WorkAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "根据Work Id获取组装的Work内容.", response = WrapOutMap.class)
	@GET
	@Path("{id}/complex")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response complex(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionComplex().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据Work Id获取组装的Work内容,同时装载Mobile使用的Form.", response = WrapOutMap.class)
	@GET
	@Path("{id}/complex/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response complexMobile(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionComplexMobile().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据Work Id获取组装的Work内容同时返回指定的Form,其中application和form可以指定id,name或者alias.", response = WrapOutMap.class)
	@GET
	@Path("{id}/complex/appoint/form/{formFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response complexAppointForm(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("formFlag") String formFlag) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionComplexAppointForm().execute(effectivePerson, id, formFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据Work Id获取组装的Work内容.", response = WrapOutMap.class)
	@GET
	@Path("{id}/complex/appoint//form/{formTag}/mobile")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response complexAppointFormMobile(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("formFlag") String formFlag) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionComplexAppointFormMobile().execute(effectivePerson, id, formFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定人员的Task,TaskCompleted,Read,ReadCompleted,Review.没有权限限制", response = WrapOutMap.class)
	@GET
	@Path("count/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countWithPerson(@Context HttpServletRequest request, @PathParam("credential") String credential) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionCountWithPerson().execute(credential);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Path("{id}/processing")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "尝试流转一个Work.", response = WrapOutId.class)
	public Response processing(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionProcessing().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("process/{processFlag}")
	@HttpMethodDescribe(value = "创建工作.", request = WrapInWork.class, response = WrapOutWorkLog.class)
	public Response create(@Context HttpServletRequest request, @PathParam("processFlag") String processFlag,
			WrapInWork wrapIn) {
		ActionResult<List<WrapOutWorkLog>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreate().execute(effectivePerson, processFlag, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除工作，需要应用管理权限或者是工作的创建者。", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	// @HttpMethodDescribe(value = "列示指定应用下当前用户创建的Work对象,下一页.", response =
	// WrapOutWork.class)
	// @GET
	// @Path("list/{id}/next/{count}")
	// @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	// @Consumes(MediaType.APPLICATION_JSON)
	// public Response listNext(@Context HttpServletRequest request,
	// @PathParam("id") String id,
	// @PathParam("count") Integer count, @PathParam("applicationFlag") String
	// applicationFlag) {
	// ActionResult<List<WrapOutWork>> result = new ActionResult<>();
	// try {
	// EffectivePerson effectivePerson = this.effectivePerson(request);
	// result = new ActionListNext().execute(effectivePerson, id, count,
	// applicationFlag);
	// } catch (Throwable th) {
	// th.printStackTrace();
	// result.error(th);
	// }
	// return ResponseFactory.getDefaultActionResultResponse(result);
	// }
	//
	// @HttpMethodDescribe(value = "列示指定应用下当前用户创建的Work对象,上一页.", response =
	// WrapOutWork.class)
	// @GET
	// @Path("list/{id}/prev/{count}")
	// @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	// @Consumes(MediaType.APPLICATION_JSON)
	// public Response listPrev(@Context HttpServletRequest request,
	// @PathParam("id") String id,
	// @PathParam("count") Integer count, @PathParam("applicationFlag") String
	// applicationFlag) {
	// ActionResult<List<WrapOutWork>> result = new ActionResult<>();
	// try {
	// EffectivePerson effectivePerson = this.effectivePerson(request);
	// result = new ActionListPrev().execute(effectivePerson, id, count,
	// applicationFlag);
	// } catch (Throwable th) {
	// th.printStackTrace();
	// result.error(th);
	// }
	// return ResponseFactory.getDefaultActionResultResponse(result);
	// }

	@HttpMethodDescribe(value = "列示指定应用当前用户创建的Work对象,下一页.", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/next/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithApplication(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListNextWithApplication().execute(effectivePerson, id, count, applicationFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定应用当前用户创建的Work对象,上一页.", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/prev/{count}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithApplication(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListPrevWithApplication().execute(effectivePerson, id, count, applicationFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定流程当前用户创建的Work对象,下一页.", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/next/{count}/process/{processFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithProcess(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("processFlag") String processFlag) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListNextWithProcess().execute(effectivePerson, id, count, processFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定流程当前用户创建的Work对象,上一页.", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/prev/{count}/process/{processFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithProcess(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("processFlag") String processFlag) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListPrevWithProcess().execute(effectivePerson, id, count, processFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "统计当前用户创建的Work，按应用分类.", response = WrapOutWork.class)
	@GET
	@Path("list/count/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countWithApplication(@Context HttpServletRequest request) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListCountWithApplication().execute(effectivePerson);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "统计当前用户在指定应用下的待办，按流程分类.", response = WrapOutWork.class)
	@GET
	@Path("list/count/application/{applicationFlag}/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCountWithProcess(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListCountWithProcess().execute(effectivePerson, applicationFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取用于过滤的可选属性值,限定范围为我创建的工作", response = WrapOutMap.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("filter/attribute/application/{applicationFlag}")
	public Response filterAttribute(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<Map<String, List<NameValueCountPair>>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionFilterAttribute().execute(effectivePerson, applicationFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Work,下一页,限定范围为我创建的工作.", response = WrapOutWork.class, request = WrapInFilter.class)
	@POST
	@Path("list/{id}/next/{count}/application/{applicationFlag}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag,
			WrapInFilter wrapIn) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListNextWithFilter().execute(effectivePerson, id, count, applicationFlag, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Work,上一页,限定范围为我创建的工作.", response = WrapOutWork.class, request = WrapInFilter.class)
	@POST
	@Path("list/{id}/prev/{count}/application/{applicationFlag}/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag,
			WrapInFilter wrapIn) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListPrevWithFilter().execute(effectivePerson, id, count, applicationFlag, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "将工作召回。", response = WrapOutId.class)
	@PUT
	@Path("{id}/retract")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response retract(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionRetract().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "将工作召回。", response = WrapOutId.class)
	@PUT
	@Path("{id}/reroute/activity/{activityId}/activitytype/{activityType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reroute(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("activityId") String activityId, @PathParam("activityType") ActivityType activityType) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionReroute().execute(effectivePerson, id, activityId, activityType);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定应用不带权限区分的所有Work,下一页.", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/next/{count}/application/{applicationFlag}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageListNext().execute(effectivePerson, id, count, applicationFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定应用不带权限区分的所有Work,上一页.", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/prev/{count}/application/{applicationFlag}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageListPrev().execute(effectivePerson, id, count, applicationFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取工作内容.", response = WrapOutId.class)
	@GET
	@Path("{id}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageGet(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutWork> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageGet().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定Work下所有的待办已办,待阅已阅和参阅.", response = WrapOutMap.class)
	@GET
	@Path("{id}/assignment/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageGetAssignment(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageGetAssignment().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定application下根据porcess分类的work数量.", response = WrapOutWork.class)
	@GET
	@Path("list/count/application/{applicationFlag}/process/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListCountWithProcess(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageListCountWithProcess().execute(effectivePerson, applicationFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示关联的Work对象.", response = WrapOutWork.class)
	@GET
	@Path("{id}/relative/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListRelative(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageListRelative().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除单个工作.", response = WrapOutId.class)
	@DELETE
	@Path("{id}/single/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageDeleteSingleWork(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<List<WrapOutId>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageDeleteSingleWork().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除所有相关联的工作.", response = WrapOutId.class)
	@DELETE
	@Path("{id}/relative/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageDeleteRelativeWork(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<List<WrapOutId>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageDeleteRelativeWork().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageFilterAttribute().execute(effectivePerson, applicationFlag);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Work,下一页.", response = WrapOutWork.class, request = WrapInFilter.class)
	@POST
	@Path("list/{id}/next/{count}/application/{applicationFlag}/filter/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag,
			WrapInFilter wrapIn) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageListNextWithFilter().execute(effectivePerson, id, count, applicationFlag, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Work,上一页.", response = WrapOutWork.class, request = WrapInFilter.class)
	@POST
	@Path("list/{id}/prev/{count}/application/{applicationFlag}/filter/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manageListPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("applicationFlag") String applicationFlag,
			WrapInFilter wrapIn) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ManageListPrevWithFilter().execute(effectivePerson, id, count, applicationFlag, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}