package com.x.teamwork.assemble.control.jaxrs.global;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Priority;

public class ActionInitConfig extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionInitConfig.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		List<Priority> prioritys = null;
		
		try {
			prioritys = priorityQueryService.listPriority();
			if( ListTools.isEmpty( prioritys )) {
				//初始化默认的优先级配置、默认模板
				this.initPrioritys(wo,effectivePerson);
				
			}
		} catch (Exception e) {
			Exception exception = new PriorityQueryException(e, "查询优先级信息列表时发生异常。");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		result.setData( wo );
		return result;
	}	
	
	private void initPrioritys(Wo wo,EffectivePerson effectivePerson) throws Exception{
		for(int i=0;i<3;i++){
			Priority priority = new Priority();
			if(i==0){
				priority.setPriority("紧急");
				priority.setPriorityColor("#e62412");
			}
			if(i==1){
				priority.setPriority("优先");
				priority.setPriorityColor("#fa8c15");
			}
			if(i==2){
				priority.setPriority("普通");
				priority.setPriorityColor("#15ad31");
			}
			priority.setOrder(i);
			priority.setOwner("系统");
			try {
				priority = priorityPersistService.save( priority, effectivePerson );
				CacheManager.notify( Priority.class );
				wo.setId( priority.getId() );
			} catch (Exception e) {
				Exception exception = new PriorityPersistException(e, "优先级信息保存时发生异常。");
			}
		}
	}

	public static class Wo extends WoId {
		
	}
	
}