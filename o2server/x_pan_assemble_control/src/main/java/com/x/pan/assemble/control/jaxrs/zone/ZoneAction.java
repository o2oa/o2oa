package com.x.pan.assemble.control.jaxrs.zone;

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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author sword
 */
@Path("zone")
@JaxrsDescribe("共享区")
public class ZoneAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ZoneAction.class);

	@JaxrsMethodDescribe(value = "获取指定共享区信息。", action = ActionGet.class)
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

	@JaxrsMethodDescribe(value = "创建共享区,不能重名.", action = ActionCreate.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
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

	@JaxrsMethodDescribe(value = "更新共享区名称或描述,不能重名.", action = ActionUpdate.class)
	@POST
	@Path("{id}/update")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                    @JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
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

	@JaxrsMethodDescribe(value = "删除共享区,同时删除所有下属的文件和文件夹.", action = ActionDelete.class)
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

	@JaxrsMethodDescribe(value = "获取共享区列表。", action = ActionList.class)
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

	@JaxrsMethodDescribe(value = "获取共享区所有文件并打包为zip，输出流.", action = ActionDownload.class)
	@GET
	@Path("{id}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						 @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "保存共享区权限", action = ActionSavePermission.class)
	@POST
	@Path("{id}/save/permission")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void savePermission(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					   @JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionSavePermission.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSavePermission().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除共享区权限", action = ActionDeletePermission.class)
	@POST
	@Path("{id}/delete/permission")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deletePermission(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionDeletePermission.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeletePermission().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取所有共享区列表(网盘管理员查询)。", action = ActionManagerList.class)
	@GET
	@Path("list/manager")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionManagerList.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerList().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "重置共享区所有子目录权限(仅网盘管理员可操作)。", action = ActionResetPermission.class)
	@POST
	@Path("{id}/reset/permission")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void resetPermission(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionResetPermission.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionResetPermission().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}


}
