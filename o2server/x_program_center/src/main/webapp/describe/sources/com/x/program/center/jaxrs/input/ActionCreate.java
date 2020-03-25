package com.x.program.center.jaxrs.input;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.element.*;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Invoke;
import com.x.program.center.core.entity.wrap.ServiceModuleEnum;
import com.x.program.center.core.entity.wrap.WrapAgent;
import com.x.program.center.core.entity.wrap.WrapInvoke;
import com.x.program.center.core.entity.wrap.WrapServiceModule;

import java.util.ArrayList;
import java.util.List;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			ServiceModuleEnum serviceModuleEnum = this.create(business, wi);
			wo.setId(serviceModuleEnum.getValue());
			result.setData(wo);
			return result;
		}
	}

	/**
	 * 新建导入
	 * @param business
	 * @param wi
	 * @return
	 * @throws Exception
	 */
	private ServiceModuleEnum create( Business business, Wi wi ) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();
		ServiceModuleEnum serviceModuleEnum = ServiceModuleEnum.getEnumByValue(wi.getId());
		if (null == serviceModuleEnum) {
			throw new ExceptionAppInfoNotExist(wi.getId());
		}
		for (WrapAgent _o : wi.getAgentList() ) {
			Agent obj = business.entityManagerContainer().find( _o.getId(), Agent.class );
			if (null != obj) {
				throw new ExceptionEntityExistForCreate(_o.getId(), Form.class);
			}
			obj = WrapAgent.inCopier.copy(_o);
			persistObjects.add(obj);
		}
		for (WrapInvoke _o : wi.getInvokeList() ) {
			Invoke obj = business.entityManagerContainer().find( _o.getId(), Invoke.class );
			if (null != obj) {
				throw new ExceptionEntityExistForCreate(_o.getId(), Form.class);
			}
			obj = WrapInvoke.inCopier.copy(_o);
			persistObjects.add(obj);
		}
		
		business.entityManagerContainer().beginTransaction(Agent.class);
		business.entityManagerContainer().beginTransaction(Invoke.class);

		for (JpaObject o : persistObjects) {
			business.entityManagerContainer().persist(o);
		}
		business.entityManagerContainer().commit();
		return serviceModuleEnum;
	}

	public static class Wi extends WrapServiceModule {

	}

	public static class Wo extends WoId {

	}
}