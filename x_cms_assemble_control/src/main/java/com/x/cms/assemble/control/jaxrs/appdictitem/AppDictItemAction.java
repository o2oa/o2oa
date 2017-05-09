package com.x.cms.assemble.control.jaxrs.appdictitem;

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
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.jaxrs.appdictitem.exception.AppDictItemDeleteException;
import com.x.cms.assemble.control.jaxrs.appdictitem.exception.AppDictItemGetException;
import com.x.cms.assemble.control.jaxrs.appdictitem.exception.AppDictItemSaveException;
import com.x.cms.assemble.control.jaxrs.appdictitem.exception.AppDictItemUpdateException;

@Path("appdictitem")
public class AppDictItemAction extends StandardJaxrsAction {
	
	private Logger logger = LoggerFactory.getLogger( AppDictItemAction.class );
	
	@HttpMethodDescribe(value = "根据路径获取App数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItem( @Context HttpServletRequest request, @PathParam("appDictId") String appDictId, @PathParam("appId") String appId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath0(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId, path0);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId, path0 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath1(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId, path0, path1 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId, path0, path1 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath2(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId, path0, path1, path2 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId, path0, path1, path2 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath3(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId, path0, path1, path2, path3 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId, path0, path1, path2, path3 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath4(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId, path0, path1, path2, path3, path4 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId, path0, path1, path2, path3, path4 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath5(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId, path0, path1, path2, path3, path4, path5 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId, path0, path1, path2, path3, path4, path5 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath6(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId, path0, path1, path2, path3, path4, path5, path6 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId, path0, path1, path2, path3, path4, path5, path6 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath7(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appId, appDictId, path0, path1, path2, path3, path4, path5, path6, path7 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemGetException( e, appDictId, path0, path1, path2, path3, path4, path5, path6, path7 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath0(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemUpdateException( e, appDictId, path0 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath1(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemUpdateException( e, appDictId, path0, path1);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath2(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemUpdateException( e, appDictId, path0, path1, path2);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath3(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemUpdateException( e, appDictId, path0, path1, path2, path3);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath4(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemUpdateException( e, appDictId, path0, path1, path2, path3, path4);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath5(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemUpdateException( e, appDictId, path0, path1, path2, path3, path4, path5);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath6(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemUpdateException( e, appDictId, path0, path1, path2, path3, path4, path5, path6);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath7(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemUpdateException( e, appDictId, path0, path1, path2, path3, path4, path5, path6, path7);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath0(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemSaveException( e, appDictId, path0 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath1(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemSaveException( e, appDictId, path0, path1);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath2(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemSaveException( e, appDictId, path0, path1, path2);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath3(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemSaveException( e, appDictId, path0, path1, path2, path3);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath4(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemSaveException( e, appDictId, path0, path1, path2, path3, path4);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath5(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemSaveException( e, appDictId, path0, path1, path2, path3, path4, path5);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath6(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemSaveException( e, appDictId, path0, path1, path2, path3, path4, path5, path6);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath7(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteSave().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemSaveException( e, appDictId, path0, path1, path2, path3, path4, path5, path6, path7);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath0(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appId, appDictId, jsonElement, path0 );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemDeleteException( e, appDictId, path0 );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath1(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemDeleteException( e, appDictId, path0, path1);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath2(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemDeleteException( e, appDictId, path0, path1, path2);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath3(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemDeleteException( e, appDictId, path0, path1, path2, path3);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath4(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemDeleteException( e, appDictId, path0, path1, path2, path3, path4);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath5(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemDeleteException( e, appDictId, path0, path1, path2, path3, path4, path5);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath6(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemDeleteException( e, appDictId, path0, path1, path2, path3, path4, path5, path6);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath7(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictItemDeleteException( e, appDictId, path0, path1, path2, path3, path4, path5, path6, path7);
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	
}