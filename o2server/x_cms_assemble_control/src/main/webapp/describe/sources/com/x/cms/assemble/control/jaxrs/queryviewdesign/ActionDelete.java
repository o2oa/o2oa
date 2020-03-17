package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.core.entity.element.QueryView;


class ActionDelete extends BaseAction {
	ActionResult<Wo> execute( EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			//Business business = new Business(emc);
			emc.beginTransaction(QueryView.class);
			QueryView queryView = emc.find(id, QueryView.class, ExceptionWhen.not_found);
			//AppInfo appInfo = emc.find(queryView.getAppId(), AppInfo.class, ExceptionWhen.not_found);
			//business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.remove(queryView, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(QueryView.class);
			
			Wo wo = new Wo();
			wo.setId( queryView.getId() );
			result.setData( wo );
			
			return result;
		}
	}
	
	public static class Wo extends WoId {

	}
}
