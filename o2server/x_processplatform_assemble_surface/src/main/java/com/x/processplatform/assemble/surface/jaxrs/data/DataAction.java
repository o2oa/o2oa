package com.x.processplatform.assemble.surface.jaxrs.data;

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

@Path("data")
@JaxrsDescribe("数据操作")
public class DataAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(DataAction.class);

	@JaxrsMethodDescribe(value = "根据job获取Data", action = ActionGetWithJob.class)
	@GET
	@Path("job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("job标识") @PathParam("job") String job) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJob().execute(effectivePerson, job);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithJobPath0.class)
	@GET
	@Path("job/{job}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath0().execute(effectivePerson, job, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithJobPath1.class)
	@GET
	@Path("job/{job}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath1().execute(effectivePerson, job, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithJobPath2.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath2().execute(effectivePerson, job, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithJobPath3.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath3().execute(effectivePerson, job, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithJobPath4.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath4().execute(effectivePerson, job, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithJobPath5.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath5().execute(effectivePerson, job, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithJobPath6.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath6().execute(effectivePerson, job, path0, path1, path2, path3, path4, path5,
					path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithJobPath7.class)
	@GET
	@Path("job/{job}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("job") String job,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath7().execute(effectivePerson, job, path0, path1, path2, path3, path4, path5,
					path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据workId获取Data", action = ActionGetWithWork.class)
	@GET
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWork().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath0.class)
	@GET
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath1.class)
	@GET
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath2.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath3.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath4.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath5.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath6.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4, path5,
					path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定work的data数据.", action = ActionGetWithWorkPath7.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4, path5,
					path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompleted.class)
	@GET
	@Path("workcompleted/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompleted().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath0.class)
	@GET
	@Path("workcompleted/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath1.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath2.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath3.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath4.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath4().execute(effectivePerson, id, path0, path1, path2, path3,
					path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath5.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath5().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath6.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath6().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", action = ActionGetWithWorkCompletedPath7.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompletedPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("已完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompletedPath7().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWork.class)
	@PUT
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWork().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWorkPath0.class)
	@PUT
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWorkPath1.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWorkPath2.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWorkPath3.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWorkPath4.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWorkPath5.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWorkPath6.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定Work的Data数据.", action = ActionUpdateWithWorkPath7.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompleted.class)
	@PUT
	@Path("workcompleted/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompleted().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompletedPath0.class)
	@PUT
	@Path("workcompleted/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompletedPath1.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompletedPath2.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath2().execute(effectivePerson, id, path0, path1, path2,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompletedPath3.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompletedPath4.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath4().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompletedPath5.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath5().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompletedPath6.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath6().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新指定WorkCompleted的Data数据.", action = ActionUpdateWithWorkCompletedPath7.class)
	@PUT
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithWorkCompletedPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("完成工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithWorkCompletedPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithWorkCompletedPath7().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, path7, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWork.class)
	@POST
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWork().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath0.class)
	@POST
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath1.class)
	@POST
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath2.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath3.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath4.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath5.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath6.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work添加局部data数据.", action = ActionCreateWithWorkPath7.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithWorkPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionCreateWithWorkPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWork.class)
	@DELETE
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("id") String id) {
		ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWork().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWorkPath0.class)
	@DELETE
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<ActionDeleteWithWorkPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWorkPath1.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<ActionDeleteWithWorkPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWorkPath2.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<ActionDeleteWithWorkPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWorkPath3.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<ActionDeleteWithWorkPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWorkPath4.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<ActionDeleteWithWorkPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWorkPath5.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<ActionDeleteWithWorkPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWorkPath6.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<ActionDeleteWithWorkPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "对指定的work删除局部data数据.", action = ActionDeleteWithWorkPath7.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<ActionDeleteWithWorkPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}