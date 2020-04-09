package com.x.teamwork.assemble.control.jaxrs.list;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.TaskList;

public class ActionRemoveTaskFromList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRemoveTaskFromList.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id, String taskId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskList taskList = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new TaskListFlagForQueryEmptyException();
			result.error( exception );
		}
		
		if ( StringUtils.isEmpty( taskId ) ) {
			check = false;
			Exception exception = new TaskIdForRemoveEmptyException();;
			result.error( exception );
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
				Exception exception = new TaskListQueryException(e, "根据指定flag查询项目信息对象时发生异常。id:" + id );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskListPersistService.removeTaskFromList(taskId, id, effectivePerson);
				// 更新缓存
				ApplicationCache.notify( TaskList.class );
				
				Wo wo = new Wo();
				wo.setId( taskList.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskListQueryException(e, "从指定的工作任务列表中删除任务信息时发生异常。id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}		
		return result;
	}

	public static class Wo extends WoId {
	}
}