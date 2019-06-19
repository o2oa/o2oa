package com.x.temwork.assemble.control.jaxrs.task;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Task parentTask = null;
		TaskListChange taskListChange = null;
		Wi wi = null;
		Boolean check = true;
		String optType = "CREATE";

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			if ( !taskPersistService.checkPermissionForPersist( effectivePerson ) ) {
				check = false;
				Exception exception = new TaskPersistException("task save permission denied!" );
				result.error(exception);
			}
		}
		
		if (check) {	
			task = taskQueryService.get( wi.getId() );
			if( task == null ) {
				optType = "CREATE";
				if( StringUtils.isNotEmpty( wi.getParent() )) { //有上级工作，说明是拆解工作
					optType = "DISASSEMBLE";
					
					//确认上级任务是否存在
					parentTask = taskQueryService.get( wi.getParent() );
					if( parentTask == null ) {
						check = false;
						Exception exception = new TaskPersistException("parent task not exists!PID=" + wi.getParent() );
						result.error(exception);
					}
				}
			}else {
				optType = "UPDATE";
			}
		}
		
		if (check) {	
			if( task != null && task.getWorkStatus().equalsIgnoreCase( wi.getWorkStatus() )) {
				//说明工作状态有变化，要查询状态绑定的列表信息
				taskListChange = taskQueryService.getTaskListChange( task.getId(), task.getWorkStatus(), wi.getWorkStatus() );
			}
		}
		
		if (check) {
			TaskDetail taskDetail = new TaskDetail();
			taskDetail.setId( wi.getId() );
			taskDetail.setProject( wi.getProject() );
			taskDetail.setDescription( wi.getDescription() );
			taskDetail.setDetail( wi.getDetail() );
			taskDetail.setMemoLob1( wi.getMemoLob1() );
			taskDetail.setMemoLob2( wi.getMemoLob2() );
			taskDetail.setMemoLob3( wi.getMemoLob3() );
			
			try {					
				task = taskPersistService.save( wi, taskDetail, effectivePerson );		
				
				// 更新缓存
				ApplicationCache.notify( Task.class );
				
				Wo wo = new Wo();
				wo.setId( task.getId() );
				if( taskListChange != null ) {
					wo.setTaskChangeList(taskListChange);
				}				
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskPersistException(e, "工作任务信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			//记录工作任务信息变化记录
			try {
				dynamicPersistService.save( task, optType, effectivePerson, jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi extends Task {
		
		private static final long serialVersionUID = -6314932919066148113L;
		
		public static WrapCopier<Wi, Task> copier = WrapCopierFactory.wi( Wi.class, Task.class, null, null );
		
		@FieldDescribe("工作内容")
		private String detail;
		
		@FieldDescribe("说明信息")
		private String description;
		
		@FieldDescribe("备用LOB信息1")
		private String memoLob1;
		
		@FieldDescribe("备用LOB信息2")
		private String memoLob2;
		
		@FieldDescribe("备用LOB信息3")
		private String memoLob3;

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getMemoLob1() {
			return memoLob1;
		}

		public void setMemoLob1(String memoLob1) {
			this.memoLob1 = memoLob1;
		}

		public String getMemoLob2() {
			return memoLob2;
		}

		public void setMemoLob2(String memoLob2) {
			this.memoLob2 = memoLob2;
		}

		public String getMemoLob3() {
			return memoLob3;
		}

		public void setMemoLob3(String memoLob3) {
			this.memoLob3 = memoLob3;
		}		
	}

	public static class Wo extends WoId {
		
		@FieldDescribe("状态改变引起的列表变化信息")
		private TaskListChange taskListChange = null;

		public TaskListChange getTaskChangeList() {
			return taskListChange;
		}

		public void setTaskChangeList(TaskListChange taskListChange) {
			this.taskListChange = taskListChange;
		}
	}
	
}