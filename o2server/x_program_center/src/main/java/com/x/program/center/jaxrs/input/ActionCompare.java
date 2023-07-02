package com.x.program.center.jaxrs.input;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.CompareServiceModule;
import com.x.program.center.core.entity.wrap.ServiceModuleEnum;
import com.x.program.center.core.entity.wrap.WrapServiceModule;

class ActionCompare extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCompare.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "receive:{}.", jsonElement);
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ServiceModuleEnum serviceModuleEnum = ServiceModuleEnum.getEnumByValue(wi.getId());
			Wo wo = new Wo();
			wo.setId(wi.getId());
			wo.setName(wi.getName());
			wo.setAlias("");
			wo.setExist(false);
			if (null != serviceModuleEnum) {
				wo.setExist(true);
				wo.setExistName(serviceModuleEnum.getDescription());
				wo.setExistAlias("");
				wo.setExistId(serviceModuleEnum.getValue());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends WrapServiceModule {


	}

	public static class Wo extends CompareServiceModule {

	}

}