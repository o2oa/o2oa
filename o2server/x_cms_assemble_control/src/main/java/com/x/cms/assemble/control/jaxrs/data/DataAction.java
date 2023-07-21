package com.x.cms.assemble.control.jaxrs.data;

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
@JaxrsDescribe("CMS文档数据操作")
public class DataAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(DataAction.class);

	@JaxrsMethodDescribe(value = "根据documentId获取Data", action = ActionGetWithDocument.class)
	@GET
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocument(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文档ID") @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithDocument().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定document的data数据.", action = ActionGetWithDocumentPath0.class)
	@GET
	@Path("document/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocumentPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithDocumentPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定document的data数据.", action = ActionGetWithDocumentPath1.class)
	@GET
	@Path("document/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocumentWithPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithDocumentPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定document的data数据.", action = ActionGetWithDocumentPath2.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocumentWithPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithDocumentPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定document的data数据.", action = ActionGetWithDocumentPath3.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocumentWithPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithDocumentPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定document的data数据.", action = ActionGetWithDocumentPath4.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocumentWithPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithDocumentPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定document的data数据.", action = ActionGetWithDocumentPath5.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocumentWithPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithDocumentPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定document的data数据.", action = ActionGetWithDocumentPath6.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocumentWithPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
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
			result = new ActionGetWithDocumentPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4, path5,
					path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取指定document的data数据.", action = ActionGetWithDocumentPath7.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithDocumentWithPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
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
			result = new ActionGetWithDocumentPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4, path5,
					path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocument.class)
	@PUT
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocument(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文档ID") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocument.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocument().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocument.class)
	@POST
	@Path("document/{id}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								   @JaxrsParameterDescribe("文档ID") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocument.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocument().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath0.class)
	@PUT
	@Path("document/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath0.class)
	@POST
	@Path("document/{id}/{path0}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath0MockPutToPost(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath1.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath1.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath1MockPutToPost(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath2.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath2.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath2MockPutToPost(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath3.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath3.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath3MockPutToPost(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath4.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath4.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath4MockPutToPost(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
											@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath5.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath5.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath5MockPutToPost(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
											@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
											@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath6.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath6.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath6MockPutToPost(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
											@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
											@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
											@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath7.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定Document的Data数据.", action = ActionUpdateWithDocumentPath7.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateWithDocumentWithPath7MockPutToPost(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
											@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
											@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
											@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
											@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateWithDocumentPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateWithDocumentPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocument.class)
	@POST
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocument(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文档ID") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocument.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocument().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocumentPath0.class)
	@POST
	@Path("document/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocumentWithPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocumentPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocumentPath0().execute(effectivePerson, id, path0, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocumentPath1.class)
	@POST
	@Path("document/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocumentWithPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocumentPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocumentPath1().execute(effectivePerson, id, path0, path1, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocumentPath2.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocumentWithPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocumentPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocumentPath2().execute(effectivePerson, id, path0, path1, path2, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocumentPath3.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocumentWithPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocumentPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocumentPath3().execute(effectivePerson, id, path0, path1, path2, path3,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocumentPath4.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocumentWithPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocumentPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocumentPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocumentPath5.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocumentWithPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocumentPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocumentPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocumentPath6.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocumentWithPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocumentPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocumentPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document添加局部data数据.", action = ActionCreateWithDocumentPath7.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createWithDocumentWithPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionCreateWithDocumentPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateWithDocumentPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocument.class)
	@DELETE
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocument(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文档ID") @PathParam("id") String id) {
		ActionResult<ActionDeleteWithDocument.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocument().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocument.class)
	@GET
	@Path("document/{id}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentMockDeleteToGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								   @JaxrsParameterDescribe("文档ID") @PathParam("id") String id) {
		ActionResult<ActionDeleteWithDocument.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocument().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath0.class)
	@DELETE
	@Path("document/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath0(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<ActionDeleteWithDocumentPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath0.class)
	@GET
	@Path("document/{id}/{path0}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath0MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<ActionDeleteWithDocumentPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath0().execute(effectivePerson, id, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath1.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath1(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<ActionDeleteWithDocumentPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath1.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath1MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<ActionDeleteWithDocumentPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath1().execute(effectivePerson, id, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath2.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath2(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<ActionDeleteWithDocumentPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath2.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath2MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<ActionDeleteWithDocumentPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath2().execute(effectivePerson, id, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath3.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath3(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<ActionDeleteWithDocumentPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath3.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath3MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<ActionDeleteWithDocumentPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath3().execute(effectivePerson, id, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath4.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath4(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<ActionDeleteWithDocumentPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath4.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath4MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
											@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<ActionDeleteWithDocumentPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath4().execute(effectivePerson, id, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath5.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath5(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<ActionDeleteWithDocumentPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath5.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath5MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
											@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
											@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<ActionDeleteWithDocumentPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath5().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath6.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath6(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<ActionDeleteWithDocumentPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath6.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath6MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
											@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
											@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
											@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<ActionDeleteWithDocumentPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath6().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath7.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath7(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<ActionDeleteWithDocumentPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对指定的document删除局部data数据.", action = ActionDeleteWithDocumentPath7.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithDocumentWithPath7MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
											@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
											@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
											@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
											@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
											@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
											@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
											@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<ActionDeleteWithDocumentPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithDocumentPath7().execute(effectivePerson, id, path0, path1, path2, path3, path4,
					path5, path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
