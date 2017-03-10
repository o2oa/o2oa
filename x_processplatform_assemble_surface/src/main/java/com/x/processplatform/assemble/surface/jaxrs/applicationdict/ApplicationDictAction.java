package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.wrapin.element.WrapInApplicationDict;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplicationDict;

@Path("applicationdict")
public class ApplicationDictAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ApplicationDictAction.class);

	@HttpMethodDescribe(value = "获取单个数据字典以及数据字典数据.", response = WrapOutApplicationDict.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<WrapOutApplicationDict> result = new ActionResult<>();
		logger.debug("run get applicationDictFlag:{}, applicationFlag:{}.", applicationDictFlag, applicationFlag);
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, applicationDictFlag, applicationFlag);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新数据字典以及数据.", request = WrapInApplicationDict.class, response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, WrapInApplicationDict wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, applicationDictFlag, applicationFlag, wrapIn);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取Application的数据字典列表.", response = WrapOutApplicationDict.class)
	@GET
	@Path("list/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithApplication(@Context HttpServletRequest request,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<WrapOutApplicationDict>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithApplication().execute(applicationFlag);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getData(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetData().execute(applicationDictFlag, applicationFlag);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataPath0(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath0().execute(applicationDictFlag, applicationFlag, path0);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataPath1(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath1().execute(applicationDictFlag, applicationFlag, path0, path1);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataPath2(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath2().execute(applicationDictFlag, applicationFlag, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataPath3(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath3().execute(applicationDictFlag, applicationFlag, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataPath4(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath4().execute(applicationDictFlag, applicationFlag, path0, path1, path2, path3,
					path4);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataPath5(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath5().execute(applicationDictFlag, applicationFlag, path0, path1, path2, path3,
					path4, path5);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataPath6(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath6().execute(applicationDictFlag, applicationFlag, path0, path1, path2, path3,
					path4, path5, path6);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取Application下的数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataPath7(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath7().execute(applicationDictFlag, applicationFlag, path0, path1, path2, path3,
					path4, path5, path6, path7);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDataPath0(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath0().execute(applicationDictFlag, applicationFlag, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据..", response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDataPath1(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath1().execute(applicationDictFlag, applicationFlag, path0, path1,
					jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDataPath2(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath2().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDataPath3(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath3().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDataPath4(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath4().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDataPath5(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath5().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDataPath6(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath6().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5, path6, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDataPath7(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath7().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5, path6, path7, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataPath0(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath0().execute(applicationDictFlag, applicationFlag, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataPath1(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath1().execute(applicationDictFlag, applicationFlag, path0, path1,
					jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataPath2(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath2().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataPath3(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath3().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataPath4(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath4().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataPath5(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath5().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataPath6(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath6().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5, path6, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataPath7(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath7().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5, path6, path7, jsonElement);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteDataPath0(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath0().execute(applicationDictFlag, applicationFlag, path0);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteDataPath1(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath1().execute(applicationDictFlag, applicationFlag, path0, path1);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteDataPath2(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath2().execute(applicationDictFlag, applicationFlag, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteDataPath3(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath3().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteDataPath4(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath4().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteDataPath5(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath5().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteDataPath6(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath6().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5, path6);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteDataPath7(@Context HttpServletRequest request,
			@PathParam("applicationDictFlag") String applicationDictFlag,
			@PathParam("applicationFlag") String applicationFlag, @PathParam("path0") String path0,
			@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3,
			@PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath7().execute(applicationDictFlag, applicationFlag, path0, path1, path2,
					path3, path4, path5, path6, path7);
		} catch (Exception e) {
			logger.error(e,  effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}