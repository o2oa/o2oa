package com.x.teamwork.assemble.control.jaxrs.global;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Priority;

public class ActionPrioritySave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPrioritySave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Priority priority = null;
		Wi wi = null;
		Wo wo = new Wo();

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			Exception exception = new PriorityPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		
		try {					
			priority = priorityPersistService.save( wi, effectivePerson );
			// 更新缓存
			CacheManager.notify( Priority.class );
			wo.setId( priority.getId() );
		} catch (Exception e) {
			Exception exception = new PriorityPersistException(e, "优先级信息保存时发生异常。");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}			
		
		result.setData( wo );
		return result;
	}	

	public static class Wi extends Priority {
		private static final long serialVersionUID = -6314932919066148113L;
		
		public static WrapCopier<Wi, Priority> copier = WrapCopierFactory.wi( Wi.class, Priority.class, null, null );
		
	}

	public static class Wo extends WoId {
		
	}
	
}