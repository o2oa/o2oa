package com.x.teamwork.assemble.control.jaxrs.list;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
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
		Wi wi = null;
		Boolean check = true;
		String optType = "CREATE";

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskListPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {	
			taskList = taskListQueryService.get( wi.getId() );
			if( taskList == null ) {
				optType = "CREATE";
			}else {
				optType = "UPDATE";
			}
		}
		
		if (check) {			
			try {					
				taskList = taskListPersistService.save( wi, effectivePerson );
				
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
				dynamicPersistService.save( taskList, optType, effectivePerson, jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi extends TaskList {
		
		private static final long serialVersionUID = -6314932919066148113L;	
		
		public static WrapCopier<Wi, TaskList> copier = WrapCopierFactory.wi( Wi.class, TaskList.class, null, null );		
	}

	public static class Wo extends WoId {
	}
	
}