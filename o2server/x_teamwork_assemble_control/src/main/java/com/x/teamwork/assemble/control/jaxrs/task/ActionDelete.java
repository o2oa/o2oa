package com.x.teamwork.assemble.control.jaxrs.task;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				task = taskQueryService.get(flag);
				if ( task == null) {
					check = false;
					Exception exception = new TaskNotExistsException(flag);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskPersistService.delete(flag, effectivePerson );
				//taskGroupPersistService.refreshTaskCountInTaskGroupWithTaskId( effectivePerson.getDistinguishedName(), flag );
				
				// 更新缓存
				ApplicationCache.notify( Task.class );
				ApplicationCache.notify( TaskGroup.class );	
				ApplicationCache.notify( TaskList.class );
				
				Wo wo = new Wo();
				wo.setId( task.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定flag删除工作任务信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {					
				new BatchOperationPersistService().addOperation( 
						BatchOperationProcessService.OPT_OBJ_TASK, 
						BatchOperationProcessService.OPT_TYPE_DELETE,  flag,  flag, "删除文档：ID=" +   flag );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		
		if (check) {
			try {					
				dynamicPersistService.taskDeleteDynamic( task, effectivePerson );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		return result;
	}

	public static class Wo extends WoId {
	}
}