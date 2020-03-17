package com.x.mind.assemble.control.jaxrs.mind;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.proxy.StandardJaxrsActionProxy;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.ThisApplication;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("mind")
@JaxrsDescribe("脑图信息管理服务")
public class MindInfoAction extends BaseAction {

	private StandardJaxrsActionProxy proxy = new StandardJaxrsActionProxy(ThisApplication.context());
	private Logger logger = LoggerFactory.getLogger(MindInfoAction.class);

	@JaxrsMethodDescribe(value = "根据ID获取脑图信息", action = ActionMindGetWithId.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getMindBaseInfoWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindGetWithId.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindGetWithId) proxy.getProxy(ActionMindGetWithId.class)).execute(request, id,
					effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取脑图缩略图", action = ActionMindIconGetWithId.class)
	@GET
	@Path("{id}/icon")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getMindIconWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindIconGetWithId.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindIconGetWithId) proxy.getProxy(ActionMindIconGetWithId.class)).execute(request,
					effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据脑图ID获取脑图所有的版本信息", action = ActionListVersionsWithMindId.class)
	@GET
	@Path("list/{id}/version")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listVersionsWithMindId(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("脑图ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListVersionsWithMindId.Wo>> result = new ActionResult<>();
		try {
			result = ((ActionListVersionsWithMindId) proxy.getProxy(ActionListVersionsWithMindId.class))
					.execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据脑图ID获取脑图所有的分享信息", action = ActionListShareRecordsWithMindId.class)
	@GET
	@Path("list/{id}/shareRecords")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listShareRecordsWithMindId(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("脑图ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListShareRecordsWithMindId.Wo>> result = new ActionResult<>();
		try {
			result = ((ActionListShareRecordsWithMindId) proxy.getProxy(ActionListShareRecordsWithMindId.class))
					.execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示符合过滤条件的脑图信息内容, 下一页.", action = ActionMyShareMindNextWithFilter.class)
	@PUT
	@Path("filter/shared/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMySharedMindNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionMyShareMindNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = ((ActionMyShareMindNextWithFilter) proxy.getProxy(ActionMyShareMindNextWithFilter.class))
					.execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示符合过滤条件的脑图信息内容, 下一页.", action = ActionMyRecycleNextWithFilter.class)
	@PUT
	@Path("filter/recycle/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyRecycleNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionMyRecycleNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = ((ActionMyRecycleNextWithFilter) proxy.getProxy(ActionMyRecycleNextWithFilter.class))
					.execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示符合过滤条件的脑图信息内容, 下一页.", action = ActionMyReciveMindNextWithFilter.class)
	@PUT
	@Path("filter/recived/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyRecivedMindNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionMyReciveMindNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = ((ActionMyReciveMindNextWithFilter) proxy.getProxy(ActionMyReciveMindNextWithFilter.class))
					.execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取脑图信息，包括脑图内容", action = ActionMindViewWithId.class)
	@GET
	@Path("view/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void viewMindWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindViewWithId.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindViewWithId) proxy.getProxy(ActionMindViewWithId.class)).execute(request, id,
					effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据历史版本信息ID获取脑图信息，包括脑图内容", action = ActionMindVersionViewWithId.class)
	@GET
	@Path("version/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void viewMindVersionWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图历史版本信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindVersionViewWithId.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindVersionViewWithId) proxy.getProxy(ActionMindVersionViewWithId.class)).execute(request,
					id, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "保存(创建或者更新)一个脑图信息", action = ActionMindSave.class)
	@POST
	@Path("save")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveMind(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindSave.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindSave) proxy.getProxy(ActionMindSave.class)).execute(request, effectivePerson,
					jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传或者替换栏目的图标内容，可以指定压缩大小	.", action = ActionMindIconUpdate.class)
	@POST
	@Path("{mindId}/icon/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void changeMindIcon(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图信息ID") @PathParam("mindId") String mindId,
			@JaxrsParameterDescribe("最大宽度") @PathParam("size") Integer size,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionMindIconUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = ((ActionMindIconUpdate) proxy.getProxy(ActionMindIconUpdate.class)).execute(request,
					effectivePerson, mindId, size, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示符合过滤条件的脑图信息内容, 下一页.", action = ActionMindListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionMindListNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = ((ActionMindListNextWithFilter) proxy.getProxy(ActionMindListNextWithFilter.class))
					.execute(request, id, count, jsonElement, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID从回收站还原脑图信息", action = ActionMindRestore.class)
	@GET
	@Path("restore/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void restoreMindWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("回收站脑图ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindRestore.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindRestore) proxy.getProxy(ActionMindRestore.class)).execute(request, effectivePerson,
					id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "将指定的脑图分享给其他用户或者组织", action = ActionMindShare.class)
	@PUT
	@Path("share/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void shareMindWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图ID") @PathParam("id") String id, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindShare.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindShare) proxy.getProxy(ActionMindShare.class)).execute(request, effectivePerson, id,
					jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据分享记录ID取消对脑图的分享", action = ActionMindShareCancel.class)
	@PUT
	@Path("share/{shareId}/cancel")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void shareCancelWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图分享信息ID") @PathParam("shareId") String shareId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindShareCancel.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindShareCancel) proxy.getProxy(ActionMindShareCancel.class)).execute(request,
					effectivePerson, shareId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据脑图文件ID删除脑图信息", action = ActionMindDestroyFromNormal.class)
	@DELETE
	@Path("{id}/destorymind")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destoryFromNormal(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindDestroyFromNormal.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindDestroyFromNormal) proxy.getProxy(ActionMindDestroyFromNormal.class)).execute(request,
					effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "从回收站根据信息ID删除脑图信息", action = ActionMindDestroyFromRecycle.class)
	@DELETE
	@Path("{recycleId}/destoryrecycle")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destoryFromRecycle(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("回收站脑图ID") @PathParam("recycleId") String recycleId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindDestroyFromRecycle.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindDestroyFromRecycle) proxy.getProxy(ActionMindDestroyFromRecycle.class))
					.execute(request, effectivePerson, recycleId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "将指定的脑图放入回收站", action = ActionMindRecycle.class)
	@DELETE
	@Path("recycle/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void recycleMindWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("脑图ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionMindRecycle.Wo> result = new ActionResult<>();
		try {
			result = ((ActionMindRecycle) proxy.getProxy(ActionMindRecycle.class)).execute(request, effectivePerson,
					id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}