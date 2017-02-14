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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;

@Path("data")
public class DataAction extends AbstractJaxrsAction {

	@HttpMethodDescribe(value = "根据workId获取Data", response = JsonElement.class)
	@GET
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWork(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWork().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定work的data数据.", response = JsonElement.class)
	@GET
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkPath0(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkPath0().execute(effectivePerson, id, path0);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定work的data数据.", response = JsonElement.class)
	@GET
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkWithPath1(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkPath1().execute(effectivePerson, id, path0, path1);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定work的data数据.", response = JsonElement.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkWithPath2(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定work的data数据.", response = JsonElement.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkWithPath3(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定work的data数据.", response = JsonElement.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定work的data数据.", response = JsonElement.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定work的data数据.", response = JsonElement.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4, path5,
					path6);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定work的data数据.", response = JsonElement.class)
	@GET
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4, path5,
					path6, path7);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompleted(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompleted().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompletedWithPath0(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompletedPath0().execute(effectivePerson, id, path0);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompletedWithPath1(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompletedPath1().execute(effectivePerson, id, path0, path1);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompletedWithPath2(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompletedPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompletedWithPath3(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompletedPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompletedWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompletedPath4().execute(effectivePerson, id, path0, path1, path2, path3,
					path4);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompletedWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompletedPath5().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompletedWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompletedPath6().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定workCompleted的data数据.", response = JsonElement.class)
	@GET
	@Path("workcompleted/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompletedWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompletedPath7().execute(effectivePerson, id, path0, path1, path2, path3,
					path4, path5, path6, path7);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWork(@Context HttpServletRequest request, @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWork().execute(effectivePerson, id, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWorkWithPath0(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWorkPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWorkWithPath1(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWorkPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWorkWithPath2(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWorkPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWorkWithPath3(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWorkWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWorkWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWorkWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Work的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWithWorkWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdateWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithWork(@Context HttpServletRequest request, @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWork().execute(effectivePerson, id, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithJobWithPath0(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWorkPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithJobWithPath1(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWorkPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithJobWithPath2(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWorkPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithJobWithPath3(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithJobWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithJobWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithJobWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWithJobWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreateWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithWork(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWork().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithJobWithPath0(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWorkPath0().execute(effectivePerson, id, path0);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithJobWithPath1(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWorkPath1().execute(effectivePerson, id, path0, path1);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithJobWithPath2(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWorkPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithJobWithPath3(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWorkPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithJobWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWorkPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithJobWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWorkPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithJobWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWorkPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的work删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("work/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithJobWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDeleteWithWorkPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}