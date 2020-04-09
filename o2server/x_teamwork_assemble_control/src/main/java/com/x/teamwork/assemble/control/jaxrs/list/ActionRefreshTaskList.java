package com.x.teamwork.assemble.control.jaxrs.list;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.TaskList;

public class ActionRefreshTaskList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRefreshTaskList.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		TaskList taskList = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
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
				taskList = taskListQueryService.get( id );
				if ( taskList == null) {
					check = false;
					Exception exception = new TaskListNotExistsException( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskListQueryException(e, "根据指定flag查询项目信息对象时发生异常。id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskListPersistService.refreshTaskListRele(wi.getTaskIds(), id, effectivePerson);
				// 更新缓存
				ApplicationCache.notify( TaskList.class );
				
				Wo wo = new Wo();
				wo.setId( taskList.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskListQueryException(e, "向指定的工作任务列表中添加任务信息时发生异常。id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}		
		return result;
	}

	public static class Wi{
		
		@FieldDescribe("需要添加到的列表里的工作任务ID")
		private List<String> taskIds = null;

		public List<String> getTaskIds() {
			return taskIds;
		}

		public void setTaskIds(List<String> taskIds) {
			this.taskIds = taskIds;
		}		
	}

	public static class Wo extends WoId {
	}
}