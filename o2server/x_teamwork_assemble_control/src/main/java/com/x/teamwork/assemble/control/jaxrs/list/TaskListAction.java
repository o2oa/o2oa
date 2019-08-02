package com.x.teamwork.assemble.control.jaxrs.list;

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
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("task_list")
@JaxrsDescribe("任务列表管理")
public class TaskListAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(TaskListAction.class);

	@JaxrsMethodDescribe(value = "根据ID查询工作任务列表信息.", action = ActionGet.class)
	@GET
	@Path("taskgroup/{taskGroupId}/tasklist/{taskListId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作任务分组ID") @PathParam("taskGroupId") String taskGroupId,
			@JaxrsParameterDescribe("工作任务列表ID") @PathParam("taskListId") String taskListId ) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute( request, effectivePerson, taskGroupId, taskListId );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据工作任务组查询工作列表信息列表.", action = ActionListWithTaskGroup.class)
	@GET
	@Path("list/taskgroup/{taskgroup}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithTaskGroup(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作任务组ID") @PathParam("taskgroup") String taskgroup) {
		ActionResult<List<ActionListWithTaskGroup.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithTaskGroup().execute( request, effectivePerson, taskgroup );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据工作任务组查询工作列表信息列表.", action = ActionListWithTaskGroupWithTask.class)
	@GET
	@Path("list/taskgroup/{taskgroup}/{withTask}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithTaskGroupWithTask(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作任务组ID") @PathParam("taskgroup") String taskgroup,
			@JaxrsParameterDescribe("是否包含工作列表") @PathParam("withTask") Boolean withTask) {
		ActionResult<List<ActionListWithTaskGroupWithTask.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithTaskGroupWithTask().execute( request, effectivePerson, taskgroup, withTask );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "创建或者更新一个工作任务列表信息.", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("需要保存的工作任务列表信息") JsonElement jsonElement ) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSave().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "重新指定列表中所有的任务（删除原来的关联，添加新的关联）.", action = ActionRefreshTaskList.class)
	@PUT
	@Path("tasklist/refresh")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void refreshTaskList(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作任务列表ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("需要关联的工作任务ID") JsonElement jsonElement ) {
		ActionResult<ActionRefreshTaskList.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionRefreshTaskList().execute(request, effectivePerson, id,  jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "将一个工作任务添加到指定的列表中.", action = ActionAddTask2ListWithOrderNumber.class)
	@PUT
	@Path("add2list/{listId}/order")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addTask2ListWithOrderNumber(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作任务列表ID") @PathParam("listId") String listId,
			@JaxrsParameterDescribe("需要关联的工作任务ID及排序号信息") JsonElement jsonElement) {
		ActionResult<ActionAddTask2ListWithOrderNumber.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAddTask2ListWithOrderNumber().execute(request, effectivePerson, listId, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "将一个工作任务添加到指定的列表中.", action = ActionAddTask2ListWithBehindTask.class)
	@PUT
	@Path("add2list/{listId}/behindTask")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addTask2ListWithBehindTask(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作任务列表ID") @PathParam("listId") String listId,
			@JaxrsParameterDescribe("需要关联的工作任务ID及后序工作任务ID") JsonElement jsonElement) {
		ActionResult<ActionAddTask2ListWithBehindTask.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAddTask2ListWithBehindTask().execute(request, effectivePerson, listId, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "将一个工作任务从指定的列表中移除.", action = ActionRemoveTaskFromList.class)
	@DELETE
	@Path("remove/{listId}/task/{taskId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeTaskFromList(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作任务列表ID") @PathParam("listId") String listId,
			@JaxrsParameterDescribe("工作任务ID") @PathParam("taskId") String taskId ) {
		ActionResult<ActionRemoveTaskFromList.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionRemoveTaskFromList().execute(request, effectivePerson, listId, taskId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "删除工作任务列表信息.", action = ActionDelete.class)
	@DELETE
	@Path("delete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("标识") @PathParam("id") String id ) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}