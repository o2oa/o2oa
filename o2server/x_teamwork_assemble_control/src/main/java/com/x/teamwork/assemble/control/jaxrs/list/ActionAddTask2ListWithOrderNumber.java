package com.x.teamwork.assemble.control.jaxrs.list;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskList;

public class ActionAddTask2ListWithOrderNumber extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAddTask2ListWithOrderNumber.class);
	
	/**
	 * 一个工作任务在同一个工作任务组里只属于一个任务列表，不可重复
	 * @param request
	 * @param effectivePerson
	 * @param taskListId
	 * @param jsonElement
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String taskListId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		TaskList taskList = null;
		String message = "任务已经成功添加到列表指定位置！";
		Boolean check = true;

		if ( StringUtils.isEmpty( taskListId ) ) {
			check = false;
			Exception exception = new TaskListFlagForQueryEmptyException();
			result.error( exception );
		}
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskListPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				taskList = taskListQueryService.get( taskListId );
				if ( taskList == null) {
					check = false;
					//Exception exception = new TaskListNotExistsException( taskListId );
					//result.error( exception );
					message = "任务列表不存在或列表为[未归类任务]不允许排序操作！";
				}else {
					message = "任务已经成功添加到列表["+ taskList.getName() +"]指定位置！";
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskListQueryException(e, "根据指定ID查询项目信息对象时发生异常。taskListId:" + taskListId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {				
				taskListPersistService.addTaskToTaskListWithOrderNumber( wi.getTaskId(), taskListId, wi.getOrderNumber(), effectivePerson );
				// 更新缓存
				ApplicationCache.notify( TaskList.class );
				ApplicationCache.notify( Task.class );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskListQueryException(e, "向指定的工作任务列表中添加任务信息时发生异常。taskListId:" + taskListId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		Wo wo = new Wo();
		if( taskList != null ) {
			wo.setId( taskList.getId() );
		}
		wo.setMessage(message);
		result.setData( wo );
		return result;
	}

	public static class Wi{
		
		@FieldDescribe("需要添加到的列表里的工作任务ID")
		private String taskId = null;
		
		@FieldDescribe("工作任务在列表里的排序位置，如果是最后一个，可以为空")
		private Integer orderNumber = null;

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public Integer getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(Integer orderNumber) {
			this.orderNumber = orderNumber;
		}
	}

	public static class Wo {
		@FieldDescribe("目标工作任务列表ID")
		private String id = null;
		
		@FieldDescribe("操作结果相关的消息")
		private String message = null;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}