package com.x.teamwork.assemble.control.jaxrs.taskgroup;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.TaskGroup;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskGroup taskGroup = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new TaskGroupFlagForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				taskGroup = taskGroupQueryService.get(flag);
				if ( taskGroup == null) {
					check = false;
					Exception exception = new TaskGroupNotExistsException(flag);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskGroupQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskGroupPersistService.delete(flag, effectivePerson );				
				// 更新缓存
				ApplicationCache.notify( Project.class );
				ApplicationCache.notify( TaskGroup.class );
				
				Wo wo = new Wo();
				wo.setId( taskGroup.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskGroupQueryException(e, "根据指定flag删除工作任务信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {
	}
}