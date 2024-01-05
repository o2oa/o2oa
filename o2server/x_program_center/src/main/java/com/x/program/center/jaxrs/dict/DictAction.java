package com.x.program.center.jaxrs.dict;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * @author sword
 */
@Path("dict")
@JaxrsDescribe("数据字典")
public class DictAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(DictAction.class);

	@JaxrsMethodDescribe(value = "获取数据字典列表.", action = ActionList.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void list(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionList.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionList().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取单个数据字典以及数据字典数据.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建数据字典以及数据.", action = ActionCreate.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void post(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreate().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新数据字典以及数据.", action = ActionEdit.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void put(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除指定的数据字典以及数据字典数据.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页列示数据字典对象.", action = ActionListPaging.class)
	@POST
	@Path("list/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							 @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
							 @JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size) {
		ActionResult<List<ActionListPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPaging().execute(effectivePerson, page, size);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取数据字典数据.", action = ActionGetData.class)
	@GET
	@Path("{dictFlag}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						@JaxrsParameterDescribe("数据字典标识") @PathParam("dictFlag") String dictFlag) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetData().execute(effectivePerson, dictFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取数据字典数据.", action = ActionGetDataPath.class)
	@GET
	@Path("{dictFlag}/{path}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							@JaxrsParameterDescribe("数据字典标识") @PathParam("dictFlag") String dictFlag,
							@JaxrsParameterDescribe("路径(多层路径以.分割，如person.name)") @PathParam("path") String path) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath().execute(effectivePerson, dictFlag, path);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径更新数据字典数据.", action = ActionUpdateDataPath.class)
	@PUT
	@Path("{dictFlag}/{path}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("数据字典标识") @PathParam("dictFlag") String dictFlag,
							   @JaxrsParameterDescribe("路径(多层路径以.分割，如person.name)") @PathParam("path") String path,
							   JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath().execute(effectivePerson, dictFlag, path, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径更新数据字典数据(mock put to post).", action = ActionUpdateDataPath.class)
	@POST
	@Path("{dictFlag}/{path}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPathMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									@JaxrsParameterDescribe("数据字典标识") @PathParam("dictFlag") String dictFlag,
									@JaxrsParameterDescribe("路径(多层路径以.分割，如person.name)") @PathParam("path") String path, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath().execute(effectivePerson, dictFlag, path, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径添加数据字典数据.", action = ActionCreateDataPath.class)
	@POST
	@Path("{dictFlag}/{path}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("数据字典标识") @PathParam("dictFlag") String dictFlag,
							   @JaxrsParameterDescribe("路径(多层路径以.分割，如person.name)") @PathParam("path") String path, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath().execute(effectivePerson, dictFlag, path, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径删除数据字典数据.", action = ActionDeleteDataPath.class)
	@DELETE
	@Path("{dictFlag}/{path}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("数据字典标识") @PathParam("dictFlag") String dictFlag,
							   @JaxrsParameterDescribe("路径(多层路径以.分割，如person.name)") @PathParam("path") String path) {
		ActionResult<ActionDeleteDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath().execute(effectivePerson, dictFlag, path);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径删除数据字典数据(mock delete to get).", action = ActionDeleteDataPath.class)
	@GET
	@Path("{dictFlag}/{path}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPathMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
											  @Context HttpServletRequest request,
											  @JaxrsParameterDescribe("数据字典标识") @PathParam("dictFlag") String dictFlag,
											  @JaxrsParameterDescribe("路径(多层路径以.分割，如person.name)") @PathParam("path") String path) {
		ActionResult<ActionDeleteDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath().execute(effectivePerson, dictFlag, path);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
