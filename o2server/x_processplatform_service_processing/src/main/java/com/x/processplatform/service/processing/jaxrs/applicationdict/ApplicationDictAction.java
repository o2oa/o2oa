package com.x.processplatform.service.processing.jaxrs.applicationdict;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

@JaxrsDescribe("数据字典操作")
@Path("applicationdict")
public class ApplicationDictAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ApplicationDictAction.class);

	@JaxrsMethodDescribe(value = "更新数据字典以及数据.", action = ActionUpdate.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", action = ActionUpdateDataPath0.class)
	@PUT
	@Path("{id}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath0().execute(id, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据..", action = ActionUpdateDataPath1.class)
	@PUT
	@Path("{id}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath1().execute(id, path0, path1, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", action = ActionUpdateDataPath2.class)
	@PUT
	@Path("{id}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath2().execute(id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", action = ActionUpdateDataPath3.class)
	@PUT
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath3().execute(id, path0, path1, path2, path3, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", action = ActionUpdateDataPath4.class)
	@PUT
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath4().execute(id, path0, path1, path2, path3, path4, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", action = ActionUpdateDataPath5.class)
	@PUT
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath5().execute(id, path0, path1, path2, path3, path4, path5, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", action = ActionUpdateDataPath6.class)
	@PUT
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath6().execute(id, path0, path1, path2, path3, path4, path5, path6,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典和路径更新Application下的数据字典局部数据.", action = ActionUpdateDataPath7.class)
	@PUT
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath7().execute(id, path0, path1, path2, path3, path4, path5, path6, path7,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", action = ActionCreateDataPath0.class)
	@POST
	@Path("{id}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath0().execute(id, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", action = ActionCreateDataPath1.class)
	@POST
	@Path("{id}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("0级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath1().execute(id, path0, path1, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", action = ActionCreateDataPath2.class)
	@POST
	@Path("{id}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath2().execute(id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", action = ActionCreateDataPath3.class)
	@POST
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath3().execute(id, path0, path1, path2, path3, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", action = ActionCreateDataPath4.class)
	@POST
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath4().execute(id, path0, path1, path2, path3, path4, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", action = ActionCreateDataPath5.class)
	@POST
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath5().execute(id, path0, path1, path2, path3, path4, path5, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", action = ActionCreateDataPath6.class)
	@POST
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath6().execute(id, path0, path1, path2, path3, path4, path5, path6,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径添加Application下的新的局部数据.", action = ActionCreateDataPath7.class)
	@POST
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath7().execute(id, path0, path1, path2, path3, path4, path5, path6, path7,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", action = ActionDeleteDataPath0.class)
	@DELETE
	@Path("{id}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<ActionDeleteDataPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath0().execute(id, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", action = ActionDeleteDataPath1.class)
	@DELETE
	@Path("{id}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<ActionDeleteDataPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath1().execute(id, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", action = ActionDeleteDataPath2.class)
	@DELETE
	@Path("{id}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<ActionDeleteDataPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath2().execute(id, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", action = ActionDeleteDataPath3.class)
	@DELETE
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<ActionDeleteDataPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath3().execute(id, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", action = ActionDeleteDataPath4.class)
	@DELETE
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<ActionDeleteDataPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath4().execute(id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", action = ActionDeleteDataPath5.class)
	@DELETE
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<ActionDeleteDataPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath5().execute(id, path0, path1, path2, path3, path4, path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", action = ActionDeleteDataPath6.class)
	@DELETE
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<ActionDeleteDataPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath6().execute(id, path0, path1, path2, path3, path4, path5, path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据字典ID和路径删除Application下的数据字典局部数据.", action = ActionDeleteDataPath7.class)
	@DELETE
	@Path("{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<ActionDeleteDataPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath7().execute(id, path0, path1, path2, path3, path4, path5, path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}