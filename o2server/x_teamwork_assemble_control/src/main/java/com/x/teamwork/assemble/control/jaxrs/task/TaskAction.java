package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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

@Path("task")
@JaxrsDescribe("工作任务信息管理")
public class TaskAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(TaskAction.class);

	@JaxrsMethodDescribe(value = "根据ID查询工作任务信息.", action = ActionGet.class)
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

	@JaxrsMethodDescribe(value = "根据ID归档指定工作任务.", action = ActionArchive.class)
	@GET
	@Path("archive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void archive(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id ) {
		ActionResult<ActionArchive.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionArchive().execute( request, effectivePerson, id );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询我的项目首页中工作任务组和视图信息.", action = ActionStatisticMyTasks.class)
	@GET
	@Path("statitic/{projectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void statiticMyProject(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("项目ID") @PathParam("projectId") String projectId ) {
		ActionResult<ActionStatisticMyTasks.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionStatisticMyTasks().execute( request, effectivePerson, projectId );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询我的项目首页中工作任务视图信息.", action = ActionStatisticMyTaskViews.class)
	@GET
	@Path("statitic/group/{projectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void statiticMyProjectGroup(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("项目ID") @PathParam("projectId") String projectId ) {
		ActionResult<ActionStatisticMyTaskViews.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionStatisticMyTaskViews().execute( request, effectivePerson, projectId );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示四象限条件的任务.", action = ActionListFourQuadrantTask.class)
	@GET
	@Path("list/project/{projectId}/important/{important}/urgency/{urgency}/count/{count}/{justExecutor}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listFourQuadrantTask(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									 @JaxrsParameterDescribe("项目ID") @PathParam("projectId") String projectId,
									 @JaxrsParameterDescribe("重要程度") @PathParam("important") String important,
									 @JaxrsParameterDescribe("紧急程度") @PathParam("urgency") String urgency,
									 @JaxrsParameterDescribe("返回数量") @PathParam("count") Integer count,
									 @JaxrsParameterDescribe("是否仅自己负责的任务") @PathParam("justExecutor") Boolean justExecutor) {
		ActionResult<List<ActionListFourQuadrantTask.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListFourQuadrantTask().execute(effectivePerson, projectId, important, urgency, count, justExecutor );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示我负责的工作任务信息.", action = ActionListMyTaskPaging.class)
	@POST
	@Path("list/my/page/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyTaskPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								 @JaxrsParameterDescribe("当前页") @PathParam( "page" ) Integer page,
								 @JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "size" ) Integer size,
								 @JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement) {
		ActionResult<List<ActionListMyTaskPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListMyTaskPaging().execute(effectivePerson, page, size, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示指定项目的指定工作列表内的工作任务信息(只要可见的都显示出来).", action = ActionListWithTaskList.class)
	@GET
	@Path("list/project/{projectId}/tasklist/{taskListId}/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllTaskWithTaskListId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("项目ID") @PathParam( "projectId" ) String projectId,
			@JaxrsParameterDescribe("工作任务列表ID") @PathParam( "taskListId" ) String taskListId  ) {
		ActionResult<List<ActionListWithTaskList.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithTaskList().execute(request, effectivePerson, projectId, taskListId );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示用户可见的指定任务的下级任务信息列表.", action = ActionListSubTaskWithTaskId.class)
	@GET
	@Path("list/sub/{taskId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubTaskWithTaskId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作任务ID") @PathParam( "taskId" ) String taskId  ) {
		ActionResult<List<ActionListSubTaskWithTaskId.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListSubTaskWithTaskId().execute(request, effectivePerson, taskId );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示工作任务信息,按页码分页.", action = ActionListPageWithFilter.class)
	@POST
	@Path("list/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPageWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("当前页") @PathParam( "page" ) Integer page,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "size" ) Integer size,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionListPageWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPageWithFilter().execute(request, effectivePerson, page, size, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建或者更新一个工作任务信息.", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("需要保存的工作任务信息") JsonElement jsonElement ) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSave().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新一个工作任务指定的单个属性信息内容.", action = ActionUpdateSingleProperty.class)
	@Path("{id}/property")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateSingleProperty(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作任务信息ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("需要保存的工作任务信息") JsonElement jsonElement ) {
		ActionResult<ActionUpdateSingleProperty.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateSingleProperty().execute( request, effectivePerson, id, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "为工作任务信息添加新的参与者.", action = ActionParticipantUpdate.class)
	@POST
	@Path("participant/{id}/update")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateParticipant(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作任务信息ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("需要添加的参与者标识列表") JsonElement jsonElement ) {
		ActionResult<ActionParticipantUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionParticipantUpdate().execute(request, effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示(视图)当前项目所有工作任务信息,下一页.", action = ActionViewAllListNextWithFilter.class)
	@POST
	@Path("listall/{id}/next/{count}/projectId/{projectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllTaskNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("当前项目ID") @PathParam( "projectId" ) String projectId,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionViewAllListNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionViewAllListNextWithFilter().execute(request, effectivePerson, id, count, projectId, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示(视图)当前项目我负责的工作任务信息,下一页.", action = ActionViewMyExecutListNextWithFilter.class)
	@POST
	@Path("listmyexecut/{id}/next/{count}/projectId/{projectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyExecutTaskNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("当前项目ID") @PathParam( "projectId" ) String projectId,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionViewMyExecutListNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionViewMyExecutListNextWithFilter().execute(request, effectivePerson, id, count, projectId, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示(视图)当前项目未完成的工作任务信息,下一页.", action = ActionViewUncompletedListNextWithFilter.class)
	@POST
	@Path("listuncompleted/{id}/next/{count}/projectId/{projectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listUncompletedTaskNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("当前项目ID") @PathParam( "projectId" ) String projectId,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionViewUncompletedListNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionViewUncompletedListNextWithFilter().execute(request, effectivePerson, id, count, projectId, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示(视图)当前项目已完成的工作任务信息,下一页.", action = ActionViewCompletedListNextWithFilter.class)
	@POST
	@Path("listcompleted/{id}/next/{count}/projectId/{projectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCompletedTaskNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("当前项目ID") @PathParam( "projectId" ) String projectId,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionViewCompletedListNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionViewCompletedListNextWithFilter().execute(request, effectivePerson, id, count, projectId, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示(视图)当前项目逾期的工作任务信息,下一页.", action = ActionViewOverTimeListNextWithFilter.class)
	@POST
	@Path("listovertime/{id}/next/{count}/projectId/{projectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listOverTimeTaskNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam( "id" ) String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam( "count" ) Integer count,
			@JaxrsParameterDescribe("当前项目ID") @PathParam( "projectId" ) String projectId,
			@JaxrsParameterDescribe("查询过滤条件") JsonElement jsonElement ) {
		ActionResult<List<ActionViewOverTimeListNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionViewOverTimeListNextWithFilter().execute(request, effectivePerson, id, count, projectId, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "按条件导出任务信息.", action = ActionExportWithFilter.class)
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

	@JaxrsMethodDescribe(value = "根据ID查询工作任务是否有查看权限.", action = ActionCanRead.class)
	@GET
	@Path("{id}/can/read")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void canRead(@Suspended final AsyncResponse asyncResponse,
					@Context HttpServletRequest request,
					@JaxrsParameterDescribe("标识") @PathParam("id") String id ) {
		ActionResult<ActionCanRead.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCanRead().execute( request, effectivePerson, id );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新任务的序列号.", action = ActionUpdateTaskNumber.class)
	@POST
	@Path("update/task/number/{projectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateTaskNumber(@Suspended final AsyncResponse asyncResponse,
								  @Context HttpServletRequest request,
								  @JaxrsParameterDescribe("项目ID") @PathParam("projectId") String projectId ) {
		ActionResult<ActionUpdateTaskNumber.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateTaskNumber().execute(effectivePerson, projectId );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
