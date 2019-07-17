package com.x.teamwork.assemble.control.jaxrs.tasktag;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.TaskTag;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String taskTagId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskTag taskTag = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( taskTagId ) ) {
			check = false;
			Exception exception = new TaskTagIdForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				taskTag = taskTagQueryService.get(taskTagId);
				if ( taskTag == null) {
					check = false;
					Exception exception = new TaskTagNotExistsException(taskTagId);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskTagQueryException(e, "根据指定flag查询工作任务标签信息对象时发生异常。taskTagId:" + taskTagId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskTagPersistService.delete( taskTagId, effectivePerson );				
				// 更新缓存
				ApplicationCache.notify( TaskTag.class );
				
				Wo wo = new Wo();
				wo.setId( taskTag.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskTagQueryException(e, "根据指定flag删除工作任务标签信息对象时发生异常。taskTagId:" + taskTagId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}		
				
		if (check) {
			try {					
				dynamicPersistService.taskTagDeleteDynamic( taskTag, effectivePerson);
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		return result;
	}

	public static class Wo extends WoId {
	}
}