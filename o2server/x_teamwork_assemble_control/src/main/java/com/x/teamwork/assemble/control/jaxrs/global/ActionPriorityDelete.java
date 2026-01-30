package com.x.teamwork.assemble.control.jaxrs.global;


import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Priority;

public class ActionPriorityDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPriorityDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Priority priority = null;
		Boolean check = true;
		Wo wo = new Wo();

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new PriorityFlagForQueryEmptyException();
			result.error( exception );
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				priority = priorityQueryService.get(flag);
				if ( priority == null) {
					check = false;
					Exception exception = new PriorityNotExistsException(flag);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new PriorityQueryException(e, "根据指定flag查询优先级信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( Boolean.TRUE.equals( check ) ){
			try {
				priorityPersistService.delete(flag, effectivePerson );				
				// 更新缓存
				CacheManager.notify( Priority.class );
				
				wo.setId( priority.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new PriorityQueryException(e, "根据指定flag删除优先级信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wo extends WoId {
		
	}
	
}