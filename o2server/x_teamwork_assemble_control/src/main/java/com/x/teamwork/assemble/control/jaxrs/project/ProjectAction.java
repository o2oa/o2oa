package com.x.teamwork.assemble.control.jaxrs.project;

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

@Path("project")
@JaxrsDescribe("项目信息管理")
public class ProjectAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(ProjectAction.class);

	@JaxrsMethodDescribe(value = "查询我的项目统计信息.", action = ActionStatisticMyProjects.class)
	@GET
	@Path("statitic/my")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void statiticMyProject(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request ) {
		ActionResult<ActionStatisticMyProjects.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionStatisticMyProjects().execute( request, effectivePerson );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询我的项目组统计信息.", action = ActionStatisticMyProjectsGroups.class)
	@GET
	@Path("statitic/group")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void statiticMyProjectGroup(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request ) {
		ActionResult<ActionStatisticMyProjectsGroups.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionStatisticMyProjectsGroups().execute( request, effectivePerson );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID查询项目信息.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id ) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID给项目标星.", action = ActionStar.class)
	@GET
	@Path("star/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void star(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id ) {
		ActionResult<ActionStar.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionStar().execute( request, effectivePerson, id );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID取消项目标星.", action = ActionUnStar.class)
	@GET
	@Path("unstar/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unStar(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id ) {
		ActionResult<ActionUnStar.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUnStar().execute( request, effectivePerson, id );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示项目信息,按页码分页.", action = ActionListPageWithFilter.class)
	@POST
	@Path("list/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPageWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("当前页码") @PathParam( "page" ) Integer page,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "size" ) Integer size,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionListPageWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPageWithFilter().execute(effectivePerson, page, size, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页列示项目信息（管理员查询）.", action = ActionManageListPageWithFilter.class)
	@POST
	@Path("list/{page}/size/{size}/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListPageWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								   @JaxrsParameterDescribe("当前页码") @PathParam( "page" ) Integer page,
								   @JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "size" ) Integer size,
								   @JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionManageListPageWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListPageWithFilter().execute(effectivePerson, page, size, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示指定项目组中的项目信息,按页码分页.", action = ActionListPageInGroupWithFilter.class)
	@POST
	@Path("list/{page}/size/{size}/group/{groupId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPageInGroupWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("当前页码") @PathParam( "page" ) Integer page,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "size" ) Integer size,
			@JaxrsParameterDescribe("项目组ID") @PathParam( "groupId" ) String groupId,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionListPageInGroupWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPageInGroupWithFilter().execute(request, effectivePerson, page, size, groupId, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示指定项目组中的项目信息,下一页.", action = ActionListNextInGroupWithFilter.class)
	@POST
	@Path("list/{id}/next/{count}/group/{groupId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextInGroupWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("项目组ID") @PathParam( "groupId" ) String groupId,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionListNextInGroupWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNextInGroupWithFilter().execute(request, effectivePerson, id, count, groupId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示我负责的项目信息,下一页.", action = ActionListMyNextWithFilter.class)
	@POST
	@Path("list/my/{pageNum}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("当前页码") @PathParam( "pageNum" ) Integer pageNum,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionListMyNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListMyNextWithFilter().execute(effectivePerson, pageNum, count, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示未分类项目信息,下一页.", action = ActionListNoGroupNextWithFilter.class)
	@POST
	@Path("list/groupness/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNoGroupNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionListNoGroupNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNoGroupNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示标星项目信息,下一页.", action = ActionListStarNextWithFilter.class)
	@POST
	@Path("list/star/{pageNum}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStarNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("当前页数") @PathParam( "pageNum" ) Integer pageNum,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionListStarNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListStarNextWithFilter().execute(effectivePerson, pageNum, count, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示回收站项目信息,下一页.", action = ActionListRecycleNextWithFilter.class)
	@POST
	@Path("list/recycle/{pageNum}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listRecycleNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("当前页数") @PathParam( "pageNum" ) Integer pageNum,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionListRecycleNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListRecycleNextWithFilter().execute(effectivePerson, pageNum, count, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建或者更新一个项目信息.", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("需要保存的项目信息") JsonElement jsonElement ) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSave().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新一个项目图标信息.", action = ActionUpdateIcon.class)
	@POST
	@Path("{id}/icon")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateIcon(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("需要保存的项目图标信息") JsonElement jsonElement ) {
		ActionResult<ActionUpdateIcon.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateIcon().execute(request, effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据标识更改项目状态.", action = ActionUpdateStatus.class)
	@POST
	@Path("update/{id}/status")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateProjectStatus(@Suspended final AsyncResponse asyncResponse,
								@Context HttpServletRequest request,
								@JaxrsParameterDescribe("项目标识") @PathParam("id") String id,
								@JaxrsParameterDescribe("需要保存的项目状态信息") JsonElement jsonElement ) {
		ActionResult<ActionUpdateStatus.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateStatus().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据标识设置项目是否可新建任务.", action = ActionCreateable.class)
	@POST
	@Path("{id}/createable")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createableProject(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("需要保存的项目状态信息") JsonElement jsonElement ) {
		ActionResult<ActionCreateable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateable().execute(request, effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "按条件导出项目信息.", action = ActionExportWithFilter.class)
	@POST
	@Path("export")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void exportWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								 @JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<ActionExportWithFilter.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExportWithFilter().execute(effectivePerson, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "导出Excel的执行结果.", action = ActionExportResult.class)
	@GET
	@Path("export/result/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void exportResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							 @JaxrsParameterDescribe("对象标识") @PathParam("flag") String flag) {
		ActionResult<ActionExportResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExportResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
