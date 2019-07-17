package com.x.teamwork.assemble.control.jaxrs.list;

import javax.servlet.http.HttpServletRequest;

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
import com.x.teamwork.core.entity.TaskList;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskList taskList = null;
		TaskList taskList_old = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskListPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {	
			taskList_old = taskListQueryService.get( wi.getId() );
		}
		
		if (check) {			
			try {
				
				taskList = taskListPersistService.save( Wi.copier.copy(wi), effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( TaskList.class );
				Wo wo = new Wo();
				wo.setId( taskList.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskListPersistException(e, "工作任务列表信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {					
				dynamicPersistService.taskListSaveDynamic(taskList_old, taskList, effectivePerson,  jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi{
		
		@FieldDescribe("列表ID，新建时为空.")
		private String id;
		
		@FieldDescribe("所属任务分组(必填).")
		private String taskGroup;
		
		@FieldDescribe("工作任务列表名称(必填)")
		private String name;
		
		@FieldDescribe("排序号")
		private Integer order = 0;
		
		@FieldDescribe("列表描述")
		private String memo;			
		
		public static WrapCopier<Wi, TaskList> copier = WrapCopierFactory.wi( Wi.class, TaskList.class, null, null );		
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTaskGroup() {
			return taskGroup;
		}

		public void setTaskGroup(String taskGroup) {
			this.taskGroup = taskGroup;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getOrder() {
			return order;
		}

		public void setOrder(Integer order) {
			this.order = order;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}		
	}

	public static class Wo extends WoId {
	}
	
}